package com.lawsssscat.learn.chat;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

import com.lawsssscat.learn.utils.Logger;

/**
 *
 * 服务端接收客户端消息，并转发给其他客户端
 *
 * 长连接
 *
 * @author lawsssscat
 *
 */
public class BIOChatServer {

	private static final Logger logger = Logger.get(BIOChatServer.class);

	private int port;

	public BIOChatServer(int port) {
		this.port = port;
	}

	public void start() throws IOException {
		ServerSocket serverSocket = new ServerSocket(port);
		logger.info("start...");
		Thread thread = new Thread(new Runnable() {
			@Override
			public void run() {
				int count = 0;
				while (true) {
					count++;
					try {
						logger.info("waiting...");
						Socket socket = serverSocket.accept();
						logger.info("accept!");
						Thread thread = new Thread(new BIOChatServerHandlerRunnable(socket));
						thread.setName("thread-server[" + count + "]");
						thread.start();
						logger.info("deploy~");
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
		});
		thread.setName("thread-server[main]");
		thread.setDaemon(true);
		thread.start();
	}

}
