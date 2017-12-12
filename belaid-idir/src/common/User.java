package common;

import java.util.*;

public class User {
    private final String name;
    private final String address;
    private Collection<Ad> ads;

    public User(String name, String address) {
        this.name = name;
        this.address = address;
        this.ads = new LinkedList<Ad>();
    }
    //AJOUTER L'ANNONCE DU CLIENT//
    public void postAd(String title, String body) {
        ads.add(new Ad(title, body, this));
    }
    //SUPPRIMER LES ANNONCES DU CLIENT//
    public boolean deleteAd(long adId) {
        return ads.removeIf(ad -> ad.getId() == adId);
    }
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

    @Override
    public String toString() {
        return address;
    }
}
