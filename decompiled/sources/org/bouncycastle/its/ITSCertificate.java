package org.bouncycastle.its;

import java.io.IOException;
import java.io.OutputStream;
import org.bouncycastle.its.operator.ECDSAEncoder;
import org.bouncycastle.its.operator.ITSContentVerifierProvider;
import org.bouncycastle.oer.OEREncoder;
import org.bouncycastle.oer.its.ieee1609dot2.CertificateBase;
import org.bouncycastle.oer.its.ieee1609dot2.IssuerIdentifier;
import org.bouncycastle.oer.its.ieee1609dot2.basetypes.PublicEncryptionKey;
import org.bouncycastle.oer.its.template.ieee1609dot2.IEEE1609dot2;
import org.bouncycastle.operator.ContentVerifier;
import org.bouncycastle.util.Encodable;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/its/ITSCertificate.class */
public class ITSCertificate implements Encodable {
    private final CertificateBase certificate;

    public ITSCertificate(CertificateBase certificateBase) {
        this.certificate = certificateBase;
    }

    public IssuerIdentifier getIssuer() {
        return this.certificate.getIssuer();
    }

    public ITSValidityPeriod getValidityPeriod() {
        return new ITSValidityPeriod(this.certificate.getToBeSigned().getValidityPeriod());
    }

    public ITSPublicEncryptionKey getPublicEncryptionKey() {
        PublicEncryptionKey encryptionKey = this.certificate.getToBeSigned().getEncryptionKey();
        if (encryptionKey != null) {
            return new ITSPublicEncryptionKey(encryptionKey);
        }
        return null;
    }

    public boolean isSignatureValid(ITSContentVerifierProvider iTSContentVerifierProvider) throws Exception {
        ContentVerifier contentVerifier = iTSContentVerifierProvider.get(this.certificate.getSignature().getChoice());
        OutputStream outputStream = contentVerifier.getOutputStream();
        outputStream.write(OEREncoder.toByteArray(this.certificate.getToBeSigned(), IEEE1609dot2.ToBeSignedCertificate.build()));
        outputStream.close();
        return contentVerifier.verify(ECDSAEncoder.toX962(this.certificate.getSignature()));
    }

    public CertificateBase toASN1Structure() {
        return this.certificate;
    }

    @Override // org.bouncycastle.util.Encodable
    public byte[] getEncoded() throws IOException {
        return OEREncoder.toByteArray(this.certificate, IEEE1609dot2.CertificateBase.build());
    }
}
