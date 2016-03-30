package com.zapporoo.nighthawk.ui.activities;


import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.zapporoo.nighthawk.R;
import com.zapporoo.nighthawk.callbacks.IPersonalBusinessActivity;
import com.zapporoo.nighthawk.model.ModUser;

public class Activity_Personal_HotDeals extends ActivityDefault implements IPersonalBusinessActivity {

    private Toolbar mToolbarMain;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_personal_hot_deals);
        setupGui();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);

        getMenuInflater().inflate(R.menu.menu_activity_personal_hot_deals, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
         switch (item.getItemId()) {
            case R.id.action_settings: {
                startNextActivity(Activity_Settings.class);
                break;
            }
        }
        return super.onOptionsItemSelected(item);
    }

    private void setupGui() {
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

        showToolbarTitle(mToolbarMain, getString(R.string.title_activity_activity__personal_hotdeals));  // default
    }

    @Override
    public void showBusinessDetails(ModUser business) {
        Activity_Personal_Business_Default.mBusiness = business;
        Intent intent = new Intent(this, Activity_Personal_Business_Details.class);
        intent.putExtra(ModUser.EXTRA_BUSINESS_ID, business.getObjectId());
        startNextActivity(intent);
    }
}