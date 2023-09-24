import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

public class AsyncSocketClient {
    public static void main(String[] args) {
        Socket socket = new Socket();
        //서버쪽 주소 생성. ip는 localhost, 포트는 18501. 필요에 맞게 바꾸기
        SocketAddress address = new InetSocketAddress("127.0.0.1", 18501);
        //주소에 해당하는 서버랑 연결
        try {
            socket.connect(address);

            // 데이터 보내기
            String data = "data";
            byte[] bytes = data.getBytes();

            OutputStream outputStream = socket.getOutputStream();
            outputStream.write(bytes);
            outputStream.flush();

            InputStream is = socket.getInputStream();
            byte[] receiveData = new byte[1024];
            int receiveSize = is.read(receiveData);

            if (receiveSize > 0) {
                //받아온 byte를 Object로 변환
                String receiveDataString = new String(receiveData, StandardCharsets.UTF_8);
                //확인을 위해 출력
                System.out.println("receive Data : " + receiveDataString);
            }


        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
