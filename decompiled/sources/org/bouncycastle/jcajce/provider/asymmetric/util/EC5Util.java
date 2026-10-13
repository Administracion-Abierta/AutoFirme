package org.bouncycastle.jcajce.provider.asymmetric.util;

import java.math.BigInteger;
import java.security.spec.ECField;
import java.security.spec.ECFieldF2m;
import java.security.spec.ECFieldFp;
import java.security.spec.ECParameterSpec;
import java.security.spec.ECPoint;
import java.security.spec.EllipticCurve;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.cryptopro.ECGOST3410NamedCurves;
import org.bouncycastle.asn1.cryptopro.GOST3410PublicKeyAlgParameters;
import org.bouncycastle.asn1.x9.ECNamedCurveTable;
import org.bouncycastle.asn1.x9.X962Parameters;
import org.bouncycastle.asn1.x9.X9ECParameters;
import org.bouncycastle.asn1.x9.X9ECParametersHolder;
import org.bouncycastle.crypto.ec.CustomNamedCurves;
import org.bouncycastle.crypto.params.ECDomainParameters;
import org.bouncycastle.jcajce.provider.config.ProviderConfiguration;
import org.bouncycastle.jce.ECGOST3410NamedCurveTable;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.jce.spec.ECNamedCurveParameterSpec;
import org.bouncycastle.jce.spec.ECNamedCurveSpec;
import org.bouncycastle.math.ec.ECAlgorithms;
import org.bouncycastle.math.ec.ECCurve;
import org.bouncycastle.math.field.FiniteField;
import org.bouncycastle.math.field.Polynomial;
import org.bouncycastle.math.field.PolynomialExtensionField;
import org.bouncycastle.util.Arrays;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/jcajce/provider/asymmetric/util/EC5Util.class */
public class EC5Util {

    /* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/jcajce/provider/asymmetric/util/EC5Util$CustomCurves.class */
    private static class CustomCurves {
        private static Map CURVE_MAP = createCurveMap();

        private CustomCurves() {
        }

        private static Map createCurveMap() {
            HashMap map = new HashMap();
            Enumeration names = CustomNamedCurves.getNames();
            while (names.hasMoreElements()) {
                String str = (String) names.nextElement();
                X9ECParametersHolder byNameLazy = ECNamedCurveTable.getByNameLazy(str);
                if (byNameLazy != null) {
                    ECCurve curve = byNameLazy.getCurve();
                    if (ECAlgorithms.isFpCurve(curve)) {
                        map.put(curve, CustomNamedCurves.getByNameLazy(str).getCurve());
                    }
                }
            }
            ECCurve curve2 = CustomNamedCurves.getByNameLazy("Curve25519").getCurve();
            map.put(new ECCurve.Fp(curve2.getField().getCharacteristic(), curve2.getA().toBigInteger(), curve2.getB().toBigInteger(), curve2.getOrder(), curve2.getCofactor(), true), curve2);
            return map;
        }

        static ECCurve substitute(ECCurve eCCurve) {
            ECCurve eCCurve2 = (ECCurve) CURVE_MAP.get(eCCurve);
            return null != eCCurve2 ? eCCurve2 : eCCurve;
        }
    }

    public static ECCurve getCurve(ProviderConfiguration providerConfiguration, X962Parameters x962Parameters) {
        ECCurve curve;
        Set acceptableNamedCurves = providerConfiguration.getAcceptableNamedCurves();
        if (x962Parameters.isNamedCurve()) {
            ASN1ObjectIdentifier aSN1ObjectIdentifier = ASN1ObjectIdentifier.getInstance(x962Parameters.getParameters());
            if (!acceptableNamedCurves.isEmpty() && !acceptableNamedCurves.contains(aSN1ObjectIdentifier)) {
                throw new IllegalStateException("named curve not acceptable");
            }
            X9ECParameters namedCurveByOid = ECUtil.getNamedCurveByOid(aSN1ObjectIdentifier);
            if (namedCurveByOid == null) {
                namedCurveByOid = (X9ECParameters) providerConfiguration.getAdditionalECParameters().get(aSN1ObjectIdentifier);
            }
            curve = namedCurveByOid.getCurve();
        } else if (x962Parameters.isImplicitlyCA()) {
            curve = providerConfiguration.getEcImplicitlyCa().getCurve();
        } else {
            ASN1Sequence aSN1Sequence = ASN1Sequence.getInstance(x962Parameters.getParameters());
            if (!acceptableNamedCurves.isEmpty()) {
                throw new IllegalStateException("encoded parameters not acceptable");
            }
            curve = aSN1Sequence.size() > 3 ? X9ECParameters.getInstance(aSN1Sequence).getCurve() : ECGOST3410NamedCurves.getByOIDX9(ASN1ObjectIdentifier.getInstance(aSN1Sequence.getObjectAt(0))).getCurve();
        }
        return curve;
    }

    public static ECDomainParameters getDomainParameters(ProviderConfiguration providerConfiguration, ECParameterSpec eCParameterSpec) {
        ECDomainParameters domainParameters;
        if (eCParameterSpec == null) {
            org.bouncycastle.jce.spec.ECParameterSpec ecImplicitlyCa = providerConfiguration.getEcImplicitlyCa();
            domainParameters = new ECDomainParameters(ecImplicitlyCa.getCurve(), ecImplicitlyCa.getG(), ecImplicitlyCa.getN(), ecImplicitlyCa.getH(), ecImplicitlyCa.getSeed());
        } else {
            domainParameters = ECUtil.getDomainParameters(providerConfiguration, convertSpec(eCParameterSpec));
        }
        return domainParameters;
    }

    public static ECParameterSpec convertToSpec(X962Parameters x962Parameters, ECCurve eCCurve) {
        ECParameterSpec eCNamedCurveSpec;
        if (x962Parameters.isNamedCurve()) {
            ASN1ObjectIdentifier aSN1ObjectIdentifier = (ASN1ObjectIdentifier) x962Parameters.getParameters();
            X9ECParameters namedCurveByOid = ECUtil.getNamedCurveByOid(aSN1ObjectIdentifier);
            if (namedCurveByOid == null) {
                Map additionalECParameters = BouncyCastleProvider.CONFIGURATION.getAdditionalECParameters();
                if (!additionalECParameters.isEmpty()) {
                    namedCurveByOid = (X9ECParameters) additionalECParameters.get(aSN1ObjectIdentifier);
                }
            }
            eCNamedCurveSpec = new ECNamedCurveSpec(ECUtil.getCurveName(aSN1ObjectIdentifier), convertCurve(eCCurve, namedCurveByOid.getSeed()), convertPoint(namedCurveByOid.getG()), namedCurveByOid.getN(), namedCurveByOid.getH());
        } else if (x962Parameters.isImplicitlyCA()) {
            eCNamedCurveSpec = null;
        } else {
            ASN1Sequence aSN1Sequence = ASN1Sequence.getInstance(x962Parameters.getParameters());
            if (aSN1Sequence.size() > 3) {
                X9ECParameters x9ECParameters = X9ECParameters.getInstance(aSN1Sequence);
                EllipticCurve ellipticCurveConvertCurve = convertCurve(eCCurve, x9ECParameters.getSeed());
                eCNamedCurveSpec = x9ECParameters.getH() != null ? new ECParameterSpec(ellipticCurveConvertCurve, convertPoint(x9ECParameters.getG()), x9ECParameters.getN(), x9ECParameters.getH().intValue()) : new ECParameterSpec(ellipticCurveConvertCurve, convertPoint(x9ECParameters.getG()), x9ECParameters.getN(), 1);
            } else {
                GOST3410PublicKeyAlgParameters gOST3410PublicKeyAlgParameters = GOST3410PublicKeyAlgParameters.getInstance(aSN1Sequence);
                ECNamedCurveParameterSpec parameterSpec = ECGOST3410NamedCurveTable.getParameterSpec(ECGOST3410NamedCurves.getName(gOST3410PublicKeyAlgParameters.getPublicKeyParamSet()));
                eCNamedCurveSpec = new ECNamedCurveSpec(ECGOST3410NamedCurves.getName(gOST3410PublicKeyAlgParameters.getPublicKeyParamSet()), convertCurve(parameterSpec.getCurve(), parameterSpec.getSeed()), convertPoint(parameterSpec.getG()), parameterSpec.getN(), parameterSpec.getH());
            }
        }
        return eCNamedCurveSpec;
    }

    public static ECParameterSpec convertToSpec(X9ECParameters x9ECParameters) {
        return new ECParameterSpec(convertCurve(x9ECParameters.getCurve(), null), convertPoint(x9ECParameters.getG()), x9ECParameters.getN(), x9ECParameters.getH().intValue());
    }

    public static ECParameterSpec convertToSpec(ECDomainParameters eCDomainParameters) {
        return new ECParameterSpec(convertCurve(eCDomainParameters.getCurve(), null), convertPoint(eCDomainParameters.getG()), eCDomainParameters.getN(), eCDomainParameters.getH().intValue());
    }

    public static EllipticCurve convertCurve(ECCurve eCCurve, byte[] bArr) {
        return new EllipticCurve(convertField(eCCurve.getField()), eCCurve.getA().toBigInteger(), eCCurve.getB().toBigInteger(), null);
    }

    public static ECCurve convertCurve(EllipticCurve ellipticCurve) {
        ECField field = ellipticCurve.getField();
        BigInteger a = ellipticCurve.getA();
        BigInteger b = ellipticCurve.getB();
        if (field instanceof ECFieldFp) {
            return CustomCurves.substitute(new ECCurve.Fp(((ECFieldFp) field).getP(), a, b, null, null));
        }
        ECFieldF2m eCFieldF2m = (ECFieldF2m) field;
        int m = eCFieldF2m.getM();
        int[] iArrConvertMidTerms = ECUtil.convertMidTerms(eCFieldF2m.getMidTermsOfReductionPolynomial());
        return new ECCurve.F2m(m, iArrConvertMidTerms[0], iArrConvertMidTerms[1], iArrConvertMidTerms[2], a, b, (BigInteger) null, (BigInteger) null);
    }

    public static ECField convertField(FiniteField finiteField) {
        if (ECAlgorithms.isFpField(finiteField)) {
            return new ECFieldFp(finiteField.getCharacteristic());
        }
        Polynomial minimalPolynomial = ((PolynomialExtensionField) finiteField).getMinimalPolynomial();
        int[] exponentsPresent = minimalPolynomial.getExponentsPresent();
        return new ECFieldF2m(minimalPolynomial.getDegree(), Arrays.reverseInPlace(Arrays.copyOfRange(exponentsPresent, 1, exponentsPresent.length - 1)));
    }

    public static ECParameterSpec convertSpec(EllipticCurve ellipticCurve, org.bouncycastle.jce.spec.ECParameterSpec eCParameterSpec) {
        ECPoint eCPointConvertPoint = convertPoint(eCParameterSpec.getG());
        return eCParameterSpec instanceof ECNamedCurveParameterSpec ? new ECNamedCurveSpec(((ECNamedCurveParameterSpec) eCParameterSpec).getName(), ellipticCurve, eCPointConvertPoint, eCParameterSpec.getN(), eCParameterSpec.getH()) : new ECParameterSpec(ellipticCurve, eCPointConvertPoint, eCParameterSpec.getN(), eCParameterSpec.getH().intValue());
    }

    public static org.bouncycastle.jce.spec.ECParameterSpec convertSpec(ECParameterSpec eCParameterSpec) {
        ECCurve eCCurveConvertCurve = convertCurve(eCParameterSpec.getCurve());
        org.bouncycastle.math.ec.ECPoint eCPointConvertPoint = convertPoint(eCCurveConvertCurve, eCParameterSpec.getGenerator());
        BigInteger order = eCParameterSpec.getOrder();
        BigInteger bigIntegerValueOf = BigInteger.valueOf(eCParameterSpec.getCofactor());
        byte[] seed = eCParameterSpec.getCurve().getSeed();
        return eCParameterSpec instanceof ECNamedCurveSpec ? new ECNamedCurveParameterSpec(((ECNamedCurveSpec) eCParameterSpec).getName(), eCCurveConvertCurve, eCPointConvertPoint, order, bigIntegerValueOf, seed) : new org.bouncycastle.jce.spec.ECParameterSpec(eCCurveConvertCurve, eCPointConvertPoint, order, bigIntegerValueOf, seed);
    }

    public static org.bouncycastle.math.ec.ECPoint convertPoint(ECParameterSpec eCParameterSpec, ECPoint eCPoint) {
        return convertPoint(convertCurve(eCParameterSpec.getCurve()), eCPoint);
    }

    public static org.bouncycastle.math.ec.ECPoint convertPoint(ECCurve eCCurve, ECPoint eCPoint) {
        return eCCurve.createPoint(eCPoint.getAffineX(), eCPoint.getAffineY());
    }

    public static ECPoint convertPoint(org.bouncycastle.math.ec.ECPoint eCPoint) {
        org.bouncycastle.math.ec.ECPoint eCPointNormalize = eCPoint.normalize();
        return new ECPoint(eCPointNormalize.getAffineXCoord().toBigInteger(), eCPointNormalize.getAffineYCoord().toBigInteger());
    }
}
