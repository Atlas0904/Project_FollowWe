package com.as.atlas.googlemapfollowwe;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.multidex.MultiDex;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.List;

public class LoginActivity extends AppCompatActivity {


    private static final String TAG = LoginActivity.class.getSimpleName();
    public static final int INTENT_REQUSET_LOGIN_ICON_SELECT_ACTIVITY = 1;
    private static final String PREFERENCE_CURRENT_USER_INFO = "preference_current_user_info";

    private EditText editTextName;
    private EditText editTextPwd;
    private EditText editTextRoomNo;

    private Button buttonLogin;
    private Button buttonIconSelect;

    private CurrentUserInfo currentUserInfo;

    SharedPreferences appSharedPrefs;
    SharedPreferences.Editor prefsEditor;


    // Section for multi-dex application due to method number exceed 65536
    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(newBase);
        MultiDex.install(this);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Binding icon
        editTextName = (EditText) findViewById(R.id.editTextName);
        editTextPwd = (EditText) findViewById(R.id.editTextPwd);
        editTextRoomNo = (EditText) findViewById(R.id.editTextRoomNo);

        appSharedPrefs = PreferenceManager.getDefaultSharedPreferences(this.getApplicationContext());
        prefsEditor = appSharedPrefs.edit();
        currentUserInfo = (loadCurrentUserFromSharePref() != null) ? loadCurrentUserFromSharePref() : new CurrentUserInfo();

        buttonLogin = (Button) findViewById(R.id.buttonLogin);
        buttonLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                currentUserInfo.name = editTextName.getText().toString();
                saveCurrentUserLoginInfoToSharePref();

                Intent intent = new Intent(LoginActivity.this, MapsActivity.class);
                intent.putExtra(CurrentUserInfo.NAME, currentUserInfo.name);
                intent.putExtra(CurrentUserInfo.ICON_NO, currentUserInfo.iconNo);
                intent.putExtra(CurrentUserInfo.NUMBER_ROOM, currentUserInfo.roomNo);
                startActivity(intent);
                Log.d(TAG, "startActivityForResult: name=" + editTextName.getText().toString());
            }
        });

        buttonIconSelect = (Button) findViewById(R.id.buttonIconSelect);
        buttonIconSelect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LoginActivity.this, LoginIconSelectActivity.class);
                startActivityForResult(intent, INTENT_REQUSET_LOGIN_ICON_SELECT_ACTIVITY);
                Log.d(TAG, "startActivityForResult: onClick=" + LoginIconSelectActivity.class.getSimpleName());
            }
        });


    }

    private void saveCurrentUserLoginInfoToSharePref() {
        Gson gson = new Gson();
        String jsonUserMarkers = gson.toJson(currentUserInfo);
        Log.d(TAG,"saveUserLoginInfo jsonUserMarkers = " + jsonUserMarkers);

        prefsEditor.putString(PREFERENCE_CURRENT_USER_INFO, jsonUserMarkers);
        prefsEditor.commit();

    }

    public CurrentUserInfo loadCurrentUserFromSharePref () {

        Gson gson = new Gson();
        String json = appSharedPrefs.getString(PREFERENCE_CURRENT_USER_INFO, "");

        Type type = new TypeToken<CurrentUserInfo>(){}.getType();
        currentUserInfo = gson.fromJson(json, type);

        Log.d(TAG, "loadCurrentUserFromSharePref: currentUserInfo=" + currentUserInfo);
        return currentUserInfo;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.d(TAG, "onActivityResult requestCode=" + requestCode + " resultCode=" + resultCode + " intent=" + data);
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == INTENT_REQUSET_LOGIN_ICON_SELECT_ACTIVITY) {
            if (resultCode == RESULT_OK) {
                currentUserInfo.iconNo = data.getIntExtra(LoginIconSelectActivity.EXTRA_USER_SELECT_IMG, R.mipmap.ic_launcher);
                buttonIconSelect.setBackgroundResource(currentUserInfo.iconNo);
            }
        }

    }
}
