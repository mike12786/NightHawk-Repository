package com.zapporoo.nighthawk.ui.activities;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.SaveCallback;
import com.parse.SignUpCallback;
import com.zapporoo.nighthawk.R;
import com.zapporoo.nighthawk.model.ModUser;
import com.zapporoo.nighthawk.util.XxUtil;

import java.io.ByteArrayOutputStream;

public class Activity_Register_Business2 extends ActivityDefault {

    Button btnFinish;
    public static ModUser user;
    EditText etShortDescription, etHotDeals;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_business2);
        setupGui();
        //testData();
    }

    private void testData() {
        etShortDescription.setText("Food Delivery");
        etHotDeals.setText("$5 Hot Sheep\n$10 Hot Goat");
    }

    private void setupGui() {
        etShortDescription = (EditText) findViewById(R.id.etBusinessShortDescription);
        etHotDeals = (EditText) findViewById(R.id.etBusinessHotDeals);

        btnFinish = (Button) findViewById(R.id.btnRegisterBusiness2Finish);
        btnFinish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkAndSaveUser();
            }
        });
    }


    private void checkAndSaveUser() {
        if (!isInputValid())
            return;

        showProgressDialog("Signing up...");
        saveProfileImageAsFileToServer();
    }

    private boolean isInputValid() {
        if (!XxUtil.isValid(etHotDeals)) {
            XxUtil.focusSelect(etHotDeals);
            showAlertDialog("Field can't be empty!");
            return false;
        }

        if (!XxUtil.isValid(etShortDescription)) {
            XxUtil.focusSelect(etShortDescription);
            showAlertDialog("Field can't be empty!");
            return false;
        }

        return true;
    }

    private void saveProfileImageAsFileToServer() {
        if (user.getProfileImageBitmap() != null) {
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            user.getProfileImageBitmap().compress(Bitmap.CompressFormat.JPEG, ModUser.IMAGE_COMPRESSION_QUALITY, stream);

            final ParseFile file = new ParseFile(ModUser.IMAGE_FILE_NAME, stream.toByteArray());
            file.saveInBackground(new SaveCallback() {

                @Override
                public void done(ParseException e) {
                    if (e == null) {
                        user.setProfileImage(file);
                        saveUserData();
                    } else {
                        dismissProgressDialog();
                        showAlertDialog("Error..." + e.getMessage());
                    }
                }
            });
        }
    }

    private void saveUserData() {
        //save user data
        user.setShortDescription(etShortDescription.getText().toString());
        user.setHotDeals(etHotDeals.getText().toString());
        signupUser(user);
    }

    private void signupUser(ModUser modBusiness) {
        modBusiness.signUpInBackground(new SignUpCallback() {
            @Override
            public void done(ParseException e) {
                dismissProgressDialog();
                if (e != null)
                    showAlertDialog("Error..." + e.getMessage());

                if (e == null)
                    startBusinessHomeActivity();
            }
        });
    }

    private void startBusinessHomeActivity() {
        startNextActivityClearTask(Activity_Home_Business.class);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startPrevoiusActivitySlide(this);
    }
}
