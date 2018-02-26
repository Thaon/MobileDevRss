package com.dfg.gabriele.mobiledevrss;

import android.util.Log;

/**
 * Created by gabri on 26/02/2018.
 */

public class RssItem {
    public enum WorkType {RoadWork, Planned, Incident};

    //members
    public String m_title, m_description, m_publishedDate;
    public WorkType m_type;

    //methods
    public RssItem(String data)
    {
        Log.e("Data", data);
    }
}
