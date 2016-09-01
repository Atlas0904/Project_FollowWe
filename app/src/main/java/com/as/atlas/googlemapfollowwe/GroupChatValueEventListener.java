package com.as.atlas.googlemapfollowwe;

import android.content.Context;

import com.firebase.client.Firebase;

/**
 * Created by atlas on 2016/9/1.
 */
public class GroupChatValueEventListener extends BaseValueEventListener<UserMessage> {

    public GroupChatValueEventListener(Context context, Firebase ref, Class clazz) {
        super(context, ref, clazz);
    }

    @Override
    protected void onDataChangeAction(UserMessage userMessage) {

    }
}
