package com.zapporoo.nighthawk.quickblox.conversation.chat;

import android.os.Bundle;
import android.util.Log;

import com.quickblox.chat.QBChatService;
import com.quickblox.chat.QBGroupChat;
import com.quickblox.chat.QBGroupChatManager;
import com.quickblox.chat.exception.QBChatException;
import com.quickblox.chat.listeners.QBMessageListenerImpl;
import com.quickblox.chat.model.QBChatMessage;
import com.quickblox.chat.model.QBDialog;
import com.quickblox.core.QBEntityCallback;
import com.quickblox.core.exception.QBResponseException;
import com.zapporoo.nighthawk.quickblox.conversation.IConversationHost;

import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smackx.muc.DiscussionHistory;

import java.util.ArrayList;
import java.util.List;

public class ChatGroupImpl extends QBMessageListenerImpl<QBGroupChat> implements IChat {
    private static final String TAG = ChatGroupImpl.class.getSimpleName();

    private IConversationHost conversationHost;

    private QBGroupChatManager groupChatManager;
    private QBGroupChat groupChat;

    public ChatGroupImpl(IConversationHost conversationHost) {
        this.conversationHost = conversationHost;
    }

    public void joinGroupChat(QBDialog dialog, QBEntityCallback callback){
        initManagerIfNeed();

        if(groupChat == null) {
            groupChat = groupChatManager.createGroupChat(dialog.getRoomJid());
        }
        join(groupChat, callback);
    }

    private void initManagerIfNeed(){
        if(groupChatManager == null){
            groupChatManager = QBChatService.getInstance().getGroupChatManager();
        }
    }

    private void join(final QBGroupChat groupChat, final QBEntityCallback callback) {
        DiscussionHistory history = new DiscussionHistory();
        history.setMaxStanzas(0);

        //Toast.makeText(activityChat, "Joining room...", Toast.LENGTH_SHORT).show();

        groupChat.join(history, new QBEntityCallback() {
            @Override
            public void onSuccess(final Object o, final Bundle bundle) {
                groupChat.addMessageListener(ChatGroupImpl.this);

                conversationHost.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        callback.onSuccess(o, bundle);
                        //conversationHost.showInfoToast("Join successful");
                    }
                });
                Log.w("IChat", "Join successful");
            }

            @Override
            public void onError(final QBResponseException e) {
                conversationHost.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        callback.onError(e);
                    }
                });

                Log.w("Could not join chat:", " " + e.getErrors());
            }

        });
    }

    public void leave(){
        try {
            groupChat.leave();
        } catch (SmackException.NotConnectedException nce) {
            nce.printStackTrace();
        } catch (XMPPException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void release() throws XMPPException {
        if (groupChat != null) {
            leave();
            groupChat.removeMessageListener(this);
        }
    }

    @Override
    public List<Integer> getParticipants() {
        try {
            List<Integer> l = new ArrayList<>();
            l.addAll(groupChat.getOnlineUsers());
            return l;
        } catch (XMPPException e) {
            e.printStackTrace();
        }

        return null;
    }

    @Override
    public void sendMessage(QBChatMessage message) throws XMPPException, SmackException.NotConnectedException {
        if (groupChat != null) {
            try {
                groupChat.sendMessage(message);
            } catch (SmackException.NotConnectedException nce){
                nce.printStackTrace();
                conversationHost.showInfoToast("Can't send a message, You are not connected to chat");
            } catch (IllegalStateException e){
                e.printStackTrace();
                //conversationHost.showInfoToast("You are still joining a group chat, please wait a bit");
                conversationHost.showInfoToast("You are removed from this group chat...");
            }
        } else {
            conversationHost.showInfoToast("Join unsuccessful");
        }
    }

    @Override
    public void processMessage(QBGroupChat groupChat, QBChatMessage chatMessage) {
        Log.w(TAG, "new incoming message: " + chatMessage);
        conversationHost.showQBMessage(chatMessage);
    }

    @Override
    public void processError(QBGroupChat groupChat, QBChatException error, QBChatMessage originMessage){

    }
}
