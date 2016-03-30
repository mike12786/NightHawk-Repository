package com.zapporoo.nighthawk.callbacks;

import com.zapporoo.nighthawk.adapters.AdapterFriends;
import com.zapporoo.nighthawk.adapters.AdapterFriendsGroup;
import com.zapporoo.nighthawk.adapters.AdapterFriendsRelationships;
import com.zapporoo.nighthawk.model.ModGroup;
import com.zapporoo.nighthawk.model.ModRelationship;
import com.zapporoo.nighthawk.model.ModUser;

/**
 * Created by dare on 12.1.16..
 */
public interface ICallbackFriendSearch {
    void onLoadFriendData(AdapterFriends.ViewHolderFriend vHolder, ModUser currentItem, int pAdapterId);
    void onFriendClicked(AdapterFriends.ViewHolderFriend vHolder, ModUser currentItem, int pAdapterId);
}
