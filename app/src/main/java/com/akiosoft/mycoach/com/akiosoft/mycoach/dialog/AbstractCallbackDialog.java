package com.akiosoft.mycoach.com.akiosoft.mycoach.dialog;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;

import com.akiosoft.mycoach.R;

/**
 * Created by nvoskeritchian on 9/11/14.
 */
public abstract class AbstractCallbackDialog extends DialogFragment {

    public interface NoticeDialogListener {
        public void onDialogConfirmClick(DialogFragment dialog);
        public void onDialogCancelClick(DialogFragment dialog);
    }

    // Use this instance of the interface to deliver action events
    NoticeDialogListener mListener;

    // Override the Fragment.onAttach() method to instantiate the NoticeDialogListener
//    @Override
//    public void onAttach(Activity activity) {
//        super.onAttach(activity);
//        // Verify that the host activity implements the callback interface
//        try {
//            // Instantiate the NoticeDialogListener so we can send events to the host
//            mListener = (NoticeDialogListener) activity;
//        } catch (ClassCastException e) {
//            // The activity doesn't implement the interface, throw exception
//            throw new ClassCastException(activity.toString()
//                    + " must implement NoticeDialogListener");
//        }
//    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            mListener = (NoticeDialogListener) getTargetFragment();
        } catch (ClassCastException e) {
            throw new ClassCastException("Calling fragment must implement DialogClickListener interface");
        }
    }

    @Override
    public final Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the Builder class for convenient dialog construction
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        // Get the layout inflater
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = this.getCustomView(inflater);
        if(view != null) {
            builder.setView(view);
        }

        builder.setMessage(getMessageId())
                .setPositiveButton(getConfirmButtonTextId(), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        mListener.onDialogConfirmClick(AbstractCallbackDialog.this);
                    }
                })
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        mListener.onDialogCancelClick(AbstractCallbackDialog.this);
                    }
                });
        // Create the AlertDialog object and return it
        Dialog d = builder.create();
        return d;
    }

    protected abstract View getCustomView(LayoutInflater inflater);

    protected abstract int getMessageId();

    protected abstract int getConfirmButtonTextId();
}
