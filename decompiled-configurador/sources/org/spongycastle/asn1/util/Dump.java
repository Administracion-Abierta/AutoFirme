package org.spongycastle.asn1.util;

import java.io.FileInputStream;
import org.spongycastle.asn1.ASN1InputStream;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:org/spongycastle/asn1/util/Dump.class */
public class Dump {
    public static void main(String[] args) throws Exception {
        FileInputStream fIn = new FileInputStream(args[0]);
        ASN1InputStream bIn = new ASN1InputStream(fIn);
        while (true) {
            Object obj = bIn.readObject();
            if (obj != null) {
                System.out.println(ASN1Dump.dumpAsString(obj));
            } else {
                return;
            }
        }
    }
}
