package server;

import common.ProtocolParameters;
import java.io.IOException;
import java.net.ServerSocket;

class Main {
    public static void main(String[] args) {
        ServerSocket server = null;
        int backlog = 100;

        //AFFICHER DE L'AIDE POUR LANCER LE SERVEUR//
        if (args.length > 0) {
            switch (args[0]) {
            case "-help":
                System.out.println(
                    "Usage: java -cp classes/ server.Main [<backlog>]"
                );
                System.exit(0);
                break;
            default:
                try {
                    backlog = Integer.parseInt(args[0]);
                } catch (NumberFormatException e) {}
            }
        }

        try {
            server = new ServerSocket(ProtocolParameters.SERVER_PORT, backlog);
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(1);
        }

        System.out.println("Server started on port " + server.getLocalPort());
        while (true) {
            try {
                //UN THREAD POUR CHAQUE CONNEXION//
                (new ServerThread(server.accept())).start();
            } catch (IOException e) {
                System.err.println(e);
            }
        }
    }
}
