package com.javaws.threads.service;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class FileWorker implements Worker {

	private final Encrypter encrypter;

	private final Decrypter decrypter;

	public FileWorker(Encrypter encrypter, Decrypter decrypter) {
		super();
		this.encrypter = encrypter;
		this.decrypter = decrypter;
	}

	@Override
	public void encrypt(List<File> files) throws FileNotFoundException, IOException {
		Set<Thread> threads = new HashSet<>();
		
		for (File file : files) {
			Thread thread = new Thread(() -> {
				try {
					File outputFile = Path.of(file.getAbsolutePath() + "._locked").toFile();
					outputFile.createNewFile();
					this.encrypter.encrypt(new FileInputStream(file), new FileOutputStream(outputFile));
					
					System.out.println("Encryption : "+outputFile.getAbsolutePath());
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
}
