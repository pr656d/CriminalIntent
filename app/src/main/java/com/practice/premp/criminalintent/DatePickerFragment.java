package com.practice.premp.criminalintent;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.DatePicker;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

public class DatePickerFragment extends DialogFragment {

    // CONSTANTS
    public static final String EXTRA_DATE = "com.practice.premp.criminalintent.date";
    private static final String ARG_DATE = "date";

    // Declarations
    private DatePicker mDatePicker;

    // Creating new intent.
    public static DatePickerFragment newInstance(Date date) {
        Bundle args = new Bundle();
        args.putSerializable(ARG_DATE, date);

        DatePickerFragment fragment = new DatePickerFragment();
        fragment.setArguments(args);
        return fragment;

    } // newInstance() end.

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        // Check for nullPointerException for getting arguments.
        Date date = new Date();
        if (getArguments() != null) {
            date = (Date) getArguments().getSerializable(ARG_DATE);
        }

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DATE);

        // Creating new view of date_picker.xml
        View v = LayoutInflater.from(getActivity()).inflate(R.layout.date_picker, null);

        mDatePicker = v.findViewById(R.id.dialog_date_picker);
        mDatePicker.init(year, month, day, null);

        // Creating and returning alert dialog.
        return new AlertDialog.Builder(getActivity())
                .setView(v)
                .setTitle(R.string.date_picker_title)
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Date date = new GregorianCalendar(
                                mDatePicker.getYear(),
                                mDatePicker.getMonth(),
                                mDatePicker.getDayOfMonth()
                        ).getTime();
                        sendResult(Activity.RESULT_OK, date);
                    }
                })
                .create();

    } // onCreateDialog() end.

    private void sendResult(int resultCode, Date date) {
        if (getTargetFragment() == null) {
            return;
        }

        Intent intent = new Intent();
        intent.putExtra(EXTRA_DATE, date);

        getTargetFragment().onActivityResult(getTargetRequestCode(), resultCode, intent);
    } // sendResult() end.
}
