package org.spongycastle.operator.bc;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import org.spongycastle.asn1.cryptopro.CryptoProObjectIdentifiers;
import org.spongycastle.asn1.nist.NISTObjectIdentifiers;
import org.spongycastle.asn1.oiw.OIWObjectIdentifiers;
import org.spongycastle.asn1.pkcs.PKCSObjectIdentifiers;
import org.spongycastle.asn1.rosstandart.RosstandartObjectIdentifiers;
import org.spongycastle.asn1.teletrust.TeleTrusTObjectIdentifiers;
import org.spongycastle.asn1.x509.AlgorithmIdentifier;
import org.spongycastle.crypto.ExtendedDigest;
import org.spongycastle.crypto.digests.GOST3411Digest;
import org.spongycastle.crypto.digests.GOST3411_2012_256Digest;
import org.spongycastle.crypto.digests.GOST3411_2012_512Digest;
import org.spongycastle.crypto.digests.MD2Digest;
import org.spongycastle.crypto.digests.MD4Digest;
import org.spongycastle.crypto.digests.MD5Digest;
import org.spongycastle.crypto.digests.RIPEMD128Digest;
import org.spongycastle.crypto.digests.RIPEMD160Digest;
import org.spongycastle.crypto.digests.RIPEMD256Digest;
import org.spongycastle.crypto.digests.SHA1Digest;
import org.spongycastle.crypto.digests.SHA224Digest;
import org.spongycastle.crypto.digests.SHA256Digest;
import org.spongycastle.crypto.digests.SHA384Digest;
import org.spongycastle.crypto.digests.SHA3Digest;
import org.spongycastle.crypto.digests.SHA512Digest;
import org.spongycastle.operator.OperatorCreationException;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:org/spongycastle/operator/bc/BcDefaultDigestProvider.class */
public class BcDefaultDigestProvider implements BcDigestProvider {
    private static final Map lookup = createTable();
    public static final BcDigestProvider INSTANCE = new BcDefaultDigestProvider();

    private static Map createTable() {
        Map table = new HashMap();
        table.put(OIWObjectIdentifiers.idSHA1, new BcDigestProvider() { // from class: org.spongycastle.operator.bc.BcDefaultDigestProvider.1
            @Override // org.spongycastle.operator.bc.BcDigestProvider
            public ExtendedDigest get(AlgorithmIdentifier digestAlgorithmIdentifier) {
                return new SHA1Digest();
            }
        });
        table.put(NISTObjectIdentifiers.id_sha224, new BcDigestProvider() { // from class: org.spongycastle.operator.bc.BcDefaultDigestProvider.2
            @Override // org.spongycastle.operator.bc.BcDigestProvider
            public ExtendedDigest get(AlgorithmIdentifier digestAlgorithmIdentifier) {
                return new SHA224Digest();
            }
        });
        table.put(NISTObjectIdentifiers.id_sha256, new BcDigestProvider() { // from class: org.spongycastle.operator.bc.BcDefaultDigestProvider.3
            @Override // org.spongycastle.operator.bc.BcDigestProvider
            public ExtendedDigest get(AlgorithmIdentifier digestAlgorithmIdentifier) {
                return new SHA256Digest();
            }
        });
        table.put(NISTObjectIdentifiers.id_sha384, new BcDigestProvider() { // from class: org.spongycastle.operator.bc.BcDefaultDigestProvider.4
            @Override // org.spongycastle.operator.bc.BcDigestProvider
            public ExtendedDigest get(AlgorithmIdentifier digestAlgorithmIdentifier) {
                return new SHA384Digest();
            }
        });
        table.put(NISTObjectIdentifiers.id_sha512, new BcDigestProvider() { // from class: org.spongycastle.operator.bc.BcDefaultDigestProvider.5
            @Override // org.spongycastle.operator.bc.BcDigestProvider
            public ExtendedDigest get(AlgorithmIdentifier digestAlgorithmIdentifier) {
                return new SHA512Digest();
            }
        });
        table.put(NISTObjectIdentifiers.id_sha3_224, new BcDigestProvider() { // from class: org.spongycastle.operator.bc.BcDefaultDigestProvider.6
            @Override // org.spongycastle.operator.bc.BcDigestProvider
            public ExtendedDigest get(AlgorithmIdentifier digestAlgorithmIdentifier) {
                return new SHA3Digest(224);
            }
        });
        table.put(NISTObjectIdentifiers.id_sha3_256, new BcDigestProvider() { // from class: org.spongycastle.operator.bc.BcDefaultDigestProvider.7
            @Override // org.spongycastle.operator.bc.BcDigestProvider
            public ExtendedDigest get(AlgorithmIdentifier digestAlgorithmIdentifier) {
                return new SHA3Digest(256);
            }
        });
        table.put(NISTObjectIdentifiers.id_sha3_384, new BcDigestProvider() { // from class: org.spongycastle.operator.bc.BcDefaultDigestProvider.8
            @Override // org.spongycastle.operator.bc.BcDigestProvider
            public ExtendedDigest get(AlgorithmIdentifier digestAlgorithmIdentifier) {
                return new SHA3Digest(384);
            }
        });
        table.put(NISTObjectIdentifiers.id_sha3_512, new BcDigestProvider() { // from class: org.spongycastle.operator.bc.BcDefaultDigestProvider.9
            @Override // org.spongycastle.operator.bc.BcDigestProvider
            public ExtendedDigest get(AlgorithmIdentifier digestAlgorithmIdentifier) {
                return new SHA3Digest(512);
            }
        });
        table.put(PKCSObjectIdentifiers.md5, new BcDigestProvider() { // from class: org.spongycastle.operator.bc.BcDefaultDigestProvider.10
            @Override // org.spongycastle.operator.bc.BcDigestProvider
            public ExtendedDigest get(AlgorithmIdentifier digestAlgorithmIdentifier) {
                return new MD5Digest();
            }
        });
        table.put(PKCSObjectIdentifiers.md4, new BcDigestProvider() { // from class: org.spongycastle.operator.bc.BcDefaultDigestProvider.11
            @Override // org.spongycastle.operator.bc.BcDigestProvider
            public ExtendedDigest get(AlgorithmIdentifier digestAlgorithmIdentifier) {
                return new MD4Digest();
            }
        });
        table.put(PKCSObjectIdentifiers.md2, new BcDigestProvider() { // from class: org.spongycastle.operator.bc.BcDefaultDigestProvider.12
            @Override // org.spongycastle.operator.bc.BcDigestProvider
            public ExtendedDigest get(AlgorithmIdentifier digestAlgorithmIdentifier) {
                return new MD2Digest();
            }
        });
        table.put(CryptoProObjectIdentifiers.gostR3411, new BcDigestProvider() { // from class: org.spongycastle.operator.bc.BcDefaultDigestProvider.13
            @Override // org.spongycastle.operator.bc.BcDigestProvider
            public ExtendedDigest get(AlgorithmIdentifier digestAlgorithmIdentifier) {
                return new GOST3411Digest();
            }
        });
        table.put(RosstandartObjectIdentifiers.id_tc26_gost_3411_12_256, new BcDigestProvider() { // from class: org.spongycastle.operator.bc.BcDefaultDigestProvider.14
            @Override // org.spongycastle.operator.bc.BcDigestProvider
            public ExtendedDigest get(AlgorithmIdentifier digestAlgorithmIdentifier) {
                return new GOST3411_2012_256Digest();
            }
        });
        table.put(RosstandartObjectIdentifiers.id_tc26_gost_3411_12_512, new BcDigestProvider() { // from class: org.spongycastle.operator.bc.BcDefaultDigestProvider.15
            @Override // org.spongycastle.operator.bc.BcDigestProvider
            public ExtendedDigest get(AlgorithmIdentifier digestAlgorithmIdentifier) {
                return new GOST3411_2012_512Digest();
            }
        });
        table.put(TeleTrusTObjectIdentifiers.ripemd128, new BcDigestProvider() { // from class: org.spongycastle.operator.bc.BcDefaultDigestProvider.16
            @Override // org.spongycastle.operator.bc.BcDigestProvider
            public ExtendedDigest get(AlgorithmIdentifier digestAlgorithmIdentifier) {
                return new RIPEMD128Digest();
            }
        });
        table.put(TeleTrusTObjectIdentifiers.ripemd160, new BcDigestProvider() { // from class: org.spongycastle.operator.bc.BcDefaultDigestProvider.17
            @Override // org.spongycastle.operator.bc.BcDigestProvider
            public ExtendedDigest get(AlgorithmIdentifier digestAlgorithmIdentifier) {
                return new RIPEMD160Digest();
            }
        });
        table.put(TeleTrusTObjectIdentifiers.ripemd256, new BcDigestProvider() { // from class: org.spongycastle.operator.bc.BcDefaultDigestProvider.18
            @Override // org.spongycastle.operator.bc.BcDigestProvider
            public ExtendedDigest get(AlgorithmIdentifier digestAlgorithmIdentifier) {
                return new RIPEMD256Digest();
            }
        });
        return Collections.unmodifiableMap(table);
    }

    private BcDefaultDigestProvider() {
    }

    @Override // org.spongycastle.operator.bc.BcDigestProvider
    public ExtendedDigest get(AlgorithmIdentifier digestAlgorithmIdentifier) throws OperatorCreationException {
        BcDigestProvider extProv = (BcDigestProvider) lookup.get(digestAlgorithmIdentifier.getAlgorithm());
        if (extProv == null) {
            throw new OperatorCreationException("cannot recognise digest");
        }
        return extProv.get(digestAlgorithmIdentifier);
    }
}
