package client;

import common.*;
import java.io.*;
import java.net.*;
import java.util.*;

class CLI {
    private static final Console CONSOLE = System.console();
    private static BufferedReader in;
    private static BufferedWriter out;
    private static boolean quit = false;
    private static boolean signedOut = false;
    private static String username;

    private CLI() {}
    //INITIALISATION DES BUFFERS DE COMMUNICATION//
    static void init(BufferedReader in, BufferedWriter out)
        throws SocketException {
        CLI.in = in;
        CLI.out = out;
        Mailbox.open();
    }
    //
    static boolean quit() {
        return quit;
    }
    //LIRE UNE LIGNE SUR L'ENTRÉE CONSOLE//
    static String readLine(String prompt) {
        return CONSOLE.readLine("%s ", prompt);
    }
    //ECRIRE SUR LA SORTIE CONSOLE//
    private static void print(String s) {
        CONSOLE.printf(s);
    }
    //AFFICHER UN MESSAGE 's'//
    private static void println(String s) {
        print(s + "\n");
    }
    //AFFICHER UNE ERREUR//
    static void perror(String msg) {
        println("Error: " + msg);
    }
    //AFFICHER UNE RÉFÉRENCE D'AIDE//
    public static void hint() {
        println("Hint: Type 'help' for help");
    }
    //AFFICHER L'ERREUR ET UNE RÉFÉRENCE D'AIDE//
    static void perrorWithHint(String msg) {
        perror(msg);
        hint();
    }
    //ENVOYER LA REQUÊTE SI PAS D'ERREUR//
    private static Response sendRequest(Request request)
        throws IOException, InvalidResponseException,
               NoServerConnectionException {
        final String response;
        final String rawRequest = request.toString();

        if (signedOut) {
            throw new NoServerConnectionException();
        }
        out.write(rawRequest);
        out.newLine();
        out.flush();
        Logger.info("Send request '" + rawRequest + "'");
        response = in.readLine();
        if (response == null) {
            signedOut = true;
            in.close();
            out.close();
            throw new NoServerConnectionException();
        }
        Logger.info("Receive response '" + response + "'");
        return Response.valueOf(response);
    }
    //AFFICHER LA RÉPONSE DU SERVER OU L'ERREUR EN CAS D'ERREUR//
    private static void reportResponse(Response response)
        throws InvalidResponseException {
        if (!response.ok()) {
            Response.Error err = response.getError();

            if (err == null) {
                throw new InvalidResponseException();
            }
            perror(response.getError().toString());
        } else {
            try {
                if (response.withPayload()) {
                    printAds(Ad.valuesOf(response.getPayload()));
                }
            } catch (InvalidAdException e) {
                throw new InvalidResponseException();
            }
        }
    }

    private static void print(Collection<String> strs) {
        final char[] sep = new char[72];

        Arrays.fill(sep, '=');
        print(String.join(new String(sep) + "\n", strs));
    }
    //AFFICHER LES ANNONCES (LEUR ID, LEUR AUTEUR ET LEUR TITRE)//
    private static void printAds(Collection<Ad> ads) {
        final Collection<String> adStrs = new LinkedList<String>();

        for (Ad ad: ads) {
            adStrs.add(String.format("Id: %d\n" +
                                     "Author: %s\n" +
                                     "Title: %s\n\n"+
                                     "%s\n\n",
                                     ad.getId(), ad.getAuthor(),
                                     ad.getTitle(), ad.getBody()));
        }
        print(adStrs);
    }
    //AFFICHER TOUS LES MESSAGES DE LA COMMUNICATION AVEC LES AUTRES CLIENTS//
    private static void printInbox() {
        final Collection<String> msgs = new LinkedList<String>();

        for (Message msg: Mailbox.inbox()) {
            msgs.add(String.format("Author: %s\n" +
                                   "Message: %s\n\n",
                                   msg.getAuthorAddress(),
                                   msg.getContents()));
        }
        print(msgs);
    }
    //GÉRER LES REQUÊTES//
    static void process(Command cmd, List<String> args)
        throws IOException, InvalidResponseException,
               InvalidCLIArgumentsException, NoServerConnectionException {
        final Request request;
        final Response response;

        if (cmd.getArity() != args.size()) {
            throw new InvalidCLIArgumentsException();
        }
        request = cmd.prepareRequest(args);
        if (request != null) {
            response = sendRequest(request);
            reportResponse(response);
        }
        quit = cmd.onSuccess(args);
    }
    //LES COMMANDES POSSIBLES//
    static enum Command {
        SIGNIN(1) {
            @Override
            Request prepareRequest(List<String> args) {
                final List<String> allArgs = new LinkedList<String>(args);

                username = allArgs.get(0);
                return new Request(Request.Command.SIGNIN, allArgs);
            }

            @Override
            boolean onSuccess(List<String> args) {
                return false;
            }
        },

        SIGNOUT(0) {
            @Override
            Request prepareRequest(List<String> args) {
                final List<String> allArgs = new LinkedList<String>(args);

                allArgs.add(username);
                return new Request(Request.Command.SIGNOUT, allArgs);
            }

            @Override
            boolean onSuccess(List<String> args) {
                return false;
            }
        },

        ADS(0) {
            @Override
            Request prepareRequest(List<String> args) {
                return new Request(Request.Command.GET,
                                   new LinkedList<String>(args));
            }

            @Override
            boolean onSuccess(List<String> args) {
                return false;
            }
        },

        POST(0) {
            @Override
            Request prepareRequest(List<String> args) {
                final List<String> allArgs = new LinkedList<String>(args);

                allArgs.add(readLine("Title:"));
                allArgs.add(readLine("Body:"));
                return new Request(Request.Command.POST, allArgs);
            }

            @Override
            boolean onSuccess(List<String> args) {
                return false;
            }
        },

        DELAD(1) {
            @Override
            Request prepareRequest(List<String> args)
                throws InvalidCLIArgumentsException {
                final List<String> allArgs = new LinkedList<String>(args);

                try {
                    Long.parseLong(args.get(0));
                } catch (NumberFormatException e) {
                    throw new InvalidCLIArgumentsException();
                }
                allArgs.add(username);
                return new Request(Request.Command.DELETEP, allArgs);
            }

            @Override
            boolean onSuccess(List<String> args) {
                return false;
            }
        },

        BYE(0) {
            @Override
            Request prepareRequest(List<String> args) {
                return null;
            }

            @Override
            boolean onSuccess(List<String> args) {
                Mailbox.close();
                return true;
            }
        },

        HELP(0) {
            @Override
            Request prepareRequest(List<String> args) {
                return null;
            }

            @Override
            boolean onSuccess(List<String> args) {
                println("Available commands:\n\n" +
                       "signin <username> Sign in as <username>\n" +
                       "signout           Sign out\n" +
                       "ads               List all ads on the server\n" +
                       "post              Post an ad\n" +
                       "delad <ad-id>     Delete the ad whose id is <ad-id>\n" +
                       "contact <ip-addr> Send a message to <ip-addr>\n" +
                       "inbox             List incoming messages\n" +
                       "bye               Quit the program\n" +
                       "help              Display this help");
                return false;
            }
        },

        CONTACT(1) {
            @Override
            Request prepareRequest(List<String> args) {
                return null;
            }

            @Override
            boolean onSuccess(List<String> args) {
                try {
                    Mailbox.send(InetAddress.getByName(args.get(0)),
                                 readLine("Message:"));
                } catch (IOException e) {
                    perror(e.getMessage());
                }
                return false;
            }
        },

        INBOX(0) {
            @Override
            Request prepareRequest(List<String> args) {
                return null;
            }

            @Override
            boolean onSuccess(List<String> args) {
                printInbox();
                return false;
            }
        };

        private final int arity;
        //INITIALISATION DE L'ARITÉ DE LA COMMANDE (NBR ARGUMENTS)//
        private Command(int arity) {
            this.arity = arity;
        }
        //RETOURNER L'ARITÉ DE LA COMMANDE//
        private int getArity() {
            return arity;
        }

        abstract Request prepareRequest(List<String> args)
            throws InvalidCLIArgumentsException;
        abstract boolean onSuccess(List<String> args);
    }
}
