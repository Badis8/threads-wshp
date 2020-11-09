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
	
	synchronized private void put(String path, String key) {
		this.keys.put(path, key);
	}
	
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
	
					this.put(file.getAbsolutePath(), randomKey);
					
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
