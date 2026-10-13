package com.aowagie.text.pdf.interfaces;

import com.aowagie.text.DocumentException;
import java.security.cert.Certificate;

/* JADX INFO: loaded from: autofirma.jar:com/aowagie/text/pdf/interfaces/PdfEncryptionSettings.class */
public interface PdfEncryptionSettings {
    void setEncryption(byte[] bArr, byte[] bArr2, int i, int i2) throws DocumentException;

    void setEncryption(Certificate[] certificateArr, int[] iArr, int i) throws DocumentException;
}
