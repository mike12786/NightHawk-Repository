package com.zapporoo.nighthawk.callbacks;

/**
 * Created by dare on 20.2.16..
 */
public interface ICallbackSearchableAdapter <T>{
    int getCurrentSearchOffset();

    int getMaxSearchResults();

    void onSearchResults(T searchResults);

    int getCurrentSearchId();

}
