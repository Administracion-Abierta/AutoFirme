package org.spongycastle.est;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

/* JADX INFO: loaded from: autofirma.jar:org/spongycastle/est/ESTException.class */
public class ESTException extends IOException {
    private Throwable cause;
    private InputStream body;
    private int statusCode;
    private static final long MAX_ERROR_BODY = 8192;

    public ESTException(String msg) {
        this(msg, null);
    }

    public ESTException(String msg, Throwable cause) {
        super(msg);
        this.cause = cause;
        this.body = null;
        this.statusCode = 0;
    }

    public ESTException(String message, Throwable cause, int statusCode, InputStream body) {
        super(message);
        this.cause = cause;
        this.statusCode = statusCode;
        if (body != null) {
            byte[] b = new byte[8192];
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            try {
                int i = body.read(b);
                while (i >= 0) {
                    if (bos.size() + i > MAX_ERROR_BODY) {
                        int i2 = 8192 - bos.size();
                        bos.write(b, 0, i2);
                        break;
                    } else {
                        bos.write(b, 0, i);
                        i = body.read(b);
                    }
                }
                bos.flush();
                bos.close();
                this.body = new ByteArrayInputStream(bos.toByteArray());
                body.close();
                return;
            } catch (Exception e) {
                return;
            }
        }
        this.body = null;
    }

    @Override // java.lang.Throwable
    public Throwable getCause() {
        return this.cause;
    }

    @Override // java.lang.Throwable
    public String getMessage() {
        return super.getMessage() + " HTTP Status Code: " + this.statusCode;
    }

    public InputStream getBody() {
        return this.body;
    }

    public int getStatusCode() {
        return this.statusCode;
    }
}
