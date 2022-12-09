package com.lawsssscat.learn.chat;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;

import com.lawsssscat.learn.utils.Logger;

public class NIOChatServer {

	private static final Logger logger = Logger.get(NIOChatServer.class);

	// 多路复用器
	private Selector selector;
	// 服务端管道
	private ServerSocketChannel serverChannel;

	/**
	 *
	 * 初始化服务
	 *
	 * @param port
	 * @throws IOException
	 */
	public NIOChatServer(int port) throws IOException {
		logger.info("server init...");
		// 初始化组件
		selector = Selector.open();
		serverChannel = ServerSocketChannel.open();
		serverChannel.bind(new InetSocketAddress(port));
		serverChannel.configureBlocking(false);
		serverChannel.register(selector, SelectionKey.OP_ACCEPT); // 注册接收事件
		logger.info("server listen [" + port + "]");
	}

	/**
	 *
	 * 开启服务监听
	 *
	 * @throws IOException
	 */
	public void listen() {
		// 开始监听
		while (true) {
			logger.info("server waiting...%s", serverChannel);
			try {
				selector.select();
				logger.info("server active! [online:%s]", selector.keys().size());
				Iterator<SelectionKey> it = selector.selectedKeys().iterator();
				while (it.hasNext()) {
					SelectionKey key = it.next();
					it.remove();
					logger.info("server handle selectionKey %s", key);
					serviceSelectionKey(key);
				}
			} catch (Throwable e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 *
	 * 处理监听事件
	 *
	 * @param key
	 * @throws IOException
	 */
	private void serviceSelectionKey(SelectionKey key) throws IOException {
		try {
			if (key.isAcceptable()) {
				// 客户端接入请求（服务端专属事件）
				SocketChannel channel = serverChannel.accept(); // 三次握手，创建连接（connect）
				logger.info("accept: %s", channel);
				channel.configureBlocking(false);
				channel.register(selector, SelectionKey.OP_READ);
			} else if (key.isReadable()) {
				SocketChannel channel = (SocketChannel) key.channel();
				logger.info("read: %s", channel);
				// 客户端数据
				handleClientMsg(channel);
			} else {
				logger.info("触发未监听事件： %s", key);
			}
		} catch (Throwable e) {
			e.printStackTrace();
			if (key.channel() != null) {
				logger.info("通道异常，需要断开！ %s", key.channel());
				key.channel().close();
			}
		}
	}

	/**
	 *
	 * 处理客户端信息
	 *
	 * @param channel
	 * @throws IOException
	 */
	private void handleClientMsg(SocketChannel channel) throws IOException {
		// 接收数据
		ByteBuffer buffer = ByteBuffer.allocate(4);
		StringBuilder sb = new StringBuilder();
		int len = 0;
		while ((len = channel.read(buffer)) > 0) {
			logger.info("buffer： %s [%s]", buffer, sb.length());
			buffer.flip();
			sb.append(new String(buffer.array(), 0, buffer.remaining()));
			buffer.clear();
		}
		if (len < 0) {
			logger.info("客户端关闭通道！ %s", channel);
			channel.close();
			return;
		}
		String msg = sb.toString();

		// 处理数据
		logger.info("接收通道（%s）信息： \"%s\"", channel, msg);
		for (SelectionKey key : selector.keys()) { // 广播
			if (key.channel() instanceof SocketChannel) {
				SocketChannel socketChannel = (SocketChannel) key.channel();
				if (socketChannel.equals(channel)) {
					logger.info("回复欢迎词 [%s => %s]", serverChannel.getLocalAddress(), socketChannel.getRemoteAddress());
					socketChannel.write(ByteBuffer.wrap("欢迎登录！".getBytes()));
				} else {
					logger.info("广播消息 [%s => %s] \"%s\"", channel.getRemoteAddress(), socketChannel.getRemoteAddress(), msg);
					String broadcastMsg = String.format("%s: %s", socketChannel.getRemoteAddress(), msg);
					socketChannel.write(ByteBuffer.wrap(broadcastMsg.getBytes()));
				}
			}
		}
	}

}
