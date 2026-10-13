package org.bouncycastle.jce;

import java.util.Enumeration;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.x9.X9ECParameters;
import org.bouncycastle.crypto.ec.CustomNamedCurves;
import org.bouncycastle.jce.spec.ECNamedCurveParameterSpec;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/jce/ECNamedCurveTable.class */
public class ECNamedCurveTable {
    public static ECNamedCurveParameterSpec getParameterSpec(String str) {
        ASN1ObjectIdentifier aSN1ObjectIdentifier;
        try {
            aSN1ObjectIdentifier = possibleOID(str) ? new ASN1ObjectIdentifier(str) : null;
        } catch (IllegalArgumentException e) {
            aSN1ObjectIdentifier = null;
        }
        X9ECParameters byOID = aSN1ObjectIdentifier != null ? CustomNamedCurves.getByOID(aSN1ObjectIdentifier) : CustomNamedCurves.getByName(str);
        if (byOID == null) {
            byOID = aSN1ObjectIdentifier != null ? org.bouncycastle.asn1.x9.ECNamedCurveTable.getByOID(aSN1ObjectIdentifier) : org.bouncycastle.asn1.x9.ECNamedCurveTable.getByName(str);
        }
        if (byOID == null) {
            return null;
        }
        return new ECNamedCurveParameterSpec(str, byOID.getCurve(), byOID.getG(), byOID.getN(), byOID.getH(), byOID.getSeed());
    }

    public static Enumeration getNames() {
        return org.bouncycastle.asn1.x9.ECNamedCurveTable.getNames();
    }

    private static boolean possibleOID(String str) {
        char cCharAt;
        return str.length() >= 3 && str.charAt(1) == '.' && (cCharAt = str.charAt(0)) >= '0' && cCharAt <= '2';
    }
}
