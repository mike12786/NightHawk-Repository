package com.zapporoo.nighthawk.ui.fragments;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.media.Image;
import android.os.Build;
import android.os.Looper;
import android.support.v4.app.Fragment;
import android.support.v4.app.ShareCompat;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.parse.ParseFile;
import com.zapporoo.nighthawk.R;
import com.zapporoo.nighthawk.callbacks.ICallbackImage;
import com.zapporoo.nighthawk.model.ModUser;
import com.zapporoo.nighthawk.ui.activities.ActivityDefault;
import com.zapporoo.nighthawk.ui.activities.Activity_Splash;
import com.zapporoo.nighthawk.ui.views.CircularImageView;
import com.zapporoo.nighthawk.util.UtilAds;
import com.zapporoo.nighthawk.util.UtilDialog;

/**
 * Created by Pile on 1/11/2016.
 */
public class FragmentDefault extends Fragment {

    protected boolean isOnMainUI(){
        if (Looper.myLooper() == Looper.getMainLooper())
            return true;
        else
            return false;
    }

    protected void showProgressDialog(String text) {
        if (getActivity() != null)
            ((ActivityDefault)getActivity()).showProgressDialog(text);
    }

    protected void dismissProgressDialog(){
        if (getActivity() != null)
            ((ActivityDefault)getActivity()).dismissProgressDialog();

    }

    protected void showAlertDialog(String message){
        if (getActivity() != null)
            ((ActivityDefault)getActivity()).showAlertDialog(message);
    }

    protected void showToast(String message){
        if (getActivity() != null)
            ((ActivityDefault)getActivity()).showToast(message);
    }

    protected void startSplashActivity(){
        if (getActivity() != null){
            ((ActivityDefault)getActivity()).startNextActivityClearTask(Activity_Splash.class);
        }
    }

    public Drawable getDrawableCheck(int id){
        if (getActivity() != null){
            return ((ActivityDefault)getActivity()).getDrawableCheck(id);
        }
        return null;
    }

    public boolean isNetworkAvailable(){
        if (getActivity() != null){
            return ((ActivityDefault)getActivity()).isNetworkAvailableShowDialog();
        }
        return false;
    }

    protected void startNextActivity(Intent intent){
        UtilAds.showInterstitialAndNull();
        startActivity(intent);
        startNextActivitySlide(getActivity());
    }

    protected void startNextActivitySlide(Activity activity) {
        activity.overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
    }

    public void showConfirmDialog(String text, View.OnClickListener positiveOnClickListener){
        UtilDialog.showConfirmDialog(getActivity(), text, positiveOnClickListener);
    }

    public void showOkDialog(String text, View.OnClickListener okOnClickListener){
        UtilDialog.showOkDialog(getActivity(), text, okOnClickListener);
    }

    public void showOkFinishDialog(String text){
        UtilDialog.showOkDialog(getActivity(), text, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().finish();
            }
        });
    }

    protected void fetchProfileImage(ModUser user, final ImageView circImgFriend) {
        user.getProfileImage(new ICallbackImage() {
            @Override
            public void imageFetched(Bitmap bitmap, int pRequestId) {
                circImgFriend.setImageBitmap(bitmap);
            }

            @Override
            public void imageFetchedError(String message) {

            }

            @Override
            public void setActive(boolean isActive) {

            }

            @Override
            public boolean isActive() {
                return false;
            }

            @Override
            public void setFileForCancel(ParseFile file) {
                file.cancel();
            }

            @Override
            public void setImageView(CircularImageView iv) {

            }

            @Override
            public void setImageView(ImageView iv) {

            }

            @Override
            public Context getContext() {
                return null;
            }

            @Override
            public int getDesiredPictureWidth() {
                return 0;
            }

            @Override
            public int getDesiredPictureHeight() {
                return 0;
            }

            @Override
            public int getFixedDimenType() {
                return 0;
            }

            @Override
            public int getDefaultImageResId() {
                return 0;
            }
        });
    }

}
