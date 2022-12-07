package com.lawsssscat.learn.chat;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.LinkedList;
import java.util.List;

public class BIOChatServerBus {

	private static Object mutex = new Object();

	private List<BIOChatEndpoint> onlineEndpointList;

	private static BIOChatServerBus instance;

	private BIOChatServerBus() {
		onlineEndpointList = new LinkedList<>();
	}

	public static BIOChatServerBus getInstance() {
		if (instance == null) {
			synchronized (mutex) {
				if (instance == null) {
					instance = new BIOChatServerBus();
				}
			}
		}
		return instance;
	}

	public void broadcast(String msg) {
		synchronized (onlineEndpointList) {
			onlineEndpointList.forEach((endpoint) -> {
				try {
					OutputStream os = endpoint.getSocket().getOutputStream();
					PrintStream ps = new PrintStream(os);
					ps.println(msg);
				} catch (IOException e) {
					e.printStackTrace();
				}
			});
		}
	}

	public void addEndpoint(BIOChatEndpoint endpoint) {
		synchronized (onlineEndpointList) {
			onlineEndpointList.add(endpoint);
		}
	}

	public void removeEndpoint(BIOChatEndpoint endpoint) {
		synchronized (onlineEndpointList) {
			onlineEndpointList.remove(endpoint);
		}
	}

}
