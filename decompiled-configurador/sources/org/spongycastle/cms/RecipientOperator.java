package org.spongycastle.cms;

import java.io.InputStream;
import org.spongycastle.asn1.x509.AlgorithmIdentifier;
import org.spongycastle.operator.InputDecryptor;
import org.spongycastle.operator.MacCalculator;
import org.spongycastle.util.io.TeeInputStream;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:org/spongycastle/cms/RecipientOperator.class */
public class RecipientOperator {
    private final AlgorithmIdentifier algorithmIdentifier;
    private final Object operator;

    public RecipientOperator(InputDecryptor decryptor) {
        this.algorithmIdentifier = decryptor.getAlgorithmIdentifier();
        this.operator = decryptor;
    }

    public RecipientOperator(MacCalculator macCalculator) {
        this.algorithmIdentifier = macCalculator.getAlgorithmIdentifier();
        this.operator = macCalculator;
    }

    public InputStream getInputStream(InputStream dataIn) {
        if (this.operator instanceof InputDecryptor) {
            return ((InputDecryptor) this.operator).getInputStream(dataIn);
        }
        return new TeeInputStream(dataIn, ((MacCalculator) this.operator).getOutputStream());
    }

    public boolean isMacBased() {
        return this.operator instanceof MacCalculator;
    }

    public byte[] getMac() {
        return ((MacCalculator) this.operator).getMac();
    }
}
