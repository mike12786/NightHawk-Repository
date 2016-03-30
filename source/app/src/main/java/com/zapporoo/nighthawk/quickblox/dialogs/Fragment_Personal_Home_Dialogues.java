package com.zapporoo.nighthawk.quickblox.dialogs;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ProgressBar;
import android.widget.Switch;

import com.parse.FunctionCallback;
import com.parse.GetCallback;
import com.parse.ParseCloud;
import com.parse.ParseException;
import com.parse.SaveCallback;
import com.quickblox.chat.model.QBDialog;
import com.quickblox.chat.model.QBDialogType;
import com.quickblox.core.QBEntityCallback;
import com.quickblox.core.exception.QBResponseException;
import com.quickblox.users.model.QBUser;
import com.zapporoo.nighthawk.R;
import com.zapporoo.nighthawk.model.ModUser;
import com.zapporoo.nighthawk.quickblox.Consts;
import com.zapporoo.nighthawk.quickblox.push.UtilPush;
import com.zapporoo.nighthawk.quickblox.util.UtilChat;
import com.zapporoo.nighthawk.quickblox.util.UtilDate;
import com.zapporoo.nighthawk.ui.fragments.Fragment_Personal_Home_Tab;
import com.zapporoo.nighthawk.util.NhLog;
import com.zapporoo.nighthawk.util.ParseExceptionHandler;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Pile on 12/15/2015.
 */

public class Fragment_Personal_Home_Dialogues extends Fragment_Personal_Home_Tab implements View.OnClickListener {
    private Toolbar mMessagesToolbar;
    private ProgressBar pbChatDialogues;
    private Switch swOnline;
    private View llSwitchWrap, vMessagesOverlay;

    /**
     * The fragment argument representing the section number for this
     * fragment.
     */
    private static final String ARG_SECTION_NUMBER = "section_number";

    public Fragment_Personal_Home_Dialogues() {
    }

    /**
     * Returns a new instance of this fragment for the given section
     * number.
     */
    public static Fragment_Personal_Home_Dialogues newInstance(int sectionNumber) {
        Fragment_Personal_Home_Dialogues fragment = new Fragment_Personal_Home_Dialogues();
        Bundle args = new Bundle();
        args.putInt(ARG_SECTION_NUMBER, sectionNumber);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_personal_home_messages, container, false);
        setHasOptionsMenu(true);

        mMessagesToolbar = (Toolbar) rootView.findViewById(R.id.tbrMessages);
        llSwitchWrap = mMessagesToolbar.findViewById(R.id.llSwitchWrap);
        llSwitchWrap.setOnClickListener(this);
//        llSwitchWrap.setOnTouchListener(new View.OnTouchListener() {
//
//            @Override
//            public boolean onTouch(View v, MotionEvent event){
//                return true;
//            }
//        });

        swOnline = (Switch) mMessagesToolbar.findViewById(R.id.swOnline);
        swOnline.setChecked(ModUser.getModUser().getMessagesEnabled());
        pbChatDialogues = (ProgressBar) rootView.findViewById(R.id.pbChatDialogues);

        vMessagesOverlay = rootView.findViewById(R.id.vMessagesOverlay);
        vMessagesOverlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });


        //mItems = Test.createDummyMessages();
        setupGuiListAndAdapter(rootView);

        vMessagesOverlay.setVisibility(View.VISIBLE);

        if (ModUser.getModUser().getMessagesEnabled()){
            vMessagesOverlay.setVisibility(View.GONE);
            startDialogsFetch(new QBUser(ModUser.getModUser().getChatUsername(), ModUser.getModUser().getChatPassword()));
        }

        return rootView;
    }

    @Override
    public void onResume() {
        swOnline.setChecked(ModUser.getModUser().getMessagesEnabled());
        showFetchedDialogues();

        //if (mAdapterMessages.getItemCount() < UtilChat.getDialogs().size() &&
        if (ModUser.getModUser().getMessagesEnabled()){
            vMessagesOverlay.setVisibility(View.GONE);
            if (!UtilChat.isChatSessionActive()
                    || !UtilChat.isUserLoggedIn())
                startDialogsFetch(new QBUser(ModUser.getModUser().getChatUsername(), ModUser.getModUser().getChatPassword()));
        }else{
            vMessagesOverlay.setVisibility(View.VISIBLE);
        }

        super.onResume();
    }

    @Override
    protected void dismissProgressDialogPrivate() {
        pbChatDialogues.setVisibility(View.GONE);
        super.dismissProgressDialogPrivate();
    }

    @Override
    protected void showProgressDialogPrivate() {
        pbChatDialogues.setVisibility(View.VISIBLE);
        super.showProgressDialogPrivate();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_activity_home_personal, menu);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.llSwitchWrap: {
                if (ModUser.getModUser().getMessagesEnabled()){
                    if (ModUser.getModUser().getShowMessageDialog())
                        showMessagesOnOffDialog();
                    else
                        setMessages(false);
                }else
                    setMessages(true);
                break;
            }
        }
    }

    public void showMessagesOnOffDialog() {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity());

        View alertView = LayoutInflater.from(getActivity()).inflate(R.layout.dialog_messages_on_off, null);
        Button btnOk = (Button) alertView.findViewById(R.id.btnDialogOk);
        Button btnCancel = (Button) alertView.findViewById(R.id.btnDialogCancel);
        final CheckBox cbDoNotShow = (CheckBox) alertView.findViewById(R.id.cbMessagesDoNotShow);

        alertDialogBuilder.setCancelable(true)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setView(alertView);

        final AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.setCanceledOnTouchOutside(true);
        alertDialog.show();

        btnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setMessages(false);
                ModUser.getModUser().setShowMessageDialog(!cbDoNotShow.isChecked());
                alertDialog.dismiss();
            }
        });
        btnCancel.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
            }
        });
    }

    private void setMessages(final boolean status) {
        showProgressDialog(getString(R.string.progress_saving));
        ModUser.getModUser().setMessagesEnabled(status);
        ModUser.getModUser().saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                dismissProgressDialog();
                try{
                    if (e == null) {
                        if (swOnline.isChecked() != ModUser.getModUser().getMessagesEnabled()) {
                            swOnline.toggle();
                        }
                        messagesSwitchChanged(status);
                    }else
                        ParseExceptionHandler.handleParseError(e, getActivity());
                }catch (Exception ex){
                    ModUser.getModUser().setShowMessageDialog(true);
                    ParseExceptionHandler.handleParseError(e, getActivity());
                }
            }
        });
    }

    private void messagesSwitchChanged(boolean messagesEnabled) {
        if (messagesEnabled) {
            vMessagesOverlay.setVisibility(View.GONE);
            startDialogsFetch(new QBUser(ModUser.getModUser().getChatUsername(), ModUser.getModUser().getChatPassword()));
        }else{
            vMessagesOverlay.setVisibility(View.VISIBLE);
        }

    }


    @Override
    public void onOpenDialogItem(QBDialog pDialog) {
        startConversationActivityFromDialog(pDialog);
    }

    @Override
    public void onLoadDialogItem(RecyclerView.ViewHolder vHolder, QBDialog dialog) {
        AdapterPersonalDialogues.ViewHolderMessages vhMessage = (AdapterPersonalDialogues.ViewHolderMessages) vHolder;
        vhMessage.tvName.setText(dialog.getName());
        vhMessage.tvMessage.setText(dialog.getLastMessage());

        long lastSent = dialog.getLastMessageDateSent();

        if (lastSent == 0)
            vhMessage.tvMessageTime.setText("-");
        else
            vhMessage.tvMessageTime.setText(UtilDate.timeSocialNetwork(lastSent));

        Integer opponentId = dialog.getLastMessageUserId();
        ModUser opponentUser = ModUser.getUserByChatIdFromCache(opponentId);

        if (opponentUser != null)
            fetchProfileImage(opponentUser, ((AdapterPersonalDialogues.ViewHolderMessages) vHolder).circProfile);
        else {
            ModUser.queryUserByChatId(opponentId).getFirstInBackground(new GetCallback<ModUser>() {
                @Override
                public void done(ModUser object, ParseException e) {
                    if (e == null) {
                        if (object != null) {
                            ModUser.addSinglePrivateCache(object);
                            refreshAdapter();
                        }
                    } else {

                    }
                }
            });
        }
    }

    @Override
    public void onDeleteDialogItem(final QBDialog currentItem) {
        String txt = "";
        boolean isOurDialog = false;
        boolean isGroup = false;



        if (currentItem.getType() == QBDialogType.GROUP) {
            isGroup = true;
            if (ModUser.getModUser().getGroupChatId() != null && ModUser.getModUser().getGroupChatId().equals(currentItem.getDialogId()))
                isOurDialog = true;

            if (isOurDialog)
                txt = getString(R.string.confirm_leave_and_remove_chat_group);
            else
                txt = getString(R.string.confirm_leave_chat_group);
        }

        if (currentItem.getType() == QBDialogType.PRIVATE) {
            isGroup = false;
            if (ModUser.getModUser().getChatId() == currentItem.getUserId())
                isOurDialog = true;

            if (isOurDialog)
                txt = getString(R.string.confirm_leave_and_remove_chat_single);
            else
                txt = getString(R.string.confirm_leave_chat_single);
        }

        final boolean isOurDialogFinal = isOurDialog;

        final boolean finalIsGroup = isGroup;
        showConfirmDialog(txt, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showProgressDialog(getString(R.string.progress_removing));
                if (isOurDialogFinal)
                    leaveAndDeleteDialog(currentItem);
                else
                     leaveDialog(finalIsGroup, currentItem);
            }
        });
    }

    private void leaveAndDeleteDialog(QBDialog currentItem) {
        final QBDialogType type = currentItem.getType();
        final String id = currentItem.getDialogId();

        UtilChat.deleteDialog(currentItem, new QBEntityCallback<QBDialog>() {
            @Override
            public void onSuccess(QBDialog dialog, Bundle bundle) {
                if (type == QBDialogType.GROUP) {
                    //ModUser.getModUser().setGroupChatId("");
                    ModUser.getModUser().saveInBackground(new SaveCallback() {
                        @Override
                        public void done(ParseException e) {
                            dismissProgressDialog();
                            if (e == null){
                                UtilChat.removeDialog(id);
                                showFetchedDialogues();
                            }
                        }
                    });
                }else{
                    dismissProgressDialog();
                    UtilChat.removeDialog(id);
                    showFetchedDialogues();
                }

            }

            @Override
            public void onError(QBResponseException e) {
                dismissProgressDialog();
            }
        });
    }

    private void leaveDialog(boolean isGroup, final QBDialog currentItem) {
        QBEntityCallback<QBDialog> callback = new QBEntityCallback<QBDialog>() {
            @Override
            public void onSuccess(QBDialog dialog, Bundle bundle) {
                if (currentItem.getType() == QBDialogType.GROUP)
                    removeMeFromTheGroup(currentItem.getDialogId());

                leaveComplete(currentItem);
            }

            @Override
            public void onError(QBResponseException e) {
                dismissProgressDialog();
                if (e.getHttpStatusCode() == 403) {
                    leaveComplete(currentItem);
                }
                NhLog.e("QB", "leaveDialog error!" + e.getErrors());
            }
        };



        if (isGroup)
            UtilChat.removeUserFromGroupChat(currentItem, ModUser.getModUser().getChatId(), callback);
        else
            UtilChat.deleteDialog_private(currentItem, new QBEntityCallback<Void>() {
                @Override
                public void onSuccess(Void aVoid, Bundle bundle) {
                    leaveComplete(currentItem);
                }

                @Override
                public void onError(QBResponseException e) {
                    dismissProgressDialog();
                    if (e.getHttpStatusCode() == 403) {
                        leaveComplete(currentItem);
                    }
                    NhLog.e("QB", "leaveDialog error!" + e.getErrors());
                }
            });
    }

    private void leaveComplete(QBDialog dialog) {
        dismissProgressDialog();
        UtilPush.sendPushNotificationInfoLeftDialog(dialog);
        UtilChat.removeDialog(dialog.getDialogId());
        showFetchedDialogues();
    }

    private void removeMeFromTheGroup(String dialogId) {
        Map<String, String> parameters = new HashMap<>();
        parameters.put("dialogId", dialogId);
        parameters.put("userId", ModUser.getModUser().getObjectId());
        removeMeCloud(parameters);
    }

    private static void removeMeCloud(Map<String, String> values_map){
        //TEST values_map.put(KEY_ALERT, "" + System.currentTimeMillis());
        ParseCloud.callFunctionInBackground("removeFromGroup", values_map, new FunctionCallback<String>() {
            public void done(String success, ParseException e) {
                if (e == null)
                    NhLog.e("cloud", "Push Cloud send success!" + success);
                else
                    NhLog.e("cloud", "Push Cloud send error " + e.getMessage());
            }
        });
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        LocalBroadcastManager.getInstance(getActivity()).registerReceiver(getBroadcastReceiver(), new IntentFilter(Consts.NEW_PUSH_EVENT_MESSAGE));
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onDestroy() {
        LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver(getBroadcastReceiver());
        super.onDestroy();
    }

    private BroadcastReceiver mPushReceiver;

    private BroadcastReceiver getBroadcastReceiver(){
        if (mPushReceiver == null)
            mPushReceiver = new BroadcastReceiver() {
                @Override
                public void onReceive(Context context, Intent intent) {

                    // Get extra data included in the Intent
                    String message = intent.getStringExtra(Consts.KEY_MESSAGE_TEXT);

                    showFetchedDialogues();
                    //refreshAdapter();
                }
            };
        return mPushReceiver;
    }

}