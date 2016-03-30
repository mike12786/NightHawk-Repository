package com.zapporoo.nighthawk.ui.fragments;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatDialogFragment;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.ViewTreeObserver;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.RatingBar;

import com.zapporoo.nighthawk.R;
import com.zapporoo.nighthawk.callbacks.IPersonalBusinessDetailsActivity;
import com.zapporoo.nighthawk.model.ModRating;
import com.zapporoo.nighthawk.model.ModUser;

/**
 * 
 */
public class Fragment_Personal_Business_Rate extends AppCompatDialogFragment
{
	private EditText etReview;
    private RatingBar rbRating;
	private IPersonalBusinessDetailsActivity businessDetailsHome;
    private ModUser businessForReview;
	public static final String TAG = "Fragment_Personal_Business_Rate";

	protected static final String EXTRA_LAYOUT_RES_ID = "LAYOUT_RES_ID";

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		try {
			businessDetailsHome = (IPersonalBusinessDetailsActivity) activity;
		} catch (ClassCastException e) {
			throw new ClassCastException(
					"Activity must implement main activity interface.");
		}
	}

	@Override
	public void onDetach() {
		super.onDetach();

		businessDetailsHome = null;
	}

	public static Fragment_Personal_Business_Rate create(ModUser pBusinessData, FragmentManager pFragmentManager) {
		Fragment_Personal_Business_Rate rateDlg = new Fragment_Personal_Business_Rate();

		rateDlg.show(pFragmentManager, TAG);
        rateDlg.setBusiness(pBusinessData);
        return rateDlg;
	}

    private void setBusiness(ModUser pBusinessData) {
        this.businessForReview = pBusinessData;
    }

    @Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.fragment_personal_business_rate, null);

		etReview = (EditText) rootView.findViewById(R.id.etBusinessReview);
        rbRating = (RatingBar) rootView.findViewById(R.id.rbBusinessRate);
        rbRating.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
                Log.e("RATING", "onRatingChanged" + rating);
            }
        });

		Button btnSubmit = (Button) rootView.findViewById(R.id.btnBusinessRateSubmit);
		btnSubmit.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if(TextUtils.isEmpty(etReview.getText().toString().trim()))
				{
					businessDetailsHome.showReviewEmptyAlert();
				}else if(etReview.getText().toString().length() > 4096) { // TODO replace if this is necessary to check
					businessDetailsHome.showReviewTooLongAlert();
				}else {
                    hideKeyboard();
                    startRatingSaveToServer();
					dismiss();
				}
			}
		});

		ViewTreeObserver vto = rootView.getViewTreeObserver();
		vto.addOnGlobalLayoutListener(new LayoutMeasuredListener((ViewGroup) rootView));

		return rootView;
	}

    private void hideKeyboard() {
        if(getDialog().getCurrentFocus()!=null) {
            InputMethodManager inputMethodManager = (InputMethodManager) getActivity().getSystemService(getActivity().INPUT_METHOD_SERVICE);
            inputMethodManager.hideSoftInputFromWindow(getDialog().getCurrentFocus().getWindowToken(), 0);
        }
    }

    private void startRatingSaveToServer() {
        ModRating rating = new ModRating();
        rating.setFrom(ModUser.getModUser());
        rating.setTo(businessForReview);
        rating.setValue((int) rbRating.getRating());
        rating.setComment(etReview.getText().toString());
        businessDetailsHome.onBusinessRated(rating);
    }

    @Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		Dialog dialog = new Dialog(getActivity());

		// request a window without the title
		dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
		return dialog;
	}

	private class LayoutMeasuredListener implements OnGlobalLayoutListener
	{
		private final ViewGroup layout;

		public LayoutMeasuredListener(ViewGroup vg) {
			this.layout = vg;
		}

		@Override
		@TargetApi(Build.VERSION_CODES.JELLY_BEAN)
		public void onGlobalLayout() {
			if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN) {
				layout.getViewTreeObserver().removeGlobalOnLayoutListener(this);
			} else {
				layout.getViewTreeObserver().removeOnGlobalLayoutListener(this);
			}

			WindowManager wm = (WindowManager) layout.getContext().getSystemService(Context.WINDOW_SERVICE);

			DisplayMetrics metrics = new DisplayMetrics();
			wm.getDefaultDisplay().getMetrics(metrics);

//			final int height = (int) (metrics.heightPixels * 0.75);
			final int width = (int ) (metrics.widthPixels * 0.75);

			layout.setLayoutParams(new FrameLayout.LayoutParams(width, LayoutParams.WRAP_CONTENT));
		}
	}
}
