package drinkwater.helper;


import java.io.IOException;
import java.net.ServerSocket;

public class SocketUtils {

    public static int freePort() {
        try (ServerSocket socket = new ServerSocket(0)) {
            return socket.getLocalPort();
        } catch (IOException e) {
        }
        throw new RuntimeException("could not find freeport on this host");
    }
}
