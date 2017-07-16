package com.sch.fakecontacts.model.group;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.ContactsContract;
import android.provider.ContactsContract.Groups;

public class GroupManager {
    private final Context context;

    public GroupManager(Context context) {
        this.context = context;
    }

    public long createGroup(String groupName) {
        final ContentValues values = new ContentValues();
        values.put(Groups.TITLE, groupName);
        values.put(Groups.GROUP_VISIBLE, 1);
        final Uri uri = context.getContentResolver().insert(Groups.CONTENT_URI, values);
        return ContentUris.parseId(uri);
    }

    public void deleteGroup(long groupId) {
        final Uri uri = ContentUris.withAppendedId(Groups.CONTENT_URI, groupId)
                .buildUpon().appendQueryParameter(ContactsContract.CALLER_IS_SYNCADAPTER, "true")
                .build();

        context.getContentResolver().delete(uri, null, null);
    }

    public long getDefaultGroupId() {
        try (Cursor cursor = context.getContentResolver().query(Groups.CONTENT_URI,
                new String[]{Groups._ID, Groups.TITLE}, Groups.GROUP_VISIBLE + "=?", new String[]{"1"}, null)) {

            if (cursor != null && cursor.moveToFirst()) {
                return cursor.getLong(cursor.getColumnIndexOrThrow(Groups._ID));
            } else {
                return createGroup("Default group");
            }
        }
    }
}
