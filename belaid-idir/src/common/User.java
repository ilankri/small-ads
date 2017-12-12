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

    public void postAd(String title, String body) {
        ads.add(new Ad(title, body, this));
    }

    public boolean deleteAd(long adId) {
        return ads.removeIf(ad -> ad.getId() == adId);
    }

    public String getName() {
        return name;
    }

    public String getAddress() {
        return address;
    }

    public Collection<Ad> getAds() {
        return Collections.unmodifiableCollection(ads);
    }

    @Override
    public String toString() {
        return address;
    }
}
