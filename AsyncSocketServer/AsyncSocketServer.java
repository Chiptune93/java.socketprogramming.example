import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

public class AsyncSocketServer {
    public static void main(String[] args) {
        // 소켓 생성
        try {
            ServerSocket serverSocket = new ServerSocket(18501);

            while (true) {
                // 포트 오픈 및 리스닝
                Socket socket = serverSocket.accept();

                // 클라이언트 연결
                InputStream inputStream = socket.getInputStream();
                byte[] bytes = new byte[1024];
                System.out.println("bytes : " + new String(bytes, StandardCharsets.UTF_8));
                // 메세지 처리
                int data = inputStream.read(bytes);
                System.out.println("bytes : " + new String(bytes, StandardCharsets.UTF_8));

                if (data > 0) {
                    sendData(bytes, socket);
                }

                // 연결 종료 및 예외 처리
                inputStream.close();

                // 서버 종료 조건 및 처리

            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static void sendData(byte[] bytes, Socket socket) {
        try {
            OutputStream os = socket.getOutputStream();
            os.write(bytes);
            os.flush();
        } catch (Exception e1) {
            e1.printStackTrace();
        }
    }
}
