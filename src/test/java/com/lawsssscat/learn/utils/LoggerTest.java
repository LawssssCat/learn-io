package com.lawsssscat.learn.utils;

import org.junit.Test;

public class LoggerTest {

	private static final Logger logger = Logger.get(LoggerTest.class);

	@Test
	public void testInfo() {
		logger.info("hello world");
	}

}
