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
import com.google.api.client.json.GenericJson;
import com.google.api.client.util.Strings;

import java.io.IOException;
import java.util.List;

/**
 * Created by nvoskeritchian on 9/12/14.
 */
public abstract class AbstractModelFragment<T extends GenericJson> extends Fragment implements View.OnClickListener, AbstractCallbackDialog.NoticeDialogListener {

    protected final String LOG_TAG = this.getClass().getName();
    private static final int ACTIVITY_RESULT_FROM_ACCOUNT_SELECTION = 2222;
    private DataAdapter<T> mListAdapter;
    private AuthorizationCheckTask mAuthTask;
    private String mEmailAccount = "";
    protected T selectedItem;
    private ListView listView;

    public AbstractModelFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_sports, container, false);
        listView = (ListView) rootView.findViewById(R.id.model_list_view);
        mListAdapter = new DataAdapter<T>((Application) getActivity().getApplication()){

        };
        listView.setAdapter(mListAdapter);
        listView.setLongClickable(true);
        listView.setClickable(true);
        listView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            public boolean onItemLongClick(AdapterView<?> parent, View v, int position, long id) {
                DeleteConfirmationFragment d = new DeleteConfirmationFragment();
                d.setTargetFragment(AbstractModelFragment.this, 0);
                d.show(getFragmentManager(), (String)((T) parent.getItemAtPosition(position)).get("label"));
                return true;
            }
        });
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                selectedItem = (T) adapterView.getItemAtPosition(position);
            }
        });

        Button b = (Button) rootView.findViewById(R.id.sign_in_button);
        b.setOnClickListener(this);
//        b = (Button) rootView.findViewById(R.id.create_sport_button);
//        b.setOnClickListener(this);
//        b = (Button) rootView.findViewById(R.id.get_all_sport_button);
        b.setOnClickListener(this);

        this.onClickSignIn(rootView);
        return rootView;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.sports_action_items, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // handle item selection
        switch (item.getItemId()) {
            case R.id.new_item:
                Log.d(LOG_TAG, "New action item selected");
                this.onNewActionItemSelected();
                return true;
            case R.id.delete_item:
                Log.d(LOG_TAG, "Delete action item selected");
                if(selectedItem!=null) {
                    DeleteConfirmationFragment dc = new DeleteConfirmationFragment();
                    dc.setTargetFragment(AbstractModelFragment.this, 0);
                    dc.show(getFragmentManager(), (String)((GenericJson)selectedItem).get("label"));
                } else {
                    Toast.makeText(getActivity(), "You must select an item first.", Toast.LENGTH_LONG).show();
                }
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    protected abstract boolean onNewActionItemSelected();


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.sign_in_button:
                onClickSignIn(v);
                break;
//            case R.id.get_all_sport_button:
//                this.populateItemList(v);
//                break;
//            case R.id.create_sport_button:
//                this.createNewSport(v);
//                break;
        }
    }

    public void onClickSignIn(View view) {
        //REVERT
        TextView emailAddressTV = (TextView) view.getRootView().findViewById(R.id.email_address_tv);
        // Check to see how many Google accounts are registered with the device.
        int googleAccounts = AppConstants.countGoogleAccounts(getActivity());
        if (googleAccounts == 0) {
            // No accounts registered, nothing to do.
            Toast.makeText(getActivity(), R.string.toast_no_google_accounts_registered,
                    Toast.LENGTH_LONG).show();
        } else if (googleAccounts == 1) {
            // If only one account then select it.
            Toast.makeText(getActivity(), R.string.toast_only_one_google_account_registered,
                    Toast.LENGTH_LONG).show();
            AccountManager am = AccountManager.get(getActivity());
            Account[] accounts = am.getAccountsByType(GoogleAuthUtil.GOOGLE_ACCOUNT_TYPE);
            if (accounts != null && accounts.length > 0) {
                // Select account and perform authorization check.
                emailAddressTV.setText(accounts[0].name);
                mEmailAccount = accounts[0].name;
                performAuthCheck(accounts[0].name);

                this.populateItemList(view);
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

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        //REVERT
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == ACTIVITY_RESULT_FROM_ACCOUNT_SELECTION && resultCode == getActivity().RESULT_OK) {
            // This path indicates the account selection activity resulted in the user selecting a
            // Google account and clicking OK.

            // Set the selected account.
            String accountName = data.getStringExtra(AccountManager.KEY_ACCOUNT_NAME);
            TextView emailAccountTextView = (TextView) getActivity().findViewById(R.id.email_address_tv);
            emailAccountTextView.setText(accountName);

            // Fire off the authorization check for this account and OAuth2 scopes.
            performAuthCheck(accountName);
        }
    }


    public void populateItemList(View unused) {
        if (!isSignedIn()) {
            Toast.makeText(getActivity(), "You must sign in for this action.", Toast.LENGTH_LONG).show();
            return;
        }

        AsyncTask<Void, Void, List<T>> getAuthedSportList =
                new AsyncTask<Void, Void, List<T>>() {
                    @Override
                    protected List<T> doInBackground(Void... unused) {
                        if (!isSignedIn()) {
                            return null;
                        }
                        ;

                        if (!AppConstants.checkGooglePlayServicesAvailable(getActivity())) {
                            return null;
                        }

                        // Create a Google credential since this is an authenticated request to the API.
                        GoogleAccountCredential credential = GoogleAccountCredential.usingAudience(
                                getActivity(), AppConstants.AUDIENCE);
                        credential.setSelectedAccountName(mEmailAccount);

                        // Retrieve service handle using credential since this is an authenticated call.
                        CoachApi apiServiceHandle = AppConstants.getApiServiceHandle(credential);

                        try {
                            CoachApi.CoachAPI.GetAllSports getAuthedGreetingCommand = apiServiceHandle.coachAPI().getAllSports();
                            GenericJson greeting = getAuthedGreetingCommand.execute();
                            return (List<T>)greeting.get("items");
                        } catch (IOException e) {
                            Log.e(LOG_TAG, "Exception during API call", e);
                        }
                        return null;
                    }

                    @Override
                    protected void onPostExecute(List<T> greeting) {
                        if (greeting != null) {
                            displayItems(greeting);
                        } else {
                            Log.e(LOG_TAG, "No greetings were returned by the API.");
                        }
                    }
                };

        getAuthedSportList.execute((Void) null);
    }

    private void displayItems(List<T> greetings) {
        String msg;
        if (greetings == null || greetings.size() < 1) {
            msg = "Greeting was not present";
            Toast.makeText(getActivity(), msg, Toast.LENGTH_LONG).show();
        } else {
            Log.d(LOG_TAG, "Displaying " + greetings.size() + " greetings.");

//            List<Sport> greetingsList = greetings.Arrays.asList(greetings);
            mListAdapter.replaceData(greetings);
        }
    }

    protected abstract void onDeleteItemConfirmed(T item);

    @Override
    public void onDialogConfirmClick(DialogFragment dialog) {
        if (dialog instanceof DeleteConfirmationFragment && selectedItem !=null) {
            Log.e(LOG_TAG, "Delete sport was selected");

            this.onDeleteItemConfirmed(selectedItem);
            listView.clearChoices();
            selectedItem = null;

        }
    }

    @Override
    public void onDialogCancelClick(DialogFragment dialog) {
        Toast.makeText(getActivity(), "Cancel was touched", Toast.LENGTH_SHORT).show();
    }

    private boolean isSignedIn() {
        if (!Strings.isNullOrEmpty(mEmailAccount)) {
            return true;
        } else {
            return false;
        }
    }


    class AuthorizationCheckTask extends AsyncTask<String, Integer, Boolean> {
        @Override
        protected Boolean doInBackground(String... emailAccounts) {
            Log.i(LOG_TAG, "Background task started.");

            if (!AppConstants.checkGooglePlayServicesAvailable(getActivity())) {
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
                        getActivity(), AppConstants.AUDIENCE);
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
            Toast.makeText(getActivity(), stringId, Toast.LENGTH_SHORT).show();
        }

        @Override
        protected void onPreExecute() {
            mAuthTask = this;
        }

        @Override
        protected void onPostExecute(Boolean success) {
            //REVERT
            TextView emailAddressTV = (TextView) getActivity().findViewById(R.id.email_address_tv);
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
}
