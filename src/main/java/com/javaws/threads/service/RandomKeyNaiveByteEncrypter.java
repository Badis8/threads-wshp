package com.javaws.threads.service;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.UUID;

public class RandomKeyNaiveByteEncrypter implements RandomKeyEncrypter {

	@Override
	public String encrypt(InputStream input, OutputStream output) throws IOException {
		try {
			String randomKey = UUID.randomUUID().toString();
			byte[] key = randomKey.getBytes();

			byte[] cbuf = new byte[key.length];
			byte[] cbufEncoded = new byte[key.length];

			for (int n = input.read(cbuf, 0, cbuf.length); n > -1; n = input.read(cbuf, 0, cbuf.length)) {
				for (int i = 0; i < n; i++) {
					cbufEncoded[i] = (byte) (cbuf[i] ^ key[i]);
				}

				output.write(cbufEncoded, 0, n);
			}

			output.flush();

			return randomKey;
		} finally {
			if (output != null) {
				output.close();
			}

			if (input != null) {
				input.close();
			}
		}
	}

}
