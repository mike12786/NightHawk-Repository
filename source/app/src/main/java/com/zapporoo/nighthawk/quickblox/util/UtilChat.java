package com.zapporoo.nighthawk.quickblox.util;

import android.app.NotificationManager;
import android.content.Context;
import android.os.Bundle;
import android.preference.PreferenceManager;

import com.quickblox.chat.QBChat;
import com.quickblox.chat.QBChatService;
import com.quickblox.chat.QBGroupChatManager;
import com.quickblox.chat.QBPrivateChatManager;
import com.quickblox.chat.model.QBChatMessage;
import com.quickblox.chat.model.QBDialog;
import com.quickblox.chat.model.QBDialogType;
import com.quickblox.core.QBEntityCallback;
import com.quickblox.core.exception.BaseServiceException;
import com.quickblox.core.exception.QBResponseException;
import com.quickblox.core.request.QBRequestUpdateBuilder;
import com.quickblox.core.server.BaseService;
import com.zapporoo.nighthawk.quickblox.Consts;
import com.zapporoo.nighthawk.util.NhLog;

import java.util.ArrayList;
import java.util.Date;

/**
 * Created by Pile on 2/5/2016.
 */
public class UtilChat {
    //QB
    public static final String KEY_ALERT = "alert";
    public static final String KEY_DATE_SENT = "date_sent";

    //CUSTOM
    public static final String KEY_PERSON_NAME = "KEY_PERSON_NAME";
    public static final java.lang.String KEY_USER_CHAT_ID = "objectId";
    public static final java.lang.String KEY_USER_GROUP_CHAT_ID = "KEY_USER_GROUP_CHAT_ID";
    private static final String KEY_OPEN_DIALOG_ID = "KEY_OPEN_DIALOG_ID";
    private static final int DEFAULT_ID = 123; //used when we can only identify the pushs with String


    public static boolean isChatSessionActive() {
        try {
            if (BaseService.getBaseService().getTokenExpirationDate().after(new Date()))
                return true;
            else
                return false;
        }catch (BaseServiceException e) {
            e.printStackTrace();
        }catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public static boolean isUserLoggedIn() {
        if (ChatService.getInstance().getCurrentUser() == null)
            return false;
        else
            return  true;
    }

    private static ArrayList<QBDialog> dialogList;

    public static ArrayList<QBDialog> getDialogs(){
        if (dialogList == null)
            dialogList = new ArrayList<>();

        return dialogList;
    }

    public static QBDialog getDialog(String groupChatId) {
        if (dialogList == null)
            return null;

        for (QBDialog dialog: dialogList){
            String dialogId = dialog.getDialogId();
            if (dialogId.equals(groupChatId))
                return dialog;
        }

        return null;
    }

    public static void addToCacheList(QBDialog qbDialog) {
        boolean update = false;
        QBDialog target = null;

        if (dialogList == null)
            dialogList = new ArrayList<>();

        for (QBDialog d: dialogList)
            if (d.getDialogId().equals(qbDialog.getDialogId())){
                update = true;
                target = d;
                break;
            }

        try {
            if (update){
                int pos = dialogList.indexOf(target);
                dialogList.remove(target);
                dialogList.add(pos, qbDialog);
                return;
            }

            dialogList.add(qbDialog);
        }catch (Exception e){

        }
    }

    public static void setDialogs(ArrayList<QBDialog> dialogs) {
        dialogList = dialogs;
    }


    public static void deleteDialog(QBDialog dialog, QBEntityCallback<QBDialog> callback){

        QBEntityCallback<Void> call = new QBEntityCallback<Void>() {
            @Override
            public void onSuccess(Void aVoid, Bundle bundle) {
                NhLog.breakpoint();
            }

            @Override
            public void onError(QBResponseException e) {
                NhLog.breakpoint();
            }
        };

        if (dialog.getType() == QBDialogType.PRIVATE){
            deleteDialog_private(dialog, call);
        }

        //if (dialog.getType() == QBDialogType.GROUP){
            deleteDialog_group(dialog, callback, call);
        //}
    }

    public static void deleteDialog_group(QBDialog dialog, QBEntityCallback<QBDialog> callback, QBEntityCallback<Void> call){
        removeAllUsersFromGroupChat(dialog, callback);
        QBGroupChatManager groupChatManager = QBChatService.getInstance().getGroupChatManager();
        groupChatManager.deleteDialog(dialog.getDialogId(), call);
    }



    public static void deleteDialog_private(QBDialog dialog, QBEntityCallback<Void> call){
        QBPrivateChatManager privateChatManager = QBChatService.getInstance().getPrivateChatManager();
        privateChatManager.deleteDialog(dialog.getDialogId(), call);
    }




    public static void removeAllUsersFromGroupChat(QBDialog dialog, QBEntityCallback<QBDialog> callback){
        QBRequestUpdateBuilder requestBuilder = new QBRequestUpdateBuilder();
        requestBuilder.pullAll("occupants_ids", dialog.getOccupants());

        QBGroupChatManager groupChatManager = QBChatService.getInstance().getGroupChatManager();
        groupChatManager.updateDialog(dialog,requestBuilder, callback);
    }

    public static void removeUserFromGroupChat(QBDialog dialog, Integer userId, QBEntityCallback<QBDialog> callback){
        QBRequestUpdateBuilder requestBuilder = new QBRequestUpdateBuilder();
        requestBuilder.pullAll("occupants_ids", userId); // add another users
        // requestBuilder.pullAll("occupants_ids", 22); // Remove yourself (user with ID 22)

        QBGroupChatManager groupChatManager = QBChatService.getInstance().getGroupChatManager();
        groupChatManager.updateDialog(dialog,requestBuilder, callback);
    }


    public static void addUserToChat(QBDialog dialog, Integer userId, QBEntityCallback<QBDialog> callback){
        QBRequestUpdateBuilder requestBuilder = new QBRequestUpdateBuilder();
        requestBuilder.pushAll("occupants_ids", userId); // add another users
        // requestBuilder.pullAll("occupants_ids", 22); // Remove yourself (user with ID 22)

        QBGroupChatManager groupChatManager = QBChatService.getInstance().getGroupChatManager();
        groupChatManager.updateDialog(dialog,requestBuilder, callback);
    }

    public static void removeAllDialogs() {
        if (dialogList != null)
            dialogList.clear();
    }

    public static void putOpenDialogId(Context context, String id){
        PreferenceManager.getDefaultSharedPreferences(context).edit().putString(KEY_OPEN_DIALOG_ID, id).commit();
    }

    public static String getOpenDialogId(Context context){
        return PreferenceManager.getDefaultSharedPreferences(context).getString(KEY_OPEN_DIALOG_ID, "");
    }

    public static void clearPushForChatId(Context context, String tag) {
        NotificationManager nm = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        nm.cancel(tag, Consts.DEFAULT_NOTIFICATION_ID);
    }

    public static void clearAllPushs(Context context) {
        NotificationManager nm = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        nm.cancelAll();
    }

    public static void updateDialog(QBDialog mDialog, QBChatMessage message) {
        for (QBDialog d: dialogList){
            if (d.getDialogId().equals(mDialog.getDialogId())) {
                d.setLastMessage(message.getBody());
                d.setLastMessageUserId(message.getSenderId());
                d.setLastMessageDateSent(message.getDateSent());
            }
            return;
        }
    }

    public static void updateDialogLastMessage(QBDialog mDialog) {
        for (QBDialog d: dialogList){
            if (d.getDialogId().equals(mDialog.getDialogId())) {
                d.setLastMessage(mDialog.getLastMessage());
                d.setLastMessageUserId(mDialog.getLastMessageUserId());
                d.setLastMessageDateSent(mDialog.getLastMessageDateSent());
            }
            return;
        }
    }

    public static void replaceDialog(QBDialog mDialog) {
        if (mDialog == null)
            return;

        int pos = -1;
        int i =-1;
        for (QBDialog d: dialogList){
            i++;
            if (d.getDialogId().equals(mDialog.getDialogId())) {
                pos = i;
                break;
            }
        }
        if (pos != -1) {
            dialogList.remove(pos);
            dialogList.add(pos,mDialog);
        }
    }

    public static void removeDialog(String id) {
        int pos = -1;
        int i =-1;
        for (QBDialog d: dialogList){
            i++;
            if (d.getDialogId().equals(id)) {
                pos = i;
                break;
            }
        }
        if (pos != -1) {
            dialogList.remove(pos);
        }
    }
}
