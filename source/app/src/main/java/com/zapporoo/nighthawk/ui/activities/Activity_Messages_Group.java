package com.zapporoo.nighthawk.ui.activities;


import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.quickblox.chat.model.QBDialogType;
import com.zapporoo.nighthawk.R;
import com.zapporoo.nighthawk.model.ModGroup;
import com.zapporoo.nighthawk.model.ModMyClub;
import com.zapporoo.nighthawk.model.ModUser;
import com.zapporoo.nighthawk.quickblox.conversation.Activity_Conversation_Host;
import com.zapporoo.nighthawk.quickblox.util.UtilChat;

import java.util.ArrayList;
import java.util.List;

public class Activity_Messages_Group extends Activity_Conversation_Host {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_messages_group);  // layout is the same as the one used in single chat.
        setupGui();
    }

    private void setupGui() {
        tb = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(tb);

        // Get the ActionBar here to configure the way it behaves.
        final ActionBar ab = getSupportActionBar();
        if(ab != null) {
            ab.setDisplayShowHomeEnabled(true); // show or hide the default home button
            ab.setDisplayHomeAsUpEnabled(true);
            ab.setDisplayShowCustomEnabled(true); // enable overriding the default toolbar layout
            ab.setDisplayShowTitleEnabled(false); // disable the default title element here (for centered title)
        }

        showToolbarTitle(tb, getString(R.string.title_activity_activity__messages_group));  // default
    }

    @Override
    protected ArrayList<Integer> getParticipants() {
        ArrayList<Integer> list = ModGroup.toIntegetList(ModGroup.getAllCached());
        return list;
    }

    @Override
    protected QBDialogType getDialogType(){
        return QBDialogType.GROUP;
    }

}