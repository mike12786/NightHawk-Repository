package com.zapporoo.nighthawk.ui.activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.Switch;

import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.quickblox.core.QBEntityCallback;
import com.quickblox.core.exception.QBResponseException;
import com.zapporoo.nighthawk.R;
import com.zapporoo.nighthawk.model.ModCheckIn;
import com.zapporoo.nighthawk.model.ModMyClub;
import com.zapporoo.nighthawk.model.ModUser;
import com.zapporoo.nighthawk.quickblox.util.ChatService;
import com.zapporoo.nighthawk.quickblox.util.UtilChat;
import com.zapporoo.nighthawk.util.UtilDialog;
import com.zapporoo.nighthawk.util.XxUtil;

public class Activity_Settings extends ActivityDefault implements View.OnClickListener, QBEntityCallback<Void> {

    Switch swMessages, swNotifications, swHideMe;
    View rlTutorial, rlRateApp, rlReportProblem, rlAbout, rlTerms, rlLogout;
    ModUser user;
    Spinner spSearchRadius;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        user = (ModUser) ModUser.getCurrentUser();

        if (user == null) {
            startNextActivityClearTask(Activity_Splash.class);
            return;
        }
        setupGui();
    }

    private void setupGui() {
        swHideMe = (Switch) findViewById(R.id.swHideMe);
        swHideMe.setChecked(user.isHidden());
        swHideMe.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                user.setHidden(isChecked);
                saveUserState(swHideMe, !isChecked);
            }
        });

        swMessages = (Switch) findViewById(R.id.swMessages);
        swMessages.setChecked(user.getMessagesEnabled());
        swMessages.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                user.setMessagesEnabled(isChecked);
                saveUserState(swMessages, !isChecked);
            }
        });

        swNotifications = (Switch) findViewById(R.id.swNotifications);
        swNotifications.setChecked(user.getNotificationsEnabled());
        swNotifications.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                user.setNotificationsEnabled(isChecked);
                saveUserState(swNotifications, !isChecked);
            }
        });

        rlAbout = findViewById(R.id.rlSettingsAbout);
        rlAbout.setOnClickListener(this);

        rlLogout = findViewById(R.id.rlSettingsLogout);
        rlLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showLogoutDialog();
            }
        });
        rlRateApp = findViewById(R.id.rlSettingsRateApp);
        rlReportProblem = findViewById(R.id.rlSettingsReport);
        rlTerms = findViewById(R.id.rlSettingsTerms);
        rlTutorial = findViewById(R.id.rlSettingsTutorial);

        ImageButton arrowBtn = (ImageButton) rlRateApp.findViewById(R.id.imgBtnRateApp);
        arrowBtn.setOnClickListener(this);
        rlRateApp.setOnClickListener(this);

        arrowBtn = (ImageButton) rlReportProblem.findViewById(R.id.imgBtnReportProblem);
        arrowBtn.setOnClickListener(this);
        rlReportProblem.setOnClickListener(this);

        arrowBtn = (ImageButton) rlTerms.findViewById(R.id.imgBtnTermsAndConditions);
        arrowBtn.setOnClickListener(this);
        rlTerms.setOnClickListener(this);

        spSearchRadius = (Spinner) findViewById(R.id.spSearchRadius);
        setupSpinner();

    }

    private void setupSpinner() {
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.spMiles, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spSearchRadius.setAdapter(adapter);
        spSearchRadius.setSelection(XxUtil.getSearchRadiusIndex(this));
    }

    @Override
    protected void onPause() {
        super.onPause();
        XxUtil.putSearchRadiusIndex(this, spSearchRadius.getSelectedItemPosition());
        XxUtil.putSearchRadiusValue(this, (String) spSearchRadius.getSelectedItem());
    }

    private void showLogoutDialog() {
        UtilDialog.showConfirmDialog(this, getString(R.string.alert_logout),
                new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        ParseUser.logOutInBackground();
                        ModUser.cacheReleaseAll();
                        ModCheckIn.cacheReleaseAll();
                        ModMyClub.cacheReleaseAll();
                        UtilChat.removeAllDialogs();
                        UtilChat.clearAllPushs(Activity_Settings.this);
                        ChatService.getInstance().unsubscribeFromPushNotifications(Activity_Settings.this, Activity_Settings.this);
                        startNextActivityClearTask(Activity_Login.class);
                    }
                });
    }

    private void saveUserState(final Switch switched, final boolean previous){
        showProgressDialog(getString(R.string.progress_saving));
        user.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                dismissProgressDialog();
                if (e == null)
                    showToast(getString(R.string.alert_save_completed));
                else {
                    switched.setEnabled(previous);
                    undoChange(switched);
                    showToast(getString(R.string.error_server_error));
                }
            }
        });
    }

    private void undoChange(Switch switched) {
        int id = switched.getId();

        if (id == R.id.swMessages)
            user.setMessagesEnabled(!user.getMessagesEnabled());

        if (id == R.id.swNotifications)
            user.setNotificationsEnabled(!user.getNotificationsEnabled());

        if (id == R.id.swHideMe)
            user.setHidden(!user.isHidden());
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.rlSettingsRateApp:
            case R.id.imgBtnRateApp: {
                final String appPackageName = v.getContext().getPackageName(); // getPackageName() from Context or Activity object
                try {
					startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackageName)));
//                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://")));
                } catch (android.content.ActivityNotFoundException anfe) {
					startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + appPackageName)));
//                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store")));
                }
                break;
            }
            case R.id.rlSettingsReport:
            case R.id.imgBtnReportProblem: {
                try {
                    Intent intent = new Intent(Intent.ACTION_SEND);
                    intent.setType("message/rfc822");
                    //intent.setType("plain/text");
				intent.putExtra(Intent.EXTRA_EMAIL, new String[] { "nighthawk1286@gmail.com" });
				intent.putExtra(Intent.EXTRA_SUBJECT, "Report a problem");
//				intent.putExtra(Intent.EXTRA_TEXT, "mail body");
                    startActivity(Intent.createChooser(intent, "Send mail"));
                } catch (Exception e) {
                    showAlertDialog(getString(R.string.dlg_rate_email_client_missing));
                }
                break;
            }
            case R.id.rlSettingsTerms:
            case R.id.imgBtnTermsAndConditions: {
                Intent intent = new Intent(this, Activity_Terms_Agreement.class);
                intent.putExtra(Activity_Terms_Agreement.EXTRA_SHOW_ONLY_TEXT, true);
                startNextActivity(intent);
                break;
            }

            case R.id.rlSettingsAbout:{
                Intent intent = new Intent(this, Activity_Terms_Agreement.class);
                intent.putExtra(Activity_Terms_Agreement.EXTRA_SHOW_ABOUT, true);
                startNextActivity(intent);
                break;
            }
        }
    }


    //UN-SUBSCRIBED CHAT
    @Override
    public void onSuccess(Void aVoid, Bundle bundle) {
        ChatService.getInstance().logout();
    }

    //UN-SUBSCRIBED CHAT
    @Override
    public void onError(QBResponseException e) {
        ChatService.getInstance().logout();
    }
}
