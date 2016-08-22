package com.as.atlas.googlemapfollowwe;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.Toast;

public class LoginIconSelectActivity extends AppCompatActivity {

    GridView gridView;
    String [] description = {
            "0",
            "1",
            "2",
            "3",
            "4",
            "5",
            "6",
            "7",
            "8",
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
            }
        });

    }
}
