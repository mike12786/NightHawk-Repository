package com.zapporoo.nighthawk.util;

import android.graphics.Bitmap;

/**
 * Created by dare on 9.12.15..
 */
public class BasicImageInfo {
    private final String mPictureFile;
    private final Bitmap mBitmap;
    private final int mDefaultImageResId;
    private Throwable mLastError;

    public BasicImageInfo(String pPictureFile, Bitmap pThumbBmp, int pDefaultImageResId) {
        this.mPictureFile = pPictureFile;
        this.mBitmap = pThumbBmp;
        this.mDefaultImageResId = pDefaultImageResId;
    }

    public BasicImageInfo(Throwable t) {
        this.mLastError = t;
        this.mPictureFile = null;
        this.mBitmap = null;
        this.mDefaultImageResId = -1;
    }

    public String getPictureFile() {
        return mPictureFile;
    }

    public Bitmap getBitmap() {
        return mBitmap;
    }

    public Throwable getLastError() {
        return mLastError;
    }

    public int getDefaultImageResId() {
        return mDefaultImageResId;
    }
}
