package com.adnoc.service.email.util;

public class AdnocDKIMSignerException extends Exception {

    public AdnocDKIMSignerException() {
        super();
    }

    public AdnocDKIMSignerException(String message) {
        super(message);
    }

    public AdnocDKIMSignerException(String message, Throwable cause) {
        super(message, cause);
    }
}
