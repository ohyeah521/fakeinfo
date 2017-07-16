package com.sch.fakecontacts.model.generator;

import android.graphics.Bitmap;
import android.support.annotation.Nullable;

import com.google.common.collect.LinkedHashMultimap;
import com.google.common.collect.Multimap;

import java.util.HashMap;
import java.util.Map;

public class Contact {
    private ContactName name;
    private Bitmap avatar;
    private final Multimap<EmailType, String> emails = LinkedHashMultimap.create();
    private final Multimap<PhoneType, String> phoneNumbers = LinkedHashMultimap.create();
    private final Map<PostalAddressType, PostalAddress> postalAddresses = new HashMap<>();
    private final Map<EventType, String> events = new HashMap<>();

    public ContactName getName() {
        return name;
    }

    public Contact setName(ContactName name) {
        this.name = name;
        return this;
    }

    @Nullable
    public Bitmap getAvatar() {
        return avatar;
    }

    public Contact setAvatar(Bitmap avatar) {
        this.avatar = avatar;
        return this;
    }

    public Multimap<EmailType, String> getEmails() {
        return emails;
    }

    public Contact addEmail(EmailType emailType, String email) {
        if (!emails.containsEntry(emailType, email)) {
            emails.put(emailType, email);
        }
        return this;
    }

    public Multimap<PhoneType, String> getPhoneNumbers() {
        return phoneNumbers;
    }

    public Contact addPhoneNumber(PhoneType phoneType, String phoneNumber) {
        if (!phoneNumbers.containsEntry(phoneType, phoneNumber)) {
            phoneNumbers.put(phoneType, phoneNumber);
        }
        return this;
    }

    public Map<PostalAddressType, PostalAddress> getPostalAddresses() {
        return postalAddresses;
    }

    public Contact addPostalAddress(PostalAddressType postalAddressType, PostalAddress postalAddress) {
        postalAddresses.put(postalAddressType, postalAddress);
        return this;
    }

    public Map<EventType, String> getEvents() {
        return events;
    }

    public Contact addEvent(EventType eventType, String startDate) {
        events.put(eventType, startDate);
        return this;
    }
}
