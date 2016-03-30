package com.zapporoo.nighthawk.util;

import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Build;
import android.util.Log;
import android.widget.ImageView;

import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;
import com.zapporoo.nighthawk.ui.views.CircularImageView;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FilenameFilter;
import java.io.IOException;

import okio.BufferedSink;
import okio.Okio;

/**
 * Created by Pile on 12/29/2015.
 */
public class UtilImage {
    public static final String IMG_PREFIX_YELP = "yelp_";


    public static Bitmap adjustToImageView(Bitmap bm, int dW, int dH, ImageView iv) {
        Matrix matrix = new Matrix();

        try {
            //iv.setImageBitmap(bm);

            int iW = bm.getWidth();
            int iH = bm.getHeight();
            float scaleWidth = 1;// = ((float) dW) / iW;
            float scaleHeight = 1;// ((float) dH) / iH;

            int total_width = 0;
            // slika je uza
            if (iW < dW) {
                if (iH >= dH) {
                    scaleWidth = ((float) dW) / iW;
                    matrix.preScale(scaleWidth, scaleWidth);
                } else {
                    if ((((float) dW) / iW) >= ((float) dH) / iH) {
                        scaleWidth = ((float) dW) / iW;
                        matrix.preScale(scaleWidth, scaleWidth);
                    } else {
                        scaleHeight = ((float) dH) / iH;
                        matrix.preScale(scaleHeight, scaleHeight);
                    }

                }
            } else {//slika je sira
                //ako je slika vislja
                if (iH >= dH) {
                    scaleWidth = ((float) dW) / iW;
                    scaleHeight = ((float) dH) / iH;
                    if (scaleHeight > scaleWidth)
                        scaleWidth = scaleHeight;
                    else
                        scaleHeight = scaleWidth;

                    matrix.preScale(scaleWidth, scaleWidth);
                } else {//ako je niza
                    if ((((float) dW) / iW) >= ((float) dH) / iH) {
                        scaleWidth = ((float) dW) / iW;
                        matrix.preScale(scaleWidth, scaleWidth);
                    } else {
                        scaleHeight = ((float) dH) / iH;
                        matrix.preScale(scaleHeight, scaleHeight);
                    }
                }
            }
            if(iv instanceof CircularImageView) {
                int x, y;
                // ako je slika posle skaliranja sira od ekrana, treba je centrirati
                total_width = (int) (iW * scaleWidth);
                if (total_width > dW) {
                    x = (total_width - dW) / 2;
//                bmMatrix.postTranslate(, 0);// bm.getHeight());
                } else {
                    x = (dW - total_width) / 2;
                }

                int total_height = (int) (iH * scaleHeight);
                if (total_height > dH) {
                    y = (total_height - dH) / 2;
//                bmMatrix.postTranslate(0, );// bm.getHeight());
                } else {
                    y = (dH - total_height) / 2;
                }

                // odradi matrix na originalnoj bitmapi
                bm = Bitmap.createBitmap(bm, 0, 0, bm.getWidth(), bm.getHeight(), matrix, true);
                // Posto CircularImageView iz nekog razloga uvijek radi skaliranje slike - onda ovdje moramo uraditi crop bitmape.
                bm = Bitmap.createBitmap(bm, x, y, dW, dH);

                iv.setImageBitmap(bm);
                iv.setImageMatrix(new Matrix());
            }else // normalna metoda za ImageView:
            {
                // ako je slika posle skaliranja sira od ekrana, treba je centrirati
                total_width = (int) (iW * scaleWidth);
                if (total_width > dW) {
                    matrix.postTranslate(-(total_width - dW) / 2, 0);// bm.getHeight());
                }

                int total_height = (int) (iH * scaleHeight);
                if (total_height > dH) {
                    matrix.postTranslate(0, -(total_height - dH) / 2);// bm.getHeight());
                }

                iv.setImageBitmap(bm);
                iv.setImageMatrix(matrix);
            }

        } catch (Exception e) {
        }

        return bm;
    }

    public static Bitmap rotateBitmap(Bitmap source, float angle)	{
        Matrix matrix = new Matrix();
        matrix.postRotate(angle);
        return Bitmap.createBitmap(source, 0, 0, source.getWidth(), source.getHeight(), matrix, true);
    }

    public static float getImageOrientation(String filePath) {
        int orientation =-1;
        orientation = getExifOrientationAttribute(filePath);
        switch (orientation) {
            case ExifInterface.ORIENTATION_ROTATE_90:
                return 90.0f;
            case ExifInterface.ORIENTATION_ROTATE_270:
                return 270.0f;
            case ExifInterface.ORIENTATION_ROTATE_180:
                return 180.0f;
        }
        return 0;
    }

    public static int getExifOrientationAttribute(String filePath){
        ExifInterface exif;
        String exifOrientation;
        try {
            exif = new ExifInterface(filePath);
            exifOrientation = exif.getAttribute(ExifInterface.TAG_ORIENTATION);
            Log.e("ExifOrientation", "Image ExifOrientation: " + exifOrientation);
            return Integer.valueOf(exifOrientation);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }     //Since API Level 5

        return 0;
    }

    public static Bitmap loadOptimizedImage(int zeljenaSirina, int zeljenaVisina, String file_path, Context context) {
        if (file_path != null) {
            Uri selectedImage = Uri.fromFile(new File(file_path));
            return loadImageOptimally(selectedImage, context, zeljenaSirina, zeljenaVisina);
        } else
            return null;
    }

    private static Bitmap loadImageOptimally(Uri selectedImage, Context context, int zeljenaSirina, int zeljenaVisina) {
        AssetFileDescriptor fileDescriptor = null;
        Bitmap bm = null;
        BitmapFactory.Options options = new BitmapFactory.Options();

        try {
            options.inJustDecodeBounds = true;
            fileDescriptor = context.getContentResolver().openAssetFileDescriptor(selectedImage, "r");
            BitmapFactory.decodeFileDescriptor(fileDescriptor.getFileDescriptor(), null, options);

            if(zeljenaVisina == -1)
                zeljenaVisina = zeljenaSirina * options.outHeight / options.outWidth;

            options.inSampleSize = calculateInSampleSize(options, zeljenaSirina, zeljenaVisina);
            Log.e("Bitmap: ", "inSampleSize: " + options.inSampleSize + " W:" + zeljenaSirina + " H:" + zeljenaVisina);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } finally {
            try {
                options.inJustDecodeBounds = false;
                options.inScaled = false;
                options.inPreferredConfig = Bitmap.Config.RGB_565;

                bm = BitmapFactory.decodeFileDescriptor(fileDescriptor.getFileDescriptor(), null, options);
                fileDescriptor.close();
            } catch (Exception e) {
                // SdLog.e("Exception UtilImage", "Exception: Failed to load " +
                // selectedImage.getEncodedPath());
                e.printStackTrace();
            }
        }
        return bm;
    }

    private static int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {

            final int halfHeight = height / 2;
            final int halfWidth = width / 2;

            // Calculate the largest inSampleSize value that is a power of 2 and
            // keeps both
            // height and width larger than the requested height and width.
            while ((halfHeight / inSampleSize) > reqHeight && (halfWidth / inSampleSize) > reqWidth) {
                inSampleSize *= 2;
            }
        }

        return inSampleSize;
    }

    public static Bitmap getBitmapFromImageView(ImageView view){
        view.setDrawingCacheEnabled(true);
        return view.getDrawingCache();
    }


    public static File generatePictureFile(File pCacheDir, String pImageFileName) {
        File parentDir = new File(pCacheDir, ".images");
        if (!parentDir.exists()) {
            parentDir.mkdir();
        }

        return new File(parentDir, pImageFileName);
    }

    public static String generateYelpBusinessImageFileName(String imageId) {
        return IMG_PREFIX_YELP + imageId + ".jpg";
    }

    public static File downloadFile(File pDest, String pUrl, OkHttpClient pClient) {

        if (pDest.exists()) {
            pDest.delete();
        }

        Request request = new Request.Builder()
                .url(pUrl)
                .build();

        Response response;
        BufferedSink sink = null;
        try {
            response = pClient.newCall(request).execute();
            sink = Okio.buffer(Okio.sink(pDest));
            sink.writeAll(response.body().source());
            sink.close();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (sink != null) {
                try {
                    sink.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        return pDest;
    }

    public static Bitmap getImageFromFile(File pFile, int pImageWidth, int pImageHeight, int pFixedDimen, boolean pExactDimens) {
        Bitmap bm;
        int offsetX = 0, offsetY = 0;
        boolean rotate = false;
        double aspectRatio = .0;
        int desiredWidth, desiredHeight, temp;
        FileInputStream pictureFile = null;

        BitmapFactory.Options options = new BitmapFactory.Options();
        try {
            float orientation = getImageOrientation(pFile.getAbsolutePath());

            if(orientation > 89.5f && orientation < 90.5f || orientation > 269.5f && orientation < 270.5f)
                rotate = true;

            pictureFile = new FileInputStream(pFile);
            MarkableFileInputStream pictureInputStream = new MarkableFileInputStream(pictureFile);
            pictureInputStream.mark(-1);
            options.inJustDecodeBounds = true;
            BitmapFactory.decodeStream(pictureInputStream, null, options);
            pictureInputStream.reset();

            aspectRatio = (double) options.outHeight / options.outWidth;

            if(pImageHeight <= 0) {
                pImageHeight = options.outHeight;
            }

            if(pImageWidth <= 0) {
                pImageWidth = options.outWidth;
            }

            switch (pFixedDimen) {
                default:
                case 0: { // by x
                    desiredWidth = pImageWidth;
                    desiredHeight = (int) (pImageWidth * aspectRatio);
                    break;
                }
                case 1: { // by y
                    desiredHeight = pImageHeight;
                    desiredWidth = (int) (pImageHeight / aspectRatio);
                    break;
                }
                case 2: { // by x and y
                    desiredWidth = pImageWidth;
                    desiredHeight = (int) (pImageWidth * aspectRatio);

                    if(desiredHeight > pImageHeight) {
                        desiredHeight = pImageHeight;
                        desiredWidth = (int) (pImageHeight / aspectRatio);
                    }
                    break;
                }
            }

            if(rotate) {
                temp = desiredWidth;
                desiredWidth = desiredHeight;
                desiredHeight = temp;
            }

            options.inSampleSize = calculateInSampleSize(options, desiredWidth, desiredHeight);
            options.inMutable = false;
            options.inJustDecodeBounds = false;
            options.inDither = false;
            options.inPurgeable = true;

            if (Build.VERSION.SDK_INT < 12) {
                System.gc();
            }


            bm = BitmapFactory.decodeStream(pictureInputStream, null, options);
            if (bm != null) {
                if (rotate) {
                    Matrix matrix = new Matrix();
                    matrix.postRotate(orientation, desiredWidth / 2f, desiredHeight / 2f);
                    offsetX =  (options.outWidth - desiredWidth) / 4;
                    offsetY =  (options.outHeight - desiredHeight) / 4;

                    if(offsetX < 0)
                        offsetX = 0;

                    if(offsetY < 0)
                        offsetY = 0;

                    if(pExactDimens) {
                        bm = Bitmap.createScaledBitmap(bm, desiredHeight, desiredWidth, true);
                        bm = Bitmap.createBitmap(bm, 0, 0, bm.getWidth(), bm.getHeight(), matrix, true);  //rotate
                    }else {
                        // we can crop it a bit:
                        bm = Bitmap.createBitmap(bm, offsetX, offsetY, options.outWidth - offsetX, options.outHeight - offsetY, matrix, true);
                        bm = Bitmap.createScaledBitmap(bm, desiredWidth, desiredHeight, true); // it is rotated at this point so we switch height and width.
                    }
                } else {
                    if(pExactDimens)
                        bm = Bitmap.createScaledBitmap(bm, desiredWidth, desiredHeight, true);
                    else
                    {
                        // we can crop it a bit:
                        offsetX = (options.outWidth - desiredWidth) / 4;
                        offsetY = (options.outHeight - desiredHeight) / 4;

                        if(offsetX < 0)
                            offsetX = 0;

                        if(offsetY < 0)
                            offsetY = 0;
                        bm = Bitmap.createBitmap(bm, offsetX, offsetY, options.outWidth - offsetX, options.outHeight - offsetY);
                        bm = Bitmap.createScaledBitmap(bm, desiredWidth, desiredHeight, true);
                    }
                }
/*                Log.d("TEST", "Orientation: " + String.valueOf(orientation) + ", outWidth: " +
                        options.outWidth + ", outHeight: " + options.outHeight + ", desiredWidth: " + desiredWidth +
                        ", desiredHeight: " + desiredHeight + ", offsetX: " + offsetX + ", offsetY: " + offsetY);*/
            }

            return bm;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (pictureFile != null) {
                    pictureFile.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    public static void clearPicturesCache(final String pPrefix, File pCacheDir) {
        File parentDir = new File(pCacheDir, ".images");
        if (parentDir.exists()) {
            File[] pictures = parentDir.listFiles(new FilenameFilter() {
                @Override
                public boolean accept(File dir, String filename) {
                    return filename.startsWith(pPrefix);
                }
            });

            if (pictures != null) {
                for (File f : pictures) {
                    f.delete();
                }
            }

        }
    }
}
