package com.zapporoo.nighthawk.quickblox.dialogs;

import android.support.v7.widget.RecyclerView;

import com.quickblox.chat.model.QBDialog;

/**
 * Created by dare on 12.1.16..
 */
public interface IDialogItemCallback {

    void onOpenDialogItem(QBDialog pDialog);

    void onLoadDialogItem(RecyclerView.ViewHolder vHolder, QBDialog pDialog);

    void onDeleteDialogItem(QBDialog currentItem);
}
