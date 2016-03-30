package com.zapporoo.nighthawk.util;

import android.app.Activity;
import android.content.Intent;
import android.os.Build;

import com.parse.ParseException;
import com.zapporoo.nighthawk.ui.activities.ActivityDefault;
import com.zapporoo.nighthawk.ui.activities.Activity_Splash;

/**
 * Created by Pile on 1/11/2016.
 */
public class ParseExceptionHandler {

    private static final String TAG = "ParseExceptionHandler";

    public static void handleParseError(ParseException e, Activity context){
        handleParseError(e, context, null);
    }

    public static void handleParseError(ParseException e, Activity context, String customMessage) {
        NhLog.e(TAG, e.getMessage());
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            if (context == null || context.isDestroyed())
                return;
        }

        if (context instanceof ActivityDefault)
            ((ActivityDefault)context).dismissProgressDialog();

        switch (e.getCode()) {
            case ParseException.INVALID_SESSION_TOKEN:
                handleInvalidSessionToken(context);
                break;

            case ParseException.OTHER_CAUSE:
                checkTwitterOAuth(e, context);
                break;


            default:
                try{
                    if (customMessage == null)
                        showAlert(context, e.getMessage());
                    else
                        showAlert(context, customMessage);

                }catch (Exception ex){}
                break;
        }
    }

    private static void checkTwitterOAuth(ParseException e, Activity context){
        try{
            if (e.getMessage().contains("OAuth Flow Error -9"))
                showAlert(context, "Twitter username and/or password is wrong!");
        }catch (Exception ex){
            showAlert(context, "Unknown Twitter server error!");
        }
    }


    private static void showAlert(Activity context,String message){
        ((ActivityDefault)context).dismissProgressDialog();
        ((ActivityDefault)context).showAlertDialog(message);
    }


    private static void handleInvalidSessionToken(Activity context) {
        Intent i = new Intent(context, Activity_Splash.class);
        i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        context.startActivity(i);
        context.finish();
        // --------------------------------------
        // Option 1: Show a message asking the user to log out and log back in.
        // --------------------------------------
        // If the user needs to finish what they were doing, they have the
        // opportunity to do so.
        //
        // new AlertDialog.Builder(getActivity())
        // .setMessage("Session is no longer valid, please log out and log in again.")
        // .setCancelable(false).setPositiveButton("OK", ...).create().show();

        // --------------------------------------
        // Option #2: Show login screen so user can re-authenticate.
        // --------------------------------------
        // You may want this if the logout button could be inaccessible in the
        // UI.
        //
        //startActivityForResult(new ParseLoginBuilder(context).build(), 0);
    }



}
