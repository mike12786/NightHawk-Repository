package com.zapporoo.nighthawk.model;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import com.parse.DeleteCallback;
import com.parse.GetCallback;
import com.parse.GetDataCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseGeoPoint;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.ProgressCallback;
import com.parse.SaveCallback;
import com.quickblox.users.model.QBUser;
import com.yelp.clientlib.entities.Business;
import com.yelp.clientlib.entities.Category;
import com.yelp.clientlib.entities.Deal;
import com.zapporoo.nighthawk.R;
import com.zapporoo.nighthawk.adapters.AdapterPersonalBusinessSearch;
import com.zapporoo.nighthawk.callbacks.CallbackImageDefault;
import com.zapporoo.nighthawk.callbacks.ICallbackImage;
import com.zapporoo.nighthawk.util.UtilImage;
import com.zapporoo.nighthawk.util.XxUtil;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Created by Pile on 12/14/2015.
 */

public class ModUser extends ParseUser {
    //
    public static final String KEY_EMAIL = "email";
    public static final String KEY_PROFILE_IMAGE = "KEY_PROFILE_IMAGE";
    public static final String KEY_NAME_SEARCH = "KEY_NAME_SEARCH"; //lower case

    public static final String KEY_TYPE = "KEY_TYPE";
    public static final int VALUE_TYPE_PERSONAL = 1;
    public static final int VALUE_TYPE_BUSINESS = 2;

    private static final String KEY_ID = "objectId";
    private static final String KEY_EMAIL_SEARCH = "KEY_EMAIL_SEARCH";
    public static final String EXTRA_SIGN_ME_IN = "EXTRA_SIGN_ME_IN";
    public static final String KEY_OBJECT_ID = "objectId";

    private static final String KEY_YELP_GENERATED_FROM = "KEY_YELP_GENERATED_FROM";
    private static final String KEY_YELP_COMMENT = "KEY_YELP_COMMENT";
    private static final String KEY_YELP_ID = "KEY_YELP_ID";
    private static final String KEY_YELP_SEARCH_RADIUS_MILES = "KEY_YELP_SEARCH_RADIUS_MILES";
    //
    public static int IMAGE_COMPRESSION_QUALITY = 85;
    public static String IMAGE_FILE_NAME = "image_profile.jpg";
    private Bitmap profileImageBitmap;
    public static final String KEY_CHAT_ID = "KEY_CHAT_ID";
    public static final String KEY_GROUP_CHAT_ID = "KEY_GROUP_CHAT_ID";

    private boolean generatedFromYelp;
    private String yelpImageUrl;

    public static final String KEY_SHOW_MESSAGES_WARNING_DIALOG = "KEY_SHOW_MESSAGES_WARNING_DIALOG";
    public int checkIns = 0;

    public void setShowMessageDialog(boolean enabled) {
        put(KEY_SHOW_MESSAGES_WARNING_DIALOG, enabled);
    }

    public Boolean getShowMessageDialog() {
        return getBoolean(KEY_SHOW_MESSAGES_WARNING_DIALOG);
    }

    public void setGroupChatId(String chatId) {
        put(KEY_GROUP_CHAT_ID, chatId);
    }

    public String getGroupChatId(){
        return getString(KEY_GROUP_CHAT_ID);
    }

    //EXTRAS
    public static final String EXTRA_BUSINESS_ID = "EXTRA_BUSINESS_ID";


    public int getType() {
        return getInt(KEY_TYPE);
    }

    public void setProfileImage(ParseFile file) {
        put(KEY_PROFILE_IMAGE, file);
    }


    public static void persistImageFile(Bitmap bm, Context context, SaveCallback saveCallback, ProgressCallback progressCallback, ModUser user) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bm.compress(Bitmap.CompressFormat.JPEG, IMAGE_COMPRESSION_QUALITY, stream);
        user.persistFile(KEY_PROFILE_IMAGE, "image_profile.jpg", stream.toByteArray(), context, saveCallback, progressCallback);
    }

    public static ParseQuery<ModUser> queryUserByChatId(Integer chatId) {
        ParseQuery<ModUser> query = ParseQuery.getQuery(ModUser.class);
        query.whereEqualTo(ModUser.KEY_CHAT_ID, chatId);
        return query;
    }


    public void persistFile(final String KEY, String file_name, byte[] bytes, final Context context, SaveCallback saveCallback, ProgressCallback progressCallback) {
        final ParseFile file = new ParseFile(file_name, bytes);
        put(KEY, file);
        file.saveInBackground(saveCallback, progressCallback);
    }


    public void getImage(final ICallbackImage callback, String KEY) {
        callback.setActive(true);
        ParseFile file = (ParseFile) get(KEY);
        if (file != null) {
            callback.setFileForCancel(file);
            file.getDataInBackground(new GetDataCallback() {
                public void done(byte[] data, ParseException e) {
                    if (e == null) {
                        if (data == null) {
                            fetchInBackground(new GetCallback<ModUser>() {

                                @Override
                                public void done(ModUser object, ParseException e) {
                                    //NO IMAGE
                                }
                            });
                        } else {
                            Bitmap bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);
                            callback.imageFetched(bitmap, -1);
                        }
                    } else {
                        callback.imageFetchedError(e.getMessage());
                    }
                }
            });
        }
    }

    public void setProfileImageBitmap(Bitmap bitmapFromImageView) {
        if (this.profileImageBitmap != null)
            this.profileImageBitmap.recycle();
        this.profileImageBitmap = bitmapFromImageView;
    }

    public Bitmap getProfileImageBitmap() {
        return profileImageBitmap;
    }


    public static final String KEY_FIRST_NAME = "KEY_FIRST_NAME";
    public static final String KEY_LAST_NAME = "KEY_LAST_NAME";

    public static final String KEY_S_NOTIFICATIONS_ENABLED = "KEY_S_NOTIFICATIONS_ENABLED";
    public static final String KEY_S_MESSAGES_ENABLED = "KEY_S_MESSAGES_ENABLED";
    public static final String KEY_S_HIDDEN = "KEY_S_HIDDEN";

    public ModUser newPersonal() {
        ModUser user = new ModUser();
        user.put(KEY_TYPE, VALUE_TYPE_PERSONAL);
        user.setHidden(false);
        user.setMessagesEnabled(true);
        user.setNotificationsEnabled(true);
        user.setYelpSearchRadiusMiles(2);
        return user;
    }

    public static ModUser getModUser() {
        return (ModUser) ParseUser.getCurrentUser();
    }

    public void getProfileImage(final ICallbackImage callback) {
        getImage(callback, KEY_PROFILE_IMAGE);
    }

    public void setName(String firstName, String lastName) {
        put(KEY_FIRST_NAME, firstName);
        put(KEY_LAST_NAME, lastName);
        put(KEY_NAME_SEARCH, firstName.toLowerCase() + " " + lastName.toLowerCase());
    }

    public String getFirstName() {
        return getString(KEY_FIRST_NAME);
    }

    public String getLastName() {
        return getString(KEY_LAST_NAME);
    }

    public String getPersonalName(){
        return getFirstName() + " " + getLastName();
    }

    public void setNotificationsEnabled(boolean enabled) {
        put(KEY_S_NOTIFICATIONS_ENABLED, enabled);
    }

    public Boolean getNotificationsEnabled() {
        return getBoolean(KEY_S_NOTIFICATIONS_ENABLED);
    }

    public void setMessagesEnabled(boolean enabled) {
        put(KEY_S_MESSAGES_ENABLED, enabled);
    }

    public Boolean getMessagesEnabled() {
        return getBoolean(KEY_S_MESSAGES_ENABLED);
    }

    public void setHidden(boolean enabled) {
        put(KEY_S_HIDDEN, enabled);
    }

    public Boolean isHidden() {
        return getBoolean(KEY_S_HIDDEN);
    }

    public void setEmail(String email){
        put(KEY_EMAIL_SEARCH, email.toLowerCase());
    }

    public void setChatID(int id) {
        put(KEY_CHAT_ID, id);
    }

    public int getChatId() {
        return getInt(KEY_CHAT_ID);
    }

    public String getChatUsername(){
        //if (getAuthData() == null && !getGooglePlusFlag())
        return XxUtil.md5(getUsername().trim().replace(" ", ""));
        //else
        //	return getObjectId();
    }

    public String getChatPassword(){
        //if (getAuthData() == null && !getGooglePlusFlag())
        return XxUtil.md5(getUsername().trim().replace(" ", ""));
        //else
        //	return getObjectId();
    }

    public static final String KEY_NAME = "KEY_NAME";


    public static final String KEY_ADDRESS = "KEY_ADDRESS";
    public static final String KEY_ADDRESS_SEARCH = "KEY_ADDRESS_SEARCH"; //lower case

    public static final String KEY_ESTABLISHMENT_TYPE = "KEY_ESTABLISHMENT_TYPE";
    public static final String KEY_WORK_HOURS = "KEY_WORK_HOURS";
    public static final String KEY_CONTACT_NUMBER = "KEY_CONTACT_NUMBER";
    public static final String KEY_WEB_SITE = "KEY_WEB_SITE";
    public static final String KEY_SHORT_DESCRIPTION = "KEY_SHORT_DESCRIPTION";
    public static final String KEY_HOT_DEALS = "KEY_HOT_DEALS";
    public static final String KEY_HOT_DEALS_TITLE = "KEY_HOT_DEALS_TITLE";

    public static final String KEY_RATING_SUM = "KEY_RATING_SUM"; //to be ++ every time someone posts rating.
    public static final String KEY_RATING_COUNT = "KEY_RATING_COUNT"; //to be incremented every time someone posts rating.


    public static final String KEY_GEOPOINT = "KEY_GEOPOINT";

    public ModUser newBusiness() {
        ModUser user = new ModUser();
        user.put(KEY_TYPE, VALUE_TYPE_BUSINESS);
        user.setHidden(false);
        user.initializeRating();
        return user;
    }

    private void initializeRating() {
        put(KEY_RATING_SUM, 0);
        put(KEY_RATING_COUNT, 0);
    }

    public String getRatingString(){
        return String.format("%.1f / 5", getRatingValue());
    }

    public float getRatingValue() {
        int rating_sum = getInt(KEY_RATING_SUM);
        int rating_count = getInt(KEY_RATING_COUNT);
        if (rating_count == 0)
            return 0;
        float rating = (float)rating_sum/ (float) rating_count;
        return  rating;
    }


    public void setName(String name) {
        put(KEY_NAME, name);
        put(KEY_NAME_SEARCH, name.toLowerCase());
    }

    public String getBusinessName() {
        return getString(KEY_NAME);
    }

    public void setAddress(String address) {
        put(KEY_ADDRESS, address);
        put(KEY_ADDRESS_SEARCH, address.toLowerCase());
    }

    public String getAddress() {
        return getString(KEY_ADDRESS);
    }

    public void setEstablishmentType(String establishmentType) {
        put(KEY_ESTABLISHMENT_TYPE, establishmentType);
    }

    public String getEstablishmentType() {
        return getString(KEY_ESTABLISHMENT_TYPE);
    }

    public void setWorkHours(String workHours) {
        put(KEY_WORK_HOURS, workHours);
    }

    public String getWorkHours() {
        return getString(KEY_WORK_HOURS);
    }

    public void setContactNumber(String contactNumber) {
        put(KEY_CONTACT_NUMBER, contactNumber);
    }

    public String getContactNumber() {
        return getString(KEY_CONTACT_NUMBER);
    }

    public void setWebSite(String webSite) {
        put(KEY_WEB_SITE, webSite);
    }

    public String getWebSite() {
        return getString(KEY_WEB_SITE);
    }

    public void setShortDescription(String shortDescription) {
        put(KEY_SHORT_DESCRIPTION, shortDescription);
    }

    public String getShortDescription() {
        return getString(KEY_SHORT_DESCRIPTION);
    }

    public void setGeopoint(ParseGeoPoint geopoint) {
        put(KEY_GEOPOINT, geopoint);
    }

    public ParseGeoPoint getGeopoint() {
        return getParseGeoPoint(KEY_GEOPOINT);
    }

    public void setHotDeals(String hotDeals) {
        put(KEY_HOT_DEALS, hotDeals);
    }

    public String getHotDeals() {
        return getString(KEY_HOT_DEALS);
    }

    public void setHotDealsTitle(String hotDealsTitle) {
        put(KEY_HOT_DEALS_TITLE, hotDealsTitle);
    }

    public String getHotDealsTitle() {
        return getString(KEY_HOT_DEALS_TITLE);
    }

    public boolean isEqual(ModUser compare) {
        try{
            if (getObjectId().equals(compare.getObjectId()))
                return true;
        }catch (Exception e){
            return false;
        }
        return false;
    }

    public static ParseQuery<ModUser> queryAllBusiness() {
        ParseQuery<ModUser> query = ParseQuery.getQuery(ModUser.class);
        query.whereEqualTo(ModUser.KEY_TYPE, ModUser.VALUE_TYPE_BUSINESS);
        return query;
    }

    public static ParseQuery<ModUser> queryAllBusinessInRange(ParseGeoPoint userLocation, double distance) {
        ParseQuery<ModUser> query = ParseQuery.getQuery(ModUser.class);
        query.whereEqualTo(ModUser.KEY_TYPE, ModUser.VALUE_TYPE_BUSINESS);
        query.whereWithinKilometers(KEY_GEOPOINT, userLocation, distance);
        return query;
    }

    public static ParseQuery<ModUser> queryBusinessIncludingInName(String search_txt) {
        ParseQuery<ModUser> query = ParseQuery.getQuery(ModUser.class);
        query.whereEqualTo(KEY_TYPE, VALUE_TYPE_BUSINESS);
        query.whereContains(KEY_NAME_SEARCH, search_txt.toLowerCase(Locale.US));

        ParseQuery<ModUser> queryZip = ParseQuery.getQuery(ModUser.class);
        queryZip.whereEqualTo(KEY_TYPE, VALUE_TYPE_BUSINESS);
        queryZip.whereContains(KEY_ADDRESS_SEARCH, search_txt.toLowerCase(Locale.US));

        List<ParseQuery<ModUser>> queries = new ArrayList<>();
        queries.add(query);
        queries.add(queryZip);

        ParseQuery<ModUser> mainQuery = ParseQuery.or(queries);

        return mainQuery;
    }

    public static ParseQuery<ModUser> querySearchUsers(String text) {
        ParseQuery<ModUser> query = ParseQuery.getQuery(ModUser.class);
        query.whereEqualTo(KEY_S_HIDDEN, false);
        query.whereEqualTo(KEY_TYPE, VALUE_TYPE_PERSONAL);
        query.whereNotEqualTo(KEY_ID, ModUser.getModUser().getObjectId());
        query.whereContains(KEY_NAME_SEARCH, text.toLowerCase(Locale.US));

        ParseQuery<ModUser> queryZip = ParseQuery.getQuery(ModUser.class);
        queryZip.whereEqualTo(KEY_S_HIDDEN, false);
        queryZip.whereEqualTo(KEY_TYPE, VALUE_TYPE_PERSONAL);
        queryZip.whereNotEqualTo(KEY_ID, ModUser.getModUser().getObjectId());
        queryZip.whereEqualTo(KEY_EMAIL, text.toLowerCase(Locale.US));

        List<ParseQuery<ModUser>> queries = new ArrayList<>();
        queries.add(query);
        queries.add(queryZip);

        ParseQuery<ModUser> mainQuery = ParseQuery.or(queries);

        return mainQuery;
    }

    private static String CACHE_BUSINESS = "CACHE_BUSINESS";
    private static String CACHE_PRIVATE = "CACHE_PRIVATE";

    public static void saveLocallyBusiness(final List<ModUser> initialResultList) {
        try {
            ParseObject.unpinAll(CACHE_BUSINESS);
            ParseObject.pinAllInBackground(CACHE_BUSINESS, initialResultList);
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    public static void saveLocallyPrivate(final List<ModUser> initialResultList) {
        try {
            ParseObject.unpinAll(CACHE_PRIVATE);
            ParseObject.pinAllInBackground(CACHE_PRIVATE, initialResultList);
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    public static List<ModUser> getAllBusinessLocalCache() {
        try {
            ParseQuery<ModUser> query = ParseQuery.getQuery(ModUser.class);
            query.fromLocalDatastore();
            query.whereEqualTo(KEY_TYPE, VALUE_TYPE_BUSINESS);
            return query.find();

        } catch (ParseException e) {
            e.printStackTrace();
            return null;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static List<ModUser> getAllPrivateLocalCache() {
        try {
            ParseQuery<ModUser> query = ParseQuery.getQuery(ModUser.class);
            query.fromLocalDatastore();
            query.whereEqualTo(KEY_TYPE, VALUE_TYPE_PERSONAL);
            return query.find();

        } catch (ParseException e) {
            e.printStackTrace();
            return null;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static List<ModUser> getAllFromLocalCache() {
        try {
            ParseQuery<ModUser> query = ParseQuery.getQuery(ModUser.class);
            query.fromLocalDatastore();
            return query.find();

        } catch (ParseException e) {
            e.printStackTrace();
            return null;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static ModUser getBusinessFromCache(String id) {
        try {
            ParseQuery<ModUser> query = ParseQuery.getQuery(ModUser.class);
            query.fromLocalDatastore();
            query.whereEqualTo(KEY_TYPE, VALUE_TYPE_BUSINESS);
            query.whereEqualTo(KEY_ID, id);
            return query.getFirst();

        } catch (ParseException e) {
            e.printStackTrace();
            return null;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static ParseQuery<ModUser> getViaString(String id) {
        try {
            ParseQuery<ModUser> query = ParseQuery.getQuery(ModUser.class);
            query.whereEqualTo(KEY_ID, id);
            return query;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static ModUser getPrivateFromCache(String id) {
        try {
            ParseQuery<ModUser> query = ParseQuery.getQuery(ModUser.class);
            query.fromLocalDatastore();
            query.whereEqualTo(KEY_TYPE, VALUE_TYPE_PERSONAL);
            query.whereEqualTo(KEY_ID, id);
            return query.getFirst();

        } catch (ParseException e) {
            e.printStackTrace();
            return null;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static void addSingleBusiness(ModUser business){
        List<ModUser> list = getAllBusinessLocalCache();

        if (list == null)
            list = new ArrayList<>();

        list.add(business);
        saveLocallyBusiness(list);
    }

    public static void addMultiBusiness(List<ModUser> businessList){
        List<ModUser> list = getAllBusinessLocalCache();

        if (list == null)
            list = new ArrayList<>();

        list.addAll(businessList);
        saveLocallyBusiness(list);
    }

    public static void addSinglePrivateCache(ModUser privateUser){
        List<ModUser> list = getAllPrivateLocalCache();

        if (list == null)
            list = new ArrayList<>();

        list.add(privateUser);
        saveLocallyPrivate(list);
    }



    public static void cacheReleaseAll() {
        ParseObject.unpinAllInBackground(CACHE_BUSINESS, new DeleteCallback() {
            public void done(ParseException e) {

            }
        });

        ParseObject.unpinAllInBackground(CACHE_PRIVATE, new DeleteCallback() {
            public void done(ParseException e) {

            }
        });
    }

    public static ModUser getUserByChatIdFromCache(Integer chatId){
        if (chatId == null)
            return  null;

        try {
            ParseQuery<ModUser> query = ParseQuery.getQuery(ModUser.class);
            query.fromLocalDatastore();
            query.whereEqualTo(KEY_CHAT_ID, chatId);
            return query.getFirst();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static ModUser getUserByIdFromCache(String id){
        try {
            ParseQuery<ModUser> query = ParseQuery.getQuery(ModUser.class);
            query.fromLocalDatastore();
            query.whereEqualTo(KEY_ID, id);
            return query.getFirst();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }


    public static ModUser createFromYelpBusiness(Business business) {
        Log.e("Size:","= " + getAllFromLocalCache().size());

        // TODO pile provjeri da li je ovo ok ovako.
        String val;

        ModUser tBusiness = getUserByIdFromCache(business.id());
        if (tBusiness != null)
            return tBusiness;
        else
            tBusiness = new ModUser();

        tBusiness.setGeneratedFromYelp(true);
        tBusiness.put(KEY_TYPE, VALUE_TYPE_BUSINESS);

        val = business.id();
        tBusiness.setYelpId(val == null ? "" : val);

        val = business.name();
        tBusiness.setName(val == null ? "" : val);
        // tBusiness.setAddress(business.); fali data!

        val = business.displayPhone();
        tBusiness.setContactNumber(val == null ? "" : val);

        val = business.url();
        tBusiness.setWebSite(val == null ? "" : val);

        Double rating = business.rating();
        tBusiness.setRating(rating == null ? .0 : rating.doubleValue());

        StringBuilder hotDeals = new StringBuilder();
        if(business.deals() != null) {
            for(Deal deal : business.deals()) {
                hotDeals.append(deal.title());
                hotDeals.append('\n');
            }
        }
        tBusiness.setHotDeals(hotDeals.toString());

        if (business.categories() != null){
            val = "";
            for(Category cat: business.categories()) {
                val = val + cat.name() + "\n";
            }
            tBusiness.setEstablishmentType(val);
        }

        tBusiness.setYelpComment(business.snippetText());
        tBusiness.yelpImageUrl = business.imageUrl();

        val ="";
        for (String s:business.location().displayAddress())
            val = val + s +"\n";
        tBusiness.setAddress(val);

        if (business.location().coordinate() != null) {
            ParseGeoPoint gp = new ParseGeoPoint(business.location().coordinate().latitude(), business.location().coordinate().longitude());
            tBusiness.setGeopoint(gp);
        }

        //addSingleBusiness(tBusiness);
        return tBusiness;
    }

    private void setYelpComment(String s) {
        if (s == null)
            return;

        put(KEY_YELP_COMMENT, s);
    }

    public String getYelpComment(){
        return getString(KEY_YELP_COMMENT);
    }

    private void setRating(double pRating) {
        put(KEY_RATING_SUM, pRating * 5);
        put(KEY_RATING_COUNT, 5);
    }

    public boolean isGeneratedFromYelp() {
        // TODO pile use this to determine if this object was generated from yelp object.
        return getGeneratedFromYelp();
    }

    public String getYelpImageUrl() {
        return yelpImageUrl;
    }

    public void setGeneratedFromYelp(boolean generatedFromYelp) {
        put(KEY_YELP_GENERATED_FROM,generatedFromYelp);
    }

    public boolean getGeneratedFromYelp() {
        return getBoolean(KEY_YELP_GENERATED_FROM);
    }

    public String getYelpId() {
        return getString(KEY_YELP_ID);
    }

    public void setYelpId(String id) {
        put(KEY_YELP_ID, id);
    }

    public void setYelpSearchRadiusMiles(int raidus) {
        put(KEY_YELP_SEARCH_RADIUS_MILES, raidus);
    }

    public int getYelpSearchRadiusMiles() {
        return getInt(KEY_YELP_SEARCH_RADIUS_MILES);
    }

    public String getParseProfileImageUrl() {
        ParseFile f = getParseFile(KEY_PROFILE_IMAGE);
        if (f != null)
            return f.getUrl();
        return null;
    }

    public QBUser getChatUser() {
        return new QBUser(getChatUsername(), getChatPassword());
    }
}