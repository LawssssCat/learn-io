package com.lawsssscat.learn.file;

import java.io.IOException;

import org.junit.Test;

import com.lawsssscat.learn.Callback;
import com.lawsssscat.learn.utils.Logger;

public class BIOFileTest {

	private static final Logger logger = Logger.get(BIOFileClient.class);

	String projectPath = System.getProperty("user.dir");

	private String getInputPath() {
		String name = this.getClass().getName();
		return projectPath + "/src/test/java/" + name.replace(".", "/") + ".java";
	}

	@Test
	public void testFileBIO() throws IOException, InterruptedException {
		int port = 9997;
		// server
		BIOFileServer server = new BIOFileServer(port, projectPath+"/target/");
		server.start(new Callback() {
			@Override
			public void run(Object... args) {
				logger.info("[server] [%sbyte] %s => %s", args);
			}
		});
		// client
		BIOFileClient client = new BIOFileClient("127.0.0.1", port);
		new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					client.sendFile(getInputPath());
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}).start();
		new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					client.sendFile(projectPath + "/images/logo-java.jpg");
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}).start();
		// end
		Thread.sleep(1000);
	}

}
