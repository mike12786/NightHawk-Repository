package com.zapporoo.nighthawk.ui.fragments;

import android.app.Activity;
import android.content.ClipData;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.SaveCallback;
import com.zapporoo.nighthawk.R;
import com.zapporoo.nighthawk.callbacks.ICallbackImage;
import com.zapporoo.nighthawk.model.ModUser;
import com.zapporoo.nighthawk.ui.dialogs.DialogOptionItem;
import com.zapporoo.nighthawk.ui.views.CircularImageView;
import com.zapporoo.nighthawk.util.ParseExceptionHandler;
import com.zapporoo.nighthawk.util.UtilImage;
import com.zapporoo.nighthawk.util.XxUtil;

import java.io.ByteArrayOutputStream;
import java.io.File;

/**
 * Created by Pile on 12/15/2015.
 */

public class Fragment_Business_Home_EditInfo extends FragmentDefault {
    EditText etBusinessWebSiteUrl, etBusinessShortDescription, etBusinessContactNo,
            etBusinessHoursOfOperation, etBusinessTypeOfEstablishment, etBusinessAddress;
    Button btnBusinessEditInfo;
    ImageView ivBusinessEditInfo;

    private static final String ARG_SECTION_NUMBER = "section_number";
    private ModUser user;

    private static final int REQ_IMAGE_PICKER = 10051;
    private static final int REQ_IMAGE_TAKE = 10052;
    private Uri tempImageUri;
    private boolean isImageChanged = false;

    public Fragment_Business_Home_EditInfo() {
    }

    /**
     * Returns a new instance of this fragment for the given section
     * number.
     */
    public static Fragment_Business_Home_EditInfo newInstance(int sectionNumber) {
        Fragment_Business_Home_EditInfo fragment = new Fragment_Business_Home_EditInfo();
        Bundle args = new Bundle();
        args.putInt(ARG_SECTION_NUMBER, sectionNumber);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_business_edit_info, container, false);
        user = ModUser.getModUser();
        if (user == null) {
            startSplashActivity();
        }else
            setupGui(rootView);
        return rootView;
    }

    private void setupGui(View rootView) {
        etBusinessAddress = (EditText) rootView.findViewById(R.id.etBusinessAddress);
        etBusinessAddress.setText(user.getAddress());

        etBusinessContactNo = (EditText) rootView.findViewById(R.id.etBusinessContactNo);
        etBusinessContactNo.setText(user.getContactNumber());

        etBusinessHoursOfOperation = (EditText) rootView.findViewById(R.id.etBusinessHoursOfOperation);
        etBusinessHoursOfOperation.setText(user.getWorkHours());

        etBusinessShortDescription = (EditText) rootView.findViewById(R.id.etBusinessShortDescription);
        etBusinessShortDescription.setText(user.getShortDescription());

        etBusinessTypeOfEstablishment = (EditText) rootView.findViewById(R.id.etBusinessTypeOfEstablishment);
        etBusinessTypeOfEstablishment.setText(user.getEstablishmentType());

        etBusinessWebSiteUrl = (EditText) rootView.findViewById(R.id.etBusinessWebSiteUrl);
        etBusinessWebSiteUrl.setText(user.getWebSite());

        btnBusinessEditInfo = (Button) rootView.findViewById(R.id.btnBusinessEditInfo);
        btnBusinessEditInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isInputValid())
                    saveUserStart();
            }
        });

        ivBusinessEditInfo = (ImageView) rootView.findViewById(R.id.ivBusinessEditInfo);
        ivBusinessEditInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showProfileImageOptions();
            }
        });

        fetchProfileImage(user, ivBusinessEditInfo);
    }

    private boolean isInputValid() {
        return (XxUtil.isValid(etBusinessAddress)
                && XxUtil.isValid(etBusinessContactNo)
                && XxUtil.isValid(etBusinessHoursOfOperation)
                && XxUtil.isValid(etBusinessShortDescription)
                && XxUtil.isValid(etBusinessTypeOfEstablishment)
                && XxUtil.isValid(etBusinessWebSiteUrl)
        );
    }

    private void saveUserStart() {
        showProgressDialog(getString(R.string.progress_saving));
        if (isImageChanged){
            saveProfileImageBitmap();
            saveProfileImageAsFileToServer();
        }
        else
            saveUserData();
    }

    private void saveUserData() {
        user.setAddress(etBusinessAddress.getText().toString());
        user.setContactNumber(etBusinessContactNo.getText().toString());
        user.setWorkHours(etBusinessHoursOfOperation.getText().toString());
        user.setShortDescription(etBusinessShortDescription.getText().toString());
        user.setEstablishmentType(etBusinessTypeOfEstablishment.getText().toString());
        user.setWebSite(etBusinessWebSiteUrl.getText().toString());

        user.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                dismissProgressDialog();
                if (e == null)
                    showToast(getString(R.string.alert_save_completed));
                else
                    ParseExceptionHandler.handleParseError(e, getActivity());
            }
        });
    }

    private void saveProfileImageBitmap() {
        if (isImageChanged){
            user.setProfileImageBitmap(UtilImage.getBitmapFromImageView(ivBusinessEditInfo));
        }
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
        }else
            saveUserData();

    }



    private void showProfileImageOptions() {
        final Fragment_Dialog_options dialogOptions = Fragment_Dialog_options.newInstance(1);
        Context context = getActivity();
        DialogOptionItem itemTake = new DialogOptionItem(context, DialogOptionItem.TYPE_PHOTO_TAKE, new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                dialogOptions.dismiss();
                profilePhotoStartTake();
                // testCamera();
            }
        });

        DialogOptionItem itemPick = new DialogOptionItem(context, DialogOptionItem.TYPE_PHOTO_PICK, new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                dialogOptions.dismiss();
                profilePhotoStartPick();
            }
        });

        DialogOptionItem itemDelete = new DialogOptionItem(context, DialogOptionItem.TYPE_DELETE, new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                dialogOptions.dismiss();
                profilePhotoRemove();
            }
        });

        dialogOptions.addItem(itemTake);
        dialogOptions.addItem(itemPick);
        dialogOptions.addItem(itemDelete);
        dialogOptions.showDialog(getActivity());
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
        ivBusinessEditInfo.setImageDrawable(null);
        isImageChanged = true;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Uri image_uri;

        switch (resultCode) {
            case  Activity.RESULT_OK: {

                if (requestCode == REQ_IMAGE_PICKER){
                    image_uri = data.getData();
                    image_uri = Uri.parse(XxUtil.getRealPathFromURI(getActivity(), image_uri));
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
        Bitmap bm = UtilImage.loadOptimizedImage(1024, 1024, uri.getEncodedPath(), getActivity());
        Bitmap rot = null;
        //int w1 = ivBusinessEditInfo.getLayoutParams().width;
        if (f != 0) {
            try {
                rot = UtilImage.rotateBitmap(bm, f);
                ivBusinessEditInfo.setImageBitmap(rot);
                //ivBusinessProfile.setImageBitmap(UtilImage.adjustToImageView(rot, w1, w1, ivBusinessProfile));
                bm.recycle();
            } catch (Exception e) {
            }
        } else {
            ivBusinessEditInfo.setImageBitmap(bm);
            //ivBusinessProfile.setImageBitmap(UtilImage.adjustToImageView(bm, w1, w1, ivBusinessProfile));
            //bm.recycle();
        }
        isImageChanged = true;
    }


}