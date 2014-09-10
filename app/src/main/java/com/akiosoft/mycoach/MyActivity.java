package com.akiosoft.mycoach;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.akiosoft.coachapi.server.dao.coachApi.CoachApi;
import com.akiosoft.coachapi.server.dao.coachApi.model.Sport;
import com.akiosoft.coachapi.server.dao.coachApi.model.SportCollection;
import com.akiosoft.mycoach.com.akiosoft.mycoach.dialog.DeleteConfirmationFragment;
import com.google.android.gms.auth.GoogleAuthException;
import com.google.android.gms.auth.GoogleAuthUtil;
import com.google.android.gms.common.AccountPicker;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.client.util.Strings;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


public class MyActivity extends ActionBarActivity implements DeleteConfirmationFragment.NoticeDialogListener {
    private static final String LOG_TAG = "MainActivity";
    private GreetingsDataAdapter mListAdapter;

    private static final int ACTIVITY_RESULT_FROM_ACCOUNT_SELECTION = 2222;

    private AuthorizationCheckTask mAuthTask;
    private String mEmailAccount = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my);

        // Prevent the keyboard from being visible upon startup.
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        ListView listView = (ListView) findViewById(R.id.greetings_list_view);
        mListAdapter = new GreetingsDataAdapter((Application) getApplication());
        listView.setAdapter(mListAdapter);
        listView.setLongClickable(true);
        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            public boolean onItemLongClick(AdapterView<?> parent, View v, int position, long id) {
                DeleteConfirmationFragment d = new DeleteConfirmationFragment();
//                Toast.makeText(MyActivity.this, ((Sport) parent.getItemAtPosition(position)).getName() + " was touched", Toast.LENGTH_SHORT).show();
                d.show(getSupportFragmentManager(), ((Sport) parent.getItemAtPosition(position)).getName());
                return true;
            }
        });
    }

    @Override
    public void onDialogDeleteClick(DialogFragment dialog) {
        Log.e(LOG_TAG, "Delete sport was selected");
//        Toast.makeText(MyActivity.this, "Delete was touched", Toast.LENGTH_SHORT).show();
        AsyncTask<String, Void, Void> getAndDisplayGreeting =
                new AsyncTask<String, Void, Void> () {
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
                        onClickGetAuthenticatedGreeting(null);

                    }
                };

        getAndDisplayGreeting.execute(dialog.getTag());
    }

    @Override
    public void onDialogCancelClick(DialogFragment dialog) {
        Toast.makeText(MyActivity.this, "Cancel was touched", Toast.LENGTH_SHORT).show();
    }

    /**
     * Simple use of an ArrayAdapter but we're using a static class to ensure no references to the
     * Activity exists.
     */
    static class GreetingsDataAdapter extends ArrayAdapter {
        GreetingsDataAdapter(Application application) {
            super(application.getApplicationContext(), android.R.layout.simple_list_item_1,
                    application.greetings);
        }

        void replaceData(List<Sport> greetings) {
            clear();
            for (Sport greeting : greetings) {
                add(greeting);
            }
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            TextView view = (TextView) super.getView(position, convertView, parent);
            view.setTextColor(Color.BLACK);

            Sport greeting = (Sport)this.getItem(position);

            StringBuilder sb = new StringBuilder();

//            Set<String> fields = greeting.keySet();
//            boolean firstLoop = true;
//            for (String fieldName : fields) {
//                // Append next line chars to 2.. loop runs.
//                if (firstLoop) {
//                    firstLoop = false;
//                } else {
//                    sb.append("\n");
//                }
//
//                sb.append(fieldName)
//                        .append(": ")
//                        .append(greeting.get("name"));
//            }

            view.setText(greeting.getName());
            return view;
        }
    }

    public void onClickGetGreeting(View view) {
        View rootView = view.getRootView();
        TextView greetingIdInputTV = (TextView)rootView.findViewById(R.id.greeting_id_edit_text);
        if (greetingIdInputTV.getText()==null ||
                Strings.isNullOrEmpty(greetingIdInputTV.getText().toString())) {
            Toast.makeText(this, "Input a Greeting ID", Toast.LENGTH_SHORT).show();
            return;
        };

        String sportName = greetingIdInputTV.getText().toString();
//        int greetingId = Integer.parseInt(greetingIdString);

        // Use of an anonymous class is done for sample code simplicity. {@code AsyncTasks} should be
        // static-inner or top-level classes to prevent memory leak issues.
        // @see http://goo.gl/fN1fuE @26:00 for a great explanation.
        AsyncTask<String, Void, Sport> getAndDisplayGreeting =
                new AsyncTask<String, Void, Sport> () {
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
                        if (sport!=null) {
                            sports.add(sport);
//                            displayGreetings(sports);
                            onClickGetAuthenticatedGreeting(null);
                        } else {
                            Log.e(LOG_TAG, "No sports were returned by the API.");
                        }
                    }
                };

        getAndDisplayGreeting.execute(sportName);
    }

    private void displayGreetings(List<Sport> greetings) {
        String msg;
        if (greetings==null || greetings.size() < 1) {
            msg = "Greeting was not present";
            Toast.makeText(this, msg, Toast.LENGTH_LONG).show();
        } else {
            Log.d(LOG_TAG, "Displaying " + greetings.size() + " greetings.");

//            List<Sport> greetingsList = greetings.Arrays.asList(greetings);
            mListAdapter.replaceData(greetings);
        }
    }

    public void performAuthCheck(String emailAccount) {
        // Cancel previously running tasks.
        if (mAuthTask != null) {
            try {
                mAuthTask.cancel(true);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        new AuthorizationCheckTask().execute(emailAccount);
    }

    class AuthorizationCheckTask extends AsyncTask<String, Integer, Boolean> {
        @Override
        protected Boolean doInBackground(String... emailAccounts) {
            Log.i(LOG_TAG, "Background task started.");

            if (!AppConstants.checkGooglePlayServicesAvailable(MyActivity.this)) {
                return false;
            }

            String emailAccount = emailAccounts[0];
            // Ensure only one task is running at a time.
            mAuthTask = this;

            // Ensure an email was selected.
            if (Strings.isNullOrEmpty(emailAccount)) {
                publishProgress(R.string.toast_no_google_account_selected);
                // Failure.
                return false;
            }

            Log.d(LOG_TAG, "Attempting to get AuthToken for account: " + mEmailAccount);

            try {
                // If the application has the appropriate access then a token will be retrieved, otherwise
                // an error will be thrown.
                GoogleAccountCredential credential = GoogleAccountCredential.usingAudience(
                        MyActivity.this, AppConstants.AUDIENCE);
                credential.setSelectedAccountName(emailAccount);

                String accessToken = credential.getToken();

                Log.d(LOG_TAG, "AccessToken retrieved");

                // Success.
                return true;
            } catch (GoogleAuthException unrecoverableException) {
                Log.e(LOG_TAG, "Exception checking OAuth2 authentication.", unrecoverableException);
                publishProgress(R.string.toast_exception_checking_authorization);
                // Failure.
                return false;
            } catch (IOException ioException) {
                Log.e(LOG_TAG, "Exception checking OAuth2 authentication.", ioException);
                publishProgress(R.string.toast_exception_checking_authorization);
                // Failure or cancel request.
                return false;
            }
        }

        @Override
        protected void onProgressUpdate(Integer... stringIds) {
            // Toast only the most recent.
            Integer stringId = stringIds[0];
            Toast.makeText(MyActivity.this, stringId, Toast.LENGTH_SHORT).show();
        }

        @Override
        protected void onPreExecute() {
            mAuthTask = this;
        }

        @Override
        protected void onPostExecute(Boolean success) {
            TextView emailAddressTV = (TextView) MyActivity.this.findViewById(R.id.email_address_tv);
            if (success) {
                // Authorization check successful, set internal variable.
                mEmailAccount = emailAddressTV.getText().toString();
            } else {
                // Authorization check unsuccessful, reset TextView to empty.
                emailAddressTV.setText("");
            }
            mAuthTask = null;
        }

        @Override
        protected void onCancelled() {
            mAuthTask = null;
        }
    }

    public void onClickSignIn(View view) {
        TextView emailAddressTV = (TextView) view.getRootView().findViewById(R.id.email_address_tv);
        // Check to see how many Google accounts are registered with the device.
        int googleAccounts = AppConstants.countGoogleAccounts(this);
        if (googleAccounts == 0) {
            // No accounts registered, nothing to do.
            Toast.makeText(this, R.string.toast_no_google_accounts_registered,
                    Toast.LENGTH_LONG).show();
        } else if (googleAccounts == 1) {
            // If only one account then select it.
            Toast.makeText(this, R.string.toast_only_one_google_account_registered,
                    Toast.LENGTH_LONG).show();
            AccountManager am = AccountManager.get(this);
            Account[] accounts = am.getAccountsByType(GoogleAuthUtil.GOOGLE_ACCOUNT_TYPE);
            if (accounts != null && accounts.length > 0) {
                // Select account and perform authorization check.
                emailAddressTV.setText(accounts[0].name);
                mEmailAccount = accounts[0].name;
                performAuthCheck(accounts[0].name);
            }
        } else {
            // More than one Google Account is present, a chooser is necessary.

            // Reset selected account.
            emailAddressTV.setText("");

            // Invoke an {@code Intent} to allow the user to select a Google account.
            Intent accountSelector = AccountPicker.newChooseAccountIntent(null, null,
                    new String[]{GoogleAuthUtil.GOOGLE_ACCOUNT_TYPE}, false,
                    "Select the account to access the HelloEndpoints API.", null, null, null);
            startActivityForResult(accountSelector,
                    ACTIVITY_RESULT_FROM_ACCOUNT_SELECTION);
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == ACTIVITY_RESULT_FROM_ACCOUNT_SELECTION && resultCode == RESULT_OK) {
            // This path indicates the account selection activity resulted in the user selecting a
            // Google account and clicking OK.

            // Set the selected account.
            String accountName = data.getStringExtra(AccountManager.KEY_ACCOUNT_NAME);
            TextView emailAccountTextView = (TextView)this.findViewById(R.id.email_address_tv);
            emailAccountTextView.setText(accountName);

            // Fire off the authorization check for this account and OAuth2 scopes.
            performAuthCheck(accountName);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mAuthTask!=null) {
            mAuthTask.cancel(true);
            mAuthTask = null;
        }
    }

    public void onClickGetAuthenticatedGreeting(View unused) {
        if (!isSignedIn()) {
            Toast.makeText(this, "You must sign in for this action.", Toast.LENGTH_LONG).show();
            return;
        }

        AsyncTask<Void, Void, SportCollection> getAuthedGreetingAndDisplay =
                new AsyncTask<Void, Void, SportCollection> () {
                    @Override
                    protected SportCollection doInBackground(Void... unused) {
                        if (!isSignedIn()) {
                            return null;
                        };

                        if (!AppConstants.checkGooglePlayServicesAvailable(MyActivity.this)) {
                            return null;
                        }

                        // Create a Google credential since this is an authenticated request to the API.
                        GoogleAccountCredential credential = GoogleAccountCredential.usingAudience(
                                MyActivity.this, AppConstants.AUDIENCE);
                        credential.setSelectedAccountName(mEmailAccount);

                        // Retrieve service handle using credential since this is an authenticated call.
                        CoachApi apiServiceHandle = AppConstants.getApiServiceHandle(credential);

                        try {
                            CoachApi.CoachAPI.GetAllSports getAuthedGreetingCommand = apiServiceHandle.coachAPI().getAllSports();
                            SportCollection greeting = getAuthedGreetingCommand.execute();
                            return greeting;
                        } catch (IOException e) {
                            Log.e(LOG_TAG, "Exception during API call", e);
                        }
                        return null;
                    }

                    @Override
                    protected void onPostExecute(SportCollection greeting) {
                        if (greeting!=null) {
                            displayGreetings(greeting.getItems());
                        } else {
                            Log.e(LOG_TAG, "No greetings were returned by the API.");
                        }
                    }
                };

        getAuthedGreetingAndDisplay.execute((Void)null);
    }

    private boolean isSignedIn() {
        if (!Strings.isNullOrEmpty(mEmailAccount)) {
            return true;
        } else {
            return false;
        }
    }
}
