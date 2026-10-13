package org.spongycastle.tsp;

import java.io.IOException;
import java.math.BigInteger;
import org.spongycastle.asn1.ASN1Boolean;
import org.spongycastle.asn1.ASN1Encodable;
import org.spongycastle.asn1.ASN1Integer;
import org.spongycastle.asn1.ASN1ObjectIdentifier;
import org.spongycastle.asn1.DERNull;
import org.spongycastle.asn1.tsp.MessageImprint;
import org.spongycastle.asn1.tsp.TimeStampReq;
import org.spongycastle.asn1.x509.AlgorithmIdentifier;
import org.spongycastle.asn1.x509.Extensions;
import org.spongycastle.asn1.x509.ExtensionsGenerator;

/* JADX INFO: loaded from: autofirma.jar:org/spongycastle/tsp/TimeStampRequestGenerator.class */
public class TimeStampRequestGenerator {
    private ASN1ObjectIdentifier reqPolicy;
    private ASN1Boolean certReq;
    private ExtensionsGenerator extGenerator = new ExtensionsGenerator();

    public void setReqPolicy(String reqPolicy) {
        this.reqPolicy = new ASN1ObjectIdentifier(reqPolicy);
    }

    public void setReqPolicy(ASN1ObjectIdentifier reqPolicy) {
        this.reqPolicy = reqPolicy;
    }

    public void setCertReq(boolean certReq) {
        this.certReq = ASN1Boolean.getInstance(certReq);
    }

    public void addExtension(String OID, boolean critical, ASN1Encodable value) throws IOException {
        addExtension(OID, critical, value.toASN1Primitive().getEncoded());
    }

    public void addExtension(String OID, boolean critical, byte[] value) {
        this.extGenerator.addExtension(new ASN1ObjectIdentifier(OID), critical, value);
    }

    public void addExtension(ASN1ObjectIdentifier oid, boolean isCritical, ASN1Encodable value) throws TSPIOException {
        TSPUtil.addExtension(this.extGenerator, oid, isCritical, value);
    }

    public void addExtension(ASN1ObjectIdentifier oid, boolean isCritical, byte[] value) {
        this.extGenerator.addExtension(oid, isCritical, value);
    }

    public TimeStampRequest generate(String digestAlgorithm, byte[] digest) {
        return generate(digestAlgorithm, digest, (BigInteger) null);
    }

    public TimeStampRequest generate(String digestAlgorithmOID, byte[] digest, BigInteger nonce) {
        if (digestAlgorithmOID == null) {
            throw new IllegalArgumentException("No digest algorithm specified");
        }
        ASN1ObjectIdentifier digestAlgOID = new ASN1ObjectIdentifier(digestAlgorithmOID);
        AlgorithmIdentifier algID = new AlgorithmIdentifier(digestAlgOID, DERNull.INSTANCE);
        MessageImprint messageImprint = new MessageImprint(algID, digest);
        Extensions ext = null;
        if (!this.extGenerator.isEmpty()) {
            ext = this.extGenerator.generate();
        }
        if (nonce != null) {
            return new TimeStampRequest(new TimeStampReq(messageImprint, this.reqPolicy, new ASN1Integer(nonce), this.certReq, ext));
        }
        return new TimeStampRequest(new TimeStampReq(messageImprint, this.reqPolicy, null, this.certReq, ext));
    }

    public TimeStampRequest generate(ASN1ObjectIdentifier digestAlgorithm, byte[] digest) {
        return generate(digestAlgorithm.getId(), digest);
    }

    public TimeStampRequest generate(ASN1ObjectIdentifier digestAlgorithm, byte[] digest, BigInteger nonce) {
        return generate(digestAlgorithm.getId(), digest, nonce);
    }
}
