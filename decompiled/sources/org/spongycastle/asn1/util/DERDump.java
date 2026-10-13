package org.spongycastle.asn1.util;

import org.spongycastle.asn1.ASN1Encodable;
import org.spongycastle.asn1.ASN1Primitive;

/* JADX INFO: loaded from: autofirma.jar:org/spongycastle/asn1/util/DERDump.class */
public class DERDump extends ASN1Dump {
    public static String dumpAsString(ASN1Primitive obj) {
        StringBuffer buf = new StringBuffer();
        _dumpAsString("", false, obj, buf);
        return buf.toString();
    }

    public static String dumpAsString(ASN1Encodable obj) {
        StringBuffer buf = new StringBuffer();
        _dumpAsString("", false, obj.toASN1Primitive(), buf);
        return buf.toString();
    }
}
