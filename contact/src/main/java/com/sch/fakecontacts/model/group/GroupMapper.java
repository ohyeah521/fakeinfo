package com.sch.fakecontacts.model.group;

import android.database.Cursor;
import android.provider.ContactsContract.Groups;

public class GroupMapper {
    public Group map(Cursor cursor) {
        final Group group = new Group();
        group.setId(cursor.getLong(cursor.getColumnIndexOrThrow(Groups._ID)));
        group.setTitle(cursor.getString(cursor.getColumnIndexOrThrow(Groups.TITLE)));
        return group;
    }
}
