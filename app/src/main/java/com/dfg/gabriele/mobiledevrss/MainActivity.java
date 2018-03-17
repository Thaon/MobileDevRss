//by Gabriele Maddaloni S1436255

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
import android.widget.Spinner;
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
    private Spinner m_rssSelector;
    private RssItem.WorkType m_selectedFeed;

    //results lis
    private String m_selectedRssFeed;
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
        m_rssSelector = findViewById(R.id.RssSelector);

        //fill in the Rss Selector spinner and add on click adapter
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.SpinnerStrings, android.R.layout.simple_spinner_item);
        m_rssSelector.setAdapter(adapter);

        m_rssSelector.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> arg0, View arg1,
                                       int pos, long arg3) {
                if (pos == 0)
                {
                    m_selectedRssFeed = getResources().getString(R.string.str_planned);
                    m_selectedFeed = RssItem.WorkType.Planned;
                }
                else
                {
                    m_selectedRssFeed = getResources().getString(R.string.str_incidents);
                    m_selectedFeed = RssItem.WorkType.Incident;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> arg0) {
                // TODO Auto-generated method stub

            }

        });

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
                m_flipper.setDisplayedChild(3); //details view
            }
        });
    }


    public void FetchDataButtonPressed(View view)
    {
        m_flipper.setDisplayedChild(1); //progress bar

        new FeedGetter().execute((Void) null);
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
        m_flipper.setDisplayedChild(1); //progress bar
        new FeedGetter().execute((Void) null);
    }

    //take care of the back functionality
    @Override
    public void onBackPressed() {
        int displayedPage = m_flipper.getDisplayedChild();

        switch (displayedPage)
        {
            case 1:
                //we are in the loading bar screen, do nothing
                break;

            case 2: //back from the list view
                m_flipper.setDisplayedChild(0); //go back to menu
                break;

            case 3: //back from the details view
                m_flipper.setDisplayedChild(2); //go back to list view
                break;

            default: //in any other case we just close the application
                super.onBackPressed();
                break;
        }
    }

//nested class to fetch the rss feed
    //passing to, progress value, return value
    private class FeedGetter extends AsyncTask<Void, Void, List<RssItem>> {

    @Override
    protected List<RssItem> doInBackground(Void... voids) {
        try
        {
            //get the connection going
            URL url = new URL(m_selectedRssFeed);
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
                items.add(new RssItem(matches.group(1), m_selectedFeed)); //using 1 as 0 is the parent tag
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
        m_flipper.setDisplayedChild(2); //list view
        m_items = result;
        List<RssItem> relevantItems = new ArrayList<>();

        //calculate the biggest duration for the works
        int maxDuration = 0;
        for (RssItem itm : m_items)
        {
            if (itm.GetDurationInDays() > maxDuration)
            {
                maxDuration = itm.GetDurationInDays();
            }
        }

        //check if a date has been selected and proceed accordingly
        if (m_dateSelected)
        {
            for (RssItem item : m_items)
            {
                if (item.IsWithin(m_selDay, m_selMonth, m_selYear))
                    relevantItems.add(item);
            }

        }
        else //we don't have any specific date, fetch everything
        {
            relevantItems = m_items;
        }

        //fill up results view with the items using an adapter, following tutorial from: https://www.raywenderlich.com/124438/android-listview-tutorial
        RssItemAdapter adapter = new RssItemAdapter(getBaseContext(), relevantItems, maxDuration);
        m_resultsView.setAdapter(adapter);
    }
}
}
