package com.sch.fakecontacts.ui.dialogs;

import android.content.Context;
import android.support.annotation.StringRes;
import android.support.v4.app.FragmentManager;

public class ProgressDialogController {
    private static final String DIALOG_TAG = ProgressDialogFragment.class.getName();

    private final Context context;
    private final FragmentManager fragmentManager;

    public ProgressDialogController(Context context, FragmentManager fragmentManager) {
        this.context = context;
        this.fragmentManager = fragmentManager;
    }

    public void show(@StringRes int messageId) {
        final ProgressDialogFragment fragment = (ProgressDialogFragment) fragmentManager.findFragmentByTag(DIALOG_TAG);
        if (fragment == null) {
            fragmentManager
                    .beginTransaction()
                    .add(ProgressDialogFragment.newInstance(context, messageId), DIALOG_TAG)
                    .commitAllowingStateLoss();
        }
    }

    public void dismiss() {
        final ProgressDialogFragment fragment = (ProgressDialogFragment) fragmentManager.findFragmentByTag(DIALOG_TAG);
        if (fragment != null) {
            fragment.dismissAllowingStateLoss();
        }
    }
}
