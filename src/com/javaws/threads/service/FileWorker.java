package com.javaws.threads.service;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class FileWorker implements Worker {

	private final RandomKeyEncrypter encrypter;

	private final Decrypter decrypter;

	private final Map<String, String> keys;
	
	public FileWorker(RandomKeyEncrypter encrypter, Decrypter decrypter) {
		super();
		this.encrypter = encrypter;
		this.decrypter = decrypter;
		this.keys = new HashMap<>();
	}

	@Override
	public void encrypt(List<File> files) throws FileNotFoundException, IOException {
		Set<Thread> threads = new HashSet<>();
		
		for (File file : files) {
			Thread thread = new Thread(() -> {
				try {
					File outputFile = Path.of(file.getAbsolutePath() + "._locked").toFile();
					outputFile.createNewFile();
					String randomKey = this.encrypter.encrypt(new FileInputStream(file), new FileOutputStream(outputFile));
	
					// Caution here !
					// HashMap is not thread safe
					// meaning that this can break
					// Try to encrypt 3000 files and see the number of keys in the hashmap
					// generally it will be lower than 3000
					this.keys.put(file.getAbsolutePath(), randomKey);
					
					System.out.println("Encryption : "+outputFile.getAbsolutePath());
					
				} catch (IOException  e) {
					e.printStackTrace();
				} 
			});
			thread.start();
			
			threads.add(thread);
		}
		
		for(Thread t : threads) {
			try {
				t.join();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	@Override
	public void decrypt(List<File> files) throws FileNotFoundException, IOException {

		Set<Thread> threads = new HashSet<>();
		
		for (File file : files) {
			Thread thread = new Thread(() -> {
				try {

					File outputFile = Path.of(file.getAbsolutePath() + "._unlocked").toFile();
					outputFile.createNewFile();
					this.decrypter.decrypt(new FileInputStream(file), new FileOutputStream(outputFile));
					
					System.out.println("Decryption : "+outputFile.getAbsolutePath());
					
				} catch (IOException e) {
					e.printStackTrace();
				}
			});
			thread.start();
			
			threads.add(thread);
		}
		
		for(Thread t : threads) {
			try {
				t.join();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	@Override
	public Map<String, String> getKeys() {
		return this.keys;
	}
}
