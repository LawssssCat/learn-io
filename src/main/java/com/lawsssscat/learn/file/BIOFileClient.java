package com.lawsssscat.learn.file;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.Socket;

/**
 *
 * 客户端上传任意类型的文件数据
 *
 * @author lawsssscat
 *
 */
public class BIOFileClient {

	private int port;
	private String host;

	public BIOFileClient(String host, int port) {
		this.host = host;
		this.port = port;
	}

	public void sendFile(String path) throws IOException {
		try (Socket socket = new Socket(host, port);) {
			DataOutputStream dos = new DataOutputStream(socket.getOutputStream());
			File file = new File(path);
			String filename = file.getName();
			dos.writeUTF(filename);
			System.out.println(String.format("[client] [%sbyte] upload %s ", file.length(), file.getPath()));
			try (FileInputStream fis = new FileInputStream(file)) {
				byte[] buffer = new byte[1024];
				int len;
				while ((len = fis.read(buffer)) > 0) {
					dos.write(buffer, 0, len);
				}
			}
			dos.flush();
		}
	}

}
