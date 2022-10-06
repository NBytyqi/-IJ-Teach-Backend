package com.interjoin.teach.utils;

import java.util.regex.Pattern;

public class EmailValidatorUtil {
    private static final String emailPattern = "^(.+)@(\\S+)$";

    public static boolean patternMatches(String emailAddress) {
        return emailAddress != null && Pattern.compile(emailPattern)
                .matcher(emailAddress)
                .matches();
    }
}
