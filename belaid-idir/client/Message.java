package client;

import java.net.InetAddress;

class Message {
    private final InetAddress authorAddress;
    private final String contents;

    Message(InetAddress authorAddress, String contents) {
        this.authorAddress = authorAddress;
        this.contents = contents;
    }

    InetAddress getAuthorAddress() {
        return authorAddress;
    }

    String getContents() {
        return contents;
    }
}
