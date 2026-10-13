package org.bouncycastle.est;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/est/ESTException.class */
public class ESTException extends IOException {
    private Throwable cause;
    private InputStream body;
    private int statusCode;
    private static final long MAX_ERROR_BODY = 8192;

    public ESTException(String str) {
        this(str, null);
    }

    public ESTException(String str, Throwable th) {
        super(str);
        this.cause = th;
        this.body = null;
        this.statusCode = 0;
    }

    public ESTException(String str, Throwable th, int i, InputStream inputStream) {
        super(str);
        this.cause = th;
        this.statusCode = i;
        if (inputStream == null) {
            this.body = null;
            return;
        }
        byte[] bArr = new byte[8192];
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        try {
            int i2 = inputStream.read(bArr);
            while (i2 >= 0) {
                if (byteArrayOutputStream.size() + i2 > MAX_ERROR_BODY) {
                    byteArrayOutputStream.write(bArr, 0, 8192 - byteArrayOutputStream.size());
                    break;
                } else {
                    byteArrayOutputStream.write(bArr, 0, i2);
                    i2 = inputStream.read(bArr);
                }
            }
            byteArrayOutputStream.flush();
            byteArrayOutputStream.close();
            this.body = new ByteArrayInputStream(byteArrayOutputStream.toByteArray());
            inputStream.close();
        } catch (Exception e) {
        }
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
        return this.body == null ? new InputStream() { // from class: org.bouncycastle.est.ESTException.1
            @Override // java.io.InputStream
            public int read() throws IOException {
                return -1;
            }
        } : this.body;
    }

    public int getStatusCode() {
        return this.statusCode;
    }
}
