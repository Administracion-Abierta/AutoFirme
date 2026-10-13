package org.bouncycastle.cert.path.validations;

import java.io.IOException;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1Null;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.asn1.x509.SubjectPublicKeyInfo;
import org.bouncycastle.cert.CertException;
import org.bouncycastle.cert.X509CertificateHolder;
import org.bouncycastle.cert.X509ContentVerifierProviderBuilder;
import org.bouncycastle.cert.path.CertPathValidation;
import org.bouncycastle.cert.path.CertPathValidationContext;
import org.bouncycastle.cert.path.CertPathValidationException;
import org.bouncycastle.operator.OperatorCreationException;
import org.bouncycastle.util.Memoable;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/cert/path/validations/ParentCertIssuedValidation.class */
public class ParentCertIssuedValidation implements CertPathValidation {
    private X509ContentVerifierProviderBuilder contentVerifierProvider;
    private X500Name workingIssuerName;
    private SubjectPublicKeyInfo workingPublicKey;
    private AlgorithmIdentifier workingAlgId;

    public ParentCertIssuedValidation(X509ContentVerifierProviderBuilder x509ContentVerifierProviderBuilder) {
        this.contentVerifierProvider = x509ContentVerifierProviderBuilder;
    }

    @Override // org.bouncycastle.cert.path.CertPathValidation
    public void validate(CertPathValidationContext certPathValidationContext, X509CertificateHolder x509CertificateHolder) throws CertPathValidationException {
        if (this.workingIssuerName != null && !this.workingIssuerName.equals(x509CertificateHolder.getIssuer())) {
            throw new CertPathValidationException("Certificate issue does not match parent");
        }
        if (this.workingPublicKey != null) {
            try {
                if (!x509CertificateHolder.isSignatureValid(this.contentVerifierProvider.build(this.workingPublicKey.getAlgorithm().equals(this.workingAlgId) ? this.workingPublicKey : new SubjectPublicKeyInfo(this.workingAlgId, this.workingPublicKey.parsePublicKey())))) {
                    throw new CertPathValidationException("Certificate signature not for public key in parent");
                }
            } catch (IOException e) {
                throw new CertPathValidationException("Unable to build public key: " + e.getMessage(), e);
            } catch (CertException e2) {
                throw new CertPathValidationException("Unable to validate signature: " + e2.getMessage(), e2);
            } catch (OperatorCreationException e3) {
                throw new CertPathValidationException("Unable to create verifier: " + e3.getMessage(), e3);
            }
        }
        this.workingIssuerName = x509CertificateHolder.getSubject();
        this.workingPublicKey = x509CertificateHolder.getSubjectPublicKeyInfo();
        if (this.workingAlgId == null) {
            this.workingAlgId = this.workingPublicKey.getAlgorithm();
        } else if (!this.workingPublicKey.getAlgorithm().getAlgorithm().equals((ASN1Primitive) this.workingAlgId.getAlgorithm())) {
            this.workingAlgId = this.workingPublicKey.getAlgorithm();
        } else {
            if (isNull(this.workingPublicKey.getAlgorithm().getParameters())) {
                return;
            }
            this.workingAlgId = this.workingPublicKey.getAlgorithm();
        }
    }

    private boolean isNull(ASN1Encodable aSN1Encodable) {
        return aSN1Encodable == null || (aSN1Encodable instanceof ASN1Null);
    }

    @Override // org.bouncycastle.util.Memoable
    public Memoable copy() {
        ParentCertIssuedValidation parentCertIssuedValidation = new ParentCertIssuedValidation(this.contentVerifierProvider);
        parentCertIssuedValidation.workingAlgId = this.workingAlgId;
        parentCertIssuedValidation.workingIssuerName = this.workingIssuerName;
        parentCertIssuedValidation.workingPublicKey = this.workingPublicKey;
        return parentCertIssuedValidation;
    }

    @Override // org.bouncycastle.util.Memoable
    public void reset(Memoable memoable) {
        ParentCertIssuedValidation parentCertIssuedValidation = (ParentCertIssuedValidation) memoable;
        this.contentVerifierProvider = parentCertIssuedValidation.contentVerifierProvider;
        this.workingAlgId = parentCertIssuedValidation.workingAlgId;
        this.workingIssuerName = parentCertIssuedValidation.workingIssuerName;
        this.workingPublicKey = parentCertIssuedValidation.workingPublicKey;
    }
}
