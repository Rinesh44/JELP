package com.brilltech.jelp.utils;

import android.util.Patterns;

import com.google.i18n.phonenumbers.NumberParseException;
import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.google.i18n.phonenumbers.Phonenumber;

public class ValidationUtils {
    public static boolean isValidEmailPhone(String emailPhone) {
        if (emailPhone.contains("@")) {
            return !Patterns.EMAIL_ADDRESS.matcher(emailPhone).matches();
        }
        return !isValidPhone(emailPhone);
    }

    private static boolean isValidPhone(String phone) {
        try {
            PhoneNumberUtil phoneNumberUtil = PhoneNumberUtil.getInstance();
            Phonenumber.PhoneNumber phoneNumber = phoneNumberUtil.parse(phone, "NP");
            return phoneNumberUtil.isValidNumber(phoneNumber);
        } catch (NumberParseException e) {
            e.printStackTrace();
        }
        return false;
    }

    public static boolean isValidPassword(String password) {
        if (!password.isEmpty() && password.length() >= 6) {
            return true;
        }
        return false;
    }
}
