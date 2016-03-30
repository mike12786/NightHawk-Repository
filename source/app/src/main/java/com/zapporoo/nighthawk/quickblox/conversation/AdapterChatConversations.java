package com.zapporoo.nighthawk.quickblox.conversation;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.quickblox.chat.model.QBChatMessage;
import com.zapporoo.nighthawk.R;
import com.zapporoo.nighthawk.ui.views.CircularImageView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by dare on 3.2.16..
 */
public class AdapterChatConversations extends RecyclerView.Adapter<AdapterChatConversations.ViewHolderMessage> {
    private final List<QBChatMessage> mItems;
    private ICallbackMessageItem mMessageCallback;
    private int ownerId;

    public AdapterChatConversations(List<QBChatMessage> pMessages, ICallbackMessageItem pMessageCallback, int ownerId) {
        if (pMessages == null)
            pMessages = new ArrayList<>();

        this.ownerId = ownerId;
        this.mItems = pMessages;
        this.mMessageCallback = pMessageCallback;
    }

    @Override
    public int getItemCount() {
        return mItems.size();
    }

    @Override
    public void onBindViewHolder(final AdapterChatConversations.ViewHolderMessage vHolder, int arg1) {
        final QBChatMessage currentItem = mItems.get(arg1);

        if (mMessageCallback != null) {
            mMessageCallback.onLoadMessageItem(vHolder, currentItem);
        }
    }

    @Override
    public int getItemViewType(int position) {
        final QBChatMessage currentItem = mItems.get(position);

        if(currentItem.getSenderId() == ownerId)
            return 0;
        else
            return 1;
    }

    public void addSingle(QBChatMessage message) {
        mItems.add(message);
        notifyDataSetChanged();
    }

    @Override
    public ViewHolderMessage onCreateViewHolder(ViewGroup arg0, int arg1) {
        switch (arg1) {
            case 0: {
                LayoutInflater inflater = LayoutInflater.from(arg0.getContext());
                return new ViewHolderMessage(inflater.inflate(R.layout.view_message_sent, arg0, false));
            }
            case 1: {
                LayoutInflater inflater = LayoutInflater.from(arg0.getContext());
                return new ViewHolderMessage(inflater.inflate(R.layout.view_message_received, arg0, false));
            }
            default:
                return null;
        }
    }

    public void updateItems(List<QBChatMessage> pMessages) {
        if (pMessages == null)
            pMessages = new ArrayList<>();

        mItems.clear();
        mItems.addAll(pMessages);

        notifyDataSetChanged();
    }

    public static class ViewHolderMessage extends RecyclerView.ViewHolder {
        public final TextView tvMessage;
        public final CircularImageView circProfileImage;

        public ViewHolderMessage(View itemView) {
            super(itemView);

            tvMessage = (TextView) itemView.findViewById(R.id.tvMessageText);
            circProfileImage = (CircularImageView) itemView.findViewById(R.id.circProfileImage);
        }
    }
}