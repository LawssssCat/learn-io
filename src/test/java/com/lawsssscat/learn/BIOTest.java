package com.lawsssscat.learn;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.net.Socket;

import org.junit.Test;

public class BIOTest {

	private Integer count = 0;

	@Test
	public void testSingleLine() throws IOException, InterruptedException {
		int port = 9999;
		String msg = "hello \nworld! 服务器端，你好呀！";
		new BIOServer().start(port, new Callback() {
			@Override
			public void run(Object... args) {
				try {
					Thread.sleep(100);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				String receiveMsg = (String) args[0];
				Socket socket = (Socket) args[1];
				synchronized (count) {
					count++;
					System.out.println(String.format("%s: %s [%s]", socket, receiveMsg, count));
				}
				assertTrue(receiveMsg.indexOf(msg) >= 0);
			}
		});
		Integer num = 10;
		BIOClient client = new BIOClient();
		for (int i = 0; i < num; i++) {
			Thread thread = new Thread(new Runnable() {
				@Override
				public void run() {
					try {
						System.out.println("客户端，发送信息...");
						client.submitSingleLine("127.0.0.1", port, msg + Thread.currentThread().getName());
						System.out.println("客户端，发送信息完成！");
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			});
			thread.setName("thread-" + i);
			thread.start();
		}
		Thread.sleep(1000);
		synchronized (count) {
			assertEquals(num, count);
		}
	}

}
