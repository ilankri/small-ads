package client;

import common.ProtocolParameters;
import java.io.*;
import java.net.*;
import java.util.*;

class Mailbox {
    private static class Inbox extends Thread {
        private final Collection<Message> contents = new LinkedList<Message>();
        //RECUPÉRER LES MESSAGES REÇUS//
        Collection<Message> getContents() {
            synchronized (contents) {
                return Collections.unmodifiableCollection(contents);
            }
        }

        @Override
        public void run() {
            while (true) {
                final byte[] buf = new byte[ProtocolParameters.MSG_MAX_SIZE];
                final DatagramPacket packet =
                    new DatagramPacket(buf, buf.length);
                final Message msg;

                try {
                    final String msgContents;
                    final InetAddress addr;

                    socket.receive(packet);
                    msgContents = new String(packet.getData(), 0,
                                             packet.getLength(),
                                             ProtocolParameters.CHARSET);
                    addr = packet.getAddress();
                    Logger.info("Receive message '" + msgContents +
                                "' from " + addr);
                    msg = new Message(addr, msgContents);
                    synchronized (contents) {
                        contents.add(msg);
                    }
                } catch (SocketException e) {
                    break;
                } catch (IOException e) {
                    continue;
                }
            }
        }
    }

    private static DatagramSocket socket;
    private static Inbox inbox;

    private Mailbox() {}
    //OUVRIR UNE CONNEXION CLIENT CLIENT//
    static void open() throws SocketException {
        socket = new DatagramSocket(ProtocolParameters.CLIENT_PORT);
        inbox = new Inbox();
        inbox.start();
    }
    //FERMER LA CONNEXION//
    static void close() {
        socket.close();
    }
    //ENVOYER UN MESSAGE 'msg' POUR LE CLIENT DE L'ADRESSE 'addr'//
    static void send(InetAddress addr, String msg) throws IOException {
        final byte[] rawMsg = msg.getBytes(ProtocolParameters.CHARSET);

        socket.send(new DatagramPacket(rawMsg, rawMsg.length, addr,
                                       ProtocolParameters.CLIENT_PORT));
        Logger.info("Send message '" + msg + "' to " + addr);
    }

    static Collection<Message> inbox() {
        return inbox.getContents();
    }

}
