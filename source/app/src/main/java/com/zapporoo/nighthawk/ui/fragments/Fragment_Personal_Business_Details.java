package com.zapporoo.nighthawk.ui.fragments;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseGeoPoint;
import com.yelp.clientlib.entities.Review;
import com.zapporoo.nighthawk.R;
import com.zapporoo.nighthawk.adapters.AdapterPersonalBusiness;
import com.zapporoo.nighthawk.adapters.AdapterPersonalBusinessReviews;
import com.zapporoo.nighthawk.adapters.ReviewViewHolder;
import com.zapporoo.nighthawk.callbacks.ICallbackBusinessDetails;
import com.zapporoo.nighthawk.callbacks.ICallbackImage;
import com.zapporoo.nighthawk.callbacks.IPersonalBusinessDetailsActivity;
import com.zapporoo.nighthawk.model.ModRating;
import com.zapporoo.nighthawk.model.ModUser;
import com.zapporoo.nighthawk.ui.views.CircularImageView;
import com.zapporoo.nighthawk.ui.views.SpacesItemDecoration;
import com.zapporoo.nighthawk.util.ParseExceptionHandler;
import com.zapporoo.nighthawk.util.PictureDownloaderTask;
import com.zapporoo.nighthawk.util.UtilImage;

import java.util.List;

/**
 * Created by Pile on 12/15/2015.
 */
public class Fragment_Personal_Business_Details extends FragmentDefault implements ICallbackBusinessDetails {
    private RecyclerView rvBusiness;
    private AdapterPersonalBusinessReviews mAdapterBusiness;
    private List<ModRating> mReviews;
    private ModUser mBusiness;

    private IPersonalBusinessDetailsActivity mPersonalHomeBusinessDetailsActivity;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try
        {
            mPersonalHomeBusinessDetailsActivity = (IPersonalBusinessDetailsActivity) activity;
            mPersonalHomeBusinessDetailsActivity.setFragment(this);
        }catch (ClassCastException e) {
            throw new IllegalStateException("This fragment must be attached to IPersonalBusinessDetailsActivity!");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();

        mPersonalHomeBusinessDetailsActivity = null;
    }

    /**
     * The fragment argument representing the section number for this
     * fragment.
     */
    private static final String ARG_SECTION_NUMBER = "section_number";

    public Fragment_Personal_Business_Details() {
    }

    /**
     * Returns a new instance of this fragment for the given section
     * number.
     */
    public static Fragment_Personal_Business_Details newInstance(int sectionNumber) {
        Fragment_Personal_Business_Details fragment = new Fragment_Personal_Business_Details();
        Bundle args = new Bundle();
        args.putInt(ARG_SECTION_NUMBER, sectionNumber);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_personal_business_details, container, false);
        mBusiness = mPersonalHomeBusinessDetailsActivity.getBusinessForDisplay();

        mAdapterBusiness = new AdapterPersonalBusinessReviews(mReviews, mBusiness, this);

        rvBusiness = (RecyclerView) rootView.findViewById(R.id.rvBusiness);
        rvBusiness.setLayoutManager(new LinearLayoutManager(rootView.getContext(), LinearLayoutManager.VERTICAL, false));
        rvBusiness.addItemDecoration(new SpacesItemDecoration(1));
        rvBusiness.setAdapter(mAdapterBusiness);

        if (mBusiness.isGeneratedFromYelp()){
            ModRating rating = new ModRating();
            rating.setComment(mBusiness.getYelpComment());
            reloadAdapterLocal(rating);
            //checkSignInRequest();
        }else
            startReviewQuery();

        return rootView;
    }

    @Override
    public void onResume() {
        if (mBusiness.isGeneratedFromYelp())
            checkSignInRequest();

        super.onResume();
    }

    private void startReviewQuery() {
        showProgressDialog("Fetching Ratings...");
        ModRating.queryAllRatingsOfBusiness(mBusiness).findInBackground(new FindCallback<ModRating>() {
            @Override
            public void done(List<ModRating> objects, ParseException e) {
                dismissProgressDialog();
                if (e == null) {
                    reloadAdapter(objects);
                    checkSignInRequest();
                } else
                    ParseExceptionHandler.handleParseError(e, getActivity());
            }
        });
    }

    private void checkSignInRequest() {
        mPersonalHomeBusinessDetailsActivity.checkSignInRequest();
    }

    public void reloadAdapter(List<ModRating> objects){
        mAdapterBusiness.updateList(objects);
    }

    @Override
    public void onLoadBusinessHeader(AdapterPersonalBusinessReviews.HeaderViewHolder pHeaderViewHolder, final ModUser pBusinessData) {
        pHeaderViewHolder.tvBusinessName.setText(pBusinessData.getBusinessName());
        pHeaderViewHolder.tvBusinessRate.setText(pBusinessData.getRatingString());
        pHeaderViewHolder.tvBusinessDetailsShortDescription.setText(pBusinessData.getShortDescription());
        pHeaderViewHolder.tvHotDealsBody.setText(pBusinessData.getHotDeals());

        pHeaderViewHolder.tvBusinessAddress.setText(pBusinessData.getAddress());
        pHeaderViewHolder.tvBusinessPhone.setText(pBusinessData.getContactNumber());


        pHeaderViewHolder.llLocationWrap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startNavigation(pBusinessData);
            }
        });

        pHeaderViewHolder.llContactWrap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                callNumber(pBusinessData);
            }
        });

        if(pBusinessData.isGeneratedFromYelp()) {
            pHeaderViewHolder.btnWriteReview.setVisibility(View.INVISIBLE);
            pHeaderViewHolder.imgBusiness.setVisibility(View.GONE);
            ICallbackImage imgCallback = new CallbackYelpBusinessImage();
            imgCallback.setImageView(pHeaderViewHolder.imgHotDeal);
            PictureDownloaderTask downloaderTask = new PictureDownloaderTask(0, imgCallback, UtilImage.generateYelpBusinessImageFileName(pBusinessData.getYelpId()));
            downloaderTask.execute(pBusinessData.getYelpImageUrl());

            pHeaderViewHolder.tvBusinessWebsite.setText("WEB SITE");
            pHeaderViewHolder.tvHotDealsTitle.setText("Deals");

            //pBusinessData.getWebSite());
        }else{
            fetchProfileImage(pBusinessData, pHeaderViewHolder.imgBusiness);

            pHeaderViewHolder.tvBusinessWebsite.setText(pBusinessData.getWebSite());
            pHeaderViewHolder.tvHotDealsTitle.setText(pBusinessData.getHotDealsTitle());
        }
    }

    private void callNumber(ModUser user) {
        String uri = "tel:" + user.getContactNumber().trim() ;
        Intent intent = new Intent(Intent.ACTION_CALL);
        intent.setData(Uri.parse(uri));
        startActivity(intent);
    }

    private void startNavigation(ModUser user){

    }

    @Override
    public void onLoadBusinessReview(ReviewViewHolder pReviewViewHolder, ModRating pReviewData) {
        String rating = "";
        if (pReviewData.getFrom() != null)
            rating = pReviewData.getFrom().getPersonalName() + ", " + pReviewData.getValue();

        pReviewViewHolder.tvBusinessReviewTitle.setText(rating);
        pReviewViewHolder.tvBusinessReviewBody.setText(pReviewData.getComment());
    }

    @Override
    public void onWriteReview(ModUser pBusinessData) {
        mPersonalHomeBusinessDetailsActivity.showRateDlg(pBusinessData);
    }

    @Override
    public void onOpenBusinessWebsite(ModUser businessData) {
        String url = businessData.getWebSite().trim();

        if (!url.startsWith("http://") && !url.startsWith("https://")) url = "http://" + url;

        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(Uri.parse(url));
        startActivity(intent);
    }

    @Override
    public void onNavigationShowAddress(ModUser businessData) {
        ParseGeoPoint geoPoint = businessData.getGeopoint();

        double destinationLatitude = geoPoint.getLatitude();
        double destinationLongitude = geoPoint.getLongitude();

        String url = "http://maps.google.com/maps?f=d&daddr="+ destinationLatitude+","+destinationLongitude+"&dirflg=d&layer=t";
        Intent intent = new Intent(android.content.Intent.ACTION_VIEW, Uri.parse(url));
        intent.setClassName("com.google.android.apps.maps", "com.google.android.maps.MapsActivity");
        startActivity(intent);
    }

    public void reloadAdapterLocal(ModRating rating) {
        mAdapterBusiness.addSingle(rating);
    }

    private class CallbackYelpBusinessImage implements ICallbackImage {
        private ImageView mImageView;

        @Override
        public void imageFetched(Bitmap bitmap, int pRequestId) {
            if(mImageView != null) {
                mImageView.setImageBitmap(bitmap);
            }
        }

        @Override
        public void imageFetchedError(String message) {

        }

        @Override
        public void setActive(boolean isActive) {
            // not needed here.
        }

        @Override
        public boolean isActive() {
            return Fragment_Personal_Business_Details.this.isAdded();
        }

        @Override
        public void setFileForCancel(ParseFile file) {

        }

        @Override
        public void setImageView(CircularImageView iv) {
            mImageView = iv;
        }

        @Override
        public void setImageView(ImageView iv) {
            mImageView = iv;
        }

        @Override
        public Context getContext() {
            return Fragment_Personal_Business_Details.this.getActivity();
        }

        @Override
        public int getDesiredPictureWidth() {
            int desiredPictureWidth;

            if(mImageView != null) {
                if(mImageView.getWidth() == 0) {
                    desiredPictureWidth = 0;
                }else
                {
                    desiredPictureWidth = mImageView.getWidth();
                }
            }else
            {
                desiredPictureWidth = 0;
            }

            return desiredPictureWidth;
        }

        @Override
        public int getDesiredPictureHeight() {
            int desiredPictureHeight;

            if(mImageView != null) {
                if(mImageView.getHeight() == 0) {
                    desiredPictureHeight = 0;
                }else
                {
                    desiredPictureHeight = mImageView.getHeight();
                }
            }else
            {
                desiredPictureHeight = 0;
            }

            return desiredPictureHeight;
        }

        @Override
        public int getFixedDimenType() {
            return 1;
        }

        @Override
        public int getDefaultImageResId() {
            return R.drawable.ic_image_grey;
        }
    }
}