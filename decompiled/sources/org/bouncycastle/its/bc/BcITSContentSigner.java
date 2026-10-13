package org.bouncycastle.its.bc;

import java.io.IOException;
import java.io.OutputStream;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.nist.NISTObjectIdentifiers;
import org.bouncycastle.asn1.sec.SECObjectIdentifiers;
import org.bouncycastle.asn1.teletrust.TeleTrusTObjectIdentifiers;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.crypto.Digest;
import org.bouncycastle.crypto.io.DigestOutputStream;
import org.bouncycastle.crypto.params.ECNamedDomainParameters;
import org.bouncycastle.crypto.params.ECPrivateKeyParameters;
import org.bouncycastle.crypto.signers.DSADigestSigner;
import org.bouncycastle.crypto.signers.ECDSASigner;
import org.bouncycastle.its.ITSCertificate;
import org.bouncycastle.its.operator.ITSContentSigner;
import org.bouncycastle.operator.OperatorCreationException;
import org.bouncycastle.operator.bc.BcDefaultDigestProvider;
import org.bouncycastle.util.Arrays;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/its/bc/BcITSContentSigner.class */
public class BcITSContentSigner implements ITSContentSigner {
    private final ECPrivateKeyParameters privKey;
    private final ITSCertificate signerCert;
    private final AlgorithmIdentifier digestAlgo;
    private final Digest digest;
    private final byte[] parentData;
    private final ASN1ObjectIdentifier curveID;
    private final byte[] parentDigest;

    public BcITSContentSigner(ECPrivateKeyParameters eCPrivateKeyParameters) {
        this(eCPrivateKeyParameters, null);
    }

    public BcITSContentSigner(ECPrivateKeyParameters eCPrivateKeyParameters, ITSCertificate iTSCertificate) {
        this.privKey = eCPrivateKeyParameters;
        this.curveID = ((ECNamedDomainParameters) eCPrivateKeyParameters.getParameters()).getName();
        this.signerCert = iTSCertificate;
        if (this.curveID.equals((ASN1Primitive) SECObjectIdentifiers.secp256r1) || this.curveID.equals((ASN1Primitive) TeleTrusTObjectIdentifiers.brainpoolP256r1)) {
            this.digestAlgo = new AlgorithmIdentifier(NISTObjectIdentifiers.id_sha256);
        } else {
            if (!this.curveID.equals((ASN1Primitive) TeleTrusTObjectIdentifiers.brainpoolP384r1)) {
                throw new IllegalArgumentException("unknown key type");
            }
            this.digestAlgo = new AlgorithmIdentifier(NISTObjectIdentifiers.id_sha384);
        }
        try {
            this.digest = BcDefaultDigestProvider.INSTANCE.get(this.digestAlgo);
            if (iTSCertificate == null) {
                this.parentData = null;
                this.parentDigest = new byte[this.digest.getDigestSize()];
                this.digest.doFinal(this.parentDigest, 0);
            } else {
                try {
                    this.parentData = iTSCertificate.getEncoded();
                    this.parentDigest = new byte[this.digest.getDigestSize()];
                    this.digest.update(this.parentData, 0, this.parentData.length);
                    this.digest.doFinal(this.parentDigest, 0);
                } catch (IOException e) {
                    throw new IllegalStateException("signer certificate encoding failed: " + e.getMessage());
                }
            }
        } catch (OperatorCreationException e2) {
            throw new IllegalStateException("cannot recognise digest type: " + this.digestAlgo.getAlgorithm());
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
    public OutputStream getOutputStream() {
        return new DigestOutputStream(this.digest);
    }

    @Override // org.bouncycastle.its.operator.ITSContentSigner
    public boolean isForSelfSigning() {
        return this.parentData == null;
    }

    @Override // org.bouncycastle.its.operator.ITSContentSigner
    public ASN1ObjectIdentifier getCurveID() {
        return this.curveID;
    }

    @Override // org.bouncycastle.its.operator.ITSContentSigner
    public byte[] getSignature() {
        byte[] bArr = new byte[this.digest.getDigestSize()];
        this.digest.doFinal(bArr, 0);
        DSADigestSigner dSADigestSigner = new DSADigestSigner(new ECDSASigner(), this.digest);
        dSADigestSigner.init(true, this.privKey);
        dSADigestSigner.update(bArr, 0, bArr.length);
        dSADigestSigner.update(this.parentDigest, 0, this.parentDigest.length);
        return dSADigestSigner.generateSignature();
    }
}
