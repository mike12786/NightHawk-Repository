package com.zapporoo.nighthawk.ui.fragments;

import android.app.Activity;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.LinearLayout;


import com.zapporoo.nighthawk.ui.dialogs.DialogOptionItem;

import java.util.ArrayList;
import java.util.List;
import com.zapporoo.nighthawk.R;

public class Fragment_Dialog_options extends DialogFragment {
    int mNum;
	Bitmap bmp;
	
	List<DialogOptionItem> mListOptionItems;

    /**
     * Create a new instance of MyDialogFragment, providing "num"
     * as an argument.
     */
    public static Fragment_Dialog_options newInstance(int num) {
        Fragment_Dialog_options f = new Fragment_Dialog_options();

        // Supply num input as an argument.
        Bundle args = new Bundle();
        args.putInt("num", num);
        f.setArguments(args);

        return f;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mNum = getArguments().getInt("num");
        
    }
    
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
      Dialog dialog = super.onCreateDialog(savedInstanceState);

      // request a window without the title
      dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
      return dialog;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    	
        LinearLayout view = (LinearLayout) inflater.inflate(R.layout.dialog_option_wrap, container, false);
        getDialog().setTitle("DIALOG TITLE!");
        
        for (DialogOptionItem item: mListOptionItems){
        	view.addView(item.getView());
        }
        return view;
    }
    
    public void showDialog(Activity context) {

	    // DialogFragment.show() will take care of adding the fragment
	    // in a transaction.  We also want to remove any currently showing
	    // dialog, so make our own transaction and take care of that here.
	    FragmentTransaction ft = context.getFragmentManager().beginTransaction();
	    Fragment prev = context.getFragmentManager().findFragmentByTag("dialog");
	    if (prev != null) {
	        ft.remove(prev);
	    }
	    ft.addToBackStack(null);

	    // Create and show the dialog.
	    //FragmentDialog newFragment = FragmentDialog.newInstance(count);
	    show(ft, "dialog");
	}
    
    public void addItem(DialogOptionItem item){
    	if (mListOptionItems == null)
    		mListOptionItems = new ArrayList<DialogOptionItem>();
    	mListOptionItems.add(item);
    }
    
}