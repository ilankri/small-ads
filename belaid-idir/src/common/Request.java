package common;

import java.util.*;

/* Class representing requests sent by the client to the server.  */
public class Request {

    /*
     * A request handler must specifiy a method to process each
     * available command specified by the protocol.
     */
    public static interface Handler {
        Response signin(String name);
        Response signout();
        Response get();
        Response post(String title, String body);
        Response deletep(long adId);
    }

    /*
     * Available request commands.  See the protocol specificatio for
     * details.
     */
    public static enum Command {
        SIGNIN(1) {
            @Override
            public Response process(Handler handler, List<String> args) {
                return handler.signin(args.get(0));
            }
        },

        SIGNOUT(1) {
            @Override
            public Response process(Handler handler, List<String> args) {
                return handler.signout();
            }
        },

        GET(0) {
            @Override
            public Response process(Handler handler, List<String> args) {
                return handler.get();
            }
        },

        POST(2) {
            @Override
            public Response process(Handler handler, List<String> args) {
                return handler.post(args.get(0), args.get(1));
            }
        },

        DELETEP(2) {
            @Override
            public Response process(Handler handler, List<String> args) {
                final long adId;

                try {
                    adId = Long.parseLong(args.get(0));
                    if (adId < 0) {
                        return new Response(Response.Error.ILL_FORMED_REQUEST);
                    }
                    return handler.deletep(adId);
                } catch (NumberFormatException e) {
                    return new Response(Response.Error.ILL_FORMED_REQUEST);
                }
            }
        };

        /* The number of arguments needed by the command.  */
        private final int arity;

        private Command(int arity) {
            this.arity = arity;
        }

        /*
         * Call the handler to process the command with the given
         * arguments.
         */
        public abstract Response process(Handler handler, List<String> args);

        /* Return the number of arguments expected by a command.  */
        public int getArity() {
            return arity;
        }
    }

    /* The command of the request.  */
    private final Command cmd;

    /* The arguments of the command.  */
    private final List<String> args;

    /***************/
    /* Constructor */
    /***************/
    public Request(Command cmd, List<String> args) {
        this.cmd = cmd;
        this.args = args;
    }


    /***********/
    /* Getters */
    /***********/
    public Command getCmd() {
        return cmd;
    }

    public List<String> getArgs() {
        return Collections.unmodifiableList(args);
    }

    /******************/
    /* Request parser */
    /******************/
    public static Request valueOf(String request)
        throws InvalidRequestException {
        final String[] tokens = request.split(ProtocolParameters.FIELD_SEP, 2);
        final Command cmd;
        final List<String> args;

        try {
            if (tokens.length < 1) {
                throw new InvalidRequestException();
            }
            cmd = Enum.valueOf(Command.class, tokens[0]);
            if (tokens.length == 1) {
                if (cmd.getArity() != 0) {
                    throw new InvalidRequestException();
                }
                args = new LinkedList<String>();
            } else {
                args = Arrays.asList(tokens[1]
                                     .split(ProtocolParameters.FIELD_SEP));
                if (args.size() != cmd.getArity()) {
                    throw new InvalidRequestException();
                }
                for (String arg: args) {
                    if (arg.contains(ProtocolParameters.AD_SEP)) {
                        throw new InvalidRequestException();
                    }
                }
            }
            return new Request(cmd, args);
        } catch (IllegalArgumentException e) {
            throw new InvalidRequestException();
        }

    }

    /*****************************/
    /* Serialization of requests */
    /*****************************/
    @Override
    public String toString() {
        final String rest = String.join(ProtocolParameters.FIELD_SEP, args);

        return cmd +
            (rest.isEmpty() ? "" : ProtocolParameters.FIELD_SEP + rest);
    }
}
