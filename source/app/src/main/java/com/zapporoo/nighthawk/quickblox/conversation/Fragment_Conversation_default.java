package com.zapporoo.nighthawk.quickblox.conversation;

import android.app.Activity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.parse.ParseException;
import com.parse.SaveCallback;
import com.quickblox.chat.QBChatService;
import com.quickblox.chat.model.QBChatMessage;
import com.quickblox.chat.model.QBDialog;
import com.quickblox.chat.model.QBDialogType;
import com.quickblox.core.QBEntityCallback;
import com.quickblox.core.exception.QBResponseException;
import com.quickblox.core.helper.StringifyArrayList;
import com.quickblox.core.request.QBRequestGetBuilder;
import com.quickblox.users.model.QBUser;
import com.zapporoo.nighthawk.R;
import com.zapporoo.nighthawk.callbacks.IChatHostActivity;
import com.zapporoo.nighthawk.model.ModUser;
import com.zapporoo.nighthawk.quickblox.push.UtilPush;
import com.zapporoo.nighthawk.quickblox.util.ChatService;
import com.zapporoo.nighthawk.quickblox.util.ExceptionChatUnimplemetedMethod;
import com.zapporoo.nighthawk.quickblox.util.UtilChat;
import com.zapporoo.nighthawk.quickblox.conversation.chat.ChatGroupImpl;
import com.zapporoo.nighthawk.quickblox.conversation.chat.ChatPrivateImpl;
import com.zapporoo.nighthawk.quickblox.conversation.chat.IChat;
import com.zapporoo.nighthawk.ui.fragments.FragmentDefault;
import com.zapporoo.nighthawk.util.NhLog;

import org.jivesoftware.smack.ConnectionListener;
import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Created by Pile on 2/5/2016.
 */
public class Fragment_Conversation_default extends FragmentDefault implements IConversationHost, QBEntityCallback<QBDialog>, ICallbackMessageItem {

    protected static final String TAG = "Fragment_Conversation_Single";

    protected RecyclerView rvConversation;
    protected AdapterChatConversations mAdapterPersonalMessages;
    protected List<QBChatMessage> mMessages;
    protected IChatHostActivity chatHostActivity;
    protected QBDialog mDialog;
    protected IChat mChat;
    protected final String PROPERTY_SAVE_TO_HISTORY = "save_to_history";

    //SINGLE
    //protected Integer opponentID;


    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        try {
            showProgressDialog(getString(R.string.progress_loading_data));
            chatHostActivity = (IChatHostActivity) activity;
            chatHostActivity.getChatDialog(this);
        }catch (ClassCastException e) {
            throw new IllegalStateException("This fragment must be attached to ICallbackMessageItem!");
        }
    }



    //When Activity created dialog
    //QBEntityCallback<QBDialog>
    @Override
    public void onSuccess(QBDialog qbDialog, Bundle bundle) {
        //When dialog loaded
        if (qbDialog == null) {
            dismissProgressDialog();
            showOkFinishDialog(getString(R.string.alert_removed_from_dialog));
            return;
        }
        //
        this.mDialog = qbDialog;

        if (qbDialog.getType() == QBDialogType.GROUP){
            checkIfUserOwner(qbDialog);
        }else {
            onChatDialogLoaded();
            UtilChat.addToCacheList(qbDialog);
        }

        setPushCurrentChatId();
    }

    private void checkIfUserOwner(QBDialog qbDialog) {
        if (ModUser.getModUser().getChatId() == qbDialog.getUserId()){

            if (ModUser.getModUser().getGroupChatId() == null || ModUser.getModUser().getGroupChatId().isEmpty()){

                UtilChat.addToCacheList(qbDialog);

                ModUser.getModUser().setGroupChatId(qbDialog.getDialogId());
                ModUser.getModUser().saveInBackground(new SaveCallback() {
                    @Override
                    public void done(ParseException e) {
                        if (e == null)
                            onChatDialogLoaded();
                        else{
                            dismissProgressDialog();
                            showOkFinishDialog("Chat error, please try later!");
                        }
                    }
                });
            }else
                onChatDialogLoaded();

        }else
            onChatDialogLoaded();

    }

    //QBEntityCallback<QBDialog>
    @Override
    public void onError(QBResponseException errors) {
        dismissProgressDialog();
        showOkFinishDialog("Chat error, please try later!");
        NhLog.e("", "IChat error, please try later!" + errors.getErrors() );
    }



    protected void onChatDialogLoaded() {
        initGui();
        setPushCurrentChatId();

        if (UtilChat.isChatSessionActive() && ChatService.getInstance().getCurrentUser() != null) {
            initChat();
        }else{
            ChatService.getInstance().login(ModUser.getModUser().getChatUser(), new QBEntityCallback() {
                @Override
                public void onSuccess(Object o, Bundle bundle) {
                    //dismissProgressDialog();
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            initChat();
                        }
                    });
                }

                @Override
                public void onError(QBResponseException errors) {
                    dismissProgressDialog();
                    showOkFinishDialog(getString(R.string.error_server_error));
                }
            });
        }
        ChatService.getInstance().addConnectionListener(chatConnectionListener);
    }

    private void setTitle() {
        if (chatHostActivity != null && mDialog != null){
            if (mDialog.getType() == QBDialogType.GROUP && mDialog.getUserId() == ModUser.getModUser().getChatId())
                return;
            else
                chatHostActivity.setToolbarTitle(mDialog.getName());
        }
    }

    private void setPushCurrentChatId() {
        if (mDialog != null) {
            UtilChat.putOpenDialogId(getContext(), mDialog.getDialogId());
            UtilChat.clearPushForChatId(getContext(), mDialog.getDialogId());
        }
    }

    @Override
    public void onResume() {
        setPushCurrentChatId();
        clearAllPushForThisChatId();
        setTitle();
        super.onResume();
    }

    private void clearAllPushForThisChatId() {
        if (getActivity() != null && mDialog != null)
            UtilChat.clearPushForChatId(getActivity(), mDialog.getDialogId());
    }

    @Override
    public void onPause() {
        if (getActivity() != null)
            UtilChat.putOpenDialogId(getContext(), "");
        super.onPause();
    }

    protected void initGui() {
        if (mDialog.getType() == QBDialogType.PRIVATE) {
            setupGuiSingleChat();
            //opponentID = ChatService.getInstance().getOpponentIDForPrivateDialog(mDialog);
            //Map<Integer, QBUser> dd = ChatService.getInstance().getDialogsUsers();

            //UtilPush.putOpenChatUserId(this, String.valueOf(opponentID));
//            QBUser aa = dd.get(opponentID);
            //           companionLabel.setText(aa.getLogin());
//           setTitleFont(ChatService.getInstance().getDialogsUsers().get(opponentID).getLogin());
        }else if (mDialog.getType() == QBDialogType.GROUP) {
            setupGuiGroupChat();
        }

        // Send button
        // Stickers

//        attachButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                startNewPostOptionsDialog();
//            }
//        });

    }

    protected void setupGuiListAndAdapter(View rootView, int ownerId){
        mAdapterPersonalMessages = new AdapterChatConversations(getMessages(), this, ownerId);

        rvConversation = (RecyclerView) rootView.findViewById(R.id.rvConversation);
        rvConversation.setLayoutManager(new LinearLayoutManager(rootView.getContext(), LinearLayoutManager.VERTICAL, false));
        rvConversation.setHasFixedSize(false);
        rvConversation.setAdapter(mAdapterPersonalMessages);
    }

    protected void setupGuiSingleChat() {
        throw new ExceptionChatUnimplemetedMethod();
    }

    protected void setupGuiGroupChat() {
        throw new ExceptionChatUnimplemetedMethod();
    }

    protected List<QBChatMessage> getMessages() {
        if(mMessages == null)
            mMessages = new ArrayList<>();
        return mMessages;
    }

    protected void refreshAdapter(ArrayList<QBChatMessage> dialogs) {
        mAdapterPersonalMessages.updateItems(dialogs);
        mAdapterPersonalMessages.notifyDataSetChanged();
    }

    protected void refreshAdapter(){
        mAdapterPersonalMessages.notifyDataSetChanged();
    }



    private void initChat() {
        showProgressDialog(getString(R.string.progress_loading_data));

        if (mDialog.getType() == QBDialogType.GROUP) {
            mChat = new ChatGroupImpl(this);
            joinGroupChat();
        } else if (mDialog.getType() == QBDialogType.PRIVATE) {
            Integer opponentID = ChatService.getInstance().getOpponentIDForPrivateDialog(mDialog);
            mChat = new ChatPrivateImpl(this, opponentID);
            loadChatHistory();
        }
    }




    protected void joinGroupChat() {
        ((ChatGroupImpl) mChat).joinGroupChat(mDialog, new QBEntityCallback() {
            @Override
            public void onSuccess(Object o, Bundle bundle) {
                loadChatHistory();
            }

            @Override
            public void onError(QBResponseException errors) {
                dismissProgressDialog();
                showOkFinishDialog("Error when join group chat: You have been removed from this group chat!");
            }
        });
    }

    protected void loadChatHistory() {
        QBRequestGetBuilder customObjectRequestBuilder = new QBRequestGetBuilder();
        customObjectRequestBuilder.setLimit(100);
        customObjectRequestBuilder.sortAsc(UtilChat.KEY_DATE_SENT);

        QBChatService.getDialogMessages(mDialog, customObjectRequestBuilder, new QBEntityCallback<ArrayList<QBChatMessage>>() {
            @Override
            public void onSuccess(ArrayList<QBChatMessage> messages, Bundle args) {
                dismissProgressDialog();

                if (messages.size() >0)
                    UtilChat.updateDialog(mDialog, messages.get(messages.size() - 1));
                refreshAdapter(messages);
                scrollDownAdapter();
            }

            @Override
            public void onError(QBResponseException errors) {
                dismissProgressDialog();
                if (isAdded()) {
                    showAlertDialog("load chat history errors: " + errors.getErrors());
                }
            }
        });

    }

    protected void scrollDownAdapter() {
        rvConversation.scrollToPosition(mAdapterPersonalMessages.getItemCount() - 1);
    }









    protected void sendTextMessage(String messageText) {
        if (!isNetworkAvailable())
            return;

        // create a message
        QBChatMessage chatMessage = new QBChatMessage();

        chatMessage.setBody(messageText);
        chatMessage.setProperty(PROPERTY_SAVE_TO_HISTORY, "1");
        //chatMessage.setProperty("objectid", objectId);
        chatMessage.setDateSent(new Date().getTime() / 1000);
        chatMessage.setSenderId(ChatService.getInstance().getCurrentUser().getId());

        try {
            mChat.sendMessage(chatMessage);
        } catch (XMPPException e) {
            NhLog.e(TAG, "failed to send a message", e);
        } catch (SmackException sme) {
            NhLog.e(TAG, "failed to send a message", sme);
        }

        StringifyArrayList<Integer> userIds = new StringifyArrayList<>();
        //opponentID = ChatService.getInstance().getOpponentIDForPrivateDialog(mDialog);
        //userIds.add(opponentID);
        userIds = ChatService.getInstance().getOpponentIDsForDialog(mDialog);
        UtilPush.sendPushNotification(userIds, messageText, mDialog);

        clearTextField();

        if (mChat instanceof ChatPrivateImpl)
            showQBMessage(chatMessage);
    }

    protected void clearTextField() {
        throw new ExceptionChatUnimplemetedMethod();
    }


    //IConversationHost
    public void showQBMessage(final QBChatMessage message) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                UtilChat.updateDialog(mDialog, message);
                mAdapterPersonalMessages.addSingle(message);
                scrollDownAdapter();
            }
        });
    }

    //IConversationHost
    @Override
    public void showInfoToast(String message) {
        showToast(message);
    }

    //IConversationHost
    @Override
    public void runOnUiThread(Runnable runnable) {
        NhLog.i(TAG, "connected");
        if (getActivity() != null)
            getActivity().runOnUiThread(runnable);
    }





    protected ConnectionListener chatConnectionListener = new ConnectionListener() {
        @Override
        public void connected(XMPPConnection connection) {
            NhLog.i(TAG, "connected");
        }

        @Override
        public void authenticated(XMPPConnection xmppConnection, boolean b) {

        }

        @Override
        public void connectionClosed() {
            NhLog.i(TAG, "connectionClosed");
        }

        @Override
        public void connectionClosedOnError(final Exception e) {
            NhLog.i(TAG, "connectionClosedOnError: " + e.getLocalizedMessage());

            // leave active room
            //
            if (mDialog.getType() == QBDialogType.GROUP) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        ((ChatGroupImpl) mChat).leave();
                    }
                });
            }
        }

        @Override
        public void reconnectingIn(final int seconds) {
            if (seconds % 5 == 0) {
                NhLog.i(TAG, "reconnectingIn: " + seconds);
            }
        }

        @Override
        public void reconnectionSuccessful() {
            NhLog.i(TAG, "reconnectionSuccessful");

            // Join active room
            //
            if (mDialog.getType() == QBDialogType.GROUP) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        joinGroupChat();
                    }
                });
            }

            if (mDialog.getType() == QBDialogType.PRIVATE) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        joinGroupChat();
                    }
                });
            }
        }

        @Override
        public void reconnectionFailed(final Exception error) {
            NhLog.i(TAG, "reconnectionFailed: " + error.getLocalizedMessage());
        }
    };

    @Override
    public void onOpenMessageItem(QBChatMessage pMessage) {
        throw new ExceptionChatUnimplemetedMethod();
    }

    @Override
    public void onLoadMessageItem(RecyclerView.ViewHolder vHolder, QBChatMessage pMessage) {
        throw new ExceptionChatUnimplemetedMethod();
    }
}
