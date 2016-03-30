package com.zapporoo.nighthawk.callbacks;

import com.parse.ParseObject;
import com.zapporoo.nighthawk.adapters.AdapterFriends;
import com.zapporoo.nighthawk.adapters.AdapterFriendsGroup;
import com.zapporoo.nighthawk.adapters.AdapterFriendsRelationships;
import com.zapporoo.nighthawk.model.ModGroup;
import com.zapporoo.nighthawk.model.ModRelationship;
import com.zapporoo.nighthawk.model.ModUser;

/**
 * Created by dare on 12.1.16..
 */
public interface ICallbackFriendItem {
    void onLoadGroupItemData(AdapterFriendsGroup.ViewHolderFriend vHolder, ModGroup currentItem, int pAdapterId);
    void onGroupItemClicked(AdapterFriendsGroup.ViewHolderFriend vHolder, ModGroup currentItem, int pAdapterId);

    void onLoadRelationshipItemData(AdapterFriendsRelationships.ViewHolderFriend vHolder, ModRelationship currentItem, int pAdapterId);
    void onRelationshipItemClicked(AdapterFriendsRelationships.ViewHolderFriend vHolder, ModRelationship currentItem, int pAdapterId);
    void onRelationshipItemDelete(AdapterFriendsRelationships.ViewHolderFriend vHolder, ModRelationship currentItem, int pAdapterId);
    void onRelationshipItemStartChat(AdapterFriendsRelationships.ViewHolderFriend vHolder, ModRelationship currentItem, int pAdapterId);
}
