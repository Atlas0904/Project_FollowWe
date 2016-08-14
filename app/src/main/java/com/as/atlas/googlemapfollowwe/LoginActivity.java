package com.as.atlas.googlemapfollowwe;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class LoginActivity extends AppCompatActivity {


    private static final String TAG = LoginActivity.class.getSimpleName();
    private EditText editTextName;
    private EditText editTextPwd;
    private EditText editTextRoomNo;

    private Button buttonLogin;

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
                startActivity(intent);
                Log.d(TAG, "startActivityForResult: name=" + editTextName.getText().toString());
            }
        });
    }
}
