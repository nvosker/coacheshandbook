package com.akiosoft.mycoach.com.akiosoft.mycoach;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.Activity;
import android.app.DialogFragment;
import android.app.Fragment;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.akiosoft.coachapi.server.dao.coachApi.CoachApi;
import com.akiosoft.coachapi.server.dao.coachApi.model.Sport;
import com.akiosoft.coachapi.server.dao.coachApi.model.SportCollection;
import com.akiosoft.mycoach.AppConstants;
import com.akiosoft.mycoach.Application;
import com.akiosoft.mycoach.R;
import com.akiosoft.mycoach.com.akiosoft.mycoach.dialog.DeleteConfirmationFragment;
import com.google.android.gms.auth.GoogleAuthException;
import com.google.android.gms.auth.GoogleAuthUtil;
import com.google.android.gms.common.AccountPicker;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.client.util.Strings;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by nvoskeritchian on 9/10/14.
 */
public class HomeFragment extends Fragment {

    public HomeFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_home, container, false);

        return rootView;
    }

}
