/**
 * 
 */
package com.smartcity.core;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;

import java.util.Properties;

/**
 * @author Jacob
 * 
 */
public class FileServerBootstrap {

	private static final String CONFIG_FILE = "config.properties";
	
	private static final String SERVER_PORT = "server.port";

	public static void main(String[] args) throws Exception {
		Properties properties = new Properties();
		properties.load(FileServerBootstrap.class.getClassLoader()
				.getResourceAsStream(CONFIG_FILE));

		EventLoopGroup bossGroup = new NioEventLoopGroup();
		EventLoopGroup workerGroup = new NioEventLoopGroup();
		try {
			ServerBootstrap bootstrap = new ServerBootstrap();
			bootstrap.group(bossGroup, workerGroup)
					.channel(NioServerSocketChannel.class)
					.childHandler(new FileServerInitializer(properties));
			bootstrap
					.bind(Integer.valueOf(properties.getProperty(SERVER_PORT)))
					.sync().channel().closeFuture().sync();
		} finally {
			bossGroup.shutdownGracefully();
			workerGroup.shutdownGracefully();
		}
	}
}
