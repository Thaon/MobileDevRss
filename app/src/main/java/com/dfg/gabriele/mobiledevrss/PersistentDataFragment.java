//by Gabriele Maddaloni S1436255

package com.dfg.gabriele.mobiledevrss;

import android.os.Bundle;
import android.app.Fragment;

import java.util.Date;
import java.util.List;

//from the documentation at: https://developer.android.com/guide/topics/resources/runtime-changes.html#RetainingAnObject
public class PersistentDataFragment extends Fragment {

    private List<RssItem> m_cachedItems;
    private int m_loadedPage;
    private RssItem m_rssItem;
    private Date m_selectedDate;
    private boolean m_dateSelected;

    //setup the fragment to be persistent during app changes
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // retain this fragment during app restart
        setRetainInstance(true);
    }

    public void SaveData(int page, List<RssItem> items, RssItem rssItem, boolean isDateSel, Date selDate)
    {
        m_cachedItems = items;
        m_loadedPage = page;
        m_rssItem = rssItem;
        m_selectedDate = selDate;
        m_dateSelected = isDateSel;
    }

    public int GetPage()
    {
        return m_loadedPage;
    }

    public List<RssItem> GetCachedItems()
    {
        return m_cachedItems;
    }

    public RssItem GetItem()
    {
        return m_rssItem;
    }

    public Date GetDate()
    {
        return m_selectedDate;
    }

    public boolean IsDateSelected()
    {
        return m_dateSelected;
    }
}
