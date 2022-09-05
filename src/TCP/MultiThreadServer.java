package TCP;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class MultiThreadServer {

    private ServerSocket connectionSocket;
    private Socket dataSocket;

    public MultiThreadServer(int port) {
        try {
            connectionSocket = new ServerSocket(port);
            System.out.println("SERVER: Sono online e in attesa di connessioni");
        } catch (IOException e) {
            System.out.println(e);
        }

    }


    public void listen() {
        try {
            while (true) {
                dataSocket = connectionSocket.accept();
                ServerThread s = new ServerThread(dataSocket);
                s.start();
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


    public static void main(String[] args) {
        MultiThreadServer server = new MultiThreadServer(14302);
        server.listen();
    }

}
