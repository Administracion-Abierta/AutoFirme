package org.bouncycastle.its.jcajce;

import es.gob.afirma.core.signers.AOSignConstants;
import java.io.IOException;
import java.io.OutputStream;
import java.security.PrivateKey;
import java.security.Provider;
import java.security.Signature;
import java.security.interfaces.ECPrivateKey;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.nist.NISTObjectIdentifiers;
import org.bouncycastle.asn1.pkcs.PrivateKeyInfo;
import org.bouncycastle.asn1.sec.SECObjectIdentifiers;
import org.bouncycastle.asn1.teletrust.TeleTrusTObjectIdentifiers;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.its.ITSCertificate;
import org.bouncycastle.its.operator.ITSContentSigner;
import org.bouncycastle.jcajce.util.DefaultJcaJceHelper;
import org.bouncycastle.jcajce.util.JcaJceHelper;
import org.bouncycastle.jcajce.util.NamedJcaJceHelper;
import org.bouncycastle.jcajce.util.ProviderJcaJceHelper;
import org.bouncycastle.operator.DigestCalculator;
import org.bouncycastle.operator.OperatorCreationException;
import org.bouncycastle.operator.jcajce.JcaDigestCalculatorProviderBuilder;
import org.bouncycastle.util.Arrays;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/its/jcajce/JcaITSContentSigner.class */
public class JcaITSContentSigner implements ITSContentSigner {
    private final ECPrivateKey privateKey;
    private final ITSCertificate signerCert;
    private final AlgorithmIdentifier digestAlgo;
    private final DigestCalculator digest;
    private final byte[] parentData;
    private final ASN1ObjectIdentifier curveID;
    private final byte[] parentDigest;
    private final String signer;
    private final JcaJceHelper helper;

    /* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/its/jcajce/JcaITSContentSigner$Builder.class */
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

        public JcaITSContentSigner build(PrivateKey privateKey) {
            return new JcaITSContentSigner((ECPrivateKey) privateKey, null, this.helper);
        }

        public JcaITSContentSigner build(PrivateKey privateKey, ITSCertificate iTSCertificate) {
            return new JcaITSContentSigner((ECPrivateKey) privateKey, iTSCertificate, this.helper);
        }
    }

    private JcaITSContentSigner(ECPrivateKey eCPrivateKey, ITSCertificate iTSCertificate, JcaJceHelper jcaJceHelper) {
        this.privateKey = eCPrivateKey;
        this.signerCert = iTSCertificate;
        this.helper = jcaJceHelper;
        this.curveID = ASN1ObjectIdentifier.getInstance(PrivateKeyInfo.getInstance(eCPrivateKey.getEncoded()).getPrivateKeyAlgorithm().getParameters());
        if (this.curveID.equals((ASN1Primitive) SECObjectIdentifiers.secp256r1) || this.curveID.equals((ASN1Primitive) TeleTrusTObjectIdentifiers.brainpoolP256r1)) {
            this.digestAlgo = new AlgorithmIdentifier(NISTObjectIdentifiers.id_sha256);
            this.signer = AOSignConstants.SIGN_ALGORITHM_SHA256WITHECDSA;
        } else {
            if (!this.curveID.equals((ASN1Primitive) TeleTrusTObjectIdentifiers.brainpoolP384r1)) {
                throw new IllegalArgumentException("unknown key type");
            }
            this.digestAlgo = new AlgorithmIdentifier(NISTObjectIdentifiers.id_sha384);
            this.signer = AOSignConstants.SIGN_ALGORITHM_SHA384WITHECDSA;
        }
        try {
            try {
                this.digest = new JcaDigestCalculatorProviderBuilder().setHelper(jcaJceHelper).build().get(this.digestAlgo);
                if (iTSCertificate == null) {
                    this.parentData = null;
                    this.parentDigest = this.digest.getDigest();
                    return;
                }
                try {
                    this.parentData = iTSCertificate.getEncoded();
                    OutputStream outputStream = this.digest.getOutputStream();
                    outputStream.write(this.parentData, 0, this.parentData.length);
                    outputStream.close();
                    this.parentDigest = this.digest.getDigest();
                } catch (IOException e) {
                    throw new IllegalStateException("signer certificate encoding failed: " + e.getMessage());
                }
            } catch (OperatorCreationException e2) {
                throw new IllegalStateException("cannot recognise digest type: " + this.digestAlgo.getAlgorithm(), e2);
            }
        } catch (Exception e3) {
            throw new IllegalStateException(e3.getMessage(), e3);
        }
    }

    @Override // org.bouncycastle.its.operator.ITSContentSigner
    public OutputStream getOutputStream() {
        return this.digest.getOutputStream();
    }

    @Override // org.bouncycastle.its.operator.ITSContentSigner
    public byte[] getSignature() {
        byte[] digest = this.digest.getDigest();
        try {
            Signature signatureCreateSignature = this.helper.createSignature(this.signer);
            signatureCreateSignature.initSign(this.privateKey);
            signatureCreateSignature.update(digest, 0, digest.length);
            signatureCreateSignature.update(this.parentDigest, 0, this.parentDigest.length);
            return signatureCreateSignature.sign();
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    @Override // org.bouncycastle.its.operator.ITSContentSigner
    public ITSCertificate getAssociatedCertificate() {
        return this.signerCert;
    }

    @Override // org.bouncycastle.its.operator.ITSContentSigner
    public byte[] getAssociatedCertificateDigest() {
        return Arrays.clone(this.parentDigest);
    }

    @Override // org.bouncycastle.its.operator.ITSContentSigner
    public AlgorithmIdentifier getDigestAlgorithm() {
        return this.digestAlgo;
    }

    @Override // org.bouncycastle.its.operator.ITSContentSigner
    public ASN1ObjectIdentifier getCurveID() {
        return this.curveID;
    }

    @Override // org.bouncycastle.its.operator.ITSContentSigner
    public boolean isForSelfSigning() {
        return this.parentData == null;
    }
}
