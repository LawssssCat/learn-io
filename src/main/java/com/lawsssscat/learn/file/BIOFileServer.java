package com.lawsssscat.learn.file;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

import com.lawsssscat.learn.Callback;

/**
 *
 * 服务端接收任意类型数据，并保存
 *
 * 短连接
 *
 * @author lawsssscat
 *
 */
public class BIOFileServer {

	private int port;
	private String basePath;

	public BIOFileServer(int port, String basePath) {
		this.port = port;
		this.basePath = basePath;
	}

	private ServerSocket serverSocket;

	public void start(Callback callback) throws IOException {
		serverSocket = new ServerSocket(port);
		System.out.println("[server] start");
		Thread thread = new Thread(new Runnable() {
			@Override
			public void run() {
				while(true) {
					try {
						System.out.println("[server] waiting...");
						Socket socket = serverSocket.accept();
						System.out.println("[server] accept!");
						new Thread(new BIOFileServerHandlerRunnable(socket, basePath, callback)).start();
						System.out.println("[server] deploy");
					} catch (Throwable e) {
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
