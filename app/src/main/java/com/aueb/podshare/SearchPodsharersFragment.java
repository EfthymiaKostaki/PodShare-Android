package com.aueb.podshare;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;

public class SearchPodsharersFragment extends Fragment {

    View view;

    public SearchPodsharersFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.search_podsharers_fragment, container, false);
        return view;
    }

}