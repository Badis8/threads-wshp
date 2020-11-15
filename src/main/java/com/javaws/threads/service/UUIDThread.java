package com.javaws.threads.service;

import java.util.UUID;

public class UUIDThread extends Thread {


	public UUIDThread(Runnable target) {
		super(target, UUID.randomUUID().toString());
	}

	@Override
	public synchronized void start() {
		System.out.println("Starting thread : "+ this.getName());
		super.start();
	}

	@Override
	public void run() {
		try {
			super.run();
		}finally {
			System.out.println("Run ends for thread : "+ this.getName());
		}
	}

}
