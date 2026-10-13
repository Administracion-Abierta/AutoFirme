package com.aowagie.text.pdf.crypto;

import org.slf4j.Marker;

/* JADX INFO: loaded from: autofirma.jar:com/aowagie/text/pdf/crypto/IVGenerator.class */
public final class IVGenerator {
    private static ARCFOUREncryption arcfour = new ARCFOUREncryption();

    static {
        long time = System.currentTimeMillis();
        long mem = Runtime.getRuntime().freeMemory();
        String s = time + Marker.ANY_NON_NULL_MARKER + mem;
        arcfour.prepareARCFOURKey(s.getBytes());
    }

    private IVGenerator() {
    }

    public static byte[] getIV() {
        return getIV(16);
    }

    public static byte[] getIV(int len) {
        byte[] b = new byte[len];
        synchronized (arcfour) {
            arcfour.encryptARCFOUR(b);
        }
        return b;
    }
}
