package com.zapporoo.nighthawk.ui.fragments;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.zapporoo.nighthawk.R;
import com.zapporoo.nighthawk.adapters.AdapterBusinessHomeReviews;
import com.zapporoo.nighthawk.adapters.ReviewViewHolder;
import com.zapporoo.nighthawk.callbacks.CallbackImageDefault;
import com.zapporoo.nighthawk.callbacks.ICallbackBusinessHome;
import com.zapporoo.nighthawk.model.ModRating;
import com.zapporoo.nighthawk.model.ModUser;
import com.zapporoo.nighthawk.ui.views.SpacesItemDecoration;
import com.zapporoo.nighthawk.util.ParseExceptionHandler;

/**
 * Created by Pile on 12/15/2015.
 */
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import java.util.List;

public class Fragment_Business_Home extends FragmentDefault implements ICallbackBusinessHome {
    private ImageView ivBusinessProfile;

    private RecyclerView rvBusiness;
    TextView tvBusinessName, tvBusinessRating;
    RatingBar rbBusinessRate;

    private AdapterBusinessHomeReviews mAdapterBusiness;
    private List<ModRating> mReviews;
    private ModUser mBusiness;
    private static final String ARG_SECTION_NUMBER = "section_number";

    public Fragment_Business_Home() {
    }

    /**
     * Returns a new instance of this fragment for the given section
     * number.
     */
    public static Fragment_Business_Home newInstance(int sectionNumber) {
        Fragment_Business_Home fragment = new Fragment_Business_Home();
        Bundle args = new Bundle();
        args.putInt(ARG_SECTION_NUMBER, sectionNumber);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_business_home, container, false);

        tvBusinessName = (TextView) rootView.findViewById(R.id.tvProfileName);
        tvBusinessRating = (TextView) rootView.findViewById(R.id.tvBusinessRating);
        ivBusinessProfile = (ImageView) rootView.findViewById(R.id.ivBusinessProfile);
        rbBusinessRate = (RatingBar) rootView.findViewById(R.id.rbBusinessRating);

        mAdapterBusiness = new AdapterBusinessHomeReviews(mReviews, ModUser.getModUser(), this);

        rvBusiness = (RecyclerView) rootView.findViewById(R.id.rvRatingBusiness);
        rvBusiness.setLayoutManager(new LinearLayoutManager(rootView.getContext(), LinearLayoutManager.VERTICAL, false));
        rvBusiness.addItemDecoration(new SpacesItemDecoration(1));
        rvBusiness.setAdapter(mAdapterBusiness);

        mBusiness = ModUser.getModUser();
        fillBusinessDetails();
        startReviewQuery();
        return rootView;
    }

    private void fillBusinessDetails() {
        tvBusinessName.setText(mBusiness.getBusinessName());
        tvBusinessRating.setText(mBusiness.getRatingString());
        rbBusinessRate.setRating(mBusiness.getRatingValue());
        mBusiness.getProfileImage(new CallbackImageDefault(ivBusinessProfile));
    }

    private void startReviewQuery() {
        showProgressDialog("Fetching Ratings (change this for spinner in list)");
        ModRating.queryAllRatingsOfBusiness(mBusiness).findInBackground(new FindCallback<ModRating>() {
            @Override
            public void done(List<ModRating> objects, ParseException e) {
                dismissProgressDialog();
                if (e == null) {
                    reloadAdapter(objects);
                } else
                    ParseExceptionHandler.handleParseError(e, getActivity());
            }
        });
    }

    public void reloadAdapter(List<ModRating> objects){
        mAdapterBusiness.updateList(objects);
    }


    @Override
    public void onLoadBusinessHeader(AdapterBusinessHomeReviews.HeaderViewHolder pHeaderViewHolder, ModUser pBusinessData) {
        tvBusinessName.setText(pBusinessData.getBusinessName());
        tvBusinessRating.setText(pBusinessData.getRatingString());
        //pHeaderViewHolder.tvBusinessDetailsShortDescription.setText(pBusinessData.getShortDescription());

       // pHeaderViewHolder.tvHotDealsTitle.setText(pBusinessData.getHotDealsTitle());
       // pHeaderViewHolder.tvHotDealsBody.setText(pBusinessData.getHotDeals());

       // pHeaderViewHolder.tvBusinessAddress.setText(pBusinessData.getAddress());
       // pHeaderViewHolder.tvBusinessPhone.setText(pBusinessData.getContactNumber());
       // pHeaderViewHolder.tvBusinessWebsite.setText(pBusinessData.getWebSite());

        fetchProfileImage(pBusinessData, ivBusinessProfile);
    }

    @Override
    public void onLoadBusinessReview(ReviewViewHolder pReviewViewHolder, ModRating pReviewData) {
        pReviewViewHolder.tvBusinessReviewTitle.setText(pReviewData.getFrom().getPersonalName() + ", " + pReviewData.getValue());
        pReviewViewHolder.tvBusinessReviewBody.setText(pReviewData.getComment());
    }

    @Override
    public void onWriteReview(ModUser pBusinessData) {

    }
}