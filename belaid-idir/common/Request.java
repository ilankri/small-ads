package common;

import java.util.*;

public class Request {
    public static interface Handler {
        Response signin(String name);
        Response signout();
        Response get();
        Response post(String title, String body);
        Response bye();
        Response deletep(long adId);
    }

    public static enum Command {
        SIGNIN(2) {
            @Override
            public Response process(Handler handler, List<String> args) {
                return handler.signin(args.get(0));
            }
        },

        SIGNOUT(2) {
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
        },

        BYE(0) {
            @Override
            public Response process(Handler handler, List<String> args) {
                return handler.bye();
            }
        };

        private final int arity;

        private Command(int arity) {
            this.arity = arity;
        }

        public abstract Response process(Handler handler, List<String> args);

        public int getArity() {
            return arity;
        }
    }

    private final Command cmd;
    private final List<String> args;

    public Request(Command cmd, List<String> args) {
        this.cmd = cmd;
        this.args = args;
    }

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

    public Command getCmd() {
        return cmd;
    }

    public List<String> getArgs() {
        return Collections.unmodifiableList(args);
    }

    @Override
    public String toString() {
        final String rest = String.join(ProtocolParameters.FIELD_SEP, args);

        return cmd +
            (rest.isEmpty() ? "" : ProtocolParameters.FIELD_SEP + rest);
    }
}
