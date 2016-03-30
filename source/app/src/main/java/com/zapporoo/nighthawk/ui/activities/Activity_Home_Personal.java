package com.zapporoo.nighthawk.ui.activities;


import android.app.AlertDialog;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.yelp.clientlib.entities.Business;
import com.yelp.clientlib.entities.Deal;
import com.yelp.clientlib.entities.SearchResponse;
import com.zapporoo.nighthawk.App;
import com.zapporoo.nighthawk.R;
import com.zapporoo.nighthawk.callbacks.ICallbackSearchableAdapter;
import com.zapporoo.nighthawk.callbacks.IPersonalHomeActivity;
import com.zapporoo.nighthawk.quickblox.dialogs.Fragment_Personal_Home_Dialogues;
import com.zapporoo.nighthawk.ui.fragments.Fragment_Personal_Home_MyClubs;
import com.zapporoo.nighthawk.ui.fragments.Fragment_Personal_Home_Profile;
import com.zapporoo.nighthawk.ui.fragments.Fragment_Personal_Home_Search;
import com.zapporoo.nighthawk.ui.fragments.Fragment_Personal_Home_Tab;
import com.zapporoo.nighthawk.ui.views.CustomViewPager;
import com.zapporoo.nighthawk.util.NhLog;
import com.zapporoo.nighthawk.util.PictureDownloaderTask;
import com.zapporoo.nighthawk.util.UtilImage;
import com.zapporoo.nighthawk.util.XxUtil;

import java.util.HashMap;
import java.util.Map;

import retrofit.Call;
import retrofit.Callback;
import retrofit.Response;
import retrofit.Retrofit;

public class Activity_Home_Personal extends ActivityDefault implements ViewPager.OnPageChangeListener, IPersonalHomeActivity {

    private SectionsPagerAdapter mSectionsPagerAdapter;

    private int mTabs = 4;

    private Fragment_Personal_Home_Tab personalHomeFragments[] = new Fragment_Personal_Home_Tab[mTabs];

    private CustomViewPager mViewPager;
    private int mSelected = 0;

    private Toolbar mToolbarMain;
    private TabLayout tabLayout;
    private ImageView imgToolbarLogo;
    private TextView tvToolbarTitle;

    Fragment_Personal_Home_MyClubs fMyClubs;
    Fragment_Personal_Home_Search fSearch;

    AlertDialog alertdialog;

    View vLogout;

    private int[] tabIcons = {
            R.drawable.ic_tab_business_home,
            R.drawable.ic_tab_business_edit_info,
            R.drawable.ic_tab_business_hot_deals,
            R.drawable.ic_tab_business_log_out
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_personal);
        setupGui();
        setupTabIcons();

        Toolbar tb = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(tb);

        // Get the ActionBar here to configure the way it behaves.
        final ActionBar ab = getSupportActionBar();
        if(ab != null) {
            ab.setDisplayShowHomeEnabled(false); // show or hide the default home button
            ab.setDisplayHomeAsUpEnabled(false);
            ab.setDisplayShowCustomEnabled(true); // enable overriding the default toolbar layout
            ab.setDisplayShowTitleEnabled(false); // disable the default title element here (for centered title)
        }

        UtilImage.clearPicturesCache(UtilImage.IMG_PREFIX_YELP, getExternalCacheDir());
        //testYelpSearch();
    }

    int offset = 0;
    int MAX_RESULTS = 300;

    private void testYelpSearch() {
        if (offset > MAX_RESULTS)
            return;

        Map<String, String> params = new HashMap<>();

        // general params
        //params.put("term", "food");
        //params.put("limit", "30");
        params.put("offset", String.valueOf(offset));
        // locale params
        //params.put("lang", "fr");
        params.put("category_filter", "restaurants,bars,cafes");
        offset+=20;

        Call<SearchResponse> call = App.yelpAPI.search("San Francisco", params);
        Callback<SearchResponse> callback = new Callback<SearchResponse>() {
            @Override
            public void onResponse(Response<SearchResponse> response, Retrofit retrofit) {
                SearchResponse searchResponse = response.body();

                for (Business b:searchResponse.businesses()){
                    NhLog.e("Business result:", "" + b.id() + " #  " + b.name());

                    if (b.deals() != null)
                        for (Deal deal:b.deals())
                            NhLog.e("---deal:", deal.title());
                }

                testYelpSearch();
            }
            @Override
            public void onFailure(Throwable t) {
                // HTTP error happened, do something to handle it.
                showToast("Error");
            }
        };
        call.enqueue(callback);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        UtilImage.clearPicturesCache(UtilImage.IMG_PREFIX_YELP, getExternalCacheDir());
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
         switch (item.getItemId()) {
            case R.id.action_settings: {
                startNextActivity(Activity_Settings.class);
                break;
            }
        }
        return super.onOptionsItemSelected(item);
    }

    private void setupGui() {
        // Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        //setSupportActionBar(toolbar);
        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (CustomViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);
        mViewPager.setPagingEnabled(false);
        mViewPager.setOffscreenPageLimit(4);

        tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);

        mViewPager.addOnPageChangeListener(this);

        mToolbarMain = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbarMain);

        imgToolbarLogo = (ImageView) mToolbarMain.findViewById(R.id.toolbar_img);
        tvToolbarTitle = (TextView) mToolbarMain.findViewById(R.id.tvToolbarTitle);

        // Get the ActionBar here to configure the way it behaves.
        final ActionBar ab = getSupportActionBar();
        if(ab != null) {
            ab.setDisplayShowHomeEnabled(false); // show or hide the default home button
            ab.setDisplayHomeAsUpEnabled(false);
            ab.setDisplayShowCustomEnabled(true); // enable overriding the default toolbar layout
            ab.setDisplayShowTitleEnabled(false); // disable the default title element here (for centered title)
        }

        showToolbarLogo(mToolbarMain);  // default
    }

//    private void setupTabIcons() {
//        tabLayout.getTabAt(0).setIcon(tabIcons[0]);
//        tabLayout.getTabAt(1).setIcon(tabIcons[1]);
//        tabLayout.getTabAt(2).setIcon(tabIcons[2]);
//        tabLayout.getTabAt(3).setIcon(tabIcons[3]);
//    }

    private void setupTabIcons() {

        TextView tabOne = (TextView) LayoutInflater.from(this).inflate(R.layout.view_custom_tab, null).findViewById(R.id.tvCustomtab);
        tabOne.setText("Search");
        tabOne.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.ic_tab_personal_search, 0, 0);
        tabLayout.getTabAt(0).setCustomView(tabOne);

        TextView tabTwo = (TextView) LayoutInflater.from(this).inflate(R.layout.view_custom_tab, null).findViewById(R.id.tvCustomtab);
        tabTwo.setText("Messages");
        tabTwo.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.ic_tab_personal_messages, 0, 0);
        tabLayout.getTabAt(1).setCustomView(tabTwo);

        TextView tabThree = (TextView) LayoutInflater.from(this).inflate(R.layout.view_custom_tab, null).findViewById(R.id.tvCustomtab);
        tabThree.setText("My Clubs");
        tabThree.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.ic_tab_personal_myclubs, 0, 0);
        tabLayout.getTabAt(2).setCustomView(tabThree);

        TextView tabFour = (TextView) LayoutInflater.from(this).inflate(R.layout.view_custom_tab, null).findViewById(R.id.tvCustomtab);;
        tabFour.setText("Profile");
        tabFour.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.ic_tab_personal_profile, 0, 0);
        tabLayout.getTabAt(3).setCustomView(tabFour);
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int selected) {
        if (selected == 0){
            fSearch.refreshListAdapter();
        }
        if(selected == 2) {  // TODO do it better DARE!
            // my club is selected:
            showToolbarTitle(mToolbarMain, getString(R.string.tab_personal_my_clubs_title));
            fMyClubs.refreshAdapter();
        }else {
            showToolbarLogo(mToolbarMain);
        }
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }

    @Override
    public void showMyGroupActivity() {
        startNextActivity(Activity_Home_Friends.class);
    }

    @Override
    public void showHotDealsActivity() {
        startNextActivity(Activity_Personal_HotDeals.class);
    }

    public class SectionsPagerAdapter extends FragmentPagerAdapter {



        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            Fragment_Personal_Home_Tab item = null;
            if(personalHomeFragments[position] == null) {
                switch (position) {
                    case 1:
                        item = Fragment_Personal_Home_Dialogues.newInstance(position);
                        break;
                    case 2:
                        fMyClubs = Fragment_Personal_Home_MyClubs.newInstance(position);
                        item = fMyClubs;
                        break;
                    case 3:
                        item = Fragment_Personal_Home_Profile.newInstance(position);
                        break;
                    default:
                    case 0:
                        fSearch = Fragment_Personal_Home_Search.newInstance(position);
                        item = fSearch;
                        break;
                }
                personalHomeFragments[position] = item;
            }else
            {
                item = personalHomeFragments[position];
            }

            return item;
        }


        @Override
        public int getCount() {
            return 4;
        }

        @Override
        public CharSequence getPageTitle(int position) {
//            switch (position) {
//                case 0:
//                    return "SECTION 1";
//                case 1:
//                    return "SECTION 2";
//                case 2:
//                    return "SECTION 3";
//                case 3:
//                    return "SECTION 4";
//            }
            return null;
        }
    }


    @Override
    public void onBackPressed() {
        boolean handled = false;

        for(int i = 0; i < personalHomeFragments.length && !handled; ++i) {
            if (personalHomeFragments[i] != null) {
                handled = personalHomeFragments[i].handleBackPress();
            }
        }

        if(!handled)
            super.onBackPressed();
    }
}