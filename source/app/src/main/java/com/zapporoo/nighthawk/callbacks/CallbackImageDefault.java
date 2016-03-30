package com.zapporoo.nighthawk.callbacks;

import android.content.Context;
import android.graphics.Bitmap;
import android.widget.ImageView;

import com.parse.ParseFile;
import com.zapporoo.nighthawk.ui.views.CircularImageView;
import com.zapporoo.nighthawk.util.UtilImage;

import java.lang.ref.WeakReference;

public class CallbackImageDefault implements ICallbackImage {

	public WeakReference<CircularImageView> ivRefCircular;
	public WeakReference<ImageView> ivRefNormal;
	
	public boolean isActive = true;
	private String TAG = "CallbackImageDefault";
	private ParseFile file;
	
	public CallbackImageDefault(CircularImageView iv){
		this.ivRefCircular = new WeakReference<>(iv);
	}
	
	public CallbackImageDefault(ImageView iv){
		this.ivRefNormal = new WeakReference<>(iv);
	}
	
	public void setImageView(CircularImageView iv){
		this.ivRefCircular = new WeakReference<>(iv);
	}
	
	public void setImageView(ImageView iv){
		this.ivRefNormal = new WeakReference<>(iv);
	}

	@Override
	public Context getContext() {
		return null;
	}

	@Override
	public int getDesiredPictureWidth() {
		return 0;
	}

	@Override
	public int getDesiredPictureHeight() {
		return 0;
	}

	@Override
	public int getFixedDimenType() {
		return 0;
	}

	@Override
	public int getDefaultImageResId() {
		return 0;
	}

	@Override
	public void imageFetched(Bitmap bitmap, int pRequestId) {
		if (!isActive())
			return;
		
		if (ivRefCircular != null){
			CircularImageView ivCirc = ivRefCircular.get();
			
			if (ivCirc != null){
				int w1 = ivCirc.getLayoutParams().width;
				UtilImage.adjustToImageView(bitmap, w1, w1, ivCirc);
				return;
			}
		}
		
		if (ivRefNormal != null){
			ImageView ivNormal = ivRefNormal.get();
			if (ivNormal != null){
				int w = ivNormal.getLayoutParams().width;
				int h = ivNormal.getLayoutParams().height;
				UtilImage.adjustToImageView(bitmap, w, h, ivNormal);
				return;
			}
		}
		
	}

	@Override
	public void imageFetchedError(String message) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setActive(boolean isActive) {
		if (!isActive && file != null)
			file.cancel();
		this.isActive = isActive;
	}

	@Override
	public boolean isActive() {
		return this.isActive;
	}

	@Override
	public void setFileForCancel(ParseFile file) {
		this.file = file;
	}

}
