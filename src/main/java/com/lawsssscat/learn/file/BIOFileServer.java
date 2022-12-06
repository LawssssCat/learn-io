package com.lawsssscat.learn.file;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

import com.lawsssscat.learn.Callback;

public class BIOFileServer {

	private int port;
	private String basePath;

	public BIOFileServer(int port, String basePath) {
		this.port = port;
		this.basePath = basePath;
	}

	public void start(Callback callback) throws IOException {
		ServerSocket serverSocket = new ServerSocket(port);
		System.out.println("[server] start");
		Thread thread = new Thread(new Runnable() {
			@Override
			public void run() {
				while(true) {
					try {
						System.out.println("[server] waiting...");
						Socket socket = serverSocket.accept();
						System.out.println("[server] accept!");
						new Thread(new BIOFileServerHanderRunnable(socket, basePath, callback)).start();
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
