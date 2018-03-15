//by Gabriele Maddaloni S1436255

package com.dfg.gabriele.mobiledevrss;

import android.util.Log;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RssItem {
    public enum WorkType {RoadWork, Planned, Incident};

    //members
    public String m_title, m_description, m_publishedDate, m_startDate, m_endDate;
    public int m_sDay, m_sMonth, m_sYear, m_eDay, m_eMonth, m_eYear;
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
        {
            //extract dates and info
            String fullDes = matches.group(1);
            String[] sections = fullDes.split("&lt;br /&gt;");

            //dates: start date
            regex = ", (.*?) -"; //just after the day
            pt = Pattern.compile(regex);
            matches = pt.matcher(sections[0]); //start date
            if (matches.find())
            {
                m_startDate = matches.group(1);
                //get the actual date values
                String[] dateValues = m_startDate.split(" ");
                m_sDay = Integer.valueOf(dateValues[0]);
                m_sMonth = GetMonthFromString(dateValues[1]);
                m_sYear = Integer.valueOf(dateValues[2]);
            }

            //dates: end date, we use the same regex on a different string
            matches = pt.matcher(sections[1]); //end date
            if (matches.find())
            {
                m_endDate = matches.group(1);
                //get the actual date values
                String[] dateValues = m_endDate.split(" ");
                m_eDay = Integer.valueOf(dateValues[0]);
                m_eMonth = GetMonthFromString(dateValues[1]);
                m_eYear = Integer.valueOf(dateValues[2]);
            }

            //description is just the last section
            if (sections.length > 2)
                m_description = sections[2];
            else
                m_description = "No other details available.";
        }
    }

    private String GetMonthFromInt(int monthID)
    {
        String[] months = {"January", "February", "March", "April", "May", "June", "July", "August", "September", "October", "November", "December"};
        return months[monthID];
    }

    private int GetMonthFromString(String month)
    {
        String[] months = {"January", "February", "March", "April", "May", "June", "July", "August", "September", "October", "November", "December"};
        //Log.e("Month checking:", month);

        //remove unwanted spaces
        month = month.trim();

        for (int i = 0; i < months.length; i++)
        {
            if (months[i].equals(month))
                return i;
        }
        return -1; //fail state
    }

    public boolean IsWithin(int day, int month, int year)
    {
        //Log.e("Date", "Date: " + day + ", " + month + ", " + year + " checked against: " + m_sDay + ", "+ m_sMonth + ", "+ m_sYear + ", ending at: "+ m_eDay + ", "+ m_eMonth + ", "+ m_eYear);
        if (year > m_sYear && year <= m_eYear) //only check for the end date in this case
            if (m_eMonth >= month)
                if (m_sDay <= day && m_eDay >= day)
                    return true;

        if (year >= m_sYear && year <= m_eYear)
            if (m_sMonth <= month && m_eMonth >= month)
                if (m_sDay <= day && m_eDay >= day)
                    return true;

        return false;
    }

    public int GetDurationInDays()
    {
        int days = (m_eYear - m_sYear) * 365;
        int months = 0;
        if (m_sMonth > m_eMonth)
            months += 12;
        months += m_eMonth - m_sMonth;
        days += months * 30; //we average the amount of days each month has to 30 as an approximation is ok for us
        if (m_sDay > m_eDay)
            days += 30;
        days += m_eDay - m_sDay;

        return days;
    }
}
