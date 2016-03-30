package com.zapporoo.nighthawk.ui.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.parse.ParseException;
import com.parse.SaveCallback;
import com.zapporoo.nighthawk.R;
import com.zapporoo.nighthawk.model.ModUser;
import com.zapporoo.nighthawk.util.ParseExceptionHandler;
import com.zapporoo.nighthawk.util.XxUtil;

/**
 * Created by Pile on 12/15/2015.
 */

public class Fragment_Business_Home_HotDeals extends FragmentDefault {
    /**
     * The fragment argument representing the section number for this
     * fragment.
     */
    private static final String ARG_SECTION_NUMBER = "section_number";
    EditText etHotDeals;
    EditText etHotDealsTitle;
    Button btnBusinessHotDealsUpdate;

    public Fragment_Business_Home_HotDeals() {
    }

    /**
     * Returns a new instance of this fragment for the given section
     * number.
     */
    public static Fragment_Business_Home_HotDeals newInstance(int sectionNumber) {
        Fragment_Business_Home_HotDeals fragment = new Fragment_Business_Home_HotDeals();
        Bundle args = new Bundle();
        args.putInt(ARG_SECTION_NUMBER, sectionNumber);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_business_hot_deals, container, false);
        setupGui(rootView);
        return rootView;
    }

    private void setupGui(View view){
        etHotDeals = (EditText) view.findViewById(R.id.etHotDeals);
        etHotDealsTitle = (EditText) view.findViewById(R.id.etHotDealsTitle);
        setHotDealsValues();
        btnBusinessHotDealsUpdate = (Button) view.findViewById(R.id.btnBusinessHotDealsUpdate);
        btnBusinessHotDealsUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkAndUpdateHotDeals();
            }
        });
    }

    private void setHotDealsValues() {
        if (ModUser.getModUser() != null){
            etHotDeals.setText(ModUser.getModUser().getHotDeals());
            etHotDealsTitle.setText(ModUser.getModUser().getHotDealsTitle());
        }
    }


    private void checkAndUpdateHotDeals() {
        if (XxUtil.isValid(etHotDeals) && XxUtil.isValid(etHotDealsTitle) && isNetworkAvailable()){
            ModUser.getModUser().setHotDeals(etHotDeals.getText().toString());
            ModUser.getModUser().setHotDealsTitle(etHotDealsTitle.getText().toString());

            showProgressDialog(getString(R.string.progress_saving));
            ModUser.getModUser().saveInBackground(new SaveCallback() {
                @Override
                public void done(ParseException e) {
                    dismissProgressDialog();
                    if (e == null){
                        showToast(getString(R.string.alert_update_sucess));
                    }else
                        ParseExceptionHandler.handleParseError(e, getActivity(), getString(R.string.error_server_error));
                }
            });
        }

    }


}