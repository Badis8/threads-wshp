package com.javaws.threads.service;

import com.javaws.threads.repository.KeyItem;
import com.javaws.threads.repository.KeysRepository;

public class RandomKeyWorker implements Runnable, RandomKeySender {

	private final KeysRepository keyRepository;

	private KeyItem currentKeyItem;
	
	private boolean keyWriteActive = true;

	public RandomKeyWorker(KeysRepository keyRepository) {
		super();
		this.keyRepository = keyRepository;
	}

	@Override
	synchronized public void send(String path, String key) {
		while (currentKeyItem != null) {
			try {
				this.wait();
			} catch (InterruptedException e) {
				// https://dzone.com/articles/why-do-we-need-threadcurrentthreadinterrupt-in-int
				Thread.currentThread().interrupt();
				e.printStackTrace();
			}
		}
		this.currentKeyItem = new KeyItem(path, key);
		this.notifyAll();

	}

	synchronized public void close() {
		while (currentKeyItem != null) {
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
	}

	@Override
	public void run() {
		synchronized (this) {
			while (this.keyWriteActive) {

				while (this.keyWriteActive && currentKeyItem == null) {
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
				Object id = this.keyRepository.create(currentKeyItem);
				System.out.println("Inserted key ID : "+id+" "+id.getClass());
				this.currentKeyItem = null;
				this.notifyAll();
			}

		}
	}

}
