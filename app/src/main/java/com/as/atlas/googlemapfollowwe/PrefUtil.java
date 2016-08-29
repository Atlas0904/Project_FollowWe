package com.as.atlas.googlemapfollowwe;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by atlas on 2016/8/26.
 */
public class PrefUtil<T> {

    private static final String TAG = PrefUtil.class.getSimpleName();

    private static SharedPreferences appSharedPrefs;
    private static SharedPreferences.Editor prefsEditor;

    private Type type;

    public PrefUtil(Context context, Type type) {
        appSharedPrefs = PreferenceManager.getDefaultSharedPreferences(context.getApplicationContext());
        prefsEditor = appSharedPrefs.edit();
        this.type = type;
    }

    public void saveToSharePref(T t, String PREF_NAME) {
        Gson gson = new Gson();
        String json = gson.toJson(t);
        Log.d(TAG,"saveToSharePref: json = " + json);

        Log.d(TAG, "saveToSharePref: t=" + t + " PREF_NAME=" + PREF_NAME);

//        GsonBuilder gsonBuilder = new GsonBuilder();
//        gsonBuilder.registerTypeAdapter(Date.class,
//                new JsonDeserializer<Date>() {
//                    @Override
//                    public Date deserialize(JsonElement jsonElement, Type type,
//                                            JsonDeserializationContext context)
//                            throws JsonParseException {
//                        Calendar calendar = Calendar.getInstance();
//                        calendar.setTimeInMillis(jsonElement.getAsLong());
//                        return calendar.getTime();
//                    }
//                });
//        Gson gson = gsonBuilder.create();

//        Type type = new TypeToken<T>(){}.getType();
//        String json = gson.toJson(t, type);

        Log.d(TAG,"saveToSharePref: json = " + json);

        prefsEditor.putString(PREF_NAME, json);
        prefsEditor.commit();
    }

    public T loadFromPref(String PREF_NAME) {
        Gson gson = new Gson();
        Log.d(TAG, "loadFromPref: PREF_NAME=" + PREF_NAME);

//        GsonBuilder gsonBuilder = new GsonBuilder();
//        gsonBuilder.registerTypeAdapter(Date.class,
//                new JsonDeserializer<Date>() {
//                    @Override
//                    public Date deserialize(JsonElement jsonElement, Type type,
//                                            JsonDeserializationContext context)
//                            throws JsonParseException {
//                        Calendar calendar = Calendar.getInstance();
//                        calendar.setTimeInMillis(jsonElement.getAsLong());
//                        return calendar.getTime();
//                    }
//                });
//        Gson gson = gsonBuilder.create();


        String json = appSharedPrefs.getString(PREF_NAME, "");
        Log.d(TAG, "loadFromPref: json" + json);


//        Type type = new TypeToken<T>(){}.getType();
        Log.d(TAG, "loadFromPref: type" + type);
        return (T) gson.fromJson(json, type);
    }
}
