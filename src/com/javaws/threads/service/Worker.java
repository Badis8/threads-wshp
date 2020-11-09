package com.javaws.threads.service;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;

public interface Worker {

	void encrypt(List<File> files) throws FileNotFoundException, IOException;

	void decrypt(List<File> files) throws FileNotFoundException, IOException;

}