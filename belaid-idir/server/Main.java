package server;

import common.ProtocolParameters;
import java.io.IOException;
import java.net.ServerSocket;

class Main {
    public static void main(String[] args) {
        ServerSocket server = null;

        try {
            server = new ServerSocket(ProtocolParameters.SERVER_PORT);
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(1);
        }

        System.out.println("Server started on port " + server.getLocalPort());
        while (true) {
            try {
                (new ServerThread(server.accept())).start();
            } catch (IOException e) {
                System.err.println(e);
            }
        }
    }
}
