package com.lawsssscat.learn.normal;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

import org.junit.Test;

import com.lawsssscat.learn.utils.Logger;

public class NIOTest {

	private static final Logger logger = Logger.get(NIOTest.class);

	/**
	 * buffer 常用方法
	 */
	@Test
	public void testBuffer() {
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

	@Test
	public void testBuffer02() {
		// wrap
		ByteBuffer wrap = ByteBuffer.wrap("hello world!".getBytes());
		logger.info("wrap: %s [hash=%s]", wrap, wrap.hashCode());
		// compact
		byte[] bytes = new byte[3];
		ByteBuffer get = wrap.get(bytes);
		logger.info(new String(bytes));
		logger.info("get: %s [hash=%s]", get, get.hashCode());
		Buffer limit = get.limit(10);
		logger.info("limit: %s [hash=%s]", limit, limit.hashCode());
		ByteBuffer compact = wrap.compact();
		logger.info("compact: %s [hash=%s]", compact, compact.hashCode());
		ByteBuffer flip = (ByteBuffer) compact.flip();
		logger.info(new String(flip.array(), 0, flip.remaining()));
		logger.info("flip: %s [hash=%s]", flip, flip.hashCode());
	}

	private String projectPath = System.getProperty("user.dir");

	private File channelWriteFile = new File(projectPath + "/target/channelWriteTest.txt");

	/**
	 * Channel读/写数据
	 */
	@Test
	public void testChannel() {
		String except = String.format("hello world! %s", new SimpleDateFormat("YYYY-MM-DD hh:mm:ss").format(new Date()));
		// 写
		try (// 直接输出流通向目标文件
				FileOutputStream fos = new FileOutputStream(channelWriteFile)) {
			// 得到直接输出流对应的Channel
			FileChannel channel = fos.getChannel();
			// 分配缓冲区
			ByteBuffer buffer = ByteBuffer.allocate(1024);
			buffer.put(except.getBytes());
			buffer.flip();
			// 写出数据
			channel.write(buffer);
			channel.close();
			logger.info("写出完成! %s", channelWriteFile.getAbsolutePath());
		} catch (IOException e) {
			e.printStackTrace();
		}

		String actual = null;
		try (// 读
				FileInputStream fis = new FileInputStream(channelWriteFile)) {
			FileChannel channel = fis.getChannel();
			ByteBuffer buffer = ByteBuffer.allocate(1024);
			channel.read(buffer);
			buffer.flip();
			actual = new String(buffer.array(), 0, buffer.remaining());
			logger.info("读出： \"%s\"", actual);
		} catch (IOException e) {
			e.printStackTrace();
		}

		assertEquals(except, actual);
	}

	/**
	 * Channel复制数据
	 */
	@Test
	public void testChannelCopy() {
		File srcFile = new File(projectPath + "/src/test/java/" + NIOTest.class.getName().replaceAll("\\.", "/") + ".java");
		File dstFile = new File(projectPath + "/target/channelCopyTest-" + UUID.randomUUID().toString() + ".java");

		logger.info("copy <== \"%s\"", srcFile.getAbsolutePath());
		logger.info("copy ==> \"%s\"", dstFile.getAbsolutePath());
		assertTrue("file does not exists " + srcFile.getAbsolutePath(), srcFile.exists());

		try (
				FileInputStream fis = new FileInputStream(srcFile);
				FileOutputStream fos = new FileOutputStream(dstFile)) {
			FileChannel isChannel = fis.getChannel();
			FileChannel osChannel = fos.getChannel();

			ByteBuffer buffer = ByteBuffer.allocate(1024);
			while (isChannel.read(buffer) > 0) {
				buffer.flip();
				osChannel.write(buffer);
				buffer.clear();
			}

			isChannel.close();
			osChannel.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

		logger.info("复制完成！ [%sbyte] => [%sbyte]", srcFile.length(), dstFile.length());
		assertEquals(srcFile.length(), dstFile.length());
	}

	/**
	 * Channel复制数据 - 通过transfer方法
	 */
	@Test
	public void testChannelCopyByTransfer() {
		File srcFile = new File(projectPath + "/src/test/java/" + NIOTest.class.getName().replaceAll("\\.", "/") + ".java");
		File dstFile = new File(projectPath + "/target/channelCopyTestByTransform-" + UUID.randomUUID().toString() + ".java");

		logger.info("copy <== \"%s\"", srcFile.getAbsolutePath());
		logger.info("copy ==> \"%s\"", dstFile.getAbsolutePath());
		assertTrue("file does not exists " + srcFile.getAbsolutePath(), srcFile.exists());

		try (FileInputStream fis = new FileInputStream(srcFile); FileOutputStream fos = new FileOutputStream(dstFile)) {
			FileChannel isChannel = fis.getChannel();
			FileChannel osChannel = fos.getChannel();

			// 两种方法，功能一样
			// osChannel.transferFrom(isChannel, isChannel.position(), isChannel.size());
			isChannel.transferTo(isChannel.position(), isChannel.size(), osChannel);

			isChannel.close();
			osChannel.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

		logger.info("复制完成！ [%sbyte] => [%sbyte]", srcFile.length(), dstFile.length());
		assertEquals(srcFile.length(), dstFile.length());
	}

	/**
	 * 测试客户端、服务端通信
	 *
	 * @throws IOException
	 * @throws InterruptedException
	 */
	@Test
	public void testSelector() throws IOException, InterruptedException {
		int port = 7798;

		// server
		NIOServer server = new NIOServer(port);
		server.startDaemon();

		// client
		int num = 3;
		for (int i = 0; i < num; i++) {
			Thread thread = new Thread(new Runnable() {
				@Override
				public void run() {
					NIOClient client = new NIOClient("127.0.0.1", port);
					try {
						client.login();
						client.send("Hello world! I am " + Thread.currentThread());
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			});
			thread.setName("thread-client[" + i + "]");
			thread.start();
		}

		Thread.sleep(1000);
	}
}
