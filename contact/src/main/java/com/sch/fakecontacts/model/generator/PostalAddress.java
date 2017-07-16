package com.sch.fakecontacts.model.generator;

import android.support.annotation.Nullable;

import com.google.auto.value.AutoValue;

@AutoValue
public abstract class PostalAddress {
    public abstract String country();

    public abstract String city();

    @Nullable
    public abstract String region();

    public abstract String street();

    @Nullable
    public abstract String postcode();

    public static Builder builder() {
        return new AutoValue_PostalAddress.Builder();
    }

    @AutoValue.Builder
    public static abstract class Builder {
        public abstract Builder setCountry(String country);

        public abstract Builder setCity(String city);

        public abstract Builder setRegion(String region);

        public abstract Builder setStreet(String street);

        public abstract Builder setPostcode(String postcode);

        public abstract PostalAddress build();
    }
}
