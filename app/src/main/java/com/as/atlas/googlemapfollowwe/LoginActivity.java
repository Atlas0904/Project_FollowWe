package com.as.atlas.googlemapfollowwe;

import android.content.Context;
import android.content.Intent;
import android.support.multidex.MultiDex;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class LoginActivity extends AppCompatActivity {


    private static final String TAG = LoginActivity.class.getSimpleName();
    public static final int INTENT_REQUSET_LOGIN_ICON_SELECT_ACTIVITY = 1;

    private EditText editTextName;
    private EditText editTextPwd;
    private EditText editTextRoomNo;

    private Button buttonLogin;
    private Button buttonIconSelect;

    private int iconNo = R.mipmap.ic_launcher;

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

        buttonLogin = (Button) findViewById(R.id.buttonLogin);
        buttonLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, MapsActivity.class);
                intent.putExtra(CurrentUserInfo.NAME, editTextName.getText().toString());
                intent.putExtra(CurrentUserInfo.ICON_NO, iconNo);
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.d(TAG, "onActivityResult requestCode=" + requestCode + " resultCode=" + resultCode + " intent=" + data);
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == INTENT_REQUSET_LOGIN_ICON_SELECT_ACTIVITY) {
            if (resultCode == RESULT_OK) {
                iconNo = data.getIntExtra(LoginIconSelectActivity.EXTRA_USER_SELECT_IMG, R.mipmap.ic_launcher);
                buttonIconSelect.setBackgroundResource(iconNo);
            }
        }

    }
}
