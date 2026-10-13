package org.spongycastle.jcajce.provider.asymmetric.ecgost;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.math.BigInteger;
import java.security.interfaces.ECPublicKey;
import java.security.spec.ECParameterSpec;
import java.security.spec.ECPoint;
import java.security.spec.ECPublicKeySpec;
import java.security.spec.EllipticCurve;
import org.apache.xml.security.keys.content.x509.XMLX509Certificate;
import org.spongycastle.asn1.ASN1Encodable;
import org.spongycastle.asn1.ASN1ObjectIdentifier;
import org.spongycastle.asn1.ASN1OctetString;
import org.spongycastle.asn1.ASN1Primitive;
import org.spongycastle.asn1.DERBitString;
import org.spongycastle.asn1.DEROctetString;
import org.spongycastle.asn1.cryptopro.CryptoProObjectIdentifiers;
import org.spongycastle.asn1.cryptopro.ECGOST3410NamedCurves;
import org.spongycastle.asn1.cryptopro.GOST3410PublicKeyAlgParameters;
import org.spongycastle.asn1.x509.AlgorithmIdentifier;
import org.spongycastle.asn1.x509.SubjectPublicKeyInfo;
import org.spongycastle.asn1.x9.X962Parameters;
import org.spongycastle.asn1.x9.X9ECParameters;
import org.spongycastle.crypto.params.ECDomainParameters;
import org.spongycastle.crypto.params.ECPublicKeyParameters;
import org.spongycastle.jcajce.provider.asymmetric.util.EC5Util;
import org.spongycastle.jcajce.provider.asymmetric.util.ECUtil;
import org.spongycastle.jcajce.provider.asymmetric.util.KeyUtil;
import org.spongycastle.jcajce.provider.config.ProviderConfiguration;
import org.spongycastle.jce.ECGOST3410NamedCurveTable;
import org.spongycastle.jce.interfaces.ECPointEncoder;
import org.spongycastle.jce.provider.BouncyCastleProvider;
import org.spongycastle.jce.spec.ECNamedCurveParameterSpec;
import org.spongycastle.jce.spec.ECNamedCurveSpec;
import org.spongycastle.math.ec.ECCurve;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:org/spongycastle/jcajce/provider/asymmetric/ecgost/BCECGOST3410PublicKey.class */
public class BCECGOST3410PublicKey implements ECPublicKey, org.spongycastle.jce.interfaces.ECPublicKey, ECPointEncoder {
    static final long serialVersionUID = 7026240464295649314L;
    private String algorithm;
    private boolean withCompression;
    private transient ECPublicKeyParameters ecPublicKey;
    private transient ECParameterSpec ecSpec;
    private transient ASN1Encodable gostParams;

    public BCECGOST3410PublicKey(BCECGOST3410PublicKey key) {
        this.algorithm = "ECGOST3410";
        this.ecPublicKey = key.ecPublicKey;
        this.ecSpec = key.ecSpec;
        this.withCompression = key.withCompression;
        this.gostParams = key.gostParams;
    }

    public BCECGOST3410PublicKey(ECPublicKeySpec spec) {
        this.algorithm = "ECGOST3410";
        this.ecSpec = spec.getParams();
        this.ecPublicKey = new ECPublicKeyParameters(EC5Util.convertPoint(this.ecSpec, spec.getW(), false), EC5Util.getDomainParameters(null, spec.getParams()));
    }

    public BCECGOST3410PublicKey(org.spongycastle.jce.spec.ECPublicKeySpec spec, ProviderConfiguration configuration) {
        this.algorithm = "ECGOST3410";
        if (spec.getParams() != null) {
            ECCurve curve = spec.getParams().getCurve();
            EllipticCurve ellipticCurve = EC5Util.convertCurve(curve, spec.getParams().getSeed());
            this.ecPublicKey = new ECPublicKeyParameters(spec.getQ(), ECUtil.getDomainParameters(configuration, spec.getParams()));
            this.ecSpec = EC5Util.convertSpec(ellipticCurve, spec.getParams());
            return;
        }
        org.spongycastle.jce.spec.ECParameterSpec s = configuration.getEcImplicitlyCa();
        this.ecPublicKey = new ECPublicKeyParameters(s.getCurve().createPoint(spec.getQ().getAffineXCoord().toBigInteger(), spec.getQ().getAffineYCoord().toBigInteger()), EC5Util.getDomainParameters(configuration, (ECParameterSpec) null));
        this.ecSpec = null;
    }

    public BCECGOST3410PublicKey(String algorithm, ECPublicKeyParameters params, ECParameterSpec spec) {
        this.algorithm = "ECGOST3410";
        ECDomainParameters dp = params.getParameters();
        this.algorithm = algorithm;
        this.ecPublicKey = params;
        if (spec == null) {
            EllipticCurve ellipticCurve = EC5Util.convertCurve(dp.getCurve(), dp.getSeed());
            this.ecSpec = createSpec(ellipticCurve, dp);
        } else {
            this.ecSpec = spec;
        }
    }

    public BCECGOST3410PublicKey(String algorithm, ECPublicKeyParameters params, org.spongycastle.jce.spec.ECParameterSpec spec) {
        this.algorithm = "ECGOST3410";
        ECDomainParameters dp = params.getParameters();
        this.algorithm = algorithm;
        this.ecPublicKey = params;
        if (spec == null) {
            EllipticCurve ellipticCurve = EC5Util.convertCurve(dp.getCurve(), dp.getSeed());
            this.ecSpec = createSpec(ellipticCurve, dp);
        } else {
            EllipticCurve ellipticCurve2 = EC5Util.convertCurve(spec.getCurve(), spec.getSeed());
            this.ecSpec = EC5Util.convertSpec(ellipticCurve2, spec);
        }
    }

    public BCECGOST3410PublicKey(String algorithm, ECPublicKeyParameters params) {
        this.algorithm = "ECGOST3410";
        this.algorithm = algorithm;
        this.ecPublicKey = params;
        this.ecSpec = null;
    }

    private ECParameterSpec createSpec(EllipticCurve ellipticCurve, ECDomainParameters dp) {
        return new ECParameterSpec(ellipticCurve, new ECPoint(dp.getG().getAffineXCoord().toBigInteger(), dp.getG().getAffineYCoord().toBigInteger()), dp.getN(), dp.getH().intValue());
    }

    public BCECGOST3410PublicKey(ECPublicKey key) {
        this.algorithm = "ECGOST3410";
        this.algorithm = key.getAlgorithm();
        this.ecSpec = key.getParams();
        this.ecPublicKey = new ECPublicKeyParameters(EC5Util.convertPoint(this.ecSpec, key.getW(), false), EC5Util.getDomainParameters(null, key.getParams()));
    }

    BCECGOST3410PublicKey(SubjectPublicKeyInfo info) {
        this.algorithm = "ECGOST3410";
        populateFromPubKeyInfo(info);
    }

    private void populateFromPubKeyInfo(SubjectPublicKeyInfo info) {
        ASN1ObjectIdentifier paramOID;
        DERBitString bits = info.getPublicKeyData();
        this.algorithm = "ECGOST3410";
        try {
            ASN1OctetString key = (ASN1OctetString) ASN1Primitive.fromByteArray(bits.getBytes());
            byte[] keyEnc = key.getOctets();
            byte[] x = new byte[32];
            byte[] y = new byte[32];
            for (int i = 0; i != x.length; i++) {
                x[i] = keyEnc[31 - i];
            }
            for (int i2 = 0; i2 != y.length; i2++) {
                y[i2] = keyEnc[63 - i2];
            }
            if (info.getAlgorithm().getParameters() instanceof ASN1ObjectIdentifier) {
                paramOID = ASN1ObjectIdentifier.getInstance(info.getAlgorithm().getParameters());
                this.gostParams = paramOID;
            } else {
                GOST3410PublicKeyAlgParameters params = GOST3410PublicKeyAlgParameters.getInstance(info.getAlgorithm().getParameters());
                this.gostParams = params;
                paramOID = params.getPublicKeyParamSet();
            }
            ECNamedCurveParameterSpec spec = ECGOST3410NamedCurveTable.getParameterSpec(ECGOST3410NamedCurves.getName(paramOID));
            ECCurve curve = spec.getCurve();
            EllipticCurve ellipticCurve = EC5Util.convertCurve(curve, spec.getSeed());
            this.ecPublicKey = new ECPublicKeyParameters(curve.createPoint(new BigInteger(1, x), new BigInteger(1, y)), ECUtil.getDomainParameters((ProviderConfiguration) null, spec));
            this.ecSpec = new ECNamedCurveSpec(ECGOST3410NamedCurves.getName(paramOID), ellipticCurve, new ECPoint(spec.getG().getAffineXCoord().toBigInteger(), spec.getG().getAffineYCoord().toBigInteger()), spec.getN(), spec.getH());
        } catch (IOException e) {
            throw new IllegalArgumentException("error recovering public key");
        }
    }

    @Override // java.security.Key
    public String getAlgorithm() {
        return this.algorithm;
    }

    @Override // java.security.Key
    public String getFormat() {
        return XMLX509Certificate.JCA_CERT_ID;
    }

    @Override // java.security.Key
    public byte[] getEncoded() {
        ASN1Encodable params;
        if (this.gostParams != null) {
            params = this.gostParams;
        } else if (this.ecSpec instanceof ECNamedCurveSpec) {
            params = new GOST3410PublicKeyAlgParameters(ECGOST3410NamedCurves.getOID(((ECNamedCurveSpec) this.ecSpec).getName()), CryptoProObjectIdentifiers.gostR3411_94_CryptoProParamSet);
        } else {
            ECCurve curve = EC5Util.convertCurve(this.ecSpec.getCurve());
            X9ECParameters ecP = new X9ECParameters(curve, EC5Util.convertPoint(curve, this.ecSpec.getGenerator(), this.withCompression), this.ecSpec.getOrder(), BigInteger.valueOf(this.ecSpec.getCofactor()), this.ecSpec.getCurve().getSeed());
            params = new X962Parameters(ecP);
        }
        BigInteger bX = this.ecPublicKey.getQ().getAffineXCoord().toBigInteger();
        BigInteger bY = this.ecPublicKey.getQ().getAffineYCoord().toBigInteger();
        byte[] encKey = new byte[64];
        extractBytes(encKey, 0, bX);
        extractBytes(encKey, 32, bY);
        try {
            SubjectPublicKeyInfo info = new SubjectPublicKeyInfo(new AlgorithmIdentifier(CryptoProObjectIdentifiers.gostR3410_2001, params), new DEROctetString(encKey));
            return KeyUtil.getEncodedSubjectPublicKeyInfo(info);
        } catch (IOException e) {
            return null;
        }
    }

    private void extractBytes(byte[] encKey, int offSet, BigInteger bI) {
        byte[] val = bI.toByteArray();
        if (val.length < 32) {
            byte[] tmp = new byte[32];
            System.arraycopy(val, 0, tmp, tmp.length - val.length, val.length);
            val = tmp;
        }
        for (int i = 0; i != 32; i++) {
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

    @Override // java.security.interfaces.ECPublicKey
    public ECPoint getW() {
        return new ECPoint(this.ecPublicKey.getQ().getAffineXCoord().toBigInteger(), this.ecPublicKey.getQ().getAffineYCoord().toBigInteger());
    }

    @Override // org.spongycastle.jce.interfaces.ECPublicKey
    public org.spongycastle.math.ec.ECPoint getQ() {
        if (this.ecSpec == null) {
            return this.ecPublicKey.getQ().getDetachedPoint();
        }
        return this.ecPublicKey.getQ();
    }

    ECPublicKeyParameters engineGetKeyParameters() {
        return this.ecPublicKey;
    }

    org.spongycastle.jce.spec.ECParameterSpec engineGetSpec() {
        if (this.ecSpec != null) {
            return EC5Util.convertSpec(this.ecSpec, this.withCompression);
        }
        return BouncyCastleProvider.CONFIGURATION.getEcImplicitlyCa();
    }

    public String toString() {
        return ECUtil.publicKeyToString(this.algorithm, this.ecPublicKey.getQ(), engineGetSpec());
    }

    @Override // org.spongycastle.jce.interfaces.ECPointEncoder
    public void setPointFormat(String style) {
        this.withCompression = !"UNCOMPRESSED".equalsIgnoreCase(style);
    }

    public boolean equals(Object o) {
        if (!(o instanceof BCECGOST3410PublicKey)) {
            return false;
        }
        BCECGOST3410PublicKey other = (BCECGOST3410PublicKey) o;
        return this.ecPublicKey.getQ().equals(other.ecPublicKey.getQ()) && engineGetSpec().equals(other.engineGetSpec());
    }

    public int hashCode() {
        return this.ecPublicKey.getQ().hashCode() ^ engineGetSpec().hashCode();
    }

    private void readObject(ObjectInputStream in) throws ClassNotFoundException, IOException {
        in.defaultReadObject();
        byte[] enc = (byte[]) in.readObject();
        populateFromPubKeyInfo(SubjectPublicKeyInfo.getInstance(ASN1Primitive.fromByteArray(enc)));
    }

    private void writeObject(ObjectOutputStream out) throws IOException {
        out.defaultWriteObject();
        out.writeObject(getEncoded());
    }

    ASN1Encodable getGostParams() {
        return this.gostParams;
    }
}
