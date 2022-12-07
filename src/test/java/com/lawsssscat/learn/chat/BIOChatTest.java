package com.lawsssscat.learn.chat;

import java.io.IOException;
import java.util.Date;
import java.util.Random;

import org.junit.Test;

import com.lawsssscat.learn.utils.Logger;

public class BIOChatTest {

	private static final Logger logger = Logger.get(BIOChatTest.class);

	private Random random = new Random(new Date().getTime());

	private int port = 8797;

	@Test
	public void testChat() throws InterruptedException, IOException {
		// server
		BIOChatServer server = new BIOChatServer(port);
		server.start();
		// client
		int num = 3;
		for (int i = 0; i < num; i++) {
			Thread thread = new Thread(new Runnable() {
				@Override
				public void run() {
					BIOChatClient client = new BIOChatClient("127.0.0.1", port);
					try {
						Thread.sleep(random.nextInt(500) + 500);
						client.login();
						Thread.sleep(random.nextInt(500) + 500);
						client.send("hi! i am " + client.getClientId());
						Thread.sleep(random.nextInt(500) + 500);
						client.logout();
					} catch (IOException | InterruptedException e) {
						e.printStackTrace();
					}
				}
			});
			thread.setName("thread-client[" + i + "]");
			thread.start();
		}
		Thread.sleep(3000);
	}

}
