package com.sch.fakecontacts.ui.dialogs;

import android.content.Context;
import android.content.DialogInterface;
import android.support.v4.app.DialogFragment;

public abstract class BaseDialogFragment extends DialogFragment {
    private OnCancelDialogListener onCancelDialogListener;
    private OnDismissDialogListener onDismissDialogListener;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (getParentFragment() instanceof OnCancelDialogListener) {
            onCancelDialogListener = (OnCancelDialogListener) getParentFragment();
        } else if (getContext() instanceof OnCancelDialogListener) {
            onCancelDialogListener = (OnCancelDialogListener) getContext();
        }
        if (getParentFragment() instanceof OnDismissDialogListener) {
            onDismissDialogListener = (OnDismissDialogListener) getParentFragment();
        } else if (getContext() instanceof OnDismissDialogListener) {
            onDismissDialogListener = (OnDismissDialogListener) getContext();
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        onCancelDialogListener = null;
        onDismissDialogListener = null;
    }

    @Override
    public void onCancel(DialogInterface dialog) {
        super.onCancel(dialog);
        if (onCancelDialogListener != null) {
            onCancelDialogListener.onCancelDialog(this);
        }
    }

    @Override
    public void onDismiss(DialogInterface dialog) {
        super.onDismiss(dialog);
        if (onDismissDialogListener != null) {
            onDismissDialogListener.onDismissDialog(this);
        }
    }
}
