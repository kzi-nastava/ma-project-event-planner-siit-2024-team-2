package com.example.eventplanner.clients.utils;

public class ImageUtil {
    private ImageUtil() {}

    public static String getImageUrl(String imageName) {
        return ClientUtils.SERVICE_API_PATH + "images/" + imageName;
    }
}
