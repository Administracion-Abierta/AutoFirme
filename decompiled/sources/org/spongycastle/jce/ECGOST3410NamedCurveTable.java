package org.spongycastle.jce;

import java.util.Enumeration;
import org.spongycastle.asn1.ASN1ObjectIdentifier;
import org.spongycastle.asn1.cryptopro.ECGOST3410NamedCurves;
import org.spongycastle.crypto.params.ECDomainParameters;
import org.spongycastle.jce.spec.ECNamedCurveParameterSpec;

/* JADX INFO: loaded from: autofirma.jar:org/spongycastle/jce/ECGOST3410NamedCurveTable.class */
public class ECGOST3410NamedCurveTable {
    public static ECNamedCurveParameterSpec getParameterSpec(String name) {
        ECDomainParameters ecP = ECGOST3410NamedCurves.getByName(name);
        if (ecP == null) {
            try {
                ecP = ECGOST3410NamedCurves.getByOID(new ASN1ObjectIdentifier(name));
            } catch (IllegalArgumentException e) {
                return null;
            }
        }
        if (ecP == null) {
            return null;
        }
        return new ECNamedCurveParameterSpec(name, ecP.getCurve(), ecP.getG(), ecP.getN(), ecP.getH(), ecP.getSeed());
    }

    public static Enumeration getNames() {
        return ECGOST3410NamedCurves.getNames();
    }
}
