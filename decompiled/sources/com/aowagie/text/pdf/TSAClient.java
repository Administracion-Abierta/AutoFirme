package com.aowagie.text.pdf;

/* JADX INFO: loaded from: autofirma.jar:com/aowagie/text/pdf/TSAClient.class */
interface TSAClient {
    int getTokenSizeEstimate();

    byte[] getTimeStampToken(PdfPKCS7 pdfPKCS7, byte[] bArr) throws Exception;
}
