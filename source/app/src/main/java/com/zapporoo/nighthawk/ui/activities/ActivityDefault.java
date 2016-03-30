package com.zapporoo.nighthawk.ui.activities;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Looper;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.ParseFile;
import com.zapporoo.nighthawk.R;
import com.zapporoo.nighthawk.callbacks.ICallbackImage;
import com.zapporoo.nighthawk.model.ModUser;
import com.zapporoo.nighthawk.quickblox.Consts;
import com.zapporoo.nighthawk.ui.views.CircularImageView;
import com.zapporoo.nighthawk.util.NhLog;
import com.zapporoo.nighthawk.util.UtilAds;
import com.zapporoo.nighthawk.util.UtilDialog;

/**
 * Created by Pile on 12/14/2015.
 */
public class ActivityDefault extends AppCompatActivity {

    protected ProgressDialog mProgressDialog;
    protected AlertDialog mAlertDialog;
    protected Toolbar tb;

    protected boolean isOnMainUI(){
        if (Looper.myLooper() == Looper.getMainLooper())
            return true;
        else
            return false;
    }

    protected void setTitle(String title){
        if (tb != null)
            showToolbarTitle(tb, title);
    }

    protected  void startNextActivity(Class<?> toBeStarted){
        UtilAds.showInterstitialAndNull();

        Intent intent = new Intent(ActivityDefault.this, toBeStarted);
        startActivity(intent);
        startNextActivitySlide(this);
    }

    protected  void startNextActivity(Intent intent){
        startActivity(intent);
        startNextActivitySlide(this);
    }

    public void showAlertDialog(String message){
        mAlertDialog = UtilDialog.showAlertDialog(this, null, message);
    }

    public void showProgressDialog(int id){

        dismissProgressDialog();
        mProgressDialog = UtilDialog.createProgressDialogSpinning(this, getString(id));
    }

    public void showProgressDialog(String message){
        dismissProgressDialog();
        mProgressDialog = UtilDialog.createProgressDialogSpinning(this, message);
    }

    public void showProgressDialogSpinning(){
        dismissProgressDialog();
        mProgressDialog = UtilDialog.createProgressDialogSpinning(this, null);
    }

    public void dismissProgressDialog() {
        if (mProgressDialog != null)
            mProgressDialog.dismiss();
    }

    protected void startNextActivitySlide(Activity activity) {
        activity.overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
    }

    public void startNextActivityClearTask(Class<?> toBeStarted) {
        Intent intent = new Intent(ActivityDefault.this, toBeStarted);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }

    public void startPrevoiusActivitySlide(Activity activity) {
        activity.overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
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

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            finish();
            startPrevoiusActivitySlide(this);
            return true;
        }
        return false;
    }

    public boolean isNetworkAvailableShowDialog() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        if (activeNetworkInfo == null) {
            UtilDialog.showOkDialog(this, "Please check your network connection and try again", null);
            return false;
        } else {
            if (activeNetworkInfo.isConnected())
                return true;
            else {
                UtilDialog.showOkDialog(this, "Please check your network connection and try again", null);
                return false;
            }
        }
    }

    public static boolean isNetworkAvailable(Context context, boolean show_dialog) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        if (activeNetworkInfo == null) {
            UtilDialog.showNoConnectionDialog(context, show_dialog);
            return false;
        } else {
            if (activeNetworkInfo.isConnected())
                return true;
            else {
                UtilDialog.showNoConnectionDialog(context, show_dialog);
                return false;
            }
        }
    }

    public Drawable getDrawableCheck(int id){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP){
            return getDrawable(id);
        }else
            return getResources().getDrawable(id);
    }

    public String getStringCheck(int id){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP){
            return getString(id);
        }else
            return getResources().getString(id);
    }

    public void showToast(String message){
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    public static void showToolbarLogo(Toolbar pToolbar) {
        ImageView imgToolbarLogo = (ImageView) pToolbar.findViewById(R.id.toolbar_img);
        TextView tvToolbarTitle = (TextView) pToolbar.findViewById(R.id.tvToolbarTitle);

        if(imgToolbarLogo != null)
            imgToolbarLogo.setVisibility(View.VISIBLE);

        if(tvToolbarTitle != null) {
            tvToolbarTitle.setVisibility(View.GONE);
            tvToolbarTitle.setText("");
        }
    }

    public static void showToolbarTitle(Toolbar pToolbar, String title) {
        ImageView imgToolbarLogo = (ImageView) pToolbar.findViewById(R.id.toolbar_img);
        TextView tvToolbarTitle = (TextView) pToolbar.findViewById(R.id.tvToolbarTitle);

        if(imgToolbarLogo != null)
            imgToolbarLogo.setVisibility(View.GONE);

        if(tvToolbarTitle != null) {
            tvToolbarTitle.setVisibility(View.VISIBLE);
            tvToolbarTitle.setText(title);
        }
    }

    public void showConfirmDialog(String text, View.OnClickListener positiveOnClickListener){
        UtilDialog.showConfirmDialog(this, text, positiveOnClickListener);
    }

    public void hideSoftKeyboard() {
        if(getCurrentFocus()!=null) {
            InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
            inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
        }
    }

    /**
     * Shows the soft keyboard
     */
    public void showSoftKeyboard(View view) {
        InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        view.requestFocus();
        inputMethodManager.showSoftInput(view, 0);
    }

    public static AlertDialog showCloseActivity(final Context context, String message) {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);

        View alertView = LayoutInflater.from(context).inflate(R.layout.dialog_alert, null);
        LinearLayout llDialogButtons = (LinearLayout) alertView.findViewById(R.id.llDialogButtons);
        llDialogButtons.setWeightSum(1);
        TextView tvAlert = (TextView) alertView.findViewById(R.id.tvAlert);
        alertView.findViewById(R.id.llDialogButtons).setVisibility(View.VISIBLE);
        Button btnOk = (Button) alertView.findViewById(R.id.btnDialogOk);
        btnOk.setVisibility(View.GONE);

        Button btnCancel = (Button) alertView.findViewById(R.id.btnDialogCancel);
        btnCancel.setText("Close");

        alertDialogBuilder.setCancelable(false)

                .setIcon(android.R.drawable.ic_dialog_alert)
                .setView(alertView);

        if (message != null && !message.isEmpty())
            tvAlert.setText(message);

        final AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.setCanceledOnTouchOutside(false);
        try {
            alertDialog.show();
        }catch (Exception e){

        }


        btnCancel.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
                ((Activity)context).finish();
            }
        });

        return alertDialog;

    }

    public void startSplash() {
        Intent i = new Intent(this, Activity_Splash.class);
        startActivity(i);
    }

    @Override
    protected void onResume() {
        LocalBroadcastManager.getInstance(this).registerReceiver(getBroadcastReceiver(), new IntentFilter(Consts.NEW_PUSH_EVENT_INFO));
        super.onResume();
    }

    @Override
    protected void onPause() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(getBroadcastReceiver());
        super.onPause();
    }

    private BroadcastReceiver mPushReceiver;

    private BroadcastReceiver getBroadcastReceiver(){
        if (mPushReceiver == null)
            mPushReceiver = new BroadcastReceiver() {
                @Override
                public void onReceive(Context context, Intent intent) {
                    String message = intent.getStringExtra(Consts.KEY_MESSAGE_TEXT);
                    showAlertDialog(message);
                    //NhLog.e("BroadcastReceiver", "Receiving event " + Consts.NEW_PUSH_EVENT_MESSAGE + " with data: " + message);
                }
            };
        return mPushReceiver;
    }
}
