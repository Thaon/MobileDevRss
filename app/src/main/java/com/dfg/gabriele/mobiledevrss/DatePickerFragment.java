package com.dfg.gabriele.mobiledevrss;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.widget.DatePicker;
import android.widget.Toast;

import java.util.Calendar;

//from the date picker tutorial at: https://neurobin.org/docs/android/android-date-picker-example/
public class DatePickerFragment extends DialogFragment
        implements DatePickerDialog.OnDateSetListener {

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the current date as the default date in the picker
        final Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);

        // Create a new instance of DatePickerDialog and return it
        return new DatePickerDialog(getActivity(), this, year, month, day);
    }

    public void onDateSet(DatePicker view, int year, int month, int day) {
        // Do something with the date chosen by the user
        //TextView tv1= (TextView) getActivity().findViewById(R.id.textview1);
        //tv1.setText("Year: "+view.getYear()+" Month: "+view.getMonth()+" Day: "+view.getDayOfMonth());

        Context context = getActivity().getApplicationContext();
        CharSequence text = "Year: "+view.getYear()+" Month: "+view.getMonth()+" Day: "+view.getDayOfMonth();
        int duration = Toast.LENGTH_SHORT;

        Toast toast = Toast.makeText(context, text, duration);
        toast.show();
    }
}