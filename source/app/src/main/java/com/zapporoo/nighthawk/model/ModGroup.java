package com.zapporoo.nighthawk.model;

import com.parse.DeleteCallback;
import com.parse.ParseClassName;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Pile on 1/7/2016.
 */
@ParseClassName("Group")
public class ModGroup extends ModDefault {
    public static final String KEY_FROM = "KEY_FROM";
    public static final String KEY_TO = "KEY_TO";
    public static final String KEY_STATUS = "KEY_STATUS";
    public static final int VALUE_STATUS_NONE = -1;
    public static final int VALUE_STATUS_REQUESTED = 2;
    public static final int VALUE_STATUS_FRIENDS = 3;

    private static final String CACHE_GROUP = "CACHE_GROUP";

    public int getStatus(){
        return getInt(KEY_STATUS);
    }

    public void setStatus(int status){
        put(KEY_STATUS, status);
    }

    public ModUser getFrom(){
        return (ModUser) get(KEY_FROM);
    }

    public void setFrom(ModUser from){
        put(KEY_FROM, from);
    }

    public ModUser getTo(){
        return (ModUser) get(KEY_TO);
    }

    public void setTo(ModUser to){
        put(KEY_TO, to);
    }

    public static ParseQuery<ModGroup> queryMyGroup() {
        ParseQuery<ModGroup> query = ParseQuery.getQuery(ModGroup.class);
        query.whereEqualTo(KEY_FROM, ModUser.getModUser());
        query.include(KEY_TO);
        return query;
    }

    public ModUser extractNotMe() {
        if (isEqualLogged(getFrom()))
            return getTo();
        else
            return getFrom();
    }

    public static boolean isEqualLogged(ModUser user) {
        try{
            if (user.getObjectId().equals(ModUser.getModUser().getObjectId()))
                return true;
        }catch (Exception e){
            return false;
        }
        return false;
    }


    public static void saveLocally(final List<ModGroup> initialResultList) {
        ParseObject.unpinAllInBackground(CACHE_GROUP, new DeleteCallback() {
            public void done(ParseException e) {
                if (e != null) {
                    // There was some error.
                    return;
                }
                // Add the latest results for this query to the cache.
                ParseObject.pinAllInBackground(CACHE_GROUP, initialResultList);
            }
        });
    }

    public static List<ModGroup> getAllCached() {
        try {
            ParseQuery<ModGroup> query = ParseQuery.getQuery(ModGroup.class);
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

    public static void addSingle(ModGroup checkIn){
        List<ModGroup> list = getAllCached();

        if (list == null)
            list = new ArrayList<>();

        list.add(checkIn);
        saveLocally(list);
    }

    public static void removeSingle(ModGroup checkIn){
        List<ModGroup> list = getAllCached();

        if (list == null)
            list = new ArrayList<>();

        list.remove(checkIn);
        saveLocally(list);
    }

    public static void cacheReleaseAll() {
        ParseObject.unpinAllInBackground(CACHE_GROUP, new DeleteCallback() {
            public void done(ParseException e) {

            }
        });
    }

    public static ArrayList<Integer> toIntegetList(List<ModGroup> allCached) {
        ArrayList<Integer> ret = new ArrayList<>();
        for (ModGroup group:allCached){
            ret.add(group.getTo().getChatId());
        }
        return ret;
    }
}
