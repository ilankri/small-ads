package common;

import java.util.*;

public class Ad {
    private static Long nbAds = 0L;

    private final Long id;
    private final String title;
    private final String body;
    private final User author;

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
