package com.adnoc.service.exception;

public class AdnocPaymentException extends RuntimeException
{
    /**
     * Constructor with an error message.
     *
     * @param message the error message to be displayed.
     */
    public AdnocPaymentException(String message) {
        super(message);
    }

    /**
     * Constructor with an error message and a cause.
     *
     * @param message the error message to be displayed.
     * @param cause the underlying cause of the exception.
     */
    public AdnocPaymentException(String message, Throwable cause) {
        super(message, cause);
    }
}
