package org.bouncycastle.its.jcajce;

import es.gob.afirma.core.signers.AOSignConstants;
import java.io.IOException;
import java.io.OutputStream;
import java.security.Provider;
import java.security.Signature;
import java.security.interfaces.ECPublicKey;
import org.bouncycastle.asn1.nist.NISTObjectIdentifiers;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.its.ITSCertificate;
import org.bouncycastle.its.ITSPublicVerificationKey;
import org.bouncycastle.its.operator.ITSContentVerifierProvider;
import org.bouncycastle.jcajce.util.DefaultJcaJceHelper;
import org.bouncycastle.jcajce.util.JcaJceHelper;
import org.bouncycastle.jcajce.util.NamedJcaJceHelper;
import org.bouncycastle.jcajce.util.ProviderJcaJceHelper;
import org.bouncycastle.oer.OEREncoder;
import org.bouncycastle.oer.its.ieee1609dot2.VerificationKeyIndicator;
import org.bouncycastle.oer.its.ieee1609dot2.basetypes.PublicVerificationKey;
import org.bouncycastle.oer.its.template.ieee1609dot2.IEEE1609dot2;
import org.bouncycastle.operator.ContentVerifier;
import org.bouncycastle.operator.DigestCalculator;
import org.bouncycastle.operator.OperatorCreationException;
import org.bouncycastle.operator.jcajce.JcaDigestCalculatorProviderBuilder;
import org.bouncycastle.util.Arrays;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/its/jcajce/JcaITSContentVerifierProvider.class */
public class JcaITSContentVerifierProvider implements ITSContentVerifierProvider {
    private final ITSCertificate issuer;
    private final byte[] parentData;
    private final JcaJceHelper helper;
    private AlgorithmIdentifier digestAlgo;
    private ECPublicKey pubParams;
    private int sigChoice;

    /* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/its/jcajce/JcaITSContentVerifierProvider$Builder.class */
    public static class Builder {
        private JcaJceHelper helper = new DefaultJcaJceHelper();

        public Builder setProvider(Provider provider) {
            this.helper = new ProviderJcaJceHelper(provider);
            return this;
        }

        public Builder setProvider(String str) {
            this.helper = new NamedJcaJceHelper(str);
            return this;
        }

        public JcaITSContentVerifierProvider build(ITSCertificate iTSCertificate) {
            return new JcaITSContentVerifierProvider(iTSCertificate, this.helper);
        }

        public JcaITSContentVerifierProvider build(ITSPublicVerificationKey iTSPublicVerificationKey) {
            return new JcaITSContentVerifierProvider(iTSPublicVerificationKey, this.helper);
        }
    }

    private JcaITSContentVerifierProvider(ITSCertificate iTSCertificate, JcaJceHelper jcaJceHelper) {
        this.issuer = iTSCertificate;
        this.helper = jcaJceHelper;
        try {
            this.parentData = iTSCertificate.getEncoded();
            VerificationKeyIndicator verifyKeyIndicator = iTSCertificate.toASN1Structure().getToBeSigned().getVerifyKeyIndicator();
            if (!(verifyKeyIndicator.getVerificationKeyIndicator() instanceof PublicVerificationKey)) {
                throw new IllegalArgumentException("not public verification key");
            }
            initForPvi(PublicVerificationKey.getInstance(verifyKeyIndicator.getVerificationKeyIndicator()), jcaJceHelper);
        } catch (IOException e) {
            throw new IllegalStateException("unable to extract parent data: " + e.getMessage());
        }
    }

    private JcaITSContentVerifierProvider(ITSPublicVerificationKey iTSPublicVerificationKey, JcaJceHelper jcaJceHelper) {
        this.issuer = null;
        this.parentData = null;
        this.helper = jcaJceHelper;
        initForPvi(iTSPublicVerificationKey.toASN1Structure(), jcaJceHelper);
    }

    private void initForPvi(PublicVerificationKey publicVerificationKey, JcaJceHelper jcaJceHelper) {
        this.sigChoice = publicVerificationKey.getChoice();
        switch (publicVerificationKey.getChoice()) {
            case 0:
                this.digestAlgo = new AlgorithmIdentifier(NISTObjectIdentifiers.id_sha256);
                break;
            case 1:
                this.digestAlgo = new AlgorithmIdentifier(NISTObjectIdentifiers.id_sha256);
                break;
            case 2:
                this.digestAlgo = new AlgorithmIdentifier(NISTObjectIdentifiers.id_sha384);
                break;
            default:
                throw new IllegalArgumentException("unknown key type");
        }
        this.pubParams = (ECPublicKey) new JcaITSPublicVerificationKey(publicVerificationKey, jcaJceHelper).getKey();
    }

    @Override // org.bouncycastle.its.operator.ITSContentVerifierProvider
    public boolean hasAssociatedCertificate() {
        return this.issuer != null;
    }

    @Override // org.bouncycastle.its.operator.ITSContentVerifierProvider
    public ITSCertificate getAssociatedCertificate() {
        return this.issuer;
    }

    @Override // org.bouncycastle.its.operator.ITSContentVerifierProvider
    public ContentVerifier get(int i) throws OperatorCreationException {
        byte[] digest;
        Signature signatureCreateSignature;
        if (this.sigChoice != i) {
            throw new OperatorCreationException("wrong verifier for algorithm: " + i);
        }
        try {
            final DigestCalculator digestCalculator = new JcaDigestCalculatorProviderBuilder().setHelper(this.helper).build().get(this.digestAlgo);
            try {
                final OutputStream outputStream = digestCalculator.getOutputStream();
                if (this.parentData != null) {
                    outputStream.write(this.parentData, 0, this.parentData.length);
                }
                final byte[] digest2 = digestCalculator.getDigest();
                if (this.issuer == null || !this.issuer.getIssuer().isSelf()) {
                    digest = null;
                } else {
                    byte[] byteArray = OEREncoder.toByteArray(this.issuer.toASN1Structure().getToBeSigned(), IEEE1609dot2.ToBeSignedCertificate.build());
                    outputStream.write(byteArray, 0, byteArray.length);
                    digest = digestCalculator.getDigest();
                }
                switch (this.sigChoice) {
                    case 0:
                    case 1:
                        signatureCreateSignature = this.helper.createSignature(AOSignConstants.SIGN_ALGORITHM_SHA256WITHECDSA);
                        break;
                    case 2:
                        signatureCreateSignature = this.helper.createSignature(AOSignConstants.SIGN_ALGORITHM_SHA384WITHECDSA);
                        break;
                    default:
                        throw new IllegalArgumentException("choice " + this.sigChoice + " not supported");
                }
                final Signature signature = signatureCreateSignature;
                final byte[] bArr = digest;
                return new ContentVerifier() { // from class: org.bouncycastle.its.jcajce.JcaITSContentVerifierProvider.1
                    @Override // org.bouncycastle.operator.ContentVerifier
                    public AlgorithmIdentifier getAlgorithmIdentifier() {
                        return null;
                    }

                    @Override // org.bouncycastle.operator.ContentVerifier
                    public OutputStream getOutputStream() {
                        return outputStream;
                    }

                    @Override // org.bouncycastle.operator.ContentVerifier
                    public boolean verify(byte[] bArr2) {
                        byte[] digest3 = digestCalculator.getDigest();
                        try {
                            signature.initVerify(JcaITSContentVerifierProvider.this.pubParams);
                            signature.update(digest3);
                            if (bArr == null || !Arrays.areEqual(digest3, bArr)) {
                                signature.update(digest2);
                            } else {
                                signature.update(digestCalculator.getDigest());
                            }
                            return signature.verify(bArr2);
                        } catch (Exception e) {
                            throw new RuntimeException(e.getMessage(), e);
                        }
                    }
                };
            } catch (Exception e) {
                throw new IllegalStateException(e.getMessage(), e);
            }
        } catch (Exception e2) {
            throw new IllegalStateException(e2.getMessage(), e2);
        }
    }
}
