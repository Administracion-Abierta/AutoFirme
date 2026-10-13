package com.aowagie.text.pdf;

/* JADX INFO: loaded from: autofirma.jar:com/aowagie/text/pdf/ExtraEncoding.class */
interface ExtraEncoding {
    byte[] charToByte(String str, String str2);

    byte[] charToByte(char c, String str);

    String byteToChar(byte[] bArr, String str);
}
