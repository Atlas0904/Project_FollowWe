package com.as.atlas.googlemapfollowwe;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class GroupChatroomActivity extends AppCompatActivity implements View.OnClickListener{

    ListView listView;
    EditText editTextInput;
    Button buttonSend;
    ChatroomAdapter chatroomAdapter;

    CurrentUserInfo currentUserInfo;
    List<UserMessage> userMessages;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_chatroom);

        listView = (ListView) findViewById(R.id.listViewGroupchat);
        editTextInput = (EditText) findViewById(R.id.editTextGroupchatInput);
        buttonSend = (Button) findViewById(R.id.buttonGroupChatSent);
        userMessages = new ArrayList<UserMessage>();
        chatroomAdapter = new ChatroomAdapter(this, userMessages);
        listView.setAdapter(chatroomAdapter);

        currentUserInfo  = (CurrentUserInfo) getIntent().getSerializableExtra(CurrentUserInfo.EXTRA_CURRENTUSERINFO);
        buttonSend.setOnClickListener(this);


        setTitle("Group chat Room: " + currentUserInfo.roomNo);
    }

    @Override
    public void onClick(View view) {
        String input = editTextInput.getText().toString();
        userMessages.add(new UserMessage(currentUserInfo.name, input, Utils.getCurrentTimeStampWithoutDate()));
        chatroomAdapter.update();
        scollToListViewButton();
    }

    private void scollToListViewButton() {
        listView.post(new Runnable() {
            @Override
            public void run() {
                listView.setSelection(chatroomAdapter.getCount() -1);
            }
        });
    }
}
