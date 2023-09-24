import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousServerSocketChannel;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;

public class AsyncServerWithNIO {
    public static void main(String[] args) throws IOException {
        // 비동기 서버 소켓 채널을 생성하고 8080 포트에 바인딩합니다. 이 소켓 채널은 클라이언트 연결을 수락하기 위해 사용됩니다.
        final AsynchronousServerSocketChannel serverSocketChannel = AsynchronousServerSocketChannel.open().bind(new InetSocketAddress(13099));

        // 클라이언트의 연결을 비동기적으로 수락하고 요청을 처리하기 위한 CompletionHandler를 등록합니다.
        // accept 메서드는 클라이언트 연결을 수락하면 콜백 함수로 등록된 completed 메서드가 호출됩니다.
        serverSocketChannel.accept(null, new CompletionHandler<AsynchronousSocketChannel, Void>() {
            @Override
            public void completed(AsynchronousSocketChannel socketChannel, Void attachment) {
                serverSocketChannel.accept(null, this);
                handleRequest(socketChannel);
            }

            @Override
            public void failed(Throwable exc, Void attachment) {
                exc.printStackTrace();
            }
        });

        try {
            // 서버 프로세스가 종료되지 않도록 메인 스레드를 무한 대기시킵니다. 이 부분은 서버가 계속 실행되도록 유지하기 위한 임시적인 방법입니다.
            Thread.sleep(Integer.MAX_VALUE);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    // 클라이언트의 요청을 처리하기 위한 메서드를 정의합니다. 이 메서드는 socketChannel을 통해 클라이언트와 통신하고 요청을 처리합니다.
    private static void handleRequest(AsynchronousSocketChannel socketChannel) {
        // 1024 바이트 크기의 버퍼를 생성합니다. 이 버퍼는 데이터를 읽거나 쓸 때 사용됩니다.
        ByteBuffer buffer = ByteBuffer.allocate(1024);

        // socketChannel을 통해 비동기적으로 데이터를 읽고, 읽기 작업이 완료되면 completed 메서드가 호출됩니다.
        // 이 메서드에서 데이터를 처리하고 클라이언트에 응답을 보내는 코드를 구현할 수 있습니다.
        socketChannel.read(buffer, null, new CompletionHandler<Integer, Void>() {
            @Override
            public void completed(Integer bytesRead, Void attachment) {
                // 읽은 데이터의 바이트 수가 0 미만인 경우 클라이언트 연결을 종료합니다.
                if (bytesRead < 0) {
                    try {
                        socketChannel.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    return;
                }

                // 버퍼의 위치와 제한을 조정하여 데이터를 읽을 준비를 합니다.
                buffer.flip();
                // 읽은 데이터를 저장할 바이트 배열을 생성합니다.
                byte[] data = new byte[buffer.limit()];
                // 버퍼에서 데이터를 읽어와 data 배열에 저장합니다.
                buffer.get(data);
                // 바이트 배열을 문자열로 변환합니다.
                String message = new String(data);
                // 받은 메시지를 콘솔에 출력합니다.
                System.out.println("Received: " + message);

                // 여기서 클라이언트에 응답을 보낼 수 있습니다.
                // 응답을 보내는 방법은 비동기 방식으로 구현해야 합니다.
                // socketChannel.write(ByteBuffer.wrap("Response".getBytes()), null, new CompletionHandler<Integer, Void>() { ... });
            }

            // failed 메서드는 비동기 작업이 실패한 경우 호출되며, 예외를 출력합니다.
            @Override
            public void failed(Throwable exc, Void attachment) {
                exc.printStackTrace();
            }
        });
    }
}
