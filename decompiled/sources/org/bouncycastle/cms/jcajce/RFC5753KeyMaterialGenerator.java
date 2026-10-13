package org.bouncycastle.cms.jcajce;

import java.io.IOException;
import org.bouncycastle.asn1.cms.ecc.ECCCMSSharedInfo;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.util.Pack;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/cms/jcajce/RFC5753KeyMaterialGenerator.class */
class RFC5753KeyMaterialGenerator implements KeyMaterialGenerator {
    RFC5753KeyMaterialGenerator() {
    }

    @Override // org.bouncycastle.cms.jcajce.KeyMaterialGenerator
    public byte[] generateKDFMaterial(AlgorithmIdentifier algorithmIdentifier, int i, byte[] bArr) {
        try {
            return new ECCCMSSharedInfo(algorithmIdentifier, bArr, Pack.intToBigEndian(i)).getEncoded("DER");
        } catch (IOException e) {
            throw new IllegalStateException("Unable to create KDF material: " + e);
        }
    }
}
