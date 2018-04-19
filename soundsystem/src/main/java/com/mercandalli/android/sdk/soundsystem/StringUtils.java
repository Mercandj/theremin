package com.mercandalli.android.sdk.soundsystem;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;

/**
 * Static methods for dealing with the {@link String}.
 */
public final class StringUtils {

    public static List<String> fromJsonArray(String json) {
        List<String> result = new ArrayList<>();
        try {
            JSONArray jsonArray = new JSONArray(json);
            return fromJsonArray(jsonArray);
        } catch (JSONException ignored) {
        }
        return result;
    }

    public static List<String> fromJsonArray(JSONArray jsonArray) {
        List<String> result = new ArrayList<>();
        try {
            for (int i = 0, size = jsonArray.length(); i < size; i++) {
                String string = jsonArray.getString(i);
                result.add(string);
            }
        } catch (JSONException ignored) {
        }
        return result;
    }

}
