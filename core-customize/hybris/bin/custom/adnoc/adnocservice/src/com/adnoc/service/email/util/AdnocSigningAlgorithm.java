package com.adnoc.service.email.util;

public class AdnocSigningAlgorithm
{

    public static AdnocSigningAlgorithm SHA256withRSA = new AdnocSigningAlgorithm("rsa-sha256", "SHA256withRSA", "SHA-256");
    public static AdnocSigningAlgorithm SHA1withRSA = new AdnocSigningAlgorithm("rsa-sha1", "SHA1withRSA", "SHA-1");

    private final String rfc4871Notation;
    private final String javaSecNotation;
    private final String javaHashNotation;

    public AdnocSigningAlgorithm(String rfc4871Notation, String javaSecNotation, String javaHashNotation)
    {
        this.rfc4871Notation = rfc4871Notation;
        this.javaSecNotation = javaSecNotation;
        this.javaHashNotation = javaHashNotation;
    }

    public String getJavaHashNotation()
    {
        return javaHashNotation;
    }

    public String getJavaSecNotation()
    {
        return javaSecNotation;
    }

    public String getRfc4871Notation()
    {
        return rfc4871Notation;
    }
}
