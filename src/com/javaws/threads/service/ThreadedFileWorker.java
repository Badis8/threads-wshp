package com.javaws.threads.service;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;

public class ThreadedFileWorker implements Worker {

	private final Encrypter encrypter;

	private final Decrypter decrypter;

	public ThreadedFileWorker(Encrypter encrypter, Decrypter decrypter) {
		super();
		this.encrypter = encrypter;
		this.decrypter = decrypter;
	}

	@Override
	public void encrypt(List<File> files) throws FileNotFoundException, IOException {
		for (File file : files) {
			Thread thread = new Thread(new EncryptRunnable(encrypter, file));
			thread.start();
		}
	}

	@Override
	public void decrypt(List<File> files) throws FileNotFoundException, IOException {
		for (File file : files) {
			Thread thread = new Thread(new DecryptRunnable(decrypter, file));
			thread.start();
		}
	}
}
