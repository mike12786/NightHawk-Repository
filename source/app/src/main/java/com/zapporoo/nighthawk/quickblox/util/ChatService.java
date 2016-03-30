package com.zapporoo.nighthawk.quickblox.util;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.Looper;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.util.Log;

import com.quickblox.auth.QBAuth;
import com.quickblox.auth.model.QBSession;
import com.quickblox.chat.QBChatService;
import com.quickblox.chat.model.QBDialog;
import com.quickblox.core.QBEntityCallback;
import com.quickblox.core.QBSettings;
import com.quickblox.core.exception.QBResponseException;
import com.quickblox.core.helper.StringifyArrayList;
import com.quickblox.core.request.QBPagedRequestBuilder;
import com.quickblox.core.request.QBRequestGetBuilder;
import com.quickblox.messages.QBPushNotifications;
import com.quickblox.messages.model.QBEnvironment;
import com.quickblox.messages.model.QBNotificationChannel;
import com.quickblox.messages.model.QBSubscription;
import com.quickblox.users.QBUsers;
import com.quickblox.users.model.QBUser;
import com.zapporoo.nighthawk.App;
import com.zapporoo.nighthawk.BuildConfig;
import com.zapporoo.nighthawk.model.ModUser;
import com.zapporoo.nighthawk.util.NhLog;

import org.jivesoftware.smack.ConnectionListener;
import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.XMPPConnection;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by igorkhomenko on 4/28/15.
 */
public class ChatService {

    private static final String TAG = ChatService.class.getSimpleName();

    static final int AUTO_PRESENCE_INTERVAL_IN_SECONDS = 30;

    private static ChatService instance;

    private QBChatService chatService;

    protected boolean isOnMainUI() {
        if (Looper.myLooper() == Looper.getMainLooper())
            return true;
        else
            return false;
    }

    private ChatService() {
        chatService = QBChatService.getInstance();
        chatService.addConnectionListener(chatConnectionListener);
    }

    public static synchronized ChatService getInstance() {
        if (instance == null) {
            instance = new ChatService();
        }
        return instance;
    }

    public void addConnectionListener(ConnectionListener listener) {
        chatService.addConnectionListener(listener);
    }

    public void removeConnectionListener(ConnectionListener listener) {
        chatService.removeConnectionListener(listener);
    }

    public static boolean initIfNeed(Context ctx) {
        if (!QBChatService.isInitialized()) {
            //QBChatService.setDebugEnabled(true);
            QBChatService.init(ctx);
            QBChatService.setDebugEnabled(App.DEBUG);
            if(ctx.getApplicationContext() != null) {
                App app = (App) ctx.getApplicationContext();
                if(app != null)
                    QBSettings.getInstance().setLogLevel(app.getLogLevel());
            }
            NhLog.d(TAG, "Initialise QBChatService");
            return true;
        }

        return false;
    }

    public void login(QBUser user, final QBEntityCallback callback) {
        if (user == null) {
            user = new QBUser();
            String s = ModUser.getModUser().getChatUsername();
            String p = ModUser.getModUser().getChatPassword();
            user.setLogin(s);
            user.setPassword(p);
        }

        creteSession(user, callback);
    }

    public void logout() {
        chatService.logout(new QBEntityCallback<Void>() {
            @Override
            public void onSuccess(Void aVoid, Bundle bundle) {
                NhLog.e(TAG, "logout onSuccess()");
            }

            @Override
            public void onError(QBResponseException errors) {
                NhLog.e(TAG, "logout onError() " + errors.getErrors() );
            }

        });
    }

    public void creteSession(final QBUser user, final QBEntityCallback callback) {
        QBAuth.createSession(user, new QBEntityCallback<QBSession>() {
            @Override
            public void onSuccess(QBSession session, Bundle args) {
                Integer i = session.getUserId();
                user.setId(i);
                loginToChat(user, callback);
            }

            @Override
            public void onError(QBResponseException errors) {
                callback.onError(errors);
            }
        });
    }

    private void loginToChat(final QBUser user, final QBEntityCallback callback) {
        chatService.login(user, new QBEntityCallback() {
            @Override
            public void onSuccess(Object o, Bundle bundle) {
                // Start sending presences
                //
                try {
                    chatService.startAutoSendPresence(AUTO_PRESENCE_INTERVAL_IN_SECONDS);
                } catch (SmackException.NotLoggedInException e) {
                    e.printStackTrace();
                }
                callback.onSuccess(o, bundle);
            }

            @Override
            public void onError(QBResponseException errors) {
                callback.onError(errors);
            }
        });
    }


    public void getDialogue(final String dialogueId, final QBEntityCallback callback){
        QBEntityCallback<QBSession> dialogCallback = new QBEntityCallback<QBSession>() {
            @Override
            public void onSuccess(QBSession qbSession, Bundle bundle) {
                QBRequestGetBuilder customObjectRequestBuilder = new QBRequestGetBuilder();
                customObjectRequestBuilder.setLimit(1);
                customObjectRequestBuilder.eq("_id", dialogueId);
                NhLog.e("getDialogue", "_Id = " + dialogueId);

                QBChatService.getChatDialogs(null, customObjectRequestBuilder, new QBEntityCallback<ArrayList<QBDialog>>() {
                    @Override
                    public void onSuccess(final ArrayList<QBDialog> dialogs, Bundle args) {
                        if (dialogs.size() != 0) {
                            UtilChat.addToCacheList(dialogs.get(0));
                            callback.onSuccess(dialogs.get(0), null);
                        }else
                            callback.onSuccess(null, null);

                    }

                    @Override
                    public void onError(QBResponseException errors) {
                        callback.onError(errors);
                    }
                });
            }

            @Override
            public void onError(QBResponseException e) {
                callback.onError(e);
            }
        };

        QBUser user = getCurrentUser();

        if (user == null){
            QBAuth.createSession(ModUser.getModUser().getChatUsername(), ModUser.getModUser().getChatPassword() , dialogCallback);
        }else
            QBAuth.createSession(getCurrentUser(), dialogCallback);
    }


    public void getDialogs(final QBEntityCallback callback) {
        QBRequestGetBuilder customObjectRequestBuilder = new QBRequestGetBuilder();
        customObjectRequestBuilder.setLimit(100);

        QBChatService.getChatDialogs(null, customObjectRequestBuilder, new QBEntityCallback<ArrayList<QBDialog>>() {
            @Override
            public void onSuccess(final ArrayList<QBDialog> dialogs, Bundle args) {

                // collect all occupants ids
                //
                List<Integer> usersIDs = new ArrayList<>();
                for (QBDialog dialog : dialogs) {
                    usersIDs.addAll(dialog.getOccupants());
                }

                // Get all occupants info
                //
                QBPagedRequestBuilder requestBuilder = new QBPagedRequestBuilder();
                requestBuilder.setPage(1);
                requestBuilder.setPerPage(usersIDs.size());
                //
                QBUsers.getUsersByIDs(usersIDs, requestBuilder, new QBEntityCallback<ArrayList<QBUser>>() {
                    @Override
                    public void onSuccess(ArrayList<QBUser> users, Bundle params) {
                        setDialogsUsers(users);
                        callback.onSuccess(removeEmptyDialogs(dialogs), null);
                    }

                    @Override
                    public void onError(QBResponseException errors) {
                        callback.onError(errors);
                    }

                });
            }

            @Override
            public void onError(QBResponseException errors) {
                callback.onError(errors);
            }
        });
    }

    private ArrayList<QBDialog> removeEmptyDialogs(ArrayList<QBDialog> dialogs) {
        ArrayList<QBDialog> ret = new ArrayList<>();
        for (QBDialog dialog : dialogs) {
            if (dialog.getLastMessageUserId() != null)
                ret.add(dialog);
        }
        return ret;
    }


    private Map<Integer, QBUser> dialogsUsers = new HashMap<>();

    public Map<Integer, QBUser> getDialogsUsers() {
        return dialogsUsers;
    }

    public void setDialogsUsers(List<QBUser> setUsers) {
        dialogsUsers.clear();

        for (QBUser user : setUsers) {
            dialogsUsers.put(user.getId(), user);
        }
    }

    public void addDialogsUsers(List<QBUser> newUsers) {
        for (QBUser user : newUsers) {
            dialogsUsers.put(user.getId(), user);
        }
    }

    public QBUser getCurrentUser() {
        return QBChatService.getInstance().getUser();
    }

    public Integer getOpponentIDForPrivateDialog(QBDialog dialog) {
        Integer opponentID = -1;
        for (Integer userID : dialog.getOccupants()) {
            if (!userID.equals(getCurrentUser().getId())) {
                opponentID = userID;
                break;
            }
        }
        return opponentID;
    }

    public StringifyArrayList<Integer> getOpponentIDsForDialog(QBDialog dialog) {
        StringifyArrayList<Integer> opponentList = new StringifyArrayList<>();
        for (Integer userID : dialog.getOccupants()) {
            if (!userID.equals(getCurrentUser().getId())) {
                opponentList.add(userID);
                continue;
            }
        }
        return opponentList;
    }


    ConnectionListener chatConnectionListener = new ConnectionListener() {
        @Override
        public void connected(XMPPConnection connection) {
            Log.i(TAG, "connected");
        }

        @Override
        public void authenticated(XMPPConnection xmppConnection, boolean b) {

        }

        @Override
        public void connectionClosed() {
            Log.i(TAG, "connectionClosed");
        }

        @Override
        public void connectionClosedOnError(final Exception e) {
            Log.i(TAG, "connectionClosedOnError: " + e.getLocalizedMessage());
        }

        @Override
        public void reconnectingIn(final int seconds) {
            if(seconds % 5 == 0) {
                Log.i(TAG, "reconnectingIn: " + seconds);
            }
        }

        @Override
        public void reconnectionSuccessful() {
            Log.i(TAG, "reconnectionSuccessful");
        }

        @Override
        public void reconnectionFailed(final Exception error) {
            Log.i(TAG, "reconnectionFailed: " + error.getLocalizedMessage());
        }
    };


    public void subscribeToPushNotifications(Activity activity, String registrationID) {
        QBEnvironment env;
        if (BuildConfig.DEBUG)
            env = QBEnvironment.PRODUCTION;
        else
            env = QBEnvironment.PRODUCTION;

        QBSubscription subscription = new QBSubscription(QBNotificationChannel.GCM);
        subscription.setEnvironment(env);
        //
        String deviceId;
        final TelephonyManager mTelephony = (TelephonyManager) activity.getSystemService(
                Context.TELEPHONY_SERVICE);
        if (mTelephony.getDeviceId() != null) {
            deviceId = mTelephony.getDeviceId(); //*** use for mobiles
        } else {
            deviceId = Settings.Secure.getString(activity.getContentResolver(),
                    Settings.Secure.ANDROID_ID); //*** use for tablets
        }
        subscription.setDeviceUdid(deviceId);
        //
        subscription.setRegistrationID(registrationID);
        //
        NhLog.e(TAG, "subscribing...\nRegID: " + registrationID);

        QBPushNotifications.createSubscription(subscription, new QBEntityCallback<ArrayList<QBSubscription>>() {

            @Override
            public void onSuccess(ArrayList<QBSubscription> subscriptions, Bundle args) {
                NhLog.e(TAG, "subscribeToPushNotificationsTask -> subscribed");
            }

            @Override
            public void onError(QBResponseException error) {
                NhLog.e(TAG, "subscribeToPushNotificationsTask -> Error: " + error.getErrors());
            }
        });
    }


    public void unsubscribeFromPushNotifications(final Context context, final QBEntityCallback<Void> callback){
        QBPushNotifications.getSubscriptions(new QBEntityCallback<ArrayList<QBSubscription>>() {
            @Override
            public void onSuccess(ArrayList<QBSubscription> subscriptions, Bundle args) {
                String deviceId = ((TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE)).getDeviceId();
                for(QBSubscription subscription : subscriptions){
                    if(subscription.getDevice().getId().equals(deviceId)){
                        QBPushNotifications.deleteSubscription(subscription.getId(), callback);
                        break;
                    }
                }
            }

            @Override
            public void onError(QBResponseException errors) {

            }
        });
    }


}
