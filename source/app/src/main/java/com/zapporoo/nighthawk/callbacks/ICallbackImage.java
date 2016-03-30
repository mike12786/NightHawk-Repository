package com.zapporoo.nighthawk.callbacks;

import android.content.Context;
import android.graphics.Bitmap;
import android.widget.ImageView;

import com.parse.ParseFile;
import com.zapporoo.nighthawk.ui.views.CircularImageView;

/**	
 * 	Callback defines what to be done with image file when it's downloaded.
 * 	Usually availability of image views should be checked on implementation
 * 	of imageFetched() method.
 * 
 */
public interface ICallbackImage {
	
	void imageFetched(Bitmap bitmap, int pRequestId);
	void imageFetchedError(String message);
	void setActive(boolean isActive);
	boolean isActive();
	void setFileForCancel(ParseFile file);
	void setImageView(CircularImageView iv);
	void setImageView(ImageView iv);

	Context getContext();

	int getDesiredPictureWidth();
	int getDesiredPictureHeight();
	int getFixedDimenType();
	int getDefaultImageResId();
}
