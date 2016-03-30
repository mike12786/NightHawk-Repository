package com.zapporoo.nighthawk.util;

/**
 * Created by Pile on 3/3/2016.
 */

import android.os.AsyncTask;
import android.util.Log;

import com.parse.ParseGeoPoint;
import com.yelp.clientlib.entities.SearchResponse;
import com.zapporoo.nighthawk.callbacks.ICallbackSearchableAdapter;
import com.zapporoo.nighthawk.ui.fragments.Fragment_Personal_Home_Search;

public class TaskPrepareResults extends AsyncTask<Integer, Void, Integer> {
    public Fragment_Personal_Home_Search frag;
    public String search_text;
    public ICallbackSearchableAdapter<SearchResponse> pAdapter;
    public boolean searchMore;
    public String location_text;
    public ParseGeoPoint pointGPS;
    public int radiusMiles;
    public SearchResponse searchResponse;

    @Override
    protected Integer doInBackground(Integer[] params) {
        pAdapter.onSearchResults(searchResponse);
        Log.e("RESULTS:", " " + searchResponse.businesses().size());
        if(searchResponse.businesses() != null && searchResponse.businesses().size() > 0) { // zavrsi rekurziju ako nista nije nadjeno.
            frag.mYelpSearchTask = UtilSearch.yelpSearchGPS(frag, search_text, pAdapter, false, location_text, pointGPS, radiusMiles);
        }else if (frag != null){
            return 0;
        }

        return -1;
    }

    @Override
    protected void onPostExecute(Integer o) {
        if (o == 0){
            frag.yelpSearchLocationComplete();
        }
        super.onPostExecute(o);
    }
}