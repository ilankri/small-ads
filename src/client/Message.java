package client;

import java.net.InetAddress;

class Message {
    private final InetAddress authorAddress;
    private final String contents;
    //CRÉER UN MESSAGE//
    Message(InetAddress authorAddress, String contents) {
        this.authorAddress = authorAddress;
        this.contents = contents;
    }
    //RETOURNER L'AUTEUR DU MESSAGE//
    InetAddress getAuthorAddress() {
        return authorAddress;
    }
    //RETOURNER LE CONTENU DU MESSAGE//
    String getContents() {
        return contents;
    }
}
