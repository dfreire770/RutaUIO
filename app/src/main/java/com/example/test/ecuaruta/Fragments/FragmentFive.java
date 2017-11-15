package com.example.test.ecuaruta.Fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.test.ecuaruta.R;

/**
 * Created by Diego on 10/8/2015.
 */
public class FragmentFive extends Fragment {

    public FragmentFive() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_denuncias, container, false);

        return rootView;
    }

}