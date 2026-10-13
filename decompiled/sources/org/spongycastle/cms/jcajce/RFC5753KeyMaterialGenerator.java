package org.spongycastle.cms.jcajce;

import java.io.IOException;
import org.spongycastle.asn1.cms.ecc.ECCCMSSharedInfo;
import org.spongycastle.asn1.x509.AlgorithmIdentifier;
import org.spongycastle.util.Pack;

/* JADX INFO: loaded from: autofirma.jar:org/spongycastle/cms/jcajce/RFC5753KeyMaterialGenerator.class */
class RFC5753KeyMaterialGenerator implements KeyMaterialGenerator {
    RFC5753KeyMaterialGenerator() {
    }

    @Override // org.spongycastle.cms.jcajce.KeyMaterialGenerator
    public byte[] generateKDFMaterial(AlgorithmIdentifier keyAlgorithm, int keySize, byte[] userKeyMaterialParameters) {
        ECCCMSSharedInfo eccInfo = new ECCCMSSharedInfo(keyAlgorithm, userKeyMaterialParameters, Pack.intToBigEndian(keySize));
        try {
            return eccInfo.getEncoded("DER");
        } catch (IOException e) {
            throw new IllegalStateException("Unable to create KDF material: " + e);
        }
    }
}
