package com.akiosoft.mycoach;

import android.app.DialogFragment;
import android.support.v7.app.ActionBarActivity;

import com.akiosoft.mycoach.com.akiosoft.mycoach.dialog.DeleteConfirmationFragment;

/**
 * Created by nvoskeritchian on 9/10/14.
 */
public class SportsActivity  extends ActionBarActivity implements DeleteConfirmationFragment.NoticeDialogListener{

    @Override
    public void onDialogDeleteClick(DialogFragment dialog) {

    }

    @Override
    public void onDialogCancelClick(DialogFragment dialog) {

    }
}
