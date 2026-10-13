package org.spongycastle.jce;

import java.io.UnsupportedEncodingException;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PrivateKey;
import java.security.Provider;
import java.security.PublicKey;
import java.security.Security;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import org.spongycastle.asn1.ASN1ObjectIdentifier;
import org.spongycastle.asn1.ASN1Primitive;
import org.spongycastle.asn1.cryptopro.CryptoProObjectIdentifiers;
import org.spongycastle.asn1.pkcs.PrivateKeyInfo;
import org.spongycastle.asn1.x509.AlgorithmIdentifier;
import org.spongycastle.asn1.x509.SubjectPublicKeyInfo;
import org.spongycastle.asn1.x9.X962Parameters;
import org.spongycastle.asn1.x9.X9ECParameters;
import org.spongycastle.asn1.x9.X9ObjectIdentifiers;
import org.spongycastle.jcajce.provider.asymmetric.util.ECUtil;
import org.spongycastle.jce.provider.BouncyCastleProvider;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:org/spongycastle/jce/ECKeyUtil.class */
public class ECKeyUtil {
    public static PublicKey publicToExplicitParameters(PublicKey key, String providerName) throws NoSuchAlgorithmException, IllegalArgumentException, NoSuchProviderException {
        Provider provider = Security.getProvider(providerName);
        if (provider == null) {
            throw new NoSuchProviderException("cannot find provider: " + providerName);
        }
        return publicToExplicitParameters(key, provider);
    }

    public static PublicKey publicToExplicitParameters(PublicKey key, Provider provider) throws NoSuchAlgorithmException, IllegalArgumentException {
        X9ECParameters curveParams;
        try {
            SubjectPublicKeyInfo info = SubjectPublicKeyInfo.getInstance(ASN1Primitive.fromByteArray(key.getEncoded()));
            if (info.getAlgorithmId().getAlgorithm().equals(CryptoProObjectIdentifiers.gostR3410_2001)) {
                throw new IllegalArgumentException("cannot convert GOST key to explicit parameters.");
            }
            X962Parameters params = X962Parameters.getInstance(info.getAlgorithmId().getParameters());
            if (params.isNamedCurve()) {
                ASN1ObjectIdentifier oid = ASN1ObjectIdentifier.getInstance(params.getParameters());
                X9ECParameters curveParams2 = ECUtil.getNamedCurveByOid(oid);
                curveParams = new X9ECParameters(curveParams2.getCurve(), curveParams2.getG(), curveParams2.getN(), curveParams2.getH());
            } else if (params.isImplicitlyCA()) {
                curveParams = new X9ECParameters(BouncyCastleProvider.CONFIGURATION.getEcImplicitlyCa().getCurve(), BouncyCastleProvider.CONFIGURATION.getEcImplicitlyCa().getG(), BouncyCastleProvider.CONFIGURATION.getEcImplicitlyCa().getN(), BouncyCastleProvider.CONFIGURATION.getEcImplicitlyCa().getH());
            } else {
                return key;
            }
            SubjectPublicKeyInfo info2 = new SubjectPublicKeyInfo(new AlgorithmIdentifier(X9ObjectIdentifiers.id_ecPublicKey, new X962Parameters(curveParams)), info.getPublicKeyData().getBytes());
            KeyFactory keyFact = KeyFactory.getInstance(key.getAlgorithm(), provider);
            return keyFact.generatePublic(new X509EncodedKeySpec(info2.getEncoded()));
        } catch (IllegalArgumentException e) {
            throw e;
        } catch (NoSuchAlgorithmException e2) {
            throw e2;
        } catch (Exception e3) {
            throw new UnexpectedException(e3);
        }
    }

    public static PrivateKey privateToExplicitParameters(PrivateKey key, String providerName) throws NoSuchAlgorithmException, IllegalArgumentException, NoSuchProviderException {
        Provider provider = Security.getProvider(providerName);
        if (provider == null) {
            throw new NoSuchProviderException("cannot find provider: " + providerName);
        }
        return privateToExplicitParameters(key, provider);
    }

    public static PrivateKey privateToExplicitParameters(PrivateKey key, Provider provider) throws NoSuchAlgorithmException, IllegalArgumentException {
        X9ECParameters curveParams;
        try {
            PrivateKeyInfo info = PrivateKeyInfo.getInstance(ASN1Primitive.fromByteArray(key.getEncoded()));
            if (info.getAlgorithmId().getAlgorithm().equals(CryptoProObjectIdentifiers.gostR3410_2001)) {
                throw new UnsupportedEncodingException("cannot convert GOST key to explicit parameters.");
            }
            X962Parameters params = X962Parameters.getInstance(info.getAlgorithmId().getParameters());
            if (params.isNamedCurve()) {
                ASN1ObjectIdentifier oid = ASN1ObjectIdentifier.getInstance(params.getParameters());
                X9ECParameters curveParams2 = ECUtil.getNamedCurveByOid(oid);
                curveParams = new X9ECParameters(curveParams2.getCurve(), curveParams2.getG(), curveParams2.getN(), curveParams2.getH());
            } else if (params.isImplicitlyCA()) {
                curveParams = new X9ECParameters(BouncyCastleProvider.CONFIGURATION.getEcImplicitlyCa().getCurve(), BouncyCastleProvider.CONFIGURATION.getEcImplicitlyCa().getG(), BouncyCastleProvider.CONFIGURATION.getEcImplicitlyCa().getN(), BouncyCastleProvider.CONFIGURATION.getEcImplicitlyCa().getH());
            } else {
                return key;
            }
            PrivateKeyInfo info2 = new PrivateKeyInfo(new AlgorithmIdentifier(X9ObjectIdentifiers.id_ecPublicKey, new X962Parameters(curveParams)), info.parsePrivateKey());
            KeyFactory keyFact = KeyFactory.getInstance(key.getAlgorithm(), provider);
            return keyFact.generatePrivate(new PKCS8EncodedKeySpec(info2.getEncoded()));
        } catch (IllegalArgumentException e) {
            throw e;
        } catch (NoSuchAlgorithmException e2) {
            throw e2;
        } catch (Exception e3) {
            throw new UnexpectedException(e3);
        }
    }

    /* JADX INFO: loaded from: autofirmaConfigurador.jar:org/spongycastle/jce/ECKeyUtil$UnexpectedException.class */
    private static class UnexpectedException extends RuntimeException {
        private Throwable cause;

        UnexpectedException(Throwable cause) {
            super(cause.toString());
            this.cause = cause;
        }

        @Override // java.lang.Throwable
        public Throwable getCause() {
            return this.cause;
        }
    }
}
