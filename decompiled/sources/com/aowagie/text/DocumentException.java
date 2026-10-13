package com.aowagie.text;

/* JADX INFO: loaded from: autofirma.jar:com/aowagie/text/DocumentException.class */
public class DocumentException extends Exception {
    private static final long serialVersionUID = -2191131489390840739L;

    public DocumentException(Exception ex) {
        super(ex);
    }

    public DocumentException() {
    }

    public DocumentException(String message) {
        super(message);
    }
}
