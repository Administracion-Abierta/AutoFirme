package org.spongycastle.jce.netscape;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.Signature;
import java.security.SignatureException;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;
import org.spongycastle.asn1.ASN1EncodableVector;
import org.spongycastle.asn1.ASN1Encoding;
import org.spongycastle.asn1.ASN1InputStream;
import org.spongycastle.asn1.ASN1Object;
import org.spongycastle.asn1.ASN1Primitive;
import org.spongycastle.asn1.ASN1Sequence;
import org.spongycastle.asn1.DERBitString;
import org.spongycastle.asn1.DERIA5String;
import org.spongycastle.asn1.DERSequence;
import org.spongycastle.asn1.x509.AlgorithmIdentifier;
import org.spongycastle.asn1.x509.SubjectPublicKeyInfo;
import org.spongycastle.jce.provider.BouncyCastleProvider;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:org/spongycastle/jce/netscape/NetscapeCertRequest.class */
public class NetscapeCertRequest extends ASN1Object {
    AlgorithmIdentifier sigAlg;
    AlgorithmIdentifier keyAlg;
    byte[] sigBits;
    String challenge;
    DERBitString content;
    PublicKey pubkey;

    private static ASN1Sequence getReq(byte[] r) throws IOException {
        ASN1InputStream aIn = new ASN1InputStream(new ByteArrayInputStream(r));
        return ASN1Sequence.getInstance(aIn.readObject());
    }

    public NetscapeCertRequest(byte[] req) throws IOException {
        this(getReq(req));
    }

    public NetscapeCertRequest(ASN1Sequence spkac) {
        try {
            if (spkac.size() != 3) {
                throw new IllegalArgumentException("invalid SPKAC (size):" + spkac.size());
            }
            this.sigAlg = AlgorithmIdentifier.getInstance(spkac.getObjectAt(1));
            this.sigBits = ((DERBitString) spkac.getObjectAt(2)).getOctets();
            ASN1Sequence pkac = (ASN1Sequence) spkac.getObjectAt(0);
            if (pkac.size() != 2) {
                throw new IllegalArgumentException("invalid PKAC (len): " + pkac.size());
            }
            this.challenge = ((DERIA5String) pkac.getObjectAt(1)).getString();
            this.content = new DERBitString(pkac);
            SubjectPublicKeyInfo pubkeyinfo = SubjectPublicKeyInfo.getInstance(pkac.getObjectAt(0));
            X509EncodedKeySpec xspec = new X509EncodedKeySpec(new DERBitString(pubkeyinfo).getBytes());
            this.keyAlg = pubkeyinfo.getAlgorithm();
            this.pubkey = KeyFactory.getInstance(this.keyAlg.getAlgorithm().getId(), BouncyCastleProvider.PROVIDER_NAME).generatePublic(xspec);
        } catch (Exception e) {
            throw new IllegalArgumentException(e.toString());
        }
    }

    public NetscapeCertRequest(String challenge, AlgorithmIdentifier signing_alg, PublicKey pub_key) throws InvalidKeySpecException, NoSuchAlgorithmException, NoSuchProviderException {
        this.challenge = challenge;
        this.sigAlg = signing_alg;
        this.pubkey = pub_key;
        ASN1EncodableVector content_der = new ASN1EncodableVector();
        content_der.add(getKeySpec());
        content_der.add(new DERIA5String(challenge));
        try {
            this.content = new DERBitString(new DERSequence(content_der));
        } catch (IOException e) {
            throw new InvalidKeySpecException("exception encoding key: " + e.toString());
        }
    }

    public String getChallenge() {
        return this.challenge;
    }

    public void setChallenge(String value) {
        this.challenge = value;
    }

    public AlgorithmIdentifier getSigningAlgorithm() {
        return this.sigAlg;
    }

    public void setSigningAlgorithm(AlgorithmIdentifier value) {
        this.sigAlg = value;
    }

    public AlgorithmIdentifier getKeyAlgorithm() {
        return this.keyAlg;
    }

    public void setKeyAlgorithm(AlgorithmIdentifier value) {
        this.keyAlg = value;
    }

    public PublicKey getPublicKey() {
        return this.pubkey;
    }

    public void setPublicKey(PublicKey value) {
        this.pubkey = value;
    }

    public boolean verify(String challenge) throws NoSuchAlgorithmException, SignatureException, InvalidKeyException, NoSuchProviderException {
        if (!challenge.equals(this.challenge)) {
            return false;
        }
        Signature sig = Signature.getInstance(this.sigAlg.getAlgorithm().getId(), BouncyCastleProvider.PROVIDER_NAME);
        sig.initVerify(this.pubkey);
        sig.update(this.content.getBytes());
        return sig.verify(this.sigBits);
    }

    public void sign(PrivateKey priv_key) throws InvalidKeySpecException, NoSuchAlgorithmException, SignatureException, InvalidKeyException, NoSuchProviderException {
        sign(priv_key, null);
    }

    public void sign(PrivateKey priv_key, SecureRandom rand) throws InvalidKeySpecException, NoSuchAlgorithmException, SignatureException, InvalidKeyException, NoSuchProviderException {
        Signature sig = Signature.getInstance(this.sigAlg.getAlgorithm().getId(), BouncyCastleProvider.PROVIDER_NAME);
        if (rand != null) {
            sig.initSign(priv_key, rand);
        } else {
            sig.initSign(priv_key);
        }
        ASN1EncodableVector pkac = new ASN1EncodableVector();
        pkac.add(getKeySpec());
        pkac.add(new DERIA5String(this.challenge));
        try {
            sig.update(new DERSequence(pkac).getEncoded(ASN1Encoding.DER));
            this.sigBits = sig.sign();
        } catch (IOException ioe) {
            throw new SignatureException(ioe.getMessage());
        }
    }

    private ASN1Primitive getKeySpec() throws InvalidKeySpecException, NoSuchAlgorithmException, NoSuchProviderException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try {
            baos.write(this.pubkey.getEncoded());
            baos.close();
            ASN1InputStream derin = new ASN1InputStream(new ByteArrayInputStream(baos.toByteArray()));
            ASN1Primitive obj = derin.readObject();
            return obj;
        } catch (IOException ioe) {
            throw new InvalidKeySpecException(ioe.getMessage());
        }
    }

    @Override // org.spongycastle.asn1.ASN1Object, org.spongycastle.asn1.ASN1Encodable
    public ASN1Primitive toASN1Primitive() {
        ASN1EncodableVector spkac = new ASN1EncodableVector();
        ASN1EncodableVector pkac = new ASN1EncodableVector();
        try {
            pkac.add(getKeySpec());
        } catch (Exception e) {
        }
        pkac.add(new DERIA5String(this.challenge));
        spkac.add(new DERSequence(pkac));
        spkac.add(this.sigAlg);
        spkac.add(new DERBitString(this.sigBits));
        return new DERSequence(spkac);
    }
}
