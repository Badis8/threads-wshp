package com.javaws.threads.service;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class NaiveByteCypher implements Encrypter, Decrypter {

	private final String secretKey;

	public NaiveByteCypher(String secretKey) {
		super();
		this.secretKey = secretKey;
	}

	@Override
	public void encrypt(InputStream input, OutputStream output) throws IOException {
		try {
			byte[] key = secretKey.getBytes();

			byte[] cbuf = new byte[key.length];
			byte[] cbufEncoded = new byte[key.length];
		    
		    for(int n = input.read(cbuf, 0, cbuf.length); n > -1; n = input.read(cbuf, 0, cbuf.length)) {
			   for (int i = 0; i < n; i++) {
				   cbufEncoded[i] = (byte) (cbuf[i] ^ key[i]);
			   }
			   
			   output.write(cbufEncoded, 0, n);
		    } 
		    
		    output.flush();
		    
		} finally {
			if (output != null) {
				output.close();
			}

			if (input != null) {
				input.close();
			}
		}
	}

	@Override
	public void decrypt(InputStream input, OutputStream output) throws IOException {
		encrypt(input, output);
	}

}
