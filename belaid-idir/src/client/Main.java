package client;

import common.*;
import java.io.*;
import java.net.Socket;
import java.util.*;

class Main {
    public static void main(String[] args) {
        if (args.length == 0) {
            System.err.println("Please specify the server address.");
            System.exit(1);
        }

        try (
            Socket socket = new Socket(args[0], ProtocolParameters.SERVER_PORT);
            BufferedReader in = new BufferedReader(
                new InputStreamReader(socket.getInputStream())
            );
            BufferedWriter out = new BufferedWriter(
                new OutputStreamWriter(socket.getOutputStream())
            );
        ) {
            Logger.info("Establish connection with the server");
            CLI.init(in, out);

            while (!CLI.quit()) {
                final String rawCmdLine;
                final CLI.Command cmd;
                final Response response;
                final List<String> argv = new LinkedList<String>();

                rawCmdLine = CLI.readLine(">");
                if (rawCmdLine == null) { /* The user type Ctrl-d.  */
                    cmd = CLI.Command.BYE;
                } else {
                    final String sep = "\\p{Space}+";
                    final String tokens[] = rawCmdLine.trim().split(sep, 2);

                    try {
                        cmd = Enum.valueOf(CLI.Command.class,
                                           tokens[0].toUpperCase());
                    } catch (IllegalArgumentException e) {
                        if (!rawCmdLine.isEmpty()) {
                            CLI.perrorWithHint("No such command");
                        }
                        continue;
                    }
                    if (tokens.length == 2) {
                        for (String arg: tokens[1].split(sep)) {
                            argv.add(arg);
                        }
                    }
                }
                try {
                    CLI.process(cmd, argv);
                } catch (InvalidResponseException | IOException e) {
                    CLI.perror(e.getMessage());
                } catch (InvalidCLIArgumentsException e) {
                    CLI.perrorWithHint(e.getMessage());
                }
            }
        } catch (IOException e) {
            System.err.println(e);
            System.exit(1);
        }
    }
}
