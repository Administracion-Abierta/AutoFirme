package org.spongycastle.mozilla;

import java.io.IOException;
import java.io.OutputStream;
import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PublicKey;
import java.security.Signature;
import java.security.SignatureException;
import java.security.spec.X509EncodedKeySpec;
import org.spongycastle.asn1.ASN1Primitive;
import org.spongycastle.asn1.DERBitString;
import org.spongycastle.asn1.DEROutputStream;
import org.spongycastle.asn1.mozilla.PublicKeyAndChallenge;
import org.spongycastle.asn1.x509.AlgorithmIdentifier;
import org.spongycastle.asn1.x509.SubjectPublicKeyInfo;
import org.spongycastle.operator.ContentVerifier;
import org.spongycastle.operator.ContentVerifierProvider;
import org.spongycastle.operator.OperatorCreationException;
import org.spongycastle.util.Encodable;

/* JADX INFO: loaded from: autofirma.jar:org/spongycastle/mozilla/SignedPublicKeyAndChallenge.class */
public class SignedPublicKeyAndChallenge implements Encodable {
    protected final org.spongycastle.asn1.mozilla.SignedPublicKeyAndChallenge spkacSeq;

    public SignedPublicKeyAndChallenge(byte[] bytes) {
        this.spkacSeq = org.spongycastle.asn1.mozilla.SignedPublicKeyAndChallenge.getInstance(bytes);
    }

    protected SignedPublicKeyAndChallenge(org.spongycastle.asn1.mozilla.SignedPublicKeyAndChallenge struct) {
        this.spkacSeq = struct;
    }

    public org.spongycastle.asn1.mozilla.SignedPublicKeyAndChallenge toASN1Structure() {
        return this.spkacSeq;
    }

    public ASN1Primitive toASN1Primitive() {
        return this.spkacSeq.toASN1Primitive();
    }

    public PublicKeyAndChallenge getPublicKeyAndChallenge() {
        return this.spkacSeq.getPublicKeyAndChallenge();
    }

    public boolean isSignatureValid(ContentVerifierProvider verifierProvider) throws OperatorCreationException, IOException {
        ContentVerifier verifier = verifierProvider.get(this.spkacSeq.getSignatureAlgorithm());
        OutputStream sOut = verifier.getOutputStream();
        DEROutputStream dOut = new DEROutputStream(sOut);
        dOut.writeObject(this.spkacSeq.getPublicKeyAndChallenge());
        sOut.close();
        return verifier.verify(this.spkacSeq.getSignature().getOctets());
    }

    public boolean verify() throws NoSuchAlgorithmException, SignatureException, InvalidKeyException, NoSuchProviderException {
        return verify((String) null);
    }

    public boolean verify(String provider) throws NoSuchAlgorithmException, SignatureException, InvalidKeyException, NoSuchProviderException {
        Signature sig;
        if (provider == null) {
            sig = Signature.getInstance(this.spkacSeq.getSignatureAlgorithm().getAlgorithm().getId());
        } else {
            sig = Signature.getInstance(this.spkacSeq.getSignatureAlgorithm().getAlgorithm().getId(), provider);
        }
        PublicKey pubKey = getPublicKey(provider);
        sig.initVerify(pubKey);
        try {
            sig.update(this.spkacSeq.getPublicKeyAndChallenge().getEncoded());
            return sig.verify(this.spkacSeq.getSignature().getBytes());
        } catch (Exception e) {
            throw new InvalidKeyException("error encoding public key");
        }
    }

    public SubjectPublicKeyInfo getSubjectPublicKeyInfo() {
        return this.spkacSeq.getPublicKeyAndChallenge().getSubjectPublicKeyInfo();
    }

    public String getChallenge() {
        return this.spkacSeq.getPublicKeyAndChallenge().getChallenge().getString();
    }

    public PublicKey getPublicKey(String provider) throws NoSuchAlgorithmException, InvalidKeyException, NoSuchProviderException {
        SubjectPublicKeyInfo subjectPKInfo = this.spkacSeq.getPublicKeyAndChallenge().getSubjectPublicKeyInfo();
        try {
            DERBitString bStr = new DERBitString(subjectPKInfo);
            X509EncodedKeySpec xspec = new X509EncodedKeySpec(bStr.getOctets());
            AlgorithmIdentifier keyAlg = subjectPKInfo.getAlgorithm();
            KeyFactory factory = KeyFactory.getInstance(keyAlg.getAlgorithm().getId(), provider);
            return factory.generatePublic(xspec);
        } catch (Exception e) {
            throw new InvalidKeyException("error encoding public key");
        }
    }

    @Override // org.spongycastle.util.Encodable
    public byte[] getEncoded() throws IOException {
        return toASN1Structure().getEncoded();
    }
}
