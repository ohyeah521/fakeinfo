package com.sch.fakecontacts.model.generator;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.text.TextPaint;

import com.sch.fakecontacts.ChineseName;

import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.threeten.bp.LocalDate;
import org.threeten.bp.YearMonth;
import org.threeten.bp.format.DateTimeFormatter;

import java.util.Random;

import cn.binarywang.tools.ChineseCharUtils;

public class RandomContactGenerator {
    private static final int AVATAR_SIZE_PX = 256;
    private static final DateTimeFormatter EVENT_DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    private final Random random = new Random();
    private final Paint textPaint = new TextPaint();
    private int minEmails;
    private int maxEmails;
    private int minEvents;
    private int maxEvents;
    private int minPhoneNumbers;
    private int maxPhoneNumbers;

    public RandomContactGenerator withEmails(int minEmails, int maxEmails) {
        this.minEmails = minEmails;
        this.maxEmails = maxEmails;
        return this;
    }

    public RandomContactGenerator withEvents(int minEvents, int maxEvents) {
        this.minEvents = minEvents;
        this.maxEvents = maxEvents;
        return this;
    }

    public RandomContactGenerator withPhoneNumbers(int minPhoneNumbers, int maxPhoneNumbers) {
        this.minPhoneNumbers = minPhoneNumbers;
        this.maxPhoneNumbers = maxPhoneNumbers;
        return this;
    }

    public Contact generate(int id) {
        final Contact contact = new Contact();
        final ContactName contactName = ContactName.create(ChineseName.getInstance().genFirstName(), ChineseCharUtils.genRandomLengthChineseChars(1, 2), null);
        contact.setName(contactName);
        contact.setAvatar(generateAvatar(AVATAR_SIZE_PX, AVATAR_SIZE_PX,
                (contactName.firstName().charAt(0) + "" + contactName.lastName().charAt(0)).toUpperCase()));
        if (maxEmails != 0) {
            final int numEmails = randomValueBetween(minEmails, maxEmails);
            for (int i = 0; i < numEmails; i++) {
                contact.addEmail(randomElementOf(EmailType.values()), generateEmail());
            }
        }
        if (maxEvents != 0) {
            final int numEvents = randomValueBetween(minEvents, maxEvents);
            for (int i = 0; i < numEvents; i++) {
                contact.addEvent(randomElementOf(EventType.values()), generateEventDate());
            }
        }
        if (maxPhoneNumbers != 0) {
            final int numPhoneNumbers = randomValueBetween(minPhoneNumbers, maxPhoneNumbers);
            for (int i = 0; i < numPhoneNumbers; i++) {
                contact.addPhoneNumber(randomElementOf(PhoneType.values()), generatePhoneNumber());
            }
        }
        contact.addPostalAddress(randomElementOf(PostalAddressType.values()), generatePostalAddress());
        return contact;
    }

    private String generatePhoneNumber() {
        return "+79" + RandomStringUtils.randomNumeric(9);
    }

    private String generateEmail() {
        return randomAlphabeticString(5, 10).toLowerCase() + "@" +
                randomAlphabeticString(4, 7).toLowerCase() + ".com";
    }

    private String generateEventDate() {
        final int year = randomValueBetween(1990, 2016);
        final int month = randomValueBetween(1, 12);
        final int day = randomValueBetween(1, YearMonth.of(year, month).lengthOfMonth());
        return LocalDate.of(year, month, day).format(EVENT_DATE_FORMATTER);
    }

    private PostalAddress generatePostalAddress() {
        final PostalAddress.Builder builder = PostalAddress.builder()
                .setCountry(StringUtils.capitalize(randomAlphabeticString(5, 9).toLowerCase()))
                .setCity(StringUtils.capitalize(randomAlphabeticString(4, 9).toLowerCase()))
                .setStreet(StringUtils.capitalize(randomAlphabeticString(10, 20).toLowerCase()));
        if (random.nextBoolean()) {
            builder.setRegion(StringUtils.capitalize(randomAlphabeticString(5, 9).toLowerCase()));
        }
        if (random.nextBoolean()) {
            builder.setPostcode(String.valueOf(random.nextInt(1000000)));
        }
        return builder.build();
    }

    private Bitmap generateAvatar(int width, int height, String initials) {
        final int red = random.nextInt(256);
        final int green = random.nextInt(256);
        final int blue = random.nextInt(256);
        final int bgColor = Color.rgb(red, green, blue);
        final int textColor;

        if (red * 299 + green * 587 + blue * 114 < 186000) {
            textColor = Color.WHITE;
        } else {
            textColor = Color.BLACK;
        }

        final Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565);
        final Canvas canvas = new Canvas(bitmap);
        canvas.drawColor(bgColor);

        textPaint.setColor(textColor);
        textPaint.setTextSize(width / 3);
        textPaint.setAntiAlias(true);

        final int x = (int) (canvas.getWidth() - textPaint.measureText(initials)) / 2;
        final int y = (int) (canvas.getHeight() / 2 - (textPaint.descent() + textPaint.ascent()) / 2);
        canvas.drawText(initials, x, y, textPaint);
        return bitmap;
    }

    private int randomValueBetween(int min, int max) {
        return random.nextInt(max - min + 1) + min;
    }

    @SuppressWarnings("unchecked")
    private <T> T randomElementOf(T... values) {
        return values[random.nextInt(values.length)];
    }

    private String randomAlphabeticString(int minLength, int maxLength) {
        final int length = minLength + random.nextInt(maxLength - minLength + 1);
        return RandomStringUtils.randomAlphabetic(length);
    }
}
