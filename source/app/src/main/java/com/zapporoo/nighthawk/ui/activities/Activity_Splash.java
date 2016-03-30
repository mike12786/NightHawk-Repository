package com.zapporoo.nighthawk.ui.activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.zapporoo.nighthawk.R;
import com.zapporoo.nighthawk.model.ModRelationship;
import com.zapporoo.nighthawk.model.ModUser;
import com.zapporoo.nighthawk.quickblox.push.IRegGCM;
import com.zapporoo.nighthawk.quickblox.push.PlayServicesHelper;
import com.zapporoo.nighthawk.quickblox.util.UtilChat;
import com.zapporoo.nighthawk.util.NhLog;
import com.zapporoo.nighthawk.util.UtilAds;
import com.zapporoo.nighthawk.util.UtilImage;
import com.zapporoo.nighthawk.util.XxUtil;

import java.util.List;

public class Activity_Splash extends ActivityDefault implements IRegGCM {
    private static final String TAG = "Activity_Splash";
    Runnable mRunnable;
    Handler mHandler;
    int SPLASH_DELAY = 1500;
    private boolean termsChecked = false;
    boolean userFetched = false;

    PlayServicesHelper playHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        UtilAds.setupInterstitialAd(this, getString(R.string.ad_interstitial));
        setContentView(R.layout.activity_splash);

        if (!isNetworkAvailable(this, false)){
            showCloseActivity(Activity_Splash.this, "Please check your network connection and try again");
            return;
        }

        playHelper = new PlayServicesHelper(this);

        //startUserFetch();


        Activity_Register_Business2.user = null;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        UtilImage.clearPicturesCache(UtilImage.IMG_PREFIX_YELP, getExternalCacheDir());
    }

    private void checkTermCountdown(){
        mRunnable = new Runnable() {
            @Override
            public void run() {
                checkTermAccepted();
            }
        };
        mHandler = new Handler();
        mHandler.postDelayed(mRunnable, SPLASH_DELAY);
    }

    private void checkTermAccepted() {
        if (XxUtil.getTermsAcceptedStatus(this))
            checkUserLogged();
            //startLoginSignupActivity();
            //startTermsActivity();
        else
            startTermsActivity();
    }

    private void checkUserLogged() {
        ModUser user = (ModUser) ModUser.getCurrentUser();
        termsChecked = true;

        if (user != null && user.getType() == ModUser.VALUE_TYPE_PERSONAL && userFetched)
            startHomePersonalActivity();

        if (user != null && user.getType() == ModUser.VALUE_TYPE_BUSINESS && userFetched)
            startHomeBusinessActivity();

        if (user == null)
            startLoginSignupActivity();
    }



    private void startUserFetch() {
        if (ModUser.getCurrentUser() != null){
            ModUser.getCurrentUser().fetchInBackground(new GetCallback<ParseObject>() {
                @Override
                public void done(ParseObject object, ParseException e) {
                    if (e == null){
                        userFetched = true;

                        if (termsChecked)
                            checkUserLogged();
                        else
                            checkTermAccepted();

                    }else{
                        userFetched = false;
                        ModUser.logOutInBackground();
                        showCloseActivity(Activity_Splash.this, getString(R.string.error_server_error));
                    }
                }
            });
        }else{
            checkTermCountdown();
        }

    }

    private void startHomeBusinessActivity() {
        startNextActivityClearTask(Activity_Home_Business.class);
        finish();
    }

    private void startHomePersonalActivity() {
        startNextActivityClearTask(Activity_Home_Personal.class);
        finish();
    }

    private void startTermsActivity() {
        Intent intent = new Intent(this, Activity_Terms_Agreement.class);
        startActivity(intent);
        finish();
    }

    @Override
    public void onBackPressed() {
        if (mHandler != null && mRunnable != null)
            mHandler.removeCallbacks(mRunnable);
        super.onBackPressed();
    }

    private void startLoginSignupActivity(){
        Intent intent = new Intent(this, Activity_Login.class);
        startActivity(intent);
        finish();
    }

    @Override
    public void onGcmRegSuccess() {
        NhLog.e(TAG,"onGcmRegSuccess");
        startUserFetch();
    }

    @Override
    public void onGcmRegFailed(String error) {
        NhLog.e(TAG,"onGcmRegFailed" + error);
        finish();
    }
}
