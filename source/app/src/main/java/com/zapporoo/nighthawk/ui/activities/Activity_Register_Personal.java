package com.zapporoo.nighthawk.ui.activities;

import android.app.Activity;
import android.content.ClipData;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.SaveCallback;
import com.parse.SignUpCallback;
import com.quickblox.auth.QBAuth;
import com.quickblox.auth.model.QBSession;
import com.quickblox.core.QBEntityCallback;
import com.quickblox.core.QBEntityCallbackImpl;
import com.quickblox.core.exception.QBResponseException;
import com.quickblox.users.QBUsers;
import com.quickblox.users.model.QBUser;
import com.zapporoo.nighthawk.R;
import com.zapporoo.nighthawk.model.ModUser;
import com.zapporoo.nighthawk.quickblox.util.ChatService;
import com.zapporoo.nighthawk.ui.dialogs.DialogOptionItem;
import com.zapporoo.nighthawk.ui.fragments.Fragment_Dialog_options;
import com.zapporoo.nighthawk.ui.views.CircularImageView;
import com.zapporoo.nighthawk.util.NhLog;
import com.zapporoo.nighthawk.util.UtilImage;
import com.zapporoo.nighthawk.util.XxUtil;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.List;

public class Activity_Register_Personal extends ActivityDefault implements QBEntityCallback {

    private static final int REQ_IMAGE_PICKER = 10051;
    private static final int REQ_IMAGE_TAKE = 10052;
    private static final String TAG = "Activity_Register_Personal";

    Button btnFinish;
    ModUser modPersonal;
    EditText etFirstName;
    EditText etLastName;
    CircularImageView ivProfile;

    private Uri tempImageUri;
    private boolean isImageSelected = false;
    private QBUser qbuser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_personal);
        setupGui();
        setupUserFromIntentData();
    }

    private void setupUserFromIntentData() {
        modPersonal = new ModUser().newPersonal();
        modPersonal.setPassword(getIntent().getStringExtra(Activity_Login_Details.USER_PASSWORD));
        modPersonal.setUsername(getIntent().getStringExtra(Activity_Login_Details.USER_EMAIL));
        modPersonal.setEmail(getIntent().getStringExtra(Activity_Login_Details.USER_EMAIL));
        modPersonal.setShowMessageDialog(true);
    }

    private void setupGui() {
        ivProfile = (CircularImageView) findViewById(R.id.ivPersonalProfile);
        ivProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showProfileImageOptions();
            }
        });
        ivProfile.setDrawingCacheEnabled(true);
        btnFinish = (Button) findViewById(R.id.btnPersonalFinish);
        btnFinish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkAndSaveUser();
            }
        });
        etFirstName = (EditText) findViewById(R.id.etPersonalFirstName);
        etLastName = (EditText) findViewById(R.id.etPersonalLastName);
    }

    private void checkAndSaveUser() {
        if (!isInputValid())
            return;

        showProgressDialog("Signing up...");

        if (isImageSelected)
            saveProfileImageAsFileToServer();
        else
            signUpChatSessionInitialize();

    }

    private void saveProfileImageAsFileToServer() {
        if (isImageSelected){
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            UtilImage.getBitmapFromImageView(ivProfile).compress(Bitmap.CompressFormat.JPEG, ModUser.IMAGE_COMPRESSION_QUALITY, stream);

            final ParseFile file = new ParseFile(ModUser.IMAGE_FILE_NAME, stream.toByteArray());
            file.saveInBackground(new SaveCallback() {

                @Override
                public void done(ParseException e) {
                    if (e == null){
                        modPersonal.setProfileImage(file);
                        signUpChatSessionInitialize();
                    } else {
                        dismissProgressDialog();
                        showAlertDialog("Error..." + e.getMessage());
                    }
                }
            });
        }
    }

    private void signUpChatSessionInitialize(){
        QBAuth.createSession(new QBEntityCallbackImpl<QBSession>() {

            @Override
            public void onSuccess(QBSession session, Bundle params) {
                signUpChatUser();
            }

            @Override
            public void onError(QBResponseException errors) {
                dismissProgressDialog();
                showAlertDialog("Error with server communication, plese try again later");
                NhLog.e(TAG, "" + errors.getErrors());
            }
        });
    }

    private void signUpChatUser(){
        modPersonal.setName(etFirstName.getText().toString(), etLastName.getText().toString());
        modPersonal.setEmail(modPersonal.getUsername());
        String s = modPersonal.getChatUsername();
        qbuser = new QBUser(s, s);
        qbuser.setFullName(modPersonal.getPersonalName());
        qbuser.setPassword(modPersonal.getChatPassword());

        QBUsers.signUp(qbuser, new QBEntityCallback<QBUser>() {

            @Override
            public void onSuccess(QBUser user, Bundle arg1) {
                ChatService.initIfNeed(Activity_Register_Personal.this);
                ChatService.getInstance().login(qbuser, Activity_Register_Personal.this);
            }

            @Override
            public void onError(QBResponseException errors) {
                dismissProgressDialog();
                showAlertDialog("Error with server communication, please try again later");
                NhLog.e(TAG, "" + errors.getErrors());
            }

        });
    }

    private void signUpParseUser(final ModUser modPersonal, QBUser qbuser) {
        modPersonal.setName(etFirstName.getText().toString(), etLastName.getText().toString());
        modPersonal.setChatID(qbuser.getId());
        modPersonal.signUpInBackground(new SignUpCallback() {
            @Override
            public void done(ParseException e) {
                dismissProgressDialog();

                if (e != null) {
                    showAlertDialog("Error..." + e.getMessage());
                    deleteChatUser(Activity_Register_Personal.this.qbuser);
                }

                if (e == null)
                    startUserHomeActivity();
            }
        });
    }

    protected void deleteChatUser(QBUser user) {
        QBUsers.deleteUser(user.getId(), new QBEntityCallback<Void>() {

            @Override
            public void onSuccess(Void aVoid, Bundle bundle) {

            }

            @Override
            public void onError(QBResponseException errors) {
                dismissProgressDialog();
                showAlertDialog(getString(R.string.error_server_error) + " " + errors.toString());
            }
        });

    }



    private void startUserHomeActivity() {
        startNextActivityClearTask(Activity_Home_Personal.class);
    }

    private boolean isInputValid() {
        if (!XxUtil.isValid(etFirstName)) {
            XxUtil.focusSelect(etFirstName);
            showAlertDialog("First Name can't be empty!");
            return false;
        }

        if (!XxUtil.isValid(etLastName)) {
            XxUtil.focusSelect(etLastName);
            showAlertDialog("Last Name can't be empty!");
            return false;
        }

        return true;
    }

    private void showProfileImageOptions() {
        final Fragment_Dialog_options dialogOptions = Fragment_Dialog_options.newInstance(1);

        DialogOptionItem itemTake = new DialogOptionItem(this, DialogOptionItem.TYPE_PHOTO_TAKE, new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                dialogOptions.dismiss();
                profilePhotoStartTake();
                // testCamera();
            }
        });

        DialogOptionItem itemPick = new DialogOptionItem(this, DialogOptionItem.TYPE_PHOTO_PICK, new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                dialogOptions.dismiss();
                profilePhotoStartPick();
            }
        });

        DialogOptionItem itemDelete = new DialogOptionItem(this, DialogOptionItem.TYPE_DELETE, new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                dialogOptions.dismiss();
                profilePhotoRemove();
            }
        });

        dialogOptions.addItem(itemTake);
        dialogOptions.addItem(itemPick);
        dialogOptions.addItem(itemDelete);
        dialogOptions.showDialog(this);
    }

    protected void profilePhotoStartTake() {
        // Determine Uri of camera image to save.
        File root = new File(XxUtil.getRootDirectory());
        root.mkdirs();
        String fname = XxUtil.getUniqueImageFilename(".jpg");
        File sdImageMainDirectory = new File(root, fname);
        tempImageUri = Uri.fromFile(sdImageMainDirectory);

        // Camera.
        Intent captureIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
        captureIntent.putExtra(MediaStore.EXTRA_OUTPUT, tempImageUri);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
            captureIntent.setClipData(ClipData.newRawUri(null, tempImageUri));
        startActivityForResult(captureIntent, REQ_IMAGE_TAKE);
    }

    protected void profilePhotoStartPick() {
        // Filesystem.
        // final Intent galleryIntent = new Intent();
        Intent galleryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        // galleryIntent.setType("image/*");
        // galleryIntent.setAction(Intent.ACTION_GET_CONTENT);

        // Chooser of filesystem options.
        final Intent chooserIntent = Intent.createChooser(galleryIntent, "Select Source");
        startActivityForResult(chooserIntent, REQ_IMAGE_PICKER);

    }

    protected void profilePhotoRemove() {
        ivProfile.setImageDrawable(XxUtil.getDrawable(this, R.drawable.ic_image_user));
        isImageSelected = false;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Uri image_uri;
        switch (resultCode) {
            case  Activity.RESULT_OK: {

                if (requestCode == REQ_IMAGE_PICKER){
                    image_uri = data.getData();
                    image_uri = Uri.parse(XxUtil.getRealPathFromURI(this, image_uri));
                    setProfileImage(image_uri);
                }

                if (requestCode == REQ_IMAGE_TAKE) {
                    setProfileImage(tempImageUri);
                }
                break;
            }
        }
    }

//    private void setProfileImage(Uri uri) {
//        if (uri == null)
//            return;
//
//        Float f = UtilImage.getImageOrientation(uri.getEncodedPath());
//        Bitmap bm = UtilImage.loadOptimizedImage(1024, 1024, uri.getEncodedPath(), this);
//        Bitmap rot = null;
//        int w1 = ivProfile.getLayoutParams().width;
//        if (f != 0) {
//            try {
//                rot = UtilImage.rotateBitmap(bm, f);
//                //ivProfile.setImageBitmap(UtilImage.adjustToImageView(rot, w1, w1, ivProfile));
//                ivProfile.setImageBitmap(rot);
//                bm.recycle();
//            } catch (Exception e) {
//            }
//        } else {
//            //ivProfile.setImageBitmap(UtilImage.adjustToImageView(bm, w1, w1, ivProfile));
//            ivProfile.setImageBitmap(bm);
//            //bm.recycle();
//        }
//        isImageSelected = true;
//    }

    private void setProfileImage(Uri uri) {
        if (uri == null)
            return;

        Float f = UtilImage.getImageOrientation(uri.getEncodedPath());
        int w1 = ivProfile.getLayoutParams().width;
        Bitmap bm = UtilImage.loadOptimizedImage(w1, -1, uri.getEncodedPath(), this);
        Bitmap rot = null;
        if (f != 0) {
            try {
                rot = UtilImage.rotateBitmap(bm, f);
                //ivPersonalProfile.setImageBitmap(rot);
                ivProfile.setImageBitmap(UtilImage.adjustToImageView(rot, w1, w1, ivProfile));
                bm.recycle();
            } catch (Exception e) {
                showAlertDialog(getString(R.string.alert_error_default));
                return;
            }
        } else {
            //ivPersonalProfile.setImageBitmap(bm);
            ivProfile.setImageBitmap(UtilImage.adjustToImageView(bm, w1, w1, ivProfile));
        }
        isImageSelected = true;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startPrevoiusActivitySlide(this);
    }

    @Override
    public void onSuccess(Object o, Bundle bundle) {
        signUpParseUser(modPersonal, qbuser);
    }

    @Override
    public void onError(QBResponseException errors) {
        dismissProgressDialog();
        showAlertDialog(getString(R.string.error_server_error) + " " + errors.getErrors());
    }
}
