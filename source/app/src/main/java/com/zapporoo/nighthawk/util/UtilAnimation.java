package com.zapporoo.nighthawk.util;

import android.app.Activity;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;

import com.zapporoo.nighthawk.R;

public class UtilAnimation {
	public static void startNextActivitySlide(Activity activity) {
		activity.overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
	}

	public static void startPrevoiusActivitySlide(Activity activity) {
		activity.overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
	}

    public static void setAnimationFadeOut(View b) {
        //b.setVisibility(View.GONE);
        Animation fadeIn = new AlphaAnimation(1, 0);
        fadeIn.setInterpolator(new AccelerateInterpolator()); //add this
        fadeIn.setDuration(600);

        AnimationSet animation = new AnimationSet(false); //change to false
        animation.addAnimation(fadeIn);
        b.setAnimation(animation);
        b.startAnimation(animation);
        b.setVisibility(View.GONE);
    }

    public static void setAnimationFadeIn(View b) {
        b.setVisibility(View.GONE);
        Animation fadeIn = new AlphaAnimation(0, 1);
        fadeIn.setInterpolator(new AccelerateInterpolator()); //add this
        fadeIn.setDuration(600);

        AnimationSet animation = new AnimationSet(false); //change to false
        animation.addAnimation(fadeIn);
        b.setAnimation(animation);
        b.startAnimation(animation);
        b.setVisibility(View.VISIBLE);

    }
}
