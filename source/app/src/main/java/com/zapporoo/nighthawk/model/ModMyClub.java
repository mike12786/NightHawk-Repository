package com.zapporoo.nighthawk.model;

import com.parse.DeleteCallback;
import com.parse.ParseClassName;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by dare on 10.1.16..
 */
@ParseClassName("MyClub")
public class ModMyClub extends ModDefault{
    public static final String KEY_FROM = "KEY_FROM";
    public static final String KEY_TO = "KEY_TO";
    public static final String KEY_TO_YELP = "KEY_TO_YELP";
    public static final String KEY_RATING_YELP = "KEY_RATING_YELP";
    public static final String KEY_NAME_YELP = "KEY_NAME_YELP";

    public ModUser getFrom(){
        return (ModUser) get(KEY_FROM);
    }

    public void setFrom(ParseUser from){
        put(KEY_FROM, from);
    }

    public ModUser getTo(){
        return (ModUser) get(KEY_TO);
    }

    public void setTo(ModUser to){
        put(KEY_TO, to);
    }

    public String getToYelp(){
        return getString(KEY_TO_YELP);
    }

    public void setToYelp(ModUser to){
        put(KEY_TO_YELP, to.getYelpId());
    }

    public String getRatingYelp(){
        return getString(KEY_RATING_YELP);
    }

    public void setRatingYelp(ModUser to){
        put(KEY_RATING_YELP, to.getRatingString());
    }

    public String getNameYelp(){
        return getString(KEY_NAME_YELP);
    }

    public void setNameYelp(ModUser to){
        put(KEY_NAME_YELP, to.getBusinessName());
    }

    private String mName;
    private float mRating;
    private boolean mSelected;

//    public ModMyClub(String pName, float pRating, boolean pSelected) {
//        this.mName = pName;
//        this.mRating = pRating;
//        this.mSelected = pSelected;
//    }

    public String getName() {
        return mName;
    }

    public void setName(String mName) {
        this.mName = mName;
    }

    public float getRating() {
        return mRating;
    }

    public void setRating(float mRating) {
        this.mRating = mRating;
    }

    public boolean isSelected() {
        return mSelected;
    }

    public void setSelected(boolean pSelected) {
        mSelected = pSelected;
    }


    public static ParseQuery<ModMyClub> queryAllUserFavoriteClubs(ModUser user) {
        ParseQuery<ModMyClub> query = ParseQuery.getQuery(ModMyClub.class);
        query.whereEqualTo(KEY_FROM, user);
        query.include(KEY_TO);
        return query;
    }



    private static String CACHE_MY_CLUBS = "CACHE_MY_CLUBS";

    public static void saveLocally(final List<ModMyClub> initialResultList) {
        try {
            ParseObject.unpinAll(CACHE_MY_CLUBS);
            ParseObject.pinAllInBackground(CACHE_MY_CLUBS, initialResultList);
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    public static List<ModMyClub> getAllMyClubsFromCache() {
        try {
            ParseQuery<ModMyClub> query = ParseQuery.getQuery(ModMyClub.class);
            query.fromLocalDatastore();
            query.whereEqualTo(KEY_FROM, ModUser.getCurrentUser());
            query.include(KEY_TO);
            return query.find();

        } catch (ParseException e) {
            e.printStackTrace();
            return null;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static ModMyClub getClubFromCache(ModUser business) {
        try {
            ParseQuery<ModMyClub> query = ParseQuery.getQuery(ModMyClub.class);
            query.fromLocalDatastore();
            if (business.isGeneratedFromYelp())
                query.whereEqualTo(KEY_TO_YELP, business.getYelpId());
            else {
                query.whereEqualTo(KEY_TO, business);
                query.include(KEY_TO);
            }


            return query.getFirst();

        } catch (ParseException e) {
            e.printStackTrace();
            return null;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static ParseQuery<ModMyClub> getClubFromCacheQuery(ModUser business) {
        ParseQuery<ModMyClub> query = ParseQuery.getQuery(ModMyClub.class);
        query.fromLocalDatastore();
        if (business.isGeneratedFromYelp())
            query.whereEqualTo(KEY_TO_YELP, business.getYelpId());
        else {
            query.whereEqualTo(KEY_TO, business);
            query.include(KEY_TO);
        }
        return query;
    }

    public static void addSingle(ModMyClub myClub){
        List<ModMyClub> list = getAllMyClubsFromCache();

        if (list == null)
            list = new ArrayList<>();

        list.add(myClub);
        saveLocally(list);
    }

    public static void removeSingleFromCache(ModMyClub myClub){
        List<ModMyClub> list = getAllMyClubsFromCache();

        if (list == null)
            list = new ArrayList<>();

        list.remove(myClub);
        saveLocally(list);
    }

    public static void removeListFromCache(List<ModMyClub> removeList){
        List<ModMyClub> list = getAllMyClubsFromCache();

        if (list == null)
            list = new ArrayList<>();

        list.removeAll(removeList);
        saveLocally(list);
    }

    public static void cacheReleaseAll() {
        ParseObject.unpinAllInBackground(CACHE_MY_CLUBS);
    }




}
