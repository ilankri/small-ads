package common;

import java.util.*;

public class Response {
    public static enum Error {
        ALREADY_USED_USERNAME(1) {
            @Override
            public String toString() {
                return "Already used username";
            }
        },

        INVALID_CREDENTIALS(2),

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
        },

        OPERATION_NOT_PERMITTED(5) {
            @Override
            public String toString() {
                return "Operation not permitted";
            }
        },

        ALREADY_SIGNED_IN(6) {
            @Override
            public String toString() {
                return "You are already signed in";
            }
        };

        private static final Map<Integer, Error> map =
            new Hashtable<Integer, Error>();

        static {
            for (Error err: Error.values()) {
                map.put(err.code, err);
            }
        }

        private final int code;

        private Error(int code) {
            this.code = code;
        }

        private static Error valueOf(int code) {
            return map.get(code);
        }
    }

    private enum Status { OK, KO }

    private final Status status;
    private final int code;
    private final String payload;

    public Response(String payload) {
        this.status = Status.OK;
        this.code = payload.isEmpty() ? 0 : 1;
        this.payload = payload;
    }

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

    public boolean ok() {
        return status == Status.OK;
    }

    public String getPayload() {
        return payload;
    }

    public boolean withPayload() {
        return ok() && code == 1;
    }

    public Error getError() {
        return Error.valueOf(code);
    }

    @Override
    public String toString() {
        return status + ProtocolParameters.FIELD_SEP + code +
            (withPayload() ? (ProtocolParameters.FIELD_SEP + payload) : "");
    }
}
