package com.sch.fakecontacts.ui.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.View;

public interface OnViewHolderClickListener<VH extends RecyclerView.ViewHolder> {
    void onViewHolderClick(VH holder, View view);
}
