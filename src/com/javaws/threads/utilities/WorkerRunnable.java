package com.javaws.threads.utilities;

import java.util.concurrent.LinkedBlockingQueue;

public class WorkerRunnable implements Runnable {

	private final LinkedBlockingQueue<Runnable> queue;

	public WorkerRunnable(LinkedBlockingQueue<Runnable> queue) {
		super();
		this.queue = queue;
	}

	public void run() {
		Runnable task;

		while (true) {
			try {
				// Blocking queue methods are thread safe 
				// https://docs.oracle.com/javase/7/docs/api/java/util/concurrent/BlockingQueue.html
				task = (Runnable) queue.take();
				task.run();

			} catch (InterruptedException e) {
				Thread.currentThread().interrupt();
				e.printStackTrace();
			}
		}
	}
}