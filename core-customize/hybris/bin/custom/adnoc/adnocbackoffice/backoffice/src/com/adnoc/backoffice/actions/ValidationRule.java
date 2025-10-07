package com.adnoc.backoffice.actions;

public class ValidationRule
{
    private final String regex;
    private final String message;

    public ValidationRule(String regex, String message)
    {
        this.regex = regex;
        this.message = message;
    }

    public String getRegex()
    {
        return regex;
    }

    public String getMessage()
    {
        return message;
    }
}
