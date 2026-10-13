package org.spongycastle.jcajce.provider.asymmetric.ec;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.math.BigInteger;
import java.security.interfaces.ECPrivateKey;
import java.security.spec.ECParameterSpec;
import java.security.spec.ECPoint;
import java.security.spec.EllipticCurve;
import java.util.Enumeration;
import org.spongycastle.asn1.ASN1Encodable;
import org.spongycastle.asn1.ASN1Integer;
import org.spongycastle.asn1.ASN1ObjectIdentifier;
import org.spongycastle.asn1.ASN1Primitive;
import org.spongycastle.asn1.DERBitString;
import org.spongycastle.asn1.pkcs.PrivateKeyInfo;
import org.spongycastle.asn1.x509.AlgorithmIdentifier;
import org.spongycastle.asn1.x509.SubjectPublicKeyInfo;
import org.spongycastle.asn1.x9.X962Parameters;
import org.spongycastle.asn1.x9.X9ObjectIdentifiers;
import org.spongycastle.crypto.params.ECDomainParameters;
import org.spongycastle.crypto.params.ECPrivateKeyParameters;
import org.spongycastle.jcajce.provider.asymmetric.util.EC5Util;
import org.spongycastle.jcajce.provider.asymmetric.util.ECUtil;
import org.spongycastle.jcajce.provider.asymmetric.util.PKCS12BagAttributeCarrierImpl;
import org.spongycastle.jcajce.provider.config.ProviderConfiguration;
import org.spongycastle.jce.interfaces.ECPointEncoder;
import org.spongycastle.jce.interfaces.PKCS12BagAttributeCarrier;
import org.spongycastle.jce.provider.BouncyCastleProvider;
import org.spongycastle.jce.spec.ECPrivateKeySpec;
import org.spongycastle.math.ec.ECCurve;

/* JADX INFO: loaded from: autofirma.jar:org/spongycastle/jcajce/provider/asymmetric/ec/BCECPrivateKey.class */
public class BCECPrivateKey implements ECPrivateKey, org.spongycastle.jce.interfaces.ECPrivateKey, PKCS12BagAttributeCarrier, ECPointEncoder {
    static final long serialVersionUID = 994553197664784084L;
    private String algorithm;
    private boolean withCompression;
    private transient BigInteger d;
    private transient ECParameterSpec ecSpec;
    private transient ProviderConfiguration configuration;
    private transient DERBitString publicKey;
    private transient PKCS12BagAttributeCarrierImpl attrCarrier;

    protected BCECPrivateKey() {
        this.algorithm = "EC";
        this.attrCarrier = new PKCS12BagAttributeCarrierImpl();
    }

    public BCECPrivateKey(ECPrivateKey key, ProviderConfiguration configuration) {
        this.algorithm = "EC";
        this.attrCarrier = new PKCS12BagAttributeCarrierImpl();
        this.d = key.getS();
        this.algorithm = key.getAlgorithm();
        this.ecSpec = key.getParams();
        this.configuration = configuration;
    }

    public BCECPrivateKey(String algorithm, ECPrivateKeySpec spec, ProviderConfiguration configuration) {
        this.algorithm = "EC";
        this.attrCarrier = new PKCS12BagAttributeCarrierImpl();
        this.algorithm = algorithm;
        this.d = spec.getD();
        if (spec.getParams() != null) {
            ECCurve curve = spec.getParams().getCurve();
            EllipticCurve ellipticCurve = EC5Util.convertCurve(curve, spec.getParams().getSeed());
            this.ecSpec = EC5Util.convertSpec(ellipticCurve, spec.getParams());
        } else {
            this.ecSpec = null;
        }
        this.configuration = configuration;
    }

    public BCECPrivateKey(String algorithm, java.security.spec.ECPrivateKeySpec spec, ProviderConfiguration configuration) {
        this.algorithm = "EC";
        this.attrCarrier = new PKCS12BagAttributeCarrierImpl();
        this.algorithm = algorithm;
        this.d = spec.getS();
        this.ecSpec = spec.getParams();
        this.configuration = configuration;
    }

    public BCECPrivateKey(String algorithm, BCECPrivateKey key) {
        this.algorithm = "EC";
        this.attrCarrier = new PKCS12BagAttributeCarrierImpl();
        this.algorithm = algorithm;
        this.d = key.d;
        this.ecSpec = key.ecSpec;
        this.withCompression = key.withCompression;
        this.attrCarrier = key.attrCarrier;
        this.publicKey = key.publicKey;
        this.configuration = key.configuration;
    }

    public BCECPrivateKey(String algorithm, ECPrivateKeyParameters params, BCECPublicKey pubKey, ECParameterSpec spec, ProviderConfiguration configuration) {
        this.algorithm = "EC";
        this.attrCarrier = new PKCS12BagAttributeCarrierImpl();
        ECDomainParameters dp = params.getParameters();
        this.algorithm = algorithm;
        this.d = params.getD();
        this.configuration = configuration;
        if (spec == null) {
            EllipticCurve ellipticCurve = EC5Util.convertCurve(dp.getCurve(), dp.getSeed());
            this.ecSpec = new ECParameterSpec(ellipticCurve, new ECPoint(dp.getG().getAffineXCoord().toBigInteger(), dp.getG().getAffineYCoord().toBigInteger()), dp.getN(), dp.getH().intValue());
        } else {
            this.ecSpec = spec;
        }
        this.publicKey = getPublicKeyDetails(pubKey);
    }

    public BCECPrivateKey(String algorithm, ECPrivateKeyParameters params, BCECPublicKey pubKey, org.spongycastle.jce.spec.ECParameterSpec spec, ProviderConfiguration configuration) {
        this.algorithm = "EC";
        this.attrCarrier = new PKCS12BagAttributeCarrierImpl();
        ECDomainParameters dp = params.getParameters();
        this.algorithm = algorithm;
        this.d = params.getD();
        this.configuration = configuration;
        if (spec == null) {
            EllipticCurve ellipticCurve = EC5Util.convertCurve(dp.getCurve(), dp.getSeed());
            this.ecSpec = new ECParameterSpec(ellipticCurve, new ECPoint(dp.getG().getAffineXCoord().toBigInteger(), dp.getG().getAffineYCoord().toBigInteger()), dp.getN(), dp.getH().intValue());
        } else {
            EllipticCurve ellipticCurve2 = EC5Util.convertCurve(spec.getCurve(), spec.getSeed());
            this.ecSpec = EC5Util.convertSpec(ellipticCurve2, spec);
        }
        try {
            this.publicKey = getPublicKeyDetails(pubKey);
        } catch (Exception e) {
            this.publicKey = null;
        }
    }

    public BCECPrivateKey(String algorithm, ECPrivateKeyParameters params, ProviderConfiguration configuration) {
        this.algorithm = "EC";
        this.attrCarrier = new PKCS12BagAttributeCarrierImpl();
        this.algorithm = algorithm;
        this.d = params.getD();
        this.ecSpec = null;
        this.configuration = configuration;
    }

    BCECPrivateKey(String algorithm, PrivateKeyInfo info, ProviderConfiguration configuration) throws IOException {
        this.algorithm = "EC";
        this.attrCarrier = new PKCS12BagAttributeCarrierImpl();
        this.algorithm = algorithm;
        this.configuration = configuration;
        populateFromPrivKeyInfo(info);
    }

    private void populateFromPrivKeyInfo(PrivateKeyInfo info) throws IOException {
        X962Parameters params = X962Parameters.getInstance(info.getPrivateKeyAlgorithm().getParameters());
        ECCurve curve = EC5Util.getCurve(this.configuration, params);
        this.ecSpec = EC5Util.convertToSpec(params, curve);
        ASN1Encodable privKey = info.parsePrivateKey();
        if (privKey instanceof ASN1Integer) {
            ASN1Integer derD = ASN1Integer.getInstance(privKey);
            this.d = derD.getValue();
        } else {
            org.spongycastle.asn1.sec.ECPrivateKey ec = org.spongycastle.asn1.sec.ECPrivateKey.getInstance(privKey);
            this.d = ec.getKey();
            this.publicKey = ec.getPublicKey();
        }
    }

    @Override // java.security.Key
    public String getAlgorithm() {
        return this.algorithm;
    }

    @Override // java.security.Key
    public String getFormat() {
        return "PKCS#8";
    }

    @Override // java.security.Key
    public byte[] getEncoded() {
        int orderBitLength;
        org.spongycastle.asn1.sec.ECPrivateKey keyStructure;
        X962Parameters params = ECUtils.getDomainParametersFromName(this.ecSpec, this.withCompression);
        if (this.ecSpec == null) {
            orderBitLength = ECUtil.getOrderBitLength(this.configuration, null, getS());
        } else {
            orderBitLength = ECUtil.getOrderBitLength(this.configuration, this.ecSpec.getOrder(), getS());
        }
        if (this.publicKey != null) {
            keyStructure = new org.spongycastle.asn1.sec.ECPrivateKey(orderBitLength, getS(), this.publicKey, params);
        } else {
            keyStructure = new org.spongycastle.asn1.sec.ECPrivateKey(orderBitLength, getS(), params);
        }
        try {
            PrivateKeyInfo info = new PrivateKeyInfo(new AlgorithmIdentifier(X9ObjectIdentifiers.id_ecPublicKey, params), keyStructure);
            return info.getEncoded("DER");
        } catch (IOException e) {
            return null;
        }
    }

    @Override // java.security.interfaces.ECKey
    public ECParameterSpec getParams() {
        return this.ecSpec;
    }

    @Override // org.spongycastle.jce.interfaces.ECKey
    public org.spongycastle.jce.spec.ECParameterSpec getParameters() {
        if (this.ecSpec == null) {
            return null;
        }
        return EC5Util.convertSpec(this.ecSpec, this.withCompression);
    }

    org.spongycastle.jce.spec.ECParameterSpec engineGetSpec() {
        if (this.ecSpec != null) {
            return EC5Util.convertSpec(this.ecSpec, this.withCompression);
        }
        return this.configuration.getEcImplicitlyCa();
    }

    @Override // java.security.interfaces.ECPrivateKey
    public BigInteger getS() {
        return this.d;
    }

    @Override // org.spongycastle.jce.interfaces.ECPrivateKey
    public BigInteger getD() {
        return this.d;
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

    @Override // org.spongycastle.jce.interfaces.ECPointEncoder
    public void setPointFormat(String style) {
        this.withCompression = !"UNCOMPRESSED".equalsIgnoreCase(style);
    }

    public boolean equals(Object o) {
        if (!(o instanceof BCECPrivateKey)) {
            return false;
        }
        BCECPrivateKey other = (BCECPrivateKey) o;
        return getD().equals(other.getD()) && engineGetSpec().equals(other.engineGetSpec());
    }

    public int hashCode() {
        return getD().hashCode() ^ engineGetSpec().hashCode();
    }

    public String toString() {
        return ECUtil.privateKeyToString("EC", this.d, engineGetSpec());
    }

    private org.spongycastle.math.ec.ECPoint calculateQ(org.spongycastle.jce.spec.ECParameterSpec spec) {
        return spec.getG().multiply(this.d).normalize();
    }

    private DERBitString getPublicKeyDetails(BCECPublicKey pub) {
        try {
            SubjectPublicKeyInfo info = SubjectPublicKeyInfo.getInstance(ASN1Primitive.fromByteArray(pub.getEncoded()));
            return info.getPublicKeyData();
        } catch (IOException e) {
            return null;
        }
    }

    private void readObject(ObjectInputStream in) throws ClassNotFoundException, IOException {
        in.defaultReadObject();
        byte[] enc = (byte[]) in.readObject();
        this.configuration = BouncyCastleProvider.CONFIGURATION;
        populateFromPrivKeyInfo(PrivateKeyInfo.getInstance(ASN1Primitive.fromByteArray(enc)));
        this.attrCarrier = new PKCS12BagAttributeCarrierImpl();
    }

    private void writeObject(ObjectOutputStream out) throws IOException {
        out.defaultWriteObject();
        out.writeObject(getEncoded());
    }
}
