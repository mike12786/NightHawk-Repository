package com.zapporoo.nighthawk.callbacks;

import com.zapporoo.nighthawk.model.ModMyClub;

/**
 * Created by dare on 12.1.16..
 */
public interface ICallbackMyClubItem {
    public void onItemFavorite(ModMyClub pItem);

    void onItemSelectionChanged(ModMyClub currentItem, boolean isChecked);
}
