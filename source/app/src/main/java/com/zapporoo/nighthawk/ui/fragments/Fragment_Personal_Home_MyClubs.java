package com.zapporoo.nighthawk.ui.fragments;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.parse.DeleteCallback;
import com.parse.ParseException;
import com.zapporoo.nighthawk.R;
import com.zapporoo.nighthawk.adapters.AdapterPersonalMyClubs;
import com.zapporoo.nighthawk.callbacks.ICallbackMyClubItem;
import com.zapporoo.nighthawk.model.ModMyClub;
import com.zapporoo.nighthawk.ui.views.SpacesItemDecoration;
import com.zapporoo.nighthawk.util.ParseExceptionHandler;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Pile on 12/15/2015.
 */
public class Fragment_Personal_Home_MyClubs extends Fragment_Personal_Home_Tab implements Toolbar.OnMenuItemClickListener, ICallbackMyClubItem {
    private Toolbar mClubSelectionToolbar;

    private int mCurrentMode;
    private MenuItem mEdit, mDelete;
    private RecyclerView rvClubs;
    private AdapterPersonalMyClubs mAdapterMyClubs;
    private List<ModMyClub> mItems;
    private TextView tvSelection;

    private static final int MODE_VIEW = 0;
    private static final int MODE_EDIT = 1;

    /**
     * The fragment argument representing the section number for this
     * fragment.
     */
    private static final String ARG_SECTION_NUMBER = "section_number";

    public Fragment_Personal_Home_MyClubs() {
    }

    /**
     * Returns a new instance of this fragment for the given section
     * number.
     */
    public static Fragment_Personal_Home_MyClubs newInstance(int sectionNumber) {
        Fragment_Personal_Home_MyClubs fragment = new Fragment_Personal_Home_MyClubs();
        Bundle args = new Bundle();
        args.putInt(ARG_SECTION_NUMBER, sectionNumber);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_personal_home_myclubs, container, false);
        setHasOptionsMenu(true);

        mClubSelectionToolbar = (Toolbar) rootView.findViewById(R.id.tbrClubSelection);
        mClubSelectionToolbar.inflateMenu(R.menu.menu_fragment_personal_clubs);
        mClubSelectionToolbar.setOnMenuItemClickListener(this);

        tvSelection = (TextView) mClubSelectionToolbar.findViewById(R.id.tvToolbarTitle);
        tvSelection.setText("");

        Menu clubSelectionMenu = mClubSelectionToolbar.getMenu();
        mEdit = clubSelectionMenu.findItem(R.id.action_edit);
        mDelete = clubSelectionMenu.findItem(R.id.action_delete);

        // TODO read from saved instance state.
        mCurrentMode = MODE_VIEW;

        mItems = ModMyClub.getAllMyClubsFromCache();

        mAdapterMyClubs = new AdapterPersonalMyClubs(getClubItems(), false, this);

        rvClubs = (RecyclerView) rootView.findViewById(R.id.rvMyClubs);
        rvClubs.setLayoutManager(new LinearLayoutManager(rootView.getContext(), LinearLayoutManager.VERTICAL, false));
        rvClubs.addItemDecoration(new SpacesItemDecoration(1));
        rvClubs.setAdapter(mAdapterMyClubs);

        refreshMenu();

        return rootView;
    }

    private List<ModMyClub> getClubItems() {
        if(mItems == null)
            mItems = new ArrayList<>();

        return mItems;
    }

    @Override
    public void onItemFavorite(ModMyClub pItem) {
        // TODO Pile mark pItem as favorite on server!
    }

    @Override
    public void onItemSelectionChanged(ModMyClub currentItem, boolean isChecked) {
        refreshMenu();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);

        inflater.inflate(R.menu.menu_activity_home_personal, menu);
    }

    private void refreshMenu() {
        switch (mCurrentMode) {
            case MODE_VIEW: {
                if(mEdit != null) {
                    mEdit.setVisible(true);
                }

                tvSelection.setVisibility(View.INVISIBLE);

                if(mDelete != null) {
                    mDelete.setVisible(false);
                }
                break;
            }
            case MODE_EDIT: {
                if(mEdit != null) {
                    mEdit.setVisible(false);
                }

                String tempStr;
                int selectedItemsCount = 0;

                for(ModMyClub item : getClubItems())
                {
                    if(item.isSelected())
                        selectedItemsCount++;
                }

                if(selectedItemsCount == 0)
                    tempStr = "";
                else
                    tempStr = selectedItemsCount + " SELECTED";

                tvSelection.setVisibility(View.VISIBLE);
                tvSelection.setText(tempStr);

                if (mDelete != null) {
                    mDelete.setVisible(selectedItemsCount > 0);
                }
                break;
            }
        }
    }

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_edit: {
                setMode(MODE_EDIT);
                break;
            }
            case R.id.action_delete: {
                setMode(MODE_VIEW);
                List <ModMyClub> toRemove = new ArrayList<>();
                for(ModMyClub club : getClubItems()) {
                    if(club.isSelected()) {
                        toRemove.add(club);
                        // TODO Pile obrisati na serveru ovaj item.
                        // TODO nakon uspjesne operacije na serveru treba obrisati element iz liste (ili refreshovati sa servera opet).
                    }
                }
                removeSelected(toRemove);
                break;
            }
        }
        return false;
    }

    private void removeSelected(final List<ModMyClub> toRemove) {
        showProgressDialog(getText(R.string.progress_removing).toString());
        ModMyClub.deleteAllInBackground(toRemove, new DeleteCallback() {
            @Override
            public void done(ParseException e) {
                dismissProgressDialog();
                if (e == null){
                    ModMyClub.removeListFromCache(toRemove);
                    refreshAdapter();
                }else
                    ParseExceptionHandler.handleParseError(e, getActivity());

            }
        });
    }

    private void setMode(int pMode) {
        mCurrentMode = pMode;
        refreshMenu();
        mAdapterMyClubs.setAllowChecking(pMode == MODE_EDIT);
    }

    @Override
    public boolean handleBackPress() {
        if(mCurrentMode == MODE_EDIT)
        {
            for(ModMyClub item : getClubItems()) {
                item.setSelected(false);  //reset to default.
            }
            setMode(MODE_VIEW);
            return true;
        }else
        {
            return false;
        }
    }

    public void refreshAdapter() {
        mAdapterMyClubs.updateItems(ModMyClub.getAllMyClubsFromCache());
        mAdapterMyClubs.notifyDataSetChanged();
    }
}