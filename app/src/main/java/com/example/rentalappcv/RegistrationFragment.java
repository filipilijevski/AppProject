package com.example.rentalappcv;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.fragment.app.Fragment;

/**
 * A Fragment that inflates a layout resource specified by the caller.
 * Used for flexible or dynamic registration-related layouts.
 */

public class RegistrationFragment extends Fragment {

    private static final String ARG_LAYOUT_RES_ID = "layoutResId";

    public static RegistrationFragment newInstance(int layoutResId) {
        RegistrationFragment fragment = new RegistrationFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_LAYOUT_RES_ID, layoutResId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        int layoutResId = getArguments().getInt(ARG_LAYOUT_RES_ID);
        return inflater.inflate(layoutResId, container, false);
    }
}
