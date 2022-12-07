package com.lawsssscat.learn.chat;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.Socket;

import com.lawsssscat.learn.utils.Logger;

public class BIOChatClient {

	private static final Logger logger = Logger.get(BIOChatClient.class);

	private String clientId;

	private String host;
	private int port;

	private Socket socket;

	public BIOChatClient(String host, int port) {
		this.host = host;
		this.port = port;
		this.clientId = Thread.currentThread().getName();
	}

	public void send(String msg) throws IOException {
		PrintStream ps = new PrintStream(socket.getOutputStream());
		ps.println(msg);
		ps.flush();
	}

	public void login() throws IOException {
		socket = new Socket(host, port);
		// client id
		DataOutputStream dos = new DataOutputStream(socket.getOutputStream());
		dos.writeUTF(this.clientId);
		dos.flush();
		// receive msg
		Thread readerThread = new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					BufferedReader br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
					String msg;
					while ((msg = br.readLine()) != null) {
						logger.info("server: %s", msg);
					}
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		});
		readerThread.setName(clientId + "-reader");
		readerThread.setDaemon(true);
		readerThread.start();
	}

	public void logout() throws IOException {
		socket.close();
		socket = null;
	}

	public String getClientId() {
		return clientId;
	}

	public void setClientId(String clientId) {
		this.clientId = clientId;
	}

}
