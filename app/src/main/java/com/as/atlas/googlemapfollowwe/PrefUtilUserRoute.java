package com.as.atlas.googlemapfollowwe;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;

/**
 * Created by atlas on 2016/8/29.
 */
public class PrefUtilUserRoute {

    private static final String TAG = PrefUtilUserRoute.class.getSimpleName();

    private static Context context;
    private static SharedPreferences appSharedPrefs;
    private static SharedPreferences.Editor prefsEditor;

    public PrefUtilUserRoute(Context context) {
        this.context = context;
        appSharedPrefs = PreferenceManager.getDefaultSharedPreferences(context.getApplicationContext());
        prefsEditor = appSharedPrefs.edit();
    }

    public void saveToSharePref(UserRoute t, String PREF_NAME) {

        Gson gson = new Gson();
        String json = gson.toJson(t);
        Log.d(TAG,"saveToSharePref: json = " + json);


        prefsEditor.putString(PREF_NAME, json);
        prefsEditor.commit();
    }

    public UserRoute loadFromPref(String PREF_NAME) {
        Gson gson = new Gson();

        String json = appSharedPrefs.getString(PREF_NAME, "");
        Log.d(TAG, "loadFromPref: json" + json);

        Type type = new TypeToken<UserRoute>(){}.getType();
        return gson.fromJson(json, type);
    }
}
