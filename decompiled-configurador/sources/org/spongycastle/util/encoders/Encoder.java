package org.spongycastle.util.encoders;

import java.io.IOException;
import java.io.OutputStream;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:org/spongycastle/util/encoders/Encoder.class */
public interface Encoder {
    int encode(byte[] bArr, int i, int i2, OutputStream outputStream) throws IOException;

    int decode(byte[] bArr, int i, int i2, OutputStream outputStream) throws IOException;

    int decode(String str, OutputStream outputStream) throws IOException;
}
