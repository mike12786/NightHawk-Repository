package com.zapporoo.nighthawk.util;

import com.yelp.clientlib.entities.Business;
import com.yelp.clientlib.entities.Deal;
import com.yelp.clientlib.entities.SearchResponse;
import com.zapporoo.nighthawk.App;

import java.util.HashMap;
import java.util.Map;

import retrofit.Call;
import retrofit.Callback;
import retrofit.Response;
import retrofit.Retrofit;

/**
 * Created by Pile on 2/20/2016.
 */
public class UtilYelp {

    static int MAX_RESULTS = 300;

    public static void testYelpSearch(Callback<SearchResponse> response_callback, int offset) {
        if (offset > MAX_RESULTS)
            return;

        Map<String, String> params = new HashMap<>();

        // general params
        //params.put("term", "food");
        //params.put("limit", "30");
        params.put("offset", String.valueOf(offset));
        // locale params
        //params.put("lang", "fr");
        params.put("category_filter", "restaurants,bars,cafes");

        Call<SearchResponse> call = App.yelpAPI.search("San Francisco", params);
        Callback<SearchResponse> callback = new Callback<SearchResponse>() {
            @Override
            public void onResponse(Response<SearchResponse> response, Retrofit retrofit) {
                SearchResponse searchResponse = response.body();

                for (Business business:searchResponse.businesses()){
                    NhLog.e("Business result:", "" + business.id() + " #  " + business.name());
                    business.id();
                    business.name();
                    business.imageUrl();
                    business.phone();
                    business.displayPhone();
                    business.deals();
                    business.rating();
                    business.url();
                    business.location().coordinate().latitude();
                    business.location().coordinate().longitude();

                    if (business.deals() != null)
                        for (Deal deal:business.deals())
                            NhLog.e("---deal:", deal.title());
                }

                //testYelpSearch();
            }
            @Override
            public void onFailure(Throwable t) {
                // HTTP error happened, do something to handle it.
                //showToast("Error");
            }
        };

        call.enqueue(response_callback);

    }
}
