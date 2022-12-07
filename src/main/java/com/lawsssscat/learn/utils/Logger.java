package com.lawsssscat.learn.utils;

import java.text.SimpleDateFormat;
import java.util.Date;

public class Logger {

	public static Logger get(Class<?> clazz) {
		return new Logger(clazz);
	}

	private Class<?> clazz;

	public Logger(Class<?> clazz) {
		this.clazz = clazz;
	}

	/**
	 *
	 * 打印日志信息
	 *
	 * @param msg    信息表达式，可以用String.format的占位符转换
	 * @param values 占位符转换值
	 */
	public void info(String msg, Object... values) {
		msg = String.format(msg, values);
		StringBuilder sb = new StringBuilder();
		sb.append("[").append(new SimpleDateFormat("YYYY-MM-dd HH:mm:ss:SSS").format(new Date())).append("] ");
		sb.append("[").append(Thread.currentThread().getName()).append("] ");
		// 行号
		// sb.append("[").append(clazz).append("] ");
		StackTraceElement stackTraceElement = Thread.currentThread().getStackTrace()[2];
		sb.append("(").append(clazz.getSimpleName()).append(".java:").append(stackTraceElement.getLineNumber())
				.append(") ");
		sb.append(msg);
		System.out.println(sb.toString());
	}

}
