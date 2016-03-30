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
import android.widget.ImageView;
import android.widget.TextView;

import com.parse.ParseException;
import com.parse.ParseFacebookUtils;
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
import java.util.Arrays;
import java.util.Collection;

/**
 * Created by Pile on 12/15/2015.
 */

public class Fragment_Personal_Home_Profile extends Fragment_Personal_Home_Tab {

    CircularImageView ivPersonalProfile;
    Button btnProfileHideMe;
    ModUser user;
    TextView tvPersonalName;
    private static final String ARG_SECTION_NUMBER = "section_number";

    private static final int REQ_IMAGE_PICKER = 10051;
    private static final int REQ_IMAGE_TAKE = 10052;
    private Uri tempImageUri;
    private boolean isImageChanged = false;
    private Button btnFacebookConnect;

    public Fragment_Personal_Home_Profile() {
    }

    /**
     * Returns a new instance of this fragment for the given section
     * number.
     */
    public static Fragment_Personal_Home_Profile newInstance(int sectionNumber) {
        Fragment_Personal_Home_Profile fragment = new Fragment_Personal_Home_Profile();
        Bundle args = new Bundle();
        args.putInt(ARG_SECTION_NUMBER, sectionNumber);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_personal_home_profile, container, false);
        user = ModUser.getModUser();
        if (user != null)
            setupGui(rootView);
        return rootView;
    }

    private void setupGui(View rootView) {
        tvPersonalName = (TextView) rootView.findViewById(R.id.tvPersonalName);
        tvPersonalName.setText(user.getFirstName() + " " + user.getLastName());

        btnProfileHideMe = (Button) rootView.findViewById(R.id.btnProfileHideMe);
        setupButton();

        ivPersonalProfile = (CircularImageView) rootView.findViewById(R.id.ivPersonalProfile);
        ivPersonalProfile.setDrawingCacheEnabled(true);
        ivPersonalProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showProfileImageOptions();
            }
        });

        btnFacebookConnect = (Button) rootView.findViewById(R.id.btnProfileFacebookConnect);
        btnFacebookConnect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                facebookConnect();
            }
        });

        fetchProfileImage(user, ivPersonalProfile);
    }

    private void setupButton() {
        if (user.isHidden()){
            btnProfileHideMe.setText(getString(R.string.lbl_unhide));
            btnProfileHideMe.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    user.setHidden(false);
                    saveUserData();
                }
            });
        }else{
            btnProfileHideMe.setText(getString(R.string.lbl_hide));
            btnProfileHideMe.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    user.setHidden(true);
                    saveUserData();
                }
            });
        }
    }

    private void saveUserData() {
        showProgressDialog(getString(R.string.progress_saving));
        user.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                dismissProgressDialog();
                if (e == null){
                    showToast(getString(R.string.alert_save_completed));
                    setupButton();
                }else{
                    user.setHidden(!user.isHidden());
                    ParseExceptionHandler.handleParseError(e, getActivity());
                }
            }
        });
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
        ivPersonalProfile.setImageDrawable(null);
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
        ParseFacebookUtils.onActivityResult(requestCode, resultCode, data);
    }

    private void setProfileImage(Uri uri) {
        if (uri == null)
            return;

        Float f = UtilImage.getImageOrientation(uri.getEncodedPath());
        int w1 = ivPersonalProfile.getLayoutParams().width;
        Bitmap bm = UtilImage.loadOptimizedImage(w1, -1, uri.getEncodedPath(), getActivity());
        Bitmap rot = null;
        if (f != 0) {
            try {
                rot = UtilImage.rotateBitmap(bm, f);
                //ivPersonalProfile.setImageBitmap(rot);
                ivPersonalProfile.setImageBitmap(UtilImage.adjustToImageView(rot, w1, w1, ivPersonalProfile));
                bm.recycle();
            } catch (Exception e) {
                showAlertDialog(getString(R.string.alert_error_default));
                return;
            }
        } else {
            //ivPersonalProfile.setImageBitmap(bm);
            ivPersonalProfile.setImageBitmap(UtilImage.adjustToImageView(bm, w1, w1, ivPersonalProfile));
        }
        isImageChanged = true;
        saveProfileImageAsFileToServer();
    }

    private void saveProfileImageAsFileToServer() {
        if (isImageChanged){
            showProgressDialog(getString(R.string.progress_saving));
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            UtilImage.getBitmapFromImageView(ivPersonalProfile).compress(Bitmap.CompressFormat.JPEG, ModUser.IMAGE_COMPRESSION_QUALITY, stream);

            final ParseFile file = new ParseFile(ModUser.IMAGE_FILE_NAME, stream.toByteArray());
            file.saveInBackground(new SaveCallback() {

                @Override
                public void done(ParseException e) {
                    if (e == null){
                        ModUser.getModUser().setProfileImage(file);
                        ModUser.getModUser().saveInBackground(new SaveCallback() {
                            @Override
                            public void done(ParseException e) {
                                dismissProgressDialog();
                                if (e != null)
                                    showAlertDialog("Error..." + e.getMessage());
                            }
                        });
                    } else {
                        dismissProgressDialog();
                        showAlertDialog("Error..." + e.getMessage());
                    }
                }
            });
        }
    }

    private void facebookConnect(){
        if (!ParseFacebookUtils.isLinked(user)) {
            showProgressDialog(getString(R.string.progress_fb_connecting));
            Collection<String> permissions = Arrays.asList("public_profile, email");
            ParseFacebookUtils.linkWithReadPermissionsInBackground(user, this, permissions, new SaveCallback() {
                @Override
                public void done(ParseException e) {
                    dismissProgressDialog();
                    if (e == null){
                        if (ParseFacebookUtils.isLinked(user)) {
                            showToast(getString(R.string.toast_fb_connected));
                        }
                    }
                    else
                        ParseExceptionHandler.handleParseError(e, getActivity());

                }
            });

        }else
            showOkDialog(getString(R.string.info_already_fb), null);
    }
}