package com.akiosoft.mycoach.com.akiosoft.mycoach.dialog;

import android.view.LayoutInflater;
import android.view.View;

import com.akiosoft.mycoach.R;

/**
 * Created by nvoskeritchian on 9/10/14.
 */
public class DeleteConfirmationFragment extends AbstractCallbackDialog {

    @Override
    protected View getCustomView(LayoutInflater inflater) {
        return null;
    }

    @Override
    protected int getMessageId() {
        return R.string.dialog_delete_confirmation;
    }

    @Override
    protected int getConfirmButtonTextId() {
        return R.string.delete;
    }
}
