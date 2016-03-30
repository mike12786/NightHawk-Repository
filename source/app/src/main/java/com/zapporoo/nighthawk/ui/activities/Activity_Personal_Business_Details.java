package com.zapporoo.nighthawk.ui.activities;


import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.parse.DeleteCallback;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.SaveCallback;
import com.quickblox.auth.QBAuth;
import com.quickblox.auth.model.QBSession;
import com.quickblox.chat.QBChatService;
import com.quickblox.chat.model.QBDialog;
import com.quickblox.chat.model.QBDialogType;
import com.quickblox.core.QBEntityCallback;
import com.quickblox.core.exception.QBResponseException;
import com.quickblox.core.request.QBRequestGetBuilder;
import com.quickblox.users.model.QBUser;
import com.zapporoo.nighthawk.R;
import com.zapporoo.nighthawk.adapters.AdapterPersonalBusinessCheckedIn;
import com.zapporoo.nighthawk.callbacks.ICallbackBusinessCheckedIn;
import com.zapporoo.nighthawk.model.ModCheckIn;
import com.zapporoo.nighthawk.model.ModUser;
import com.zapporoo.nighthawk.quickblox.conversation.chat.IChat;
import com.zapporoo.nighthawk.quickblox.util.ChatService;
import com.zapporoo.nighthawk.quickblox.util.UtilChat;
import com.zapporoo.nighthawk.ui.views.CircularImageView;
import com.zapporoo.nighthawk.ui.views.SpacesItemDecoration;
import com.zapporoo.nighthawk.util.NhLog;
import com.zapporoo.nighthawk.util.ParseExceptionHandler;

import java.util.ArrayList;
import java.util.List;

public class Activity_Personal_Business_Details extends Activity_Personal_Business_Default implements ICallbackBusinessCheckedIn {

    private static final int STATE_NORMAL = 0;
    private static final int STATE_CHECK_IN = 1;

    private RecyclerView rvCheckedInItems;
    private CircularImageView circImgProfile;
    private TextView tvProfileName;
    private TextView tvCheckedInInfo;
    private ViewGroup mContainerCheckIn;
    private AdapterPersonalBusinessCheckedIn mAdapterCheckedIn;
    private int mState;
    private ModCheckIn checkIn;
    private Boolean isUserCheckedHere = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        isUserCheckedHere = null;
        setState(STATE_CHECK_IN);
        setContentView(R.layout.activity_personal_business_check_in);
        setupGui();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);

        getMenuInflater().inflate(R.menu.menu_activity_personal_business_details, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        boolean rlt = false;
        switch (item.getItemId()) {
            case R.id.action_check_in: {
                showCheckInForm(getBusinessForDisplay());
                break;
            }
            case R.id.action_check_out: {
                //showCheckInForm(getBusinessForDisplay());
                checkOut(getBusinessForDisplay());
                break;
            }
//            case R.id.action_show_checked_in: {
//                setState((mState + 1) %2);
//                onStateChanged();
//                break;
//            }
            case android.R.id.home: {
                onBackPressed();
//                if(mState != STATE_NORMAL) {
//                    setState(STATE_NORMAL);
//                    rlt = true;
//                }else
//                {
                    rlt = super.onOptionsItemSelected(item);
//                }
                break;
            }
            default:
                rlt = super.onOptionsItemSelected(item);
        }
        return rlt;
    }

    private void checkOut(ModUser businessForDisplay) {
            if (checkIn != null){
                showProgressDialog(R.string.progress_removing);
                checkIn.deleteInBackground(new DeleteCallback() {
                    @Override
                    public void done(ParseException e) {
                        dismissProgressDialog();
                        showToast("Checked out...");
                        if (e == null){
                            ModCheckIn.removeSingle(checkIn);
                            mAdapterCheckedIn.removeSingleItem(checkIn);
                            isUserCheckedHere = false;
                            invalidateOptionsMenu();
                        }
                    }
                });
            }
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        MenuItem checkInItem = menu.findItem(R.id.action_check_in);
        MenuItem checkOut = menu.findItem(R.id.action_check_out);

        if (isUserCheckedHere == null){
            checkInItem.setVisible(false);
            checkOut.setVisible(false);
        }else
            if (isUserCheckedHere){
                checkInItem.setVisible(false);
                checkOut.setVisible(true);
            }else{
                checkInItem.setVisible(true);
                checkOut.setVisible(false);
            }

        return super.onPrepareOptionsMenu(menu);
    }

    protected void setupGui() {
        super.setupGui();

        mContainerCheckIn = (ViewGroup) findViewById(R.id.lContainerCheckIn);

        rvCheckedInItems = (RecyclerView) mContainerCheckIn.findViewById(R.id.rvCheckedInItems);
        circImgProfile = (CircularImageView) mContainerCheckIn.findViewById(R.id.circProfileImage);
        tvProfileName = (TextView) mContainerCheckIn.findViewById(R.id.tvProfileName);
        tvCheckedInInfo = (TextView) mContainerCheckIn.findViewById(R.id.tvCheckedInInfo);

        startCheckInQueryParse();

        mAdapterCheckedIn = new AdapterPersonalBusinessCheckedIn(null, this);
        rvCheckedInItems.setLayoutManager(new GridLayoutManager(this, 5, GridLayoutManager.VERTICAL, false));
        rvCheckedInItems.addItemDecoration(new SpacesItemDecoration(1));
        rvCheckedInItems.setAdapter(mAdapterCheckedIn);

        checkIn = ModCheckIn.getUserCheckedInThisBusinessCache(ModUser.getModUser(), getBusinessForDisplay());

//        if (checkIn != null)
//            setState(STATE_CHECK_IN);
//        else
//            setState(STATE_NORMAL);
    }

    private void startCheckInQueryParse() {
        showProgressDialog(getString(R.string.progress_loading_data));
        ModCheckIn.queryAllWhoAreCheckedInBusiness(getBusinessForDisplay()).findInBackground(new FindCallback<ModCheckIn>() {
            @Override
            public void done(List<ModCheckIn> objects, ParseException e) {
                dismissProgressDialog();
                if (e == null){
                    checkForCurrentUser(objects);
                    mAdapterCheckedIn.updateItems(removeHidden(objects));
                }else
                    ParseExceptionHandler.handleParseError(e, Activity_Personal_Business_Details.this);
            }
        });
    }

    private void checkForCurrentUser(List<ModCheckIn> objects) {
        isUserCheckedHere = false;
        for (ModCheckIn checkIn:objects)
            if (checkIn.getFrom().isEqual(ModUser.getModUser())) {
                this.checkIn = checkIn;
                isUserCheckedHere = true;
                break;
            }
        invalidateOptionsMenu();
    }

    private List<ModCheckIn> removeHidden(List<ModCheckIn> objects) {
        List ret = new ArrayList();
        for (ModCheckIn item:objects)
            if (item.getFrom().isHidden())
                continue;
            else
                ret.add(item);

        return ret;
    }

    @Override
    public void checkSignInRequest() {
        Bundle bundle = getIntent().getExtras();

        if (bundle.getBoolean(ModUser.EXTRA_SIGN_ME_IN, false))
            showCheckInForm(getBusinessForDisplay());

    }

    @Override
    public void showCheckInForm(ModUser mBusinessData) {
        removePreviousCheckIns(mBusinessData);
        //startUserCheckIn(mBusinessData);
        setState(STATE_CHECK_IN);
    }

    private void removePreviousCheckIns(final ModUser mBusinessData){
        ModCheckIn.queryAllWhereUserIsCheckedIn(ModUser.getModUser()).findInBackground(new FindCallback<ModCheckIn>() {
            @Override
            public void done(List<ModCheckIn> objects, ParseException e) {
                if (e == null){
                    boolean isHere = false;
                    for (ModCheckIn checkIn:objects) {

                        if (mBusinessData.isGeneratedFromYelp()){
                            if (checkIn.getToYelp() != null
                                    && checkIn.getToYelp().equals(mBusinessData.getYelpId())) {
                                isHere = true;
                                continue;
                            }
                            else{
                                checkIn.deleteEventually();
                                ModCheckIn.removeSingle(checkIn);
                            }
                        }else {
                            if (checkIn.getTo() != null && checkIn.getTo().isEqual(mBusinessData)) {
                                isHere = true;
                                continue;
                            }else {
                                checkIn.deleteInBackground();
                                ModCheckIn.removeSingle(checkIn);
                            }
                        }
                    }

                    if (!isHere)
                        startUserCheckIn(mBusinessData);

                }else
                    ParseExceptionHandler.handleParseError(e, Activity_Personal_Business_Details.this);

            }
        });
    }

    private void startUserCheckIn(ModUser mBusinessData) {
        if (ModCheckIn.getUserCheckedInThisBusinessCache(mBusinessData, getBusinessForDisplay()) != null)
            return;

        showProgressDialog(R.string.progress_saving);
        final ModCheckIn checkIn = new ModCheckIn();
        checkIn.setFrom(ModUser.getCurrentUser());

        if (mBusinessData.isGeneratedFromYelp())
            checkIn.setToYelp(mBusinessData.getYelpId());
        else
            checkIn.setTo(mBusinessData);

        checkIn.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                dismissProgressDialog();

                if (e == null){
                    showToast(getString(R.string.checkin_complete));
                    mAdapterCheckedIn.addSingleItem(checkIn);
                    ModCheckIn.addSingle(checkIn);
                    Activity_Personal_Business_Details.this.checkIn = checkIn;
                    isUserCheckedHere = true;
                    invalidateOptionsMenu();
                }else
                    ParseExceptionHandler.handleParseError(e, Activity_Personal_Business_Details.this);
            }

        });
    }

    private void dismissProgressCheckin() {
    }

    private void showCheckinProgress() {

    }

    private void setState(int pState) {
        mState = pState;
        onStateChanged();
    }

    @Override
    protected void onResume() {
        onStateChanged();
        super.onResume();
    }

    private void onStateChanged() {
        if (mContainerCheckIn == null)
            return;

        switch (mState) {
            case STATE_CHECK_IN: {
                mContainerCheckIn.setVisibility(View.VISIBLE);
                break;
            }
            case STATE_NORMAL:
            default:
                mContainerCheckIn.setVisibility(View.GONE);
        }

        invalidateOptionsMenu();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

//        if(mState != STATE_NORMAL) {
//            setState(STATE_NORMAL);
//        }else {
//            super.onBackPressed();
//        }
    }

    @Override
    public void onLoadBusinessCheckedInItem(AdapterPersonalBusinessCheckedIn.ViewHolderCheckedIn pViewHolder, ModCheckIn pProfileData) {
        //pViewHolder.tvName.setText(currentItem.getPersonalName());
        if (!pViewHolder.lastId.equals(pProfileData.getFrom().getObjectId()))
            pViewHolder.circProfileImage.setImageDrawable(getDrawableCheck(R.drawable.ic_image_user));

        pViewHolder.lastId = pProfileData.getFrom().getObjectId();
        fetchProfileImage(pProfileData.getFrom(), pViewHolder.circProfileImage);
    }

    @Override
    public void onBusinessCheckedInItemClick(ModCheckIn pProfileData) {
        if (ModUser.getModUser().isEqual(pProfileData.getFrom()))
            return;

        if (!ModUser.getModUser().getMessagesEnabled()){
            showAlertDialog(getString(R.string.alert_messaging_is_off));
            return;
        }

        getDialogsPrivate(pProfileData.getFrom().getChatId());

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
                NhLog.e("Ero", "" + errors.getErrors());
            }
        });

    }

    private void startChatActivity(QBDialog dialog, Integer id){
        Intent intent = new Intent(this, Activity_Messages_Single.class);

        if (dialog != null){
            Bundle bundle = new Bundle();
            bundle.putSerializable(IChat.EXTRA_DIALOG, dialog);
            intent.putExtras(bundle);
        }

        intent.putExtra(UtilChat.KEY_USER_CHAT_ID, id);
        startNextActivity(intent);
    }

}