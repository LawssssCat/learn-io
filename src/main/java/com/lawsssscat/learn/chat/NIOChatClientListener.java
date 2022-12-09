package com.lawsssscat.learn.chat;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.StandardCharsets;
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

			Charset charset = StandardCharsets.UTF_8;
			CharsetDecoder decoder = charset.newDecoder(); // 线程不安全

			// read msg
			int bufferSize = 10; // 大于8（2022年utf8最长大概8字节了，但规则好像是无限长度的）
			ByteBuffer byteBuffer = ByteBuffer.allocate(bufferSize);
			CharBuffer charBuffer = CharBuffer.allocate(bufferSize);
			StringBuilder sb = new StringBuilder();
			int len = 0;
			while ((len = channel.read(byteBuffer)) >= 0) {
				byteBuffer.flip();
				if (len > 0) {
					decoder.decode(byteBuffer, charBuffer, false);
				} else {
					decoder.decode(byteBuffer, charBuffer, true);
				}
				charBuffer.flip();
				logger.info("append: %s %s", byteBuffer, charBuffer);
				sb.append(charBuffer);
				charBuffer.clear();
				byteBuffer.compact(); // ⚠️是compact
				if (len == 0) {
					break;
				}
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
