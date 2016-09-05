package com.as.atlas.googlemapfollowwe;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import com.firebase.client.Firebase;

import java.util.List;

/**
 * Created by atlas on 2016/9/5.
 */
public abstract class BaseChatRoomActivity extends AppCompatActivity
        implements View.OnClickListener, ChatValueEventListener.OnDataChangeListener {

    private static final String TAG = BaseChatRoomActivity.class.getSimpleName();
    ListView listView;
    EditText editTextInput;
    Button buttonSend;
    ChatroomAdapter chatroomAdapter;

    ChatValueEventListener chatValueEventListener;
    Firebase ref;
    String roomName;

    CurrentUserInfo currentUserInfo;
    List<UserMessage> userMessages;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chatroom);

        initializeByDerviedClass();

        Log.d(TAG, "onCreate ref=" + ref);
        chatValueEventListener = new ChatValueEventListener(this, ref, UserMessage.class);
        listView = (ListView) findViewById(R.id.listViewGroupchat);
        editTextInput = (EditText) findViewById(R.id.editTextGroupchatInput);
        buttonSend = (Button) findViewById(R.id.buttonGroupChatSent);

        userMessages = chatValueEventListener.getList();
        chatroomAdapter = new ChatroomAdapter(this, userMessages);
        listView.setAdapter(chatroomAdapter);

        buttonSend.setOnClickListener(this);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        setTitle("Chat Room: " + roomName);
    }

    protected abstract void initializeByDerviedClass();

    @Override
    public void onClick(View view) {
        String input = editTextInput.getText().toString();
        UserMessage userMessages = new UserMessage(currentUserInfo.name, input, Utils.getCurrentTimeStampWithoutDate());

        chatValueEventListener.push(userMessages);
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
        userMessages = chatValueEventListener.getList();
        chatroomAdapter.update();
        scollToListViewButton();
    }
}
