package com.zapporoo.nighthawk.quickblox.conversation;

import android.support.v7.widget.RecyclerView;

import com.quickblox.chat.model.QBChatMessage;

/**
 * Created by dare on 12.1.16..
 */
public interface ICallbackMessageItem {

    void onOpenMessageItem(QBChatMessage pMessage);

    void onLoadMessageItem(RecyclerView.ViewHolder vHolder, QBChatMessage pMessage);
}
