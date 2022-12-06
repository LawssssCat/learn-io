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

	public void start(int port, ServerCallback callback) throws IOException {
		ServerSocket serverSocket = new ServerSocket(port);
		System.out.println("===服务端启动===");
		Thread thread = new Thread(new Runnable() {
			@Override
			public void run() {
				while (true) {
					try {
						Socket socket = serverSocket.accept();
						InputStream is = socket.getInputStream();
						BufferedReader reader = new BufferedReader(new InputStreamReader(is));
						String msg = null;
						if ((msg = reader.readLine()) != null) {
							callback.run(msg);
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
