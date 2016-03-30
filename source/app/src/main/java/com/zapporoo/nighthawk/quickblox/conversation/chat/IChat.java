package com.zapporoo.nighthawk.quickblox.conversation.chat;

import com.quickblox.chat.model.QBChatMessage;

import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.XMPPException;

import java.util.List;

public interface IChat {
    String EXTRA_DIALOG = "dialog";

    void sendMessage(QBChatMessage message) throws XMPPException, SmackException.NotConnectedException;

    void release() throws XMPPException;
    List<Integer> getParticipants();
}
