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
 * Created by Pile on 1/7/2016.
 */
@ParseClassName("CheckIn")
public class ModCheckIn extends ModDefault {
    public static final String KEY_FROM = "KEY_FROM";
    public static final String KEY_TO = "KEY_TO";
    public static final String KEY_TO_YELP = "KEY_TO_YELP";


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
        return  getString(KEY_TO_YELP);
    }

    public void setToYelp(String to){
        put(KEY_TO_YELP, to);
    }

    public static ModCheckIn addNewCheckIn(ModUser business){
        ModCheckIn checkIn = new ModCheckIn();
        checkIn.setFrom(ModUser.getModUser());
        checkIn.setTo(business);
        return checkIn;
    }

    public static ParseQuery<ModCheckIn> queryAllWhoAreCheckedInBusiness(ModUser business) {
        ParseQuery<ModCheckIn> query = ParseQuery.getQuery(ModCheckIn.class);

        if (business.isGeneratedFromYelp()) {
            query.whereEqualTo(KEY_TO_YELP, business.getYelpId());
        }
        else {
            query.whereEqualTo(KEY_TO, business);
        }

        query.include(KEY_FROM);

        return query;
    }

    public static ParseQuery<ModCheckIn> queryAllWhereUserIsCheckedIn(ModUser user) {
        ParseQuery<ModCheckIn> query = ParseQuery.getQuery(ModCheckIn.class);
        query.whereEqualTo(KEY_FROM, user);
        query.include(KEY_TO);
        return query;
    }

    private static String CACHE_MY_CHECK_INS = "CACHE_MY_CHECK_INS";

    public static void saveLocally(final List<ModCheckIn> initialResultList) {
        try {
            ParseObject.unpinAll(CACHE_MY_CHECK_INS);
            ParseObject.pinAllInBackground(CACHE_MY_CHECK_INS, initialResultList);
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    public static List<ModCheckIn> getAllUserCheckInsFromLocalCache() {
        try {
            ParseQuery<ModCheckIn> query = ParseQuery.getQuery(ModCheckIn.class);
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

    public static ModCheckIn getUserCheckedInThisBusinessCache(ModUser user, ModUser business) {
        try {
            ParseQuery<ModCheckIn> query = ParseQuery.getQuery(ModCheckIn.class);
            query.fromLocalDatastore();
            query.whereEqualTo(KEY_FROM, user);

            if (business.isGeneratedFromYelp())
                query.whereEqualTo(KEY_TO_YELP, business.getYelpId());
            else
                query.whereEqualTo(KEY_TO, business);

            return query.getFirst();

        } catch (ParseException e) {
            e.printStackTrace();
            return null;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static ParseQuery<ModCheckIn> getCheckInFromCacheQuery(ModUser business) {
            ParseQuery<ModCheckIn> query = ParseQuery.getQuery(ModCheckIn.class);
            query.fromLocalDatastore();

            if (business.isGeneratedFromYelp())
                query.whereEqualTo(KEY_TO_YELP, business.getYelpId());
            else
                query.whereEqualTo(KEY_TO, business);

            return query;

    }

    public static void addSingle(ModCheckIn checkIn){
        List<ModCheckIn> list = getAllUserCheckInsFromLocalCache();

        if (list == null)
            list = new ArrayList<>();

        list.add(checkIn);
        saveLocally(list);
    }

    public static void removeSingle(ModCheckIn checkIn){
        List<ModCheckIn> list = getAllUserCheckInsFromLocalCache();

        if (list == null)
            list = new ArrayList<>();

        list.remove(checkIn);
        saveLocally(list);
    }

    public static void cacheReleaseAll() {
        ParseObject.unpinAllInBackground(CACHE_MY_CHECK_INS, new DeleteCallback() {
            public void done(ParseException e) {

            }
        });
    }

    public static ParseQuery<ModCheckIn> queryAllWhoAreCheckedInBusiness(List<ModUser> businessList) {
        List<ModUser> yelpList = new ArrayList<>();
        List<ModUser> parseList = new ArrayList<>();

        for (ModUser business:businessList){
            if (business.isGeneratedFromYelp())
                yelpList.add(business);
            else
                parseList.add(business);
        }

        ParseQuery<ModCheckIn> queryYelp = ParseQuery.getQuery(ModCheckIn.class);
        queryYelp.whereContainedIn(KEY_TO_YELP, getYelpIdList(yelpList) );

        ParseQuery<ModCheckIn> queryParse = ParseQuery.getQuery(ModCheckIn.class);
        queryParse.whereContainedIn(KEY_TO, parseList);


        List<ParseQuery<ModCheckIn>> queries = new ArrayList<>();
        queries.add(queryYelp);
        queries.add(queryParse);

        ParseQuery<ModCheckIn> return_query = ParseQuery.or(queries);
        return_query.include(KEY_TO);

        return return_query;
    }

    private static List<String> getYelpIdList(List<ModUser> businessList){
        List<String> idList = new ArrayList<>();

        for (ModUser business:businessList)
            idList.add(business.getYelpId());

        return idList;
    }
}
