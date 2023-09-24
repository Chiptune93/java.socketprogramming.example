import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;

public class AsyncClientWithNIO {
    public static void main(String[] args) throws IOException {
        // 비동기 클라이언트 소켓 채널을 생성합니다.
        final AsynchronousSocketChannel clientSocketChannel = AsynchronousSocketChannel.open();
        InetSocketAddress serverAddress = new InetSocketAddress("localhost", 13099); // 서버의 호스트 및 포트 정보

        // clientSocketChannel을 사용하여 서버에 비동기적으로 연결하고 연결 완료 또는 실패 시에 호출될 CompletionHandler를 등록합니다.
        clientSocketChannel.connect(serverAddress, null, new CompletionHandler<Void, Void>() {
            @Override
            // 서버 연결이 완료되면 호출됩니다.
            public void completed(Void result, Void attachment) {
                // 서버에 연결이 완료되면 이 곳에서 메시지를 전송할 수 있습니다.
                String message = "Hello, Server!"; // 서버에 전송할 메시지를 준비합니다.
                // 메시지를 ByteBuffer 형식으로 변환하여 소켓 채널을 통해 서버에 보낼 준비를 합니다.
                ByteBuffer buffer = ByteBuffer.wrap(message.getBytes());

                // 버퍼의 데이터를 서버로 비동기적으로 전송하고, 전송 완료 또는 실패 시에 호출될 CompletionHandler를 등록합니다.
                clientSocketChannel.write(buffer, null, new CompletionHandler<Integer, Void>() {
                    @Override
                    public void completed(Integer bytesWritten, Void attachment) {
                        // 버로 메시지가 전송되었음을 콘솔에 출력합니다.
                        System.out.println("Message sent to server: " + message);

                        // 서버로부터 응답을 받을 수도 있습니다.
                        // 서버에서 받을 응답을 저장할 버퍼를 생성합니다.
                        ByteBuffer responseBuffer = ByteBuffer.allocate(1024);
                        // 서버로부터 응답 데이터를 비동기적으로 읽고, 읽기 완료 또는 실패 시에 호출될 CompletionHandler를 등록합니다.
                        clientSocketChannel.read(responseBuffer, null, new CompletionHandler<Integer, Void>() {
                            @Override
                            // 메서드 (내부): 서버에서 데이터를 읽을 때 호출됩니다.
                            public void completed(Integer bytesRead, Void attachment) {
                                // 버퍼의 위치와 제한을 조정하여 데이터를 읽을 준비를 합니다.
                                responseBuffer.flip();
                                // 읽은 데이터를 저장할 바이트 배열을 생성합니다.
                                byte[] responseData = new byte[responseBuffer.limit()];
                                // 버퍼에서 데이터를 읽어와 responseData 배열에 저장합니다.
                                responseBuffer.get(responseData);
                                // 바이트 배열을 문자열로 변환합니다.
                                String response = new String(responseData);
                                System.out.println("Response from server: " + response);
                            }

                            @Override
                            // 메서드 (내부): 연결 또는 데이터 전송 작업이 실패한 경우 호출됩니다. 예외를 출력합니다.
                            public void failed(Throwable exc, Void attachment) {
                                exc.printStackTrace();
                            }
                        });
                    }

                    @Override
                    // 메서드 (바깥): 연결 작업이 실패한 경우 호출됩니다. 예외를 출력합니다.
                    public void failed(Throwable exc, Void attachment) {
                        exc.printStackTrace();
                    }
                });
            }

            @Override
            public void failed(Throwable exc, Void attachment) {
                exc.printStackTrace();
            }
        });

        try {
            // 블록: 메인 스레드가 종료되지 않도록 Thread.sleep(Integer.MAX_VALUE);을 사용하여 무한 대기합니다.
            // 이 부분은 클라이언트가 서버와 통신을 유지하기 위한 임시적인 방법입니다.
            Thread.sleep(Integer.MAX_VALUE);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
