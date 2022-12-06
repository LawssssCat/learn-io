package com.lawsssscat.learn;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.net.Socket;
import java.net.UnknownHostException;

/**
 *
 * 客户端
 *
 * @author lawsssscat
 *
 */
public class BIOClient {

	public void submitSingleLine(String host, int port, String msg) throws UnknownHostException, IOException {
		Socket socket = new Socket(host, port);
		OutputStream os = socket.getOutputStream();
		PrintStream ps = new PrintStream(os);
		// ⚠️ 注意：
		// 因为服务器在等换行，所以这里也要传换行（即println）
		// 否则服务端报错java.net.SocketException: Connection reset
		ps.println(msg);
		ps.flush();
	}

}
