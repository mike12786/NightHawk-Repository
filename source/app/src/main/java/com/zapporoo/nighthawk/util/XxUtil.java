package com.zapporoo.nighthawk.util;

import android.annotation.SuppressLint;
import android.content.Context;
import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.text.SpannableString;
import android.text.format.DateFormat;
import android.text.style.UnderlineSpan;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.Toast;

import java.io.File;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Date;

/**
 * Created by Pile on 12/29/2015.
 */
public class XxUtil {

    private static final String KEY_TERMS_ACCEPTED = "KEY_TERMS_ACCEPTED";
    private static final String KEY_SEARCH_RADIUS_INDEX = "KEY_SEARCH_RADIUS_INDEX";
    private static final String KEY_SEARCH_RADIUS_VALUE = "KEY_SEARCH_RADIUS_VALUE";

    public static void putTermsAcceptedStatus(Context context, boolean status){
        PreferenceManager.getDefaultSharedPreferences(context).edit().putBoolean(KEY_TERMS_ACCEPTED, status).commit();
    }
    public static boolean getTermsAcceptedStatus(Context context){
        return PreferenceManager.getDefaultSharedPreferences(context).getBoolean(KEY_TERMS_ACCEPTED, false);
    }

    public static boolean isValid(EditText et) {
        if (et.getText().toString().trim().isEmpty()) {
            //et.setError("can't be empty!");
            return false;
        }
        return true;
    }

    public static boolean isEmailValid(EditText et){
        return android.util.Patterns.EMAIL_ADDRESS.matcher(et.getText().toString()).matches();
    }

    public static SpannableString getUnderscore(String text){
        SpannableString spanString = new SpannableString(text);
        spanString.setSpan(new UnderlineSpan(), 0, spanString.length(), 0);
        return spanString;
    }

    public static void focusSelect(EditText view) {
        if (view.getText().toString().length() > 0)
            view.setSelection(0, view.getText().toString().length());
        view.requestFocus();
    }

    public static String getRootDirectory(){
        return Environment.getExternalStorageDirectory() + File.separator + "NightHawk" + File.separator;
    }

    public static String getUniqueImageFilename(String extension) {
        return DateFormat.format("dd.MMM.yyyy.kk-mm-ss", new Date().getTime()) + extension;
    }

    @SuppressWarnings("deprecation")
    @SuppressLint("NewApi")
    public static Drawable getDrawable(Context context, int id){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP){
            return context.getDrawable(id);
        }else
            return context.getResources().getDrawable(id);
    }

    public static String getRealPathFromURI(Context context, Uri contentUri) {
        Cursor cursor = null;
        try {
            String[] proj = { MediaStore.Images.Media.DATA };
            cursor = context.getContentResolver().query(contentUri, proj, null, null, null);
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();
            return cursor.getString(column_index);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }

    public static void showToast(Context context, String message){
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
    }

    public static String formatRate(float pRate) {
        return String.format("%.1f", pRate);
    }

    public static final String md5(final String s) {
        final String MD5 = "MD5";
        try {
            // Create MD5 Hash
            MessageDigest digest = java.security.MessageDigest.getInstance(MD5);
            digest.update(s.getBytes());
            byte messageDigest[] = digest.digest();

            // Create Hex String
            StringBuilder hexString = new StringBuilder();
            for (byte aMessageDigest : messageDigest) {
                String h = Integer.toHexString(0xFF & aMessageDigest);
                while (h.length() < 2)
                    h = "0" + h;
                hexString.append(h);
            }
            return hexString.toString();

        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return "";
    }

    public static void focusRemove(View view, Context context) {
        InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    public static void putSearchRadiusIndex(Context context, int miles) {
        PreferenceManager.getDefaultSharedPreferences(context).edit().putInt(KEY_SEARCH_RADIUS_INDEX, miles).commit();
    }

    public static int getSearchRadiusIndex(Context context) {
        int ret = PreferenceManager.getDefaultSharedPreferences(context).getInt(KEY_SEARCH_RADIUS_INDEX, 0);
        return ret;
    }

    public static void putSearchRadiusValue(Context context, String selectedMiles) {
        int miles = 2;
        try{
            miles = Integer.parseInt(selectedMiles);
        }catch (Exception e){

        }
        PreferenceManager.getDefaultSharedPreferences(context).edit().putInt(KEY_SEARCH_RADIUS_VALUE, miles).commit();

    }

    public static int getSearchRadiusValue(Context context) {
        int ret = PreferenceManager.getDefaultSharedPreferences(context).getInt(KEY_SEARCH_RADIUS_VALUE, 2);
        return ret;
    }
}
