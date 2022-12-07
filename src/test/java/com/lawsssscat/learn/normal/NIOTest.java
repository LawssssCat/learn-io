package com.lawsssscat.learn.normal;

import java.nio.Buffer;
import java.nio.ByteBuffer;

import org.junit.Test;

import com.lawsssscat.learn.utils.Logger;

public class NIOTest {

	private static final Logger logger = Logger.get(NIOTest.class);

	@Test
	public void test01() {
		// 容量1024byte
		ByteBuffer buffer = ByteBuffer.allocate(1024);
		logger.info("buffer: %s [hash=%s]", buffer, buffer.hashCode());
		logger.info("capacity: %s", buffer.capacity());
		// remaining
		logger.info("================ remaining ====================");
		logger.info("remaining: %s", buffer.remaining());
		logger.info("hasRemaining: %s", buffer.hasRemaining());
		// position
		logger.info("================ position ====================");
		logger.info("position: %s", buffer.position()); // position初始值0
		Buffer position = buffer.position(256);
		logger.info("position: %s [hash=%s(%s)]", position, position.hashCode(), position == buffer);
		// limit
		logger.info("================ limit ====================");
		logger.info("limit: %s", buffer.limit()); // limit初始值capacity
		Buffer limit = buffer.limit(512);
		logger.info("limit: %s [hash=%s(%s)]", limit, limit.hashCode(), limit==buffer);
		// mark,put,reset,get
		logger.info("================ mark,put,reset,get ====================");
		Buffer mark = buffer.mark();
		logger.info("mark: %s [hash=%s(%s)]", mark, mark.hashCode(), mark == buffer);
		ByteBuffer put = buffer.put("hello world!".getBytes());
		logger.info("put: %s [hash=%s(%s)]", put, put.hashCode(), put == buffer); // position 增长
		Buffer reset = buffer.reset();
		logger.info("reset: %s [hash=%s(%s)]", reset, reset.hashCode(), reset == buffer); // position 回归
		byte[] temp = new byte[6];
		ByteBuffer get = buffer.get(temp);
		logger.info(new String(temp));
		logger.info("get: %s [hash=%s(%s)]", get, get.hashCode(), get == buffer); // position 增长【⚠️get也是往后读，因此导致position增长】
		reset = buffer.reset();
		int position_index = buffer.position();
		temp = new byte[6];
		get = buffer.get(temp);
		logger.info(new String(temp) + " （数据可以被重新读到）"); // 数据可以重新被读到
		logger.info("get: %s [hash=%s(%s)]", get, get.hashCode(), get == buffer); //
		// clear
		logger.info("================ clear ====================");
		Buffer clear = buffer.clear();
		logger.info("clear: %s [hash=%s(%s)]", clear, clear.hashCode(), clear == buffer);
		position = buffer.position(position_index);
		temp = new byte[6];
		get = buffer.get(temp);
		logger.info(new String(temp) + " （clear后，数据还在）"); // 数据还在
		logger.info("get: %s [hash=%s(%s)]", get, get.hashCode(), get == buffer); //
		// rewind
		logger.info("================ rewind ====================");
		limit = buffer.limit(512);
		position = buffer.position(256);
		mark = buffer.mark();
		Buffer rewind = buffer.rewind();
		logger.info("rewind: %s [hash=%s(%s)]", rewind, rewind.hashCode(), rewind == buffer);
		position = buffer.position(position_index);
		temp = new byte[6];
		get = buffer.get(temp);
		logger.info(new String(temp) + " （rewind后，数据还在）"); // 数据还在
		logger.info("get: %s [hash=%s(%s)]", get, get.hashCode(), get == buffer);
		// flip
		Buffer flip = buffer.flip();
		logger.info("flip: %s [hash=%s(%s)]", flip, flip.hashCode(), flip == buffer);
	}

}
