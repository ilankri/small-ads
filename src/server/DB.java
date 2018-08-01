package server;

import common.*;
import java.util.*;
import java.net.InetAddress;

class DB {
    private static final Map<InetAddress, User> USERS =
        new Hashtable<InetAddress, User>();

    private DB() {}
    //AJOUT D'UN CLIENT A LA LISTE DES CLIENTS CONNECTÉS//
    static synchronized void createUser(String name, InetAddress addr)
        throws AlreadyUsedUsernameException {
        for (User user: USERS.values()) {
            if (user.getName().equals(name)) {
                throw new AlreadyUsedUsernameException();
            }
        }
        USERS.put(addr, new User(name, addr.toString()));
    }
    //AJOUTER UNE ANNONCE D'UN CLIENT//
    static void createAd(InetAddress userAddr, String title,
                         String body) {
        final User user = DB.readUser(userAddr);

        synchronized (user) {
            user.postAd(title, body);
        }
    }
    //SUPPRIMER LE CLIENT DE LA LISTE DES CLIENTS CONNECTÉS//
    static synchronized void deleteUser(InetAddress userAddr) {
        USERS.remove(userAddr);
    }
    //SUPPRIMER UNE ANNONCE D'UN CLIENT//
    static boolean deleteAd(InetAddress userAddr, long adId) {
        final User user = DB.readUser(userAddr);

        synchronized (user) {
            return user.deleteAd(adId);
        }
    }
    //RECUPÉRER LE PROPRIÉTAIRE DE L'ANNONCE//
    static synchronized User readUser(InetAddress userAddr) {
        return USERS.get(userAddr);
    }
    //RECUPÉRER TOUTES LES ANNONCES DE TOUS LES CLIENTS//
    static Collection<Ad> readAllAds() {
        Collection<Ad> ads = new LinkedList<Ad>();

        synchronized (USERS) {
            for (User user: USERS.values()) {
                for (Ad ad: user.getAds()) {
                    ads.add(ad);
                }
            }
        }
        return ads;
    }
}
