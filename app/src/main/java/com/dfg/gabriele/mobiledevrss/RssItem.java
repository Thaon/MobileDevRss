//by Gabriele Maddaloni S1436255

package com.dfg.gabriele.mobiledevrss;

import android.util.Log;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class RssItem {
    public enum WorkType {RoadWork, Planned, Incident};

    //members
    public String m_title, m_description, m_publishedDate, m_startDate, m_endDate;
    public Date m_startingDate, m_endingDate;
    public WorkType m_type;

    //methods
    public RssItem(String data, WorkType type)
    {
        String regex = "<title>(.*?)</title>";
        Pattern pt = Pattern.compile(regex);
        Matcher matches = pt.matcher(data);
        if (matches.find())
            m_title = matches.group(1);

        //let's extrapolate the description
        regex = "<description>(.*?)</description>";
        pt = Pattern.compile(regex);
        matches = pt.matcher(data);

        if (matches.find())
        {
            m_type = type;
            //set up description regardless of work type
            m_description = matches.group(1);

            if (type == WorkType.Planned) {
                //extract dates and info
                String fullDes = m_description;
                String[] sections = fullDes.split("&lt;br /&gt;");

                //dates: start date
                m_startDate = sections[0].substring(12); //remove "Start Date: " from the string
                //Log.e("Start Date", m_startDate);
                //get the actual date values
                String dateFormat = "EEEEE, dd MMMMM yyyy - HH:mm"; //from the table in: https://stackoverflow.com/questions/4772425/change-date-format-in-a-java-string
                SimpleDateFormat format = new SimpleDateFormat(dateFormat, Locale.UK);
                try {
                    m_startingDate = format.parse(m_startDate); //get the date from the string
                } catch (ParseException e) {
                    e.printStackTrace();
                }

                //dates: end date
                m_endDate = sections[1].substring(10); //remove "End Date: " from the string
                //Log.e("End Date:", m_endDate);
                //get the actual date values
                String endDateFormat = "EEEEE, dd MMMMM yyyy - HH:mm"; //from the table in: https://stackoverflow.com/questions/4772425/change-date-format-in-a-java-string
                SimpleDateFormat endFormat = new SimpleDateFormat(endDateFormat, Locale.UK);
                try {
                    m_endingDate = endFormat.parse(m_endDate); //get the date from the string
                } catch (ParseException e) {
                    e.printStackTrace();
                }

                //description is just the last section for planned roadworks
                if (sections.length > 2) {
                    m_description = sections[2];
                }
                else {
                    m_description = "No other details available.";
                }
            }
        }

        //we now check for the published date if we are checking incidents
        if (type == WorkType.Incident)
        {
            //now get the date
            regex = "<pubDate>(.*?)</pubDate>";
            pt = Pattern.compile(regex);
            matches = pt.matcher(data);

            if (matches.find())
            {
                regex = "(.*?) GMT";
                pt = Pattern.compile(regex);
                matches = pt.matcher(matches.group(1));

                if (matches.find())
                {
                    m_startDate = matches.group(1);
                    //parse into date
                    String dateFormat = "EEE, d MMM yyyy HH:mm:ss"; //from the table in: https://stackoverflow.com/questions/4772425/change-date-format-in-a-java-string
                    SimpleDateFormat format  = new SimpleDateFormat(dateFormat, Locale.UK);
                    try {
                        m_startingDate = format.parse(m_startDate);
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    public boolean IsWithin(int day, int month, int year)
    {
        //create date from data
        Calendar date = new GregorianCalendar(year, month, day);

        if (m_type == WorkType.Planned)
        {
            //Log.e("is within", String.valueOf(date.getTime().before(m_endingDate) && date.getTime().after(m_startingDate)));
            return (date.getTime().before(m_endingDate) && date.getTime().after(m_startingDate));
        }
        else
        {
            return (date.get(Calendar.YEAR) == Calendar.getInstance(Locale.UK).get(Calendar.YEAR) && date.get(Calendar.MONTH) == Calendar.getInstance(Locale.UK).get(Calendar.MONTH) && date.get(Calendar.DAY_OF_MONTH) == Calendar.getInstance(Locale.UK).get(Calendar.DAY_OF_MONTH)); //incidents are on the feed only for the current day
        }
    }

    public int GetDurationInDays()
    {
        int days = daysBetween(m_startingDate, m_endingDate);
        return days;
    }

    public int daysBetween(Date d1, Date d2) //from: https://stackoverflow.com/questions/7103064/java-calculate-the-number-of-days-between-two-dates
    {
        if (d1 != null && d2 != null)
        {
            int days = (int)( (d2.getTime() - d1.getTime()) / (1000 * 60 * 60 * 24));
            return days;
        }
        return -1;
    }
}
