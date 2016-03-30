package com.zapporoo.nighthawk.callbacks;

import com.zapporoo.nighthawk.model.ModRating;
import com.zapporoo.nighthawk.model.ModUser;
import com.zapporoo.nighthawk.ui.fragments.Fragment_Personal_Business_Details;

/**
 * Created by dare on 15.1.16..
 */
public interface IPersonalBusinessDetailsActivity {
    void showCheckInForm(ModUser mBusinessData);

    void onBusinessRated(ModRating rating);

    void showReviewEmptyAlert();

    void showReviewTooLongAlert();

    void showRateDlg(ModUser pBusinessData);

    ModUser getBusinessForDisplay();

    void checkSignInRequest();

    void setFragment(Fragment_Personal_Business_Details pFragmentDetails);
}
