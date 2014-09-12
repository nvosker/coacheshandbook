package com.akiosoft.mycoach.com.akiosoft.mycoach.dialog;

import android.view.LayoutInflater;
import android.view.View;

import com.akiosoft.mycoach.R;

/**
 * Created by nvoskeritchian on 9/11/14.
 */
public class CreateSportDialog extends AbstractCallbackDialog {

    @Override
    protected View getCustomView(LayoutInflater inflater) {
        return inflater.inflate(R.layout.fragment_create_sport,null);
    }

    @Override
    protected int getMessageId() {
        return R.string.dialog_save_confirmation;
    }

    @Override
    protected int getConfirmButtonTextId() {
        return R.string.save;
    }
}
