package com.as.atlas.googlemapfollowwe;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.List;

/**
 * Created by atlas on 2016/8/26.
 */
public class PrefUtil<T> {

    private static final String TAG = PrefUtil.class.getSimpleName();

    private static Context context;
    private static SharedPreferences appSharedPrefs;
    private static SharedPreferences.Editor prefsEditor;

    public PrefUtil(Context context) {
        this.context = context;
        appSharedPrefs = PreferenceManager.getDefaultSharedPreferences(context.getApplicationContext());
        prefsEditor = appSharedPrefs.edit();
    }

    public void saveToSharePref(T t, String PREF_NAME) {
        Gson gson = new Gson();
        String json = gson.toJson(t);
        Log.d(TAG,"jsonUserMarkers = " + json);

        prefsEditor.putString(PREF_NAME, json);
        prefsEditor.commit();
    }

    public T loadFromPref(String PREF_NAME) {
        Gson gson = new Gson();
        String json = appSharedPrefs.getString(PREF_NAME, "");

        Type type = new TypeToken<List<T>>(){}.getType();
        return gson.fromJson(json, type);
    }
}
