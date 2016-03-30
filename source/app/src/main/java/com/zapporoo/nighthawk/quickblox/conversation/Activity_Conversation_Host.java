package com.zapporoo.nighthawk.quickblox.conversation;

import android.os.Bundle;
import android.view.MenuItem;

import com.quickblox.chat.QBChatService;
import com.quickblox.chat.QBGroupChatManager;
import com.quickblox.chat.model.QBDialog;
import com.quickblox.chat.model.QBDialogType;
import com.quickblox.core.QBEntityCallback;
import com.quickblox.core.exception.QBResponseException;
import com.zapporoo.nighthawk.callbacks.IChatHostActivity;
import com.zapporoo.nighthawk.model.ModUser;
import com.zapporoo.nighthawk.quickblox.Consts;
import com.zapporoo.nighthawk.quickblox.conversation.chat.IChat;
import com.zapporoo.nighthawk.quickblox.util.ChatService;
import com.zapporoo.nighthawk.quickblox.util.ExceptionChatUnimplemetedMethod;
import com.zapporoo.nighthawk.quickblox.util.UtilChat;
import com.zapporoo.nighthawk.ui.activities.ActivityDefault;
import com.zapporoo.nighthawk.util.NhLog;

import java.util.ArrayList;

/**
 * Created by Pile on 2/5/2016.
 */
public class Activity_Conversation_Host extends ActivityDefault implements IChatHostActivity {

    @Override
    public void getChatDialog(QBEntityCallback<QBDialog> callback) {
        showProgressDialogSpinning();
        ChatService.initIfNeed(this);

        QBDialog mDialog = (QBDialog) getIntent().getSerializableExtra(IChat.EXTRA_DIALOG);

        if (mDialog == null) {
            checkIfFromPush(callback, getIntent().getExtras());
        }else {
            if (mDialog.getOccupants() == null) { //tempDialog
                ChatService.getInstance().getDialogue(mDialog.getDialogId(), callback);
            }
            else
                callback.onSuccess(mDialog, null);
        }
    }

    @Override
    public void setToolbarTitle(String title) {
        setTitle(title);
    }

    private void checkIfFromPush(QBEntityCallback<QBDialog> callback, Bundle extras) {
        if (extras.getBoolean(Consts.KEY_FROM_PUSH, false))
            processFromPush(callback, extras);
        else
            createNewChatDialog(callback, getIntent().getExtras());
    }

    private void processFromPush(QBEntityCallback<QBDialog> callback, Bundle extras) {
        String dialogId = extras.getString(Consts.KEY_DIALOG_ID);
        QBDialog dialog = UtilChat.getDialog(dialogId);

        if (dialog != null && dialog.getId() != null)
            callback.onSuccess(dialog, null);
        else
            ChatService.getInstance().getDialogue(dialogId, callback);
    }


    protected void createNewChatDialog(QBEntityCallback<QBDialog> callback, Bundle b){
        String userName = b.getString(UtilChat.KEY_PERSON_NAME);
        //String userChatIdS = b.getString(UtilChat.KEY_USER_CHAT_ID, "-1");

        if (getDialogType() == QBDialogType.GROUP)
                createTestDialog(callback);
        else{
            QBDialog dialogToCreate = new QBDialog();
            dialogToCreate.setName(userName);
            dialogToCreate.setType(getDialogType());
            dialogToCreate.setOccupantsIds(getParticipants());

            QBChatService.getInstance().getPrivateChatManager().createDialog(getParticipants().get(0), callback);
        }
    }


    private boolean createTestDialog(final QBEntityCallback<QBDialog> callback) {
        QBDialog dialog = new QBDialog();
        dialog.setName(ModUser.getModUser().getPersonalName() + "'s Group");
        //dialog.setPhoto("1786");
        dialog.setType(QBDialogType.GROUP);
        dialog.setOccupantsIds(getParticipants());

        QBGroupChatManager groupChatManager = QBChatService.getInstance().getGroupChatManager();
        groupChatManager.createDialog(dialog, new QBEntityCallback<QBDialog>() {
            @Override
            public void onSuccess(QBDialog dialog, Bundle args) {
                NhLog.e("s","s");
                callback.onSuccess(dialog,args);
            }

            @Override
            public void onError(QBResponseException errors) {
                NhLog.e("s","s");
                callback.onError(errors);
            }
        });

        return true;
    }

    protected ArrayList<Integer> getParticipants(){
        throw new ExceptionChatUnimplemetedMethod();
    }

    protected QBDialogType getDialogType(){
        throw new ExceptionChatUnimplemetedMethod();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                if (isTaskRoot())
                    startSplash();
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        if (isTaskRoot())
            startSplash();
        super.onBackPressed();
    }
}
