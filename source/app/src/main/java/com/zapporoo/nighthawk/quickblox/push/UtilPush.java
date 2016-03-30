package com.zapporoo.nighthawk.quickblox.push;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.media.RingtoneManager;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.LocalBroadcastManager;

import com.quickblox.chat.model.QBDialog;
import com.quickblox.chat.model.QBDialogType;
import com.quickblox.core.QBEntityCallback;
import com.quickblox.core.exception.QBResponseException;
import com.quickblox.core.helper.StringifyArrayList;
import com.quickblox.messages.QBPushNotifications;
import com.quickblox.messages.model.QBEnvironment;
import com.quickblox.messages.model.QBEvent;
import com.quickblox.messages.model.QBNotificationType;
import com.quickblox.messages.model.QBPushType;
import com.zapporoo.nighthawk.R;
import com.zapporoo.nighthawk.model.ModUser;
import com.zapporoo.nighthawk.quickblox.Consts;
import com.zapporoo.nighthawk.quickblox.util.ChatService;
import com.zapporoo.nighthawk.quickblox.util.UtilChat;
import com.zapporoo.nighthawk.ui.activities.Activity_Messages_Group;
import com.zapporoo.nighthawk.ui.activities.Activity_Messages_Single;

import org.json.JSONObject;

/**
 * Created by Pile on 3/6/2016.
 */
public class UtilPush {

    public static void sendPushNotificationInfoLeftDialog(QBDialog dialog) {
        //Using PUSH notification
        StringifyArrayList<Integer> list = ChatService.getInstance().getOpponentIDsForDialog(dialog);

        QBEvent event = new QBEvent();
        event.setUserIds(list);
        event.setEnvironment(QBEnvironment.PRODUCTION);
        event.setNotificationType(QBNotificationType.PUSH);
        event.setPushType(QBPushType.GCM);

        JSONObject json = new JSONObject();

        try {
            // standard parameters
            json.put(UtilChat.KEY_ALERT, ModUser.getModUser().getPersonalName() + " has left the conversation");
            // custom parameters
            json.put(Consts.KEY_MESSAGE_TEXT, trimChar(dialog.getName()));
            json.put(Consts.KEY_DIALOG_ID, dialog.getDialogId());
            json.put(Consts.KEY_USER_CHAT_ID_FROM, ModUser.getModUser().getChatId());
            json.put(Consts.KEY_USER_NAME_FROM, ModUser.getModUser().getPersonalName());
            json.put(Consts.KEY_DIALOG_TYPE, dialog.getType());
            json.put(Consts.KEY_NOTIFICATION_TYPE, Consts.NOTIFICATION_TYPE_INFO);

        } catch (Exception e) {
            e.printStackTrace();
        }
        event.setMessage(json.toString());

        QBPushNotifications.createEvent(event, new QBEntityCallback<QBEvent>() {
            @Override
            public void onSuccess(QBEvent qbEvent, Bundle args) {
                System.out.println("GCM Message Sent inside event " );
            }

            @Override
            public void onError(QBResponseException errors) {
                System.out.println("GCM Message ERROR inside event " + errors.getErrors());
            }

        });
    }

    public static void sendPushNotification(StringifyArrayList<Integer> userIdList, String chatText, QBDialog dialog) {
        //Using PUSH notification
        QBEvent event = new QBEvent();
        event.setUserIds(userIdList);
        event.setEnvironment(QBEnvironment.PRODUCTION);
        event.setNotificationType(QBNotificationType.PUSH);
        event.setPushType(QBPushType.GCM);

        JSONObject json = new JSONObject();

        try {
            // standard parameters
            json.put(UtilChat.KEY_ALERT, "Message from `" + ModUser.getModUser().getPersonalName());
            // custom parameters
            json.put(Consts.KEY_MESSAGE_TEXT, trimChar(chatText));
            json.put(Consts.KEY_DIALOG_ID, dialog.getDialogId());
            json.put(Consts.KEY_USER_CHAT_ID_FROM, ModUser.getModUser().getChatId());
            json.put(Consts.KEY_USER_NAME_FROM, ModUser.getModUser().getPersonalName());
            json.put(Consts.KEY_DIALOG_TYPE, dialog.getType());
            json.put(Consts.KEY_NOTIFICATION_TYPE, Consts.NOTIFICATION_TYPE_MESSAGE);

        } catch (Exception e) {
            e.printStackTrace();
        }
        event.setMessage(json.toString());

        QBPushNotifications.createEvent(event, new QBEntityCallback<QBEvent>() {
            @Override
            public void onSuccess(QBEvent qbEvent, Bundle args) {
                System.out.println("GCM Message Sent inside event " );
            }

            @Override
            public void onError(QBResponseException errors) {
                System.out.println("GCM Message ERROR inside event " + errors.getErrors());
            }

        });
    }

    private static String trimChar(String message) {
        if (message.length() > 30)
            return message.substring(0, 30);
        else
            return message;
    }

    public static PushHelper processArrivedPush(String jsonData, Context context){
        Intent intent = null;
        PushHelper ret = new PushHelper();

        String message = null;//Value = extras.getString("message");
        String user_chat_id = null;// = extras.getString("userId");
        String dialog_id = null;// = extras.getString("userId");
        String title = null;// = extras.getString(UtilPush.KEY_TITLE);
        String userName = null;
        String type = null;

        int notification_type;// = extras.getInt(UtilPush.KEY_DIALOG_TYPE, -1);

        try {
            JSONObject jsonObject = new JSONObject(jsonData);
            notification_type = jsonObject.getInt(Consts.KEY_NOTIFICATION_TYPE);

            if (notification_type == Consts.NOTIFICATION_TYPE_MESSAGE){
                title = jsonObject.getString(Consts.KEY_MESSAGE_ALERT);
                message = jsonObject.getString(Consts.KEY_MESSAGE_TEXT);
                user_chat_id = jsonObject.getString(Consts.KEY_USER_CHAT_ID_FROM);
                userName = jsonObject.getString(Consts.KEY_USER_NAME_FROM);
                dialog_id = jsonObject.getString(Consts.KEY_DIALOG_ID);
                type = jsonObject.getString(Consts.KEY_DIALOG_TYPE);

                if (type.equals(QBDialogType.GROUP.toString()))
                    intent = new Intent(context, Activity_Messages_Group.class);
                else
                    intent = new Intent(context, Activity_Messages_Single.class);

                //if (type.equals(QBDialogType.PRIVATE.toString()))

                intent.putExtra(Consts.KEY_USER_CHAT_ID_FROM, user_chat_id);
                intent.putExtra(Consts.KEY_USER_NAME_FROM, userName);
                intent.putExtra(Consts.KEY_DIALOG_ID, dialog_id);
                intent.putExtra(Consts.KEY_FROM_PUSH, true);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

                ret.intent = intent;
                ret.title = title;
                ret.message = message;
                ret.user_chat_id = user_chat_id;
                ret.flag_notification = Intent.FLAG_ACTIVITY_NEW_TASK;//PendingIntent.FLAG_UPDATE_CURRENT;
                ret.dialog_id = dialog_id;
                ret.sender_name = userName;
                ret.type = type;
                ret.notification_type = notification_type;

                if (messageIsInDialogues(ret))
                    return null;

                updateExistingDialog(ret);

                notifyListenerToRefreshList(context, message);

            }

            if (notification_type == Consts.NOTIFICATION_TYPE_INFO){
                title = jsonObject.getString(Consts.KEY_MESSAGE_ALERT);
                message = jsonObject.getString(Consts.KEY_MESSAGE_TEXT);
                dialog_id = jsonObject.getString(Consts.KEY_DIALOG_ID);

                //intent = new Intent(context, Activity_hoMessages_Group.class);
                intent = new Intent();

                ret.intent = intent;
                ret.title = message;
                ret.message = title;
                ret.sender_name = userName;
                ret.dialog_id = dialog_id;
                ret.notification_type = notification_type;

                notifyListenerForInfo(context, title + " \"" + message + "\"");
            }

            return ret;
        }catch (Exception e){
            return null;
        }
    }

    private static boolean messageIsInDialogues(PushHelper pushPackage) {
        QBDialog d = UtilChat.getDialog(pushPackage.dialog_id);

        if (d != null && d.getLastMessage().equals(pushPackage.message))
            return true;

        return false;
    }

    private static void notifyListenerToRefreshList(Context context, String message) {
        Intent intentNewPush = new Intent(Consts.NEW_PUSH_EVENT_MESSAGE);
        intentNewPush.putExtra(Consts.KEY_MESSAGE_TEXT, message);
        LocalBroadcastManager.getInstance(context).sendBroadcast(intentNewPush);
    }

    private static void notifyListenerForInfo(Context context, String message) {
        Intent intentNewPush = new Intent(Consts.NEW_PUSH_EVENT_INFO);
        intentNewPush.putExtra(Consts.KEY_MESSAGE_TEXT, message);
        LocalBroadcastManager.getInstance(context).sendBroadcast(intentNewPush);
    }

    private static void updateExistingDialog(PushHelper ret) {
        QBDialog dialog = UtilChat.getDialog(ret.dialog_id);

        if (dialog == null)
            dialog = makeTempDialog(ret);

        dialog.setLastMessage(ret.message);
        dialog.setLastMessageUserId(ret.getUserUserChatId());

    }

    private static QBDialog makeTempDialog(PushHelper ret) {
        QBDialog dialog = new QBDialog();
        dialog.setLastMessageUserId(ret.getUserUserChatId());
        dialog.setLastMessage(ret.message);
        dialog.setName(ret.sender_name);
        dialog.setDialogId(ret.dialog_id);

        if (ret.type.equals("GROUP"))
            dialog.setType(QBDialogType.GROUP);
        if (ret.type.equals("PRIVATE"))
            dialog.setType(QBDialogType.PRIVATE);

        UtilChat.addToCacheList(dialog);

        return dialog;
    }

    public static Notification createNotification(Context context, String title, String contentTxt, PendingIntent contentIntent) {
        long[] pattern = { 0, 100, 200, 300 };
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context)
                .setSmallIcon(R.drawable.ic_push)
                //.setLargeIcon(UtilPush.getBitmapFromDrawable(context, R.drawable.ic_launcher, 128, 128))
                .setContentTitle(title)
                //.setStyle(new NotificationCompat.BigTextStyle().bigText(contentTxt))
                .setContentText(contentTxt)
                .setAutoCancel(true)
                .setVibrate(pattern)
                .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
                .setContentIntent(contentIntent);

//        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//            mBuilder.setColor(getColor(context, R.color.colorPrimary));
//        }

        mBuilder.setContentIntent(contentIntent);

        return mBuilder.build();
    }

    public static Bitmap getBitmapFromDrawable(Context context, int resource, int widthPixels, int heightPixels) {
        Drawable drawable = getDrawable(context, resource);
        Bitmap mutableBitmap = Bitmap.createBitmap(widthPixels, heightPixels, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(mutableBitmap);
        drawable.setBounds(0, 0, widthPixels, heightPixels);
        drawable.draw(canvas);

        return mutableBitmap;
    }

    @SuppressWarnings("deprecation")
    @SuppressLint("NewApi")
    public static Drawable getDrawable(Context context, int id) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            return context.getDrawable(id);
        } else
            return context.getResources().getDrawable(id);
    }

    public static int getColor(Context context, int id) {
        return context.getResources().getColor(id);
    }
}
