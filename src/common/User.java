package common;

import java.util.*;

/* Class to represent a user */
public class User {
    /* The username.  */
    private final String name;

    /* The user's IP address.  */
    private final String address;

    /* Ads posted by the user.  */
    private Collection<Ad> ads;

    public User(String name, String address) {
        this.name = name;
        this.address = address;
        this.ads = new LinkedList<Ad>();
    }

    /***********/
    /* Getters */
    /***********/

    //RETOURNER LE NOM DU CLIENT//
    public String getName() {
        return name;
    }
    //RETOURNER L'ADRESSE DU CLIENT//
    public String getAddress() {
        return address;
    }
    //RETOURNER LES ANNONCES DU CLIENT//
    public Collection<Ad> getAds() {
        return Collections.unmodifiableCollection(ads);
    }

    /* Method to post an ad with the given title and body.  */
    public void postAd(String title, String body) {
        ads.add(new Ad(title, body, this));
    }

    /*
     * Method to delete the ad with the given id from the collection of
     * ads posted by the user.
     */
    public boolean deleteAd(long adId) {
        return ads.removeIf(ad -> ad.getId() == adId);
    }

    /**************************/
    /* Serialization of users */
    /**************************/
    @Override
    public String toString() {
        return address;
    }
}
