package org.spongycastle.pkcs;

import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.spongycastle.asn1.ASN1Encodable;
import org.spongycastle.asn1.ASN1EncodableVector;
import org.spongycastle.asn1.ASN1Encoding;
import org.spongycastle.asn1.ASN1ObjectIdentifier;
import org.spongycastle.asn1.ASN1Set;
import org.spongycastle.asn1.DERBitString;
import org.spongycastle.asn1.DERSet;
import org.spongycastle.asn1.pkcs.Attribute;
import org.spongycastle.asn1.pkcs.CertificationRequest;
import org.spongycastle.asn1.pkcs.CertificationRequestInfo;
import org.spongycastle.asn1.x500.X500Name;
import org.spongycastle.asn1.x509.SubjectPublicKeyInfo;
import org.spongycastle.operator.ContentSigner;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:org/spongycastle/pkcs/PKCS10CertificationRequestBuilder.class */
public class PKCS10CertificationRequestBuilder {
    private SubjectPublicKeyInfo publicKeyInfo;
    private X500Name subject;
    private List attributes;
    private boolean leaveOffEmpty;

    public PKCS10CertificationRequestBuilder(PKCS10CertificationRequestBuilder original) {
        this.attributes = new ArrayList();
        this.leaveOffEmpty = false;
        this.publicKeyInfo = original.publicKeyInfo;
        this.subject = original.subject;
        this.leaveOffEmpty = original.leaveOffEmpty;
        this.attributes = new ArrayList(original.attributes);
    }

    public PKCS10CertificationRequestBuilder(X500Name subject, SubjectPublicKeyInfo publicKeyInfo) {
        this.attributes = new ArrayList();
        this.leaveOffEmpty = false;
        this.subject = subject;
        this.publicKeyInfo = publicKeyInfo;
    }

    public PKCS10CertificationRequestBuilder setAttribute(ASN1ObjectIdentifier attrType, ASN1Encodable attrValue) {
        Iterator it = this.attributes.iterator();
        while (it.hasNext()) {
            if (((Attribute) it.next()).getAttrType().equals(attrType)) {
                throw new IllegalStateException("Attribute " + attrType.toString() + " is already set");
            }
        }
        addAttribute(attrType, attrValue);
        return this;
    }

    public PKCS10CertificationRequestBuilder setAttribute(ASN1ObjectIdentifier attrType, ASN1Encodable[] attrValue) {
        Iterator it = this.attributes.iterator();
        while (it.hasNext()) {
            if (((Attribute) it.next()).getAttrType().equals(attrType)) {
                throw new IllegalStateException("Attribute " + attrType.toString() + " is already set");
            }
        }
        addAttribute(attrType, attrValue);
        return this;
    }

    public PKCS10CertificationRequestBuilder addAttribute(ASN1ObjectIdentifier attrType, ASN1Encodable attrValue) {
        this.attributes.add(new Attribute(attrType, new DERSet(attrValue)));
        return this;
    }

    public PKCS10CertificationRequestBuilder addAttribute(ASN1ObjectIdentifier attrType, ASN1Encodable[] attrValues) {
        this.attributes.add(new Attribute(attrType, new DERSet(attrValues)));
        return this;
    }

    public PKCS10CertificationRequestBuilder setLeaveOffEmptyAttributes(boolean leaveOffEmpty) {
        this.leaveOffEmpty = leaveOffEmpty;
        return this;
    }

    public PKCS10CertificationRequest build(ContentSigner signer) {
        CertificationRequestInfo info;
        if (this.attributes.isEmpty()) {
            if (this.leaveOffEmpty) {
                info = new CertificationRequestInfo(this.subject, this.publicKeyInfo, (ASN1Set) null);
            } else {
                info = new CertificationRequestInfo(this.subject, this.publicKeyInfo, new DERSet());
            }
        } else {
            ASN1EncodableVector v = new ASN1EncodableVector();
            Iterator it = this.attributes.iterator();
            while (it.hasNext()) {
                v.add(Attribute.getInstance(it.next()));
            }
            info = new CertificationRequestInfo(this.subject, this.publicKeyInfo, new DERSet(v));
        }
        try {
            OutputStream sOut = signer.getOutputStream();
            sOut.write(info.getEncoded(ASN1Encoding.DER));
            sOut.close();
            return new PKCS10CertificationRequest(new CertificationRequest(info, signer.getAlgorithmIdentifier(), new DERBitString(signer.getSignature())));
        } catch (IOException e) {
            throw new IllegalStateException("cannot produce certification request signature");
        }
    }
}
