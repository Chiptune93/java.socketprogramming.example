package com.example.nettyclient;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

@SpringBootApplication
public class NettyClientApplication {
	public static void main(String[] args) {
		SpringApplication.run(NettyClientApplication.class, args);
		startNettyClient();
	}

	private static void startNettyClient() {
		EventLoopGroup group = new NioEventLoopGroup();

		try {
			Bootstrap bootstrap = new Bootstrap();
			bootstrap.group(group)
					.channel(NioSocketChannel.class) // 소켓 채널을 사용
					.handler(new ChannelInitializer<SocketChannel>() {
						@Override
						protected void initChannel(SocketChannel ch) {
							ChannelPipeline pipeline = ch.pipeline();
							pipeline.addLast(new StringEncoder()); // 문자열 인코더
							pipeline.addLast(new StringDecoder()); // 문자열 디코더
							pipeline.addLast(new ClientHandler()); // 클라이언트 핸들러
						}
					});

			// 서버에 연결
			Channel channel = bootstrap.connect("localhost", 8888).sync().channel();

			BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
			while (true) {
				try {
					String message = reader.readLine();
					if (message != null) {
						channel.writeAndFlush(message); // 서버로 메시지 전송
					}
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		} catch (InterruptedException e) {
			e.printStackTrace();
		} finally {
			// 이벤트 루프 그룹 종료
			group.shutdownGracefully();
		}
	}

	// 클라이언트 핸들러 클래스
	private static class ClientHandler extends SimpleChannelInboundHandler<String> {
		@Override
		protected void channelRead0(ChannelHandlerContext ctx, String msg) {
			// 서버로부터 받은 응답 처리
			System.out.println("Response from server: " + msg);
		}
	}
}
