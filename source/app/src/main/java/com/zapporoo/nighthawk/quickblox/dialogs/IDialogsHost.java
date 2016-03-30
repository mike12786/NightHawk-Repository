package com.zapporoo.nighthawk.quickblox.dialogs;


import com.quickblox.chat.model.QBDialog;

import java.util.ArrayList;

/**
 * Created by Pile on 2/5/2016.
 */
public interface IDialogsHost {
    void showFetchedDialogues(ArrayList<QBDialog> dialogs);
    void showProgressDialogue();
    void dismissProgressDialogue();
}
