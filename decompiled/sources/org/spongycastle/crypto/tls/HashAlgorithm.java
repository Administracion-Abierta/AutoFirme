package org.spongycastle.crypto.tls;

import es.gob.afirma.standalone.CommandLineParameters;

/* JADX INFO: loaded from: autofirma.jar:org/spongycastle/crypto/tls/HashAlgorithm.class */
public class HashAlgorithm {
    public static final short none = 0;
    public static final short md5 = 1;
    public static final short sha1 = 2;
    public static final short sha224 = 3;
    public static final short sha256 = 4;
    public static final short sha384 = 5;
    public static final short sha512 = 6;

    public static String getName(short hashAlgorithm) {
        switch (hashAlgorithm) {
            case 0:
                return "none";
            case 1:
                return "md5";
            case 2:
                return CommandLineParameters.ALGO_SHA1;
            case 3:
                return "sha224";
            case 4:
                return CommandLineParameters.ALGO_SHA256;
            case 5:
                return CommandLineParameters.ALGO_SHA384;
            case 6:
                return CommandLineParameters.ALGO_SHA512;
            default:
                return "UNKNOWN";
        }
    }

    public static String getText(short hashAlgorithm) {
        return getName(hashAlgorithm) + "(" + ((int) hashAlgorithm) + ")";
    }

    public static boolean isPrivate(short hashAlgorithm) {
        return 224 <= hashAlgorithm && hashAlgorithm <= 255;
    }
}
