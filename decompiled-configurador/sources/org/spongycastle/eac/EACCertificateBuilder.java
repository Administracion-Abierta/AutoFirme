package org.spongycastle.eac;

import java.io.OutputStream;
import org.spongycastle.asn1.ASN1Encoding;
import org.spongycastle.asn1.DERApplicationSpecific;
import org.spongycastle.asn1.eac.CVCertificate;
import org.spongycastle.asn1.eac.CertificateBody;
import org.spongycastle.asn1.eac.CertificateHolderAuthorization;
import org.spongycastle.asn1.eac.CertificateHolderReference;
import org.spongycastle.asn1.eac.CertificationAuthorityReference;
import org.spongycastle.asn1.eac.PackedDate;
import org.spongycastle.asn1.eac.PublicKeyDataObject;
import org.spongycastle.eac.operator.EACSigner;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:org/spongycastle/eac/EACCertificateBuilder.class */
public class EACCertificateBuilder {
    private static final byte[] ZeroArray = {0};
    private PublicKeyDataObject publicKey;
    private CertificateHolderAuthorization certificateHolderAuthorization;
    private PackedDate certificateEffectiveDate;
    private PackedDate certificateExpirationDate;
    private CertificateHolderReference certificateHolderReference;
    private CertificationAuthorityReference certificationAuthorityReference;

    public EACCertificateBuilder(CertificationAuthorityReference certificationAuthorityReference, PublicKeyDataObject publicKey, CertificateHolderReference certificateHolderReference, CertificateHolderAuthorization certificateHolderAuthorization, PackedDate certificateEffectiveDate, PackedDate certificateExpirationDate) {
        this.certificationAuthorityReference = certificationAuthorityReference;
        this.publicKey = publicKey;
        this.certificateHolderReference = certificateHolderReference;
        this.certificateHolderAuthorization = certificateHolderAuthorization;
        this.certificateEffectiveDate = certificateEffectiveDate;
        this.certificateExpirationDate = certificateExpirationDate;
    }

    private CertificateBody buildBody() {
        DERApplicationSpecific certificateProfileIdentifier = new DERApplicationSpecific(41, ZeroArray);
        CertificateBody body = new CertificateBody(certificateProfileIdentifier, this.certificationAuthorityReference, this.publicKey, this.certificateHolderReference, this.certificateHolderAuthorization, this.certificateEffectiveDate, this.certificateExpirationDate);
        return body;
    }

    public EACCertificateHolder build(EACSigner signer) throws EACException {
        try {
            CertificateBody body = buildBody();
            OutputStream vOut = signer.getOutputStream();
            vOut.write(body.getEncoded(ASN1Encoding.DER));
            vOut.close();
            return new EACCertificateHolder(new CVCertificate(body, signer.getSignature()));
        } catch (Exception e) {
            throw new EACException("unable to process signature: " + e.getMessage(), e);
        }
    }
}
