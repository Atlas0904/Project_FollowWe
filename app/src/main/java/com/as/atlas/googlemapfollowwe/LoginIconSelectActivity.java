package com.as.atlas.googlemapfollowwe;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.Toast;

public class LoginIconSelectActivity extends AppCompatActivity {

    public static final String EXTRA_USER_SELECT_IMG = "user_select_img";
    private static final String TAG = LoginIconSelectActivity.class.getSimpleName();

    GridView gridView;
    String [] description = {
            "M0",
            "M1",
            "M2",
            "M3",
            "M4",
            "M5",
            "M6",
            "M7",
            "M8",
            "F0",
            "F1",
            "F2",
            "F3",
            "F4",
            "F5",
            "F6",
            "F7",
            "F8",
    };
    int[] imageId = {
            R.mipmap.icon_boy_0,
            R.mipmap.icon_boy_1,
            R.mipmap.icon_boy_2,
            R.mipmap.icon_boy_3,
            R.mipmap.icon_boy_4,
            R.mipmap.icon_boy_5,
            R.mipmap.icon_boy_6,
            R.mipmap.icon_boy_7,
            R.mipmap.icon_boy_8,
            R.mipmap.icon_girl_0,
            R.mipmap.icon_girl_1,
            R.mipmap.icon_girl_2,
            R.mipmap.icon_girl_3,
            R.mipmap.icon_girl_4,
            R.mipmap.icon_girl_5,
            R.mipmap.icon_girl_6,
            R.mipmap.icon_girl_7,
            R.mipmap.icon_girl_8
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_icon_select);

        IconSelectGridviewAdapter adapter = new IconSelectGridviewAdapter(LoginIconSelectActivity.this, description, imageId);
        gridView = (GridView) findViewById(R.id.gridview);
        gridView.setAdapter(adapter);

        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Toast.makeText(LoginIconSelectActivity.this, "You Clicked at " + description[i], Toast.LENGTH_SHORT).show();
                Log.d(TAG, "setOnItemClickListener: index=" + i + " description=" + description[i] + " icon=" + imageId[i]);

                Intent data = new Intent();
                data.putExtra(EXTRA_USER_SELECT_IMG, imageId[i]);
                if (getParent() == null) {
                    setResult(Activity.RESULT_OK, data);
                } else {
                    getParent().setResult(Activity.RESULT_OK, data);
                }
                finish();
            }
        });

    }
}
