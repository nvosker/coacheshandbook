package com.akiosoft.mycoach.com.akiosoft.mycoach;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.AlertDialog;
import android.app.DialogFragment;
import android.app.Fragment;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
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
import com.akiosoft.mycoach.com.akiosoft.mycoach.dialog.AbstractCallbackDialog;
import com.akiosoft.mycoach.com.akiosoft.mycoach.dialog.CreateSportDialog;
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
 * Created by nvoskeritchian on 9/11/14.
 */
public class SportsFragment extends AbstractModelFragment<Sport> {

    public SportsFragment() {
    }

    @Override
    protected boolean onNewActionItemSelected() {
        Log.d(LOG_TAG, "New action item selected");
        CreateSportDialog d = new CreateSportDialog();
        d.setTargetFragment(SportsFragment.this, 0);
        d.show(getFragmentManager(), "NewSport");
        return true;
    }

    @Override
    public void onDialogConfirmClick(DialogFragment dialog) {
        super.onDialogConfirmClick(dialog);
        if (dialog instanceof CreateSportDialog && selectedItem !=null) {
            Log.e(LOG_TAG, "Delete sport was selected");

            this.createNewSport(dialog.getDialog().findViewById(R.id.sport_name_edit_text));

        }
    }


    @Override
    protected void onDeleteItemConfirmed(Sport item) {
        Log.e(LOG_TAG, "Delete sport was selected");
//        Toast.makeText(MyActivity.this, "Delete was touched", Toast.LENGTH_SHORT).show();
        AsyncTask<String, Void, Void> getAndDisplayGreeting =
                new AsyncTask<String, Void, Void>() {
                    @Override
                    protected Void doInBackground(String... sportName) {
                        // Retrieve service handle.
                        CoachApi apiServiceHandle = AppConstants.getApiServiceHandle(null);

                        try {
                            CoachApi.CoachAPI.DeleteSport getGreetingCommand = apiServiceHandle.coachAPI().deleteSport(sportName[0]);
                            getGreetingCommand.execute();
                        } catch (IOException e) {
                            Log.e(LOG_TAG, "Exception during API call", e);
                        }
                        return null;
                    }

                    @Override
                    protected void onPostExecute(Void sport) {
                        populateItemList(null);

                    }
                };

        getAndDisplayGreeting.execute(item.getName());

    }


    public void createNewSport(View view) {
        View rootView = view.getRootView();
        //REVERT
        TextView greetingIdInputTV = (TextView) view.findViewById(R.id.sport_name_edit_text);
        if (greetingIdInputTV.getText() == null ||
                Strings.isNullOrEmpty(greetingIdInputTV.getText().toString())) {
            Toast.makeText(getActivity(), "Input a Greeting ID", Toast.LENGTH_SHORT).show();
            return;
        }
        ;

        String sportName = greetingIdInputTV.getText().toString();


        // Use of an anonymous class is done for sample code simplicity. {@code AsyncTasks} should be
        // static-inner or top-level classes to prevent memory leak issues.
        // @see http://goo.gl/fN1fuE @26:00 for a great explanation.
        AsyncTask<String, Void, Sport> getAndDisplayGreeting =
                new AsyncTask<String, Void, Sport>() {
                    @Override
                    protected Sport doInBackground(String... sportName) {
                        // Retrieve service handle.
                        CoachApi apiServiceHandle = AppConstants.getApiServiceHandle(null);

                        try {
                            CoachApi.CoachAPI.CreateSport getGreetingCommand = apiServiceHandle.coachAPI().createSport(sportName[0]);
                            Sport sport = getGreetingCommand.execute();
                            return sport;
                        } catch (IOException e) {
                            Log.e(LOG_TAG, "Exception during API call", e);
                        }
                        return null;
                    }

                    @Override
                    protected void onPostExecute(Sport sport) {
                        List<Sport> sports = new ArrayList<Sport>();
                        if (sport != null) {
                            sports.add(sport);
                            populateItemList(null);
                        } else {
                            Log.e(LOG_TAG, "No sports were returned by the API.");
                        }
                    }
                };

        getAndDisplayGreeting.execute(sportName);
    }

}
