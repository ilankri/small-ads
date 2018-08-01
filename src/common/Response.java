package common;

import java.util.*;

/* Class representing server responses.  */
public class Response {

    /*
     * The different errors issued by the server.  See the protocol
     * specification for more information.
     */
    public static enum Error {
        ALREADY_USED_USERNAME(1) {
            @Override
            public String toString() {
                return "Already used username";
            }
        },

        ILL_FORMED_REQUEST(3) {
            @Override
            public String toString() {
                return "Ill-formed request";
            }
        },

        NOT_SIGNED_IN(4) {
            @Override
            public String toString() {
                return "Please sign in before";
            }
        };

        private static final Map<Integer, Error> map =
            new Hashtable<Integer, Error>();

        static {
            for (Error err: Error.values()) {
                map.put(err.code, err);
            }
        }

        /* The error code.  */
        private final int code;

        private Error(int code) {
            this.code = code;
        }

        /*
         * Return the error associated to the given code.  Return null
         * when there is no error associated with the given code.
         */
        private static Error valueOf(int code) {
            return map.get(code);
        }
    }

    /*
     * Either a request was successfully processed (OK) or else its
     * processing failed (KO).
     */
    private enum Status { OK, KO }

    /*
     * The status code to signal if the processing of the request
     * succeed or not.
     */
    private final Status status;

    /* The error/success code.  */
    private final int code;

    /*
     * The payload of the response.  It is non-empty only for responses
     * to GET requests.
     */
    private final String payload;

    /****************/
    /* Constructors */
    /****************/

    /* A response to signal success.  */
    public Response(String payload) {
        this.status = Status.OK;
        this.code = payload.isEmpty() ? 0 : 1;
        this.payload = payload;
    }

    /* A response to signal failure.  */
    public Response(Error err) {
        this.status = Status.KO;
        this.code = err.code;
        this.payload = "";
    }

    private Response(Status status, int code, String payload) {
        this.status = status;
        this.code = code;
        this.payload = payload;
    }

    /***********/
    /* Getters */
    /***********/
    public String getPayload() {
        return payload;
    }

    public Error getError() {
        return Error.valueOf(code);
    }

    /*******************/
    /* Response parser */
    /*******************/
    public static Response valueOf(String response)
        throws InvalidResponseException {
        final String[] tokens = response.split(ProtocolParameters.FIELD_SEP, 3);
        final Status status;
        final int code;
        String payload = "";

        try {
            if (tokens.length < 2) {
                throw new InvalidResponseException();
            }
            status = Enum.valueOf(Status.class, tokens[0]);
            code = Integer.parseInt(tokens[1]);
            if (status == Status.OK && code == 1) {
                if (tokens.length == 2) {
                    throw new InvalidResponseException();
                }
                payload = tokens[2];
            }
            return new Response(status, code, payload);
        } catch (IllegalArgumentException e) {
            throw new InvalidResponseException();
        }
    }

    /***************/
    /* Testers     */
    /***************/

    /* Test whether it is a reponse to signal a success.  */
    public boolean ok() {
        return status == Status.OK;
    }

    /* Test whether the reponse has a payload.  */
    public boolean withPayload() {
        return ok() && code == 1;
    }

    /******************************/
    /* Serialization of responses */
    /******************************/
    @Override
    public String toString() {
        return status + ProtocolParameters.FIELD_SEP + code +
            (ok() ? ProtocolParameters.FIELD_SEP : "") + payload;
    }
}
