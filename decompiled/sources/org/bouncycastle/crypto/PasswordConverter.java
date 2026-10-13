package org.bouncycastle.crypto;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/crypto/PasswordConverter.class */
public enum PasswordConverter implements CharToByteConverter {
    ASCII { // from class: org.bouncycastle.crypto.PasswordConverter.1
        @Override // org.bouncycastle.crypto.CharToByteConverter
        public String getType() {
            return "ASCII";
        }

        @Override // org.bouncycastle.crypto.CharToByteConverter
        public byte[] convert(char[] cArr) {
            return PBEParametersGenerator.PKCS5PasswordToBytes(cArr);
        }
    },
    UTF8 { // from class: org.bouncycastle.crypto.PasswordConverter.2
        @Override // org.bouncycastle.crypto.CharToByteConverter
        public String getType() {
            return "UTF8";
        }

        @Override // org.bouncycastle.crypto.CharToByteConverter
        public byte[] convert(char[] cArr) {
            return PBEParametersGenerator.PKCS5PasswordToUTF8Bytes(cArr);
        }
    },
    PKCS12 { // from class: org.bouncycastle.crypto.PasswordConverter.3
        @Override // org.bouncycastle.crypto.CharToByteConverter
        public String getType() {
            return "PKCS12";
        }

        @Override // org.bouncycastle.crypto.CharToByteConverter
        public byte[] convert(char[] cArr) {
            return PBEParametersGenerator.PKCS12PasswordToBytes(cArr);
        }
    }
}
