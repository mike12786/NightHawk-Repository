package com.zapporoo.nighthawk.ui.fragments;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.parse.ParseFile;
import com.yelp.clientlib.entities.SearchResponse;
import com.zapporoo.nighthawk.R;
import com.zapporoo.nighthawk.adapters.AdapterPersonalBusiness;
import com.zapporoo.nighthawk.adapters.AdapterPersonalBusinessSearch;
import com.zapporoo.nighthawk.callbacks.CallbackImageDefault;
import com.zapporoo.nighthawk.callbacks.ICallbackImage;
import com.zapporoo.nighthawk.callbacks.ICallbackPersonalBusinessItem;
import com.zapporoo.nighthawk.callbacks.IPersonalBusinessActivity;
import com.zapporoo.nighthawk.model.ModUser;
import com.zapporoo.nighthawk.ui.activities.Activity_Personal_Business_Details;
import com.zapporoo.nighthawk.ui.views.CircularImageView;
import com.zapporoo.nighthawk.ui.views.SpacesItemDecoration;
import com.zapporoo.nighthawk.util.PictureDownloaderTask;
import com.zapporoo.nighthawk.util.UtilImage;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Pile on 12/15/2015.
 */
public class Fragment_Personal_HotDeals extends FragmentDefault implements ICallbackPersonalBusinessItem {
    private RecyclerView rvBusiness;
    private AdapterPersonalBusiness mAdapterBusiness;
    private List<ModUser> mItems;

    private IPersonalBusinessActivity mPersonalHomeBusinessActivity;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        try
        {
            mPersonalHomeBusinessActivity = (IPersonalBusinessActivity) activity;
        }catch (ClassCastException e) {
            throw new IllegalStateException("This fragment must be attached to IPersonalBusinessActivity!");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();

        mPersonalHomeBusinessActivity = null;
    }

    /**
     * The fragment argument representing the section number for this
     * fragment.
     */
    private static final String ARG_SECTION_NUMBER = "section_number";

    public Fragment_Personal_HotDeals() {
    }

    /**
     * Returns a new instance of this fragment for the given section
     * number.
     */
    public static Fragment_Personal_HotDeals newInstance(int sectionNumber) {
        Fragment_Personal_HotDeals fragment = new Fragment_Personal_HotDeals();
        Bundle args = new Bundle();
        args.putInt(ARG_SECTION_NUMBER, sectionNumber);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_personal_hot_deals, container, false);


        //mItems = Test.createDummyPersonalBusiness();
        mItems = ModUser.getAllBusinessLocalCache();
        mAdapterBusiness = new AdapterPersonalBusiness(getItems(), this);

        rvBusiness = (RecyclerView) rootView.findViewById(R.id.rvBusiness);
        rvBusiness.setLayoutManager(new LinearLayoutManager(rootView.getContext(), LinearLayoutManager.VERTICAL, false));
        rvBusiness.addItemDecoration(new SpacesItemDecoration(1));
        rvBusiness.setAdapter(mAdapterBusiness);

        return rootView;
    }

    private List<ModUser> getItems() {
        if (mItems == null)
            mItems = new ArrayList<>();

        return removeNotContainingDeals(mItems);
    }

    private List<ModUser> removeNotContainingDeals(List<ModUser> mItems) {
        List<ModUser> ret = new ArrayList<>();

        for (ModUser business:mItems)
            if (business.getHotDeals() != null && !business.getHotDeals().isEmpty())
                ret.add(business);

        return ret;
    }

    @Override
    public void onLoadBusiness(RecyclerView.ViewHolder pHotDealViewHolder, ModUser pBusinessData) {
        // TODO PILE populate the view holder and load the image here.
        AdapterPersonalBusiness.ViewHolderHotDeal vHolder = (AdapterPersonalBusiness.ViewHolderHotDeal) pHotDealViewHolder;
        vHolder.tvBusinessName.setText(pBusinessData.getBusinessName());
        vHolder.tvBusinessRate.setText(pBusinessData.getRatingString());
        vHolder.tvHotDealsBody.setText(pBusinessData.getHotDeals());
        vHolder.tvBusinessTitle.setText(pBusinessData.getShortDescription());  // ??

        if(pBusinessData.isGeneratedFromYelp()) {
            ICallbackImage imgCallback = new CallbackYelpBusinessImage(vHolder);
            PictureDownloaderTask downloaderTask = new PictureDownloaderTask(vHolder.getAdapterPosition(), imgCallback, UtilImage.generateYelpBusinessImageFileName(pBusinessData.getYelpId()));
            downloaderTask.execute(pBusinessData.getYelpImageUrl());
        }else
        {
/*            CallbackImageDefault cb = new CallbackImageDefault(vHolder.imgBusinessParse);
            pBusinessData.getProfileImage(cb);*/
        }
    }

    @Override
    public void onBusinessOpen(ModUser pBusinessData) {
        // TODO pass business data
        mPersonalHomeBusinessActivity.showBusinessDetails(pBusinessData);
    }

    @Override
    public void onFavoriteBusiness(ModUser pBusinessData) {

    }

    @Override
    public void onCheckBoxClick(ModUser pBusinessData) {

    }

    @Override
    public int getCurrentSearchId() {
        // we don't have search feature in this fragment.
        return 0;
    }

    @Override
    public void onSearchResults(SearchResponse pSearchResults) {

    }

    private class CallbackYelpBusinessImage implements ICallbackImage {
        private final AdapterPersonalBusiness.ViewHolderHotDeal mViewHolder;

        public CallbackYelpBusinessImage(AdapterPersonalBusiness.ViewHolderHotDeal pViewHolder) {
            mViewHolder = pViewHolder;
        }

        @Override
        public void imageFetched(Bitmap bitmap, int pRequestId) {
            if(mViewHolder != null && mViewHolder.getAdapterPosition() == pRequestId) {
                mViewHolder.imgHotDeal.setImageBitmap(bitmap);
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
            return Fragment_Personal_HotDeals.this.isAdded();
        }

        @Override
        public void setFileForCancel(ParseFile file) {

        }

        @Override
        public void setImageView(CircularImageView iv) {
        }

        @Override
        public void setImageView(ImageView iv) {
        }

        @Override
        public Context getContext() {
            return Fragment_Personal_HotDeals.this.getActivity();
        }

        @Override
        public int getDesiredPictureWidth() {
            int desiredPictureWidth;

            if(mViewHolder != null && mViewHolder.imgHotDeal != null) {
                if(mViewHolder.imgHotDeal.getWidth() == 0) {
                    desiredPictureWidth = 0;
                }else
                {
                    desiredPictureWidth = mViewHolder.imgHotDeal.getWidth();
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

            if(mViewHolder != null && mViewHolder.imgHotDeal != null) {
                if(mViewHolder.imgHotDeal.getHeight() == 0) {
                    desiredPictureHeight = 0;
                }else
                {
                    desiredPictureHeight = mViewHolder.imgHotDeal.getHeight();
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