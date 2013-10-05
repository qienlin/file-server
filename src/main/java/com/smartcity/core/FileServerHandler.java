/**
 * 
 */
package com.smartcity.core;

import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.HttpVersion;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Properties;

/**
 * @author Jacob
 * 
 */
public class FileServerHandler extends
		SimpleChannelInboundHandler<FullHttpRequest> {

	private static final String FILE_ROOT = "file.root";

	private Properties properties;

	public FileServerHandler(Properties properties) {
		this.properties = properties;
	}

	@Override
	protected void channelRead0(ChannelHandlerContext ctx, FullHttpRequest msg)
			throws Exception {

		Channel ch = ctx.channel();
		String fileName = msg.getUri().substring(
				msg.getUri().lastIndexOf("/") + 1);
		File file = new File(properties.getProperty(FILE_ROOT) + fileName);
		if (!file.exists()) {
			System.out.println("File doesn't exist.");
			throw new IllegalArgumentException("Invalid file name");
		}
		System.out.println("File exists.");
		InputStream inputStream = new FileInputStream(file);
		byte[] bytes = new byte[inputStream.available()];
		inputStream.read(bytes);
		FullHttpResponse response = new DefaultFullHttpResponse(
				HttpVersion.HTTP_1_1, HttpResponseStatus.OK,
				Unpooled.wrappedBuffer(bytes));
		response.headers().set("content-type", "text/plain");
		response.headers().set("content-length",
				response.content().readableBytes());
		ch.write(response).addListener(ChannelFutureListener.CLOSE);
	}

	@Override
	public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
		ctx.flush();
	}

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause)
			throws Exception {
		cause.printStackTrace();
		ctx.close();
	}
}
