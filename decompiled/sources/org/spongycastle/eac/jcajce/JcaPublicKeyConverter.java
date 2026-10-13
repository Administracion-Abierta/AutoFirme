package org.spongycastle.eac.jcajce;

import java.math.BigInteger;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.Provider;
import java.security.PublicKey;
import java.security.interfaces.ECPublicKey;
import java.security.spec.ECField;
import java.security.spec.ECFieldF2m;
import java.security.spec.ECFieldFp;
import java.security.spec.ECParameterSpec;
import java.security.spec.ECPoint;
import java.security.spec.ECPublicKeySpec;
import java.security.spec.EllipticCurve;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.RSAPublicKeySpec;
import org.spongycastle.asn1.ASN1ObjectIdentifier;
import org.spongycastle.asn1.eac.EACObjectIdentifiers;
import org.spongycastle.asn1.eac.ECDSAPublicKey;
import org.spongycastle.asn1.eac.PublicKeyDataObject;
import org.spongycastle.asn1.eac.RSAPublicKey;
import org.spongycastle.eac.EACException;
import org.spongycastle.math.ec.ECAlgorithms;
import org.spongycastle.math.ec.ECCurve;
import org.spongycastle.math.field.FiniteField;
import org.spongycastle.math.field.Polynomial;
import org.spongycastle.math.field.PolynomialExtensionField;
import org.spongycastle.util.Arrays;

/* JADX INFO: loaded from: autofirma.jar:org/spongycastle/eac/jcajce/JcaPublicKeyConverter.class */
public class JcaPublicKeyConverter {
    private EACHelper helper = new DefaultEACHelper();

    public JcaPublicKeyConverter setProvider(String providerName) {
        this.helper = new NamedEACHelper(providerName);
        return this;
    }

    public JcaPublicKeyConverter setProvider(Provider provider) {
        this.helper = new ProviderEACHelper(provider);
        return this;
    }

    public PublicKey getKey(PublicKeyDataObject publicKeyDataObject) throws EACException, InvalidKeySpecException {
        if (publicKeyDataObject.getUsage().on(EACObjectIdentifiers.id_TA_ECDSA)) {
            return getECPublicKeyPublicKey((ECDSAPublicKey) publicKeyDataObject);
        }
        RSAPublicKey pubKey = (RSAPublicKey) publicKeyDataObject;
        RSAPublicKeySpec pubKeySpec = new RSAPublicKeySpec(pubKey.getModulus(), pubKey.getPublicExponent());
        try {
            KeyFactory factk = this.helper.createKeyFactory("RSA");
            return factk.generatePublic(pubKeySpec);
        } catch (NoSuchAlgorithmException e) {
            throw new EACException("cannot find algorithm ECDSA: " + e.getMessage(), e);
        } catch (NoSuchProviderException e2) {
            throw new EACException("cannot find provider: " + e2.getMessage(), e2);
        }
    }

    private PublicKey getECPublicKeyPublicKey(ECDSAPublicKey key) throws EACException, InvalidKeySpecException {
        ECParameterSpec spec = getParams(key);
        ECPoint publicPoint = getPublicPoint(key);
        ECPublicKeySpec pubKeySpec = new ECPublicKeySpec(publicPoint, spec);
        try {
            KeyFactory factk = this.helper.createKeyFactory("ECDSA");
            return factk.generatePublic(pubKeySpec);
        } catch (NoSuchAlgorithmException e) {
            throw new EACException("cannot find algorithm ECDSA: " + e.getMessage(), e);
        } catch (NoSuchProviderException e2) {
            throw new EACException("cannot find provider: " + e2.getMessage(), e2);
        }
    }

    private ECPoint getPublicPoint(ECDSAPublicKey key) {
        if (!key.hasParameters()) {
            throw new IllegalArgumentException("Public key does not contains EC Params");
        }
        BigInteger p = key.getPrimeModulusP();
        ECCurve.Fp curve = new ECCurve.Fp(p, key.getFirstCoefA(), key.getSecondCoefB(), key.getOrderOfBasePointR(), key.getCofactorF());
        org.spongycastle.math.ec.ECPoint.Fp pubY = (org.spongycastle.math.ec.ECPoint.Fp) curve.decodePoint(key.getPublicPointY());
        return new ECPoint(pubY.getAffineXCoord().toBigInteger(), pubY.getAffineYCoord().toBigInteger());
    }

    private ECParameterSpec getParams(ECDSAPublicKey key) {
        if (!key.hasParameters()) {
            throw new IllegalArgumentException("Public key does not contains EC Params");
        }
        BigInteger p = key.getPrimeModulusP();
        ECCurve.Fp curve = new ECCurve.Fp(p, key.getFirstCoefA(), key.getSecondCoefB(), key.getOrderOfBasePointR(), key.getCofactorF());
        org.spongycastle.math.ec.ECPoint G = curve.decodePoint(key.getBasePointG());
        BigInteger order = key.getOrderOfBasePointR();
        BigInteger coFactor = key.getCofactorF();
        EllipticCurve jcaCurve = convertCurve(curve);
        return new ECParameterSpec(jcaCurve, new ECPoint(G.getAffineXCoord().toBigInteger(), G.getAffineYCoord().toBigInteger()), order, coFactor.intValue());
    }

    public PublicKeyDataObject getPublicKeyDataObject(ASN1ObjectIdentifier usage, PublicKey publicKey) {
        if (publicKey instanceof java.security.interfaces.RSAPublicKey) {
            java.security.interfaces.RSAPublicKey pubKey = (java.security.interfaces.RSAPublicKey) publicKey;
            return new RSAPublicKey(usage, pubKey.getModulus(), pubKey.getPublicExponent());
        }
        ECPublicKey pubKey2 = (ECPublicKey) publicKey;
        ECParameterSpec params = pubKey2.getParams();
        return new ECDSAPublicKey(usage, ((ECFieldFp) params.getCurve().getField()).getP(), params.getCurve().getA(), params.getCurve().getB(), convertPoint(convertCurve(params.getCurve(), params.getOrder(), params.getCofactor()), params.getGenerator()).getEncoded(), params.getOrder(), convertPoint(convertCurve(params.getCurve(), params.getOrder(), params.getCofactor()), pubKey2.getW()).getEncoded(), params.getCofactor());
    }

    private static org.spongycastle.math.ec.ECPoint convertPoint(ECCurve curve, ECPoint point) {
        return curve.createPoint(point.getAffineX(), point.getAffineY());
    }

    private static ECCurve convertCurve(EllipticCurve ec, BigInteger order, int coFactor) {
        ECField field = ec.getField();
        BigInteger a = ec.getA();
        BigInteger b = ec.getB();
        if (field instanceof ECFieldFp) {
            return new ECCurve.Fp(((ECFieldFp) field).getP(), a, b, order, BigInteger.valueOf(coFactor));
        }
        throw new IllegalStateException("not implemented yet!!!");
    }

    private static EllipticCurve convertCurve(ECCurve curve) {
        ECField field = convertField(curve.getField());
        BigInteger a = curve.getA().toBigInteger();
        BigInteger b = curve.getB().toBigInteger();
        return new EllipticCurve(field, a, b, null);
    }

    private static ECField convertField(FiniteField field) {
        if (ECAlgorithms.isFpField(field)) {
            return new ECFieldFp(field.getCharacteristic());
        }
        Polynomial poly = ((PolynomialExtensionField) field).getMinimalPolynomial();
        int[] exponents = poly.getExponentsPresent();
        int[] ks = Arrays.reverse(Arrays.copyOfRange(exponents, 1, exponents.length - 1));
        return new ECFieldF2m(poly.getDegree(), ks);
    }
}
