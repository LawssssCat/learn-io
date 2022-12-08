package com.lawsssscat.learn.chat;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.ReadableByteChannel;
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
					handleSelectionKey(key);
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
	private void handleSelectionKey(SelectionKey key) throws IOException {
		SocketChannel channel = null;
		try {
			if (key.isAcceptable()) {
				// 客户端接入请求
				channel = serverChannel.accept(); // 三次握手，创建连接（connect）
				logger.info("accept: %s", channel);
				channel.configureBlocking(false);
				channel.register(selector, SelectionKey.OP_READ);
			} else if (key.isReadable()) {
				channel = (SocketChannel) key.channel();
				logger.info("read: %s", channel);
				// 客户端数据
				handleClientMsg(channel);
			} else {
				logger.info("触发未监听事件： %s", key);
			}
		} catch (Throwable e) {
			e.printStackTrace();
			if (channel != null) {
				logger.info("通道异常，需要断开！");
				channel.close();
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
	private void handleClientMsg(ReadableByteChannel channel) throws IOException {
		ByteBuffer buffer = ByteBuffer.allocate(4);
		StringBuilder sb = new StringBuilder();
		int len = 0;
		while (true) {
			len = channel.read(buffer);
			logger.info("buffer： %s [%s]", buffer, sb.length());
			if (len > 0) {
				buffer.flip();
				sb.append(new String(buffer.array(), 0, buffer.remaining()));
				buffer.clear();
			} else {
				if (len < 0) {
					logger.info("客户端关闭通道！");
					channel.close();
				}
				break;
			}
		}

		logger.info("接收通道（%s）信息： %s", channel, sb.toString());
	}

}
