package org.bouncycastle.cms;

import java.io.InputStream;
import java.io.OutputStream;
import org.bouncycastle.operator.InputAEADDecryptor;
import org.bouncycastle.operator.InputDecryptor;
import org.bouncycastle.operator.MacCalculator;
import org.bouncycastle.util.io.TeeInputStream;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/cms/RecipientOperator.class */
public class RecipientOperator {
    private final Object operator;

    public RecipientOperator(InputDecryptor inputDecryptor) {
        this.operator = inputDecryptor;
    }

    public RecipientOperator(MacCalculator macCalculator) {
        this.operator = macCalculator;
    }

    public InputStream getInputStream(InputStream inputStream) {
        return this.operator instanceof InputDecryptor ? ((InputDecryptor) this.operator).getInputStream(inputStream) : new TeeInputStream(inputStream, ((MacCalculator) this.operator).getOutputStream());
    }

    public boolean isAEADBased() {
        return this.operator instanceof InputAEADDecryptor;
    }

    public OutputStream getAADStream() {
        return ((InputAEADDecryptor) this.operator).getAADStream();
    }

    public boolean isMacBased() {
        return this.operator instanceof MacCalculator;
    }

    public byte[] getMac() {
        if (this.operator instanceof MacCalculator) {
            return ((MacCalculator) this.operator).getMac();
        }
        if (this.operator instanceof InputAEADDecryptor) {
            return ((InputAEADDecryptor) this.operator).getMAC();
        }
        return null;
    }
}
