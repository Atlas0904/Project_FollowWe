package com.as.atlas.googlemapfollowwe;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.firebase.client.Firebase;

import java.util.ArrayList;
import java.util.List;

public class GroupChatroomActivity extends AppCompatActivity
        implements View.OnClickListener, GroupChatValueEventListener.OnDataChangeListener {

    ListView listView;
    EditText editTextInput;
    Button buttonSend;
    ChatroomAdapter chatroomAdapter;

    GroupChatValueEventListener groupChatValueEventListener;
    Firebase ref;

    CurrentUserInfo currentUserInfo;
    List<UserMessage> userMessages;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_chatroom);

        currentUserInfo  = (CurrentUserInfo) getIntent().getSerializableExtra(CurrentUserInfo.EXTRA_CURRENTUSERINFO);
        ref = new Firebase(BaseValueEventListener.URL_FIREBASE)
                .child(BaseValueEventListener.NODE_ROOM_NO).child(String.valueOf(currentUserInfo.roomNo))
                .child(BaseValueEventListener.NODE_GROUPCHAT_MSG);
        groupChatValueEventListener = new GroupChatValueEventListener(this, ref, UserMessage.class);

        listView = (ListView) findViewById(R.id.listViewGroupchat);
        editTextInput = (EditText) findViewById(R.id.editTextGroupchatInput);
        buttonSend = (Button) findViewById(R.id.buttonGroupChatSent);

        userMessages = groupChatValueEventListener.getList();
        chatroomAdapter = new ChatroomAdapter(this, userMessages);
        listView.setAdapter(chatroomAdapter);

        buttonSend.setOnClickListener(this);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        setTitle("Group chat Room: " + currentUserInfo.roomNo);
    }

    @Override
    public void onClick(View view) {
        String input = editTextInput.getText().toString();
        UserMessage userMessages = new UserMessage(currentUserInfo.name, input, Utils.getCurrentTimeStampWithoutDate());

        groupChatValueEventListener.push(userMessages);
    }

    private void scollToListViewButton() {
        listView.post(new Runnable() {
            @Override
            public void run() {
                listView.setSelection(chatroomAdapter.getCount() -1);
            }
        });
    }

    @Override
    public void onDataChangeCallback(Object o) {
        userMessages = groupChatValueEventListener.getList();
        chatroomAdapter.update();
        scollToListViewButton();
    }
}
