package com.zapporoo.nighthawk.ui.dialogs;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;

import com.zapporoo.nighthawk.R;

public class DialogOptionItem {
	OnClickListener onClickListener;
	
	public static final int TYPE_SIGNUP_EMAIL = 0;
	public static final int TYPE_SIGNUP_FACEBOOK = 1;
	public static final int TYPE_SIGNUP_TWITTER = 2;
	public static final int TYPE_SIGNUP_GOOGLE_PLUS = 3;
	
	public static final int TYPE_POST_REPOST = 4;
	public static final int TYPE_POST_REPORT_INAPPROPRIATE = 5;
	public static final int TYPE_POST_REPORTED = 13;
	
	public static final int TYPE_VIDEO_RECORD = 6;
	public static final int TYPE_AUDIO_RECORD = 7;
	public static final int TYPE_TEXT = 8;
	
	public static final int TYPE_PHOTO_TAKE = 9;
	public static final int TYPE_PHOTO_PICK = 10;
	public static final int TYPE_DELETE = 11;
	
	public static final int TYPE_VIDEO_YOUTUBE = 12;
	public static final int TYPE_POST_EDIT = 14;
	
	private Context mContext;
	private LayoutInflater mInflater;
	private View vMenuItem;
	private int mType;
	private String text;
	
	public DialogOptionItem(Context context, int type, OnClickListener onClickListener){
		this.mContext = context;
		this.mInflater = LayoutInflater.from(context);
		this.onClickListener = onClickListener;
		this.mType = type;
	}
		@SuppressLint("InflateParams")
	public View getView(){
		vMenuItem = mInflater.inflate(R.layout.dialog_option_item, null);
		ImageView iv = (ImageView) vMenuItem.findViewById(R.id.ivDialogOption);
		TextView tv = (TextView) vMenuItem.findViewById(R.id.tvDialogOption);
		
		switch (mType) {
//			case TYPE_SIGNUP_EMAIL:	setImageDrawable(iv, R.drawable.ic_menu_mail);
//				text = "EMAIL"; tv.setText(text);break;
//			case TYPE_SIGNUP_FACEBOOK:setImageDrawable(iv, R.drawable.ic_menu_facebook);
//				text = "FACEBOOK"; tv.setText(text);break;
//			case TYPE_SIGNUP_TWITTER:setImageDrawable(iv, R.drawable.ic_menu_twitter);
//				text = "TWITTER"; tv.setText(text);break;
//			case TYPE_SIGNUP_GOOGLE_PLUS:setImageDrawable(iv, R.drawable.ic_menu_google);
//				text = "GOOGLE PLUS"; tv.setText(text);break;
//			case TYPE_POST_REPORT_INAPPROPRIATE:setImageDrawable(iv, R.drawable.ic_menu_report);
//				text = "Report as inappropriate"; tv.setText(text);break;
//			case TYPE_POST_REPORTED:setImageDrawable(iv, R.drawable.ic_menu_report);
//				text = "Reported"; tv.setText(text); tv.setEnabled(false); break;
//			case TYPE_POST_REPOST:setImageDrawable(iv, R.drawable.ic_menu_repost);
//				text = "Re-post"; tv.setText(text);break;
//			case TYPE_VIDEO_RECORD:setImageDrawable(iv, R.drawable.ic_option_video);
//				text = "Record Video"; tv.setText(text);break;
//			case TYPE_AUDIO_RECORD:setImageDrawable(iv, R.drawable.ic_menu_audio);
//				text = "Record Audio"; tv.setText(text);break;
//			case TYPE_TEXT:setImageDrawable(iv, R.drawable.ic_menu_text);
//				text = "Write Text"; tv.setText(text);break;
//			case TYPE_VIDEO_YOUTUBE:setImageDrawable(iv, R.drawable.ic_menu_youtube);
//				text = "Add YouTube link"; tv.setText(text);break;
			case TYPE_PHOTO_TAKE:setImageDrawable(iv, R.drawable.ic_menu_photo_take);
				text = "Take Photo"; tv.setText(text);break;
			case TYPE_PHOTO_PICK:setImageDrawable(iv, R.drawable.ic_menu_photo_pick);
				text = "Select from Existing"; tv.setText(text);break;
			case TYPE_DELETE:setImageDrawable(iv, R.drawable.ic_menu_delete);
				text = "Remove" +
						""; tv.setText(text);break;
//			case TYPE_POST_EDIT:setImageDrawable(iv, R.drawable.ic_menu_repost);
//				text = "Edit"; tv.setText(text);break;
		
		default:
			break;
		}
		
		vMenuItem.setOnClickListener(onClickListener);
		return vMenuItem;
	}
	
	@SuppressLint("NewApi")
	private void setImageDrawable(ImageView iv, int drawable){
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
			iv.setImageDrawable(mContext.getDrawable(drawable));
		}else
			iv.setImageDrawable(mContext.getResources().getDrawable(drawable));
	}
	
	public OnClickListener getOnClickListener(){
		return onClickListener;
	}

	public CharSequence getText() {
		// TODO Auto-generated method stub
		return null;
	}

}
