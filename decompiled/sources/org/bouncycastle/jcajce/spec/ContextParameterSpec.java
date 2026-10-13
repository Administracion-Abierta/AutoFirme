package org.bouncycastle.jcajce.spec;

import java.security.spec.AlgorithmParameterSpec;
import org.bouncycastle.util.Arrays;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/jcajce/spec/ContextParameterSpec.class */
public class ContextParameterSpec implements AlgorithmParameterSpec {
    public static ContextParameterSpec EMPTY_CONTEXT_SPEC = new ContextParameterSpec(new byte[0]);
    private final byte[] context;

    public ContextParameterSpec(byte[] bArr) {
        this.context = Arrays.clone(bArr);
    }

    public byte[] getContext() {
        return Arrays.clone(this.context);
    }
}
