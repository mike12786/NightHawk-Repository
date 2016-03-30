package com.zapporoo.nighthawk.quickblox.push;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.quickblox.chat.model.QBDialog;
import com.zapporoo.nighthawk.model.ModUser;
import com.zapporoo.nighthawk.quickblox.Consts;
import com.zapporoo.nighthawk.quickblox.util.UtilChat;

public class GCMIntentService extends IntentService {

    private static final String TAG = GCMIntentService.class.getSimpleName();

    private NotificationManager notificationManager;
    private static int NOTIFICATION_INC = 100;

    public GCMIntentService() {
        super(Consts.GCM_INTENT_SERVICE);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Log.i(TAG, "new push");

        Bundle extras = intent.getExtras();
        GoogleCloudMessaging googleCloudMessaging = GoogleCloudMessaging.getInstance(this);
        // The getMessageType() intent parameter must be the intent you received
        // in your BroadcastReceiver.
        String messageType = googleCloudMessaging.getMessageType(intent);

        if (!extras.isEmpty()) {  // has effect of unparcelling Bundle
            /*
             * Filter messages based on message type. Since it is likely that GCM
             * will be extended in the future with new message types, just ignore
             * any message types you're not interested in, or that you don't
             * recognize.
             */
            if (GoogleCloudMessaging.
                    MESSAGE_TYPE_SEND_ERROR.equals(messageType)) {
                processNotification(Consts.GCM_SEND_ERROR, extras);
            } else if (GoogleCloudMessaging.
                    MESSAGE_TYPE_DELETED.equals(messageType)) {
                processNotification(Consts.GCM_DELETED_MESSAGE, extras);
                // If it's a regular GCM message, do some work.
            } else if (GoogleCloudMessaging.
                    MESSAGE_TYPE_MESSAGE.equals(messageType)) {
                // Post notification of received message.
                processNotification(Consts.GCM_RECEIVED, extras);
                Log.i(TAG, "Received: " + extras.toString());
            }
        }
        // Release the wake lock provided by the WakefulBroadcastReceiver.
        GcmBroadcastReceiver.completeWakefulIntent(intent);
    }

    // Put the message into a notification and post it.
    // This is just one simple example of what you might choose to do with
    // a GCM message.
    private void processNotification(String type, Bundle extras) {
        notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        final String messageValue = extras.getString(Consts.KEY_MESSAGE_TEXT);
        final String messageTitle = extras.getString(Consts.KEY_MESSAGE_TITLE);

        String jsonChatData = extras.getString(Consts.EXTRA_MESSAGE_JSON);

        if (!ModUser.getModUser().getMessagesEnabled())
            return;

        PushHelper pushPackage = UtilPush.processArrivedPush(jsonChatData, this);

        if (pushPackage == null) {
            return;
        }

        PendingIntent contentIntent = PendingIntent.getActivity(this, NOTIFICATION_INC++, pushPackage.intent, PendingIntent.FLAG_ONE_SHOT);
        Notification notification = UtilPush.createNotification(this, pushPackage.title, pushPackage.message, contentIntent);





        if (!ModUser.getModUser().getNotificationsEnabled() && pushPackage.notification_type != Consts.NOTIFICATION_TYPE_INFO)
            return;

        if (pushPackage.dialog_id.equals(UtilChat.getOpenDialogId(this)) && pushPackage.notification_type != Consts.NOTIFICATION_TYPE_INFO)
            return;

        notificationManager.notify(pushPackage.dialog_id , Consts.DEFAULT_NOTIFICATION_ID, notification);

        Log.i(TAG, "Broadcasting event " + Consts.NEW_PUSH_EVENT_MESSAGE + " with data: " + messageValue);
    }


}