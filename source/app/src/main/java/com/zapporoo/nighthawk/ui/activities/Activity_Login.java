package com.zapporoo.nighthawk.ui.activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.parse.GetCallback;
import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseFacebookUtils;
import com.parse.ParseUser;
import com.parse.RequestPasswordResetCallback;
import com.parse.SaveCallback;
import com.quickblox.auth.QBAuth;
import com.quickblox.auth.model.QBSession;
import com.quickblox.core.QBEntityCallback;
import com.quickblox.core.QBEntityCallbackImpl;
import com.quickblox.core.exception.QBResponseException;
import com.quickblox.users.QBUsers;
import com.quickblox.users.model.QBUser;
import com.zapporoo.nighthawk.R;
import com.zapporoo.nighthawk.model.ModRating;
import com.zapporoo.nighthawk.model.ModUser;
import com.zapporoo.nighthawk.quickblox.util.ChatService;
import com.zapporoo.nighthawk.ui.fragments.Fragment_DialogForgotPassword;
import com.zapporoo.nighthawk.util.NhLog;
import com.zapporoo.nighthawk.util.ParseExceptionHandler;
import com.zapporoo.nighthawk.util.XxUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;
import java.util.Collection;


public class Activity_Login extends ActivityDefault implements QBEntityCallback {

    private static final String TAG = "Activity_Login";
    Button btnLogin, btnFacebookLogin;
    TextView tvForgotPassword, tvRegister;
    Fragment_DialogForgotPassword mDialogForget;
    EditText etLoginEmail, etLoginPassword;

    Button btnTest;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        setupGui();
        ModUser.logOutInBackground();
    }

    private void setupGui() {
        btnLogin = (Button) findViewById(R.id.btnLogin);
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startLoginCheck();
            }
        });

        tvForgotPassword = (TextView) findViewById(R.id.tvForgotPassword);
        tvForgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showForgotDialog();
            }
        });
        tvRegister = (TextView) findViewById(R.id.tvRegister);
        tvRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startChooseAccount();
            }
        });

        btnFacebookLogin = (Button)findViewById(R.id.btnFacebookLogin);
        btnFacebookLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startFacebookLoginCheck();
            }
        });

        etLoginEmail = (EditText) findViewById(R.id.etLoginEmail);
        etLoginPassword = (EditText) findViewById(R.id.etLoginPassword);

        btnTest = (Button) findViewById(R.id.btnTest);
        btnTest.setVisibility(View.INVISIBLE);
        btnTest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                testClass();
            }
        });

    }

    private void testClass() {
//        ModRelationship relationship = new ModRelationship();
//        relationship.setFrom(ModPersonal.getModUser());
//        relationship.setStatus(ModRelationship.VALUE_STATUS_FRIENDS);
//        relationship.saveInBackground();

        ModUser.queryAllBusiness().getFirstInBackground(new GetCallback<ModUser>() {
            @Override
            public void done(ModUser object, ParseException e) {

                ModRating.addNewRating("good", ModRating.VALUE_RATING_3_STARS, object).saveInBackground(new SaveCallback() {
                    @Override
                    public void done(ParseException e) {
                        if (e == null)
                            Log.e("Done", "done");
                        else
                            Log.e("Exception", e.getMessage());
                    }
                });
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        ParseFacebookUtils.onActivityResult(requestCode, resultCode, data);
    }

    private void startFacebookLoginCheck() {
        facebookLogin();
    }

    private void startLoginCheck() {
        if (isInputValid() && isNetworkAvailableShowDialog()){
            showProgressDialog("Logging in...");
            ModUser.logInInBackground(etLoginEmail.getText().toString(), etLoginPassword.getText().toString(), new LogInCallback() {
                @Override
                public void done(ParseUser user, ParseException e) {
                    dismissProgressDialog();
                    if (e != null && e.getCode() == ParseException.OBJECT_NOT_FOUND){
                        showAlertDialog("Invalid login parameters!");
                        return;
                    }

                    if (e != null){
                        showAlertDialog("Server error!");
                        return;
                    }

                    if (user == null){
                        showAlertDialog("Invalid login parameters!");
                        return;
                    }

//                    if (!user.getBoolean("emailVerified")){
//                        showAlertDialog("Email verification error/Email not verified, please check your inbox!");
//                        ParseUser.logOutInBackground();
//                        return;
//                    }

                    if (user.getInt(ModUser.KEY_TYPE) == ModUser.VALUE_TYPE_BUSINESS)
                        startBusinessHomeActivity();

                    if (user.getInt(ModUser.KEY_TYPE) == ModUser.VALUE_TYPE_PERSONAL)
                        startPersonalHomeActivity();
                }
            });
        }
    }

    private void startBusinessHomeActivity() {
        startNextActivityClearTask(Activity_Home_Business.class);
    }

    private void startPersonalHomeActivity() {
        dismissProgressDialog();
        startNextActivityClearTask(Activity_Home_Personal.class);
    }

    private boolean isInputValid() {
        boolean validity = true;

        if (!XxUtil.isValid(etLoginEmail)) {
            XxUtil.focusSelect(etLoginEmail);
            showAlertDialog("Email can't be empty!");
            return false;
        }

        if (!XxUtil.isEmailValid(etLoginEmail)) {
            XxUtil.focusSelect(etLoginEmail);
            showAlertDialog("Invalid email!");
            return false;
        }

        if (!XxUtil.isValid(etLoginPassword)) {
            XxUtil.focusSelect(etLoginPassword);
            showAlertDialog("Password can't be empty!");
            return false;
        }

        return validity;
    }

    private void showForgotDialog() {
        mDialogForget = Fragment_DialogForgotPassword.newInstance(2);
        mDialogForget.showDialog(this);
    }

    private void startChooseAccount(){
        startNextActivity(Activity_Choose_Account.class);
    }

    public void forgotPassword(String email) {
        mDialogForget.dismiss();
        if(!isNetworkAvailableShowDialog())
            return;

        showProgressDialogSpinning();
        ParseUser.requestPasswordResetInBackground(email, new RequestPasswordResetCallback() {
            public void done(ParseException e) {
                dismissProgressDialog();
                if (e == null) {
                    showAlertDialog("PLEASE CHECK YOUR EMAIL");
                } else {
                    if (e.getCode() == ParseException.EMAIL_NOT_FOUND)
                        showAlertDialog("PLEASE CHECK YOUR EMAIL");
                    else
                        showAlertDialog("Error!\nPLEASE TRY AGAIN LATER");
                }
            }
        });
    }








    //////////
    //FACEBOOK
    //////////

    private void facebookLogin() {
        showProgressDialogSpinning();
        Collection<String> permissions = Arrays.asList("public_profile, email");
        ParseFacebookUtils.logInWithReadPermissionsInBackground(this, permissions, new LogInCallback() {
            @Override
            public void done(ParseUser parseUser, ParseException e) {
                if (e == null) {
                    if (parseUser != null) {
                        if (parseUser.isNew()) {
                            //dismissProgressDialog();
                            ((ModUser)parseUser).setMessagesEnabled(true);
                            ((ModUser)parseUser).setHidden(false);
                            ((ModUser)parseUser).setNotificationsEnabled(true);
                            ((ModUser)parseUser).setYelpSearchRadiusMiles(2);
                            facebookUserFetch();
                        } else {
                            startPersonalHomeActivity();
                        }
                    } else {
                        dismissProgressDialog();
                        showAlertDialog("Unable to sign in. Please try later!");
                    }
                } else{
                    abortSignup();
                    ParseExceptionHandler.handleParseError(e, Activity_Login.this, "Server error, please try again later.");
                }
            }
        });
    }

    private void facebookUserFetch() {
        GraphRequest request = GraphRequest.newMeRequest(AccessToken.getCurrentAccessToken(), new GraphRequest.GraphJSONObjectCallback() {
            @Override
            public void onCompleted(JSONObject object, GraphResponse response) {
                if (object != null){
                    ModUser current = ModUser.getModUser();
                    try {
                        String first_name = object.getString("first_name");
                        String last_name = object.getString("last_name");
                        current.setName(first_name, last_name);
                        current.put(ModUser.KEY_TYPE, ModUser.VALUE_TYPE_PERSONAL);
                        signUpChatSessionInitialize();
                    } catch (JSONException e) {
                        abortSignup();
                        e.printStackTrace();
                    }
                    //Toast.makeText(Activity_Login.this, "Got some...", Toast.LENGTH_SHORT).show();
                }else{
                    abortSignup();
                    //Toast.makeText(Activity_Login.this, "Got none...", Toast.LENGTH_SHORT).show();
                }
            }
        });
        Bundle parameters = new Bundle();
        parameters.putString("fields", "id, first_name, last_name");
        request.setParameters(parameters);
        request.executeAsync();
    }

    private void signUpChatSessionInitialize(){
        QBAuth.createSession(new QBEntityCallback<QBSession>() {

            @Override
            public void onSuccess(QBSession session, Bundle params) {
                signUpChatUser();
            }

            @Override
            public void onError(QBResponseException errors) {
                abortSignup();
                showAlertDialog("Error with server communication, plese try again later");
                NhLog.e(TAG, "" + errors.getErrors());
            }
        });
    }

    QBUser qbuser;
    private void signUpChatUser(){
        ModUser modPersonal = ModUser.getModUser();
        String s = modPersonal.getChatUsername();
        qbuser = new QBUser(s, s);
        qbuser.setFullName(modPersonal.getPersonalName());
        qbuser.setPassword(modPersonal.getChatPassword());

        QBUsers.signUp(qbuser, new QBEntityCallback<QBUser>() {

            @Override
            public void onSuccess(QBUser user, Bundle arg1) {
                ChatService.initIfNeed(Activity_Login.this);
                ChatService.getInstance().login(qbuser, Activity_Login.this);
            }

            @Override
            public void onError(QBResponseException errors) {
                abortSignup();
                showAlertDialog("Error with server communication, please try again later");
                NhLog.e(TAG, "" + errors.getErrors());
            }

        });
    }


    @Override
    public void onSuccess(Object o, Bundle bundle) {
        ModUser.getModUser().setChatID(qbuser.getId());
        ModUser.getModUser().saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e == null){
                    startPersonalHomeActivity();
                }else
                    abortSignup();
            }
        });
    }

    @Override
    public void onError(QBResponseException e) {
        abortSignup();
    }


    private void abortSignup() {
        dismissProgressDialog();
    }
}
