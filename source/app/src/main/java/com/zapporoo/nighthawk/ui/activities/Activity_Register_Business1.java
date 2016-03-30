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
import android.widget.ImageView;

import com.zapporoo.nighthawk.R;
import com.zapporoo.nighthawk.model.ModUser;
import com.zapporoo.nighthawk.ui.dialogs.DialogOptionItem;
import com.zapporoo.nighthawk.ui.fragments.Fragment_Dialog_options;
import com.zapporoo.nighthawk.util.UtilImage;
import com.zapporoo.nighthawk.util.XxUtil;

import java.io.File;

public class Activity_Register_Business1 extends ActivityDefault {

    private static final int REQ_IMAGE_PICKER = 10051;
    private static final int REQ_IMAGE_TAKE = 10052;
    private Uri tempImageUri;
    private boolean isImageSelected = false;

    ImageView ivBusinessProfile;
    EditText etBusinessName, etBusinessAddress, etBusinessTypeOfEstablishment, etHoursOfOperation, etBusinessContactNo, etBusinessWebSiteUrl;
    Button btnRegisterBusiness1Next;

    public static ModUser modBusiness;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_business1);

        setupGui();
        setupUserFromIntentData();
        //testData();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (modBusiness != null && modBusiness.getGeopoint() != null)
            etBusinessAddress.setText(modBusiness.getAddress());
    }

    private void testData() {
        etBusinessName.setText("Moody Foody");
        etBusinessAddress.setText("Night road 15, New York");
        etBusinessAddress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startMapActivity();
            }
        });
        etBusinessTypeOfEstablishment.setText("Food (ball)");
        etHoursOfOperation.setText("From 00 to OO");
        etBusinessContactNo.setText("0118 999 881 999 119 725 3");
        etBusinessWebSiteUrl.setText("www.wedeliver.com");
    }

    private void startMapActivity() {
        startNextActivity(Activity_Register_Business_Map.class);
    }


    private void setupGui() {
        ivBusinessProfile = (ImageView) findViewById(R.id.ivBusinessProfile);
        ivBusinessProfile.setDrawingCacheEnabled(true);
        ivBusinessProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showProfileImageOptions();
            }
        });

        etBusinessAddress = (EditText) findViewById(R.id.etBusinessAddress);
        etBusinessAddress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startMapActivity();
            }
        });
        etBusinessName = (EditText) findViewById(R.id.etBusinessName);
        etBusinessTypeOfEstablishment = (EditText) findViewById(R.id.etBusinessTypeOfEstablishment);
        etHoursOfOperation = (EditText) findViewById(R.id.etHoursOfOperation);
        etBusinessContactNo = (EditText) findViewById(R.id.etBusinessContactNo);
        etBusinessWebSiteUrl = (EditText) findViewById(R.id.etBusinessWebSiteUrl);



        btnRegisterBusiness1Next = (Button) findViewById(R.id.btnRegisterBusiness1Next);
        btnRegisterBusiness1Next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkAndSaveUser();
            }
        });
    }

    private void setupUserFromIntentData() {
        modBusiness = new ModUser().newBusiness();
        modBusiness.setPassword(getIntent().getStringExtra(Activity_Login_Details.USER_PASSWORD));
        modBusiness.setUsername(getIntent().getStringExtra(Activity_Login_Details.USER_EMAIL));
        modBusiness.setEmail(getIntent().getStringExtra(Activity_Login_Details.USER_EMAIL));
    }

    private void checkAndSaveUser() {
        if (!isInputValid())
            return;

        //showProgressDialog("Signing up...");

        if (isImageSelected)
            saveProfileImageBitmap();

        saveUserData();

    }

    private void saveUserData() {
        //save user
        modBusiness.setName(etBusinessName.getText().toString());
        modBusiness.setAddress(etBusinessAddress.getText().toString());
        modBusiness.setEstablishmentType(etBusinessTypeOfEstablishment.getText().toString());
        modBusiness.setWorkHours(etHoursOfOperation.getText().toString());
        modBusiness.setContactNumber(etBusinessContactNo.getText().toString());
        modBusiness.setWebSite(etBusinessWebSiteUrl.getText().toString());
        //start user home activity
        Activity_Register_Business2.user = modBusiness;
        startRegisterYourBusiness2();
    }



    private void saveProfileImageBitmap() {
        if (isImageSelected){
            modBusiness.setProfileImageBitmap(UtilImage.getBitmapFromImageView(ivBusinessProfile));
        }
    }


    private void startRegisterYourBusiness2() {
        startNextActivity(Activity_Register_Business2.class);
    }

    private boolean isInputValid() {
        if (!XxUtil.isValid(etBusinessName)) {
            XxUtil.focusSelect(etBusinessName);
            showAlertDialog("Business Name can't be empty!");
            return false;
        }

        if (!XxUtil.isValid(etBusinessAddress)) {
            XxUtil.focusSelect(etBusinessAddress);
            showAlertDialog("Address can't be empty!");
            return false;
        }

        if (!XxUtil.isValid(etBusinessTypeOfEstablishment)) {
            XxUtil.focusSelect(etBusinessTypeOfEstablishment);
            showAlertDialog("Establishment type can't be empty!");
            return false;
        }

        if (!XxUtil.isValid(etHoursOfOperation)) {
            XxUtil.focusSelect(etHoursOfOperation);
            showAlertDialog("Hours field can't be empty!");
            return false;
        }

        if (!XxUtil.isValid(etBusinessContactNo)) {
            XxUtil.focusSelect(etBusinessContactNo);
            showAlertDialog("Contact field can't be empty!");
            return false;
        }

//        if (!XxUtil.isValid(etBusinessWebSiteUrl)) {
//            XxUtil.focusSelect(etBusinessWebSiteUrl);
//            showAlertDialog("Address can't be empty!");
//            return false;
//        }

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
        ivBusinessProfile.setImageDrawable(null);
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

    private void setProfileImage(Uri uri) {
        if (uri == null)
            return;

        Float f = UtilImage.getImageOrientation(uri.getEncodedPath());
        Bitmap bm = UtilImage.loadOptimizedImage(1024, 1024, uri.getEncodedPath(), this);
        Bitmap rot = null;
        int w1 = ivBusinessProfile.getLayoutParams().width;
        if (f != 0) {
            try {
                rot = UtilImage.rotateBitmap(bm, f);
                ivBusinessProfile.setImageBitmap(rot);
                //ivBusinessProfile.setImageBitmap(UtilImage.adjustToImageView(rot, w1, w1, ivBusinessProfile));
                bm.recycle();
            } catch (Exception e) {
            }
        } else {
            ivBusinessProfile.setImageBitmap(bm);
            //ivBusinessProfile.setImageBitmap(UtilImage.adjustToImageView(bm, w1, w1, ivBusinessProfile));
            //bm.recycle();
        }
        isImageSelected = true;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startPrevoiusActivitySlide(this);
    }

}
