package com.zapporoo.nighthawk.ui.fragments;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseQuery;
import com.parse.SaveCallback;
import com.zapporoo.nighthawk.R;
import com.zapporoo.nighthawk.adapters.AdapterFriends;
import com.zapporoo.nighthawk.callbacks.ICallbackFriendItem;
import com.zapporoo.nighthawk.callbacks.ICallbackFriendSearch;
import com.zapporoo.nighthawk.callbacks.ICallbackImage;
import com.zapporoo.nighthawk.model.ModRelationship;
import com.zapporoo.nighthawk.model.ModUser;
import com.zapporoo.nighthawk.ui.views.CircularImageView;
import com.zapporoo.nighthawk.ui.views.SpacesItemDecoration;
import com.zapporoo.nighthawk.util.ParseExceptionHandler;
import com.zapporoo.nighthawk.util.Test;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Pile on 12/15/2015.
 */
public class Fragment_Friends_Invite extends FragmentDefault implements ICallbackFriendSearch, TextWatcher {
    private RecyclerView rvSearchResult;
    private AdapterFriends mAdapterSearchResults;
    private List<ModUser> mFoundFriends;

    ParseQuery<ModUser> query;
    private int TIME_COUNTDOWN = 300;
    private Handler mHandler;
    private Runnable mRunnableSearch;
    private View pbSearch;
    private final int CHARACTER_SEARCH_MINIMUM = 1;
    EditText etSearch;

    /**
     * The fragment argument representing the section number for this
     * fragment.
     */
    private static final String ARG_SECTION_NUMBER = "section_number";

    public Fragment_Friends_Invite() {
    }

    /**
     * Returns a new instance of this fragment for the given section
     * number.
     */
    public static Fragment_Friends_Invite newInstance(int sectionNumber) {
        Fragment_Friends_Invite fragment = new Fragment_Friends_Invite();
        Bundle args = new Bundle();
        args.putInt(ARG_SECTION_NUMBER, sectionNumber);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_friends_invite, container, false);

        // TODO provide list if available. You can also call mAdapterSearchResults.updateItems later
        //mFoundFriends = Test.createDummyFriends();

        mAdapterSearchResults = new AdapterFriends(getFoundFriends(), this, AdapterFriends.ADAPTER_ID_SEARCH_RESULTS);

        rvSearchResult = (RecyclerView) rootView.findViewById(R.id.rvSearchResult);
        rvSearchResult.setLayoutManager(new LinearLayoutManager(rootView.getContext(), LinearLayoutManager.VERTICAL, false));
        rvSearchResult.addItemDecoration(new SpacesItemDecoration(1));
        rvSearchResult.setAdapter(mAdapterSearchResults);

        etSearch = (EditText) rootView.findViewById(R.id.etSearch);
        etSearch.addTextChangedListener(this);
        pbSearch = rootView.findViewById(R.id.pbSearch);;
        return rootView;
    }


    private void refreshAdapter(){
        mAdapterSearchResults.updateItems(mFoundFriends);
    }

    private List<ModUser> getFoundFriends() {
        if(mFoundFriends == null)
            mFoundFriends = new ArrayList<>();

        return mFoundFriends;
    }

    @Override
    public void onLoadFriendData(AdapterFriends.ViewHolderFriend vHolder, ModUser currentItem, int pAdapterId) {
        vHolder.tvName.setText(currentItem.getPersonalName());
        if (!vHolder.lastId.equals(currentItem.getObjectId()))
            vHolder.circImgFriend.setImageDrawable(getDrawableCheck(R.drawable.ic_image_user));
        vHolder.lastId = currentItem.getObjectId();
        fetchProfileImage(currentItem, vHolder.circImgFriend);
    }

    @Override
    public void onFriendClicked(AdapterFriends.ViewHolderFriend vHolder, final ModUser currentItem, int pAdapterId) {
        // TODO Pile perform action on server and refresh adapter when completed.
        showConfirmDialog(getString(R.string.confirm_send_friend_request), new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showProgressDialog(getString(R.string.progress_saving));
                final ModRelationship relationship = new ModRelationship();
                relationship.setFrom(ModUser.getModUser());
                relationship.setTo(currentItem);
                relationship.setStatus(ModRelationship.VALUE_STATUS_FRIENDS);
                relationship.saveInBackground(new SaveCallback() {
                    @Override
                    public void done(ParseException e) {
                        dismissProgressDialog();
                        if (e == null) {
                            Fragment_Friends_Home.addRelationship(relationship);
                            mFoundFriends = removeExisting(mFoundFriends);
                            refreshAdapter();
                        } else
                            ParseExceptionHandler.handleParseError(e, getActivity());
                    }
                });
            }
        });
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {

    }

    @Override
    public void afterTextChanged(Editable s) {
        searchTextChanged(s.toString().trim());
    }

    public void searchTextChanged(String text){
        if (text.length() >= CHARACTER_SEARCH_MINIMUM){
            startSearchCountdown(text);
        }else{
            stopCurrentSearch();
            refreshAdapter();
        }
    }

    private boolean checkEditTextConditions(){
        if (etSearch.getText().toString().length() == 0)
            return true;
        else
            return false;
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
    }

    private void startSearchCountdown(final String text) {
        if (query != null)
            query.cancel();

        if (mHandler == null) {
            mHandler = new Handler();
        } else {
            mHandler.removeCallbacks(mRunnableSearch);
        }

        mRunnableSearch = new Runnable() {

            @Override
            public void run() {
                startSearchTask(text.trim());
            }
        };
        mHandler.postDelayed(mRunnableSearch, TIME_COUNTDOWN);
    }

    protected void startSearchTask(String search_txt) {
        stopCurrentSearch();
        showProgressBarSearch(true);
        query = ModUser.querySearchUsers(search_txt);
        executeSearchQuery(query);
    }

    private void executeSearchQuery(ParseQuery<ModUser> query){
        query.findInBackground(new FindCallback<ModUser>() {
            public void done(List<ModUser> result_list, ParseException e) {
                showProgressBarSearch(false);
                if (e == null) {
                    mFoundFriends = removeExisting(result_list);
                    refreshAdapter();
                } else {
                    ParseExceptionHandler.handleParseError(e, getActivity());
                    dismissProgressDialog();
                }
            }
        });
    }

    private List<ModUser> removeExisting(List<ModUser> result_list) {
        return  ModRelationship.extractNonFriends(Fragment_Friends_Home.getRelationships(), result_list);
    }

}