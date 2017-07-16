package com.sch.fakecontacts.model.generator;

import android.content.ContentProviderOperation;
import android.content.Context;
import android.content.OperationApplicationException;
import android.graphics.Bitmap;
import android.os.RemoteException;
import android.provider.ContactsContract;
import android.provider.ContactsContract.CommonDataKinds.Email;
import android.provider.ContactsContract.CommonDataKinds.Event;
import android.provider.ContactsContract.CommonDataKinds.GroupMembership;
import android.provider.ContactsContract.CommonDataKinds.Phone;
import android.provider.ContactsContract.CommonDataKinds.Photo;
import android.provider.ContactsContract.CommonDataKinds.StructuredName;
import android.provider.ContactsContract.CommonDataKinds.StructuredPostal;
import android.provider.ContactsContract.Data;
import android.provider.ContactsContract.RawContacts;

import com.sch.fakecontacts.model.group.GroupManager;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ContactGenerator {
    private static final String ACCOUNT_NAME_PREFIX = "fake_account_";
    private static final int BATCH_SIZE = 100;

    private final Context context;
    private final GroupManager groupManager;
    private final RandomContactGenerator randomContactGenerator = new RandomContactGenerator()
            .withEmails(0, 3)
            .withEvents(0, 1)
            .withPhoneNumbers(1, 3);

    public ContactGenerator(Context context) {
        this.context = context;
        groupManager = new GroupManager(context);
    }

    public void generate(GenerationOptions options) {
        final ArrayList<ContentProviderOperation> ops = new ArrayList<>();

        if (options.overwriteExisting()) {
            ContentProviderOperation op = ContentProviderOperation.newDelete(RawContacts.CONTENT_URI)
                    .withSelection(RawContacts.ACCOUNT_NAME + " LIKE ?", new String[]{ACCOUNT_NAME_PREFIX + "%"})
                    .build();
            ops.add(op);
        }

        final long groupId = options.getGroupId() != -1 ? options.getGroupId() : groupManager.getDefaultGroupId();

        final int contactCount = options.getContactCount();
        final int batchCount = (int) Math.ceil(contactCount / (double) BATCH_SIZE);
        for (int batch = 0; batch < batchCount; batch++) {
            final int currentBatchSize = Math.min(BATCH_SIZE, contactCount - batch * BATCH_SIZE);
            for (int i = 0; i < currentBatchSize; i++) {
                createAccount(ops, batch * BATCH_SIZE + i, options.getAccountType(), groupId, options);
            }

            try {
                context.getContentResolver().applyBatch(ContactsContract.AUTHORITY, ops);
            } catch (RemoteException | OperationApplicationException e) {
                e.printStackTrace();
                break;
            }

            ops.clear();
        }
    }

    public void deleteGeneratedContacts() {
        context.getContentResolver().delete(RawContacts.CONTENT_URI, RawContacts.ACCOUNT_NAME + " LIKE ?",
                new String[]{ACCOUNT_NAME_PREFIX + "%"});
    }

    private void createAccount(List<ContentProviderOperation> ops, int id, String type, long groupId, GenerationOptions options) {
        final int rawContactIndex = ops.size();

        final Contact contact = randomContactGenerator.generate(id);

        ContentProviderOperation op = ContentProviderOperation.newInsert(RawContacts.CONTENT_URI)
                .withValue(RawContacts.ACCOUNT_NAME, ACCOUNT_NAME_PREFIX + id)
                .withValue(RawContacts.ACCOUNT_TYPE, type)
                .build();
        ops.add(op);

        if (contact.getName() != null) {
            final ContactName contactName = contact.getName();
            final ContentProviderOperation.Builder builder = ContentProviderOperation.newInsert(Data.CONTENT_URI)
                    .withValueBackReference(Data.RAW_CONTACT_ID, rawContactIndex)
                    .withValue(Data.MIMETYPE, StructuredName.CONTENT_ITEM_TYPE);
            if (contactName.firstName() != null) {
                builder.withValue(StructuredName.GIVEN_NAME, contactName.firstName());
            }
            if (contactName.lastName() != null) {
                builder.withValue(StructuredName.FAMILY_NAME, contactName.lastName());
            }
            if (contactName.middleName() != null) {
                builder.withValue(StructuredName.MIDDLE_NAME, contactName.middleName());
            }
            ops.add(builder.build());
        }

        if (options.withPhones()) {
            for (Map.Entry<PhoneType, String> entry : contact.getPhoneNumbers().entries()) {
                op = ContentProviderOperation.newInsert(Data.CONTENT_URI)
                        .withValueBackReference(Data.RAW_CONTACT_ID, rawContactIndex)
                        .withValue(Data.MIMETYPE, Phone.CONTENT_ITEM_TYPE)
                        .withValue(Phone.NUMBER, entry.getValue())
                        .withValue(Phone.TYPE, phoneTypeToInt(entry.getKey()))
                        .build();
                ops.add(op);
            }
        }

        if (options.withEmails()) {
            for (Map.Entry<EmailType, String> entry : contact.getEmails().entries()) {
                op = ContentProviderOperation.newInsert(Data.CONTENT_URI)
                        .withValueBackReference(Data.RAW_CONTACT_ID, rawContactIndex)
                        .withValue(Data.MIMETYPE, Email.CONTENT_ITEM_TYPE)
                        .withValue(Email.ADDRESS, entry.getValue())
                        .withValue(Email.TYPE, emailTypeToInt(entry.getKey()))
                        .build();
                ops.add(op);
            }
        }

        if (options.withAddresses()) {
            for (PostalAddressType postalAddressType : contact.getPostalAddresses().keySet()) {
                final PostalAddress postalAddress = contact.getPostalAddresses().get(postalAddressType);
                final ContentProviderOperation.Builder builder = ContentProviderOperation.newInsert(Data.CONTENT_URI)
                        .withValueBackReference(Data.RAW_CONTACT_ID, rawContactIndex)
                        .withValue(Data.MIMETYPE, StructuredPostal.CONTENT_ITEM_TYPE)
                        .withValue(StructuredPostal.TYPE, postalAddressTypeToInt(postalAddressType));
                if (postalAddress.country() != null) {
                    builder.withValue(StructuredPostal.COUNTRY, postalAddress.country());
                }
                if (postalAddress.city() != null) {
                    builder.withValue(StructuredPostal.CITY, postalAddress.city());
                }
                if (postalAddress.region() != null) {
                    builder.withValue(StructuredPostal.REGION, postalAddress.region());
                }
                if (postalAddress.street() != null) {
                    builder.withValue(StructuredPostal.STREET, postalAddress.street());
                }
                if (postalAddress.postcode() != null) {
                    builder.withValue(StructuredPostal.POSTCODE, postalAddress.postcode());
                }
                ops.add(builder.build());
            }
        }

        if (options.withAvatars() && contact.getAvatar() != null) {
            op = ContentProviderOperation.newInsert(Data.CONTENT_URI)
                    .withValueBackReference(Data.RAW_CONTACT_ID, rawContactIndex)
                    .withValue(Data.MIMETYPE, Photo.CONTENT_ITEM_TYPE)
                    .withValue(Photo.PHOTO, bitmapToByteArray(contact.getAvatar()))
                    .build();
            ops.add(op);
            contact.getAvatar().recycle();
        }

        if (options.withEvents()) {
            for (Map.Entry<EventType, String> entry : contact.getEvents().entrySet()) {
                op = ContentProviderOperation.newInsert(Data.CONTENT_URI)
                        .withValueBackReference(Data.RAW_CONTACT_ID, rawContactIndex)
                        .withValue(Data.MIMETYPE, Event.CONTENT_ITEM_TYPE)
                        .withValue(Event.TYPE, eventTypeToInt(entry.getKey()))
                        .withValue(Event.START_DATE, entry.getValue())
                        .build();
                ops.add(op);
            }
        }

        op = ContentProviderOperation.newInsert(Data.CONTENT_URI)
                .withValueBackReference(Data.RAW_CONTACT_ID, rawContactIndex)
                .withValue(Data.MIMETYPE, GroupMembership.CONTENT_ITEM_TYPE)
                .withValue(GroupMembership.GROUP_ROW_ID, groupId)
                .withYieldAllowed(true)
                .build();
        ops.add(op);
    }

    private byte[] bitmapToByteArray(Bitmap bitmap) {
        final ByteArrayOutputStream output = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 0, output);
        return output.toByteArray();
    }

    private int emailTypeToInt(EmailType emailType) {
        switch (emailType) {
            case Home:
                return Email.TYPE_HOME;
            case Work:
                return Email.TYPE_WORK;
            case Mobile:
                return Email.TYPE_MOBILE;
            case Other:
                return Email.TYPE_OTHER;
            default:
                throw new RuntimeException("Unhandled EmailType: " + emailType);
        }
    }

    private int eventTypeToInt(EventType eventType) {
        switch (eventType) {
            case Birthday:
                return Event.TYPE_BIRTHDAY;
            case Anniversary:
                return Event.TYPE_ANNIVERSARY;
            case Other:
                return Event.TYPE_OTHER;
            default:
                throw new RuntimeException("Unhandled EventType: " + eventType);
        }
    }

    private int phoneTypeToInt(PhoneType phoneType) {
        switch (phoneType) {
            case Home:
                return Phone.TYPE_HOME;
            case Work:
                return Phone.TYPE_WORK;
            case Mobile:
                return Phone.TYPE_MOBILE;
            case Other:
                return Phone.TYPE_OTHER;
            default:
                throw new RuntimeException("Unhandled PhoneType: " + phoneType);
        }
    }

    private int postalAddressTypeToInt(PostalAddressType postalAddressType) {
        switch (postalAddressType) {
            case Home:
                return StructuredPostal.TYPE_HOME;
            case Work:
                return StructuredPostal.TYPE_WORK;
            case Other:
                return StructuredPostal.TYPE_OTHER;
            default:
                throw new RuntimeException("Unhandled PostalAddressType: " + postalAddressType);
        }
    }
}
