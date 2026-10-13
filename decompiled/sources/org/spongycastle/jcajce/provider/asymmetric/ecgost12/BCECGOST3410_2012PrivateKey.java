package org.spongycastle.jcajce.provider.asymmetric.ecgost12;

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
import org.spongycastle.asn1.ASN1Null;
import org.spongycastle.asn1.ASN1ObjectIdentifier;
import org.spongycastle.asn1.ASN1OctetString;
import org.spongycastle.asn1.ASN1Primitive;
import org.spongycastle.asn1.ASN1Sequence;
import org.spongycastle.asn1.DERBitString;
import org.spongycastle.asn1.DERNull;
import org.spongycastle.asn1.DEROctetString;
import org.spongycastle.asn1.cryptopro.ECGOST3410NamedCurves;
import org.spongycastle.asn1.cryptopro.GOST3410PublicKeyAlgParameters;
import org.spongycastle.asn1.pkcs.PrivateKeyInfo;
import org.spongycastle.asn1.rosstandart.RosstandartObjectIdentifiers;
import org.spongycastle.asn1.x509.AlgorithmIdentifier;
import org.spongycastle.asn1.x509.SubjectPublicKeyInfo;
import org.spongycastle.asn1.x9.X962Parameters;
import org.spongycastle.asn1.x9.X9ECParameters;
import org.spongycastle.crypto.params.ECDomainParameters;
import org.spongycastle.crypto.params.ECPrivateKeyParameters;
import org.spongycastle.jcajce.provider.asymmetric.util.EC5Util;
import org.spongycastle.jcajce.provider.asymmetric.util.ECUtil;
import org.spongycastle.jcajce.provider.asymmetric.util.PKCS12BagAttributeCarrierImpl;
import org.spongycastle.jce.ECGOST3410NamedCurveTable;
import org.spongycastle.jce.interfaces.ECPointEncoder;
import org.spongycastle.jce.interfaces.PKCS12BagAttributeCarrier;
import org.spongycastle.jce.provider.BouncyCastleProvider;
import org.spongycastle.jce.spec.ECNamedCurveParameterSpec;
import org.spongycastle.jce.spec.ECNamedCurveSpec;
import org.spongycastle.jce.spec.ECPrivateKeySpec;
import org.spongycastle.math.ec.ECCurve;

/* JADX INFO: loaded from: autofirma.jar:org/spongycastle/jcajce/provider/asymmetric/ecgost12/BCECGOST3410_2012PrivateKey.class */
public class BCECGOST3410_2012PrivateKey implements ECPrivateKey, org.spongycastle.jce.interfaces.ECPrivateKey, PKCS12BagAttributeCarrier, ECPointEncoder {
    static final long serialVersionUID = 7245981689601667138L;
    private String algorithm;
    private boolean withCompression;
    private transient GOST3410PublicKeyAlgParameters gostParams;
    private transient BigInteger d;
    private transient ECParameterSpec ecSpec;
    private transient DERBitString publicKey;
    private transient PKCS12BagAttributeCarrierImpl attrCarrier;

    protected BCECGOST3410_2012PrivateKey() {
        this.algorithm = "ECGOST3410-2012";
        this.attrCarrier = new PKCS12BagAttributeCarrierImpl();
    }

    public BCECGOST3410_2012PrivateKey(ECPrivateKey key) {
        this.algorithm = "ECGOST3410-2012";
        this.attrCarrier = new PKCS12BagAttributeCarrierImpl();
        this.d = key.getS();
        this.algorithm = key.getAlgorithm();
        this.ecSpec = key.getParams();
    }

    public BCECGOST3410_2012PrivateKey(ECPrivateKeySpec spec) {
        this.algorithm = "ECGOST3410-2012";
        this.attrCarrier = new PKCS12BagAttributeCarrierImpl();
        this.d = spec.getD();
        if (spec.getParams() != null) {
            ECCurve curve = spec.getParams().getCurve();
            EllipticCurve ellipticCurve = EC5Util.convertCurve(curve, spec.getParams().getSeed());
            this.ecSpec = EC5Util.convertSpec(ellipticCurve, spec.getParams());
            return;
        }
        this.ecSpec = null;
    }

    public BCECGOST3410_2012PrivateKey(java.security.spec.ECPrivateKeySpec spec) {
        this.algorithm = "ECGOST3410-2012";
        this.attrCarrier = new PKCS12BagAttributeCarrierImpl();
        this.d = spec.getS();
        this.ecSpec = spec.getParams();
    }

    public BCECGOST3410_2012PrivateKey(BCECGOST3410_2012PrivateKey key) {
        this.algorithm = "ECGOST3410-2012";
        this.attrCarrier = new PKCS12BagAttributeCarrierImpl();
        this.d = key.d;
        this.ecSpec = key.ecSpec;
        this.withCompression = key.withCompression;
        this.attrCarrier = key.attrCarrier;
        this.publicKey = key.publicKey;
        this.gostParams = key.gostParams;
    }

    public BCECGOST3410_2012PrivateKey(String algorithm, ECPrivateKeyParameters params, BCECGOST3410_2012PublicKey pubKey, ECParameterSpec spec) {
        this.algorithm = "ECGOST3410-2012";
        this.attrCarrier = new PKCS12BagAttributeCarrierImpl();
        ECDomainParameters dp = params.getParameters();
        this.algorithm = algorithm;
        this.d = params.getD();
        if (spec == null) {
            EllipticCurve ellipticCurve = EC5Util.convertCurve(dp.getCurve(), dp.getSeed());
            this.ecSpec = new ECParameterSpec(ellipticCurve, new ECPoint(dp.getG().getAffineXCoord().toBigInteger(), dp.getG().getAffineYCoord().toBigInteger()), dp.getN(), dp.getH().intValue());
        } else {
            this.ecSpec = spec;
        }
        this.gostParams = pubKey.getGostParams();
        this.publicKey = getPublicKeyDetails(pubKey);
    }

    public BCECGOST3410_2012PrivateKey(String algorithm, ECPrivateKeyParameters params, BCECGOST3410_2012PublicKey pubKey, org.spongycastle.jce.spec.ECParameterSpec spec) {
        this.algorithm = "ECGOST3410-2012";
        this.attrCarrier = new PKCS12BagAttributeCarrierImpl();
        ECDomainParameters dp = params.getParameters();
        this.algorithm = algorithm;
        this.d = params.getD();
        if (spec == null) {
            EllipticCurve ellipticCurve = EC5Util.convertCurve(dp.getCurve(), dp.getSeed());
            this.ecSpec = new ECParameterSpec(ellipticCurve, new ECPoint(dp.getG().getAffineXCoord().toBigInteger(), dp.getG().getAffineYCoord().toBigInteger()), dp.getN(), dp.getH().intValue());
        } else {
            EllipticCurve ellipticCurve2 = EC5Util.convertCurve(spec.getCurve(), spec.getSeed());
            this.ecSpec = new ECParameterSpec(ellipticCurve2, new ECPoint(spec.getG().getAffineXCoord().toBigInteger(), spec.getG().getAffineYCoord().toBigInteger()), spec.getN(), spec.getH().intValue());
        }
        this.gostParams = pubKey.getGostParams();
        this.publicKey = getPublicKeyDetails(pubKey);
    }

    public BCECGOST3410_2012PrivateKey(String algorithm, ECPrivateKeyParameters params) {
        this.algorithm = "ECGOST3410-2012";
        this.attrCarrier = new PKCS12BagAttributeCarrierImpl();
        this.algorithm = algorithm;
        this.d = params.getD();
        this.ecSpec = null;
    }

    BCECGOST3410_2012PrivateKey(PrivateKeyInfo info) throws IOException {
        this.algorithm = "ECGOST3410-2012";
        this.attrCarrier = new PKCS12BagAttributeCarrierImpl();
        populateFromPrivKeyInfo(info);
    }

    private void populateFromPrivKeyInfo(PrivateKeyInfo info) throws IOException {
        ASN1Primitive p = info.getPrivateKeyAlgorithm().getParameters().toASN1Primitive();
        if ((p instanceof ASN1Sequence) && (ASN1Sequence.getInstance(p).size() == 2 || ASN1Sequence.getInstance(p).size() == 3)) {
            this.gostParams = GOST3410PublicKeyAlgParameters.getInstance(info.getPrivateKeyAlgorithm().getParameters());
            ECNamedCurveParameterSpec spec = ECGOST3410NamedCurveTable.getParameterSpec(ECGOST3410NamedCurves.getName(this.gostParams.getPublicKeyParamSet()));
            ECCurve curve = spec.getCurve();
            EllipticCurve ellipticCurve = EC5Util.convertCurve(curve, spec.getSeed());
            this.ecSpec = new ECNamedCurveSpec(ECGOST3410NamedCurves.getName(this.gostParams.getPublicKeyParamSet()), ellipticCurve, new ECPoint(spec.getG().getAffineXCoord().toBigInteger(), spec.getG().getAffineYCoord().toBigInteger()), spec.getN(), spec.getH());
            ASN1Encodable privKey = info.parsePrivateKey();
            if (privKey instanceof ASN1Integer) {
                this.d = ASN1Integer.getInstance(privKey).getPositiveValue();
                return;
            }
            byte[] encVal = ASN1OctetString.getInstance(privKey).getOctets();
            byte[] dVal = new byte[encVal.length];
            for (int i = 0; i != encVal.length; i++) {
                dVal[i] = encVal[(encVal.length - 1) - i];
            }
            this.d = new BigInteger(1, dVal);
            return;
        }
        X962Parameters params = X962Parameters.getInstance(info.getPrivateKeyAlgorithm().getParameters());
        if (params.isNamedCurve()) {
            ASN1ObjectIdentifier oid = ASN1ObjectIdentifier.getInstance(params.getParameters());
            X9ECParameters ecP = ECUtil.getNamedCurveByOid(oid);
            if (ecP == null) {
                ECDomainParameters gParam = ECGOST3410NamedCurves.getByOID(oid);
                EllipticCurve ellipticCurve2 = EC5Util.convertCurve(gParam.getCurve(), gParam.getSeed());
                this.ecSpec = new ECNamedCurveSpec(ECGOST3410NamedCurves.getName(oid), ellipticCurve2, new ECPoint(gParam.getG().getAffineXCoord().toBigInteger(), gParam.getG().getAffineYCoord().toBigInteger()), gParam.getN(), gParam.getH());
            } else {
                EllipticCurve ellipticCurve3 = EC5Util.convertCurve(ecP.getCurve(), ecP.getSeed());
                this.ecSpec = new ECNamedCurveSpec(ECUtil.getCurveName(oid), ellipticCurve3, new ECPoint(ecP.getG().getAffineXCoord().toBigInteger(), ecP.getG().getAffineYCoord().toBigInteger()), ecP.getN(), ecP.getH());
            }
        } else if (params.isImplicitlyCA()) {
            this.ecSpec = null;
        } else {
            X9ECParameters ecP2 = X9ECParameters.getInstance(params.getParameters());
            EllipticCurve ellipticCurve4 = EC5Util.convertCurve(ecP2.getCurve(), ecP2.getSeed());
            this.ecSpec = new ECParameterSpec(ellipticCurve4, new ECPoint(ecP2.getG().getAffineXCoord().toBigInteger(), ecP2.getG().getAffineYCoord().toBigInteger()), ecP2.getN(), ecP2.getH().intValue());
        }
        ASN1Encodable privKey2 = info.parsePrivateKey();
        if (privKey2 instanceof ASN1Integer) {
            ASN1Integer derD = ASN1Integer.getInstance(privKey2);
            this.d = derD.getValue();
        } else {
            org.spongycastle.asn1.sec.ECPrivateKey ec = org.spongycastle.asn1.sec.ECPrivateKey.getInstance(privKey2);
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
        X962Parameters params;
        int orderBitLength;
        org.spongycastle.asn1.sec.ECPrivateKey keyStructure;
        boolean is512 = this.d.bitLength() > 256;
        ASN1ObjectIdentifier identifier = is512 ? RosstandartObjectIdentifiers.id_tc26_gost_3410_12_512 : RosstandartObjectIdentifiers.id_tc26_gost_3410_12_256;
        int size = is512 ? 64 : 32;
        if (this.gostParams != null) {
            byte[] encKey = new byte[size];
            extractBytes(encKey, size, 0, getS());
            try {
                PrivateKeyInfo info = new PrivateKeyInfo(new AlgorithmIdentifier(identifier, this.gostParams), new DEROctetString(encKey));
                return info.getEncoded("DER");
            } catch (IOException e) {
                return null;
            }
        }
        if (this.ecSpec instanceof ECNamedCurveSpec) {
            ASN1ObjectIdentifier curveOid = ECUtil.getNamedCurveOid(((ECNamedCurveSpec) this.ecSpec).getName());
            if (curveOid == null) {
                curveOid = new ASN1ObjectIdentifier(((ECNamedCurveSpec) this.ecSpec).getName());
            }
            params = new X962Parameters(curveOid);
            orderBitLength = ECUtil.getOrderBitLength(BouncyCastleProvider.CONFIGURATION, this.ecSpec.getOrder(), getS());
        } else if (this.ecSpec == null) {
            params = new X962Parameters((ASN1Null) DERNull.INSTANCE);
            orderBitLength = ECUtil.getOrderBitLength(BouncyCastleProvider.CONFIGURATION, null, getS());
        } else {
            ECCurve curve = EC5Util.convertCurve(this.ecSpec.getCurve());
            X9ECParameters ecP = new X9ECParameters(curve, EC5Util.convertPoint(curve, this.ecSpec.getGenerator(), this.withCompression), this.ecSpec.getOrder(), BigInteger.valueOf(this.ecSpec.getCofactor()), this.ecSpec.getCurve().getSeed());
            params = new X962Parameters(ecP);
            orderBitLength = ECUtil.getOrderBitLength(BouncyCastleProvider.CONFIGURATION, this.ecSpec.getOrder(), getS());
        }
        if (this.publicKey != null) {
            keyStructure = new org.spongycastle.asn1.sec.ECPrivateKey(orderBitLength, getS(), this.publicKey, params);
        } else {
            keyStructure = new org.spongycastle.asn1.sec.ECPrivateKey(orderBitLength, getS(), params);
        }
        try {
            PrivateKeyInfo info2 = new PrivateKeyInfo(new AlgorithmIdentifier(identifier, params.toASN1Primitive()), keyStructure.toASN1Primitive());
            return info2.getEncoded("DER");
        } catch (IOException e2) {
            return null;
        }
    }

    private void extractBytes(byte[] encKey, int size, int offSet, BigInteger bI) {
        byte[] val = bI.toByteArray();
        if (val.length < size) {
            byte[] tmp = new byte[size];
            System.arraycopy(val, 0, tmp, tmp.length - val.length, val.length);
            val = tmp;
        }
        for (int i = 0; i != size; i++) {
            encKey[offSet + i] = val[(val.length - 1) - i];
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
        return BouncyCastleProvider.CONFIGURATION.getEcImplicitlyCa();
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
        if (!(o instanceof BCECGOST3410_2012PrivateKey)) {
            return false;
        }
        BCECGOST3410_2012PrivateKey other = (BCECGOST3410_2012PrivateKey) o;
        return getD().equals(other.getD()) && engineGetSpec().equals(other.engineGetSpec());
    }

    public int hashCode() {
        return getD().hashCode() ^ engineGetSpec().hashCode();
    }

    public String toString() {
        return ECUtil.privateKeyToString(this.algorithm, this.d, engineGetSpec());
    }

    private DERBitString getPublicKeyDetails(BCECGOST3410_2012PublicKey pub) {
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
        populateFromPrivKeyInfo(PrivateKeyInfo.getInstance(ASN1Primitive.fromByteArray(enc)));
        this.attrCarrier = new PKCS12BagAttributeCarrierImpl();
    }

    private void writeObject(ObjectOutputStream out) throws IOException {
        out.defaultWriteObject();
        out.writeObject(getEncoded());
    }
}
