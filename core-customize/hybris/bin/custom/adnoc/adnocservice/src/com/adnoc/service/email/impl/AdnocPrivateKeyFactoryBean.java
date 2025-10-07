package com.adnoc.service.email.impl;

import org.springframework.beans.factory.FactoryBean;

import java.io.InputStream;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.spec.PKCS8EncodedKeySpec;

public class AdnocPrivateKeyFactoryBean implements FactoryBean<PrivateKey>
{

    private String path;

    @Override
    public PrivateKey getObject() throws Exception
    {
        InputStream inputStream = getClass().getClassLoader().getResourceAsStream(path);
        if (inputStream == null)
        {
            throw new IllegalArgumentException("Could not find private key at path: " + path);
        }

        byte[] keyBytes = inputStream.readAllBytes(); // binary!
        inputStream.close();

        PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(keyBytes);
        KeyFactory kf = KeyFactory.getInstance("RSA");
        return kf.generatePrivate(keySpec);
    }

    @Override
    public Class<?> getObjectType()
    {
        return PrivateKey.class;
    }

    @Override
    public boolean isSingleton()
    {
        return true;
    }

    public String getPath()
    {
        return path;
    }

    public void setPath(final String path)
    {
        this.path = path;
    }
}
