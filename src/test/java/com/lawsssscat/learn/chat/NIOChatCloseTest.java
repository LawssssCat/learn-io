package com.lawsssscat.learn.chat;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.junit.Test;

public class NIOChatCloseTest {

	private static int port = 8888;

	public static void main(String[] args) throws IOException {
		NIOChatServer server = new NIOChatServer(port);
		server.listen();
	}

	@Test
	public void testSend() throws IOException {
		try {
			NIOChatClient client = new NIOChatClient("127.0.0.1", port);
			client.login();
			client.send("你好！" + new SimpleDateFormat("YYYY-MM-DD hh:mm:ss").format(new Date()));
			Thread.sleep(1000);
//			client.logout();
			Thread.sleep(1000);
			client.send("hello world!!!" + new SimpleDateFormat("YYYY-MM-DD hh:mm:ss").format(new Date()));
//			client.logout();
//			client.login();
//			client.send("!!!!你you好！" + new SimpleDateFormat("YYYY-MM-DD hh:mm:ss").format(new Date()));
//			client.logout();
		} catch (Throwable e) {
			e.printStackTrace();
		}
	}

}
