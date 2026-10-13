package org.spongycastle.cms.jcajce;

import java.io.OutputStream;
import java.util.zip.DeflaterOutputStream;
import org.spongycastle.asn1.ASN1ObjectIdentifier;
import org.spongycastle.asn1.x509.AlgorithmIdentifier;
import org.spongycastle.operator.OutputCompressor;

/* JADX INFO: loaded from: autofirma.jar:org/spongycastle/cms/jcajce/ZlibCompressor.class */
public class ZlibCompressor implements OutputCompressor {
    private static final String ZLIB = "1.2.840.113549.1.9.16.3.8";

    @Override // org.spongycastle.operator.OutputCompressor
    public AlgorithmIdentifier getAlgorithmIdentifier() {
        return new AlgorithmIdentifier(new ASN1ObjectIdentifier("1.2.840.113549.1.9.16.3.8"));
    }

    @Override // org.spongycastle.operator.OutputCompressor
    public OutputStream getOutputStream(OutputStream comOut) {
        return new DeflaterOutputStream(comOut);
    }
}
