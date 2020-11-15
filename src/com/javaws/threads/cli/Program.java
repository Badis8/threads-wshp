package com.javaws.threads.cli;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.javaws.threads.repository.JDBCKeysRepsitory;
import com.javaws.threads.repository.KeysRepository;
import com.javaws.threads.service.FileWorker;
import com.javaws.threads.service.NaiveByteCypher;
import com.javaws.threads.service.RandomKeyEncrypter;
import com.javaws.threads.service.RandomKeyNaiveByteEncrypter;
import com.javaws.threads.service.RandomKeySender;
import com.javaws.threads.service.RandomKeyWorker;
import com.javaws.threads.service.Worker;

import picocli.CommandLine;
import picocli.CommandLine.Option;

public class Program implements Callable<Integer> {


    @Option(names = { "-e", "--encrypt" }, description = "one or more files/folders to encrypt", arity = "0..*" )
    private Set<File> encrypt;

    @Option(names = { "-d", "--decrypt" }, description = "one or more files/folders to decrypt", arity = "0..*")
    private Set<File> decrypt;
    
    @Option(names = { "-k", "--key" }, description = "encryption/decryption key")
    private String key;
    
    @Option(names = { "-kf", "--keys-file" }, description = "encryption keys output file")
    private String keysFile;
    
    
    @Option(names = { "-nt", "--nb-threads" }, description = "number of threads used for encryption", defaultValue = "3")
    private Integer nbThreads;
    
	@Override
	public Integer call() {
		if((encrypt == null || encrypt.isEmpty()) && (decrypt == null || decrypt.isEmpty())) {
			System.err.println("Need at least one file to encrypt/decrypt");
			return 1;
		}
		KeysRepository keyRepository = new JDBCKeysRepsitory();
		RandomKeyWorker keyWorker = new RandomKeyWorker(keyRepository);
		
		Worker worker = INIT_WORKER(key, keyWorker, nbThreads);
		try {
			if (encrypt != null && !encrypt.isEmpty()) {
				List<File> files = GET_FILES(encrypt);
				
				Thread keyWorkerThread = new Thread(keyWorker);
				keyWorkerThread.start();
				
				RUN(() -> worker.encrypt(files), "Start encrypting ...");
				
				keyWorker.close();
				keyWorkerThread.join();
			}
			
			if (decrypt != null && !decrypt.isEmpty()) {
				List<File> files = GET_FILES(decrypt);
				RUN(() -> worker.decrypt(files), "Start decrypting ...");
			}
			return 0;
		} catch (IOException | InterruptedException e) {
			e.printStackTrace();
			return 1;
		}
	}
	
	public static void main(String[] args) {
		
        int exitCode = new CommandLine(new Program()).execute(args);
        System.exit(exitCode);
	}

	private static List<File> GET_FILES(Set<File> files) {
		return files.stream().flatMap(f -> {
			if (!f.exists()) {
				System.err.println("File " + f.toString() + " do not exists.");
				System.exit(1);
			}

			if (f.isFile()) 
				return Stream.of(f);

			if (f.isDirectory())
				try {
					return Files.walk(f.toPath()).filter(Files::isRegularFile).map(Path::toFile);
				} catch (IOException e) {
					e.printStackTrace();
					System.exit(1);
				}

			return Stream.empty();
		}).collect(Collectors.toList());
	}
	

	public static Worker INIT_WORKER(String key, RandomKeySender sender, Integer nbThreads) {
		NaiveByteCypher naiveCypher = new NaiveByteCypher(key);
		RandomKeyEncrypter encrypter = new RandomKeyNaiveByteEncrypter();
		return new FileWorker(encrypter, naiveCypher, sender, nbThreads);
	}

	public static void RUN(LambdaRun r, String startingMsg) throws IOException {
		System.out.println(startingMsg);

		long startTime = System.currentTimeMillis();

		r.run();

		System.out.println("That took " + (System.currentTimeMillis() - startTime) + " milliseconds ... Kiss goodbye !");
	}

	static interface LambdaRun {
		void run() throws IOException;
	}

}
