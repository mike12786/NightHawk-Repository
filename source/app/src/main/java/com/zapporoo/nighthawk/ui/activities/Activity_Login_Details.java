package com.zapporoo.nighthawk.ui.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.zapporoo.nighthawk.model.ModUser;
import com.zapporoo.nighthawk.R;
import com.zapporoo.nighthawk.util.XxUtil;

import java.util.List;
import java.util.Locale;

public class Activity_Login_Details extends ActivityDefault {

    public static final String USER_EMAIL = "USER_EMAIL";
    public static final String USER_PASSWORD = "USER_PASSWORD";
    public static int USER_TYPE = 0;

    Button btnNext;
    EditText etEmail, etPassword, etPasswordConfirm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_details);
        setupGui();
        //testData();
    }

    private void testData(){
        etEmail.setText("asd@asd.com");
        etPasswordConfirm.setText("111");
        etPassword.setText("111");
    }

    private void setupGui() {
        btnNext = (Button) findViewById(R.id.btnLoginDetailsNext);
        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isInputValid())
                    checkEmailExist();
            }
        });

        etEmail = (EditText) findViewById(R.id.etLoginDetailsEmail);
        etPassword = (EditText) findViewById(R.id.etLoginDetailsPassword);
        etPasswordConfirm = (EditText) findViewById(R.id.etLoginDetailsPasswordConfirm);
    }

    private boolean isInputValid() {
        boolean validity = true;

        if (!XxUtil.isValid(etEmail)) {
            XxUtil.focusSelect(etEmail);
            showAlertDialog("Email can't be empty!");
            return false;
        }

        if (!XxUtil.isEmailValid(etEmail)) {
            XxUtil.focusSelect(etEmail);
            showAlertDialog("Invalid email!");
            return false;
        }

        if (!XxUtil.isValid(etPassword)) {
            XxUtil.focusSelect(etPassword);
            showAlertDialog("Password can't be empty!");
            return false;
        }

        if (etPassword.getText().toString().length() < 4) {
            XxUtil.focusSelect(etPassword);
            showAlertDialog("Password must be at least 4 characters long!");
            return false;
        }

        if (!etPassword.getText().toString().equals(etPasswordConfirm.getText().toString())) {
            XxUtil.focusSelect(etPasswordConfirm);
            showAlertDialog("Passwords must match!");
            return false;
        }

        return validity;
    }

    private void checkEmailExist() {
        showProgressDialog("Checking email..");
        ParseQuery<ParseUser> mainQuery = ParseQuery.getQuery(ParseUser.class);
        mainQuery.whereEqualTo(ModUser.KEY_EMAIL, etEmail.getText().toString().toLowerCase());
        mainQuery.getFirstInBackground(new GetCallback<ParseUser>(){

            @Override
            public void done(ParseUser object, ParseException e) {
                dismissProgressDialog();

                if (e != null && e.getCode() == ParseException.OBJECT_NOT_FOUND){
                    continueWithSignup();
                    return;
                }

                if (e != null){
                    showAlertDialog("Server error!");
                    return;
                }

                if (e == null && object != null ){
                    showAlertDialog("Email already exists!");
                    XxUtil.focusSelect(etEmail);
                    return;
                }
            }

        });

    }

    private void continueWithSignup() {
        if (USER_TYPE == ModUser.VALUE_TYPE_BUSINESS)
            startRegisterBusiness();

        if (USER_TYPE == ModUser.VALUE_TYPE_PERSONAL)
            startRegisterPersonal();
    }

    //    ModBusiness business = new ModBusiness();
//    business.setName("TEST");
//    business.setUsername("NightHawk business");
//    business.setPassword("1111");
//    business.signUpInBackground(new SignUpCallback(){
//        @Override
//        public void done(ParseException e) {
//            if (e == null) {
//                Toast.makeText(Activity_Choose_Account.this, "null", Toast.LENGTH_SHORT).show();
//            } else
//                Toast.makeText(Activity_Choose_Account.this, "Error", Toast.LENGTH_SHORT).show();
//        }
//    });
    private void startRegisterPersonal() {
        Intent intent = new Intent(this, Activity_Register_Personal.class);
        startActivity(prepareIntent(intent));
        startNextActivitySlide(this);
    }

    private void startRegisterBusiness() {
        Intent intent = new Intent(this, Activity_Register_Business1.class);
        startActivity(prepareIntent(intent));
        startNextActivitySlide(this);
    }

    private Intent prepareIntent(Intent intent){
        intent.putExtra(USER_EMAIL, etEmail.getText().toString().toLowerCase());
        intent.putExtra(USER_PASSWORD, etPassword.getText().toString());
        return  intent;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startPrevoiusActivitySlide(this);
    }
}
