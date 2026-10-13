package org.spongycastle.jcajce.provider.asymmetric.gost;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.math.BigInteger;
import java.util.Enumeration;
import org.spongycastle.asn1.ASN1Encodable;
import org.spongycastle.asn1.ASN1ObjectIdentifier;
import org.spongycastle.asn1.ASN1OctetString;
import org.spongycastle.asn1.ASN1Sequence;
import org.spongycastle.asn1.DEROctetString;
import org.spongycastle.asn1.cryptopro.CryptoProObjectIdentifiers;
import org.spongycastle.asn1.cryptopro.GOST3410PublicKeyAlgParameters;
import org.spongycastle.asn1.pkcs.PrivateKeyInfo;
import org.spongycastle.asn1.x509.AlgorithmIdentifier;
import org.spongycastle.crypto.params.GOST3410PrivateKeyParameters;
import org.spongycastle.jcajce.provider.asymmetric.util.PKCS12BagAttributeCarrierImpl;
import org.spongycastle.jce.interfaces.GOST3410Params;
import org.spongycastle.jce.interfaces.GOST3410PrivateKey;
import org.spongycastle.jce.interfaces.PKCS12BagAttributeCarrier;
import org.spongycastle.jce.spec.GOST3410ParameterSpec;
import org.spongycastle.jce.spec.GOST3410PrivateKeySpec;
import org.spongycastle.jce.spec.GOST3410PublicKeyParameterSetSpec;

/* JADX INFO: loaded from: autofirma.jar:org/spongycastle/jcajce/provider/asymmetric/gost/BCGOST3410PrivateKey.class */
public class BCGOST3410PrivateKey implements GOST3410PrivateKey, PKCS12BagAttributeCarrier {
    static final long serialVersionUID = 8581661527592305464L;
    private BigInteger x;
    private transient GOST3410Params gost3410Spec;
    private transient PKCS12BagAttributeCarrier attrCarrier = new PKCS12BagAttributeCarrierImpl();

    protected BCGOST3410PrivateKey() {
    }

    BCGOST3410PrivateKey(GOST3410PrivateKey key) {
        this.x = key.getX();
        this.gost3410Spec = key.getParameters();
    }

    BCGOST3410PrivateKey(GOST3410PrivateKeySpec spec) {
        this.x = spec.getX();
        this.gost3410Spec = new GOST3410ParameterSpec(new GOST3410PublicKeyParameterSetSpec(spec.getP(), spec.getQ(), spec.getA()));
    }

    BCGOST3410PrivateKey(PrivateKeyInfo info) throws IOException {
        GOST3410PublicKeyAlgParameters params = new GOST3410PublicKeyAlgParameters((ASN1Sequence) info.getAlgorithmId().getParameters());
        ASN1OctetString derX = ASN1OctetString.getInstance(info.parsePrivateKey());
        byte[] keyEnc = derX.getOctets();
        byte[] keyBytes = new byte[keyEnc.length];
        for (int i = 0; i != keyEnc.length; i++) {
            keyBytes[i] = keyEnc[(keyEnc.length - 1) - i];
        }
        this.x = new BigInteger(1, keyBytes);
        this.gost3410Spec = GOST3410ParameterSpec.fromPublicKeyAlg(params);
    }

    BCGOST3410PrivateKey(GOST3410PrivateKeyParameters params, GOST3410ParameterSpec spec) {
        this.x = params.getX();
        this.gost3410Spec = spec;
        if (spec == null) {
            throw new IllegalArgumentException("spec is null");
        }
    }

    @Override // java.security.Key
    public String getAlgorithm() {
        return "GOST3410";
    }

    @Override // java.security.Key
    public String getFormat() {
        return "PKCS#8";
    }

    @Override // java.security.Key
    public byte[] getEncoded() {
        byte[] keyBytes;
        PrivateKeyInfo info;
        byte[] keyEnc = getX().toByteArray();
        if (keyEnc[0] == 0) {
            keyBytes = new byte[keyEnc.length - 1];
        } else {
            keyBytes = new byte[keyEnc.length];
        }
        for (int i = 0; i != keyBytes.length; i++) {
            keyBytes[i] = keyEnc[(keyEnc.length - 1) - i];
        }
        try {
            if (this.gost3410Spec instanceof GOST3410ParameterSpec) {
                info = new PrivateKeyInfo(new AlgorithmIdentifier(CryptoProObjectIdentifiers.gostR3410_94, new GOST3410PublicKeyAlgParameters(new ASN1ObjectIdentifier(this.gost3410Spec.getPublicKeyParamSetOID()), new ASN1ObjectIdentifier(this.gost3410Spec.getDigestParamSetOID()))), new DEROctetString(keyBytes));
            } else {
                info = new PrivateKeyInfo(new AlgorithmIdentifier(CryptoProObjectIdentifiers.gostR3410_94), new DEROctetString(keyBytes));
            }
            return info.getEncoded("DER");
        } catch (IOException e) {
            return null;
        }
    }

    @Override // org.spongycastle.jce.interfaces.GOST3410Key
    public GOST3410Params getParameters() {
        return this.gost3410Spec;
    }

    @Override // org.spongycastle.jce.interfaces.GOST3410PrivateKey
    public BigInteger getX() {
        return this.x;
    }

    public boolean equals(Object o) {
        if (!(o instanceof GOST3410PrivateKey)) {
            return false;
        }
        GOST3410PrivateKey other = (GOST3410PrivateKey) o;
        return getX().equals(other.getX()) && getParameters().getPublicKeyParameters().equals(other.getParameters().getPublicKeyParameters()) && getParameters().getDigestParamSetOID().equals(other.getParameters().getDigestParamSetOID()) && compareObj(getParameters().getEncryptionParamSetOID(), other.getParameters().getEncryptionParamSetOID());
    }

    private boolean compareObj(Object o1, Object o2) {
        if (o1 == o2) {
            return true;
        }
        if (o1 == null) {
            return false;
        }
        return o1.equals(o2);
    }

    public int hashCode() {
        return getX().hashCode() ^ this.gost3410Spec.hashCode();
    }

    @Override // org.spongycastle.jce.interfaces.PKCS12BagAttributeCarrier
    public void setBagAttribute(ASN1ObjectIdentifier oid, ASN1Encodable attribute) {
        this.attrCarrier.setBagAttribute(oid, attribute);
    }

    @Override // org.spongycastle.jce.interfaces.PKCS12BagAttributeCarrier
    public ASN1Encodable getBagAttribute(ASN1ObjectIdentifier oid) {
        return this.attrCarrier.getBagAttribute(oid);
    }

    @Override // org.spongycastle.jce.interfaces.PKCS12BagAttributeCarrier
    public Enumeration getBagAttributeKeys() {
        return this.attrCarrier.getBagAttributeKeys();
    }

    private void readObject(ObjectInputStream in) throws ClassNotFoundException, IOException {
        in.defaultReadObject();
        String publicKeyParamSetOID = (String) in.readObject();
        if (publicKeyParamSetOID != null) {
            this.gost3410Spec = new GOST3410ParameterSpec(publicKeyParamSetOID, (String) in.readObject(), (String) in.readObject());
        } else {
            this.gost3410Spec = new GOST3410ParameterSpec(new GOST3410PublicKeyParameterSetSpec((BigInteger) in.readObject(), (BigInteger) in.readObject(), (BigInteger) in.readObject()));
            in.readObject();
            in.readObject();
        }
        this.attrCarrier = new PKCS12BagAttributeCarrierImpl();
    }

    private void writeObject(ObjectOutputStream out) throws IOException {
        out.defaultWriteObject();
        if (this.gost3410Spec.getPublicKeyParamSetOID() != null) {
            out.writeObject(this.gost3410Spec.getPublicKeyParamSetOID());
            out.writeObject(this.gost3410Spec.getDigestParamSetOID());
            out.writeObject(this.gost3410Spec.getEncryptionParamSetOID());
        } else {
            out.writeObject(null);
            out.writeObject(this.gost3410Spec.getPublicKeyParameters().getP());
            out.writeObject(this.gost3410Spec.getPublicKeyParameters().getQ());
            out.writeObject(this.gost3410Spec.getPublicKeyParameters().getA());
            out.writeObject(this.gost3410Spec.getDigestParamSetOID());
            out.writeObject(this.gost3410Spec.getEncryptionParamSetOID());
        }
    }
}
