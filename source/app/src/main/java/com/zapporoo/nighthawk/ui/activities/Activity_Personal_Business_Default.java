package com.zapporoo.nighthawk.ui.activities;


import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.view.Menu;

import com.parse.ParseException;
import com.parse.SaveCallback;
import com.zapporoo.nighthawk.R;
import com.zapporoo.nighthawk.callbacks.IPersonalBusinessDetailsActivity;
import com.zapporoo.nighthawk.model.ModRating;
import com.zapporoo.nighthawk.model.ModUser;
import com.zapporoo.nighthawk.ui.fragments.Fragment_Personal_Business_Details;
import com.zapporoo.nighthawk.ui.fragments.Fragment_Personal_Business_Rate;
import com.zapporoo.nighthawk.util.ParseExceptionHandler;

public abstract class Activity_Personal_Business_Default extends ActivityDefault implements IPersonalBusinessDetailsActivity {

    private Toolbar mToolbarMain;
    private Fragment_Personal_Business_Details fBusinessDetails;
    public static ModUser mBusiness;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        return true;
    }

    protected void setupGui() {
        mToolbarMain = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbarMain);

        // Get the ActionBar here to configure the way it behaves.
        final ActionBar ab = getSupportActionBar();
        if(ab != null) {
            ab.setDisplayShowHomeEnabled(true); // show or hide the default home button
            ab.setDisplayHomeAsUpEnabled(true);
            ab.setDisplayShowCustomEnabled(true); // enable overriding the default toolbar layout
            ab.setDisplayShowTitleEnabled(false); // disable the default title element here (for centered title)
        }

        showToolbarTitle(mToolbarMain, "");  // default
    }

    @Override
    public void showCheckInForm(ModUser mBusinessData) {
        Activity_Personal_Business_Default.mBusiness = mBusinessData;
        Intent intent = new Intent(this, Activity_Personal_Business_Details.class);
        intent.putExtra(ModUser.EXTRA_BUSINESS_ID, mBusinessData.getObjectId());
        startNextActivity(intent);
    }

    @Override
    public void onBusinessRated(final ModRating rating) {
        hideSoftKeyboard();
        showProgressDialog(getString(R.string.progress_saving));
        rating.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                dismissProgressDialog();
                if (e == null) {
                    notifyFragmentToReloadReviewList(rating);
                } else
                    ParseExceptionHandler.handleParseError(e, Activity_Personal_Business_Default.this);
            }
        });
    }

    private void notifyFragmentToReloadReviewList(ModRating rating) {
        if (fBusinessDetails != null)
            fBusinessDetails.reloadAdapterLocal(rating);
    }

    @Override
    public void showReviewEmptyAlert() {
        showAlertDialog(getString(R.string.business_item_review_empty));
    }

    @Override
    public void showReviewTooLongAlert() {
        showAlertDialog(getString(R.string.business_item_review_too_long));
    }

    @Override
    public void showRateDlg(ModUser pBusinessData) {
        Fragment_Personal_Business_Rate.create(pBusinessData, getSupportFragmentManager());
    }

    @Override
    public ModUser getBusinessForDisplay() {
        Bundle bundle = getIntent().getExtras();
        String id = bundle.getString(ModUser.EXTRA_BUSINESS_ID, "a");
        ModUser ret = ModUser.getUserByIdFromCache(id);

        if (ret == null)
            ret = mBusiness;

        return ret;
    }

    @Override
    public void setFragment(Fragment_Personal_Business_Details pFragmentDetails) {
        this.fBusinessDetails = pFragmentDetails;
    }

    @Override
    protected void onResume() {
        hideSoftKeyboard();
        super.onResume();
    }
}