package com.zapporoo.nighthawk.ui.fragments;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.parse.DeleteCallback;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.quickblox.chat.QBChatService;
import com.quickblox.chat.model.QBDialog;
import com.quickblox.chat.model.QBDialogType;
import com.quickblox.core.QBEntityCallback;
import com.quickblox.core.exception.QBResponseException;
import com.quickblox.core.request.QBRequestGetBuilder;
import com.zapporoo.nighthawk.R;
import com.zapporoo.nighthawk.adapters.AdapterFriends;
import com.zapporoo.nighthawk.adapters.AdapterFriendsGroup;
import com.zapporoo.nighthawk.adapters.AdapterFriendsRelationships;
import com.zapporoo.nighthawk.callbacks.ICallbackFriendItem;
import com.zapporoo.nighthawk.callbacks.IFriendsHomeActivity;
import com.zapporoo.nighthawk.model.ModGroup;
import com.zapporoo.nighthawk.model.ModRelationship;
import com.zapporoo.nighthawk.model.ModUser;
import com.zapporoo.nighthawk.quickblox.conversation.chat.IChat;
import com.zapporoo.nighthawk.quickblox.util.ChatService;
import com.zapporoo.nighthawk.quickblox.util.UtilChat;
import com.zapporoo.nighthawk.ui.activities.ActivityDefault;
import com.zapporoo.nighthawk.ui.activities.Activity_Messages_Single;
import com.zapporoo.nighthawk.ui.views.SpacesItemDecoration;
import com.zapporoo.nighthawk.util.NhLog;
import com.zapporoo.nighthawk.util.ParseExceptionHandler;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Pile on 12/15/2015.
 */
public class Fragment_Friends_Home extends FragmentDefault implements Toolbar.OnMenuItemClickListener, ICallbackFriendItem {
    private static boolean isListUpdated = false;
    private Toolbar mMyGroupToolbar, mMyFriendsToolbar;

    private RecyclerView rvMyGroup, rvMyFriends;
    private AdapterFriendsGroup mAdapterMyGroup;
    private AdapterFriendsRelationships mAdapterMyFriends;
    private List<ModGroup> mGroupItems;
    private static List<ModRelationship> mRelationships;
    private List<ModUser> mFriends;

    private IFriendsHomeActivity mFriendsHomeActivity;

    /**
     * The fragment argument representing the section number for this
     * fragment.
     */
    private static final String ARG_SECTION_NUMBER = "section_number";


    public Fragment_Friends_Home() {
    }

    /**
     * Returns a new instance of this fragment for the given section
     * number.
     */
    public static Fragment_Friends_Home newInstance(int sectionNumber) {
        Fragment_Friends_Home fragment = new Fragment_Friends_Home();
        Bundle args = new Bundle();
        args.putInt(ARG_SECTION_NUMBER, sectionNumber);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_friends_home, container, false);

        mMyGroupToolbar = (Toolbar) rootView.findViewById(R.id.tbrMyGroup);
        mMyGroupToolbar.inflateMenu(R.menu.menu_fragment_my_group);
        mMyGroupToolbar.setOnMenuItemClickListener(this);

        mMyFriendsToolbar = (Toolbar) rootView.findViewById(R.id.tbrMyFriends);
        mMyFriendsToolbar.inflateMenu(R.menu.menu_fragment_my_friends);
        mMyFriendsToolbar.setOnMenuItemClickListener(this);

        ActivityDefault.showToolbarTitle(mMyGroupToolbar, mMyGroupToolbar.getContext().getString(R.string.title_activity_home_friends_mygroup));
        ActivityDefault.showToolbarTitle(mMyFriendsToolbar, mMyFriendsToolbar.getContext().getString(R.string.title_activity_home_friends_myfriends));

        clearLists();
        mAdapterMyGroup = new AdapterFriendsGroup(getFriendsInGroup(), this, AdapterFriends.ADAPTER_ID_GROUP);
        mAdapterMyFriends = new AdapterFriendsRelationships(getFriends(), this, AdapterFriends.ADAPTER_ID_FRIEND);

        rvMyGroup = (RecyclerView) rootView.findViewById(R.id.rvMyGroup);
        rvMyGroup.setLayoutManager(new LinearLayoutManager(rootView.getContext(), LinearLayoutManager.VERTICAL, false));
        rvMyGroup.addItemDecoration(new SpacesItemDecoration(1));
        rvMyGroup.setAdapter(mAdapterMyGroup);

        rvMyFriends = (RecyclerView) rootView.findViewById(R.id.rvMyFriends);
        rvMyFriends.setLayoutManager(new LinearLayoutManager(rootView.getContext(), LinearLayoutManager.VERTICAL, false));
        rvMyFriends.addItemDecoration(new SpacesItemDecoration(1));
        rvMyFriends.setAdapter(mAdapterMyFriends);

        getDialogs();


        return rootView;
    }

    QBDialog mDialogOwnGroup = null;

    public void getDialogs() {
        showProgressDialog(getString(R.string.progress_loading_data));

        ChatService.getInstance().getDialogue(ModUser.getModUser().getGroupChatId(), new QBEntityCallback() {
            @Override
            public void onSuccess(Object o, Bundle bundle) {
                mDialogOwnGroup = (QBDialog) o;
                if (o == null) {
                    ModUser.getModUser().setGroupChatId("");
                    ModUser.getModUser().saveInBackground();
                }
                startTasks();
            }

            @Override
            public void onError(QBResponseException e) {

            }
        });

    }






    private void clearLists() {
        if (mRelationships != null)
            mRelationships.clear();
        if (mGroupItems != null)
            mGroupItems.clear();
    }



    private void startTasks() {
        startRelationshipsQueryTask();
    }

    private void startRelationshipsQueryTask(){
        ModRelationship.queryFriendsAll().findInBackground(new FindCallback<ModRelationship>() {
            @Override
            public void done(List<ModRelationship> objects, ParseException e) {
                if (e == null) {
                    startGroupQueryTask();
                    mRelationships = objects;
                } else {
                    ParseExceptionHandler.handleParseError(e, getActivity());
                    dismissProgressDialog();
                }
            }
        });
    }

    private void startGroupQueryTask() {
        ModGroup.queryMyGroup().findInBackground(new FindCallback<ModGroup>() {
            @Override
            public void done(List<ModGroup> objects, ParseException e) {
                dismissProgressDialog();
                if (e == null) {
                    ModGroup.saveLocally(objects);
                    mGroupItems = objects;
                    refreshAdapters();
                } else
                    ParseExceptionHandler.handleParseError(e, getActivity());
            }
        });
    }

    private void refreshAdapters() {
        mAdapterMyFriends.updateItems(getNotInGroupRelationships(mRelationships, mGroupItems));
        mAdapterMyGroup.updateItems(mGroupItems);
    }

    private List<ModRelationship> getNotInGroupRelationships(List<ModRelationship> pRelationships, List<ModGroup> pGroupItems) {
        List<ModRelationship> ret = new ArrayList<>();
        for (ModRelationship relationship:pRelationships)
            if (isInGroup(relationship, pGroupItems))
                continue;
            else
                ret.add(relationship);
        return  ret;
    }

    private boolean isInGroup(ModRelationship relationship, List<ModGroup> pGroupItems) {
        for (ModGroup groupItem:pGroupItems)
            if (relationship.extractNotMe().isEqual(groupItem.extractNotMe()))
                return true;
        return false;
    }


    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        try
        {
            mFriendsHomeActivity = (IFriendsHomeActivity) activity;
        }catch (ClassCastException e) {
            throw new IllegalStateException("This fragment must be attached to IFriendsHomeActivity!");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();

        mFriendsHomeActivity = null;
    }

    private List<ModGroup> getFriendsInGroup() {
        if(mGroupItems == null)
            mGroupItems = new ArrayList<>();

        return mGroupItems;
    }

    private List<ModRelationship> getFriends() {
        if(mRelationships == null)
            mRelationships = new ArrayList<>();

        return mRelationships;
    }

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_invite: {
                if (mAdapterMyGroup.getItemCount() == 0)
                    showToast(getString(R.string.toast_no_friends_in_group));
                else
                    mFriendsHomeActivity.showGroupChatActivity();
                break;
            }
            case R.id.action_add: {
                mFriendsHomeActivity.showInviteActivity();
                break;
            }
        }
        return false;
    }

    @Override
    public void onLoadGroupItemData(AdapterFriendsGroup.ViewHolderFriend vHolder, ModGroup currentItem, int pAdapterId) {
        vHolder.tvName.setText(currentItem.extractNotMe().getPersonalName());
        if (!vHolder.lastId.equals(currentItem.getObjectId()))
            vHolder.circImgFriend.setImageDrawable(getDrawableCheck(R.drawable.ic_image_user));
        fetchProfileImage(currentItem.extractNotMe(), vHolder.circImgFriend);
    }



    @Override
    public void onGroupItemClicked(AdapterFriendsGroup.ViewHolderFriend vHolder, ModGroup currentItem, int pAdapterId) {
        removeUserFromDialog(currentItem);
    }

    private void removeUserFromDialog(final ModGroup currentItem){
        if (ModUser.getModUser().getGroupChatId() == null
                || ModUser.getModUser().getGroupChatId().isEmpty()){

            currentItem.deleteInBackground();
            mGroupItems.remove(currentItem);
            ModGroup.removeSingle(currentItem);
            refreshAdapters();
        }else{
            if (mDialogOwnGroup != null) {
                remove(currentItem);
            }else
                ChatService.getInstance().getDialogue(ModUser.getModUser().getGroupChatId(), new QBEntityCallback() {
                    @Override
                    public void onSuccess(Object o, Bundle bundle) {
                        mDialogOwnGroup = (QBDialog) o;
                        remove(currentItem);
                    }

                    @Override
                    public void onError(QBResponseException e) {

                    }
                });
        }
    }

    private void remove(final ModGroup currentItem){
        showProgressDialog(getString(R.string.progress_saving));
        UtilChat.removeUserFromGroupChat(mDialogOwnGroup, currentItem.extractNotMe().getChatId(), new QBEntityCallback<QBDialog>() {
            @Override
            public void onSuccess(QBDialog qbDialog, Bundle bundle) {
                currentItem.deleteInBackground();
                mDialogOwnGroup = qbDialog;
                UtilChat.replaceDialog(qbDialog);
                mGroupItems.remove(currentItem);
                ModGroup.removeSingle(currentItem);
                dismissProgressDialog();
                refreshAdapters();
            }

            @Override
            public void onError(QBResponseException e) {
                dismissProgressDialog();
                NhLog.e("Ero", "" + e.getErrors());
            }
        });
    }

    @Override
    public void onLoadRelationshipItemData(AdapterFriendsRelationships.ViewHolderFriend vHolder, ModRelationship currentItem, int pAdapterId) {
        vHolder.tvName.setText(currentItem.extractNotMe().getPersonalName());
        if (!vHolder.lastId.equals(currentItem.getObjectId()))
            vHolder.circImgFriend.setImageDrawable(getDrawableCheck(R.drawable.ic_image_user));
        fetchProfileImage(currentItem.extractNotMe(), vHolder.circImgFriend);
    }

    @Override
    public void onRelationshipItemClicked(AdapterFriendsRelationships.ViewHolderFriend vHolder, ModRelationship currentItem, int pAdapterId) {
        ModGroup groupItem = new ModGroup();
        groupItem.setFrom(ModUser.getModUser());
        groupItem.setTo(currentItem.extractNotMe());
        groupItem.saveInBackground();

        addUserToDialog(groupItem);
    }

    @Override
    public void onRelationshipItemDelete(AdapterFriendsRelationships.ViewHolderFriend vHolder, final ModRelationship currentItem, int pAdapterId) {
        showConfirmDialog(getString(R.string.confirm_remove_friend), new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showProgressDialog(getString(R.string.progress_removing));
                currentItem.deleteInBackground(new DeleteCallback() {
                    @Override
                    public void done(ParseException e) {
                        dismissProgressDialog();
                        if (e == null){
                            removeRelationship(currentItem);
                            refreshAdapters();
                        }
                    }
                });
            }
        });
    }

    @Override
    public void onRelationshipItemStartChat(AdapterFriendsRelationships.ViewHolderFriend vHolder, ModRelationship currentItem, int pAdapterId) {
        if (!ModUser.getModUser().getMessagesEnabled()){
            showAlertDialog(getString(R.string.alert_messaging_is_off));
            return;
        }

        getDialogsPrivate(currentItem.getNotMe().getChatId());
    }

    public void getDialogsPrivate(final int chatId) {
        showProgressDialog(getString(R.string.progress_loading_data));

        List<Integer> occupants = new ArrayList<>();
        occupants.add(chatId);
        occupants.add(ModUser.getModUser().getChatId());

        final QBRequestGetBuilder customObjectRequestBuilder = new QBRequestGetBuilder();
        customObjectRequestBuilder.setLimit(100);
        customObjectRequestBuilder.all("occupants_ids", occupants);

        QBChatService.getChatDialogs(QBDialogType.PRIVATE, customObjectRequestBuilder, new QBEntityCallback<ArrayList<QBDialog>>() {
            @Override
            public void onSuccess(final ArrayList<QBDialog> dialogs, Bundle args) {
                dismissProgressDialog();
                if (dialogs.size() > 0 ){
                    startChatActivity(dialogs.get(0), chatId);
                    return;
                }

                startChatActivity(null, chatId);
            }

            @Override
            public void onError(QBResponseException errors) {
                dismissProgressDialog();
                showAlertDialog(getString(R.string.error_server_error));
                NhLog.e("Eror", "" + errors.getErrors());
            }
        });

    }

    private void startChatActivity(QBDialog dialog, Integer id){
        Intent intent = new Intent(getActivity(), Activity_Messages_Single.class);

        if (dialog != null){
            Bundle bundle = new Bundle();
            bundle.putSerializable(IChat.EXTRA_DIALOG, dialog);
            intent.putExtras(bundle);
        }

        intent.putExtra(UtilChat.KEY_USER_CHAT_ID, id);
        startNextActivity(intent);
    }

    private void addUserToDialog(final ModGroup groupEntry){
        if (mDialogOwnGroup != null) {
            showProgressDialog(getString(R.string.progress_saving));
            UtilChat.addUserToChat(mDialogOwnGroup, groupEntry.extractNotMe().getChatId(), new QBEntityCallback<QBDialog>() {
                @Override
                public void onSuccess(QBDialog qbDialog, Bundle bundle) {
                    mGroupItems.add(groupEntry);
                    ModGroup.addSingle(groupEntry);
                    UtilChat.replaceDialog(qbDialog);
                    dismissProgressDialog();
                    refreshAdapters();
                }

                @Override
                public void onError(QBResponseException e) {
                    groupEntry.deleteInBackground();
                    dismissProgressDialog();
                    NhLog.e("Ero", "" + e.getErrors());
                }
            });
        }else{
            mGroupItems.add(groupEntry);
            ModGroup.addSingle(groupEntry);
            refreshAdapters();
        }
    }


    @Override
    public void onResume() {
        if (isListUpdated) {
            refreshAdapters();
            isListUpdated = false;
        }
        super.onResume();
    }

    public static void addRelationship(ModRelationship relationship) {
        if (mRelationships != null)
            mRelationships.add(relationship);

        isListUpdated = true;
    }

    public static void removeRelationship(ModRelationship relationship) {
        if (mRelationships != null)
            mRelationships.remove(relationship);

        isListUpdated = true;
    }

    public static List<ModRelationship> getRelationships() {
        if (mRelationships == null) {
            mRelationships = new ArrayList<>();
        }

        return mRelationships;
    }
}