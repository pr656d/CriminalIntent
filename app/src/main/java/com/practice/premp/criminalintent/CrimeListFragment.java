package com.practice.premp.criminalintent;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;
import java.util.UUID;

public class CrimeListFragment extends Fragment {

    // TAGS
    private String TAG = "CrimeListFragment";

    // CONSTANTS
    private static final String SAVED_SUBTITLE_VISIBLE = "subtitle";

    // Declarations
    private RecyclerView mCrimeRecyclerView;
    private CrimeAdapter mAdapter;
    private boolean mSubtitleVisible;
    private CallBacks mCallBacks;

    /**
     * Required interface for hosting activities
     */

    public interface CallBacks {
        void onCrimeSelected(Crime crime);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mCallBacks = (CallBacks) context;
    } // onAttach() end.

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    } // onCreate() end.

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
//        Log.d(TAG, ".onCreateView called.");

        View view = inflater.inflate(R.layout.fragment_crime_list, container, false);
        mCrimeRecyclerView = view.findViewById(R.id.crime_recycler_view);
        mCrimeRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        if (savedInstanceState != null) {
            mSubtitleVisible = savedInstanceState.getBoolean(SAVED_SUBTITLE_VISIBLE);
        }

        updateUI();

        // Swipe to delete item from list.
        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(
                        0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
                UUID uuid = (UUID) viewHolder.itemView.getTag();
                CrimeLab.get(getActivity()).deleteCrime(uuid);
                updateUI();
            }
        }).attachToRecyclerView(mCrimeRecyclerView);

        return view;
    } // onCreateView end.

    @Override
    public void onResume() {
        super.onResume();
        updateUI();
    } // onResume() end.

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean(SAVED_SUBTITLE_VISIBLE, mSubtitleVisible);
    } // onSaveInstanceState() end.

    @Override
    public void onDetach() {
        super.onDetach();
        mCallBacks = null;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.fragment_crime_list, menu);

        MenuItem subtitleItem = menu.findItem(R.id.show_subtitle);
        if (mSubtitleVisible) {
            subtitleItem.setTitle(R.string.hide_subtitle);
        } else {
            subtitleItem.setTitle(R.string.show_subtitle);
        }

    } // onCreateOptionsMenu() end.

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.new_crime:
                Crime crime = new Crime();
                CrimeLab.get(getActivity()).addCrime(crime);
                updateUI();
                mCallBacks.onCrimeSelected(crime);
                return true;

            case R.id.show_subtitle:
                mSubtitleVisible = !mSubtitleVisible;
                getActivity().invalidateOptionsMenu();
                updateSubtitle();
                return true;

                default:
                    return super.onOptionsItemSelected(item);
        }
    } // onOptionsItemSelected() end.

    private void updateSubtitle() {
        // Shows total number of crimes as subtitle.

        CrimeLab crimeLab = CrimeLab.get(getActivity());
        int crimeCount = crimeLab.getCrimes().size();
        String subtitle = getResources()
                .getQuantityString(R.plurals.subtitle_plurals, crimeCount, crimeCount);

        if (!mSubtitleVisible) {
            subtitle = null;
        }

        AppCompatActivity activity = (AppCompatActivity) getActivity();
        activity.getSupportActionBar().setSubtitle(subtitle);

    } // updateSubtitle() end.

    public void updateUI() {
        // Updates UI when fragment starts or data changes.
//        Log.d(TAG, ".updateUI() called.");

        CrimeLab crimeLab = CrimeLab.get(getActivity());
        List<Crime> crimes = crimeLab.getCrimes();

        if (mAdapter == null) {
            mAdapter = new CrimeAdapter(crimes);
            mCrimeRecyclerView.setAdapter(mAdapter);
        } else {
            mAdapter.setCrimes(crimes);
            mAdapter.notifyDataSetChanged();
        }

        updateSubtitle();

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
//            Log.d(TAG, ".bind() called.");

            mCrime = crime;
            mTitleTextView.setText(mCrime.getTitle());
            mDateTextView.setText(mCrime.getDate().toString());
            mSolved.setVisibility(crime.isSolved() ? View.INVISIBLE : View.VISIBLE);
            itemView.setTag(mCrime.getId());
        } // bind() end.

        @Override
        public void onClick(View v) {
//            Log.d(TAG, ".onClick() called.");
            mCallBacks.onCrimeSelected(mCrime);
        } // onClick() end.

    } // CrimeHolder end.

    private class CrimeAdapter extends RecyclerView.Adapter<CrimeHolder> {
        private List<Crime> mCrimes;

        public CrimeAdapter(List<Crime> crimes) {
//            Log.d(TAG, ".CrimeAdapter() called.");
            mCrimes = crimes;
        } // CrimeAdapter() end.

        @NonNull
        @Override
        public CrimeHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
//            Log.d(TAG, ".onCreateViewHolder() called.");

            LayoutInflater layoutInflater = LayoutInflater.from(getActivity());

            return new CrimeHolder(layoutInflater, parent);
        } // onCreateViewHolder() end.

        @Override
        public void onBindViewHolder(@NonNull CrimeHolder holder, int position) {
//            Log.d(TAG, ".onBindViewHolder() called.");
            Crime crime = mCrimes.get(position);
            holder.bind(crime);
        } // onBindViewHolder() end.

        @Override
        public int getItemCount() {
//            Log.d(TAG, ".getItemCount() called.");
            return mCrimes.size();
        } // getItemCount() end.

        public void setCrimes(List<Crime> crimes) {
            mCrimes = crimes;
        }

    } // CrimeAdapter end.
}
