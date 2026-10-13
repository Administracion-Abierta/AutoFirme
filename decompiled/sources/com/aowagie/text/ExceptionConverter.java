package com.aowagie.text;

import java.io.PrintStream;
import java.io.PrintWriter;

/* JADX INFO: loaded from: autofirma.jar:com/aowagie/text/ExceptionConverter.class */
public class ExceptionConverter extends RuntimeException {
    private static final long serialVersionUID = 8657630363395849399L;
    private final Exception ex;
    private final String prefix;

    public ExceptionConverter(Exception ex) {
        this.ex = ex;
        this.prefix = ex instanceof RuntimeException ? "" : "ExceptionConverter: ";
    }

    public static final RuntimeException convertException(Exception ex) {
        if (ex instanceof RuntimeException) {
            return (RuntimeException) ex;
        }
        return new ExceptionConverter(ex);
    }

    public Exception getException() {
        return this.ex;
    }

    @Override // java.lang.Throwable
    public String getMessage() {
        return this.ex.getMessage();
    }

    @Override // java.lang.Throwable
    public String getLocalizedMessage() {
        return this.ex.getLocalizedMessage();
    }

    @Override // java.lang.Throwable
    public String toString() {
        return this.prefix + this.ex;
    }

    @Override // java.lang.Throwable
    public void printStackTrace() {
        printStackTrace(System.err);
    }

    @Override // java.lang.Throwable
    public void printStackTrace(PrintStream s) {
        synchronized (s) {
            s.print(this.prefix);
            this.ex.printStackTrace(s);
        }
    }

    @Override // java.lang.Throwable
    public void printStackTrace(PrintWriter s) {
        synchronized (s) {
            s.print(this.prefix);
            this.ex.printStackTrace(s);
        }
    }

    @Override // java.lang.Throwable
    public Throwable fillInStackTrace() {
        return this;
    }
}
