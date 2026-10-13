package org.spongycastle.crypto;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:org/spongycastle/crypto/PasswordConverter.class */
public enum PasswordConverter implements CharToByteConverter {
    ASCII { // from class: org.spongycastle.crypto.PasswordConverter.1
        @Override // org.spongycastle.crypto.CharToByteConverter
        public String getType() {
            return "ASCII";
        }

        @Override // org.spongycastle.crypto.CharToByteConverter
        public byte[] convert(char[] password) {
            return PBEParametersGenerator.PKCS5PasswordToBytes(password);
        }
    },
    UTF8 { // from class: org.spongycastle.crypto.PasswordConverter.2
        @Override // org.spongycastle.crypto.CharToByteConverter
        public String getType() {
            return "UTF8";
        }

        @Override // org.spongycastle.crypto.CharToByteConverter
        public byte[] convert(char[] password) {
            return PBEParametersGenerator.PKCS5PasswordToUTF8Bytes(password);
        }
    },
    PKCS12 { // from class: org.spongycastle.crypto.PasswordConverter.3
        @Override // org.spongycastle.crypto.CharToByteConverter
        public String getType() {
            return "PKCS12";
        }

        @Override // org.spongycastle.crypto.CharToByteConverter
        public byte[] convert(char[] password) {
            return PBEParametersGenerator.PKCS12PasswordToBytes(password);
        }
    }
}
