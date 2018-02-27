package com.dfg.gabriele.mobiledevrss;

import android.util.Log;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RssItem {
    public enum WorkType {RoadWork, Planned, Incident};

    //members
    public String m_title, m_description, m_publishedDate;
    public WorkType m_type;

    //methods
    public RssItem(String data)
    {
        String regex = "<title>(.*?)</title>";
        Pattern pt = Pattern.compile(regex);
        Matcher matches = pt.matcher(data);
        if (matches.find())
            m_title = matches.group(1);

        regex = "<description>(.*?)</description>";
        pt = Pattern.compile(regex);
        matches = pt.matcher(data);
        if (matches.find())
            m_description = matches.group(1);
    }
}
