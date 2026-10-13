package es.gob.afirma.core.ciphers;

/* JADX INFO: loaded from: autofirma.jar:es/gob/afirma/core/ciphers/CipherConstants.class */
public final class CipherConstants {
    private CipherConstants() {
    }

    /* JADX INFO: loaded from: autofirma.jar:es/gob/afirma/core/ciphers/CipherConstants$AOCipherAlgorithm.class */
    public enum AOCipherAlgorithm {
        AES("AES", "Advanced Encryption Standard (AES)", false, true, "2.16.840.1.101.3.4.1"),
        BLOWFISH("Blowfish", "Blowfish", false, true, "1.3.6.1.4.1.3029.1.1.1"),
        DES("DES", "Data Encryption Standard (DES)", false, true, "1.2.840.113549.3.6"),
        TRIPLEDES("DESede", "Triple DES (3DES)", false, true, "1.2.840.113549.3.7"),
        PBEWITHMD5ANDDES("PBEWithMD5AndDES", "Contraseña con MD5 y DES", true, false, "1.2.840.113549.1.5.3"),
        PBEWITHSHA1ANDDESEDE("PBEWithSHA1AndDESede", "Contraseña con SHA1 y 3DES", true, false, "1.2.840.113549.1.5.10"),
        PBEWITHSHA1ANDRC2_40("PBEWithSHA1AndRC2_40", "Contraseña con SHA1 y RC2", true, false, "1.2.840.113549.1.5.11"),
        HMACMD5("HmacMD5", "HmacMD5", false, true, "1.3.6.1.5.5.8.1.1"),
        HMACSHA1("HmacSHA1", "HmacSHA1", false, true, "1.2.840.113549.2.7"),
        HMACSHA256("HmacSHA256", "HmacSHA256", false, true, "1.2.840.113549.2.9"),
        HMACSHA384("HmacSHA384", "HmacSHA384", false, true, "1.2.840.113549.2.10"),
        HMACSHA512("HmacSHA512", "HmacSHA512", false, true, "1.2.840.113549.2.11");

        private String name;
        private String description;
        private boolean password;
        private boolean key;
        private String oid;
        private static final AOCipherAlgorithm DEFAULT_CIPHER_ALGO = AES;

        public static AOCipherAlgorithm getDefault() {
            return DEFAULT_CIPHER_ALGO;
        }

        AOCipherAlgorithm(String str, String str2, boolean z, boolean z2, String str3) {
            this.name = str;
            this.description = str2;
            this.password = z;
            this.key = z2;
            this.oid = str3;
        }

        public boolean supportsPassword() {
            return this.password;
        }

        public boolean supportsKey() {
            return this.key;
        }

        public String getName() {
            return this.name;
        }

        @Override // java.lang.Enum
        public String toString() {
            return this.description;
        }

        public String getOid() {
            return this.oid;
        }

        public static AOCipherAlgorithm getValueOf(String str) {
            for (AOCipherAlgorithm aOCipherAlgorithm : values()) {
                if (aOCipherAlgorithm.getName().equalsIgnoreCase(str)) {
                    return aOCipherAlgorithm;
                }
            }
            return null;
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:es/gob/afirma/core/ciphers/CipherConstants$AOCipherPadding.class */
    public enum AOCipherPadding {
        PKCS5PADDING("PKCS5PADDING", "Relleno PKCS#5"),
        ISO10126PADDING("ISO10126PADDING", "Relleno ISO-10126"),
        NOPADDING("NOPADDING", "Sin Relleno");

        private String name;
        private String description;

        AOCipherPadding(String str, String str2) {
            this.name = str;
            this.description = str2;
        }

        public String getName() {
            return this.name;
        }

        @Override // java.lang.Enum
        public String toString() {
            return this.description;
        }

        public static AOCipherPadding getValueOf(String str) {
            for (AOCipherPadding aOCipherPadding : values()) {
                if (aOCipherPadding.getName().equals(str)) {
                    return aOCipherPadding;
                }
            }
            return null;
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:es/gob/afirma/core/ciphers/CipherConstants$AOCipherBlockMode.class */
    public enum AOCipherBlockMode {
        ECB("ECB", "Electronic CodeBook (ECB)"),
        CBC("CBC", "Cipher-Block Chaining (CBC)"),
        PCBC("PCBC", "Propagating Cipher-Block Chaining (PCBC)"),
        CTR("CTR", "Counter (CTR)"),
        CFB("CFB", "Cipher FeedBack (CFB)"),
        OFB("OFB", "Output FeedBack (OFB)");

        private String name;
        private String description;

        AOCipherBlockMode(String str, String str2) {
            this.name = str;
            this.description = str2;
        }

        public String getName() {
            return this.name;
        }

        @Override // java.lang.Enum
        public String toString() {
            return this.description;
        }

        public static AOCipherBlockMode getValueOf(String str) {
            for (AOCipherBlockMode aOCipherBlockMode : values()) {
                if (aOCipherBlockMode.getName().equals(str)) {
                    return aOCipherBlockMode;
                }
            }
            return null;
        }
    }
}
