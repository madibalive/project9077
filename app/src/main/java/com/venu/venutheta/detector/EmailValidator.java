package com.venu.venutheta.detector;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Madiba on 12/1/2016.
 */

public class EmailValidator {

    private Pattern emailPattern;
    private Pattern aliasPattern;
    private Matcher matcher;

    private static final String EMAIL_PATTERN =
            "[a-zA-Z0-9\\+\\.\\_\\%\\-\\+]{1,256}" +
                    "\\@" +
                    "[a-zA-Z0-9][a-zA-Z0-9\\-]{0,64}" +
                    "(" +
                    "\\." +
                    "[a-zA-Z0-9][a-zA-Z0-9\\-]{0,25}" +
                    ")+";

    private static final String ALIAS_PATTERN = "([^\\s]+(\\+([\\w])*@))";

    public EmailValidator() {
        emailPattern = Pattern.compile(EMAIL_PATTERN);
        aliasPattern = Pattern.compile(ALIAS_PATTERN);
    }

    public boolean isValidEmail(String email) {
        if (email == null) {
            return false;
        }

        matcher = emailPattern.matcher(email);
        return matcher.matches();
    }

    //TODO: Write tests
    public boolean isAliasEmail(String email) {
        return isValidEmail(email) && aliasPattern.matcher(email).find();
    }
}
