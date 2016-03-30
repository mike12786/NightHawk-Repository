package com.zapporoo.nighthawk.quickblox.conversation;

import com.quickblox.chat.model.QBChatMessage;
import com.quickblox.chat.model.QBDialog;

/**
 * Created by Pile on 2/5/2016.
 */
public interface IConversationHost {
    void showQBMessage(QBChatMessage message);
    void showInfoToast(String message);

    void runOnUiThread(Runnable runnable);
}
