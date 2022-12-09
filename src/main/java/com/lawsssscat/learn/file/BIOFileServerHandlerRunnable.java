package com.lawsssscat.learn.file;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.UUID;

import com.lawsssscat.learn.Callback;

public class BIOFileServerHandlerRunnable implements Runnable {

	private Socket socket;
	private Callback callback;
	private String basePath;

	public BIOFileServerHandlerRunnable(Socket socket, String basePath, Callback callback) {
		this.socket = socket;
		this.callback = callback;
		this.basePath = basePath;
	}

	private String getFilePath(String filename) {
		String suffix = "";

		int lastIndexOf = filename.lastIndexOf(".");
		if(lastIndexOf>0) {
			suffix = filename.substring(lastIndexOf);
			filename = filename.substring(0, lastIndexOf-1);
		}

		while (true) {
			String newname = basePath + "/" + filename + "-" + UUID.randomUUID() + suffix;
			File file = new File(newname);
			if (!file.exists()) {
				return file.getAbsolutePath();
			}
		}
	}

	@Override
	public void run() {
		try {
			DataInputStream dis = new DataInputStream(socket.getInputStream());
			// filename
			String filename = dis.readUTF();
			String outputFilename = getFilePath(filename);

			try (// output
					FileOutputStream fos = new FileOutputStream(outputFilename)) {
				byte[] buffer = new byte[1024];
				int len;
				int size = 0;
				while ((len = dis.read(buffer)) > 0) {
					fos.write(buffer, 0, len);
					size += len;
				}

				callback.run(size, filename, outputFilename);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
