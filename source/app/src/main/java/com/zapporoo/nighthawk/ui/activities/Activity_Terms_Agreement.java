package com.zapporoo.nighthawk.ui.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.text.Spanned;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import com.zapporoo.nighthawk.R;
import com.zapporoo.nighthawk.util.XxUtil;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class Activity_Terms_Agreement extends Activity {

    public static final String EXTRA_SHOW_ABOUT = "EXTRA_SHOW_ABOUT";
    TextView tvTerms;
    Button btnContinue;
    CheckBox cbAccept;
    boolean isAccepted = false;

    public static final String EXTRA_SHOW_ONLY_TEXT = "show_only_agreement_text";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        int layoutRes;

        if(getIntent() != null) {
            layoutRes = decideLayout();
        }else
            layoutRes = R.layout.activity_agreement;

        setContentView(layoutRes);
        setupGui();
    }

    private int decideLayout() {
        if (getIntent().getBooleanExtra(EXTRA_SHOW_ABOUT, false)){
            return R.layout.activity_settings_about;
        }

        if (getIntent().getBooleanExtra(EXTRA_SHOW_ONLY_TEXT, false)){
            return R.layout.activity_settings_agreement;
        }

        return R.layout.activity_agreement;

    }

    private void setupGui() {
        tvTerms = (TextView) findViewById(R.id.tvTermsAndConditions);

        if (getIntent().getBooleanExtra(EXTRA_SHOW_ABOUT, false)){

        }
        else
            tvTerms.setText(loadText());

        cbAccept = (CheckBox) findViewById(R.id.cbTerms);
        if(cbAccept != null) {
            cbAccept.setChecked(isAccepted);
            cbAccept.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    btnContinue.setEnabled(isChecked);
                }
            });
        }

        btnContinue = (Button) findViewById(R.id.btnTermsContinue);
        if(btnContinue != null) {
            btnContinue.setOnClickListener(new View.OnClickListener() {
                                               @Override
                                               public void onClick(View v) {
                                                   XxUtil.putTermsAcceptedStatus(Activity_Terms_Agreement.this, cbAccept.isChecked());
                                                   startLoginActivity();
                                               }
                                           }
            );
        }


    }

    private Spanned loadText() {
        if (getIntent().getBooleanExtra(EXTRA_SHOW_ABOUT, false))
            return Html.fromHtml(loadTermsString("about.txt"));
        else
            return Html.fromHtml(loadTermsString("terms.txt"));

    }

    private void startLoginActivity() {
        Intent intent = new Intent(this, Activity_Login.class);
        startActivity(intent);
        finish();
    }

    private String loadTermsString(String file) {
        String text = "";

        StringBuilder buf = new StringBuilder();
        InputStream howToHtml;

        try {
            howToHtml = getAssets().open(file);
            BufferedReader in = new BufferedReader(new InputStreamReader(howToHtml, "UTF-8"));

            while ((text = in.readLine()) != null) {
                if (text.isEmpty() || text.equals(" "))
                    buf.append("<br>");
                else
                    buf.append(text);
            }
            in.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return buf.toString();
    }
}
