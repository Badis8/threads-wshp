package com.javaws.threads.service;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public interface Encrypter {

	void encrypt(InputStream input, OutputStream output) throws IOException;
	
}
