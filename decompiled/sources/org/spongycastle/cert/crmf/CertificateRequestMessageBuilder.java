package org.spongycastle.cert.crmf;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import org.spongycastle.asn1.ASN1Encodable;
import org.spongycastle.asn1.ASN1EncodableVector;
import org.spongycastle.asn1.ASN1Integer;
import org.spongycastle.asn1.ASN1Null;
import org.spongycastle.asn1.ASN1ObjectIdentifier;
import org.spongycastle.asn1.DERNull;
import org.spongycastle.asn1.DERSequence;
import org.spongycastle.asn1.crmf.AttributeTypeAndValue;
import org.spongycastle.asn1.crmf.CertReqMsg;
import org.spongycastle.asn1.crmf.CertRequest;
import org.spongycastle.asn1.crmf.CertTemplate;
import org.spongycastle.asn1.crmf.CertTemplateBuilder;
import org.spongycastle.asn1.crmf.OptionalValidity;
import org.spongycastle.asn1.crmf.POPOPrivKey;
import org.spongycastle.asn1.crmf.ProofOfPossession;
import org.spongycastle.asn1.crmf.SubsequentMessage;
import org.spongycastle.asn1.x500.X500Name;
import org.spongycastle.asn1.x509.ExtensionsGenerator;
import org.spongycastle.asn1.x509.GeneralName;
import org.spongycastle.asn1.x509.SubjectPublicKeyInfo;
import org.spongycastle.asn1.x509.Time;
import org.spongycastle.cert.CertIOException;
import org.spongycastle.operator.ContentSigner;

/* JADX INFO: loaded from: autofirma.jar:org/spongycastle/cert/crmf/CertificateRequestMessageBuilder.class */
public class CertificateRequestMessageBuilder {
    private final BigInteger certReqId;
    private ExtensionsGenerator extGenerator = new ExtensionsGenerator();
    private CertTemplateBuilder templateBuilder = new CertTemplateBuilder();
    private List controls = new ArrayList();
    private ContentSigner popSigner;
    private PKMACBuilder pkmacBuilder;
    private char[] password;
    private GeneralName sender;
    private POPOPrivKey popoPrivKey;
    private ASN1Null popRaVerified;

    public CertificateRequestMessageBuilder(BigInteger certReqId) {
        this.certReqId = certReqId;
    }

    public CertificateRequestMessageBuilder setPublicKey(SubjectPublicKeyInfo publicKey) {
        if (publicKey != null) {
            this.templateBuilder.setPublicKey(publicKey);
        }
        return this;
    }

    public CertificateRequestMessageBuilder setIssuer(X500Name issuer) {
        if (issuer != null) {
            this.templateBuilder.setIssuer(issuer);
        }
        return this;
    }

    public CertificateRequestMessageBuilder setSubject(X500Name subject) {
        if (subject != null) {
            this.templateBuilder.setSubject(subject);
        }
        return this;
    }

    public CertificateRequestMessageBuilder setSerialNumber(BigInteger serialNumber) {
        if (serialNumber != null) {
            this.templateBuilder.setSerialNumber(new ASN1Integer(serialNumber));
        }
        return this;
    }

    public CertificateRequestMessageBuilder setValidity(Date notBeforeDate, Date notAfterDate) {
        this.templateBuilder.setValidity(new OptionalValidity(createTime(notBeforeDate), createTime(notAfterDate)));
        return this;
    }

    private Time createTime(Date date) {
        if (date != null) {
            return new Time(date);
        }
        return null;
    }

    public CertificateRequestMessageBuilder addExtension(ASN1ObjectIdentifier oid, boolean critical, ASN1Encodable value) throws CertIOException {
        CRMFUtil.addExtension(this.extGenerator, oid, critical, value);
        return this;
    }

    public CertificateRequestMessageBuilder addExtension(ASN1ObjectIdentifier oid, boolean critical, byte[] value) {
        this.extGenerator.addExtension(oid, critical, value);
        return this;
    }

    public CertificateRequestMessageBuilder addControl(Control control) {
        this.controls.add(control);
        return this;
    }

    public CertificateRequestMessageBuilder setProofOfPossessionSigningKeySigner(ContentSigner popSigner) {
        if (this.popoPrivKey != null || this.popRaVerified != null) {
            throw new IllegalStateException("only one proof of possession allowed");
        }
        this.popSigner = popSigner;
        return this;
    }

    public CertificateRequestMessageBuilder setProofOfPossessionSubsequentMessage(SubsequentMessage msg) {
        if (this.popSigner != null || this.popRaVerified != null) {
            throw new IllegalStateException("only one proof of possession allowed");
        }
        this.popoPrivKey = new POPOPrivKey(msg);
        return this;
    }

    public CertificateRequestMessageBuilder setProofOfPossessionRaVerified() {
        if (this.popSigner != null || this.popoPrivKey != null) {
            throw new IllegalStateException("only one proof of possession allowed");
        }
        this.popRaVerified = DERNull.INSTANCE;
        return this;
    }

    public CertificateRequestMessageBuilder setAuthInfoPKMAC(PKMACBuilder pkmacBuilder, char[] password) {
        this.pkmacBuilder = pkmacBuilder;
        this.password = password;
        return this;
    }

    public CertificateRequestMessageBuilder setAuthInfoSender(X500Name sender) {
        return setAuthInfoSender(new GeneralName(sender));
    }

    public CertificateRequestMessageBuilder setAuthInfoSender(GeneralName sender) {
        this.sender = sender;
        return this;
    }

    public CertificateRequestMessage build() throws CRMFException {
        ASN1EncodableVector v = new ASN1EncodableVector();
        v.add(new ASN1Integer(this.certReqId));
        if (!this.extGenerator.isEmpty()) {
            this.templateBuilder.setExtensions(this.extGenerator.generate());
        }
        v.add(this.templateBuilder.build());
        if (!this.controls.isEmpty()) {
            ASN1EncodableVector controlV = new ASN1EncodableVector();
            for (Control control : this.controls) {
                controlV.add(new AttributeTypeAndValue(control.getType(), control.getValue()));
            }
            v.add(new DERSequence(controlV));
        }
        CertRequest request = CertRequest.getInstance(new DERSequence(v));
        ASN1EncodableVector v2 = new ASN1EncodableVector();
        v2.add(request);
        if (this.popSigner != null) {
            CertTemplate template = request.getCertTemplate();
            if (template.getSubject() == null || template.getPublicKey() == null) {
                SubjectPublicKeyInfo pubKeyInfo = request.getCertTemplate().getPublicKey();
                ProofOfPossessionSigningKeyBuilder builder = new ProofOfPossessionSigningKeyBuilder(pubKeyInfo);
                if (this.sender != null) {
                    builder.setSender(this.sender);
                } else {
                    PKMACValueGenerator pkmacGenerator = new PKMACValueGenerator(this.pkmacBuilder);
                    builder.setPublicKeyMac(pkmacGenerator, this.password);
                }
                v2.add(new ProofOfPossession(builder.build(this.popSigner)));
            } else {
                v2.add(new ProofOfPossession(new ProofOfPossessionSigningKeyBuilder(request).build(this.popSigner)));
            }
        } else if (this.popoPrivKey != null) {
            v2.add(new ProofOfPossession(2, this.popoPrivKey));
        } else if (this.popRaVerified != null) {
            v2.add(new ProofOfPossession());
        }
        return new CertificateRequestMessage(CertReqMsg.getInstance(new DERSequence(v2)));
    }
}
