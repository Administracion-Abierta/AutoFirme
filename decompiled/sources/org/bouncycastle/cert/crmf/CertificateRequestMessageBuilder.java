package org.bouncycastle.cert.crmf;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1Integer;
import org.bouncycastle.asn1.ASN1Null;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.DERNull;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.crmf.AttributeTypeAndValue;
import org.bouncycastle.asn1.crmf.CertReqMsg;
import org.bouncycastle.asn1.crmf.CertRequest;
import org.bouncycastle.asn1.crmf.CertTemplate;
import org.bouncycastle.asn1.crmf.CertTemplateBuilder;
import org.bouncycastle.asn1.crmf.OptionalValidity;
import org.bouncycastle.asn1.crmf.PKMACValue;
import org.bouncycastle.asn1.crmf.POPOPrivKey;
import org.bouncycastle.asn1.crmf.ProofOfPossession;
import org.bouncycastle.asn1.crmf.SubsequentMessage;
import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.asn1.x509.ExtensionsGenerator;
import org.bouncycastle.asn1.x509.GeneralName;
import org.bouncycastle.asn1.x509.SubjectPublicKeyInfo;
import org.bouncycastle.asn1.x509.Time;
import org.bouncycastle.cert.CertIOException;
import org.bouncycastle.operator.ContentSigner;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/cert/crmf/CertificateRequestMessageBuilder.class */
public class CertificateRequestMessageBuilder {
    private final BigInteger certReqId;
    private ContentSigner popSigner;
    private PKMACBuilder pkmacBuilder;
    private char[] password;
    private GeneralName sender;
    private POPOPrivKey popoPrivKey;
    private ASN1Null popRaVerified;
    private PKMACValue agreeMAC;
    private int popoType = 2;
    private ExtensionsGenerator extGenerator = new ExtensionsGenerator();
    private CertTemplateBuilder templateBuilder = new CertTemplateBuilder();
    private List controls = new ArrayList();
    private AttributeTypeAndValue[] regInfo = null;

    public CertificateRequestMessageBuilder(BigInteger bigInteger) {
        this.certReqId = bigInteger;
    }

    public CertificateRequestMessageBuilder setRegInfo(AttributeTypeAndValue[] attributeTypeAndValueArr) {
        this.regInfo = attributeTypeAndValueArr;
        return this;
    }

    public CertificateRequestMessageBuilder setPublicKey(SubjectPublicKeyInfo subjectPublicKeyInfo) {
        if (subjectPublicKeyInfo != null) {
            this.templateBuilder.setPublicKey(subjectPublicKeyInfo);
        }
        return this;
    }

    public CertificateRequestMessageBuilder setIssuer(X500Name x500Name) {
        if (x500Name != null) {
            this.templateBuilder.setIssuer(x500Name);
        }
        return this;
    }

    public CertificateRequestMessageBuilder setSubject(X500Name x500Name) {
        if (x500Name != null) {
            this.templateBuilder.setSubject(x500Name);
        }
        return this;
    }

    public CertificateRequestMessageBuilder setSerialNumber(BigInteger bigInteger) {
        if (bigInteger != null) {
            this.templateBuilder.setSerialNumber(new ASN1Integer(bigInteger));
        }
        return this;
    }

    public CertificateRequestMessageBuilder setSerialNumber(ASN1Integer aSN1Integer) {
        if (aSN1Integer != null) {
            this.templateBuilder.setSerialNumber(aSN1Integer);
        }
        return this;
    }

    public CertificateRequestMessageBuilder setValidity(Date date, Date date2) {
        this.templateBuilder.setValidity(new OptionalValidity(createTime(date), createTime(date2)));
        return this;
    }

    private Time createTime(Date date) {
        if (date != null) {
            return new Time(date);
        }
        return null;
    }

    public CertificateRequestMessageBuilder addExtension(ASN1ObjectIdentifier aSN1ObjectIdentifier, boolean z, ASN1Encodable aSN1Encodable) throws CertIOException {
        CRMFUtil.addExtension(this.extGenerator, aSN1ObjectIdentifier, z, aSN1Encodable);
        return this;
    }

    public CertificateRequestMessageBuilder addExtension(ASN1ObjectIdentifier aSN1ObjectIdentifier, boolean z, byte[] bArr) {
        this.extGenerator.addExtension(aSN1ObjectIdentifier, z, bArr);
        return this;
    }

    public CertificateRequestMessageBuilder addControl(Control control) {
        this.controls.add(control);
        return this;
    }

    public CertificateRequestMessageBuilder setProofOfPossessionSigningKeySigner(ContentSigner contentSigner) {
        if (this.popoPrivKey != null || this.popRaVerified != null || this.agreeMAC != null) {
            throw new IllegalStateException("only one proof of possession allowed");
        }
        this.popSigner = contentSigner;
        return this;
    }

    public CertificateRequestMessageBuilder setProofOfPossessionSubsequentMessage(SubsequentMessage subsequentMessage) {
        if (this.popSigner != null || this.popRaVerified != null || this.agreeMAC != null) {
            throw new IllegalStateException("only one proof of possession allowed");
        }
        this.popoType = 2;
        this.popoPrivKey = new POPOPrivKey(subsequentMessage);
        return this;
    }

    public CertificateRequestMessageBuilder setProofOfPossessionSubsequentMessage(int i, SubsequentMessage subsequentMessage) {
        if (this.popSigner != null || this.popRaVerified != null || this.agreeMAC != null) {
            throw new IllegalStateException("only one proof of possession allowed");
        }
        if (i != 2 && i != 3) {
            throw new IllegalArgumentException("type must be ProofOfPossession.TYPE_KEY_ENCIPHERMENT or ProofOfPossession.TYPE_KEY_AGREEMENT");
        }
        this.popoType = i;
        this.popoPrivKey = new POPOPrivKey(subsequentMessage);
        return this;
    }

    public CertificateRequestMessageBuilder setProofOfPossessionAgreeMAC(PKMACValue pKMACValue) {
        if (this.popSigner != null || this.popRaVerified != null || this.popoPrivKey != null) {
            throw new IllegalStateException("only one proof of possession allowed");
        }
        this.agreeMAC = pKMACValue;
        return this;
    }

    public CertificateRequestMessageBuilder setProofOfPossessionRaVerified() {
        if (this.popSigner != null || this.popoPrivKey != null) {
            throw new IllegalStateException("only one proof of possession allowed");
        }
        this.popRaVerified = DERNull.INSTANCE;
        return this;
    }

    public CertificateRequestMessageBuilder setAuthInfoPKMAC(PKMACBuilder pKMACBuilder, char[] cArr) {
        this.pkmacBuilder = pKMACBuilder;
        this.password = cArr;
        return this;
    }

    public CertificateRequestMessageBuilder setAuthInfoSender(X500Name x500Name) {
        return setAuthInfoSender(new GeneralName(x500Name));
    }

    public CertificateRequestMessageBuilder setAuthInfoSender(GeneralName generalName) {
        this.sender = generalName;
        return this;
    }

    public CertificateRequestMessage build() throws CRMFException {
        ProofOfPossession proofOfPossession;
        ProofOfPossessionSigningKeyBuilder proofOfPossessionSigningKeyBuilder;
        ASN1EncodableVector aSN1EncodableVector = new ASN1EncodableVector(3);
        aSN1EncodableVector.add(new ASN1Integer(this.certReqId));
        if (!this.extGenerator.isEmpty()) {
            this.templateBuilder.setExtensions(this.extGenerator.generate());
        }
        aSN1EncodableVector.add(this.templateBuilder.build());
        if (!this.controls.isEmpty()) {
            ASN1EncodableVector aSN1EncodableVector2 = new ASN1EncodableVector();
            for (Control control : this.controls) {
                aSN1EncodableVector2.add(new AttributeTypeAndValue(control.getType(), control.getValue()));
            }
            aSN1EncodableVector.add(new DERSequence(aSN1EncodableVector2));
        }
        CertRequest certRequest = CertRequest.getInstance(new DERSequence(aSN1EncodableVector));
        if (this.popSigner != null) {
            CertTemplate certTemplate = certRequest.getCertTemplate();
            if (certTemplate.getSubject() == null || certTemplate.getPublicKey() == null) {
                proofOfPossessionSigningKeyBuilder = new ProofOfPossessionSigningKeyBuilder(certRequest.getCertTemplate().getPublicKey());
                if (this.sender != null) {
                    proofOfPossessionSigningKeyBuilder.setSender(this.sender);
                } else {
                    proofOfPossessionSigningKeyBuilder.setPublicKeyMac(this.pkmacBuilder, this.password);
                }
            } else {
                proofOfPossessionSigningKeyBuilder = new ProofOfPossessionSigningKeyBuilder(certRequest);
            }
            proofOfPossession = new ProofOfPossession(proofOfPossessionSigningKeyBuilder.build(this.popSigner));
        } else if (this.popoPrivKey != null) {
            proofOfPossession = new ProofOfPossession(this.popoType, this.popoPrivKey);
        } else if (this.agreeMAC != null) {
            proofOfPossession = new ProofOfPossession(3, new POPOPrivKey(this.agreeMAC));
        } else {
            proofOfPossession = this.popRaVerified != null ? new ProofOfPossession() : new ProofOfPossession();
        }
        return new CertificateRequestMessage(new CertReqMsg(certRequest, proofOfPossession, this.regInfo));
    }
}
