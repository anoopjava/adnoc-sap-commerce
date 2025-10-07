package com.adnoc.service.exception;

/**
 * Custom exception class for handling errors specific to Adnoc S4 Hana integration.
 */
public class AdnocS4HanaException extends RuntimeException
{
    private static final long serialVersionUID = 1L;
    /**
     * Default constructor.
     */
    public AdnocS4HanaException() {
        super();
    }

    /**
     * Constructor with an error message.
     *
     * @param message the error message to be displayed.
     */
    public AdnocS4HanaException(String message) {
        super(message);
    }

    /**
     * Constructor with an error message and a cause.
     *
     * @param message the error message to be displayed.
     * @param cause the underlying cause of the exception.
     */
    public AdnocS4HanaException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * Constructor with a cause.
     *
     * @param cause the underlying cause of the exception.
     */
    public AdnocS4HanaException(Throwable cause) {
        super(cause);
    }
}
