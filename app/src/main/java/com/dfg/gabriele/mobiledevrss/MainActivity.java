package com.dfg.gabriele.mobiledevrss;

import android.os.AsyncTask;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.ViewFlipper;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MainActivity extends AppCompatActivity {

    //members
    //start screen
    private ViewFlipper m_flipper;

    //results lis
    private List<RssItem> m_items;
    private ListView m_resultsView;
    public boolean m_dateSelected = false;
    public int m_selDay, m_selMonth, m_selYear;

    //description screen
    private TextView m_descriptionText;
    private TextView m_startDateView;
    private TextView m_endDateView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        m_items = new ArrayList<>();

        //cache all views
        m_flipper = findViewById(R.id.ViewFlipper);
        m_resultsView = findViewById(R.id.ResultsListView);
        m_descriptionText = findViewById(R.id.DescriptionText);
        m_startDateView = findViewById(R.id.StartDate);
        m_endDateView = findViewById(R.id.EndDate);

        //display the item if it's clicked on
        m_resultsView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //get item based on selection
                RssItem selectedItem = m_items.get(position);
                //set description, start and end date
                m_descriptionText.setText(selectedItem.m_description);
                m_startDateView.setText(selectedItem.m_startDate);
                m_endDateView.setText(selectedItem.m_endDate);

                //flip to the details view
                m_flipper.setDisplayedChild(2);
            }
        });
    }


    public void FetchDataButtonPressed(View view)
    {
        new FeedGetter().execute((Void) null);
    }

    public void BackFromDetailsPressed(View view)
    {
        m_flipper.setDisplayedChild(1);
    }

    public void ShowDatePickerDialog(View view)
    {
        DialogFragment newFragment = new DatePickerFragment();
        newFragment.show(getSupportFragmentManager(), "datePicker");
    }

    public void SelectDate(int day, int month, int year)
    {
        m_dateSelected = true;
        m_selDay = day;
        m_selMonth = month;
        m_selYear = year;
        new FeedGetter().execute((Void) null);
    }

//nested class to fetch the rss feed
    //passing to, progress value, return value
    private class FeedGetter extends AsyncTask<Void, Void, List<RssItem>> {

    @Override
    protected List<RssItem> doInBackground(Void... voids) {
        try
        {
            //get the connection going
            URL url = new URL(getResources().getString(R.string.str_roadWorks));
            URLConnection conn = url.openConnection();
            InputStream is = conn.getInputStream();
            BufferedReader br = new BufferedReader(new InputStreamReader(is));

            //get our data in memory
            String doc = "", line;
            while ((line = br.readLine()) != null)
            {
                doc += line;
            }
            br.close();

            //we have the data in memory, we now parse it using RegEx
            String regex = "<item>(.*?)</item>";
            Pattern pt = Pattern.compile(regex);
            Matcher matches = pt.matcher(doc);

            //build our list of records
            List<RssItem> items = new ArrayList<>();
            while (matches.find())
            {
                items.add(new RssItem(matches.group(1))); //using 1 as 0 is the parent tag
            }
            return items;
        }
        catch (IOException exception)
        {
            Log.e("Feed Getter", exception.toString());
        }

        return null;
    }

    @Override
    protected void onPostExecute(List<RssItem> result)
    {
        m_flipper.setDisplayedChild(1);

        m_items = result;

        //check if a date has been selected and proceed accordingly
        if (m_dateSelected)
        {
            List<RssItem> relevantItems = new ArrayList<>();
            for (RssItem item : m_items)
            {
                if (item.IsWithin(m_selDay, m_selMonth, m_selYear))
                    relevantItems.add(item);
            }
            //create the text for each title
            String[] titles = new String[relevantItems.size()];
            for (int i = 0; i < relevantItems.size(); i++) {
                titles[i] = relevantItems.get(i).m_title;
            }

            //fill up list view with the items using an adapter, following tutorial from: https://www.raywenderlich.com/124438/android-listview-tutorial
            ArrayAdapter adapter = new ArrayAdapter<>(getBaseContext(), android.R.layout.simple_list_item_1, titles);
            m_resultsView.setAdapter(adapter);

        }
        else //we don't have any specific date, fetch everything
        {
            //create the text for each title
            String[] titles = new String[m_items.size()];
            for (int i = 0; i < m_items.size(); i++) {
                titles[i] = m_items.get(i).m_title;
            }

            //fill up list view with the items using an adapter, following tutorial from: https://www.raywenderlich.com/124438/android-listview-tutorial
            ArrayAdapter adapter = new ArrayAdapter<>(getBaseContext(), android.R.layout.simple_list_item_1, titles);
            m_resultsView.setAdapter(adapter);
        }
    }
}
}
