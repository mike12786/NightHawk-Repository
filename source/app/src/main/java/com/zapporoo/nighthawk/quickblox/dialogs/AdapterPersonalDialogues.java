package com.zapporoo.nighthawk.quickblox.dialogs;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.quickblox.chat.model.QBDialog;
import com.zapporoo.nighthawk.R;
import com.zapporoo.nighthawk.ui.views.CircularImageView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by dare on 4.12.15..
 */
public class AdapterPersonalDialogues extends RecyclerView.Adapter<AdapterPersonalDialogues.ViewHolderMessages>{
    private final List<QBDialog> mItems;
    private IDialogItemCallback mMessageItemCallback;

    public AdapterPersonalDialogues(List<QBDialog> pMessages, IDialogItemCallback pMessageItemCallback) {
        if(pMessages == null)
            pMessages = new ArrayList<>();

        this.mItems = pMessages;
        this.mMessageItemCallback = pMessageItemCallback;
    }

    @Override
    public int getItemCount() {
        return mItems.size();
    }

    @Override
    public void onBindViewHolder(ViewHolderMessages vHolder, int arg1) {
        final QBDialog currentItem = mItems.get(arg1);

        if(mMessageItemCallback != null) {
            mMessageItemCallback.onLoadDialogItem(vHolder, currentItem);

            vHolder.view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mMessageItemCallback.onOpenDialogItem(currentItem);
                }
            });

            vHolder.imgBtnRemove.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mMessageItemCallback.onDeleteDialogItem(currentItem);
                }
            });
        }
    }

    @Override
    public int getItemViewType(int position) {
        return 0;
    }

    @Override
    public ViewHolderMessages onCreateViewHolder(ViewGroup arg0, int arg1) {
        LayoutInflater inflater = LayoutInflater.from(arg0.getContext());
        return new ViewHolderMessages(inflater.inflate(R.layout.list_item_dialog, arg0, false));
    }

    public void updateItems(List<QBDialog> pMessages) {
        if(pMessages == null)
            pMessages = new ArrayList<>();

        mItems.clear();
        mItems.addAll(pMessages);

        notifyDataSetChanged();
    }

    public static class ViewHolderMessages extends RecyclerView.ViewHolder {
        public final TextView tvName, tvMessage, tvMessageTime;
        public final CircularImageView circProfile;
        public final ImageButton imgBtnRemove;
        public final ImageView imgBtnOpen;
        public final View view;

        public ViewHolderMessages(View itemView) {
            super(itemView);

            view = itemView;
            tvName = (TextView) itemView.findViewById(R.id.tvProfileName);
            tvMessage = (TextView) itemView.findViewById(R.id.tvMessageText);
            tvMessageTime = (TextView) itemView.findViewById(R.id.tvMessageTime);
            imgBtnOpen = (ImageView) itemView.findViewById(R.id.imgBtnSendMessage);
            imgBtnRemove = (ImageButton) itemView.findViewById(R.id.imgBtnRemove);
            circProfile = (CircularImageView) itemView.findViewById(R.id.circProfileImage);
        }
    }
}
