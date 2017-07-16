package com.sch.fakecontacts.model.generator;

import android.support.annotation.Nullable;

import com.google.auto.value.AutoValue;

@AutoValue
public abstract class ContactName {
    public abstract String firstName();

    public abstract String lastName();

    @Nullable
    public abstract String middleName();

    public static ContactName create(String firstName, String lastName, @Nullable String middleName) {
        return new AutoValue_ContactName(firstName, lastName, middleName);
    }
}
