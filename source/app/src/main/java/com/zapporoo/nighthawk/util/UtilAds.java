package com.zapporoo.nighthawk.util;

import android.content.Context;
import android.view.View;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;
import com.zapporoo.nighthawk.BuildConfig;

/**
 * Created by Pile on 2/1/2016.
 */
public class UtilAds {

    public static InterstitialAd mInterstitialAd;

    public static void setupBanner(final AdView mAdView){
        //AdView mAdView = (AdView) findViewById(R.id.adView);

        AdRequest adRequest = new AdRequest.Builder()
                //.addTestDevice(AdRequest.DEVICE_ID_EMULATOR)
                //.addTestDevice("A38EFFCCB560D2DCD5863FDE7A2A2211")
                //.addTestDevice("F4215790A168AE58200038DF4510DA24")
                .build();
        mAdView.loadAd(adRequest);
        mAdView.setVisibility(View.GONE);
        mAdView.setAdListener(new AdListener() {
            @Override
            public void onAdLoaded() {
                super.onAdLoaded();
                mAdView.setVisibility(View.VISIBLE);
            }
        });
    }

    public static void showInterstitialAndNull(){
        if (mInterstitialAd != null &&
                mInterstitialAd.isLoaded()){
            mInterstitialAd.show();
            mInterstitialAd = null;
        }
    }

    public static InterstitialAd setupInterstitialAd(Context context, String adId) {
        if (BuildConfig.DEBUG)
            return null;

        AdRequest adRequest = new AdRequest.Builder()
                .addTestDevice(AdRequest.DEVICE_ID_EMULATOR)
                .addTestDevice("A38EFFCCB560D2DCD5863FDE7A2A2211")
                .addTestDevice("F4215790A168AE58200038DF4510DA24")
                .build();

        mInterstitialAd = new InterstitialAd(context);
        mInterstitialAd.setAdUnitId(adId);

        // Create an ad request.
        AdRequest.Builder adRequestBuilder = new AdRequest.Builder();

        // Optionally populate the ad request builder.
        adRequestBuilder.addTestDevice(AdRequest.DEVICE_ID_EMULATOR);

        // Set an AdListener.
        mInterstitialAd.setAdListener(new AdListener() {
            @Override
            public void onAdLoaded() {
                //interstitialAd.show();
            }

            @Override
            public void onAdClosed() {
                // Proceed to the next level.
            }
        });

        // Start loading the ad now so that it is ready by the time the user is ready to go to
        // the next level.
        mInterstitialAd.loadAd(adRequestBuilder.build());


        return mInterstitialAd;
    }
}
