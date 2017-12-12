package common;

import java.util.*;

/* Class representing an ad */
public class Ad {
    /* Counter to generate fresh id for ads.  */
    private static Long nbAds = 0L;

    /* The ad's id. */
    private final Long id;

    /* The ad's title.  */
    private final String title;

    /* The ad's body.  */
    private final String body;

    /* The ad's author.  */
    private final User author;

    /****************/
    /* Constructors */
    /****************/
    public Ad(String title, String body, User author) {
        synchronized (nbAds) {
            id = nbAds++;
        }
        this.title = title;
        this.body = body;
        this.author = author;
    }

    private Ad(long id, String title, String body, String authorName,
               String authorAddr) {
        this.id = id;
        this.title = title;
        this.body = body;
        this.author = new User(authorName, authorAddr);
    }

    /***********/
    /* Getters */
    /***********/
    public long getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getBody() {
        return body;
    }

    public User getAuthor() {
        return author;
    }


    /**********************/
    /* Ad parsing methods */
    /**********************/

    /*
     * Parse the given string according to the format of ads sent by the
     * server.
     */
    private static Ad valueOf(String s) throws InvalidAdException {
        final String[] tokens = s.split(ProtocolParameters.FIELD_SEP, 4);

        if (tokens.length != 4) {
            throw new InvalidAdException();
        }

        /*
         * We pass the empty string as author name because our protocol
         * (in its current version) doesn't send this information.
         */
        return new Ad(Long.parseLong(tokens[0]), tokens[1], tokens[3], "",
                      tokens[2]);
    }

    /*
     * Build a collection of ads based on the given string which
     * corresponds to the payload sent back by the server when it
     * responds to a GET request.
     */
    public static Collection<Ad> valuesOf(String s) throws InvalidAdException {
        final String[] tokens = s.split(ProtocolParameters.AD_SEP);
        final Collection<Ad> ads = new LinkedList<Ad>();

        for (String token: tokens) {
            if (!token.isEmpty()) {
                ads.add(valueOf(token));
            }
        }
        return ads;
    }

    /************************/
    /* Serialization of ads */
    /************************/
    @Override
    public String toString() {
        return String.join(ProtocolParameters.FIELD_SEP, id.toString(), title,
                           author.getAddress(), body);
    }

    public static String toString(Collection<Ad> ads) {
        final Collection<String> adStrs = new LinkedList<String>();

        for (Ad ad: ads) {
            adStrs.add(ad.toString());
        }

        return ProtocolParameters.AD_SEP +
            String.join(ProtocolParameters.AD_SEP, adStrs) +
            ProtocolParameters.AD_SEP;
    }
}
