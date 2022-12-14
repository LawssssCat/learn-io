package com.lawsssscat.learn.chat;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

import com.lawsssscat.learn.utils.Logger;

public class NIOChatClient {

	private static final Logger logger = Logger.get(NIOChatClient.class);

	private String host;
	private int port;

	public NIOChatClient(String host, int port) {
		this.host = host;
		this.port = port;
	}

	private SocketChannel channel;
	private NIOChatClientListener listener;

	public void login() throws IOException {
		logger.info("init ... %s:%s", host, port);
		// 建立连接
		channel = SocketChannel.open(); // 底层socket
		channel.configureBlocking(false); // ⚠️注意，这里需要在connect之前设置，否则就是阻塞connect了（不走selector的方法）
		channel.connect(new InetSocketAddress(host, port)); // tcp三次握手
		logger.info("connect! %s", getChannelInfo(channel));
		// 客户端监听服务端消息
		listener = new NIOChatClientListener(channel);
		listener.start();
	}

	public void send(String msg) throws IOException {
		logger.info("send \"%s\"", msg);
		ByteBuffer buffer = ByteBuffer.wrap(msg.getBytes());
		channel.write(buffer);
	}

	public void logout() throws IOException {
		channel.close();
		listener.close();
		logger.info("logout %s %s", channel, getChannelInfo(channel));
	}

	private String getChannelInfo(SocketChannel channel) {
		StringBuilder sb = new StringBuilder(channel != null ? channel.toString() : "");
		sb.append("\n").append("isOpen: ").append(channel.isOpen());
		sb.append("\n").append("isConnectionPending: ").append(channel.isConnectionPending());
//		try {
//			sb.append("\n").append("finishConnect: ").append(channel.finishConnect());
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
		sb.append("\n").append("isConnected: ").append(channel.isConnected());
		return sb.toString();
	}

}
