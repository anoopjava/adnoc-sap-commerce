package com.adnoc.service.exception;

/**
 * The type Adnoc registration exception.
 */
public class AdnocRegistrationException extends RuntimeException
{

    /**
     * Instantiates a new Adnoc registration exception.
     *
     * @param message the message
     */
    public AdnocRegistrationException(final String message)
    {
        super(message);
    }

    /**
     * Instantiates a new Adnoc registration exception.
     *
     * @param message the message
     * @param cause   the cause
     */
    public AdnocRegistrationException(final String message, final Throwable cause)
    {
        super(message, cause);
    }
}
