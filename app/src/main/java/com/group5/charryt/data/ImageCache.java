package com.group5.charryt.data;

import android.graphics.Bitmap;

import java.util.HashMap;

public class ImageCache {
    private static HashMap<String, Bitmap> cache = new HashMap<>();

    public static void addImage(String path, Bitmap image) {
        cache.put(path, image);
    }

    // Returns null if image not found.
    public static Bitmap getImage(String path) {
        return cache.get(path);
    }
}
