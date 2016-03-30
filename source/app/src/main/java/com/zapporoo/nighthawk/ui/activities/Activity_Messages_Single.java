package com.zapporoo.nighthawk.ui.activities;


import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.SaveCallback;
import com.quickblox.chat.model.QBDialogType;
import com.zapporoo.nighthawk.R;
import com.zapporoo.nighthawk.model.ModRelationship;
import com.zapporoo.nighthawk.model.ModUser;
import com.zapporoo.nighthawk.quickblox.conversation.Activity_Conversation_Host;
import com.zapporoo.nighthawk.quickblox.util.ExceptionChatUnimplemetedMethod;
import com.zapporoo.nighthawk.quickblox.util.UtilChat;
import com.zapporoo.nighthawk.util.ParseExceptionHandler;

import java.util.ArrayList;
import java.util.List;

public class Activity_Messages_Single extends Activity_Conversation_Host {
    private TextView tvToolbarTitle;
    private boolean isOnline = false;
    private ModUser mOpponentUser;
    Toolbar tb;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_messages_single);
        setupGui();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);

        getMenuInflater().inflate(R.menu.menu_activity_messages_single, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
         switch (item.getItemId()) {
            case R.id.action_add_friend: {
                showConfirmDialog(getString(R.string.confirm_add_to_friends), new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        addOpponentToFriends();
                    }
                });
                break;
            }
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected ArrayList<Integer> getParticipants() {
        Integer userChatId = getIntent().getExtras().getInt(UtilChat.KEY_USER_CHAT_ID);
        ArrayList<Integer> list = new ArrayList<>();
        list.add(userChatId);
        return list;
    }

    @Override
    protected QBDialogType getDialogType(){
        return QBDialogType.PRIVATE;
    }

    private void addOpponentToFriends() {
        showProgressDialog(getString(R.string.progress_saving));
        ModRelationship.queryFriendsAll().findInBackground(new FindCallback<ModRelationship>() {
            @Override
            public void done(List<ModRelationship> objects, ParseException e) {
                if (e == null){
                    if (objects.size() == 0)
                        addNewFriend();
                    else {
                        dismissProgressDialog();
                        showToast(getString(R.string.toast_already_friend));
                    }
                }else
                    ParseExceptionHandler.handleParseError(e, Activity_Messages_Single.this);
            }
        });
    }

    private void addNewFriend() {
        ModRelationship relationship = new ModRelationship();
        relationship.setFrom(ModUser.getModUser());
        relationship.setTo(mOpponentUser);
        relationship.setStatus(ModRelationship.VALUE_STATUS_FRIENDS);
        relationship.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                dismissProgressDialog();
                if (e == null){
                    showToast(getString(R.string.toast_added_friend));
                }else
                    ParseExceptionHandler.handleParseError(e, Activity_Messages_Single.this);
            }
        });
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

        showToolbarTitle(tb, "");  // default

        tvToolbarTitle = (TextView) tb.findViewById(R.id.tvToolbarTitle);
        setOnlineStatus(isOnline);
    }

    public void setOnlineStatus(boolean online) {
        setOnlineDrawable(online);
    }

    private void setOnlineDrawable(boolean online){
        if (tvToolbarTitle == null) {
            isOnline = online;
            return;
        }

        int drawableRes = online ? R.drawable.shape_profile_online : R.drawable.shape_profile_offline;
        tvToolbarTitle.setCompoundDrawablesWithIntrinsicBounds(0, 0, drawableRes, 0);
    }

    public void setOpponentUser(ModUser user) {
        mOpponentUser = user;
    }

    public void setTitleName(String personalName) {
        showToolbarTitle(tb, personalName);
    }
}