package com.practice.premp.criminalintent;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.widget.Toast;

import java.util.UUID;

public class DeleteCrimeFragment extends DialogFragment {

    // CONSTANTS
    private static final String ARG_DELETE = "deleteCrime";

    // Declarations.
    private UUID mCrimeId;

    // creating new intent.
    public static DeleteCrimeFragment newIntent(UUID crimeId) {
        Bundle args = new Bundle();
        args.putSerializable(ARG_DELETE, crimeId);

        DeleteCrimeFragment fragment = new DeleteCrimeFragment();
        fragment.setArguments(args);
        return fragment;

    } // newIntent() end.

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        if (getArguments() != null) {
            mCrimeId = (UUID) getArguments().getSerializable(ARG_DELETE);
        }

        return new AlertDialog.Builder(getActivity())
                .setTitle(R.string.delete_message)
                .setPositiveButton(R.string.crime_delete, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        CrimeLab.get(getActivity()).deleteCrime(mCrimeId);
                        getTargetFragment().onActivityResult(
                                getTargetRequestCode(), Activity.RESULT_OK, null);
                    }
                })
                .setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Toast.makeText(getActivity(), "Cancelled", Toast.LENGTH_SHORT).show();
                    }
                })
                .create();
    }
}
