package com.as.atlas.googlemapfollowwe;

import android.os.PersistableBundle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import com.firebase.client.Firebase;

import java.util.List;

public class GroupChatroomActivity extends BaseChatRoomActivity
        implements View.OnClickListener, ChatValueEventListener.OnDataChangeListener {

    @Override
    public void onCreate(Bundle savedInstanceState, PersistableBundle persistentState) {
        super.onCreate(savedInstanceState, persistentState);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        setTitle("Chat Room= " + currentUserInfo.roomNo);
    }

    @Override
    protected void initializeByDerviedClass() {
        currentUserInfo  = (CurrentUserInfo) getIntent().getSerializableExtra(CurrentUserInfo.EXTRA_CURRENTUSERINFO);
        ref = new Firebase(BaseValueEventListener.URL_FIREBASE)
                .child(BaseValueEventListener.NODE_ROOM_NO).child(String.valueOf(currentUserInfo.roomNo))
                .child(BaseValueEventListener.NODE_GROUPCHAT_MSG);

        roomName = String.valueOf(currentUserInfo.roomNo);
    }
}
