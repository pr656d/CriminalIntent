package com.practice.premp.criminalintent;

import android.content.Intent;
import android.support.v4.app.Fragment;
import android.util.Log;

public class CrimeListActivity extends SingleFragmentActivity
        implements CrimeListFragment.CallBacks, CrimeFragment.CallBacks {

    // TAGS
    private String TAG = "CrimeListActivity";

    @Override
    protected Fragment createFragment() {
            Log.d(TAG, ".createFragment() called.");
        return new CrimeListFragment();
    } // createFragment() end.

    @Override
    protected int getLayoutResId() {
        return R.layout.activity_masterdetail;
    } // getLayoutResId() end.

    @Override
    public void onCrimeSelected(Crime crime) {
        if (findViewById(R.id.detail_fragment_container) == null) {
            Intent intent = CrimePagerActivity.newIntent(this, crime.getId());
            startActivity(intent);
        } else {
            Fragment newDetail = CrimeFragment.newInstance(crime.getId());

            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.detail_fragment_container, newDetail)
                    .commit();
        }
    } // onCrimeSelected() end.\

    @Override
    public void onCrimeUpdated(Crime crime) {
        CrimeListFragment listFragment = (CrimeListFragment)
                getSupportFragmentManager().findFragmentById(R.id.fragment_container);
        listFragment.updateUI();
    } // onCrimeUpdated() end.

    @Override
    public void onCrimeDeleted() {
        CrimeFragment crimeFragment = (CrimeFragment)
                getSupportFragmentManager().findFragmentById(R.id.detail_fragment_container);

        getSupportFragmentManager().beginTransaction()
                .remove(crimeFragment)
                .commit();
    } // onCrimeDeleted() end.
}
