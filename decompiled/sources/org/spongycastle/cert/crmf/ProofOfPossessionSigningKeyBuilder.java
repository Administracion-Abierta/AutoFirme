package org.spongycastle.cert.crmf;

import org.spongycastle.asn1.DERBitString;
import org.spongycastle.asn1.crmf.CertRequest;
import org.spongycastle.asn1.crmf.PKMACValue;
import org.spongycastle.asn1.crmf.POPOSigningKey;
import org.spongycastle.asn1.crmf.POPOSigningKeyInput;
import org.spongycastle.asn1.x509.GeneralName;
import org.spongycastle.asn1.x509.SubjectPublicKeyInfo;
import org.spongycastle.operator.ContentSigner;

/* JADX INFO: loaded from: autofirma.jar:org/spongycastle/cert/crmf/ProofOfPossessionSigningKeyBuilder.class */
public class ProofOfPossessionSigningKeyBuilder {
    private CertRequest certRequest;
    private SubjectPublicKeyInfo pubKeyInfo;
    private GeneralName name;
    private PKMACValue publicKeyMAC;

    public ProofOfPossessionSigningKeyBuilder(CertRequest certRequest) {
        this.certRequest = certRequest;
    }

    public ProofOfPossessionSigningKeyBuilder(SubjectPublicKeyInfo pubKeyInfo) {
        this.pubKeyInfo = pubKeyInfo;
    }

    public ProofOfPossessionSigningKeyBuilder setSender(GeneralName name) {
        this.name = name;
        return this;
    }

    public ProofOfPossessionSigningKeyBuilder setPublicKeyMac(PKMACValueGenerator generator, char[] password) throws CRMFException {
        this.publicKeyMAC = generator.generate(password, this.pubKeyInfo);
        return this;
    }

    public POPOSigningKey build(ContentSigner signer) {
        POPOSigningKeyInput popo;
        if (this.name != null && this.publicKeyMAC != null) {
            throw new IllegalStateException("name and publicKeyMAC cannot both be set.");
        }
        if (this.certRequest != null) {
            popo = null;
            CRMFUtil.derEncodeToStream(this.certRequest, signer.getOutputStream());
        } else if (this.name != null) {
            popo = new POPOSigningKeyInput(this.name, this.pubKeyInfo);
            CRMFUtil.derEncodeToStream(popo, signer.getOutputStream());
        } else {
            popo = new POPOSigningKeyInput(this.publicKeyMAC, this.pubKeyInfo);
            CRMFUtil.derEncodeToStream(popo, signer.getOutputStream());
        }
        return new POPOSigningKey(popo, signer.getAlgorithmIdentifier(), new DERBitString(signer.getSignature()));
    }
}
