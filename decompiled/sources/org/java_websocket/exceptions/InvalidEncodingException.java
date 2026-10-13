package org.java_websocket.exceptions;

import java.io.UnsupportedEncodingException;

/* JADX INFO: loaded from: autofirma.jar:org/java_websocket/exceptions/InvalidEncodingException.class */
public class InvalidEncodingException extends RuntimeException {
    private final UnsupportedEncodingException encodingException;

    public InvalidEncodingException(UnsupportedEncodingException encodingException) {
        if (encodingException == null) {
            throw new IllegalArgumentException();
        }
        this.encodingException = encodingException;
    }

    public UnsupportedEncodingException getEncodingException() {
        return this.encodingException;
    }
}
