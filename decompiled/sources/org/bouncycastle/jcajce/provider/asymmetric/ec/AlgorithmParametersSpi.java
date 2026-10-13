package org.bouncycastle.jcajce.provider.asymmetric.ec;

import java.io.IOException;
import java.math.BigInteger;
import java.security.spec.AlgorithmParameterSpec;
import java.security.spec.ECGenParameterSpec;
import java.security.spec.ECParameterSpec;
import java.security.spec.InvalidParameterSpecException;
import org.bouncycastle.asn1.ASN1Null;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.DERNull;
import org.bouncycastle.asn1.x9.ECNamedCurveTable;
import org.bouncycastle.asn1.x9.X962Parameters;
import org.bouncycastle.asn1.x9.X9ECParameters;
import org.bouncycastle.asn1.x9.X9ECPoint;
import org.bouncycastle.jcajce.provider.asymmetric.util.EC5Util;
import org.bouncycastle.jcajce.provider.asymmetric.util.ECUtil;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.jce.spec.ECNamedCurveSpec;
import org.bouncycastle.math.ec.ECCurve;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/jcajce/provider/asymmetric/ec/AlgorithmParametersSpi.class */
public class AlgorithmParametersSpi extends java.security.AlgorithmParametersSpi {
    private ECParameterSpec ecParameterSpec;
    private String curveName;

    protected boolean isASN1FormatString(String str) {
        return str == null || str.equals("ASN.1");
    }

    @Override // java.security.AlgorithmParametersSpi
    protected void engineInit(AlgorithmParameterSpec algorithmParameterSpec) throws InvalidParameterSpecException {
        if (!(algorithmParameterSpec instanceof ECGenParameterSpec)) {
            if (!(algorithmParameterSpec instanceof ECParameterSpec)) {
                throw new InvalidParameterSpecException("AlgorithmParameterSpec class not recognized: " + algorithmParameterSpec.getClass().getName());
            }
            if (algorithmParameterSpec instanceof ECNamedCurveSpec) {
                this.curveName = ((ECNamedCurveSpec) algorithmParameterSpec).getName();
            } else {
                this.curveName = null;
            }
            this.ecParameterSpec = (ECParameterSpec) algorithmParameterSpec;
            return;
        }
        ECGenParameterSpec eCGenParameterSpec = (ECGenParameterSpec) algorithmParameterSpec;
        X9ECParameters domainParametersFromGenSpec = ECUtils.getDomainParametersFromGenSpec(eCGenParameterSpec, BouncyCastleProvider.CONFIGURATION);
        if (null == domainParametersFromGenSpec) {
            throw new InvalidParameterSpecException("EC curve name not recognized: " + eCGenParameterSpec.getName());
        }
        this.curveName = eCGenParameterSpec.getName();
        ECParameterSpec eCParameterSpecConvertToSpec = EC5Util.convertToSpec(domainParametersFromGenSpec);
        this.ecParameterSpec = new ECNamedCurveSpec(this.curveName, eCParameterSpecConvertToSpec.getCurve(), eCParameterSpecConvertToSpec.getGenerator(), eCParameterSpecConvertToSpec.getOrder(), BigInteger.valueOf(eCParameterSpecConvertToSpec.getCofactor()));
    }

    @Override // java.security.AlgorithmParametersSpi
    protected void engineInit(byte[] bArr) throws IOException {
        engineInit(bArr, "ASN.1");
    }

    @Override // java.security.AlgorithmParametersSpi
    protected void engineInit(byte[] bArr, String str) throws IOException {
        if (!isASN1FormatString(str)) {
            throw new IOException("Unknown encoded parameters format in AlgorithmParameters object: " + str);
        }
        X962Parameters x962Parameters = X962Parameters.getInstance(bArr);
        ECCurve curve = EC5Util.getCurve(BouncyCastleProvider.CONFIGURATION, x962Parameters);
        if (x962Parameters.isNamedCurve()) {
            ASN1ObjectIdentifier aSN1ObjectIdentifier = ASN1ObjectIdentifier.getInstance(x962Parameters.getParameters());
            this.curveName = ECNamedCurveTable.getName(aSN1ObjectIdentifier);
            if (this.curveName == null) {
                this.curveName = aSN1ObjectIdentifier.getId();
            }
        }
        this.ecParameterSpec = EC5Util.convertToSpec(x962Parameters, curve);
    }

    @Override // java.security.AlgorithmParametersSpi
    protected <T extends AlgorithmParameterSpec> T engineGetParameterSpec(Class<T> cls) throws InvalidParameterSpecException {
        if (ECParameterSpec.class.isAssignableFrom(cls) || cls == AlgorithmParameterSpec.class) {
            return this.ecParameterSpec;
        }
        if (ECGenParameterSpec.class.isAssignableFrom(cls)) {
            if (this.curveName != null) {
                ASN1ObjectIdentifier namedCurveOid = ECUtil.getNamedCurveOid(this.curveName);
                return namedCurveOid != null ? new ECGenParameterSpec(namedCurveOid.getId()) : new ECGenParameterSpec(this.curveName);
            }
            ASN1ObjectIdentifier namedCurveOid2 = ECUtil.getNamedCurveOid(EC5Util.convertSpec(this.ecParameterSpec));
            if (namedCurveOid2 != null) {
                return new ECGenParameterSpec(namedCurveOid2.getId());
            }
        }
        throw new InvalidParameterSpecException("EC AlgorithmParameters cannot convert to " + cls.getName());
    }

    @Override // java.security.AlgorithmParametersSpi
    protected byte[] engineGetEncoded() throws IOException {
        return engineGetEncoded("ASN.1");
    }

    @Override // java.security.AlgorithmParametersSpi
    protected byte[] engineGetEncoded(String str) throws IOException {
        X962Parameters x962Parameters;
        if (!isASN1FormatString(str)) {
            throw new IOException("Unknown parameters format in AlgorithmParameters object: " + str);
        }
        if (this.ecParameterSpec == null) {
            x962Parameters = new X962Parameters((ASN1Null) DERNull.INSTANCE);
        } else if (this.curveName != null) {
            x962Parameters = new X962Parameters(ECUtil.getNamedCurveOid(this.curveName));
        } else {
            org.bouncycastle.jce.spec.ECParameterSpec eCParameterSpecConvertSpec = EC5Util.convertSpec(this.ecParameterSpec);
            x962Parameters = new X962Parameters(new X9ECParameters(eCParameterSpecConvertSpec.getCurve(), new X9ECPoint(eCParameterSpecConvertSpec.getG(), false), eCParameterSpecConvertSpec.getN(), eCParameterSpecConvertSpec.getH(), eCParameterSpecConvertSpec.getSeed()));
        }
        return x962Parameters.getEncoded();
    }

    @Override // java.security.AlgorithmParametersSpi
    protected String engineToString() {
        return "EC Parameters";
    }
}
