package com.zapporoo.nighthawk.ui.fragments;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.orangegangsters.github.swipyrefreshlayout.library.SwipyRefreshLayout;
import com.orangegangsters.github.swipyrefreshlayout.library.SwipyRefreshLayoutDirection;
import com.parse.DeleteCallback;
import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseGeoPoint;
import com.parse.ParseQuery;
import com.parse.SaveCallback;
import com.yelp.clientlib.entities.SearchResponse;
import com.zapporoo.nighthawk.R;
import com.zapporoo.nighthawk.adapters.AdapterPersonalBusinessSearch;
import com.zapporoo.nighthawk.callbacks.CallbackImageDefault;
import com.zapporoo.nighthawk.callbacks.ICallbackImage;
import com.zapporoo.nighthawk.callbacks.ICallbackPersonalBusinessItem;
import com.zapporoo.nighthawk.model.ModCheckIn;
import com.zapporoo.nighthawk.model.ModMyClub;
import com.zapporoo.nighthawk.model.ModUser;
import com.zapporoo.nighthawk.ui.activities.Activity_Personal_Business_Default;
import com.zapporoo.nighthawk.ui.activities.Activity_Personal_Business_Details;
import com.zapporoo.nighthawk.ui.activities.Activity_Results_Map;
import com.zapporoo.nighthawk.ui.views.CircularImageView;
import com.zapporoo.nighthawk.util.NhLog;
import com.zapporoo.nighthawk.util.ParseExceptionHandler;
import com.zapporoo.nighthawk.util.PictureDownloaderTask;
import com.zapporoo.nighthawk.util.UtilImage;
import com.zapporoo.nighthawk.util.UtilSearch;
import com.zapporoo.nighthawk.util.XxUtil;

import java.util.ArrayList;
import java.util.List;

import retrofit.Call;
import android.provider.Settings;

/**
 * Created by Pile on 12/15/2015.
 */

public class Fragment_Personal_Home_Search extends Fragment_Personal_Home_Tab implements ICallbackPersonalBusinessItem, View.OnClickListener, TextWatcher, SwipyRefreshLayout.OnRefreshListener {
    /**
     * The fragment argument representing the section number for this
     * fragment.
     */
    private static final String ARG_SECTION_NUMBER = "section_number";

    private static final int mTopMenuTitles[] = { R.string.tab_personal_hot_deals, R.string.tab_personal_my_group, R.string.tab_personal_check_in };
    private static final int mTopMenuIcons[] = {R.drawable.ic_tab_business_hot_deals, R.drawable.ic_tab_personal_profile, R.drawable.ic_tab_personal_myclubs};
    private static final String TAG = "Fragment_Personal_Home_Search";
    private static final int RC_GPS = 232;
    private View mTopMenuOptions [];
    private static final int numOfTopMenuItems = mTopMenuTitles.length;

    private RecyclerView rvHotDeals;
    private AdapterPersonalBusinessSearch mAdapterBusiness;
    private SwipyRefreshLayout mSwipyRefreshLayout;

    private int mCurrentSearchId;
    public Call<SearchResponse> mYelpSearchTask;
    private ImageView ivToggleGps;

    ParseQuery<ModUser> query;
    private int TIME_COUNTDOWN = 1000;
    private Handler mHandler;
    private Runnable mRunnableSearch;
    private View pbSearch;
    private final int CHARACTER_SEARCH_MINIMUM = 1;
    EditText etSearch;
    EditText etSearchLocation;
    ImageButton btnShowResultsMap;
    ParseGeoPoint pointTest;

    public Fragment_Personal_Home_Search() {
    }

    /**
     * Returns a new instance of this fragment for the given section
     * number.
     */
    public static Fragment_Personal_Home_Search newInstance(int sectionNumber) {
        Fragment_Personal_Home_Search fragment = new Fragment_Personal_Home_Search();
        Bundle args = new Bundle();
        args.putInt(ARG_SECTION_NUMBER, sectionNumber);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_personal_home_search, container, false);
        setHasOptionsMenu(true);

        mTopMenuOptions = new View[numOfTopMenuItems];

        mTopMenuOptions[0] = rootView.findViewById(R.id.incHotDeals);
        mTopMenuOptions[1] = rootView.findViewById(R.id.incMyGroup);
        mTopMenuOptions[2] = rootView.findViewById(R.id.incCheckIn);

        for(int i= 0; i < numOfTopMenuItems; ++i)
        {
            setTopMenuItem(mTopMenuOptions[i], i);
        }
        selectTopMenuOption(mTopMenuOptions[0], true);

//        List<ModHotDeal> items = Test.createDummyPersonalBusiness();

        mSwipyRefreshLayout = (SwipyRefreshLayout) rootView.findViewById(R.id.srSearch);
        mSwipyRefreshLayout.setOnRefreshListener(this);
        mSwipyRefreshLayout.setDirection(SwipyRefreshLayoutDirection.TOP);

        rvHotDeals = (RecyclerView) rootView.findViewById(R.id.rvBusiness);
        rvHotDeals.setLayoutManager(new LinearLayoutManager(rootView.getContext(), LinearLayoutManager.VERTICAL, false));
        //

        refreshListAdapter(new ArrayList<ModUser>());

        etSearch = (EditText) rootView.findViewById(R.id.etSearch);
        //etSearch.addTextChangedListener(this);
        pbSearch = rootView.findViewById(R.id.pbSearch);


        TextView.OnEditorActionListener editorActionListener = new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                boolean handled = false;
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    buttonSearch();
                    handled = true;
                    XxUtil.focusRemove(v, getActivity());
                }
                return handled;
            }
        };

        etSearch.setOnEditorActionListener(editorActionListener);

        etSearchLocation = (EditText)rootView.findViewById(R.id.etSearchLocation);
        etSearchLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isGpsWanted = false;
                gpsWantedCheck();
            }
        });
        etSearchLocation.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (v.getId() == R.id.etSearchLocation && hasFocus){
                    isGpsWanted = false;
                    gpsWantedCheck();
                }
            }
        });
        etSearchLocation.setOnEditorActionListener(editorActionListener);

        ivToggleGps = (ImageView) rootView.findViewById(R.id.ivToggleGps);
        ivToggleGps.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isGpsWanted = !isGpsWanted;
                if (isGpsWanted){
                    etSearchLocation.setText("");
                    etSearch.requestFocus();
                }

                gpsWantedCheck();
            }
        });

        startInitialParseQueries();

        Button btnTestSearch = (Button) rootView.findViewById(R.id.btnTestSearch);
        btnTestSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //testSearch();
                buttonSearch();
                XxUtil.focusRemove(etSearch, getActivity());
                XxUtil.focusRemove(etSearchLocation, getActivity());
            }


        });

        btnShowResultsMap = (ImageButton) rootView.findViewById(R.id.btnShowResultsMap);
        btnShowResultsMap.setOnClickListener(this);
        btnShowResultsMap.setEnabled(false);
        isGpsWanted = true;
        return rootView;
    }

    private void buttonSearch() {
        stopCurrentSearch();
        initiateSearch(etSearch.getText().toString().trim(), false);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_GPS){
            dismissProgressDialog();
        }
    }

    private void testSearch() {
        showProgressBarSearch(true);
        isParseSearchComplete = false;
        isYelpSearchComplete = false;
        String location_text = etSearchLocation.getText().toString();

        //if (!additionalPage){
        mAdapterBusiness.clearItems();
        stopCurrentSearch();
        //query = ModUser.queryBusinessIncludingInName(search_txt);
        //executeParseSearchQuery(query);
        mCurrentSearchId++;

        //UtilSearch.yelpSearch(search_txt, mAdapterBusiness);
        ModUser.cacheReleaseAll();

        pointTest = new ParseGeoPoint(33.777219, -118.161115);
        query = ModUser.queryAllBusinessInRange(pointTest, 2*1.6);
        mYelpSearchTask = UtilSearch.yelpSearchGPS(this, "hamburger", mAdapterBusiness, false, location_text, pointTest, XxUtil.getSearchRadiusValue(getActivity()) );
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_activity_home_personal, menu);
    }

    public void setTopMenuItem(View pItem, int pPosition) {
        ImageView image = (ImageView) pItem.findViewById(R.id.imgTop);
        image.setImageDrawable(XxUtil.getDrawable(getContext(), mTopMenuIcons[pPosition]));

        TextView tvTitle = (TextView) pItem.findViewById(R.id.tvTitle);
        tvTitle.setText(getText(mTopMenuTitles[pPosition]));

        pItem.setOnClickListener(this);
    }

    private void selectTopMenuOption(View pItem, boolean select) {
/*        View vIndicator;

        if(select) {
            for (View v : mTopMenuOptions) {
                if (v != pItem) {
                    vIndicator = v.findViewById(R.id.vIndicator);
                    vIndicator.setVisibility(View.INVISIBLE);
                }
            }
        }

        vIndicator = pItem.findViewById(R.id.vIndicator);
        vIndicator.setVisibility(select ? View.VISIBLE : View.INVISIBLE);*/
    }

    @Override
    public void onLoadBusiness(RecyclerView.ViewHolder pHotDealViewHolder, final ModUser pBusinessData) {
        final AdapterPersonalBusinessSearch.ViewHolderHotDeal vHolder = (AdapterPersonalBusinessSearch.ViewHolderHotDeal) pHotDealViewHolder;
        vHolder.tvAddress.setText(pBusinessData.getAddress());
        vHolder.tvBusinessName.setText(pBusinessData.getBusinessName());
        vHolder.tvBody.setText(pBusinessData.getShortDescription());
        vHolder.tvTypeOfEstablishment.setText(pBusinessData.getEstablishmentType());
        vHolder.rbBusinessRating.setRating(pBusinessData.getRatingValue());

        if(pBusinessData.isGeneratedFromYelp()) {
            // this is downloaded from yelp service
            // TODO dare ubaci download slike i pusti callback po istom principu kao ispod.
            ICallbackImage imgCallback = new CallbackYelpBusinessImage(vHolder);
            vHolder.rlImgBusinessParse.setVisibility(View.GONE);
            vHolder.rlImgBusinessYelp.setVisibility(View.VISIBLE);
            vHolder.imgBusinessYelp.setScaleType(ImageView.ScaleType.CENTER_CROP);
            vHolder.imgHotDealPlaceholderParse.setVisibility(View.INVISIBLE);
            vHolder.imgHotDealPlaceholderYelp.setVisibility(View.VISIBLE);
            vHolder.imgBusinessYelp.setImageBitmap(BitmapFactory.decodeResource(vHolder.imgBusinessYelp.getContext().getResources(), R.drawable.ic_image_grey_square));
            PictureDownloaderTask downloaderTask = new PictureDownloaderTask(vHolder.getAdapterPosition(), imgCallback, UtilImage.generateYelpBusinessImageFileName(pBusinessData.getYelpId()));
            downloaderTask.execute(pBusinessData.getYelpImageUrl());
        }else{
            vHolder.rlImgBusinessYelp.setVisibility(View.GONE);
            vHolder.rlImgBusinessParse.setVisibility(View.VISIBLE);
            vHolder.imgBusinessParse.setScaleType(ImageView.ScaleType.CENTER_CROP);
            vHolder.imgHotDealPlaceholderParse.setVisibility(View.VISIBLE);
            vHolder.imgHotDealPlaceholderYelp.setVisibility(View.INVISIBLE);
            CallbackImageDefault cb = new CallbackImageDefault(vHolder.imgBusinessParse);
            pBusinessData.getProfileImage(cb);
        }

        ModCheckIn.getCheckInFromCacheQuery(pBusinessData).getFirstInBackground(new GetCallback<ModCheckIn>() {
            @Override
            public void done(ModCheckIn checkIn, ParseException e) {
                if (checkIn == null)
                    vHolder.cbCheck.setChecked(false);
                else
                    vHolder.cbCheck.setChecked(true);
            }
        });

        ModMyClub.getClubFromCacheQuery(pBusinessData).getFirstInBackground(new GetCallback<ModMyClub>() {
            @Override
            public void done(ModMyClub myClub, ParseException e) {
                if (myClub == null)
                    vHolder.imgBtnBusinessRate.setImageDrawable(getDrawableCheck(R.drawable.ic_favorite_inactive));
                else
                    vHolder.imgBtnBusinessRate.setImageDrawable(getDrawableCheck(R.drawable.ic_favorite_active));
            }
        });

    }

    @Override
    public void onRefresh(SwipyRefreshLayoutDirection direction) {
        switch (direction) {
            case TOP: {
                if(!TextUtils.isEmpty(etSearch.getText().toString()))
                    searchTextChanged(etSearch.getText().toString(), false);
                else
                    mSwipyRefreshLayout.setRefreshing(false);
                break;
            }
            case BOTTOM: {
                if(mAdapterBusiness.getItemCount() > 0 && !TextUtils.isEmpty(etSearch.getText().toString()))
                    searchTextChanged(etSearch.getText().toString(), true);
                else
                    mSwipyRefreshLayout.setRefreshing(false);
                break;
            }
        }
    }

    public void searchError(Throwable t) {
        dismissProgressDialogPrivate();
        dismissProgressDialog();
        showProgressBarSearch(false);
    }



    private class CallbackYelpBusinessImage implements ICallbackImage {
        private final AdapterPersonalBusinessSearch.ViewHolderHotDeal mViewHolder;

        public CallbackYelpBusinessImage(AdapterPersonalBusinessSearch.ViewHolderHotDeal pViewHolder) {
            mViewHolder = pViewHolder;
        }

        @Override
        public void imageFetched(Bitmap bitmap, int pRequestId) {
            if(mViewHolder != null && mViewHolder.getAdapterPosition() == pRequestId) {
                mViewHolder.imgBusinessYelp.setImageBitmap(bitmap);
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
            return Fragment_Personal_Home_Search.this.isAdded();
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
            return Fragment_Personal_Home_Search.this.getActivity();
        }

        @Override
        public int getDesiredPictureWidth() {
            int desiredPictureWidth;

            if(mViewHolder != null && mViewHolder.imgBusinessParse != null) {
                if(mViewHolder.imgBusinessParse.getWidth() == 0) {
                    desiredPictureWidth = 0;
                }else
                {
                    desiredPictureWidth = mViewHolder.imgBusinessParse.getWidth();
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

            if(mViewHolder != null && mViewHolder.imgBusinessParse != null) {
                if(mViewHolder.imgBusinessParse.getHeight() == 0) {
                    desiredPictureHeight = 0;
                }else
                {
                    desiredPictureHeight = mViewHolder.imgBusinessParse.getHeight();
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
            return R.drawable.ic_image_grey_square;
        }
    }


    @Override
    public void onBusinessOpen(ModUser pBusinessData) {
        Activity_Personal_Business_Default.mBusiness = pBusinessData;
        Intent intent = new Intent(getActivity(), Activity_Personal_Business_Details.class);
        intent.putExtra(ModUser.EXTRA_BUSINESS_ID, pBusinessData.getObjectId());
        startNextActivity(intent);
    }

    @Override
    public void onFavoriteBusiness(final ModUser pBusinessData) {
        final ModMyClub favorite = ModMyClub.getClubFromCache(pBusinessData);
        String text;
        View.OnClickListener listener;
        if (favorite == null){
            text = getString(R.string.confirm_add_favorite_club);
            listener = new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    favoritesAddNew(pBusinessData);
                }
            };
        }else{
            text = getString(R.string.confirm_remove_favorite_club);
            listener = new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    favoritesRemove(favorite);
                }
            };
        }
        showConfirmDialog(text, listener);

    }

    private void favoritesRemove(final ModMyClub myClub) {
        showProgressDialog(getString(R.string.progress_removing));
        myClub.deleteInBackground(new DeleteCallback() {
            @Override
            public void done(ParseException e) {
                dismissProgressDialog();

                if (e == null){
                    ModMyClub.removeSingleFromCache(myClub);
                    refreshListAdapter();
                }else
                    ParseExceptionHandler.handleParseError(e, getActivity());
            }
        });
    }

    private void favoritesAddNew(ModUser pBusinessData) {
        showProgressDialog(getString(R.string.progress_saving));

        final ModMyClub myclub = new ModMyClub();
        myclub.setFrom(ModUser.getCurrentUser());

        if (pBusinessData.isGeneratedFromYelp()) {
            myclub.setToYelp(pBusinessData);
            myclub.setRatingYelp(pBusinessData);
            myclub.setNameYelp(pBusinessData);
        }
        else
            myclub.setTo(pBusinessData);

        myclub.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                dismissProgressDialog();
                if (e == null){
                    ModMyClub.addSingle(myclub);
                    refreshListAdapter();
                }else{
                    ParseExceptionHandler.handleParseError(e, getActivity());
                }

            }
        });
    }

    @Override
    public void onCheckBoxClick(ModUser pBusinessData) {
        Activity_Personal_Business_Default.mBusiness = pBusinessData;
        Intent intent = new Intent(getActivity(), Activity_Personal_Business_Details.class);
        intent.putExtra(ModUser.EXTRA_BUSINESS_ID, pBusinessData.getObjectId());

        intent.putExtra(ModUser.EXTRA_SIGN_ME_IN, true);

        startNextActivity(intent);
        mAdapterBusiness.setAllowChecking(false);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.incHotDeals: {
                getPersonalHomeActivity().showHotDealsActivity();
                break;
            }
            case R.id.incCheckIn: {
                selectTopMenuOption(v, true);
                mAdapterBusiness.setAllowChecking(!mAdapterBusiness.isAllowChecking());
                break;
            }
            case R.id.incMyGroup: {
                getPersonalHomeActivity().showMyGroupActivity();
                break;
            }

            case R.id.btnShowResultsMap:{
                if (mLocationSearched == null)
                    showResultsMap(null);
                else
                    showResultsMap(new ParseGeoPoint(mLocationSearched.getLatitude(), mLocationSearched.getLongitude()));
                break;
            }
        }
    }

    private void showResultsMap(ParseGeoPoint locationSearched) {
        Intent intent = new Intent(getActivity(), Activity_Results_Map.class);
        Activity_Results_Map.businessList = mAdapterBusiness.getAllItems();

        Bundle bundle = new Bundle();

        if (locationSearched != null) {
            bundle.putDouble(Activity_Results_Map.P_LAT, locationSearched.getLatitude());
            bundle.putDouble(Activity_Results_Map.P_LON, locationSearched.getLongitude());
        }

        intent.putExtras(bundle);
        startNextActivity(intent);
    }


    private void startInitialParseQueries(){
        showProgressDialog(getString(R.string.progress_loading_data));
        startInitialCheckInParseQuery();
    }

    private void startInitialCheckInParseQuery(){
        ModCheckIn.queryAllWhereUserIsCheckedIn(ModUser.getModUser()).findInBackground(new FindCallback<ModCheckIn>() {
            @Override
            public void done(List<ModCheckIn> objects, ParseException e) {
                if (e == null){
                    ModCheckIn.saveLocally(objects);
                    startInitialMyClubsParseQuery();
                }else
                    ParseExceptionHandler.handleParseError(e, getActivity());
            }
        });
    }

    private void startInitialMyClubsParseQuery() {
        ModMyClub.queryAllUserFavoriteClubs(ModUser.getModUser()).findInBackground(new FindCallback<ModMyClub>() {
            @Override
            public void done(List<ModMyClub> objects, ParseException e) {
                if (e == null) {
                    ModMyClub.saveLocally(objects);
                    dismissProgressDialog();
                } else
                    ParseExceptionHandler.handleParseError(e, getActivity());
            }
        });
    }














    @Override
    public int getCurrentSearchId() {
        return mCurrentSearchId;
    }

    @Override
    public void onSearchResults(SearchResponse pSearchResults) {
        mSwipyRefreshLayout.setRefreshing(false);
    }

    boolean isParseSearchComplete = false;
    boolean isYelpSearchComplete = false;


    private void refreshListAdapter(List<ModUser> objects) {
        if (mAdapterBusiness == null){
            mAdapterBusiness = new AdapterPersonalBusinessSearch(objects, this, false);
            rvHotDeals.setAdapter(mAdapterBusiness);
        }
        else
            mAdapterBusiness.updateItems(objects);
    }

    private void addAndrefreshListAdapter(List<ModUser> objects) {
        if (mAdapterBusiness == null){
            mAdapterBusiness = new AdapterPersonalBusinessSearch(objects, this, false);
            rvHotDeals.setAdapter(mAdapterBusiness);
        }
        else
            mAdapterBusiness.addItems(objects);
    }

    public void refreshListAdapter() {
        if (mAdapterBusiness != null)
            mAdapterBusiness.notifyDataSetChanged();
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {

    }

    @Override
    public void afterTextChanged(Editable s) {
        searchTextChanged(s.toString(), false);
    }

    public void searchTextChanged(String text, boolean searchMore){
        if (text.trim().length() >= CHARACTER_SEARCH_MINIMUM){
            startSearchCountdown(text, searchMore);
        }else{
            stopCurrentSearch();
        }
    }

    private void showProgressBarSearch(boolean show) {
        if (show)
            pbSearch.setVisibility(View.VISIBLE);
        else
            pbSearch.setVisibility(View.GONE);
    }

    public void stopCurrentSearch() {

        if (mHandler != null)
            mHandler.removeCallbacks(mRunnableSearch);

        if (query != null){
            query.cancel();
        }

        if (mYelpSearchTask != null)
            mYelpSearchTask.cancel();

    }

    private void startSearchCountdown(final String text, final boolean searchMore) {

        if (mHandler == null) {
            mHandler = new Handler();
        } else {
            mHandler.removeCallbacks(mRunnableSearch);
        }

        mRunnableSearch = new Runnable() {

            @Override
            public void run() {
                initiateSearch(text.trim(), searchMore);
            }
        };
        mHandler.postDelayed(mRunnableSearch, TIME_COUNTDOWN);
    }

    protected void initiateSearch(String search_txt, boolean additionalPage) {
        showProgressBarSearch(true);
        btnShowResultsMap.setEnabled(false);
        isParseSearchComplete = false;
        isYelpSearchComplete = false;
        String location_text = etSearchLocation.getText().toString();

        //if (!additionalPage){
            mAdapterBusiness.clearItems();
            stopCurrentSearch();
            //query = ModUser.queryBusinessIncludingInName(search_txt);
            //executeParseSearchQuery(query);
            mCurrentSearchId++;

            //UtilSearch.yelpSearch(search_txt, mAdapterBusiness);
            ModUser.cacheReleaseAll();
        //}

        if (mLocation != null) {
            mLocationSearched = mLocation;
            ParseGeoPoint point = new ParseGeoPoint(mLocation.getLatitude(), mLocation.getLongitude());
            query = ModUser.queryAllBusinessInRange(point, 2*1.6);
            mYelpSearchTask = UtilSearch.yelpSearchGPS(this, search_txt, mAdapterBusiness, additionalPage, location_text, point, XxUtil.getSearchRadiusValue(getActivity()) );
        }else{
            mLocationSearched = null;
            query = ModUser.queryBusinessIncludingInName(search_txt);
            mYelpSearchTask = UtilSearch.yelpSearch(this, search_txt, mAdapterBusiness, additionalPage, location_text);
        }

    }

    public void yelpSearchLocationComplete() {
        isYelpSearchComplete = true;
        if (isParseSearchComplete)
            allSearchComplete();

        executeParseSearchQuery(query);
    }

    public void parseSearchComplete(){
        query = null;
        isParseSearchComplete = true;
        if (isYelpSearchComplete)
            allSearchComplete();
    }

    private void allSearchComplete() {
        showProgressBarSearch(false);
        dismissProgressDialog();
        mSwipyRefreshLayout.setRefreshing(false);

        if (mAdapterBusiness.getItemCount() >0)
            btnShowResultsMap.setEnabled(true);
    }


    private void executeParseSearchQuery(ParseQuery<ModUser> query){
        if (query == null){
            parseSearchComplete();
            return;
        }

        query.findInBackground(new FindCallback<ModUser>() {
            public void done(List<ModUser> result_list, ParseException e) {
                if (e == null) {
                    if(mAdapterBusiness != null) {
                        mAdapterBusiness.addItems(result_list);
                    }else
                        refreshListAdapter(result_list);

                } else {
                    ParseExceptionHandler.handleParseError(e,getActivity());
                }
                parseSearchComplete();
            }
        });
    }


//    private void startInitialBusinessParseQuery(){
//
//        ModUser.queryAllBusiness().findInBackground(new FindCallback<ModUser>() {
//            @Override
//            public void done(List<ModUser> objects, ParseException e) {
//                if (e == null){
//                    ModUser.addMultiBusiness(objects);
//                    addAndrefreshListAdapter(objects);
//                    allSearchComplete();
//                }else{
//                    dismissProgressDialog();
//                    ParseExceptionHandler.handleParseError(e, getActivity());
//                }
//            }
//        });
//    }







    public static final long LOCATION_REFRESH_TIME = 3000;
    public static final long LOCATION_REFRESH_TIME_SERVICE = 2000;
    public static final float LOCATION_REFRESH_DISTANCE = 50;
    public static final float LOCATION_REFRESH_DISTANCE_SERVICE = 3;
    public static final float GPS_ACCURACY = 200;
    public static final float GPS_ACCURACY_SERVICE = 21;
    private LocationManager mLocationManager;
    Location mLocation, mLocationSearched;

    private void createLocationManager(){
        if (mLocationManager == null)
            mLocationManager = (LocationManager) getActivity().getSystemService(Activity.LOCATION_SERVICE);

        isGpsEnabled = mLocationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
    }

    private void startLocator() {
        mGpsPrecisionGoodEnough = false;
        mLocationManager.requestLocationUpdates (LocationManager.GPS_PROVIDER, LOCATION_REFRESH_TIME, LOCATION_REFRESH_DISTANCE, mLocationListener);
        changesOccurred();
    }


    private void startEnableGpsDialog() {
        startActivityForResult(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS), RC_GPS);
    }

    private final LocationListener mLocationListener = new LocationListener() {

        @Override
        public void onLocationChanged(final Location location) {
            float accuracy = location.getAccuracy() ;
            mLocation = location;
            NhLog.e(TAG, "Location ac:" + accuracy + " Lt:" + location.getLatitude() + " Lg:" + location.getLongitude());

            if (accuracy < GPS_ACCURACY  ){
                mLocation = location;
                setGpsAccuracyGood();
            }else{
                mLocation = null;
                setGpsAccuracyBad();
            }
            setGpsEnabled();
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {

            NhLog.e(TAG, "Location StatusChanged:" + status + " Provider:" + provider);

            if (LocationProvider.OUT_OF_SERVICE == status){
                setGpsDisabled();
            }
            if (LocationProvider.TEMPORARILY_UNAVAILABLE == status){
                //setGpsDisabled();
            }
            if (LocationProvider.AVAILABLE == status){
                setGpsEnabled();
            }
        }

        //!!!!!!!!!!!!!!!!!!!!!!!!
        //won't be called if GPS was turned on before we started the app.
        //will be called if GPS was turned on after we started the app.
        @Override
        public void onProviderEnabled(String provider) {
            setGpsEnabled();
            NhLog.e(TAG, "Location ProviderEnabled:" + provider);
        }

        //will be called only if GPS is turned off on the device
        @Override
        public void onProviderDisabled(String provider) {
            setGpsDisabled();
            NhLog.e(TAG, "Location ProviderDisabled:" + provider);
        }
    };

    private boolean isGpsEnabled = false;
    private boolean isGpsWanted = true;
    private boolean mGpsPrecisionGoodEnough = false;

    private void stopLocator() {
        if (mLocationManager != null)
            mLocationManager.removeUpdates(mLocationListener);
        isGpsEnabled = false;
        changesOccurred();
    }

    @Override
    public void onResume() {
        createLocationManager();

        if (isGpsWanted) {
            if (isGpsEnabled)
                startLocator();
            else {
                isGpsWanted = false;
                mLocation = null;
            }
        }

        super.onResume();
    }

    private void gpsWantedCheck() {
        if (isGpsWanted) {
            etSearchLocation.setHint("Using your GPS location");
            if (!isGpsEnabled())
                startEnableGpsDialog();
            else
                startLocator();
        }else {
            etSearchLocation.setHint(getText(R.string.hint_search_location));
            mLocation = null;
            stopLocator();
        }
    }

    private boolean isGpsEnabled() {
        isGpsEnabled = mLocationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        return isGpsEnabled;
    }

    @Override
    public void onPause() {
        stopLocator();
        super.onPause();
    }

    protected void setGpsEnabled() {
        //ivToggleGps.setBackground(getDrawableCheck(R.drawable.ic_gps_on));
//        if (isGpsEnabled)
//            return;
        isGpsEnabled = true;
        changesOccurred();
    }

    protected void setGpsDisabled() {
        //ivToggleGps.setBackground(getDrawableCheck(R.drawable.ic_gps_off));
        //setGpsAccuracyBad();
//        if (!isGpsEnabled)
//            return;
        isGpsEnabled = false;
        changesOccurred();
    }

    protected void setGpsAccuracyGood(){
        mGpsPrecisionGoodEnough = true;
        changesOccurred();
    }

    private void changesOccurred() {
        if (isGpsEnabled && mGpsPrecisionGoodEnough){
            ivToggleGps.setBackground(getDrawableCheck(R.drawable.ic_gps_on));
            stopBlinking();
            return;
        }
        if (isGpsEnabled && !mGpsPrecisionGoodEnough){
            ivToggleGps.setBackground(getDrawableCheck(R.drawable.ic_gps_on));
            startBlinking();
            return;
        }

        if (isGpsEnabled)
            ivToggleGps.setBackground(getDrawableCheck(R.drawable.ic_gps_on));
        else {
            stopBlinking();
            ivToggleGps.setBackground(getDrawableCheck(R.drawable.ic_gps_off));
        }
    }

    protected void setGpsAccuracyBad(){
//        if (!mGpsPrecisionGoodEnough){
//            return;
//        }
        mGpsPrecisionGoodEnough = false;
        changesOccurred();
    }

    private void startBlinking(){
        stopBlinking();
        final Animation animation = new AlphaAnimation(1, 0); // Change alpha from fully visible to invisible
        animation.setDuration(500); // duration - half a second
        animation.setInterpolator(new LinearInterpolator()); // do not alter animation rate
        animation.setRepeatCount(Animation.INFINITE); // Repeat animation infinitely
        animation.setRepeatMode(Animation.REVERSE); // Reverse animation at the end so the button will fade back in
        ivToggleGps.startAnimation(animation);
    }

    private void stopBlinking(){
        ivToggleGps.clearAnimation();
    }

//    public boolean checkLocationServiceAvailability() {
//
//        // check GPS on or off
//        isGPSEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
//        // check Internet access
//        ConnectivityManager connectivityManager = (ConnectivityManager) mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
//        NetworkInfo netInfo = connectivityManager.getActiveNetworkInfo();
//        if (netInfo != null && netInfo.isConnected()) {
//            isNetworkEnabled = true;
//        } else {
//            isNetworkEnabled = false;
//        }
//
//        if (isGPSEnabled || isNetworkEnabled) {
//            canGetLocation = true;
//        } else {
//            canGetLocation = false;
//        }
//
//        return canGetLocation;
//    }

}