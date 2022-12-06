package com.lawsssscat.learn;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.Socket;

public class BIOServerHandlerRunnable implements Runnable {

	private Socket socket;
	private Callback callback;

	public BIOServerHandlerRunnable(Socket socket, Callback callback) {
		this.socket = socket;
		this.callback = callback;
	}

	@Override
	public void run() {
		InputStream is;
		try {
			is = socket.getInputStream();
			BufferedReader reader = new BufferedReader(new InputStreamReader(is));
			StringBuilder sb = new StringBuilder();
			String temp = null;
			while ((temp = reader.readLine()) != null) {
				sb.append(temp).append("\n");
			}
			if (sb.length() > 0) {
				sb.deleteCharAt(sb.length() - 1);
			}
			callback.run(sb.toString(), socket);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

}
