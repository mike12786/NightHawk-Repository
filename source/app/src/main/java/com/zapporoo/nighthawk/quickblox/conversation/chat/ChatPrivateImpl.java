package com.zapporoo.nighthawk.quickblox.conversation.chat;

import com.quickblox.chat.QBChatService;
import com.quickblox.chat.QBPrivateChat;
import com.quickblox.chat.QBPrivateChatManager;
import com.quickblox.chat.exception.QBChatException;
import com.quickblox.chat.listeners.QBMessageListenerImpl;
import com.quickblox.chat.listeners.QBPrivateChatManagerListener;
import com.quickblox.chat.model.QBChatMessage;
import com.zapporoo.nighthawk.quickblox.conversation.IConversationHost;
import com.zapporoo.nighthawk.ui.activities.Activity_Messages_Single;
import com.zapporoo.nighthawk.util.NhLog;

import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.XMPPException;

import java.util.ArrayList;
import java.util.List;
//import com.quickblox.sample.chat.ui.activities.Activity_Chat;

public class ChatPrivateImpl extends QBMessageListenerImpl<QBPrivateChat> implements IChat, QBPrivateChatManagerListener {

    private static final String TAG = "PrivateChatManagerImpl";

    private IConversationHost conversationHost;

    private QBPrivateChatManager privateChatManager;
    private QBPrivateChat privateChat;

    public ChatPrivateImpl(IConversationHost conversationHost, Integer opponentID) {
        this.conversationHost = conversationHost;

        initManagerIfNeed();

        privateChat = privateChatManager.getChat(opponentID);
        if (privateChat == null) {
            privateChat = privateChatManager.createChat(opponentID, this);
        }else{
            privateChat.addMessageListener(this);
        }

    }

    private void initManagerIfNeed(){
        if(privateChatManager == null){
            privateChatManager = QBChatService.getInstance().getPrivateChatManager();
            privateChatManager.addPrivateChatManagerListener(this);
        }
    }

    //IChat
    @Override
    public void sendMessage(QBChatMessage message) throws XMPPException, SmackException.NotConnectedException {
        privateChat.sendMessage(message);
    }

    //IChat
    @Override
    public void release() {
        NhLog.w(TAG, "release private chat");
        privateChat.removeMessageListener(this);
        privateChatManager.removePrivateChatManagerListener(this);
    }

    @Override
    public List<Integer> getParticipants() {
        List<Integer> l = new ArrayList<>();
        l.add(privateChat.getParticipant());
        return l;
    }

    //QBMessageListenerImpl
    @Override
    public void processMessage(QBPrivateChat chat, QBChatMessage message) {
        NhLog.w(TAG, "new incoming message: " + message);
        conversationHost.showQBMessage(message);
    }

    //QBMessageListenerImpl
    @Override
    public void processError(QBPrivateChat chat, QBChatException error, QBChatMessage originChatMessage){

    }

    //IApplicationSessionStateCallback
    @Override
    public void chatCreated(QBPrivateChat incomingPrivateChat, boolean createdLocally) {
        if(!createdLocally){
            privateChat = incomingPrivateChat;
            privateChat.addMessageListener(ChatPrivateImpl.this);
        }

        NhLog.w(TAG, "private chat created: " + incomingPrivateChat.getParticipant() + ", createdLocally:" + createdLocally);
    }
}
