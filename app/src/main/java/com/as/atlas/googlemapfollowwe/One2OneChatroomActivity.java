package com.as.atlas.googlemapfollowwe;

import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;

import com.firebase.client.Firebase;

import java.util.Arrays;

/**
 * Created by atlas on 2016/9/5.
 */
public class One2OneChatroomActivity extends BaseChatRoomActivity {
    public final static String TAG = One2OneChatroomActivity.class.getSimpleName();
    public final static String EXTRA_TALKTO= "extra_talkto";

    String talkTo;

    @Override
    public void onCreate(Bundle savedInstanceState, PersistableBundle persistentState) {
        super.onCreate(savedInstanceState);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
    }

    @Override
    protected void initializeByDerviedClass() {
        talkTo = getIntent().getStringExtra(EXTRA_TALKTO);
        currentUserInfo  = (CurrentUserInfo) getIntent().getSerializableExtra(CurrentUserInfo.EXTRA_CURRENTUSERINFO);
        Log.d(TAG, "onCreate: talkTo=" + talkTo + " currentUserInfo=" + currentUserInfo);

        String names[] = new String[] {talkTo, currentUserInfo.name};
        Arrays.sort(names);
        roomName = names[0] + "_" +names[1];
        ref = new Firebase(BaseValueEventListener.URL_FIREBASE)
                .child(BaseValueEventListener.NODE_ROOM_NO).child(String.valueOf(currentUserInfo.roomNo))
                .child(BaseValueEventListener.NODE_ONE2ONECHAT_MSG).child(roomName);
        Log.d(TAG, "onCreate: ref=" + ref);
    }
}
