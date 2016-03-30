package com.zapporoo.nighthawk.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.parse.ParseObject;
import com.zapporoo.nighthawk.R;
import com.zapporoo.nighthawk.callbacks.ICallbackFriendItem;
import com.zapporoo.nighthawk.model.ModRelationship;
import com.zapporoo.nighthawk.model.ModUser;
import com.zapporoo.nighthawk.ui.views.CircularImageView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by dare on 12.1.16..
 */
public class AdapterFriendsRelationships extends RecyclerView.Adapter<AdapterFriendsRelationships.ViewHolderFriend>{
    private final List<ModRelationship> mItems;
    private ICallbackFriendItem mFriendCallback;
    private int mAdapterId;

    public static final int ADAPTER_ID_GROUP = 1;
    public static final int ADAPTER_ID_FRIEND = 2;
    public static final int ADAPTER_ID_SEARCH_RESULTS = 3;

    public AdapterFriendsRelationships(List<ModRelationship> pRelationships, ICallbackFriendItem pFriendCallback, int pAdapterId) {
        if(pRelationships == null)
            pRelationships = new ArrayList<>();

        this.mItems = pRelationships;
        this.mFriendCallback = pFriendCallback;
        this.mAdapterId = pAdapterId;
    }

    @Override
    public int getItemCount() {
        return mItems.size();
    }

    @Override
    public void onBindViewHolder(final ViewHolderFriend vHolder, int arg1) {
        final ModRelationship currentItem = mItems.get(arg1);

        if(mFriendCallback != null) {
            mFriendCallback.onLoadRelationshipItemData(vHolder, currentItem, mAdapterId);
        }

        View.OnClickListener onItemClicked = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mFriendCallback != null) {
                    mFriendCallback.onRelationshipItemClicked(vHolder, currentItem, mAdapterId);
                }
            }
        };

        vHolder.itemView.setOnClickListener(onItemClicked);

        vHolder.imgBtnRemoveFriend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mFriendCallback.onRelationshipItemDelete(vHolder, currentItem, mAdapterId);
            }
        });

        vHolder.imgBtnStartChat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mFriendCallback.onRelationshipItemStartChat(vHolder, currentItem, mAdapterId);
            }
        });
    }

    @Override
    public int getItemViewType(int position) {
        return 0;
    }

    @Override
    public ViewHolderFriend onCreateViewHolder(ViewGroup arg0, int arg1) {
        LayoutInflater inflater = LayoutInflater.from(arg0.getContext());
        return new ViewHolderFriend(inflater.inflate(R.layout.view_friend_item_delete, arg0, false), mAdapterId);
    }

    public void updateItems(List<ModRelationship> pRelationshipItems) {
        if(pRelationshipItems == null)
            pRelationshipItems = new ArrayList<>();

        mItems.clear();
        mItems.addAll(pRelationshipItems);

        notifyDataSetChanged();
    }

    public static class ViewHolderFriend extends RecyclerView.ViewHolder {
        public final TextView tvName;
        public final ImageButton imgBtnRemoveFriend;
        public final ImageButton imgBtnStartChat;
        public final CircularImageView circImgFriend;
        public String lastId = "";

        public ViewHolderFriend(View itemView, int mAdapterId) {
            super(itemView);

            tvName = (TextView) itemView.findViewById(R.id.tvFriendName);
            imgBtnRemoveFriend = (ImageButton) itemView.findViewById(R.id.imgBtnRemoveFriend);
            imgBtnStartChat = (ImageButton) itemView.findViewById(R.id.imgBtnMessage);
            circImgFriend = (CircularImageView) itemView.findViewById(R.id.circFriendImage);

//            switch (mAdapterId) {
//                case ADAPTER_ID_SEARCH_RESULTS: {
//                    imgBtnInviteFriend.setVisibility(View.VISIBLE);
//                    break;
//                }
//                default:
//                    imgBtnInviteFriend.setVisibility(View.INVISIBLE);
//            }
        }
    }
}