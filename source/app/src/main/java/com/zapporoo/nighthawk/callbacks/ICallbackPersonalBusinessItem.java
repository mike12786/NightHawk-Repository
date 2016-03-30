package com.zapporoo.nighthawk.callbacks;

import android.support.v7.widget.RecyclerView;

import com.yelp.clientlib.entities.SearchResponse;
import com.zapporoo.nighthawk.model.ModUser;

/**
 * Created by dare on 10.1.16..
 */
public interface ICallbackPersonalBusinessItem {
    void onLoadBusiness(RecyclerView.ViewHolder pHotDealViewHolder, ModUser pBusinessData);

    void onBusinessOpen(ModUser pBusinessData);

    void onFavoriteBusiness(ModUser pBusinessData);

    void onCheckBoxClick(ModUser pBusinessData);

    int getCurrentSearchId();

    void onSearchResults(SearchResponse pSearchResults);

}