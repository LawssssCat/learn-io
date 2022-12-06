package com.lawsssscat.learn;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.Socket;

public class BIOServerRunnable implements Runnable {

	private Socket socket;
	private Callback callback;

	public BIOServerRunnable(Socket socket, Callback callback) {
		this.socket = socket;
		this.callback = callback;
	}

	@Override
	public void run() {
		InputStream is;
		try {
			is = socket.getInputStream();
			BufferedReader reader = new BufferedReader(new InputStreamReader(is));
			String msg = null;
			if ((msg = reader.readLine()) != null) {
				callback.run(msg, socket);
			}
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

}
