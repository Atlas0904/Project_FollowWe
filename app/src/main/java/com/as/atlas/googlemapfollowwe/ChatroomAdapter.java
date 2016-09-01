package com.as.atlas.googlemapfollowwe;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

/**
 * Created by atlas on 2016/9/1.
 */
public class ChatroomAdapter extends BaseAdapter {

    private LayoutInflater mLayoutInflater;

    private List<UserMessage> mUserMessages;

    public ChatroomAdapter(Context context, List<UserMessage> userMessages) {
        mLayoutInflater = LayoutInflater.from(context);
        mUserMessages = userMessages;
    }

    public void update() {
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return mUserMessages.size();
    }

    @Override
    public Object getItem(int i) {
        return mUserMessages.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        view = mLayoutInflater.inflate(R.layout.group_chatroom_listview_item, null);
        TextView user = (TextView) view.findViewById(R.id.testViewGroupchatUser);
        TextView msg = (TextView) view.findViewById(R.id.testViewGroupchatMsg);
        TextView time = (TextView) view.findViewById(R.id.testViewGroupchatTime);

        user.setText(mUserMessages.get(i).user);
        msg.setText(mUserMessages.get(i).msg);
        time.setText(mUserMessages.get(i).timestamp);

        return view;
    }
}
