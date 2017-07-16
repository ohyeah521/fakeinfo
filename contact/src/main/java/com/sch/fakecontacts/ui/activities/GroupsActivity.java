package com.sch.fakecontacts.ui.activities;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.ContactsContract.Groups;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.sch.fakecontacts.R;
import com.sch.fakecontacts.model.group.Group;
import com.sch.fakecontacts.model.group.GroupManager;
import com.sch.fakecontacts.ui.adapter.GroupsAdapter;
import com.sch.fakecontacts.ui.dialogs.InputDialogFragment;
import com.sch.fakecontacts.ui.dialogs.OnPositiveButtonClickListener;

public class GroupsActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor>,
        OnPositiveButtonClickListener {

    public static final String EXTRA_GROUP_ID = "group_id";
    public static final String EXTRA_GROUP_NAME = "group_name";

    private static final String DIALOG_CREATE_GROUP_TAG = "dialog_create_group";

    private GroupsAdapter adapter;

    public static void startForResult(Activity activity, int requestCode) {
        final Intent intent = new Intent(activity, GroupsActivity.class);
        activity.startActivityForResult(intent, requestCode);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_groups);

        final FloatingActionButton createGroupButton = (FloatingActionButton) findViewById(R.id.button_create_group);
        createGroupButton.setOnClickListener(v -> {
            final InputDialogFragment dialog = InputDialogFragment.newInstance(this, R.string.dialog_create_group);
            dialog.show(getSupportFragmentManager(), DIALOG_CREATE_GROUP_TAG);
        });

        final RecyclerView groupsView = (RecyclerView) findViewById(R.id.groups);
        groupsView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new GroupsAdapter(this, null);
        groupsView.setAdapter(adapter);

        final View emptyView = findViewById(R.id.empty);
        setEmptyView(groupsView, emptyView);

        getSupportLoaderManager().initLoader(0, null, this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.groups, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int i = item.getItemId();
        if (i == android.R.id.home) {
            finish();
            return true;
        } else if (i == R.id.action_confirm) {
            final Group selectedGroup = adapter.getSelectedGroup();
            if (selectedGroup != null) {
                final Intent result = new Intent();
                result.putExtra(EXTRA_GROUP_ID, selectedGroup.getId());
                result.putExtra(EXTRA_GROUP_NAME, selectedGroup.getTitle());
                setResult(RESULT_OK, result);
            }
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onPositiveButtonClick(DialogFragment dialog) {
        if (DIALOG_CREATE_GROUP_TAG.equals(dialog.getTag())) {
            final InputDialogFragment inputDialogFragment = (InputDialogFragment) dialog;
            final String groupName = inputDialogFragment.getInputText();
            if (!TextUtils.isEmpty(groupName)) {
                createGroup(groupName);
            }
        }
    }

    private void createGroup(String groupName) {
        new GroupManager(this).createGroup(groupName);
        getSupportLoaderManager().restartLoader(0, null, GroupsActivity.this);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new CursorLoader(this, Groups.CONTENT_URI, new String[]{
                Groups._ID,
                Groups.TITLE,
        }, null, null, null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        adapter.swapCursor(cursor);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        adapter.swapCursor(null);
    }

    private void setEmptyView(RecyclerView recyclerView, View emptyView) {
        final RecyclerView.Adapter adapter = recyclerView.getAdapter();
        if (adapter == null) {
            throw new IllegalStateException("RecyclerView has no adapter");
        }
        adapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onChanged() {
                final boolean isEmpty = adapter.getItemCount() == 0;
                recyclerView.setVisibility(isEmpty ? View.GONE : View.VISIBLE);
                emptyView.setVisibility(isEmpty ? View.VISIBLE : View.GONE);
            }
        });
    }
}
