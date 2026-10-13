package org.bouncycastle.jce;

import java.util.Enumeration;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.cryptopro.ECGOST3410NamedCurves;
import org.bouncycastle.asn1.x9.X9ECParameters;
import org.bouncycastle.jce.spec.ECNamedCurveParameterSpec;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/jce/ECGOST3410NamedCurveTable.class */
public class ECGOST3410NamedCurveTable {
    public static ECNamedCurveParameterSpec getParameterSpec(String str) {
        X9ECParameters byNameX9 = ECGOST3410NamedCurves.getByNameX9(str);
        if (byNameX9 == null) {
            try {
                byNameX9 = ECGOST3410NamedCurves.getByOIDX9(new ASN1ObjectIdentifier(str));
            } catch (IllegalArgumentException e) {
                return null;
            }
        }
        if (byNameX9 == null) {
            return null;
        }
        return new ECNamedCurveParameterSpec(str, byNameX9.getCurve(), byNameX9.getG(), byNameX9.getN(), byNameX9.getH(), byNameX9.getSeed());
    }

    public static Enumeration getNames() {
        return ECGOST3410NamedCurves.getNames();
    }
}
