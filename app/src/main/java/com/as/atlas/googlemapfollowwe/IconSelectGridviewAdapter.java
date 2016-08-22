package com.as.atlas.googlemapfollowwe;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Created by atlas on 2016/8/22.
 */
public class IconSelectGridviewAdapter extends BaseAdapter {

    private Context context;
    private final String[] description;
    private final int[] imageId;
    private final LayoutInflater mInflater;

    public IconSelectGridviewAdapter(Context context, String[] description, int[] imageId) {
        this.context = context;
        this.description = description;
        this.imageId = imageId;
        this.mInflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return imageId.length;
    }

    @Override
    public Object getItem(int i) {
        return null;
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // TODO Auto-generated method stub
        View grid;
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        if (convertView == null) {
            grid = mInflater.inflate(R.layout.gridview_icon_select_item, parent, false);
            TextView textView = (TextView) grid.findViewById(R.id.gridview_text);
            ImageView imageView = (ImageView)grid.findViewById(R.id.gridview_image);
            textView.setText(description[position]);
            imageView.setImageResource(imageId[position]);
        } else {
            grid = (View) convertView;
        }
        return grid;
    }
}
