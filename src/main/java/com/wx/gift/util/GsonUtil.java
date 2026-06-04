package com.wx.gift.util;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public final class GsonUtil {
    private static final Gson GSON = new GsonBuilder().create();

    private GsonUtil() {}

    public static <T> T fromJson(String json, Class<T> clazz) {
        return GSON.fromJson(json, clazz);
    }

    public static String toJson(Object value) {
        return GSON.toJson(value);
    }
}

