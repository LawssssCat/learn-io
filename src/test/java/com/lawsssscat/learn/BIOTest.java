package com.lawsssscat.learn;

import static org.junit.Assert.assertEquals;

import java.io.IOException;

import org.junit.Test;

public class BIOTest {

	@Test
	public void testSingleLine() throws IOException, InterruptedException {
		int port = 9999;
		String msg = "hello world! 服务器端，你好呀！";
		new BIOServer().start(port, new ServerCallback() {
			@Override
			public void run(String receiveMsg) {
				System.out.println(receiveMsg);
				assertEquals(msg, receiveMsg);
			}
		});
		new BIOClient().submitSingleLine("127.0.0.1", port, msg);
	}

}
