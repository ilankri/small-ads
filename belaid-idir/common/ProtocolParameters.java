package common;

public class ProtocolParameters {
    public static final int SERVER_PORT = 1027;
    public static final int CLIENT_PORT = 9876;
    public static final String FIELD_SEP = "::";
    public static final String AD_SEP = "##";

    /*
     * The maximal size in bytes of messages exchanged between clients.
     */
    public static final int MSG_MAX_SIZE = 4096;

    private ProtocolParameters() {}
}
