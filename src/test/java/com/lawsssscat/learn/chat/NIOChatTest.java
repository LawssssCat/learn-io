package com.lawsssscat.learn.chat;

import java.io.IOException;
import java.util.Date;
import java.util.Random;

import org.junit.Test;

public class NIOChatTest {

	private Random random = new Random(new Date().getTime());

	private int port = 9786;

	@Test
	public void testChat() throws InterruptedException {
		// server
		Thread serverThread = new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					// 创建服务器
					NIOChatServer server = new NIOChatServer(port);
					// 开始监听
					server.listen();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		});
		serverThread.setName("thread-server[" + port + "]");
		serverThread.start();

		// client
		int num = 3;
		for (int i = 0; i < num; i++) {
			Thread clientThread = new Thread(new Runnable() {
				@Override
				public void run() {
					NIOChatClient client = new NIOChatClient("127.0.0.1", port);
					try {
						Thread.sleep(500 + random.nextInt(500));
						client.login();
						Thread.sleep(500 + random.nextInt(500));
						client.send("hello world! my name is " + Thread.currentThread().getName());
						Thread.sleep(500 + random.nextInt(500));
						client.send("[new]hello world! my name is " + Thread.currentThread().getName());
						Thread.sleep(500 + random.nextInt(500));
						client.logout();
					} catch (IOException | InterruptedException e) {
						e.printStackTrace();
					}
				}
			});
			clientThread.setName("thread-client[" + i + "]");
			clientThread.start();
		}

		// main
		Thread.sleep(6000);
	}

}
