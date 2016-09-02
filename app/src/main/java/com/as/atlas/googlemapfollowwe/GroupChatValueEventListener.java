package com.as.atlas.googlemapfollowwe;

import android.content.Context;
import android.util.Log;

import com.firebase.client.Firebase;

/**
 * Created by atlas on 2016/9/1.
 */
public class GroupChatValueEventListener extends BaseValueEventListener<UserMessage> {

    private static final String TAG = GroupChatValueEventListener.class.getSimpleName();
    private OnDataChangeListener mListener;

    public GroupChatValueEventListener(Context context, Firebase ref, Class clazz) {
        super(context, ref, clazz);
        mListener = (OnDataChangeListener) context;
    }

    public void push(UserMessage userMessage){
        Log.d(TAG, "push: userMessage=" + userMessage);
        mRef.push().setValue(userMessage);
    }

    @Override
    protected void onDataChangeAction(UserMessage userMessage) {
        Log.d(TAG, "onDataChangeAction: userMessage=" + userMessage);
    }
}
