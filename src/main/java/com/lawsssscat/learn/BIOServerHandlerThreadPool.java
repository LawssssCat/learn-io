package com.lawsssscat.learn;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class BIOServerHandlerThreadPool {

	private ExecutorService executorService;

	public BIOServerHandlerThreadPool(int maxThreadNum, int queueSize) {
		executorService = new ThreadPoolExecutor(3, maxThreadNum, 120, TimeUnit.SECONDS,
				new ArrayBlockingQueue<Runnable>(queueSize));
	}

	public void execute(Runnable command) {
		executorService.execute(command);
	}

}
