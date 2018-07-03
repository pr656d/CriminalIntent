package com.practice.premp.criminalintent;

import android.support.v4.app.Fragment;
import android.util.Log;

public class CrimeListActivity extends SingleFragmentActivity {

    // TAGS
    private String TAG = "CrimeListActivity";

    @Override
    protected Fragment createFragment() {
        Log.d(TAG, ".createFragment() called.");
        return new CrimeListFragment();
    } // createFragment() end.
}
