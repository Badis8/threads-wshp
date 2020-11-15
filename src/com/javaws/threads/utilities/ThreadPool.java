package com.javaws.threads.utilities;

import java.util.concurrent.LinkedBlockingQueue;

public class ThreadPool {
     
    private final Thread[] workers;
     
    private final LinkedBlockingQueue<Runnable> queue;
 
    public ThreadPool(int size) 
    {
    	IDThreadFactory factory = new IDThreadFactory();
        queue = new LinkedBlockingQueue<Runnable>();
        workers = new Thread[size];
 
        for (int i = 0; i < size; i++) {
            workers[i] = factory.generate(new WorkerRunnable(queue));
            workers[i].start();
        }
    }
 
    public void add(Runnable task) {
        try {
			queue.put(task);
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
			e.printStackTrace();
		}
    }
 
    public void shutdown() {
        System.out.println("Shutting down thread pool");
        EOSRunnable eos = new EOSRunnable();
        for (int i = 0; i < workers.length; i++) {
        	this.add(eos);
        }
        for (int i = 0; i < workers.length; i++) {
        	try {
				workers[i].join();
			} catch (InterruptedException e) {
				Thread.currentThread().interrupt();
				e.printStackTrace();
			} finally {
				workers[i] = null;
			}
        }
    }
}
