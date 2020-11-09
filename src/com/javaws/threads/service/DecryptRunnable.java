package com.javaws.threads.service;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Path;

public class DecryptRunnable implements Runnable {

	private final Decrypter decrypter;

	private final File inputFile;

	public DecryptRunnable(Decrypter decrypter, File inputFile) {
		super();
		this.decrypter = decrypter;
		this.inputFile = inputFile;
	}

	@Override
	public void run() {
		try {

			File outputFile = Path.of(inputFile.getAbsolutePath() + "._unlocked").toFile();
			outputFile.createNewFile();
			this.decrypter.decrypt(new FileInputStream(inputFile), new FileOutputStream(outputFile));
			
			System.out.println("Decryption : "+outputFile.getAbsolutePath());
			
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
