package com.practice.premp.criminalintent;

import android.content.Context;
import android.content.Intent;
import android.support.v4.app.Fragment;

import java.util.UUID;

public class CrimeActivity extends SingleFragmentActivity {

    // CONSTANTS
    private static final String EXTRA_CRIME_ID = "com.practice.premp.criminalintent.crime_id";

    // Creating and returning new intent.
    public static Intent newIntent(Context packageContext, UUID crimeId) {
        // Creating new intent with putting crimeId and returning new intent.

        Intent intent = new Intent(packageContext, CrimeActivity.class);
        intent.putExtra(EXTRA_CRIME_ID, crimeId);
        return intent;

    } // newIntent() end.

    @Override
    protected Fragment createFragment() {
        // Getting crimeId from intent and returning instance of CrimeFragment.

        UUID crimeId = (UUID) getIntent().getSerializableExtra(EXTRA_CRIME_ID);
        return CrimeFragment.newInstance(crimeId);

    } // createFragment() end.
}
