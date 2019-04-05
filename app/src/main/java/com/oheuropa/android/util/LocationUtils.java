package com.oheuropa.android.util;

import android.location.Location;

public class LocationUtils {

    static public String convert(double latitude, double longitude) {
        StringBuilder builder = new StringBuilder();

        String latitudeDegrees = Location.convert(Math.abs(latitude), Location.FORMAT_SECONDS);
        String[] latitudeSplit = latitudeDegrees.split(":");
        builder.append(latitudeSplit[0]);
        builder.append("° ");
        builder.append(latitudeSplit[1]);
        builder.append("' ");

        float seconds = Float.parseFloat(latitudeSplit[2]);
        builder.append((int)Math.floor(seconds));
        builder.append("\" ");

        if (latitude < 0) {
            builder.append("S ");
        } else {
            builder.append("N ");
        }

        String longitudeDegrees = Location.convert(Math.abs(longitude), Location.FORMAT_SECONDS);
        String[] longitudeSplit = longitudeDegrees.split(":");
        builder.append(longitudeSplit[0]);
        builder.append("° ");
        builder.append(longitudeSplit[1]);
        builder.append("' ");

        seconds = Float.parseFloat(longitudeSplit[2]);
        builder.append((int)Math.floor(seconds));
        builder.append("\" ");

        if (longitude < 0) {
            builder.append("W");
        } else {
            builder.append("E");
        }

        return builder.toString();
    }
}
