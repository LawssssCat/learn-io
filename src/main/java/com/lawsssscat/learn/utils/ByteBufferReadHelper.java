package com.lawsssscat.learn.utils;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.ReadableByteChannel;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.StandardCharsets;

public class ByteBufferReadHelper {

	private ByteBufferReadHelper() {
	}

	public static final Charset DEFAULT_CHARSET = StandardCharsets.UTF_8;

	public static final int DEFAULT_BUFFER_SIZE = 10;

	public static String read(ReadableByteChannel channel) throws IOException {
		CharsetDecoder decoder = DEFAULT_CHARSET.newDecoder(); // 线程不安全
		ByteBuffer byteBuffer = ByteBuffer.allocate(DEFAULT_BUFFER_SIZE);
		CharBuffer charBuffer = CharBuffer.allocate(DEFAULT_BUFFER_SIZE);
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
//			logger.info("append: %s %s", byteBuffer, charBuffer);
			sb.append(charBuffer);
			charBuffer.clear();
			byteBuffer.compact(); // ⚠️是compact
			if (len == 0) {
				break;
			}
		}
		if (len < 0) {
			throw new ClosedChannelException();
		}
		return sb.toString();
	}

}
