package com.zapporoo.nighthawk.model;

import com.parse.ParseClassName;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Pile on 1/7/2016.
 */
@ParseClassName("Relationship")
public class ModRelationship extends ModDefault {
    public static final String KEY_FROM = "KEY_FROM";
    public static final String KEY_TO = "KEY_TO";
    public static final String KEY_STATUS = "KEY_STATUS";
    public static final int VALUE_STATUS_NONE = -1;
    public static final int VALUE_STATUS_REQUESTED = 2;
    public static final int VALUE_STATUS_FRIENDS = 3;

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

    public static ParseQuery<ModRelationship> queryFriendRequestsToMe() {
        ParseQuery<ModRelationship> query = ParseQuery.getQuery(ModRelationship.class);
        query.whereEqualTo(KEY_TO, ModUser.getModUser());
        query.whereEqualTo(KEY_STATUS, VALUE_STATUS_REQUESTED);
        query.include(KEY_FROM);
        return query;
    }

    public static ParseQuery<ModRelationship> getToString(String to) {
        ParseQuery<ModRelationship> query = ParseQuery.getQuery(ModRelationship.class);
        query.whereEqualTo(KEY_TO, to);
        return query;
    }

    public static ParseQuery<ModRelationship> queryFriendsAll() {
        ParseQuery<ModRelationship> queryFrom = ParseQuery.getQuery(ModRelationship.class);
        queryFrom.whereEqualTo(KEY_FROM, ModUser.getModUser());
        queryFrom.whereEqualTo(KEY_STATUS, VALUE_STATUS_FRIENDS);

        ParseQuery<ModRelationship> queryTo = ParseQuery.getQuery(ModRelationship.class);
        queryTo.whereEqualTo(KEY_STATUS, VALUE_STATUS_FRIENDS);
        queryTo.whereEqualTo(KEY_TO, ModUser.getModUser());

        List<ParseQuery<ModRelationship>> queries = new ArrayList<>();
        queries.add(queryFrom);
        queries.add(queryTo);


        ParseQuery<ModRelationship> return_query = ParseQuery.or(queries);
        return_query.include(KEY_TO);
        return_query.include(KEY_FROM);

        return return_query;
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

    public static List<ModUser> extractNonFriends(List<ModRelationship> currentFriends, List<ModUser> searchResults) {
        List<ModUser> results = new ArrayList<>();

        for (ModUser user:searchResults)
            if (isFriend(user, currentFriends))
                continue;
            else
                results.add(user);

        return results;
    }

    private static boolean isFriend(ModUser user, List<ModRelationship> currentFriends) {
        for (ModRelationship rel:currentFriends){
            if (user.isEqual(rel.extractNotMe()))
                return true;
        }
        return false;

    }

    public ModUser getNotMe() {
        if (ModUser.getModUser().isEqual(getFrom()))
                return getTo();
        else
            return getFrom();
    }
}
