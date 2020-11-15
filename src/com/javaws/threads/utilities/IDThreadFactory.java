package com.javaws.threads.utilities;

import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

public class IDThreadFactory implements ThreadFactory {

	private final AtomicInteger counter = new AtomicInteger(0);
	
	public Thread newThread(Runnable r) {
		return new Thread(() -> {
			System.out.println("Starting thread : "+ Thread.currentThread().getName());
			try {
				r.run();
			}finally {
				System.out.println("Run ends for thread : "+ Thread.currentThread().getName());
			}
		}, "["+ counter.getAndIncrement()+"]" );
	}


}
