package com.zapporoo.nighthawk.quickblox.conversation;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;

import com.parse.GetCallback;
import com.parse.ParseException;
import com.quickblox.chat.model.QBChatMessage;
import com.zapporoo.nighthawk.R;
import com.zapporoo.nighthawk.model.ModUser;
import com.zapporoo.nighthawk.quickblox.util.ChatService;
import com.zapporoo.nighthawk.util.XxUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Pile on 12/15/2015.
 */
public class Fragment_Conversation_Group extends Fragment_Conversation_default {

    private static final String ARG_SECTION_NUMBER = "section_number";

    private ImageButton btnSendMessage;
    private EditText etMessage;
    List<Integer> listImageFetchingOpponentIds = new ArrayList<>();
    List<Integer> listUserFetchingOpponentIds = new ArrayList<>();

    public Fragment_Conversation_Group() {
    }

    public static Fragment_Conversation_Group newInstance(int sectionNumber) {
        Fragment_Conversation_Group fragment = new Fragment_Conversation_Group();
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
        if (listImageFetchingOpponentIds == null)
            listImageFetchingOpponentIds = new ArrayList<>();

        if (listUserFetchingOpponentIds == null)
            listUserFetchingOpponentIds = new ArrayList<>();

        Integer senderId = pMessage.getSenderId();

        AdapterChatConversations.ViewHolderMessage vhConverted = (AdapterChatConversations.ViewHolderMessage) vHolder;
        vhConverted.tvMessage.setText(pMessage.getBody());

        if (senderId == ModUser.getModUser().getChatId()) {
            fetchProfileImage(ModUser.getModUser(), ((AdapterChatConversations.ViewHolderMessage) vHolder).circProfileImage);
            return;
        }

        ModUser opponenUser = ModUser.getUserByChatIdFromCache(senderId);

        if (opponenUser != null) {
            if (listImageFetchingOpponentIds.contains(opponenUser))
                return;
            else{
                fetchProfileImage(opponenUser, ((AdapterChatConversations.ViewHolderMessage) vHolder).circProfileImage);
                listImageFetchingOpponentIds.add(senderId);
            }
        }else {
            if (listUserFetchingOpponentIds.contains(opponenUser))
                return;
            else {
                    ModUser.queryUserByChatId(pMessage.getSenderId()).getFirstInBackground(new GetCallback<ModUser>() {
                        @Override
                        public void done(ModUser object, ParseException e) {
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
    }


    @Override
    protected void onChatDialogLoaded() {
        Integer id = null;
        List<Integer> occupants = mDialog.getOccupants();
        //setMessagingAvailable(ModUser.getModUser());
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