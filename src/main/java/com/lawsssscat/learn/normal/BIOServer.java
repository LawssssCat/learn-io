package com.lawsssscat.learn.normal;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

import com.lawsssscat.learn.Callback;

/**
 *
 * 客户端发送消息，服务端接收消息
 *
 * 短连接
 *
 * @author lawsssscat
 *
 */
public class BIOServer {

	private ServerSocket serverSocket;

	public void start(int port, Callback callback) throws IOException {
		serverSocket = new ServerSocket(port);
		System.out.println("===服务端启动===");
		Thread thread = new Thread(new Runnable() {
			@Override
			public void run() {
				BIOServerHandlerThreadPool threadPool = new BIOServerHandlerThreadPool(6, 10);
				int count = 0;
				while (true) {
					count ++;
					try {
						System.out.println("===服务端监听==="+count);
						Socket socket = serverSocket.accept(); // 一个连接，只能处理一个客户端请求
						System.out.println("===服务端连接==="+count);
						BIOServerHandlerRunnable command = new BIOServerHandlerRunnable(socket, callback);
						threadPool.execute(command);
						System.out.println("===服务端交付===" + count);
					} catch (Throwable e) {
						e.printStackTrace();
					}
				}
			}
		});
		thread.setDaemon(true);
		thread.start();
	}

}
