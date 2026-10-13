package org.bouncycastle.jcajce.provider.asymmetric.rsa;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.math.BigInteger;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.RSAPublicKeySpec;
import org.apache.xml.security.keys.content.x509.XMLX509Certificate;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.DERNull;
import org.bouncycastle.asn1.pkcs.PKCSObjectIdentifiers;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.asn1.x509.SubjectPublicKeyInfo;
import org.bouncycastle.crypto.params.RSAKeyParameters;
import org.bouncycastle.jcajce.provider.asymmetric.util.KeyUtil;
import org.bouncycastle.util.Strings;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/jcajce/provider/asymmetric/rsa/BCRSAPublicKey.class */
public class BCRSAPublicKey implements RSAPublicKey {
    static final AlgorithmIdentifier DEFAULT_ALGORITHM_IDENTIFIER = new AlgorithmIdentifier(PKCSObjectIdentifiers.rsaEncryption, DERNull.INSTANCE);
    static final long serialVersionUID = 2675817738516720772L;
    private BigInteger modulus;
    private BigInteger publicExponent;
    private transient AlgorithmIdentifier algorithmIdentifier;
    private transient RSAKeyParameters rsaPublicKey;

    BCRSAPublicKey(RSAKeyParameters rSAKeyParameters) {
        this(DEFAULT_ALGORITHM_IDENTIFIER, rSAKeyParameters);
    }

    BCRSAPublicKey(AlgorithmIdentifier algorithmIdentifier, RSAKeyParameters rSAKeyParameters) {
        this.algorithmIdentifier = algorithmIdentifier;
        this.modulus = rSAKeyParameters.getModulus();
        this.publicExponent = rSAKeyParameters.getExponent();
        this.rsaPublicKey = rSAKeyParameters;
    }

    BCRSAPublicKey(RSAPublicKeySpec rSAPublicKeySpec) {
        this.algorithmIdentifier = DEFAULT_ALGORITHM_IDENTIFIER;
        this.modulus = rSAPublicKeySpec.getModulus();
        this.publicExponent = rSAPublicKeySpec.getPublicExponent();
        this.rsaPublicKey = new RSAKeyParameters(false, this.modulus, this.publicExponent);
    }

    BCRSAPublicKey(RSAPublicKey rSAPublicKey) {
        this.algorithmIdentifier = DEFAULT_ALGORITHM_IDENTIFIER;
        this.modulus = rSAPublicKey.getModulus();
        this.publicExponent = rSAPublicKey.getPublicExponent();
        this.rsaPublicKey = new RSAKeyParameters(false, this.modulus, this.publicExponent);
    }

    BCRSAPublicKey(SubjectPublicKeyInfo subjectPublicKeyInfo) {
        populateFromPublicKeyInfo(subjectPublicKeyInfo);
    }

    private void populateFromPublicKeyInfo(SubjectPublicKeyInfo subjectPublicKeyInfo) {
        try {
            org.bouncycastle.asn1.pkcs.RSAPublicKey rSAPublicKey = org.bouncycastle.asn1.pkcs.RSAPublicKey.getInstance(subjectPublicKeyInfo.parsePublicKey());
            this.algorithmIdentifier = subjectPublicKeyInfo.getAlgorithm();
            this.modulus = rSAPublicKey.getModulus();
            this.publicExponent = rSAPublicKey.getPublicExponent();
            this.rsaPublicKey = new RSAKeyParameters(false, this.modulus, this.publicExponent);
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
        return this.algorithmIdentifier.getAlgorithm().equals((ASN1Primitive) PKCSObjectIdentifiers.id_RSASSA_PSS) ? "RSASSA-PSS" : "RSA";
    }

    @Override // java.security.Key
    public String getFormat() {
        return XMLX509Certificate.JCA_CERT_ID;
    }

    @Override // java.security.Key
    public byte[] getEncoded() {
        return KeyUtil.getEncodedSubjectPublicKeyInfo(this.algorithmIdentifier, new org.bouncycastle.asn1.pkcs.RSAPublicKey(getModulus(), getPublicExponent()));
    }

    RSAKeyParameters engineGetKeyParameters() {
        return this.rsaPublicKey;
    }

    public int hashCode() {
        return getModulus().hashCode() ^ getPublicExponent().hashCode();
    }

    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof RSAPublicKey)) {
            return false;
        }
        RSAPublicKey rSAPublicKey = (RSAPublicKey) obj;
        return getModulus().equals(rSAPublicKey.getModulus()) && getPublicExponent().equals(rSAPublicKey.getPublicExponent());
    }

    public String toString() {
        StringBuffer stringBuffer = new StringBuffer();
        String strLineSeparator = Strings.lineSeparator();
        stringBuffer.append("RSA Public Key [").append(RSAUtil.generateKeyFingerprint(getModulus())).append("]").append(",[").append(RSAUtil.generateExponentFingerprint(getPublicExponent())).append("]").append(strLineSeparator);
        stringBuffer.append("        modulus: ").append(getModulus().toString(16)).append(strLineSeparator);
        stringBuffer.append("public exponent: ").append(getPublicExponent().toString(16)).append(strLineSeparator);
        return stringBuffer.toString();
    }

    private void readObject(ObjectInputStream objectInputStream) throws ClassNotFoundException, IOException {
        objectInputStream.defaultReadObject();
        try {
            this.algorithmIdentifier = AlgorithmIdentifier.getInstance(objectInputStream.readObject());
        } catch (Exception e) {
            this.algorithmIdentifier = DEFAULT_ALGORITHM_IDENTIFIER;
        }
        this.rsaPublicKey = new RSAKeyParameters(false, this.modulus, this.publicExponent);
    }

    private void writeObject(ObjectOutputStream objectOutputStream) throws IOException {
        objectOutputStream.defaultWriteObject();
        if (this.algorithmIdentifier.equals(DEFAULT_ALGORITHM_IDENTIFIER)) {
            return;
        }
        objectOutputStream.writeObject(this.algorithmIdentifier.getEncoded());
    }
}
