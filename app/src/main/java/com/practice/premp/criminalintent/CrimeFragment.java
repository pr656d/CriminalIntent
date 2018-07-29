package com.practice.premp.criminalintent;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;

import java.util.Date;
import java.util.UUID;

public class CrimeFragment extends Fragment {

    // TAGS
    private String TAG = "CrimeFragment";

    // CONSTANTS
    private static final String ARG_CRIME_ID = "crime_id";
    private static final String DIALOG_DATE = "DialogDate";
    private static final String DIALOG_DELETE = "DeleteCrime";

    // REQUEST CODES
    private static final int DATE_REQUEST_CODE = 0;
    private static final int DELETE_REQUEST_CODE = 1;
    public static final int SUSPECT_REQUEST_CODE = 2;

    // Declaration
    private Crime mCrime;
    private EditText mTitleField;
    private Button mDateButton;
    private Button mDeleteButton;
    private CheckBox mSolvedCheckBox;
    private Button mSuspectButton;
    private Button mReportButton;

    public static CrimeFragment newInstance(UUID crimeId) {
        // Creating CrimeFragment with crimeId arguments and returning new CrimeFragment.
        // Also called attaching arguments to a fragment.

        Bundle args = new Bundle();
        args.putSerializable(ARG_CRIME_ID, crimeId);

        CrimeFragment fragment = new CrimeFragment();
        fragment.setArguments(args);
        return fragment;

    } // newInstance() end.

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        UUID crimeId = (UUID) getArguments().getSerializable(ARG_CRIME_ID);
        mCrime = CrimeLab.get(getActivity()).getCrime(crimeId);

    }   // onCreate end.

    @Override
    public void onPause() {
        super.onPause();
        
        CrimeLab.get(getActivity())
                .updateCrime(mCrime);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        // Getting and Returning view
        View v = inflater.inflate(R.layout.fragment_crime, container, false);

        // Title textView
        mTitleField = v.findViewById(R.id.crime_title);
        mTitleField.setText(mCrime.getTitle());
        mTitleField.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // This space intentionally left blank.
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                mCrime.setTitle(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {
                // This space intentionally left blank.
            }

        }); // mTitleField.addTextChangedListener end.

        // Date button
        mDateButton = v.findViewById(R.id.crime_date);
        updateDate();
        mDateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager manager = getFragmentManager();
                DatePickerFragment dialog = DatePickerFragment.newInstance(mCrime.getDate());
                dialog.setTargetFragment(CrimeFragment.this, DATE_REQUEST_CODE);
                dialog.show(manager, DIALOG_DATE);
            }
        });

        // Solved check button
        mSolvedCheckBox = v.findViewById(R.id.crime_solved);
        mSolvedCheckBox.setChecked(mCrime.isSolved());
        mSolvedCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                mCrime.setSolved(isChecked);
            }
        }); // mSolvedCheckButton end.

        // Delete button
        mDeleteButton = v.findViewById(R.id.crime_delete);
        mDeleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager manager = getFragmentManager();
                DeleteCrimeFragment dialog = DeleteCrimeFragment.newIntent(mCrime.getId());
                dialog.setTargetFragment(CrimeFragment.this, DELETE_REQUEST_CODE);
                dialog.show(manager, DIALOG_DELETE);
            }
        });

        // Suspect Button
        final Intent pickContact = new Intent(Intent.ACTION_PICK,
                ContactsContract.Contacts.CONTENT_URI);
        mSuspectButton = v.findViewById(R.id.crime_suspect);
        mSuspectButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivityForResult(pickContact, SUSPECT_REQUEST_CODE);
            }
        });
        // Check if we have already suspect in data.
        if (mCrime.getSuspect() != null) {
            mSuspectButton.setText(mCrime.getSuspect());
        }

        // Check if any contacts app available or not to choose contact.
        // Disable suspect button if no app available for selecting contacts.
        PackageManager packageManager = getActivity().getPackageManager();
        if (packageManager.resolveActivity(pickContact,
                PackageManager.MATCH_DEFAULT_ONLY) == null) {
            mSuspectButton.setEnabled(false);
        }

        // Report Button.
        mReportButton = v.findViewById(R.id.crime_report);
        mReportButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Intent.ACTION_SEND);
                i.setType("text/plain");
                i.putExtra(Intent.EXTRA_TEXT, getCrimeReport());
                i.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.crime_report_subject));
                // Always shows chooser while sharing using createChooser().
                i = Intent.createChooser(i, getString(R.string.send_report));
                startActivity(i);
            }
        });

        return v;

    }   // onCreateView end.

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode != Activity.RESULT_OK) {
            return;
        }

        switch (requestCode) {

            // Date result.
            case DATE_REQUEST_CODE:
                Date date = (Date) data.getSerializableExtra(DatePickerFragment.EXTRA_DATE);
                mCrime.setDate(date);
                updateDate();
                break;

            case DELETE_REQUEST_CODE:
                getActivity().finish();
                break;

            case SUSPECT_REQUEST_CODE:
                Uri contactUri = data.getData();
                // Specify which fields you want your query to return values for
                String[] queryFields = new String[] {
                        ContactsContract.Contacts.DISPLAY_NAME
                };
                // Perform your query - the contactUri is like where clause here
                Cursor c = getActivity().getContentResolver()
                        .query(contactUri, queryFields, null, null, null);

                try {
                    // Double check for actually get result.
                    if (c.getCount() == 0) {
                        return;
                    }
                    // Pull out first column of the first row of data that is our suspect name
                    c.moveToFirst();
                    String suspect = c.getString(0);
                    mCrime.setSuspect(suspect);
                    mSuspectButton.setText(suspect);
                } finally {
                    c.close();
                }

                break;
        }
    } // onActivityResult() end.

    // update date.
    private void updateDate() {
        mDateButton.setText(mCrime.getDate().toString());
    } // update() end.

    // This will create and return report of crime in text format.
    private String getCrimeReport() {
        String solvedString = null;
        if (mCrime.isSolved()) {
            solvedString = getString(R.string.crime_report_solved);
        } else {
            solvedString = getString(R.string.crime_report_unsolved);
        }

        String dateFormat = "EEE, MMM, dd";
        String dateString = (String) DateFormat.format(dateFormat, mCrime.getDate());

        String suspect = mCrime.getSuspect();
        if (suspect == null) {
            suspect = getString(R.string.crime_report_no_suspect);
        } else {
            suspect = getString(R.string.crime_report_suspect);
        }

        String report = getString(R.string.crime_report,
                mCrime.getTitle(), dateString, solvedString, suspect);

        return report;

    } // getCrimeReport() end.
}
