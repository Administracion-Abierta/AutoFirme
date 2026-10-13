package org.spongycastle.cert.cmp;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import org.spongycastle.asn1.ASN1EncodableVector;
import org.spongycastle.asn1.DERSequence;
import org.spongycastle.asn1.cmp.CertConfirmContent;
import org.spongycastle.asn1.cmp.CertStatus;
import org.spongycastle.asn1.x509.AlgorithmIdentifier;
import org.spongycastle.cert.X509CertificateHolder;
import org.spongycastle.operator.DefaultDigestAlgorithmIdentifierFinder;
import org.spongycastle.operator.DigestAlgorithmIdentifierFinder;
import org.spongycastle.operator.DigestCalculator;
import org.spongycastle.operator.DigestCalculatorProvider;
import org.spongycastle.operator.OperatorCreationException;

/* JADX INFO: loaded from: autofirma.jar:org/spongycastle/cert/cmp/CertificateConfirmationContentBuilder.class */
public class CertificateConfirmationContentBuilder {
    private DigestAlgorithmIdentifierFinder digestAlgFinder;
    private List acceptedCerts;
    private List acceptedReqIds;

    public CertificateConfirmationContentBuilder() {
        this(new DefaultDigestAlgorithmIdentifierFinder());
    }

    public CertificateConfirmationContentBuilder(DigestAlgorithmIdentifierFinder digestAlgFinder) {
        this.acceptedCerts = new ArrayList();
        this.acceptedReqIds = new ArrayList();
        this.digestAlgFinder = digestAlgFinder;
    }

    public CertificateConfirmationContentBuilder addAcceptedCertificate(X509CertificateHolder certHolder, BigInteger certReqID) {
        this.acceptedCerts.add(certHolder);
        this.acceptedReqIds.add(certReqID);
        return this;
    }

    public CertificateConfirmationContent build(DigestCalculatorProvider digesterProvider) throws CMPException {
        ASN1EncodableVector v = new ASN1EncodableVector();
        for (int i = 0; i != this.acceptedCerts.size(); i++) {
            X509CertificateHolder certHolder = (X509CertificateHolder) this.acceptedCerts.get(i);
            BigInteger reqID = (BigInteger) this.acceptedReqIds.get(i);
            AlgorithmIdentifier digAlg = this.digestAlgFinder.find(certHolder.toASN1Structure().getSignatureAlgorithm());
            if (digAlg == null) {
                throw new CMPException("cannot find algorithm for digest from signature");
            }
            try {
                DigestCalculator digester = digesterProvider.get(digAlg);
                CMPUtil.derEncodeToStream(certHolder.toASN1Structure(), digester.getOutputStream());
                v.add(new CertStatus(digester.getDigest(), reqID));
            } catch (OperatorCreationException e) {
                throw new CMPException("unable to create digest: " + e.getMessage(), e);
            }
        }
        return new CertificateConfirmationContent(CertConfirmContent.getInstance(new DERSequence(v)), this.digestAlgFinder);
    }
}
