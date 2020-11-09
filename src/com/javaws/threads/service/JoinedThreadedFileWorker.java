package com.javaws.threads.service;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class JoinedThreadedFileWorker implements Worker {

	private final Encrypter encrypter;

	private final Decrypter decrypter;

	public JoinedThreadedFileWorker(Encrypter encrypter, Decrypter decrypter) {
		super();
		this.encrypter = encrypter;
		this.decrypter = decrypter;
	}

	@Override
	public void encrypt(List<File> files) throws FileNotFoundException, IOException {
		Set<Thread> threads = new HashSet<>();
		
		for (File file : files) {
			Thread thread = new Thread(new EncryptRunnable(encrypter, file));
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
			Thread thread = new Thread(new DecryptRunnable(decrypter, file));
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
