package client;

import java.util.logging.*;

class Logger {
    private static final java.util.logging.Logger logger = setup();

    private Logger() {}

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

    static void info(String msg) {
        logger.log(Level.INFO, msg);
    }
}
