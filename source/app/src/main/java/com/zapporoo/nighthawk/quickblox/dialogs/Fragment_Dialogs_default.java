package com.zapporoo.nighthawk.quickblox.dialogs;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Looper;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;

import com.quickblox.chat.model.QBDialog;
import com.quickblox.chat.model.QBDialogType;
import com.quickblox.core.QBEntityCallback;
import com.quickblox.core.QBEntityCallbackImpl;
import com.quickblox.core.exception.QBResponseException;
import com.quickblox.users.model.QBUser;
import com.zapporoo.nighthawk.R;
import com.zapporoo.nighthawk.quickblox.Consts;
import com.zapporoo.nighthawk.quickblox.conversation.Activity_Conversation_Host;
import com.zapporoo.nighthawk.quickblox.push.PlayServicesHelper;
import com.zapporoo.nighthawk.quickblox.util.ChatService;
import com.zapporoo.nighthawk.quickblox.conversation.chat.IChat;
import com.zapporoo.nighthawk.quickblox.util.ExceptionChat;
import com.zapporoo.nighthawk.quickblox.util.ExceptionChatUnimplemetedMethod;
import com.zapporoo.nighthawk.quickblox.util.UtilChat;
import com.zapporoo.nighthawk.ui.activities.Activity_Messages_Group;
import com.zapporoo.nighthawk.ui.activities.Activity_Messages_Single;
import com.zapporoo.nighthawk.ui.fragments.FragmentDefault;
import com.zapporoo.nighthawk.ui.views.SpacesItemDecoration;
import com.zapporoo.nighthawk.util.NhLog;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Pile on 2/5/2016.
 */
public class Fragment_Dialogs_default extends FragmentDefault implements IDialogItemCallback{

    protected RecyclerView rvMessages;
    protected AdapterPersonalDialogues mAdapterMessages;
    protected List<QBDialog> mItems;

    protected void setupGuiListAndAdapter(View rootView){
        mAdapterMessages = new AdapterPersonalDialogues(getQbItems(), this);

        rvMessages = (RecyclerView) rootView.findViewById(R.id.rvMessages);
        rvMessages.setLayoutManager(new LinearLayoutManager(rootView.getContext(), LinearLayoutManager.VERTICAL, false));
        rvMessages.addItemDecoration(new SpacesItemDecoration(1));
        rvMessages.setAdapter(mAdapterMessages);
    }

    protected void startDialogsFetch(QBUser user) {
        showProgressDialogPrivate();
        isOnMainUI();

        ChatService.initIfNeed(getActivity());
        if (UtilChat.isChatSessionActive() && UtilChat.isUserLoggedIn()) {
            NhLog.e("getDialogs()", "1");
            getDialogs();
        }else
            ChatService.getInstance().login(user, new QBEntityCallback() {
                @Override
                public void onSuccess(Object o, Bundle bundle) {
                    if (isOnMainUI()) {
                        NhLog.e("getDialogs()", "2");
                        getDialogs();
                    }else
                        if (getActivity() != null)
                            getActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    NhLog.e("getDialogs()", "3");
                                    getDialogs();
                                }
                            });
                }

                @Override
                public void onError(QBResponseException errors) {
                    showAlertDialog(getString(R.string.error_server_error) + " " + errors.getErrors());
                }
            });
    }

    protected void showProgressDialogPrivate() {
    }

    protected void dismissProgressDialogPrivate(){

    }

    protected void getDialogs(){
        isOnMainUI();
        registerChatGcm();
        ChatService.getInstance().getDialogs(new QBEntityCallback() {
            @Override
            public void onSuccess(Object object, Bundle bundle) {
                isOnMainUI();
                final ArrayList<QBDialog> dialogs = (ArrayList<QBDialog>)object;
                UtilChat.setDialogs(dialogs);
                showFetchedDialogues();
                dismissProgressDialogPrivate();
            }

            @Override
            public void onError(QBResponseException errors) {
                isOnMainUI();
                dismissProgressDialogPrivate();
                AlertDialog.Builder dialog = new AlertDialog.Builder(getActivity());
                dialog.setMessage("get dialogs errors: " + errors).create().show();
            }
        });
    }

    private void registerChatGcm() {
        ChatService.getInstance().subscribeToPushNotifications(getActivity(), PlayServicesHelper.getRegistrationIdProperty(getActivity()));
    }

    protected void refreshAdapter(final ArrayList<QBDialog> dialogs) {
            if (getActivity() != null)
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mAdapterMessages.updateItems(dialogs);
                        mAdapterMessages.notifyDataSetChanged();
                    }
                });
    }

    protected void refreshAdapter() {
        if (mAdapterMessages != null)
            mAdapterMessages.notifyDataSetChanged();
    }

    protected List<QBDialog> getQbItems() {
        if(mItems == null)
            mItems = new ArrayList<>();

        return mItems;
    }

    protected void showFetchedDialogues() {
        refreshAdapter(UtilChat.getDialogs());
    }

    protected void startConversationActivityFromDialog(QBDialog pDialog) {
        Intent intent = null;

        if (pDialog.getType() == QBDialogType.PRIVATE)
            intent = new Intent(getActivity(), Activity_Messages_Single.class);

        if (pDialog.getType() == QBDialogType.GROUP)
            intent = new Intent(getActivity(), Activity_Messages_Group.class);

        Bundle bundle = new Bundle();
        bundle.putSerializable(IChat.EXTRA_DIALOG, pDialog);

        intent.putExtras(bundle);
        startActivity(intent);
    }

    @Override
    public void onOpenDialogItem(QBDialog pDialog) {
        throw new ExceptionChatUnimplemetedMethod();
    }

    @Override
    public void onLoadDialogItem(RecyclerView.ViewHolder vHolder, QBDialog pDialog) {
        throw new ExceptionChatUnimplemetedMethod();
    }

    @Override
    public void onDeleteDialogItem(QBDialog currentItem) {
        throw new ExceptionChatUnimplemetedMethod();
    }


}
