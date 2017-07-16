package com.sch.fakecontacts.ui.activities;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SwitchCompat;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.sch.fakecontacts.R;
import com.sch.fakecontacts.model.generator.ContactGenerator;
import com.sch.fakecontacts.model.generator.GenerationOptions;
import com.sch.fakecontacts.ui.dialogs.ProgressDialogController;

import permissions.dispatcher.NeedsPermission;
import permissions.dispatcher.RuntimePermissions;

@RuntimePermissions
public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private static final int REQUEST_CODE_SELECT_GROUP = 0;

    private static final String PREF_CONTACT_COUNT = "contact_count";
    private static final String PREF_WITH_EMAILS = "with_emails";
    private static final String PREF_WITH_PHONES = "with_phones";
    private static final String PREF_WITH_ADDRESSES = "with_addresses";
    private static final String PREF_WITH_AVATARS = "with_avatars";
    private static final String PREF_WITH_EVENTS = "with_events";
    private static final String PREF_OVERWRITE_EXISTING = "overwrite_existing";

    private SwitchCompat withEmailsView;
    private SwitchCompat withPhonesView;
    private SwitchCompat withAddressesView;
    private SwitchCompat withAvatarsView;
    private SwitchCompat withEventsView;
    private SwitchCompat overwriteExistingView;
    private EditText countView;
    private Button selectGroupButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        countView = (EditText) findViewById(R.id.edit_count);
        withEmailsView = (SwitchCompat) findViewById(R.id.switch_with_emails);
        withPhonesView = (SwitchCompat) findViewById(R.id.switch_with_phones);
        withAddressesView = (SwitchCompat) findViewById(R.id.switch_with_addresses);
        withAvatarsView = (SwitchCompat) findViewById(R.id.switch_with_avatars);
        withEventsView = (SwitchCompat) findViewById(R.id.switch_with_events);
        overwriteExistingView = (SwitchCompat) findViewById(R.id.switch_overwrite_existing);

        final Button generateButton = (Button) findViewById(R.id.button_generate);
        generateButton.setOnClickListener(this);

        selectGroupButton = (Button) findViewById(R.id.button_select_group);
        selectGroupButton.setOnClickListener(this);

        restorePersistentState();
    }

    @Override
    protected void onStop() {
        super.onStop();
        savePersistentState();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int i = item.getItemId();
        if (i == R.id.action_open_contacts) {
            openContactsApp();
            return true;
        } else if (i == R.id.action_delete_generated_contacts) {
            MainActivityPermissionsDispatcher.deleteGeneratedContactsWithCheck(this);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case REQUEST_CODE_SELECT_GROUP:
                if (resultCode == RESULT_OK) {
                    final long groupId = data.getLongExtra(GroupsActivity.EXTRA_GROUP_ID, 0);
                    final String groupName = data.getStringExtra(GroupsActivity.EXTRA_GROUP_NAME);
                    selectGroupButton.setText(groupName);
                    selectGroupButton.setTag(groupId);
                }
                break;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        MainActivityPermissionsDispatcher.onRequestPermissionsResult(this, requestCode, grantResults);
    }

    @Override
    public void onClick(View v) {
        int i = v.getId();
        if (i == R.id.button_select_group) {
            MainActivityPermissionsDispatcher.selectGroupWithCheck(this);

        } else if (i == R.id.button_generate) {
            if (validateParams()) {
                MainActivityPermissionsDispatcher.generateContactsWithCheck(this);
            }

        }
    }

    private void openContactsApp() {
        final Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("content://contacts/people/"));
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    private int getContactCount() {
        return countView.length() > 0 ? Integer.parseInt(countView.getText().toString()) : 0;
    }

    private boolean validateParams() {
        return countView.length() > 0;
    }

    @NeedsPermission(Manifest.permission.READ_CONTACTS)
    void selectGroup() {
        GroupsActivity.startForResult(this, REQUEST_CODE_SELECT_GROUP);
    }

    @NeedsPermission(Manifest.permission.WRITE_CONTACTS)
    void generateContacts() {
        final Long groupId = (Long) selectGroupButton.getTag();
        final GenerationOptions.Builder builder = new GenerationOptions.Builder()
                .setContactCount(getContactCount())
                .setOverwriteExisting(overwriteExistingView.isChecked());

        if (groupId != null) {
            builder.setGroupId(groupId);
        }
        if (withEmailsView.isChecked()) {
            builder.withEmails();
        }
        if (withPhonesView.isChecked()) {
            builder.withPhones();
        }
        if (withAddressesView.isChecked()) {
            builder.withAddresses();
        }
        if (withAvatarsView.isChecked()) {
            builder.withAvatars();
        }
        if (withEventsView.isChecked()) {
            builder.withEvents();
        }
        new GenerateContactsTask(this, builder.build()).execute();
    }

    @NeedsPermission(Manifest.permission.WRITE_CONTACTS)
    void deleteGeneratedContacts() {
        new DeleteGeneratedContactsTask(this).execute();
    }

    private void restorePersistentState() {
        final int contactCount = getPreferences().getInt(PREF_CONTACT_COUNT, 100);
        countView.setText(String.valueOf(contactCount));

        withEmailsView.setChecked(getPreferences().getBoolean(PREF_WITH_EMAILS, true));
        withPhonesView.setChecked(getPreferences().getBoolean(PREF_WITH_PHONES, true));
        withAddressesView.setChecked(getPreferences().getBoolean(PREF_WITH_ADDRESSES, false));
        withAvatarsView.setChecked(getPreferences().getBoolean(PREF_WITH_AVATARS, false));
        withEventsView.setChecked(getPreferences().getBoolean(PREF_WITH_EVENTS, true));

        overwriteExistingView.setChecked(getPreferences().getBoolean(PREF_OVERWRITE_EXISTING, true));
    }

    private void savePersistentState() {
        getPreferences().edit()
                .putInt(PREF_CONTACT_COUNT, getContactCount())
                .putBoolean(PREF_WITH_EMAILS, withEmailsView.isChecked())
                .putBoolean(PREF_WITH_PHONES, withPhonesView.isChecked())
                .putBoolean(PREF_WITH_ADDRESSES, withAddressesView.isChecked())
                .putBoolean(PREF_WITH_AVATARS, withAvatarsView.isChecked())
                .putBoolean(PREF_WITH_EVENTS, withEventsView.isChecked())
                .putBoolean(PREF_OVERWRITE_EXISTING, overwriteExistingView.isChecked())
                .apply();
    }

    private SharedPreferences getPreferences() {
        return PreferenceManager.getDefaultSharedPreferences(this);
    }

    private static class GenerateContactsTask extends AsyncTask<Void, Void, Void> {
        private final Context context;
        private final GenerationOptions options;
        private final ProgressDialogController progressDialogController;

        GenerateContactsTask(FragmentActivity activity, GenerationOptions options) {
            this.context = activity;
            this.options = options;
            progressDialogController = new ProgressDialogController(activity, activity.getSupportFragmentManager());
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialogController.show(R.string.message_please_wait);
        }

        @Override
        protected Void doInBackground(Void... params) {
            new ContactGenerator(context).generate(options);
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            progressDialogController.dismiss();
            Toast.makeText(context, R.string.message_done, Toast.LENGTH_SHORT).show();
        }
    }

    private static class DeleteGeneratedContactsTask extends AsyncTask<Void, Void, Void> {
        private final Context context;
        private final ProgressDialogController progressDialogController;

        DeleteGeneratedContactsTask(FragmentActivity activity) {
            context = activity;
            progressDialogController = new ProgressDialogController(activity, activity.getSupportFragmentManager());
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialogController.show(R.string.message_please_wait);
        }

        @Override
        protected Void doInBackground(Void... voids) {
            new ContactGenerator(context).deleteGeneratedContacts();
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            progressDialogController.dismiss();
            Toast.makeText(context, R.string.message_done, Toast.LENGTH_SHORT).show();
        }
    }
}
