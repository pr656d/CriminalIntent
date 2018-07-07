package com.practice.premp.criminalintent;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

public class CrimeListFragment extends Fragment {

    // TAGS
    private String TAG = "CrimeListFragment";

    // Declarations
    private RecyclerView mCrimeRecyclerView;
    private CrimeAdapter mAdapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        Log.d(TAG, ".onCreateView called.");

        View view = inflater.inflate(R.layout.fragment_crime_list, container, false);
        mCrimeRecyclerView = view.findViewById(R.id.crime_recycler_view);
        mCrimeRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        updateUI();

        return view;
    } // onCreateView end.

    @Override
    public void onResume() {
        super.onResume();
        updateUI();
    } // onResume() end.

    private void updateUI() {
        // Updates UI when fragment starts or data changes.
        Log.d(TAG, ".updateUI() called.");

        CrimeLab crimeLab = CrimeLab.get(getActivity());
        List<Crime> crimes = crimeLab.getCrimes();

        if (mAdapter == null) {
            mAdapter = new CrimeAdapter(crimes);
            mCrimeRecyclerView.setAdapter(mAdapter);
        } else {
            mAdapter.notifyDataSetChanged();
        }

    } // updateUI() end.

    private class CrimeHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        // Inner class Declarations
        private Crime mCrime;

        private TextView mTitleTextView;
        private TextView mDateTextView;
        private ImageView mSolved;

        public CrimeHolder(LayoutInflater inflater, ViewGroup parent) {
            super(inflater.inflate(R.layout.list_item_crime, parent, false));
            Log.d(TAG, ".CrimeHolder() called.");

            itemView.setOnClickListener(this);

            mTitleTextView = itemView.findViewById(R.id.crime_title);
            mDateTextView = itemView.findViewById(R.id.crime_date);
            mSolved = itemView.findViewById(R.id.is_solved);
        } // CrimeHolder() end.

        public void bind(Crime crime) {
            Log.d(TAG, ".bind() called.");

            mCrime = crime;
            mTitleTextView.setText(mCrime.getTitle());
            mDateTextView.setText(mCrime.getDate().toString());
            mSolved.setVisibility(crime.isSolved() ? View.INVISIBLE : View.VISIBLE);
        } // bind() end.

        @Override
        public void onClick(View v) {
            Log.d(TAG, ".onClick() called.");
            // This method will start CrimeActivity.
//            Intent intent = CrimeActivity.newIntent(getActivity(), mCrime.getId());

            // This method will start CrimePagerActivity.
            Intent intent = CrimePagerActivity.newIntent(getActivity(), mCrime.getId());
            startActivity(intent);
        } // onClick() end.

    } // CrimeHolder end.

    private class CrimeAdapter extends RecyclerView.Adapter<CrimeHolder> {
        private List<Crime> mCrimes;

        public CrimeAdapter(List<Crime> crimes) {
            Log.d(TAG, ".CrimeAdapter() called.");
            mCrimes = crimes;
        } // CrimeAdapter() end.

        @NonNull
        @Override
        public CrimeHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            Log.d(TAG, ".onCreateViewHolder() called.");

            LayoutInflater layoutInflater = LayoutInflater.from(getActivity());

            return new CrimeHolder(layoutInflater, parent);
        } // onCreateViewHolder() end.

        @Override
        public void onBindViewHolder(@NonNull CrimeHolder holder, int position) {
            Log.d(TAG, ".onBindViewHolder() called.");
            Crime crime = mCrimes.get(position);
            holder.bind(crime);
        } // onBindViewHolder() end.

        @Override
        public int getItemCount() {
            Log.d(TAG, ".getItemCount() called.");
            return mCrimes.size();
        } // getItemCount() end.

    } // CrimeAdapter end.
}
