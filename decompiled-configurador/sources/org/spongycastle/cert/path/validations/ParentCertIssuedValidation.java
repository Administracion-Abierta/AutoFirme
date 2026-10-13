package org.spongycastle.cert.path.validations;

import java.io.IOException;
import org.spongycastle.asn1.ASN1Encodable;
import org.spongycastle.asn1.ASN1Null;
import org.spongycastle.asn1.x500.X500Name;
import org.spongycastle.asn1.x509.AlgorithmIdentifier;
import org.spongycastle.asn1.x509.SubjectPublicKeyInfo;
import org.spongycastle.cert.CertException;
import org.spongycastle.cert.X509CertificateHolder;
import org.spongycastle.cert.X509ContentVerifierProviderBuilder;
import org.spongycastle.cert.path.CertPathValidation;
import org.spongycastle.cert.path.CertPathValidationContext;
import org.spongycastle.cert.path.CertPathValidationException;
import org.spongycastle.operator.OperatorCreationException;
import org.spongycastle.util.Memoable;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:org/spongycastle/cert/path/validations/ParentCertIssuedValidation.class */
public class ParentCertIssuedValidation implements CertPathValidation {
    private X509ContentVerifierProviderBuilder contentVerifierProvider;
    private X500Name workingIssuerName;
    private SubjectPublicKeyInfo workingPublicKey;
    private AlgorithmIdentifier workingAlgId;

    public ParentCertIssuedValidation(X509ContentVerifierProviderBuilder contentVerifierProvider) {
        this.contentVerifierProvider = contentVerifierProvider;
    }

    @Override // org.spongycastle.cert.path.CertPathValidation
    public void validate(CertPathValidationContext context, X509CertificateHolder certificate) throws CertPathValidationException {
        SubjectPublicKeyInfo validatingKeyInfo;
        if (this.workingIssuerName != null && !this.workingIssuerName.equals(certificate.getIssuer())) {
            throw new CertPathValidationException("Certificate issue does not match parent");
        }
        if (this.workingPublicKey != null) {
            try {
                if (this.workingPublicKey.getAlgorithm().equals(this.workingAlgId)) {
                    validatingKeyInfo = this.workingPublicKey;
                } else {
                    validatingKeyInfo = new SubjectPublicKeyInfo(this.workingAlgId, this.workingPublicKey.parsePublicKey());
                }
                if (!certificate.isSignatureValid(this.contentVerifierProvider.build(validatingKeyInfo))) {
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
        this.workingIssuerName = certificate.getSubject();
        this.workingPublicKey = certificate.getSubjectPublicKeyInfo();
        if (this.workingAlgId != null) {
            if (this.workingPublicKey.getAlgorithm().getAlgorithm().equals(this.workingAlgId.getAlgorithm())) {
                if (!isNull(this.workingPublicKey.getAlgorithm().getParameters())) {
                    this.workingAlgId = this.workingPublicKey.getAlgorithm();
                    return;
                }
                return;
            }
            this.workingAlgId = this.workingPublicKey.getAlgorithm();
            return;
        }
        this.workingAlgId = this.workingPublicKey.getAlgorithm();
    }

    private boolean isNull(ASN1Encodable obj) {
        return obj == null || (obj instanceof ASN1Null);
    }

    @Override // org.spongycastle.util.Memoable
    public Memoable copy() {
        ParentCertIssuedValidation v = new ParentCertIssuedValidation(this.contentVerifierProvider);
        v.workingAlgId = this.workingAlgId;
        v.workingIssuerName = this.workingIssuerName;
        v.workingPublicKey = this.workingPublicKey;
        return v;
    }

    @Override // org.spongycastle.util.Memoable
    public void reset(Memoable other) {
        ParentCertIssuedValidation v = (ParentCertIssuedValidation) other;
        this.contentVerifierProvider = v.contentVerifierProvider;
        this.workingAlgId = v.workingAlgId;
        this.workingIssuerName = v.workingIssuerName;
        this.workingPublicKey = v.workingPublicKey;
    }
}
