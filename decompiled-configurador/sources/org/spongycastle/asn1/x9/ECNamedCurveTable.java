package org.spongycastle.asn1.x9;

import java.util.Enumeration;
import java.util.Vector;
import org.spongycastle.asn1.ASN1ObjectIdentifier;
import org.spongycastle.asn1.anssi.ANSSINamedCurves;
import org.spongycastle.asn1.cryptopro.ECGOST3410NamedCurves;
import org.spongycastle.asn1.gm.GMNamedCurves;
import org.spongycastle.asn1.nist.NISTNamedCurves;
import org.spongycastle.asn1.sec.SECNamedCurves;
import org.spongycastle.asn1.teletrust.TeleTrusTNamedCurves;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:org/spongycastle/asn1/x9/ECNamedCurveTable.class */
public class ECNamedCurveTable {
    public static X9ECParameters getByName(String name) {
        X9ECParameters ecP = X962NamedCurves.getByName(name);
        if (ecP == null) {
            ecP = SECNamedCurves.getByName(name);
        }
        if (ecP == null) {
            ecP = NISTNamedCurves.getByName(name);
        }
        if (ecP == null) {
            ecP = TeleTrusTNamedCurves.getByName(name);
        }
        if (ecP == null) {
            ecP = ANSSINamedCurves.getByName(name);
        }
        if (ecP == null) {
            ecP = GMNamedCurves.getByName(name);
        }
        return ecP;
    }

    public static ASN1ObjectIdentifier getOID(String name) {
        ASN1ObjectIdentifier oid = X962NamedCurves.getOID(name);
        if (oid == null) {
            oid = SECNamedCurves.getOID(name);
        }
        if (oid == null) {
            oid = NISTNamedCurves.getOID(name);
        }
        if (oid == null) {
            oid = TeleTrusTNamedCurves.getOID(name);
        }
        if (oid == null) {
            oid = ANSSINamedCurves.getOID(name);
        }
        if (oid == null) {
            oid = GMNamedCurves.getOID(name);
        }
        return oid;
    }

    public static String getName(ASN1ObjectIdentifier oid) {
        String name = NISTNamedCurves.getName(oid);
        if (name == null) {
            name = SECNamedCurves.getName(oid);
        }
        if (name == null) {
            name = TeleTrusTNamedCurves.getName(oid);
        }
        if (name == null) {
            name = X962NamedCurves.getName(oid);
        }
        if (name == null) {
            name = ECGOST3410NamedCurves.getName(oid);
        }
        if (name == null) {
            name = GMNamedCurves.getName(oid);
        }
        return name;
    }

    public static X9ECParameters getByOID(ASN1ObjectIdentifier oid) {
        X9ECParameters ecP = X962NamedCurves.getByOID(oid);
        if (ecP == null) {
            ecP = SECNamedCurves.getByOID(oid);
        }
        if (ecP == null) {
            ecP = TeleTrusTNamedCurves.getByOID(oid);
        }
        if (ecP == null) {
            ecP = ANSSINamedCurves.getByOID(oid);
        }
        if (ecP == null) {
            ecP = GMNamedCurves.getByOID(oid);
        }
        return ecP;
    }

    public static Enumeration getNames() {
        Vector v = new Vector();
        addEnumeration(v, X962NamedCurves.getNames());
        addEnumeration(v, SECNamedCurves.getNames());
        addEnumeration(v, NISTNamedCurves.getNames());
        addEnumeration(v, TeleTrusTNamedCurves.getNames());
        addEnumeration(v, ANSSINamedCurves.getNames());
        addEnumeration(v, GMNamedCurves.getNames());
        return v.elements();
    }

    private static void addEnumeration(Vector v, Enumeration e) {
        while (e.hasMoreElements()) {
            v.addElement(e.nextElement());
        }
    }
}
