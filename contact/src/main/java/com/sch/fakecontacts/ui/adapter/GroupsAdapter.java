package com.sch.fakecontacts.ui.adapter;

import android.content.Context;
import android.database.Cursor;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.sch.fakecontacts.R;
import com.sch.fakecontacts.model.group.Group;
import com.sch.fakecontacts.model.group.GroupMapper;

public class GroupsAdapter extends CursorRecyclerViewAdapter<GroupsAdapter.ViewHolder>
        implements OnViewHolderClickListener<GroupsAdapter.ViewHolder> {

    private final LayoutInflater inflater;
    private final GroupMapper mapper = new GroupMapper();
    private int selectedPosition = -1;

    public GroupsAdapter(Context context, Cursor cursor) {
        super(cursor);
        inflater = LayoutInflater.from(context);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        final View view = inflater.inflate(R.layout.item_group, parent, false);
        return new ViewHolder(view, this);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position, Cursor cursor) {
        final Group group = mapper.map(cursor);
        holder.titleView.setText(group.getTitle());
        holder.itemView.setSelected(selectedPosition == position);
    }

    @Override
    public void onViewHolderClick(ViewHolder holder, View view) {
        final int clickedPosition = holder.getAdapterPosition();
        if (selectedPosition != clickedPosition) {
            notifyItemChanged(selectedPosition);
            notifyItemChanged(clickedPosition);
            selectedPosition = clickedPosition;
        }
    }

    public Group getSelectedGroup() {
        if (selectedPosition != -1) {
            final Cursor cursor = getCursorAtPosition(selectedPosition);
            return mapper.map(cursor);
        } else {
            return null;
        }
    }

    static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        final OnViewHolderClickListener<ViewHolder> listener;
        final TextView titleView;

        ViewHolder(View itemView, OnViewHolderClickListener<ViewHolder> listener) {
            super(itemView);
            this.listener = listener;

            titleView = (TextView) itemView.findViewById(R.id.title);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            listener.onViewHolderClick(this, v);
        }
    }
}
