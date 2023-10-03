package socketprogramming.example.nettyserver;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class NettyServerApplication {
	public static void main(String[] args) {
		SpringApplication.run(NettyServerApplication.class, args);
		startNettyServer();
	}

	private static void startNettyServer() {
		// 이벤트 루프 그룹을 생성 (bossGroup와 workerGroup)
		EventLoopGroup bossGroup = new NioEventLoopGroup();
		EventLoopGroup workerGroup = new NioEventLoopGroup();

		try {
			ServerBootstrap bootstrap = new ServerBootstrap();
			bootstrap.group(bossGroup, workerGroup)
					.channel(NioServerSocketChannel.class) // 서버 소켓 채널을 사용
					.childHandler(new ChannelInitializer<SocketChannel>() {
						@Override
						protected void initChannel(SocketChannel ch) {
							ChannelPipeline pipeline = ch.pipeline();
							pipeline.addLast(new StringEncoder()); // 문자열 인코더
							pipeline.addLast(new StringDecoder()); // 문자열 디코더
							pipeline.addLast(new ServerHandler()); // 서버 핸들러
						}
					});

			// 서버 바인딩 및 대기
			ChannelFuture future = bootstrap.bind(8888).sync();
			future.channel().closeFuture().sync();
		} catch (InterruptedException e) {
			e.printStackTrace();
		} finally {
			// 이벤트 루프 그룹 종료
			bossGroup.shutdownGracefully();
			workerGroup.shutdownGracefully();
		}
	}

	// 서버 핸들러 클래스
	private static class ServerHandler extends SimpleChannelInboundHandler<String> {
		@Override
		protected void channelRead0(ChannelHandlerContext ctx, String msg) {
			// 클라이언트로부터 받은 메시지 처리
			System.out.println("Received from client: " + msg);
			ctx.writeAndFlush("Server Response: " + msg); // 클라이언트에 응답 전송
		}
	}
}
