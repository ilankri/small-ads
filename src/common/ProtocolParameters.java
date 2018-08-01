package common;

import java.nio.charset.Charset;

/* Miscellaneous parameters of the protocol */
public class ProtocolParameters {
    /*
     * The port on which the server is waiting for incoming
     * connections.
     */
    public static final int SERVER_PORT = 1027;

    /* The port on which the clients communicate together.  */
    public static final int CLIENT_PORT = 9876;

    /* Separator of field for server responses and client requests.  */
    public static final String FIELD_SEP = "::";

    /* Separator used to separate ads in the server responses.  */
    public static final String AD_SEP = "##";

    /*
     * Character encoding used for client-server and client-client
     * messages.
     */
    public static final Charset CHARSET = Charset.forName("UTF-8");

    /*
     * The maximal size in bytes of messages exchanged between clients.
     */
    public static final int MSG_MAX_SIZE = 4096;

    private ProtocolParameters() {}
}
