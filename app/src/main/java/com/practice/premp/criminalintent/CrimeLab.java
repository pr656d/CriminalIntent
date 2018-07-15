package com.practice.premp.criminalintent;

import android.content.Context;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class CrimeLab {

    // static object declaration.
    private static CrimeLab sCrimeLab;

    // Data holder variable.
    private List<Crime> mCrimes;

    public static CrimeLab get(Context context) {
        // Initializes sCrimeLab object if already not exist.
        // If already object exist then returns that object.

        if (sCrimeLab == null) {
            sCrimeLab = new CrimeLab(context);
        }
        return sCrimeLab;

    } // get() end.

    private CrimeLab(Context context) {
        // Initialize mCrimes and load data into it.

        mCrimes = new ArrayList<>();

//        loadCrimes();

    } // CrimeLab() end.

    public void addCrime(Crime c) {
        mCrimes.add(c);
    }

    public List<Crime> getCrimes() {
        return mCrimes;
    }

    public Crime getCrime(UUID id) {
        // Returns crime object by id.

        for (Crime crime : mCrimes) {
            if (crime.getId().equals(id)) {
                return crime;
            }
        }

        return null;

    } // getCrime() end.

    public void deleteCrime(UUID crimeId) {
        // Finds crime by id and deletes at the index.
        for (int i = 0; i < mCrimes.size(); i++) {
            if (mCrimes.get(i).getId() == crimeId) {
                mCrimes.remove(i);
                break;
            }
        }
    } // deleteCrime() end.

    /**
     *
     * For adding crimes into list manually.

    private void loadCrimes() {
        // Adding crime object to mCrimes array.

        for (int i = 0; i < 100; i++) {
            Crime crime = new Crime();
            crime.setTitle("Crime #" + i);
            crime.setSolved(i % 2 == 0);    // Every other one
            mCrimes.add(crime);
        }

    } // loadCrimes() end.

     */
}
