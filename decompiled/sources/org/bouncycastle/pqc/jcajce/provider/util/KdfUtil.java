package org.bouncycastle.pqc.jcajce.provider.util;

import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.ASN1OctetString;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.nist.NISTObjectIdentifiers;
import org.bouncycastle.asn1.pkcs.PKCSObjectIdentifiers;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.asn1.x9.X9ObjectIdentifiers;
import org.bouncycastle.crypto.Digest;
import org.bouncycastle.crypto.agreement.kdf.ConcatenationKDFGenerator;
import org.bouncycastle.crypto.digests.SHA256Digest;
import org.bouncycastle.crypto.digests.SHA384Digest;
import org.bouncycastle.crypto.digests.SHA512Digest;
import org.bouncycastle.crypto.digests.SHAKEDigest;
import org.bouncycastle.crypto.generators.HKDFBytesGenerator;
import org.bouncycastle.crypto.generators.KDF2BytesGenerator;
import org.bouncycastle.crypto.macs.KMAC;
import org.bouncycastle.crypto.params.HKDFParameters;
import org.bouncycastle.crypto.params.KDFParameters;
import org.bouncycastle.crypto.params.KeyParameter;
import org.bouncycastle.jcajce.spec.KEMKDFSpec;
import org.bouncycastle.util.Arrays;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/pqc/jcajce/provider/util/KdfUtil.class */
public class KdfUtil {
    public static byte[] makeKeyBytes(KEMKDFSpec kEMKDFSpec, byte[] bArr) {
        if (kEMKDFSpec == null) {
            try {
                byte[] bArr2 = new byte[bArr.length];
                System.arraycopy(bArr, 0, bArr2, 0, bArr2.length);
            } finally {
                Arrays.clear(bArr);
            }
        }
        return makeKeyBytes(kEMKDFSpec.getKdfAlgorithm(), bArr, kEMKDFSpec.getOtherInfo(), kEMKDFSpec.getKeySize());
    }

    static byte[] makeKeyBytes(AlgorithmIdentifier algorithmIdentifier, byte[] bArr, byte[] bArr2, int i) {
        byte[] bArr3 = new byte[(i + 7) / 8];
        if (algorithmIdentifier == null) {
            System.arraycopy(bArr, 0, bArr3, 0, bArr3.length);
        } else if (X9ObjectIdentifiers.id_kdf_kdf2.equals((ASN1Primitive) algorithmIdentifier.getAlgorithm())) {
            KDF2BytesGenerator kDF2BytesGenerator = new KDF2BytesGenerator(getDigest(AlgorithmIdentifier.getInstance(algorithmIdentifier.getParameters()).getAlgorithm()));
            kDF2BytesGenerator.init(new KDFParameters(bArr, bArr2));
            kDF2BytesGenerator.generateBytes(bArr3, 0, bArr3.length);
        } else if (X9ObjectIdentifiers.id_kdf_kdf3.equals((ASN1Primitive) algorithmIdentifier.getAlgorithm())) {
            ConcatenationKDFGenerator concatenationKDFGenerator = new ConcatenationKDFGenerator(getDigest(AlgorithmIdentifier.getInstance(algorithmIdentifier.getParameters()).getAlgorithm()));
            concatenationKDFGenerator.init(new KDFParameters(bArr, bArr2));
            concatenationKDFGenerator.generateBytes(bArr3, 0, bArr3.length);
        } else if (PKCSObjectIdentifiers.id_alg_hkdf_with_sha256.equals((ASN1Primitive) algorithmIdentifier.getAlgorithm())) {
            if (algorithmIdentifier.getParameters() != null) {
                throw new IllegalStateException("HDKF parameter support not added");
            }
            HKDFBytesGenerator hKDFBytesGenerator = new HKDFBytesGenerator(new SHA256Digest());
            hKDFBytesGenerator.init(new HKDFParameters(bArr, null, bArr2));
            hKDFBytesGenerator.generateBytes(bArr3, 0, bArr3.length);
        } else if (PKCSObjectIdentifiers.id_alg_hkdf_with_sha384.equals((ASN1Primitive) algorithmIdentifier.getAlgorithm())) {
            if (algorithmIdentifier.getParameters() != null) {
                throw new IllegalStateException("HDKF parameter support not added");
            }
            HKDFBytesGenerator hKDFBytesGenerator2 = new HKDFBytesGenerator(new SHA384Digest());
            hKDFBytesGenerator2.init(new HKDFParameters(bArr, null, bArr2));
            hKDFBytesGenerator2.generateBytes(bArr3, 0, bArr3.length);
        } else if (PKCSObjectIdentifiers.id_alg_hkdf_with_sha512.equals((ASN1Primitive) algorithmIdentifier.getAlgorithm())) {
            if (algorithmIdentifier.getParameters() != null) {
                throw new IllegalStateException("HDKF parameter support not added");
            }
            HKDFBytesGenerator hKDFBytesGenerator3 = new HKDFBytesGenerator(new SHA512Digest());
            hKDFBytesGenerator3.init(new HKDFParameters(bArr, null, bArr2));
            hKDFBytesGenerator3.generateBytes(bArr3, 0, bArr3.length);
        } else if (NISTObjectIdentifiers.id_Kmac128.equals((ASN1Primitive) algorithmIdentifier.getAlgorithm())) {
            byte[] octets = new byte[0];
            if (algorithmIdentifier.getParameters() != null) {
                octets = ASN1OctetString.getInstance(algorithmIdentifier.getParameters()).getOctets();
            }
            KMAC kmac = new KMAC(128, octets);
            kmac.init(new KeyParameter(bArr, 0, bArr.length));
            kmac.update(bArr2, 0, bArr2.length);
            kmac.doFinal(bArr3, 0, bArr3.length);
        } else if (NISTObjectIdentifiers.id_Kmac256.equals((ASN1Primitive) algorithmIdentifier.getAlgorithm())) {
            byte[] octets2 = new byte[0];
            if (algorithmIdentifier.getParameters() != null) {
                octets2 = ASN1OctetString.getInstance(algorithmIdentifier.getParameters()).getOctets();
            }
            KMAC kmac2 = new KMAC(256, octets2);
            kmac2.init(new KeyParameter(bArr, 0, bArr.length));
            kmac2.update(bArr2, 0, bArr2.length);
            kmac2.doFinal(bArr3, 0, bArr3.length);
        } else {
            if (!NISTObjectIdentifiers.id_shake256.equals((ASN1Primitive) algorithmIdentifier.getAlgorithm())) {
                throw new IllegalArgumentException("Unrecognized KDF: " + algorithmIdentifier.getAlgorithm());
            }
            SHAKEDigest sHAKEDigest = new SHAKEDigest(256);
            sHAKEDigest.update(bArr, 0, bArr.length);
            sHAKEDigest.update(bArr2, 0, bArr2.length);
            sHAKEDigest.doFinal(bArr3, 0, bArr3.length);
        }
        return bArr3;
    }

    static Digest getDigest(ASN1ObjectIdentifier aSN1ObjectIdentifier) {
        if (aSN1ObjectIdentifier.equals((ASN1Primitive) NISTObjectIdentifiers.id_sha256)) {
            return new SHA256Digest();
        }
        if (aSN1ObjectIdentifier.equals((ASN1Primitive) NISTObjectIdentifiers.id_sha512)) {
            return new SHA512Digest();
        }
        if (aSN1ObjectIdentifier.equals((ASN1Primitive) NISTObjectIdentifiers.id_shake128)) {
            return new SHAKEDigest(128);
        }
        if (aSN1ObjectIdentifier.equals((ASN1Primitive) NISTObjectIdentifiers.id_shake256)) {
            return new SHAKEDigest(256);
        }
        throw new IllegalArgumentException("unrecognized digest OID: " + aSN1ObjectIdentifier);
    }
}
