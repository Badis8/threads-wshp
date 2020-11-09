package com.javaws.threads.service;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.Writer;
import java.nio.charset.Charset;

public class NaiveCypher implements Encrypter, Decrypter {

	private final String secretKey;
	
	private final Charset charset;
	
	public NaiveCypher(String secretKey, Charset charset) {
		super();
		this.secretKey = secretKey;
		this.charset = charset;
	}

	@Override
	public void encrypt(InputStream input, OutputStream output) throws IOException {
		Reader reader = null;
		Writer writer = null;
		
		try {
			
		   reader = new BufferedReader(new InputStreamReader(input, this.charset));
		   writer = new BufferedWriter(new OutputStreamWriter(output, this.charset));
		   
		   char[] cbuf = new char[secretKey.length()];
		   char[] cbufEncoded = new char[secretKey.length()];
		   
		   for(int n = reader.read(cbuf); n > -1; n = reader.read(cbuf)) {
			   for (int i = 0; i < n; i++) {
				   cbufEncoded[i] = (char) ((cbuf[i] + secretKey.charAt(i) - 128) % 65535);
			   }
			   
			   writer.write(cbufEncoded, 0, n);
		   } 
		   
		} finally {
			if (reader != null) {
				reader.close();
			}
			
			if(writer != null) {
				writer.close();
			}
		}

	}

	@Override
	public void decrypt(InputStream input, OutputStream output) throws IOException {
		Reader reader = null;
		Writer writer = null;
		
		try {
			
		   reader = new BufferedReader(new InputStreamReader(input, this.charset));
		   writer = new BufferedWriter(new OutputStreamWriter(output, this.charset));
		   
		   char[] cbuf = new char[secretKey.length()];
		   char[] cbufEncoded = new char[secretKey.length()];
		   
		   for(int n = reader.read(cbuf); n > -1; n = reader.read(cbuf)) {
			   for (int i = 0; i < n; i++) {
				   cbufEncoded[i] = (char) ((cbuf[i] - secretKey.charAt(i) + 128) % 65535);
			   }
			   
			   writer.write(cbufEncoded, 0, n);
		   } 
		   
		} finally {
			if (reader != null) {
				reader.close();
			}
			
			if(writer != null) {
				writer.close();
			}
		}
	}

	
}
