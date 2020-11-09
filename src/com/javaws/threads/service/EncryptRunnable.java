package com.javaws.threads.service;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Path;

public class EncryptRunnable implements Runnable {

	private final Encrypter encrypter;

	private final File inputFile;

	public EncryptRunnable(Encrypter encrypter, File inputFile) {
		super();
		this.encrypter = encrypter;
		this.inputFile = inputFile;
	}

	@Override
	public void run() {
		try {
			File outputFile = Path.of(inputFile.getAbsolutePath() + "._locked").toFile();
			outputFile.createNewFile();
			this.encrypter.encrypt(new FileInputStream(inputFile), new FileOutputStream(outputFile));
			
			System.out.println("Encryption : "+outputFile.getAbsolutePath());
		} catch (IOException e) {
			e.printStackTrace();
		} 
	}

}
