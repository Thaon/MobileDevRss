//by Gabriele Maddaloni S1436255

package com.dfg.gabriele.mobiledevrss;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class RssItemAdapter extends BaseAdapter {

    //member variables
    private Context m_context;
    private LayoutInflater m_inflater;
    private List<RssItem> m_items;

    private int m_maxDuration;

    public RssItemAdapter(Context context, List<RssItem> items, int maxDuration) {
        m_context = context;
        m_items = items;
        m_maxDuration = maxDuration;
        m_inflater = (LayoutInflater) m_context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return m_items.size();
    }

    @Override
    public Object getItem(int i) {
        return m_items.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        View itemView = m_inflater.inflate(R.layout.item_view, viewGroup, false);

        //get a reference to our item layout's views
        ImageView icon = (ImageView) itemView.findViewById(R.id.itemIcon);
        TextView title = (TextView) itemView.findViewById(R.id.itemTitle);
        ImageView severity = (ImageView) itemView.findViewById((R.id.severityImg));

        //set the item up
        RssItem item = (RssItem) getItem(i);
        title.setText(item.m_title);

        //using the Picasso image library to load images on the background, this will not freeze the UI thread
        Picasso.with(m_context).load(m_context.getResources().getString(R.string.str_roadWorksImg)).placeholder(R.mipmap.ic_launcher).into(icon);

        //switch between severities, we first normalise the periods and then assign an icon to the item based on duration
        int duration = item.GetDurationInDays();
        if (duration < m_maxDuration / 3)
        {
            //duration is short, let's use the green dot
            Picasso.with(m_context).load(m_context.getResources().getString(R.string.str_greenDot)).placeholder(R.mipmap.ic_launcher).into(severity);
        }
        else if (duration >= m_maxDuration / 3 && duration < (m_maxDuration / 3) * 2)
        {
            //medium duration, yellow dot
            Picasso.with(m_context).load(m_context.getResources().getString(R.string.str_yellowDot)).placeholder(R.mipmap.ic_launcher).into(severity);
        }
        else
        {
            //long duration, red dot here
            Picasso.with(m_context).load(m_context.getResources().getString(R.string.str_redDot)).placeholder(R.mipmap.ic_launcher).into(severity);
        }


        return itemView;
    }
}
