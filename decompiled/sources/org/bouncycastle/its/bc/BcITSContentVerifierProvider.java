package org.bouncycastle.its.bc;

import java.io.IOException;
import java.io.OutputStream;
import org.bouncycastle.asn1.nist.NISTObjectIdentifiers;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.crypto.ExtendedDigest;
import org.bouncycastle.crypto.params.ECPublicKeyParameters;
import org.bouncycastle.crypto.signers.DSADigestSigner;
import org.bouncycastle.crypto.signers.ECDSASigner;
import org.bouncycastle.its.ITSCertificate;
import org.bouncycastle.its.operator.ITSContentVerifierProvider;
import org.bouncycastle.oer.OEREncoder;
import org.bouncycastle.oer.its.ieee1609dot2.VerificationKeyIndicator;
import org.bouncycastle.oer.its.ieee1609dot2.basetypes.PublicVerificationKey;
import org.bouncycastle.oer.its.template.ieee1609dot2.IEEE1609dot2;
import org.bouncycastle.operator.ContentVerifier;
import org.bouncycastle.operator.OperatorCreationException;
import org.bouncycastle.operator.bc.BcDefaultDigestProvider;
import org.bouncycastle.util.Arrays;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/its/bc/BcITSContentVerifierProvider.class */
public class BcITSContentVerifierProvider implements ITSContentVerifierProvider {
    private final ITSCertificate issuer;
    private final byte[] parentData;
    private final AlgorithmIdentifier digestAlgo;
    private final ECPublicKeyParameters pubParams;
    private final int sigChoice;

    public BcITSContentVerifierProvider(ITSCertificate iTSCertificate) throws IOException {
        this.issuer = iTSCertificate;
        this.parentData = iTSCertificate.getEncoded();
        VerificationKeyIndicator verifyKeyIndicator = iTSCertificate.toASN1Structure().getToBeSigned().getVerifyKeyIndicator();
        if (!(verifyKeyIndicator.getVerificationKeyIndicator() instanceof PublicVerificationKey)) {
            throw new IllegalStateException("not public verification key");
        }
        PublicVerificationKey publicVerificationKey = PublicVerificationKey.getInstance(verifyKeyIndicator.getVerificationKeyIndicator());
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
                throw new IllegalStateException("unknown key type");
        }
        this.pubParams = (ECPublicKeyParameters) new BcITSPublicVerificationKey(publicVerificationKey).getKey();
    }

    @Override // org.bouncycastle.its.operator.ITSContentVerifierProvider
    public ITSCertificate getAssociatedCertificate() {
        return this.issuer;
    }

    @Override // org.bouncycastle.its.operator.ITSContentVerifierProvider
    public boolean hasAssociatedCertificate() {
        return this.issuer != null;
    }

    @Override // org.bouncycastle.its.operator.ITSContentVerifierProvider
    public ContentVerifier get(int i) throws OperatorCreationException {
        if (this.sigChoice != i) {
            throw new OperatorCreationException("wrong verifier for algorithm: " + i);
        }
        final ExtendedDigest extendedDigest = BcDefaultDigestProvider.INSTANCE.get(this.digestAlgo);
        final byte[] bArr = new byte[extendedDigest.getDigestSize()];
        extendedDigest.update(this.parentData, 0, this.parentData.length);
        extendedDigest.doFinal(bArr, 0);
        final byte[] bArr2 = this.issuer.getIssuer().isSelf() ? new byte[extendedDigest.getDigestSize()] : null;
        if (bArr2 != null) {
            byte[] byteArray = OEREncoder.toByteArray(this.issuer.toASN1Structure().getToBeSigned(), IEEE1609dot2.ToBeSignedCertificate.build());
            extendedDigest.update(byteArray, 0, byteArray.length);
            extendedDigest.doFinal(bArr2, 0);
        }
        final OutputStream outputStream = new OutputStream() { // from class: org.bouncycastle.its.bc.BcITSContentVerifierProvider.1
            @Override // java.io.OutputStream
            public void write(int i2) throws IOException {
                extendedDigest.update((byte) i2);
            }

            @Override // java.io.OutputStream
            public void write(byte[] bArr3) throws IOException {
                extendedDigest.update(bArr3, 0, bArr3.length);
            }

            @Override // java.io.OutputStream
            public void write(byte[] bArr3, int i2, int i3) throws IOException {
                extendedDigest.update(bArr3, i2, i3);
            }
        };
        return new ContentVerifier() { // from class: org.bouncycastle.its.bc.BcITSContentVerifierProvider.2
            final DSADigestSigner signer;

            {
                this.signer = new DSADigestSigner(new ECDSASigner(), BcDefaultDigestProvider.INSTANCE.get(BcITSContentVerifierProvider.this.digestAlgo));
            }

            @Override // org.bouncycastle.operator.ContentVerifier
            public AlgorithmIdentifier getAlgorithmIdentifier() {
                return null;
            }

            @Override // org.bouncycastle.operator.ContentVerifier
            public OutputStream getOutputStream() {
                return outputStream;
            }

            @Override // org.bouncycastle.operator.ContentVerifier
            public boolean verify(byte[] bArr3) {
                byte[] bArr4 = new byte[extendedDigest.getDigestSize()];
                extendedDigest.doFinal(bArr4, 0);
                this.signer.init(false, BcITSContentVerifierProvider.this.pubParams);
                this.signer.update(bArr4, 0, bArr4.length);
                if (bArr2 == null || !Arrays.areEqual(bArr4, bArr2)) {
                    this.signer.update(bArr, 0, bArr.length);
                } else {
                    byte[] bArr5 = new byte[extendedDigest.getDigestSize()];
                    extendedDigest.doFinal(bArr5, 0);
                    this.signer.update(bArr5, 0, bArr5.length);
                }
                return this.signer.verifySignature(bArr3);
            }
        };
    }
}
