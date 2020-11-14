package com.javaws.threads.service;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Path;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class FileWorker implements Worker {

	private final RandomKeyEncrypter encrypter;

	private final Decrypter decrypter;

	private final String keysPath;
	
	private String currentKeyLine;
	
	private boolean keyWriteActive = true;
	
	synchronized private void put(String path, String key) {
		while(currentKeyLine != null) {
			try { 
                this.wait();
            } catch (InterruptedException e)  {
            	// https://dzone.com/articles/why-do-we-need-threadcurrentthreadinterrupt-in-int
                Thread.currentThread().interrupt(); 
                e.printStackTrace();
            }
		}
		this.currentKeyLine = path+":"+key;
		this.notifyAll();
	}
	
	synchronized private void writeKey(PrintWriter printWriter) {
		while(currentKeyLine == null) {
			try { 
				this.wait();
				
            } catch (InterruptedException e)  {
            	// https://dzone.com/articles/why-do-we-need-threadcurrentthreadinterrupt-in-int
                Thread.currentThread().interrupt(); 
                e.printStackTrace();
            }
		}
		printWriter.println(this.currentKeyLine);
		this.currentKeyLine = null;
		this.notifyAll();
	}
	
	public FileWorker(RandomKeyEncrypter encrypter, Decrypter decrypter, String keysPath) {
		super();
		this.encrypter = encrypter;
		this.decrypter = decrypter;
		this.keysPath = keysPath;
	}

	@Override
	public void encrypt(List<File> files) throws FileNotFoundException, IOException {
		Set<Thread> threads = new HashSet<>();
		

		File keyFile = Path.of(this.keysPath).toFile();
		keyFile.createNewFile();
		PrintWriter pw = new PrintWriter(keyFile);
		
		Thread keyThread = new Thread(() -> {
			while(keyWriteActive) {
				this.writeKey(pw);
			}
		});
		
		keyThread.start();
		
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
		synchronized (this) {
			while(currentKeyLine != null) {
				try { 
	                this.wait();
	            } catch (InterruptedException e)  {
	            	// https://dzone.com/articles/why-do-we-need-threadcurrentthreadinterrupt-in-int
	                Thread.currentThread().interrupt(); 
	                e.printStackTrace();
	            }
			}
			// Release last write
			this.keyWriteActive = false;
			this.currentKeyLine = "";
			this.notifyAll();
		}

		try {
			keyThread.join();
		} catch(InterruptedException e) {
			e.printStackTrace();
		} finally {
			pw.close();
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
