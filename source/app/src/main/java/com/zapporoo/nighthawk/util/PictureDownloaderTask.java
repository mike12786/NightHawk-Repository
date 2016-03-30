package com.zapporoo.nighthawk.util;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;

import com.squareup.okhttp.OkHttpClient;
import com.zapporoo.nighthawk.callbacks.ICallbackImage;

import java.io.File;

/**
 * Created by dare on 8.12.15..
 */
public class PictureDownloaderTask extends AsyncTask<String, Void, BasicImageInfo> {
    private final ICallbackImage mCallback;
    private final OkHttpClient client;
    private final File mCacheDir;
    private final String mImageFileName;
    private final int mDesiredPictureWidth, mDesiredPictureHeight;
    private final int mFixedDimen;
    private final int mDefaultImageResId;
    private final int mRequestId;

    public PictureDownloaderTask(int requestId, ICallbackImage pCallback, String pImageFileName) {
        this.mCallback = pCallback;
        client = new OkHttpClient();

        mCacheDir = pCallback.getContext().getExternalCacheDir();
        mImageFileName = pImageFileName;
        this.mRequestId = requestId;
        this.mDesiredPictureWidth = pCallback.getDesiredPictureWidth();
        this.mDesiredPictureHeight = pCallback.getDesiredPictureHeight();
        this.mFixedDimen = pCallback.getFixedDimenType();
        this.mDefaultImageResId = pCallback.getDefaultImageResId();
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected BasicImageInfo doInBackground(String... params) {
        BasicImageInfo result = null;
        Bitmap image;
        if(params.length == 1) {
            String url = params[0];
            try {
                File imageFile = UtilImage.generatePictureFile(mCacheDir, mImageFileName);
                if(imageFile == null || !imageFile.exists())
                    imageFile = UtilImage.downloadFile(imageFile, url, client);

                if(imageFile != null) {
                    image = UtilImage.getImageFromFile(imageFile, mDesiredPictureWidth, mDesiredPictureHeight, mFixedDimen, false);
                    if(image != null) {
                        result = new BasicImageInfo(mImageFileName, image, mDefaultImageResId);
                    }
                    /*if(imageFile.exists()) {
                        imageFile.delete();
                    }*/
                }
            } catch (Exception e) {
                e.printStackTrace();
                result = new BasicImageInfo(e);
            }
        }
        return result;
    }

    @Override
    protected void onPostExecute(BasicImageInfo pImageInfo) {
        super.onPostExecute(pImageInfo);

        if(mCallback != null && mCallback.isActive() && pImageInfo != null) {
            Bitmap image;
            if(pImageInfo.getBitmap() == null || pImageInfo.getLastError() != null) {
                image = BitmapFactory.decodeResource(mCallback.getContext().getResources(), pImageInfo.getDefaultImageResId());
            }else
            {
                image = pImageInfo.getBitmap();
            }
            mCallback.imageFetched(image, mRequestId);
        }
    }
}
