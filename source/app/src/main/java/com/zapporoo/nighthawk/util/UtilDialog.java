package com.zapporoo.nighthawk.util;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.zapporoo.nighthawk.R;
import com.zapporoo.nighthawk.quickblox.dialogs.Fragment_Personal_Home_Dialogues;

/**
 * Created by Pile on 12/15/2015.
 */
public class UtilDialog {

    public static AlertDialog showConfirmDialog(Context context, String message, final View.OnClickListener positiveOnClickListener) {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);

        View alertView = LayoutInflater.from(context).inflate(R.layout.dialog_alert, null);
        TextView tvAlert = (TextView) alertView.findViewById(R.id.tvAlert);
        alertView.findViewById(R.id.llDialogButtons).setVisibility(View.VISIBLE);
        Button btnOk = (Button) alertView.findViewById(R.id.btnDialogOk);

        Button btnCancel = (Button) alertView.findViewById(R.id.btnDialogCancel);

        alertDialogBuilder.setCancelable(true)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setView(alertView);

        if (message != null && !message.isEmpty())
            tvAlert.setText(message);

        final AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.setCanceledOnTouchOutside(true);
        alertDialog.show();

        btnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                positiveOnClickListener.onClick(v);
                alertDialog.dismiss();

            }
        });
        btnCancel.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
            }
        });

        return alertDialog;
    }

    public static AlertDialog showOkDialog(Context context, String message, final View.OnClickListener positiveOnClickListener) {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);

        View alertView = LayoutInflater.from(context).inflate(R.layout.dialog_alert, null);
        TextView tvAlert = (TextView) alertView.findViewById(R.id.tvAlert);
        LinearLayout llButtons = (LinearLayout)alertView.findViewById(R.id.llDialogButtons);
        llButtons.setVisibility(View.VISIBLE);
        llButtons.setWeightSum(1f);
        Button btnOk = (Button) alertView.findViewById(R.id.btnDialogOk);
        btnOk.setText("Close");
        Button btnCancel = (Button) alertView.findViewById(R.id.btnDialogCancel);
        btnCancel.setVisibility(View.GONE);

        alertDialogBuilder.setCancelable(true)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setView(alertView);

        if (message != null && !message.isEmpty())
            tvAlert.setText(message);

        final AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.setCanceledOnTouchOutside(true);
        alertDialog.show();

        btnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (positiveOnClickListener != null)
                    positiveOnClickListener.onClick(v);
                alertDialog.dismiss();

            }
        });
        return alertDialog;
    }

    public static AlertDialog showAlertDialog(Context context, String title, String message) {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);

        View alertView = LayoutInflater.from(context).inflate(R.layout.dialog_alert, null);
        TextView tvAlert = (TextView) alertView.findViewById(R.id.tvAlert);
        alertDialogBuilder.setCancelable(true)
                //.setIcon(android.R.drawable.ic_dialog_alert)
                .setView(alertView);

        if (message != null && !message.isEmpty())
            tvAlert.setText(message);

        if (title != null && !title.isEmpty())
            alertDialogBuilder.setTitle(title);

        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.setCanceledOnTouchOutside(true);

        try {
            alertDialog.show();
        }catch (Exception e){
        }

        return alertDialog;
    }

    public static ProgressDialog createProgressDialogSpinning(Context context, String message) {
        ProgressDialog mProgressDialog = new ProgressDialog(context);
        try {
            mProgressDialog.show();
        } catch (WindowManager.BadTokenException e) {

        }
        if (message != null)
            mProgressDialog.setMessage(message);
        else {
            mProgressDialog.setContentView(R.layout.progress_dialog_spinning);
        }
        mProgressDialog.setIndeterminate(false);
        mProgressDialog.setMax(100);
        mProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        mProgressDialog.setCancelable(false);

        return mProgressDialog;
    }

    public static void showNoConnectionDialog(Context context) {
        showAlertDialog(context, "No Internet Conection!", "Please check if your internet connection is functional.");
    }

    public static void showNoConnectionDialog(Context context, boolean show_dialog) {
        if (show_dialog)
            showNoConnectionDialog(context);
    }
}
