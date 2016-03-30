package com.zapporoo.nighthawk.ui.activities;


import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.zapporoo.nighthawk.R;
import com.zapporoo.nighthawk.callbacks.IFriendsHomeActivity;

public class Activity_Friends_Invite extends ActivityDefault {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friends_invite);
        setupGui();
    }

    private void setupGui() {
        Toolbar tb = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(tb);

        // Get the ActionBar here to configure the way it behaves.
        final ActionBar ab = getSupportActionBar();
        if(ab != null) {
            ab.setDisplayShowHomeEnabled(true); // show or hide the default home button
            ab.setDisplayHomeAsUpEnabled(true);
            ab.setDisplayShowCustomEnabled(true); // enable overriding the default toolbar layout
            ab.setDisplayShowTitleEnabled(false); // disable the default title element here (for centered title)
        }

        showToolbarTitle(tb, getString(R.string.title_activity_activity__friends__invite));  // default
    }

    @Override
    protected void onPause() {
        hideSoftKeyboard();
        super.onPause();
    }


}