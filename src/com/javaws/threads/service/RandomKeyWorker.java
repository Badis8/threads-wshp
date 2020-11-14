package com.javaws.threads.service;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Path;

public class RandomKeyWorker implements Runnable, RandomKeySender {

	private PrintWriter pw;

	private final String path;

	private String currentKeyLine;

	private boolean keyWriteActive = true;

	public RandomKeyWorker(String path) {
		super();
		this.path = path;
	}

	public void start() throws IOException {
		File file = Path.of(path).toFile();
		file.createNewFile();
		this.pw = new PrintWriter(file);
	}

	@Override
	synchronized public void send(String path, String key) {
		while (currentKeyLine != null) {
			try {
				this.wait();
			} catch (InterruptedException e) {
				// https://dzone.com/articles/why-do-we-need-threadcurrentthreadinterrupt-in-int
				Thread.currentThread().interrupt();
				e.printStackTrace();
			}
		}
		this.currentKeyLine = path + ":" + key;
		this.notifyAll();

	}

	synchronized public void close() {
		while (currentKeyLine != null) {
			try {
				this.wait();
			} catch (InterruptedException e) {
				// https://dzone.com/articles/why-do-we-need-threadcurrentthreadinterrupt-in-int
				Thread.currentThread().interrupt();
				e.printStackTrace();
			}
		}

		this.keyWriteActive = false;
		this.notifyAll();
		this.pw.close();
	}

	@Override
	public void run() {
		synchronized (this) {
			while (this.keyWriteActive) {

				while (this.keyWriteActive && currentKeyLine == null) {
					try {
						this.wait();

					} catch (InterruptedException e) {
						// https://dzone.com/articles/why-do-we-need-threadcurrentthreadinterrupt-in-int
						Thread.currentThread().interrupt();
						e.printStackTrace();
					}
				}
				if (!keyWriteActive) {
					break;
				}
				this.pw.println(this.currentKeyLine);
				this.currentKeyLine = null;
				this.notifyAll();
			}

		}
	}

}
