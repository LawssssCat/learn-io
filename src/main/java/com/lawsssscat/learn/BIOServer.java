package com.lawsssscat.learn;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;

/**
 *
 * 客户端发送消息，服务端接收消息
 *
 * @author lawsssscat
 *
 */
public class BIOServer {

	public void start(int port, Callback callback) throws IOException {
		ServerSocket serverSocket = new ServerSocket(port);
		System.out.println("===服务端启动===");
		Thread thread = new Thread(new Runnable() {
			@Override
			public void run() {
				while (true) {
					try {
						System.out.println("===服务端监听===");
						Socket socket = serverSocket.accept(); // 一个连接，只能处理一个客户端请求
						System.out.println("===服务端连接===");
						InputStream is = socket.getInputStream();
						BufferedReader reader = new BufferedReader(new InputStreamReader(is));
						String msg = null;
						if ((msg = reader.readLine()) != null) {
							callback.run(msg, socket);
						}
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
		});
		thread.setDaemon(true);
		thread.start();
	}

}
