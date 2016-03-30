package com.zapporoo.nighthawk.ui.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.zapporoo.nighthawk.R;

/**
 * Created by Pile on 12/15/2015.
 */

public class Fragment_Personal_Home_Search_MyGroup extends Fragment {
    /**
     * The fragment argument representing the section number for this
     * fragment.
     */
    private static final String ARG_SECTION_NUMBER = "section_number";

    public Fragment_Personal_Home_Search_MyGroup() {
    }

    /**
     * Returns a new instance of this fragment for the given section
     * number.
     */
    public static Fragment_Personal_Home_Search_MyGroup newInstance(int sectionNumber) {
        Fragment_Personal_Home_Search_MyGroup fragment = new Fragment_Personal_Home_Search_MyGroup();
        Bundle args = new Bundle();
        args.putInt(ARG_SECTION_NUMBER, sectionNumber);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_personal_home_messages, container, false);
        return rootView;
    }
}