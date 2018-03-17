//by Gabriele Maddaloni S1436255

package com.dfg.gabriele.mobiledevrss;

import android.util.Log;

import java.text.ParseException;
import java.text.SimpleDateFormat;
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
            //we now differentiate between incidents and planned road works
            if (type == WorkType.Incident)
            {
                m_description = matches.group(1);

                //now get the date
                regex = "<pubDate>(.*?)</pubDate>";
                pt = Pattern.compile(regex);
                matches = pt.matcher(data);

                if (matches.find())
                {
                    //remove the GMT
                    regex = "(.*?) GMT";
                    pt = Pattern.compile(regex);
                    matches = pt.matcher(data);

                    if (matches.find())
                    {
                        //parse into date
                        String dateFormat = "EEE, d MMM yyyy HH:mm:ss"; //from the table in: https://stackoverflow.com/questions/4772425/change-date-format-in-a-java-string
                        SimpleDateFormat format  = new SimpleDateFormat(dateFormat, Locale.UK);
                        try {
                            m_startingDate = format.parse(matches.group(1));
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
            else
            {
                //extract dates and info
                String fullDes = matches.group(1);
                String[] sections = fullDes.split("&lt;br /&gt;");

                //dates: start date
                regex = "Start Date: (.*?)";
                pt = Pattern.compile(regex);
                matches = pt.matcher(sections[0]); //start date
                if (matches.find()) {
                    m_startDate = matches.group(1);
                    //get the actual date values
                    String dateFormat = "EEE, dd MMMMM yyyy - HH:mm"; //from the table in: https://stackoverflow.com/questions/4772425/change-date-format-in-a-java-string
                    SimpleDateFormat format  = new SimpleDateFormat(dateFormat, Locale.UK);
                    try {
                        m_startingDate = format.parse(m_startDate); //get the date from the string
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                }

                //dates: end date
                regex = "End Date: (.*?)";
                pt = Pattern.compile(regex);
                matches = pt.matcher(sections[1]); //end date
                if (matches.find()) {
                    m_endDate = matches.group(1);
                    //get the actual date values
                    String dateFormat = "EEE, dd MMMMM yyyy - HH:mm"; //from the table in: https://stackoverflow.com/questions/4772425/change-date-format-in-a-java-string
                    SimpleDateFormat format  = new SimpleDateFormat(dateFormat, Locale.UK);
                    try {
                        m_endingDate = format.parse(m_endDate); //get the date from the string
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                }

                //description is just the last section
                if (sections.length > 2)
                    m_description = sections[2];
                else
                    m_description = "No other details available.";

            }
        }
    }

    public boolean IsWithin(int day, int month, int year)
    {
        //create date from data
        Date date = new GregorianCalendar(year, month, day).getTime();

        return (date.before(m_endingDate) && date.after(m_startingDate));
    }

    public int GetDurationInDays()
    {
        return daysBetween(m_startingDate, m_endingDate);
    }

    public int daysBetween(Date d1, Date d2) //from: https://stackoverflow.com/questions/7103064/java-calculate-the-number-of-days-between-two-dates
    {
        if (d1 != null && d2 != null)
            return (int)( (d2.getTime() - d1.getTime()) / (1000 * 60 * 60 * 24));
        return -1;
    }
}
