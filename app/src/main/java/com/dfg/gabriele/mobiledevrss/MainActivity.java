package com.dfg.gabriele.mobiledevrss;

import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

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
    TextView m_text;
    List<RssItem> m_items;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        m_text = findViewById(R.id.textView);
        m_items = new ArrayList<>();

    }

    public void FetchDataButtonPressed(View view)
    {
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
}
}
