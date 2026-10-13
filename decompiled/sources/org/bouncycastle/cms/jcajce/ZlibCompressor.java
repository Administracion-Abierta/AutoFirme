package org.bouncycastle.cms.jcajce;

import java.io.OutputStream;
import java.util.zip.DeflaterOutputStream;
import org.bouncycastle.asn1.cms.CMSObjectIdentifiers;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.operator.OutputCompressor;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/cms/jcajce/ZlibCompressor.class */
public class ZlibCompressor implements OutputCompressor {
    @Override // org.bouncycastle.operator.OutputCompressor
    public AlgorithmIdentifier getAlgorithmIdentifier() {
        return new AlgorithmIdentifier(CMSObjectIdentifiers.zlibCompress);
    }

    @Override // org.bouncycastle.operator.OutputCompressor
    public OutputStream getOutputStream(OutputStream outputStream) {
        return new DeflaterOutputStream(outputStream);
    }
}
