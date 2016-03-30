package com.zapporoo.nighthawk.ui.fragments;

import android.app.Activity;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;

import com.zapporoo.nighthawk.R;
import com.zapporoo.nighthawk.ui.activities.Activity_Login;

public class Fragment_DialogForgotPassword extends DialogFragment {
    int mNum;
    
    /**
     * Create a new instance of MyDialogFragment, providing "num"
     * as an argument.
     */
    public static Fragment_DialogForgotPassword newInstance(int num) {
    	Fragment_DialogForgotPassword f = new Fragment_DialogForgotPassword();

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
    	
        View view = inflater.inflate(R.layout.dialog_password_reset, container, false);
        //container.setBackground(null);
        final EditText etEmail = (EditText) view.findViewById(R.id.etEmailPasswordReset);
        Button btnCancel = (Button) view.findViewById(R.id.btnCancelPasswordReset);
        btnCancel.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
        
        Button btnSubmit = (Button) view.findViewById(R.id.btnSubmitPasswordReset);
        btnSubmit.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Activity_Login activity = (Activity_Login) getActivity();
				if (activity != null && validateEmail()){
					activity.forgotPassword(etEmail.getText().toString());
				}
				
			}

			private boolean validateEmail() {
				if (!android.util.Patterns.EMAIL_ADDRESS.matcher(etEmail.getText().toString()).matches()) {
					etEmail.setError("Invalid email");
					return false;
				}
				return true;
			}
		});
        
        
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
    
    
}