package com.zapporoo.nighthawk.callbacks;

import com.quickblox.chat.model.QBDialog;
import com.quickblox.core.QBEntityCallback;
import com.zapporoo.nighthawk.model.ModUser;

/**
 * Created by Pile on 2/4/2016.
 */
public interface IChatHostActivity {
    void getChatDialog(QBEntityCallback<QBDialog> callback);
    void setToolbarTitle(String title);
}
