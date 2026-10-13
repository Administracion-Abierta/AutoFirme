package org.bouncycastle.pkix;

import java.io.IOException;
import java.math.BigInteger;
import java.security.AccessControlException;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.security.Security;
import java.util.HashMap;
import java.util.Map;
import java.util.WeakHashMap;
import org.bouncycastle.asn1.ASN1Integer;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.pkcs.PKCSObjectIdentifiers;
import org.bouncycastle.asn1.pkcs.RSAPublicKey;
import org.bouncycastle.asn1.x509.SubjectPublicKeyInfo;
import org.bouncycastle.asn1.x509.X509ObjectIdentifiers;
import org.bouncycastle.asn1.x9.X962Parameters;
import org.bouncycastle.asn1.x9.X9FieldID;
import org.bouncycastle.asn1.x9.X9ObjectIdentifiers;
import org.bouncycastle.crypto.CryptoServicesRegistrar;
import org.bouncycastle.math.Primes;
import org.bouncycastle.util.BigIntegers;
import org.bouncycastle.util.Strings;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/pkix/SubjectPublicKeyInfoChecker.class */
public class SubjectPublicKeyInfoChecker {
    private static final Cache validatedQs = new Cache();
    private static final Cache validatedMods = new Cache();
    private static final BigInteger SMALL_PRIMES_PRODUCT = new BigInteger("8138e8a0fcf3a4e84a771d40fd305d7f4aa59306d7251de54d98af8fe95729a1f73d893fa424cd2edc8636a6c3285e022b0e3866a565ae8108eed8591cd4fe8d2ce86165a978d719ebf647f362d33fca29cd179fb42401cbaf3df0c614056f9c8f3cfd51e474afb6bc6974f78db8aba8e9e517fded658591ab7502bd41849462f", 16);

    /* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/pkix/SubjectPublicKeyInfoChecker$Cache.class */
    private static class Cache {
        private final Map<BigInteger, Boolean> values;
        private final BigInteger[] preserve;
        private int preserveCounter;

        private Cache() {
            this.values = new WeakHashMap();
            this.preserve = new BigInteger[8];
            this.preserveCounter = 0;
        }

        public synchronized void add(BigInteger bigInteger) {
            this.values.put(bigInteger, Boolean.TRUE);
            this.preserve[this.preserveCounter] = bigInteger;
            this.preserveCounter = (this.preserveCounter + 1) % this.preserve.length;
        }

        public synchronized boolean contains(BigInteger bigInteger) {
            return this.values.containsKey(bigInteger);
        }

        public synchronized int size() {
            return this.values.size();
        }

        public synchronized void clear() {
            this.values.clear();
            for (int i = 0; i != this.preserve.length; i++) {
                this.preserve[i] = null;
            }
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/pkix/SubjectPublicKeyInfoChecker$Properties.class */
    private static class Properties {
        private static final ThreadLocal threadProperties = new ThreadLocal();

        private Properties() {
        }

        static boolean isOverrideSet(String str) {
            try {
                return isSetTrue(getPropertyValue(str));
            } catch (AccessControlException e) {
                return false;
            }
        }

        static boolean setThreadOverride(String str, boolean z) {
            boolean zIsOverrideSet = isOverrideSet(str);
            Map map = (Map) threadProperties.get();
            if (map == null) {
                map = new HashMap();
                threadProperties.set(map);
            }
            map.put(str, z ? "true" : "false");
            return zIsOverrideSet;
        }

        static boolean removeThreadOverride(String str) {
            String str2;
            Map map = (Map) threadProperties.get();
            if (map == null || (str2 = (String) map.remove(str)) == null) {
                return false;
            }
            if (map.isEmpty()) {
                threadProperties.remove();
            }
            return "true".equals(Strings.toLowerCase(str2));
        }

        static int asInteger(String str, int i) {
            String propertyValue = getPropertyValue(str);
            return propertyValue != null ? Integer.parseInt(propertyValue) : i;
        }

        static String getPropertyValue(final String str) {
            String str2;
            String str3 = (String) AccessController.doPrivileged(new PrivilegedAction() { // from class: org.bouncycastle.pkix.SubjectPublicKeyInfoChecker.Properties.1
                @Override // java.security.PrivilegedAction
                public Object run() {
                    return Security.getProperty(str);
                }
            });
            if (str3 != null) {
                return str3;
            }
            Map map = (Map) threadProperties.get();
            return (map == null || (str2 = (String) map.get(str)) == null) ? (String) AccessController.doPrivileged(new PrivilegedAction() { // from class: org.bouncycastle.pkix.SubjectPublicKeyInfoChecker.Properties.2
                @Override // java.security.PrivilegedAction
                public Object run() {
                    return System.getProperty(str);
                }
            }) : str2;
        }

        private static boolean isSetTrue(String str) {
            if (str == null || str.length() != 4) {
                return false;
            }
            return (str.charAt(0) == 't' || str.charAt(0) == 'T') && (str.charAt(1) == 'r' || str.charAt(1) == 'R') && ((str.charAt(2) == 'u' || str.charAt(2) == 'U') && (str.charAt(3) == 'e' || str.charAt(3) == 'E'));
        }
    }

    public static void checkInfo(SubjectPublicKeyInfo subjectPublicKeyInfo) {
        ASN1ObjectIdentifier algorithm = subjectPublicKeyInfo.getAlgorithm().getAlgorithm();
        if (!X9ObjectIdentifiers.id_ecPublicKey.equals((ASN1Primitive) algorithm)) {
            if (PKCSObjectIdentifiers.rsaEncryption.equals((ASN1Primitive) algorithm) || X509ObjectIdentifiers.id_ea_rsa.equals((ASN1Primitive) algorithm) || PKCSObjectIdentifiers.id_RSAES_OAEP.equals((ASN1Primitive) algorithm) || PKCSObjectIdentifiers.id_RSASSA_PSS.equals((ASN1Primitive) algorithm)) {
                try {
                    RSAPublicKey rSAPublicKey = RSAPublicKey.getInstance(subjectPublicKeyInfo.parsePublicKey());
                    if ((rSAPublicKey.getPublicExponent().intValue() & 1) == 0) {
                        throw new IllegalArgumentException("RSA publicExponent is even");
                    }
                    if (validatedMods.contains(rSAPublicKey.getModulus())) {
                        return;
                    }
                    validate(rSAPublicKey.getModulus());
                    validatedMods.add(rSAPublicKey.getModulus());
                    return;
                } catch (IOException e) {
                    throw new IllegalArgumentException("unable to parse RSA key");
                }
            }
            return;
        }
        X962Parameters x962Parameters = X962Parameters.getInstance(subjectPublicKeyInfo.getAlgorithm().getParameters());
        if (x962Parameters.isImplicitlyCA() || x962Parameters.isNamedCurve()) {
            return;
        }
        X9FieldID x9FieldID = X9FieldID.getInstance(ASN1Sequence.getInstance(x962Parameters.getParameters()).getObjectAt(1));
        if (x9FieldID.getIdentifier().equals((ASN1Primitive) X9FieldID.prime_field)) {
            BigInteger value = ASN1Integer.getInstance(x9FieldID.getParameters()).getValue();
            if (validatedQs.contains(value)) {
                return;
            }
            int iAsInteger = Properties.asInteger("org.bouncycastle.ec.fp_max_size", 1042);
            int iAsInteger2 = Properties.asInteger("org.bouncycastle.ec.fp_certainty", 100);
            int iBitLength = value.bitLength();
            if (iAsInteger < iBitLength) {
                throw new IllegalArgumentException("Fp q value out of range");
            }
            if (Primes.hasAnySmallFactors(value) || !Primes.isMRProbablePrime(value, CryptoServicesRegistrar.getSecureRandom(), getNumberOfIterations(iBitLength, iAsInteger2))) {
                throw new IllegalArgumentException("Fp q value not prime");
            }
            validatedQs.add(value);
        }
    }

    private static boolean hasAnySmallFactors(BigInteger bigInteger) {
        BigInteger bigInteger2 = bigInteger;
        BigInteger bigInteger3 = SMALL_PRIMES_PRODUCT;
        if (bigInteger.compareTo(SMALL_PRIMES_PRODUCT) < 0) {
            bigInteger2 = SMALL_PRIMES_PRODUCT;
            bigInteger3 = bigInteger;
        }
        return !BigIntegers.modOddIsCoprimeVar(bigInteger2, bigInteger3);
    }

    private static void validate(BigInteger bigInteger) {
        int i;
        if ((bigInteger.intValue() & 1) == 0) {
            throw new IllegalArgumentException("RSA modulus is even");
        }
        if (Properties.isOverrideSet("org.bouncycastle.rsa.allow_unsafe_mod")) {
            return;
        }
        if (Properties.asInteger("org.bouncycastle.rsa.max_size", 16384) < bigInteger.bitLength()) {
            throw new IllegalArgumentException("RSA modulus out of range");
        }
        if (hasAnySmallFactors(bigInteger)) {
            throw new IllegalArgumentException("RSA modulus has a small prime factor");
        }
        int iBitLength = bigInteger.bitLength() / 2;
        if (iBitLength >= 1536) {
            i = 3;
        } else if (iBitLength >= 1024) {
            i = 4;
        } else {
            i = iBitLength >= 512 ? 7 : 50;
        }
        if (!Primes.enhancedMRProbablePrimeTest(bigInteger, CryptoServicesRegistrar.getSecureRandom(), i).isProvablyComposite()) {
            throw new IllegalArgumentException("RSA modulus is not composite");
        }
    }

    private static int getNumberOfIterations(int i, int i2) {
        if (i >= 1536) {
            if (i2 <= 100) {
                return 3;
            }
            if (i2 <= 128) {
                return 4;
            }
            return 4 + (((i2 - 128) + 1) / 2);
        }
        if (i >= 1024) {
            if (i2 <= 100) {
                return 4;
            }
            if (i2 <= 112) {
                return 5;
            }
            return 5 + (((i2 - 112) + 1) / 2);
        }
        if (i < 512) {
            if (i2 <= 80) {
                return 40;
            }
            return 40 + (((i2 - 80) + 1) / 2);
        }
        if (i2 <= 80) {
            return 5;
        }
        if (i2 <= 100) {
            return 7;
        }
        return 7 + (((i2 - 100) + 1) / 2);
    }

    public static boolean setThreadOverride(String str, boolean z) {
        return Properties.setThreadOverride(str, z);
    }

    public static boolean removeThreadOverride(String str) {
        return Properties.removeThreadOverride(str);
    }
}
