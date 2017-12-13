package client;

import java.util.logging.*;

/*
 * A simple logger that outputs to a file 'client.log' the messages
 * exchanged on the network by the client application.
 */
class Logger {
    private static final java.util.logging.Logger logger = setup();

    private Logger() {}

    /* Initialize the logger.  */
    private static java.util.logging.Logger setup() {
        final Handler handler;
        java.util.logging.Logger logger = null;

        try {
            LogManager.getLogManager().reset();
            handler = new FileHandler("client.log");
            handler.setFormatter(new SimpleFormatter());
            logger = java.util.logging.Logger.getLogger("client");
            logger.addHandler(handler);
        } catch (java.io.IOException e) {
            System.err.println(e);
            System.exit(1);
        }
        return logger;
    }
    //AJOUT DE L'INFORMATION SUR L'HISTORIQUE DE CONNEXION DU CLIENT//
    static void info(String msg) {
        logger.log(Level.INFO, msg);
    }
}
