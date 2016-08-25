package com.as.atlas.googlemapfollowwe;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;


import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;

/**
 * Created by atlas on 2016/8/24.
 */
public class MapInfoWindowAdapter implements GoogleMap.InfoWindowAdapter {

    private static final int INFO_WINDOW_SIZE = 1500;
    private Context context;
    private UserAddedPointEventListener userAddedPointEventListener;

    private View view;

    private TextView textViewLatLng;
    private TextView textViewAddr;
    private TextView textViewStar;
    private ListView listViewChatMsg;

    private ChatMessageAdapter listAdapter;

    public MapInfoWindowAdapter(Context context, UserAddedPointEventListener userAddedPointEventListener) {
        this.context = context;
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        view = layoutInflater.inflate(R.layout.map_info_window_layout, null);
        view.setLayoutParams(new RelativeLayout.LayoutParams(INFO_WINDOW_SIZE, RelativeLayout.LayoutParams.WRAP_CONTENT));
        this.userAddedPointEventListener = userAddedPointEventListener;

        textViewLatLng = (TextView) view.findViewById(R.id.textViewInfoWinLatLng);
        textViewAddr = (TextView) view.findViewById(R.id.textViewInfoWinAddr);
        textViewStar = (TextView) view.findViewById(R.id.textViewInfoWinStar);
        listViewChatMsg = (ListView) view.findViewById(R.id.listViewChatMsg);
    }

    @Override
    public View getInfoWindow(Marker marker) {
        return null;
    }

    @Override
    public View getInfoContents(Marker marker) {

        textViewLatLng.setText(marker.getTitle());
        textViewAddr.setText(marker.getSnippet());
        textViewStar.setText("5");

        // find UserPlace
        // Put into constructor
        LatLng latLng = marker.getPosition();
        String id =UserPlace.getId(latLng);
        UserPlace userPlace = userAddedPointEventListener.getUserPlaces().get(id).userPlace;

        listAdapter = new ChatMessageAdapter(context, userPlace);
        listViewChatMsg.setAdapter(listAdapter);

        return view;
    }

    public class ChatMessageAdapter extends BaseAdapter {

        private LayoutInflater layoutInflater;
        private UserPlace userPlace;

        public ChatMessageAdapter(Context context, UserPlace userPlace) {
            layoutInflater = LayoutInflater.from(context);
            this.userPlace = userPlace;
        }

        @Override
        public int getCount() {
            return userPlace.userMessages.size();
        }

        @Override
        public Object getItem(int pos) {
            return userPlace.userMessages.get(pos);
        }

        @Override
        public long getItemId(int pos) {
            return pos;
        }

        @Override
        public View getView(int pos, View view, ViewGroup viewGroup) {
            view = layoutInflater.inflate(R.layout.map_info_window_chat_msg_listview, null);
            TextView textViewName = (TextView) view.findViewById(R.id.textViewListViewUserName);
            TextView textViewMsg = (TextView) view.findViewById(R.id.textViewListViewMsg);
            TextView textViewTime = (TextView) view.findViewById(R.id.textViewListViewTime);

            textViewName.setText(userPlace.userMessages.get(pos).user);
            textViewMsg.setText(userPlace.userMessages.get(pos).msg);
            textViewTime.setText(userPlace.userMessages.get(pos).timestamp);

            return view;
        }
    }
}
