package org.bouncycastle.operator.bc;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import org.bouncycastle.asn1.ASN1Integer;
import org.bouncycastle.asn1.cryptopro.CryptoProObjectIdentifiers;
import org.bouncycastle.asn1.gm.GMObjectIdentifiers;
import org.bouncycastle.asn1.misc.MiscObjectIdentifiers;
import org.bouncycastle.asn1.nist.NISTObjectIdentifiers;
import org.bouncycastle.asn1.oiw.OIWObjectIdentifiers;
import org.bouncycastle.asn1.pkcs.PKCSObjectIdentifiers;
import org.bouncycastle.asn1.rosstandart.RosstandartObjectIdentifiers;
import org.bouncycastle.asn1.teletrust.TeleTrusTObjectIdentifiers;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.crypto.ExtendedDigest;
import org.bouncycastle.crypto.Xof;
import org.bouncycastle.crypto.digests.Blake3Digest;
import org.bouncycastle.crypto.digests.GOST3411Digest;
import org.bouncycastle.crypto.digests.GOST3411_2012_256Digest;
import org.bouncycastle.crypto.digests.GOST3411_2012_512Digest;
import org.bouncycastle.crypto.digests.MD2Digest;
import org.bouncycastle.crypto.digests.MD4Digest;
import org.bouncycastle.crypto.digests.MD5Digest;
import org.bouncycastle.crypto.digests.RIPEMD128Digest;
import org.bouncycastle.crypto.digests.RIPEMD160Digest;
import org.bouncycastle.crypto.digests.RIPEMD256Digest;
import org.bouncycastle.crypto.digests.SHA1Digest;
import org.bouncycastle.crypto.digests.SHA224Digest;
import org.bouncycastle.crypto.digests.SHA256Digest;
import org.bouncycastle.crypto.digests.SHA384Digest;
import org.bouncycastle.crypto.digests.SHA3Digest;
import org.bouncycastle.crypto.digests.SHA512Digest;
import org.bouncycastle.crypto.digests.SHAKEDigest;
import org.bouncycastle.crypto.digests.SM3Digest;
import org.bouncycastle.operator.OperatorCreationException;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/operator/bc/BcDefaultDigestProvider.class */
public class BcDefaultDigestProvider implements BcDigestProvider {
    private static final Map lookup = createTable();
    public static final BcDigestProvider INSTANCE = new BcDefaultDigestProvider();

    /* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/operator/bc/BcDefaultDigestProvider$AdjustedXof.class */
    private static class AdjustedXof implements Xof {
        private final Xof xof;
        private final int length;

        AdjustedXof(Xof xof, int i) {
            this.xof = xof;
            this.length = i;
        }

        @Override // org.bouncycastle.crypto.Digest
        public String getAlgorithmName() {
            return this.xof.getAlgorithmName() + "-" + this.length;
        }

        @Override // org.bouncycastle.crypto.Digest
        public int getDigestSize() {
            return (this.length + 7) / 8;
        }

        @Override // org.bouncycastle.crypto.Digest
        public void update(byte b) {
            this.xof.update(b);
        }

        @Override // org.bouncycastle.crypto.Digest
        public void update(byte[] bArr, int i, int i2) {
            this.xof.update(bArr, i, i2);
        }

        @Override // org.bouncycastle.crypto.Digest
        public int doFinal(byte[] bArr, int i) {
            return doFinal(bArr, i, getDigestSize());
        }

        @Override // org.bouncycastle.crypto.Digest
        public void reset() {
            this.xof.reset();
        }

        @Override // org.bouncycastle.crypto.ExtendedDigest
        public int getByteLength() {
            return this.xof.getByteLength();
        }

        @Override // org.bouncycastle.crypto.Xof
        public int doFinal(byte[] bArr, int i, int i2) {
            return this.xof.doFinal(bArr, i, i2);
        }

        @Override // org.bouncycastle.crypto.Xof
        public int doOutput(byte[] bArr, int i, int i2) {
            return this.xof.doOutput(bArr, i, i2);
        }
    }

    private static Map createTable() {
        HashMap map = new HashMap();
        map.put(OIWObjectIdentifiers.idSHA1, new BcDigestProvider() { // from class: org.bouncycastle.operator.bc.BcDefaultDigestProvider.1
            @Override // org.bouncycastle.operator.bc.BcDigestProvider
            public ExtendedDigest get(AlgorithmIdentifier algorithmIdentifier) {
                return new SHA1Digest();
            }
        });
        map.put(NISTObjectIdentifiers.id_sha224, new BcDigestProvider() { // from class: org.bouncycastle.operator.bc.BcDefaultDigestProvider.2
            @Override // org.bouncycastle.operator.bc.BcDigestProvider
            public ExtendedDigest get(AlgorithmIdentifier algorithmIdentifier) {
                return new SHA224Digest();
            }
        });
        map.put(NISTObjectIdentifiers.id_sha256, new BcDigestProvider() { // from class: org.bouncycastle.operator.bc.BcDefaultDigestProvider.3
            @Override // org.bouncycastle.operator.bc.BcDigestProvider
            public ExtendedDigest get(AlgorithmIdentifier algorithmIdentifier) {
                return new SHA256Digest();
            }
        });
        map.put(NISTObjectIdentifiers.id_sha384, new BcDigestProvider() { // from class: org.bouncycastle.operator.bc.BcDefaultDigestProvider.4
            @Override // org.bouncycastle.operator.bc.BcDigestProvider
            public ExtendedDigest get(AlgorithmIdentifier algorithmIdentifier) {
                return new SHA384Digest();
            }
        });
        map.put(NISTObjectIdentifiers.id_sha512, new BcDigestProvider() { // from class: org.bouncycastle.operator.bc.BcDefaultDigestProvider.5
            @Override // org.bouncycastle.operator.bc.BcDigestProvider
            public ExtendedDigest get(AlgorithmIdentifier algorithmIdentifier) {
                return new SHA512Digest();
            }
        });
        map.put(NISTObjectIdentifiers.id_sha3_224, new BcDigestProvider() { // from class: org.bouncycastle.operator.bc.BcDefaultDigestProvider.6
            @Override // org.bouncycastle.operator.bc.BcDigestProvider
            public ExtendedDigest get(AlgorithmIdentifier algorithmIdentifier) {
                return new SHA3Digest(224);
            }
        });
        map.put(NISTObjectIdentifiers.id_sha3_256, new BcDigestProvider() { // from class: org.bouncycastle.operator.bc.BcDefaultDigestProvider.7
            @Override // org.bouncycastle.operator.bc.BcDigestProvider
            public ExtendedDigest get(AlgorithmIdentifier algorithmIdentifier) {
                return new SHA3Digest(256);
            }
        });
        map.put(NISTObjectIdentifiers.id_sha3_384, new BcDigestProvider() { // from class: org.bouncycastle.operator.bc.BcDefaultDigestProvider.8
            @Override // org.bouncycastle.operator.bc.BcDigestProvider
            public ExtendedDigest get(AlgorithmIdentifier algorithmIdentifier) {
                return new SHA3Digest(384);
            }
        });
        map.put(NISTObjectIdentifiers.id_sha3_512, new BcDigestProvider() { // from class: org.bouncycastle.operator.bc.BcDefaultDigestProvider.9
            @Override // org.bouncycastle.operator.bc.BcDigestProvider
            public ExtendedDigest get(AlgorithmIdentifier algorithmIdentifier) {
                return new SHA3Digest(512);
            }
        });
        map.put(NISTObjectIdentifiers.id_shake128, new BcDigestProvider() { // from class: org.bouncycastle.operator.bc.BcDefaultDigestProvider.10
            @Override // org.bouncycastle.operator.bc.BcDigestProvider
            public ExtendedDigest get(AlgorithmIdentifier algorithmIdentifier) {
                return new SHAKEDigest(128);
            }
        });
        map.put(NISTObjectIdentifiers.id_shake256, new BcDigestProvider() { // from class: org.bouncycastle.operator.bc.BcDefaultDigestProvider.11
            @Override // org.bouncycastle.operator.bc.BcDigestProvider
            public ExtendedDigest get(AlgorithmIdentifier algorithmIdentifier) {
                return new SHAKEDigest(256);
            }
        });
        map.put(NISTObjectIdentifiers.id_shake128_len, new BcDigestProvider() { // from class: org.bouncycastle.operator.bc.BcDefaultDigestProvider.12
            @Override // org.bouncycastle.operator.bc.BcDigestProvider
            public ExtendedDigest get(AlgorithmIdentifier algorithmIdentifier) {
                return new AdjustedXof(new SHAKEDigest(128), ASN1Integer.getInstance(algorithmIdentifier.getParameters()).intValueExact());
            }
        });
        map.put(NISTObjectIdentifiers.id_shake256_len, new BcDigestProvider() { // from class: org.bouncycastle.operator.bc.BcDefaultDigestProvider.13
            @Override // org.bouncycastle.operator.bc.BcDigestProvider
            public ExtendedDigest get(AlgorithmIdentifier algorithmIdentifier) {
                return new AdjustedXof(new SHAKEDigest(256), ASN1Integer.getInstance(algorithmIdentifier.getParameters()).intValueExact());
            }
        });
        map.put(PKCSObjectIdentifiers.md5, new BcDigestProvider() { // from class: org.bouncycastle.operator.bc.BcDefaultDigestProvider.14
            @Override // org.bouncycastle.operator.bc.BcDigestProvider
            public ExtendedDigest get(AlgorithmIdentifier algorithmIdentifier) {
                return new MD5Digest();
            }
        });
        map.put(PKCSObjectIdentifiers.md4, new BcDigestProvider() { // from class: org.bouncycastle.operator.bc.BcDefaultDigestProvider.15
            @Override // org.bouncycastle.operator.bc.BcDigestProvider
            public ExtendedDigest get(AlgorithmIdentifier algorithmIdentifier) {
                return new MD4Digest();
            }
        });
        map.put(PKCSObjectIdentifiers.md2, new BcDigestProvider() { // from class: org.bouncycastle.operator.bc.BcDefaultDigestProvider.16
            @Override // org.bouncycastle.operator.bc.BcDigestProvider
            public ExtendedDigest get(AlgorithmIdentifier algorithmIdentifier) {
                return new MD2Digest();
            }
        });
        map.put(CryptoProObjectIdentifiers.gostR3411, new BcDigestProvider() { // from class: org.bouncycastle.operator.bc.BcDefaultDigestProvider.17
            @Override // org.bouncycastle.operator.bc.BcDigestProvider
            public ExtendedDigest get(AlgorithmIdentifier algorithmIdentifier) {
                return new GOST3411Digest();
            }
        });
        map.put(RosstandartObjectIdentifiers.id_tc26_gost_3411_12_256, new BcDigestProvider() { // from class: org.bouncycastle.operator.bc.BcDefaultDigestProvider.18
            @Override // org.bouncycastle.operator.bc.BcDigestProvider
            public ExtendedDigest get(AlgorithmIdentifier algorithmIdentifier) {
                return new GOST3411_2012_256Digest();
            }
        });
        map.put(RosstandartObjectIdentifiers.id_tc26_gost_3411_12_512, new BcDigestProvider() { // from class: org.bouncycastle.operator.bc.BcDefaultDigestProvider.19
            @Override // org.bouncycastle.operator.bc.BcDigestProvider
            public ExtendedDigest get(AlgorithmIdentifier algorithmIdentifier) {
                return new GOST3411_2012_512Digest();
            }
        });
        map.put(TeleTrusTObjectIdentifiers.ripemd128, new BcDigestProvider() { // from class: org.bouncycastle.operator.bc.BcDefaultDigestProvider.20
            @Override // org.bouncycastle.operator.bc.BcDigestProvider
            public ExtendedDigest get(AlgorithmIdentifier algorithmIdentifier) {
                return new RIPEMD128Digest();
            }
        });
        map.put(TeleTrusTObjectIdentifiers.ripemd160, new BcDigestProvider() { // from class: org.bouncycastle.operator.bc.BcDefaultDigestProvider.21
            @Override // org.bouncycastle.operator.bc.BcDigestProvider
            public ExtendedDigest get(AlgorithmIdentifier algorithmIdentifier) {
                return new RIPEMD160Digest();
            }
        });
        map.put(TeleTrusTObjectIdentifiers.ripemd256, new BcDigestProvider() { // from class: org.bouncycastle.operator.bc.BcDefaultDigestProvider.22
            @Override // org.bouncycastle.operator.bc.BcDigestProvider
            public ExtendedDigest get(AlgorithmIdentifier algorithmIdentifier) {
                return new RIPEMD256Digest();
            }
        });
        map.put(GMObjectIdentifiers.sm3, new BcDigestProvider() { // from class: org.bouncycastle.operator.bc.BcDefaultDigestProvider.23
            @Override // org.bouncycastle.operator.bc.BcDigestProvider
            public ExtendedDigest get(AlgorithmIdentifier algorithmIdentifier) {
                return new SM3Digest();
            }
        });
        map.put(MiscObjectIdentifiers.blake3_256, new BcDigestProvider() { // from class: org.bouncycastle.operator.bc.BcDefaultDigestProvider.24
            @Override // org.bouncycastle.operator.bc.BcDigestProvider
            public ExtendedDigest get(AlgorithmIdentifier algorithmIdentifier) {
                return new Blake3Digest(256);
            }
        });
        return Collections.unmodifiableMap(map);
    }

    private BcDefaultDigestProvider() {
    }

    @Override // org.bouncycastle.operator.bc.BcDigestProvider
    public ExtendedDigest get(AlgorithmIdentifier algorithmIdentifier) throws OperatorCreationException {
        BcDigestProvider bcDigestProvider = (BcDigestProvider) lookup.get(algorithmIdentifier.getAlgorithm());
        if (bcDigestProvider == null) {
            throw new OperatorCreationException("cannot recognise digest");
        }
        return bcDigestProvider.get(algorithmIdentifier);
    }
}
