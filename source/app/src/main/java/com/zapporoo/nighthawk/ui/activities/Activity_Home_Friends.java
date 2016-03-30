package com.zapporoo.nighthawk.ui.activities;


import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.quickblox.chat.model.QBDialog;
import com.quickblox.chat.model.QBDialogType;
import com.zapporoo.nighthawk.R;
import com.zapporoo.nighthawk.callbacks.IFriendsHomeActivity;
import com.zapporoo.nighthawk.model.ModUser;
import com.zapporoo.nighthawk.quickblox.conversation.chat.IChat;
import com.zapporoo.nighthawk.quickblox.util.UtilChat;

public class Activity_Home_Friends extends ActivityDefault implements IFriendsHomeActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_friends);
        setupGui();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);

        getMenuInflater().inflate(R.menu.menu_activity_home_friends, menu);

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

        showToolbarLogo(tb);  // default
    }

    @Override
    public void showInviteActivity() {
        startNextActivity(Activity_Friends_Invite.class);
    }

    @Override
    public void showGroupChatActivity() {
        Intent intent = new Intent(this, Activity_Messages_Group.class);
        QBDialog dialog = UtilChat.getDialog(ModUser.getModUser().getGroupChatId());
        if (dialog != null){
            Bundle bundle = new Bundle();
            bundle.putSerializable(IChat.EXTRA_DIALOG, dialog);
            intent.putExtras(bundle);
            startActivity(intent);
        }else{
            Bundle bundle = new Bundle();
            bundle.putString(UtilChat.KEY_PERSON_NAME, ModUser.getModUser().getPersonalName());
            intent.putExtras(bundle);
            startNextActivity(intent);
        }

    }
}