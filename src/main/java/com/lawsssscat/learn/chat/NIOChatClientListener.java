package com.lawsssscat.learn.chat;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.concurrent.TimeUnit;

import com.lawsssscat.learn.utils.Logger;

public class NIOChatClientListener {

	private static final Logger logger = Logger.get(NIOChatClientListener.class);

	private SocketChannel channel;

	public NIOChatClientListener(SocketChannel channel) {
		this.channel = channel;
	}

	private Selector selector;

	public void start() throws IOException {
		logger.info("start listen %s", channel);
		selector = Selector.open();
		channel.register(selector, SelectionKey.OP_CONNECT); // 💡 客户端监听连接事件（与之对比的是，服务端监听的是OP_ACCPET事件）
		Thread thread = new Thread(new Runnable() {
			@Override
			public void run() {
				while (selector.isOpen()) {
					try {
						int select = selector.select();
						if (select <= 0) {
							continue;
						}
						logger.info("active! %s", select);
						Iterator<SelectionKey> it = selector.selectedKeys().iterator();
						while (it.hasNext()) {
							SelectionKey key = it.next();
							it.remove();
							serviceSelectionKey(key);
						}
					} catch (Throwable t) {
						t.printStackTrace();
						logger.info("connect error! %s", channel);
						if (!channel.isOpen()) {
							try {
								close();
							} catch (IOException e) {
								e.printStackTrace();
							}
							break;
						}
					}
				}
			}
		});
		thread.setName(Thread.currentThread().getName() + "-client-listen");
		thread.setDaemon(true);
		thread.start();
	}

	public void close() throws IOException {
		selector.close();
		logger.info("close listen %s", channel);
	}

	private void serviceSelectionKey(SelectionKey key) throws IOException {
		if (key.isConnectable()) {
			logger.info("active for connectable");
			// 发现可连接（客户端专属事件）
			while (!channel.finishConnect()) {
				logger.info("connecting...%s", channel);
				try {
					TimeUnit.SECONDS.sleep(1);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			logger.info("connect ok! %s", channel);
			key.interestOps(key.interestOps() | SelectionKey.OP_READ);
		} else if (key.isReadable()) {
			logger.info("active for read");
			// read msg
			ByteBuffer buffer = ByteBuffer.allocate(4);
			StringBuilder sb = new StringBuilder();
			int len = 0;
			while ((len = channel.read(buffer)) > 0) {
				buffer.flip();
				sb.append(new String(buffer.array(), 0, buffer.remaining()));
				buffer.clear();
			}
			if (len < 0) {
				key.cancel();
				throw new IOException("服务端断开连接");
			}
			String msg = sb.toString();
			logger.info("server: " + msg);
		}
	}

}
