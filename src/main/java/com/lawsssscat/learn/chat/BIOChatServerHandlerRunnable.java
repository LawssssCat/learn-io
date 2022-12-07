package com.lawsssscat.learn.chat;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;

import com.lawsssscat.learn.utils.Logger;

public class BIOChatServerHandlerRunnable implements Runnable {

	private static final Logger logger = Logger.get(BIOChatServerHandlerRunnable.class);

	private BIOChatServerBus bus = BIOChatServerBus.getInstance();

	private Socket socket;

	public BIOChatServerHandlerRunnable(Socket socket) {
		this.socket = socket;
	}

	@Override
	public void run() {
		BIOChatEndpoint endpoint = null;
		try {
			// endpoint
			endpoint = getBIOChatEndpoint();
			// bus
			bus.addEndpoint(endpoint);
			bus.broadcast(endpoint.getClientId() + " login!");
			// msg
			BufferedReader br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			String msg;
			while ((msg = br.readLine()) != null) {
				String info = String.format("%s: %s", endpoint.getClientId(), msg);
				logger.info(info);
				bus.broadcast(info);
			}
		} catch (Throwable e) {
			logger.info("exception:" + e.getMessage());
		} finally {
			try {
				socket.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
			if (endpoint != null) {
				bus.removeEndpoint(endpoint);
				bus.broadcast(endpoint.getClientId() + " logout!");
				logger.info(endpoint.getClientId() + " logout!");
			}
		}
	}

	private BIOChatEndpoint getBIOChatEndpoint() throws IOException {
		DataInputStream ds = new DataInputStream(socket.getInputStream());
		String clientId = ds.readUTF();
		BIOChatEndpoint endpoint = new BIOChatEndpoint(socket);
		endpoint.setClientId(clientId);
		return endpoint;
	}

}
