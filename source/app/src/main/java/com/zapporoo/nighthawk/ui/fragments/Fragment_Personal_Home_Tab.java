package com.zapporoo.nighthawk.ui.fragments;

import android.app.Activity;

import com.zapporoo.nighthawk.callbacks.IPersonalHomeActivity;
import com.zapporoo.nighthawk.quickblox.dialogs.Fragment_Dialogs_default;

/**
 * Created by dare on 12.1.16..
 */
public abstract class Fragment_Personal_Home_Tab extends Fragment_Dialogs_default {
    private IPersonalHomeActivity mPersonalHomeActivity;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        try
        {
            mPersonalHomeActivity = (IPersonalHomeActivity) activity;
        }catch (ClassCastException e) {
            throw new IllegalStateException("This fragment must be attached to IPersonalHomeActivity!");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();

        mPersonalHomeActivity = null;
    }

    protected IPersonalHomeActivity getPersonalHomeActivity() {
        return mPersonalHomeActivity;
    }


    public boolean handleBackPress() {
        return false;
    }
}
