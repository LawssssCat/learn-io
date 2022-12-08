package com.lawsssscat.learn.normal;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

import com.lawsssscat.learn.utils.Logger;

/**
 *
 * NIO非阻塞通信客户端
 *
 * @author lawsssscat
 *
 */
public class NIOClient {

	private static final Logger logger = Logger.get(NIOClient.class);

	private String host;
	private int port;

	public NIOClient(String host, int port) {
		this.host = host;
		this.port = port;
		logger.info("client init");
	}

	private SocketChannel channel;

	public void login() throws IOException {
		// 获取连接
		channel = SocketChannel.open(new InetSocketAddress(host, port));
		channel.configureBlocking(false); // ⚠️ 切换成非阻塞
		logger.info("client login!");
	}

	public void send(String msg) throws IOException {
		// 分配缓冲区大小
		ByteBuffer buffer = ByteBuffer.allocate(16);
		int index = 0;
		byte[] bytes = msg.getBytes();
		while (index < bytes.length) {
			int len = bytes.length - index;
			if (len > buffer.capacity()) {
				len = buffer.capacity();
			}
			buffer.put(bytes, index, len);
			buffer.flip();
			channel.write(buffer);
			buffer.clear();
			// next
			index = index + buffer.capacity() + 1;
		}
		logger.info("client send \"%s\"", msg);
	}

}
