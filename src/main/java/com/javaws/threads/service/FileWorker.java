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
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import com.javaws.threads.utilities.IDThreadFactory;

public class FileWorker implements Worker {

	private final RandomKeyEncrypter encrypter;

	private final Decrypter decrypter;
	
	private final RandomKeySender keySender;
	
	private final Integer nbThreads;
	
	public FileWorker(RandomKeyEncrypter encrypter, Decrypter decrypter, RandomKeySender keySender, Integer nbThreads) {
		super();
		this.encrypter = encrypter;
		this.decrypter = decrypter;
		this.keySender = keySender;
		this.nbThreads = nbThreads;
	}

	@Override
	public void encrypt(List<File> files) throws FileNotFoundException, IOException {
		ThreadPoolExecutor pool =  (ThreadPoolExecutor) Executors.newFixedThreadPool(nbThreads, new IDThreadFactory());
		CountDownLatch latch = new CountDownLatch(files.size());
		
		for (File file : files) {
			pool.submit(() -> {
				try {
					File outputFile = Path.of(file.getAbsolutePath() + "._locked").toFile();
					outputFile.createNewFile();
					String randomKey = this.encrypter.encrypt(new FileInputStream(file), new FileOutputStream(outputFile));
					
					this.keySender.send(outputFile.getAbsolutePath(), randomKey);
					
					System.out.println("Encryption : "+outputFile.getAbsolutePath()+ " ["+Thread.currentThread().getName()+"]");
					
				} catch (IOException  e) {
					e.printStackTrace();
				} finally {
					latch.countDown();
				}
			});
		}

		try {
			latch.await();
		} catch (InterruptedException e) {
	        Thread.currentThread().interrupt();
			e.printStackTrace();
		} finally {
			pool.shutdown();
		    try {
		        if (!pool.awaitTermination(60, TimeUnit.SECONDS)) {
		        	pool.shutdownNow();
		        }
		    } catch (InterruptedException ex) {
		    	pool.shutdownNow();
		        Thread.currentThread().interrupt();
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
