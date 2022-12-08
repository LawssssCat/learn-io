package com.lawsssscat.learn.normal;

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

/**
 *
 * NIO非阻塞通信服务端
 *
 * @author lawsssscat
 *
 */
public class NIOServer {

	private static final Logger logger = Logger.get(NIOServer.class);

	private int port;

	public NIOServer(int port) {
		this.port = port;
		logger.info("server init");
	}

	public void startDaemon() {
		Thread thread = new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					start();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		});
		thread.setDaemon(true);
		thread.setName("thread-server[" + port + "]");
		thread.start();
	}

	public void start() throws IOException {
		logger.info("server start");
		// 获取通道
		ServerSocketChannel serverChannel = ServerSocketChannel.open();
		serverChannel.configureBlocking(false); // ⚠️ 切换非阻塞模式
		serverChannel.bind(new InetSocketAddress(port)); // 绑定连接
		// 将通道注册到选择器上 💡 这里绑定“接收事件”
		Selector selector = Selector.open();
		serverChannel.register(selector, SelectionKey.OP_ACCEPT);
		logger.info("server waiting");
		while (selector.select() > 0) {
			Iterator<SelectionKey> it = selector.selectedKeys().iterator();
			while (it.hasNext()) {
				SelectionKey key = it.next();
				if (key.isAcceptable()) {
					// 接收就绪
					SocketChannel channel = serverChannel.accept(); // ⚠️ accept 接入当前客户端通道
					channel.configureBlocking(false); // ⚠️ 切换非阻塞模式
					// 将通道注册到选择器上 💡 这里绑定“读事件”
					channel.register(selector, SelectionKey.OP_READ);
				} else if(key.isReadable()) {
					// 读就绪
					ReadableByteChannel channel = (ReadableByteChannel) key.channel(); // ⚠️ 通过选择器选择结果，反向获取客户端通道
					// 读数据
					StringBuilder sb = new StringBuilder();
					ByteBuffer buffer = ByteBuffer.allocate(4);
					while (channel.read(buffer) > 0) {
						buffer.flip();
						String msg = new String(buffer.array(), 0, buffer.remaining());
						sb.append(msg);
						buffer.clear();
					}
					logger.info(sb.toString());
				}
				it.remove(); // ⚠️ 需要移除事件，否则一直存在
			}
		}
	}

}
