package com.twelvemonkeys.io.enc;

import java.io.IOException;

/* JADX INFO: loaded from: autofirma.jar:com/twelvemonkeys/io/enc/DecodeException.class */
public class DecodeException extends IOException {
    public DecodeException(String str) {
        super(str);
    }

    public DecodeException(String str, Throwable th) {
        super(str);
        initCause(th);
    }

    public DecodeException(Throwable th) {
        this(th.getMessage(), th);
    }
}
