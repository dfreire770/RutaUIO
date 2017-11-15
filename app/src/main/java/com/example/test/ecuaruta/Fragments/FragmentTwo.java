package com.example.test.ecuaruta.Fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.example.test.ecuaruta.Activities.MainActivity;
import com.example.test.ecuaruta.Logic.CustomAdapterHistorial;
import com.example.test.ecuaruta.R;
import com.example.test.ecuaruta.SQLite.SQLiteHelper;

/**
 * Created by Diego on 10/8/2015.
 */
public class FragmentTwo extends Fragment {

    public FragmentTwo() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_historial, container, false);
        ListView lv = (ListView)rootView.findViewById(R.id.listViewH);

        SQLiteHelper sqLiteHelper = new SQLiteHelper(this.getActivity());
        String [] resultado = sqLiteHelper.getLista("historial");

        if(resultado!=null) {
            lv.setAdapter(new CustomAdapterHistorial((MainActivity) this.getActivity(), resultado, null));
        }


        return rootView;
    }

}