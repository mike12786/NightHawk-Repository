package com.zapporoo.nighthawk.quickblox.conversation;

import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;

import com.parse.GetCallback;
import com.parse.ParseException;
import com.quickblox.chat.model.QBChatMessage;
import com.zapporoo.nighthawk.R;
import com.zapporoo.nighthawk.model.ModUser;
import com.zapporoo.nighthawk.quickblox.util.ChatService;
import com.zapporoo.nighthawk.ui.activities.Activity_Messages_Single;
import com.zapporoo.nighthawk.util.XxUtil;

import java.util.List;

/**
 * Created by Pile on 12/15/2015.
 */
public class Fragment_Conversation_Single extends Fragment_Conversation_default {

    private static final String ARG_SECTION_NUMBER = "section_number";

    private ImageButton btnSendMessage;
    private EditText etMessage;
    private ModUser opponenUser;
    private boolean fethingInProgress = false;


    public Fragment_Conversation_Single() {
    }

    public static Fragment_Conversation_Single newInstance(int sectionNumber) {
        Fragment_Conversation_Single fragment = new Fragment_Conversation_Single();
        Bundle args = new Bundle();
        args.putInt(ARG_SECTION_NUMBER, sectionNumber);
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_messages_single, container, false);
        //mMessages = Test.createDummyMessages();
        etMessage = (EditText) rootView.findViewById(R.id.etMessage);

        btnSendMessage = (ImageButton) rootView.findViewById(R.id.imgBtnSendMessage);
        btnSendMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendMessageCheck();
            }
        });
        setupGuiListAndAdapter(rootView, ModUser.getModUser().getChatId());

        return rootView;
    }



    @Override
    protected void setupGuiSingleChat() {

    }

    @Override
    protected void setupGuiGroupChat() {

    }


    @Override
    protected void clearTextField() {
        etMessage.setText("");
    }

    private void sendMessageCheck() {
        if (etMessage.getText().toString().trim().isEmpty()){
            //TODO 
            return;
        }
        sendTextMessage(etMessage.getText().toString().trim());
    }


    //ICallbackMessageItem
    @Override
    public void onOpenMessageItem(QBChatMessage pMessage) {
        showToast(pMessage.getBody());
    }

    //ICallbackMessageItem
    @Override
    public void onLoadMessageItem(RecyclerView.ViewHolder vHolder, QBChatMessage pMessage) {
        AdapterChatConversations.ViewHolderMessage vhConverted = (AdapterChatConversations.ViewHolderMessage) vHolder;
        vhConverted.tvMessage.setText(pMessage.getBody());

        if (pMessage.getSenderId() == ModUser.getModUser().getChatId()) {
            fetchProfileImage(ModUser.getModUser(), ((AdapterChatConversations.ViewHolderMessage) vHolder).circProfileImage);
            return;
        }

        if (fethingInProgress)
            return;

        if (opponenUser == null)
            opponenUser = ModUser.getUserByChatIdFromCache(pMessage.getSenderId());

        if (opponenUser != null)
            fetchProfileImage(opponenUser, ((AdapterChatConversations.ViewHolderMessage) vHolder).circProfileImage);
        else {
            fethingInProgress = true;
            ModUser.queryUserByChatId(pMessage.getSenderId()).getFirstInBackground(new GetCallback<ModUser>() {
                @Override
                public void done(ModUser object, ParseException e) {
                    fethingInProgress = false;
                    if (e == null) {
                        if (object != null) {
                            ModUser.addSinglePrivateCache(object);
                            refreshAdapter();
                        }
                    } else {

                    }
                }
            });

        }
    }


    @Override
    protected void onChatDialogLoaded() {
        Integer id = null;
        List<Integer> occupants = mDialog.getOccupants();
        for (Integer i:occupants){
            if (i != ModUser.getModUser().getChatId()){
                id = i;
                break;
            }
        }

        ModUser user = null;//.getUserByChatIdFromCache(id);
        if (user == null){
            ModUser.queryUserByChatId(id).getFirstInBackground(new GetCallback<ModUser>() {
                @Override
                public void done(ModUser user, ParseException e) {
                    if (e == null){
                        if (user != null){
                            ((Activity_Messages_Single)getActivity()).setOnlineStatus(user.getMessagesEnabled());
                            ((Activity_Messages_Single)getActivity()).setOpponentUser(user);
                            ((Activity_Messages_Single)getActivity()).setTitleName(user.getPersonalName());
                            setMessagingAvailable(user);
                        }
                        else{
                            showToast("error, no user found");
                            ((Activity_Messages_Single)getActivity()).setOnlineStatus(false);
                        }

                    }else{
                        showToast("error fetching status");
                        ((Activity_Messages_Single)getActivity()).setOnlineStatus(false);
                    }

                }
            });
        }else
            ((Activity_Messages_Single)getActivity()).setOnlineStatus(user.getMessagesEnabled());


        super.onChatDialogLoaded();
    }

    private void setMessagingAvailable(ModUser user) {
        if (!user.getMessagesEnabled()) {
            etMessage.setEnabled(false);
            etMessage.setFocusable(false);
            btnSendMessage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showToast(getString(R.string.toast_user_disabled_messaging));
                }
            });
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        ChatService.getInstance().removeConnectionListener(chatConnectionListener);
    }

    @Override
    public void onResume() {
        //put settings for not showing push notifications when chatting with this person
        //UtilPush.putOpenChatUserId(this, String.valueOf(opponentID));
        super.onResume();
    }

    @Override
    public void onPause() {
        //UtilPush.putOpenChatUserId(this, String.valueOf(""));
        XxUtil.focusRemove(etMessage, getActivity());
        super.onPause();
    }



}