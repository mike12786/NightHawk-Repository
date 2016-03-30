package com.zapporoo.nighthawk.ui.activities;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.parse.SignUpCallback;
import com.zapporoo.nighthawk.model.ModUser;
import com.zapporoo.nighthawk.R;

public class Activity_Choose_Account extends ActivityDefault {

    Button btnPersonal, btnBusiness;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_account);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        setupGui();
        clearTempUsers();
    }

    private void clearTempUsers() {
        ParseUser.logOutInBackground();
    }

    private void setupGui() {
        btnBusiness = (Button) findViewById(R.id.btnChooseAccountBusiness);
        btnBusiness.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startLoginDetails(ModUser.VALUE_TYPE_BUSINESS);
            }
        });

        btnPersonal = (Button) findViewById(R.id.btnChooseAccountPersonal);
        btnPersonal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startLoginDetails(ModUser.VALUE_TYPE_PERSONAL);
            }
        });
    }

    private void startLoginDetails(int type) {
            Activity_Login_Details.USER_TYPE = type;
            startNextActivity(Activity_Login_Details.class);
    }


}
