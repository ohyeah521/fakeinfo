package com.sch.fakecontacts.ui.adapter;

import android.database.Cursor;
import android.database.DataSetObserver;
import android.support.v7.widget.RecyclerView;

public abstract class CursorRecyclerViewAdapter<VH extends RecyclerView.ViewHolder> extends RecyclerView.Adapter<VH> {
    private final DataSetObserver dataSetObserver = new MyDataSetObserver();
    private Cursor cursor;
    private boolean dataValid;
    private int rowIdColumn;

    public CursorRecyclerViewAdapter(Cursor cursor) {
        setHasStableIds(true);
        swapCursor(cursor);
    }

    @Override
    public long getItemId(int position) {
        return getCursorAtPosition(position).getLong(rowIdColumn);
    }

    @Override
    public int getItemCount() {
        return dataValid ? cursor.getCount() : 0;
    }

    @Override
    public final void onBindViewHolder(VH holder, int position) {
        onBindViewHolder(holder, position, getCursorAtPosition(position));
    }

    public abstract void onBindViewHolder(VH holder, int position, Cursor cursor);

    public Cursor getCursor() {
        return cursor;
    }

    public void changeCursor(Cursor cursor) {
        final Cursor oldCursor = swapCursor(cursor);
        if (oldCursor != null) {
            oldCursor.close();
        }
    }

    public Cursor swapCursor(Cursor cursor) {
        if (this.cursor == cursor) {
            return null;
        }
        final Cursor oldCursor = this.cursor;
        if (oldCursor != null) {
            oldCursor.unregisterDataSetObserver(dataSetObserver);
        }
        this.cursor = cursor;
        if (cursor != null) {
            cursor.registerDataSetObserver(dataSetObserver);

            dataValid = true;
            rowIdColumn = cursor.getColumnIndexOrThrow("_id");
        } else {
            dataValid = false;
            rowIdColumn = -1;
        }

        notifyDataSetChanged();
        return oldCursor;
    }

    protected Cursor getCursorAtPosition(int position) {
        if (!dataValid) {
            throw new IllegalStateException("getItemId should only be called when cursor is valid");
        }
        if (!cursor.moveToPosition(position)) {
            throw new IllegalArgumentException("Couldn't move cursor to position " + position);
        }
        return cursor;
    }

    private class MyDataSetObserver extends DataSetObserver {
        @Override
        public void onChanged() {
            dataValid = true;
            notifyDataSetChanged();
        }

        @Override
        public void onInvalidated() {
            dataValid = false;
            notifyDataSetChanged();
        }
    }
}
