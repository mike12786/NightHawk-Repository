package com.zapporoo.nighthawk.model;

import com.parse.DeleteCallback;
import com.parse.ParseClassName;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.List;

/**
 * Created by Pile on 1/7/2016.
 */
@ParseClassName("Rating")
public class ModRating extends ModDefault{

    public static final String KEY_FROM = "KEY_FROM";
    public static final String KEY_TO = "KEY_TO";
    public static final String KEY_RATING = "KEY_RATING";
        public static final int VALUE_RATING_0_STARS = 0;
        public static final int VALUE_RATING_1_STARS = 1;
        public static final int VALUE_RATING_2_STARS = 2;
        public static final int VALUE_RATING_3_STARS = 3;
        public static final int VALUE_RATING_4_STARS = 4;
        public static final int VALUE_RATING_5_STARS = 5;
    public static final String KEY_COMMENT = "KEY_COMMENT";

    private static final String CACHE_RATING = "CACHE_RATING";
    private static final String KEY_CREATED_AT = "createdAt";

    public ModUser getFrom(){
        return (ModUser) get(KEY_FROM);
    }

    public void setFrom(ParseUser from){
        put(KEY_FROM, from);
    }

    public ModUser getTo(){
        return (ModUser) get(KEY_TO);
    }

    public void setTo(ParseUser to){
        put(KEY_TO, to);
    }

    public void setValue(int value){
        put(KEY_RATING, value);
    }

    public int getValue(){
        return getInt(KEY_RATING);
    }

    public void setComment(String comment){
        if (comment == null)
            return;
        put(KEY_COMMENT, comment);
    }

    public String getComment(){
        return getString(KEY_COMMENT);
    }

    public static ModRating addNewRating(String comment, int stars, ParseUser business){
        ModRating rating = new ModRating();
        rating.setFrom(ModUser.getModUser());
        rating.setTo(business);
        rating.setComment(comment);
        rating.setValue(stars);
        return rating;
    }

    public static ParseQuery<ModRating> queryAllRatingsOfBusiness(ModUser ModUser) {
        ParseQuery<ModRating> query = ParseQuery.getQuery(ModRating.class);
        query.whereEqualTo(KEY_TO, ModUser);
        query.include(KEY_FROM);
        query.orderByDescending(KEY_CREATED_AT);
        return query;
    }

    public static void saveLocally(final List<ModRating> initialResultList) {
        ParseObject.unpinAllInBackground(CACHE_RATING, new DeleteCallback() {
            public void done(ParseException e) {
                if (e != null) {
                    // There was some error.
                    return;
                }
                // Add the latest results for this query to the cache.
                ParseObject.pinAllInBackground(CACHE_RATING, initialResultList);
            }
        });
    }

    public static List<ModRating> getAllBusinessLocalCache(ModUser business) {
        try {
            ParseQuery<ModRating> query = ParseQuery.getQuery(ModRating.class);
            query.fromLocalDatastore();
            query.whereEqualTo(KEY_TO, business);
            return query.find();

        } catch (ParseException e) {
            e.printStackTrace();
            return null;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static void cacheReleaseAll() {
        ParseObject.unpinAllInBackground(CACHE_RATING, new DeleteCallback() {
            public void done(ParseException e) {

            }
        });
    }

}
