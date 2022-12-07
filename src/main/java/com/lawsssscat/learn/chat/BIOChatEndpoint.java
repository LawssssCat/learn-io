package com.lawsssscat.learn.chat;

import java.net.Socket;

public class BIOChatEndpoint {

	private Socket socket;

	private String clientId = "undefined";

	public BIOChatEndpoint(Socket socket) {
		this.socket = socket;
	}

	public Socket getSocket() {
		return socket;
	}

	public void setSocket(Socket socket) {
		this.socket = socket;
	}

	public String getClientId() {
		return clientId;
	}

	public void setClientId(String clientId) {
		this.clientId = clientId;
	}

}
