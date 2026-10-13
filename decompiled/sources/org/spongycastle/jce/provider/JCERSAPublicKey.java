package org.spongycastle.jce.provider;

import java.io.IOException;
import java.math.BigInteger;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.RSAPublicKeySpec;
import org.apache.xml.security.keys.content.x509.XMLX509Certificate;
import org.spongycastle.asn1.DERNull;
import org.spongycastle.asn1.pkcs.PKCSObjectIdentifiers;
import org.spongycastle.asn1.x509.AlgorithmIdentifier;
import org.spongycastle.asn1.x509.SubjectPublicKeyInfo;
import org.spongycastle.crypto.params.RSAKeyParameters;
import org.spongycastle.jcajce.provider.asymmetric.util.KeyUtil;
import org.spongycastle.util.Strings;

/* JADX INFO: loaded from: autofirma.jar:org/spongycastle/jce/provider/JCERSAPublicKey.class */
public class JCERSAPublicKey implements RSAPublicKey {
    static final long serialVersionUID = 2675817738516720772L;
    private BigInteger modulus;
    private BigInteger publicExponent;

    JCERSAPublicKey(RSAKeyParameters key) {
        this.modulus = key.getModulus();
        this.publicExponent = key.getExponent();
    }

    JCERSAPublicKey(RSAPublicKeySpec spec) {
        this.modulus = spec.getModulus();
        this.publicExponent = spec.getPublicExponent();
    }

    JCERSAPublicKey(RSAPublicKey key) {
        this.modulus = key.getModulus();
        this.publicExponent = key.getPublicExponent();
    }

    JCERSAPublicKey(SubjectPublicKeyInfo info) {
        try {
            org.spongycastle.asn1.pkcs.RSAPublicKey pubKey = org.spongycastle.asn1.pkcs.RSAPublicKey.getInstance(info.parsePublicKey());
            this.modulus = pubKey.getModulus();
            this.publicExponent = pubKey.getPublicExponent();
        } catch (IOException e) {
            throw new IllegalArgumentException("invalid info structure in RSA public key");
        }
    }

    @Override // java.security.interfaces.RSAKey
    public BigInteger getModulus() {
        return this.modulus;
    }

    @Override // java.security.interfaces.RSAPublicKey
    public BigInteger getPublicExponent() {
        return this.publicExponent;
    }

    @Override // java.security.Key
    public String getAlgorithm() {
        return "RSA";
    }

    @Override // java.security.Key
    public String getFormat() {
        return XMLX509Certificate.JCA_CERT_ID;
    }

    @Override // java.security.Key
    public byte[] getEncoded() {
        return KeyUtil.getEncodedSubjectPublicKeyInfo(new AlgorithmIdentifier(PKCSObjectIdentifiers.rsaEncryption, DERNull.INSTANCE), new org.spongycastle.asn1.pkcs.RSAPublicKey(getModulus(), getPublicExponent()));
    }

    public int hashCode() {
        return getModulus().hashCode() ^ getPublicExponent().hashCode();
    }

    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof RSAPublicKey)) {
            return false;
        }
        RSAPublicKey key = (RSAPublicKey) o;
        return getModulus().equals(key.getModulus()) && getPublicExponent().equals(key.getPublicExponent());
    }

    public String toString() {
        StringBuffer buf = new StringBuffer();
        String nl = Strings.lineSeparator();
        buf.append("RSA Public Key").append(nl);
        buf.append("            modulus: ").append(getModulus().toString(16)).append(nl);
        buf.append("    public exponent: ").append(getPublicExponent().toString(16)).append(nl);
        return buf.toString();
    }
}
