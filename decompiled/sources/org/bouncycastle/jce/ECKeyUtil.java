package org.bouncycastle.jce;

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
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.cryptopro.CryptoProObjectIdentifiers;
import org.bouncycastle.asn1.pkcs.PrivateKeyInfo;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.asn1.x509.SubjectPublicKeyInfo;
import org.bouncycastle.asn1.x9.X962Parameters;
import org.bouncycastle.asn1.x9.X9ECParameters;
import org.bouncycastle.asn1.x9.X9ECPoint;
import org.bouncycastle.asn1.x9.X9ObjectIdentifiers;
import org.bouncycastle.jcajce.provider.asymmetric.util.ECUtil;
import org.bouncycastle.jce.provider.BouncyCastleProvider;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/jce/ECKeyUtil.class */
public class ECKeyUtil {

    /* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/jce/ECKeyUtil$UnexpectedException.class */
    private static class UnexpectedException extends RuntimeException {
        private Throwable cause;

        UnexpectedException(Throwable th) {
            super(th.toString());
            this.cause = th;
        }

        @Override // java.lang.Throwable
        public Throwable getCause() {
            return this.cause;
        }
    }

    public static PublicKey publicToExplicitParameters(PublicKey publicKey, String str) throws NoSuchAlgorithmException, IllegalArgumentException, NoSuchProviderException {
        Provider provider = Security.getProvider(str);
        if (provider == null) {
            throw new NoSuchProviderException("cannot find provider: " + str);
        }
        return publicToExplicitParameters(publicKey, provider);
    }

    public static PublicKey publicToExplicitParameters(PublicKey publicKey, Provider provider) throws NoSuchAlgorithmException, IllegalArgumentException {
        X9ECParameters x9ECParameters;
        try {
            SubjectPublicKeyInfo subjectPublicKeyInfo = SubjectPublicKeyInfo.getInstance(ASN1Primitive.fromByteArray(publicKey.getEncoded()));
            if (subjectPublicKeyInfo.getAlgorithm().getAlgorithm().equals((ASN1Primitive) CryptoProObjectIdentifiers.gostR3410_2001)) {
                throw new IllegalArgumentException("cannot convert GOST key to explicit parameters.");
            }
            X962Parameters x962Parameters = X962Parameters.getInstance(subjectPublicKeyInfo.getAlgorithm().getParameters());
            if (x962Parameters.isNamedCurve()) {
                x9ECParameters = ECUtil.getNamedCurveByOid(ASN1ObjectIdentifier.getInstance(x962Parameters.getParameters()));
                if (x9ECParameters.hasSeed()) {
                    x9ECParameters = new X9ECParameters(x9ECParameters.getCurve(), x9ECParameters.getBaseEntry(), x9ECParameters.getN(), x9ECParameters.getH());
                }
            } else {
                if (!x962Parameters.isImplicitlyCA()) {
                    return publicKey;
                }
                x9ECParameters = new X9ECParameters(BouncyCastleProvider.CONFIGURATION.getEcImplicitlyCa().getCurve(), new X9ECPoint(BouncyCastleProvider.CONFIGURATION.getEcImplicitlyCa().getG(), false), BouncyCastleProvider.CONFIGURATION.getEcImplicitlyCa().getN(), BouncyCastleProvider.CONFIGURATION.getEcImplicitlyCa().getH());
            }
            return KeyFactory.getInstance(publicKey.getAlgorithm(), provider).generatePublic(new X509EncodedKeySpec(new SubjectPublicKeyInfo(new AlgorithmIdentifier(X9ObjectIdentifiers.id_ecPublicKey, new X962Parameters(x9ECParameters)), subjectPublicKeyInfo.getPublicKeyData().getBytes()).getEncoded()));
        } catch (IllegalArgumentException e) {
            throw e;
        } catch (NoSuchAlgorithmException e2) {
            throw e2;
        } catch (Exception e3) {
            throw new UnexpectedException(e3);
        }
    }

    public static PrivateKey privateToExplicitParameters(PrivateKey privateKey, String str) throws NoSuchAlgorithmException, IllegalArgumentException, NoSuchProviderException {
        Provider provider = Security.getProvider(str);
        if (provider == null) {
            throw new NoSuchProviderException("cannot find provider: " + str);
        }
        return privateToExplicitParameters(privateKey, provider);
    }

    public static PrivateKey privateToExplicitParameters(PrivateKey privateKey, Provider provider) throws NoSuchAlgorithmException, IllegalArgumentException {
        X9ECParameters x9ECParameters;
        try {
            PrivateKeyInfo privateKeyInfo = PrivateKeyInfo.getInstance(ASN1Primitive.fromByteArray(privateKey.getEncoded()));
            if (privateKeyInfo.getPrivateKeyAlgorithm().getAlgorithm().equals((ASN1Primitive) CryptoProObjectIdentifiers.gostR3410_2001)) {
                throw new UnsupportedEncodingException("cannot convert GOST key to explicit parameters.");
            }
            X962Parameters x962Parameters = X962Parameters.getInstance(privateKeyInfo.getPrivateKeyAlgorithm().getParameters());
            if (x962Parameters.isNamedCurve()) {
                x9ECParameters = ECUtil.getNamedCurveByOid(ASN1ObjectIdentifier.getInstance(x962Parameters.getParameters()));
                if (x9ECParameters.hasSeed()) {
                    x9ECParameters = new X9ECParameters(x9ECParameters.getCurve(), x9ECParameters.getBaseEntry(), x9ECParameters.getN(), x9ECParameters.getH());
                }
            } else {
                if (!x962Parameters.isImplicitlyCA()) {
                    return privateKey;
                }
                x9ECParameters = new X9ECParameters(BouncyCastleProvider.CONFIGURATION.getEcImplicitlyCa().getCurve(), new X9ECPoint(BouncyCastleProvider.CONFIGURATION.getEcImplicitlyCa().getG(), false), BouncyCastleProvider.CONFIGURATION.getEcImplicitlyCa().getN(), BouncyCastleProvider.CONFIGURATION.getEcImplicitlyCa().getH());
            }
            return KeyFactory.getInstance(privateKey.getAlgorithm(), provider).generatePrivate(new PKCS8EncodedKeySpec(new PrivateKeyInfo(new AlgorithmIdentifier(X9ObjectIdentifiers.id_ecPublicKey, new X962Parameters(x9ECParameters)), privateKeyInfo.parsePrivateKey()).getEncoded()));
        } catch (IllegalArgumentException e) {
            throw e;
        } catch (NoSuchAlgorithmException e2) {
            throw e2;
        } catch (Exception e3) {
            throw new UnexpectedException(e3);
        }
    }
}
