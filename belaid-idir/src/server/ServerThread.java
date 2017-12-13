package server;

import common.*;
import java.io.*;
import java.net.*;
import java.util.List;

class ServerThread extends Thread implements Request.Handler {
    private final Socket socket;
    private final InetAddress remoteAddr;
    private boolean isSignedIn;
    private boolean quit;

    ServerThread(Socket s) {
        socket = s;
        remoteAddr = s.getInetAddress();
        isSignedIn = false;
        quit = false;
    }
    //AJOUTER UN CLIENT QUI VIENT DE SE CONNECTER//
    public Response signin(String name) {
        try {
            DB.createUser(name, remoteAddr);
            isSignedIn = true;
            return new Response("");
        } catch (AlreadyUsedUsernameException e) {
            return new Response(Response.Error.ALREADY_USED_USERNAME);
        }
    }
    //SUPPRIMER UN CLIENT AINSI QUE TOUTES SES ANNONCES//
    private void deleteUser() {
        DB.deleteUser(remoteAddr);
        isSignedIn = false;
    }
    //SUPPRIMER LE CLIENT QUI DEMMANDE LA DÉCONNEXION//
    //ET LE SUPRIMMER DE LA LISTE DES CLIENTS CONNECTÉS//
    public Response signout() {
        if (!isSignedIn) {
            return new Response(Response.Error.NOT_SIGNED_IN);
        }
        deleteUser();
        quit = true;
        return new Response("");
    }
    //RENVOYER TOUTES LES ANNONCES DE TOUS LES CLIENTS//
    public Response get() {
        return new Response(Ad.toString(DB.readAllAds()));
    }
    //AJOUTER L'ANNONCE RÉCEMMENT CRÉÉ PAR LE CLIENT//
    public Response post(String title, String body) {
        if (!isSignedIn) {
            return new Response(Response.Error.NOT_SIGNED_IN);
        }
        DB.createAd(remoteAddr, title, body);
        return new Response("");
    }
    //SUPPRIMER UNE ANNONCE D'UN CLIENT//
    public Response deletep(long adId) {
        if (!isSignedIn) {
            return new Response(Response.Error.NOT_SIGNED_IN);
        }
        if (DB.deleteAd(remoteAddr, adId)) {
            return new Response("");
        } else {
            return new Response(Response.Error.NOT_SIGNED_IN);
        }
    }
    //PARSER LA REQUÊTE ET RETOURNER LA RÉPONSE SELON LA COMMANDE//
    private Response process(Request request) {
        final Request.Command cmd = request.getCmd();
        final List<String> args = request.getArgs();

        assert(cmd.getArity() == args.size());
        return cmd.process(this, args);
    }
    //ENVOYER LA RÉPONSE AU CLIENT//
    private void sendResponse(BufferedWriter out, Response response)
        throws IOException {
        out.write(response.toString());
        out.newLine();
        out.flush();
    }

    @Override
    public void run() {
        System.out.println("Connect " + remoteAddr);
        //INITIALISATION DES BUFFERS//
        try (
            BufferedReader in = new BufferedReader(
                new InputStreamReader(socket.getInputStream())
            );
            BufferedWriter out = new BufferedWriter(
                new OutputStreamWriter(socket.getOutputStream())
            );
        ) {//TANT QUE LE CLIENT EST CONNECTÉ ET LES REQUÊTES SONT OK//
            while (!quit) {
                final String rawRequest;
                Response response;

                try {
                    rawRequest = in.readLine();
                    if (rawRequest == null) {
                        break;
                    }
                    System.out.println(rawRequest + " <- " + remoteAddr);
                    response = process(Request.valueOf(rawRequest));
                } catch (InvalidRequestException e) {
                    response = new Response(Response.Error.ILL_FORMED_REQUEST);
                }
                sendResponse(out, response);
                System.out.println(response + " -> " + remoteAddr);
            }
        } catch (IOException e) {
            System.err.println(e);
        } finally {
            try {
                deleteUser();
                socket.close();
            } catch (IOException e) {
                System.err.println(e);
            }
            System.out.println("Disconnect " + remoteAddr);
        }
    }
}
