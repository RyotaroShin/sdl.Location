package jp.ac.titech.itpro.sdl.location;

import java.util.List;

public class RestaurantInfo {
    public String name;
    public double lat;
    public double lng;
    public String photo = "";

    @Override
    public String toString() {
        return "name:" + name
                + "lat:" + lat
                + "lng:" + lng
                + "photo" + photo;
    }
}
