package com.zapporoo.nighthawk.ui.activities;

import android.app.AlertDialog;
import android.support.design.widget.TabLayout;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;

import android.widget.TextView;

import com.parse.ParseUser;
import com.zapporoo.nighthawk.R;
import com.zapporoo.nighthawk.ui.fragments.Fragment_Blank;
import com.zapporoo.nighthawk.ui.fragments.Fragment_Business_Home;
import com.zapporoo.nighthawk.ui.fragments.Fragment_Business_Home_EditInfo;
import com.zapporoo.nighthawk.ui.fragments.Fragment_Business_Home_HotDeals;
import com.zapporoo.nighthawk.ui.views.CustomViewPager;
import com.zapporoo.nighthawk.util.UtilAds;
import com.zapporoo.nighthawk.util.UtilDialog;

public class Activity_Home_Business extends ActivityDefault implements ViewPager.OnPageChangeListener {

    private SectionsPagerAdapter mSectionsPagerAdapter;

    private Fragment_Business_Home fragBusinessHome;
    private Fragment_Business_Home_EditInfo fragBusinessEdit;
    private Fragment_Business_Home_HotDeals fragBusinessHotDeals;
    private Fragment_Blank fragBlank;

    private CustomViewPager mViewPager;
    private int mSelected = 0;

    private TabLayout tabLayout;

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
        setContentView(R.layout.activity_home_business);
        setupGui();
        setupTabIcons();
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
        mViewPager.setOffscreenPageLimit(3);

        tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);

        mViewPager.addOnPageChangeListener(this);

        vLogout = findViewById(R.id.vBusinessHomeLogout);
        vLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showLogoutDialog();
            }
        });
    }

//    private void setupTabIcons() {
//        tabLayout.getTabAt(0).setIcon(tabIcons[0]);
//        tabLayout.getTabAt(1).setIcon(tabIcons[1]);
//        tabLayout.getTabAt(2).setIcon(tabIcons[2]);
//        tabLayout.getTabAt(3).setIcon(tabIcons[3]);
//    }

    private void setupTabIcons() {

        TextView tabOne = (TextView) LayoutInflater.from(this).inflate(R.layout.view_custom_tab, null).findViewById(R.id.tvCustomtab);
        tabOne.setText("Home");
        tabOne.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.ic_tab_business_home, 0, 0);
        tabLayout.getTabAt(0).setCustomView(tabOne);

        TextView tabTwo = (TextView) LayoutInflater.from(this).inflate(R.layout.view_custom_tab, null).findViewById(R.id.tvCustomtab);
        tabTwo.setText("Edit Info");
        tabTwo.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.ic_tab_business_edit_info, 0, 0);
        tabLayout.getTabAt(1).setCustomView(tabTwo);

        TextView tabThree = (TextView) LayoutInflater.from(this).inflate(R.layout.view_custom_tab, null).findViewById(R.id.tvCustomtab);
        tabThree.setText("Hot Deals");
        tabThree.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.ic_tab_business_hot_deals, 0, 0);
        tabLayout.getTabAt(2).setCustomView(tabThree);

        TextView tabFour = (TextView) LayoutInflater.from(this).inflate(R.layout.view_custom_tab, null).findViewById(R.id.tvCustomtab);;
        tabFour.setText("Log Out");
        tabFour.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.ic_tab_business_log_out, 0, 0);
        tabLayout.getTabAt(3).setCustomView(tabFour);
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int selected) {

    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }


    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            UtilAds.showInterstitialAndNull();

            switch (position) {
                case 0: if (fragBusinessHome == null)
                        fragBusinessHome  = Fragment_Business_Home.newInstance(position);
                    return fragBusinessHome;
                case 1: if (fragBusinessEdit == null)
                        fragBusinessEdit  = Fragment_Business_Home_EditInfo.newInstance(position);
                    return fragBusinessEdit;
                case 2: if (fragBusinessHotDeals == null)
                        fragBusinessHotDeals = Fragment_Business_Home_HotDeals.newInstance(position);
                    return fragBusinessHotDeals;
                case 3:
                    if (fragBlank == null)
                        fragBlank = Fragment_Blank.newInstance(position);
                    return fragBlank;
            }

            return null;
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


    private void showLogoutDialog() {
         alertdialog = UtilDialog.showConfirmDialog(this, "Are you sure you want to log out?",
                new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        ParseUser.logOutInBackground();
                        startNextActivityClearTask(Activity_Login.class);
                    }
                });
    }
}
