package com.zapporoo.nighthawk.util;

import android.os.AsyncTask;
import android.util.Log;

import com.parse.ParseGeoPoint;
import com.yelp.clientlib.entities.Business;
import com.yelp.clientlib.entities.Deal;
import com.yelp.clientlib.entities.SearchResponse;
import com.yelp.clientlib.entities.options.CoordinateOptions;
import com.yelp.clientlib.exception.exceptions.InvalidParameter;
import com.yelp.clientlib.exception.exceptions.UnavailableForLocation;
import com.zapporoo.nighthawk.App;
import com.zapporoo.nighthawk.callbacks.ICallbackSearchableAdapter;
import com.zapporoo.nighthawk.ui.activities.ActivityDefault;
import com.zapporoo.nighthawk.ui.fragments.Fragment_Personal_Home_Search;

import java.util.HashMap;
import java.util.Map;

import retrofit.Call;
import retrofit.Callback;
import retrofit.Response;
import retrofit.Retrofit;

/**
 * Created by dare on 20.2.16..
 */
public class UtilSearch {

    public static Call<SearchResponse> yelpSearch(final Fragment_Personal_Home_Search frag,
                                                  final String search_text,
                                                  final ICallbackSearchableAdapter<SearchResponse> pAdapter,
                                                  final boolean searchMore,
                                                  final String location_text) {

        return yelpSearchGPS(frag, search_text, pAdapter, searchMore, location_text, null, 0 );
    }

    public static Call<SearchResponse> yelpSearchGPS(final Fragment_Personal_Home_Search frag,
                                              final String search_text,
                                              final ICallbackSearchableAdapter<SearchResponse> pAdapter,
                                              final boolean searchMore,
                                              final String location_text,
                                              final ParseGeoPoint pointGPS,
                                              final int radiusMiles) {

        if (searchMore || pAdapter.getCurrentSearchOffset() < pAdapter.getMaxSearchResults()) {
            final int searchId = pAdapter.getCurrentSearchId();
            Map<String, String> params = new HashMap<>();
            Call<SearchResponse> call;

            if (pointGPS != null){
                params.put("term", search_text + " deals");
                params.put("offset", String.valueOf(pAdapter.getCurrentSearchOffset()));
                params.put("category_filter", "restaurants,bars,cafes");

                CoordinateOptions options = CoordinateOptions.builder()
                        .latitude(pointGPS.getLatitude())
                        .longitude(pointGPS.getLongitude())
                        .accuracy(1d)
                        .build();

                params.put("radius_filter", "" + radiusMiles * 1600);
                call = App.yelpAPI.search(options, params);
            }else{
                params.put("term", search_text + " deals");
                params.put("offset", String.valueOf(pAdapter.getCurrentSearchOffset()));
                params.put("category_filter", "restaurants,bars,cafes");
                call = App.yelpAPI.search(location_text, params);
            }

            Callback<SearchResponse> callback = new Callback<SearchResponse>() {
                @Override
                public void onResponse(Response<SearchResponse> response, Retrofit retrofit) {
                    TaskPrepareResults taskPrepareResults = new TaskPrepareResults();


                    if(searchId == pAdapter.getCurrentSearchId()) {
                        SearchResponse searchResponse = response.body();

                        taskPrepareResults.pAdapter = pAdapter;
                        taskPrepareResults.frag = frag;
                        taskPrepareResults.search_text = search_text;
                        taskPrepareResults.searchMore = searchMore;
                        taskPrepareResults.location_text = location_text;
                        taskPrepareResults.pointGPS = pointGPS;
                        taskPrepareResults.radiusMiles = radiusMiles;
                        taskPrepareResults.searchResponse = searchResponse;
                        taskPrepareResults.execute();

//                        pAdapter.onSearchResults(searchResponse);
//                        Log.e("RESULTS:", " " + searchResponse.businesses().size());
//
//                        if(searchResponse.businesses() != null && searchResponse.businesses().size() > 0) { // zavrsi rekurziju ako nista nije nadjeno.
//                            frag.mYelpSearchTask = yelpSearchGPS(frag, search_text, pAdapter, false, location_text, pointGPS, radiusMiles);
//                        }else if (frag != null){
//                            frag.yelpSearchLocationComplete();
//                        }
                    }
                }

                @Override
                public void onFailure(Throwable t) {
                    if (t instanceof UnavailableForLocation){
                        frag.showOkDialog(((UnavailableForLocation)t).getText(), null);
                    }
                    Log.e("YELP onFailure", t.getLocalizedMessage());
                    frag.searchError(t);
                }
            };

            call.enqueue(callback);
            return call;
        }else
            frag.yelpSearchLocationComplete();
        return null;
    }


    public static void getBusiness(String businessId, Callback<Business> callback){
        Map<String, String> params = new HashMap<>();
        // general params
        params.put("limit", "20");
        Call<Business> call = App.yelpAPI.getBusiness(businessId, params);
        //Response<Business> response =
        call.enqueue(callback);
    }
}
