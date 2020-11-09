package com.javaws.threads.service;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

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
		for (File file: files) {
			File outputFile = Path.of(file.getAbsolutePath() + "._locked").toFile();
			outputFile.createNewFile();
			
			this.encrypter.encrypt(new FileInputStream(file), new FileOutputStream(outputFile));
			
			System.out.println("Encryption : "+outputFile.getAbsolutePath());
		}
	}
	
	@Override
	public void decrypt(List<File> files) throws FileNotFoundException, IOException {
		for (File file: files) {
			File outputFile = Path.of(file.getAbsolutePath() + "._unlocked").toFile();
			outputFile.createNewFile();
			
			this.decrypter.decrypt(new FileInputStream(file), new FileOutputStream(outputFile));
			

			System.out.println("Decryption : "+outputFile.getAbsolutePath());
		}
	}
}
