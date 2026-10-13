package org.bouncycastle.asn1.x9;

import java.util.Enumeration;
import java.util.Vector;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.anssi.ANSSINamedCurves;
import org.bouncycastle.asn1.cryptopro.ECGOST3410NamedCurves;
import org.bouncycastle.asn1.gm.GMNamedCurves;
import org.bouncycastle.asn1.nist.NISTNamedCurves;
import org.bouncycastle.asn1.sec.SECNamedCurves;
import org.bouncycastle.asn1.teletrust.TeleTrusTNamedCurves;
import org.bouncycastle.crypto.ec.CustomNamedCurves;
import org.bouncycastle.internal.asn1.cryptlib.CryptlibObjectIdentifiers;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/asn1/x9/ECNamedCurveTable.class */
public class ECNamedCurveTable {
    public static X9ECParameters getByName(String str) {
        X9ECParameters byName = X962NamedCurves.getByName(str);
        if (byName == null) {
            byName = SECNamedCurves.getByName(str);
        }
        if (byName == null) {
            byName = NISTNamedCurves.getByName(str);
        }
        if (byName == null) {
            byName = TeleTrusTNamedCurves.getByName(str);
        }
        if (byName == null) {
            byName = ANSSINamedCurves.getByName(str);
        }
        if (byName == null) {
            byName = ECGOST3410NamedCurves.getByNameX9(str);
        }
        if (byName == null) {
            byName = GMNamedCurves.getByName(str);
        }
        return byName;
    }

    public static X9ECParametersHolder getByNameLazy(String str) {
        X9ECParametersHolder byNameLazy = X962NamedCurves.getByNameLazy(str);
        if (null == byNameLazy) {
            byNameLazy = SECNamedCurves.getByNameLazy(str);
        }
        if (null == byNameLazy) {
            byNameLazy = NISTNamedCurves.getByNameLazy(str);
        }
        if (null == byNameLazy) {
            byNameLazy = TeleTrusTNamedCurves.getByNameLazy(str);
        }
        if (null == byNameLazy) {
            byNameLazy = ANSSINamedCurves.getByNameLazy(str);
        }
        if (null == byNameLazy) {
            byNameLazy = ECGOST3410NamedCurves.getByNameLazy(str);
        }
        if (null == byNameLazy) {
            byNameLazy = GMNamedCurves.getByNameLazy(str);
        }
        return byNameLazy;
    }

    public static ASN1ObjectIdentifier getOID(String str) {
        ASN1ObjectIdentifier oid = X962NamedCurves.getOID(str);
        if (oid == null) {
            oid = SECNamedCurves.getOID(str);
        }
        if (oid == null) {
            oid = NISTNamedCurves.getOID(str);
        }
        if (oid == null) {
            oid = TeleTrusTNamedCurves.getOID(str);
        }
        if (oid == null) {
            oid = ANSSINamedCurves.getOID(str);
        }
        if (oid == null) {
            oid = ECGOST3410NamedCurves.getOID(str);
        }
        if (oid == null) {
            oid = GMNamedCurves.getOID(str);
        }
        if (oid == null && str.equals("curve25519")) {
            oid = CryptlibObjectIdentifiers.curvey25519;
        }
        return oid;
    }

    public static String getName(ASN1ObjectIdentifier aSN1ObjectIdentifier) {
        String name = X962NamedCurves.getName(aSN1ObjectIdentifier);
        if (name == null) {
            name = SECNamedCurves.getName(aSN1ObjectIdentifier);
        }
        if (name == null) {
            name = NISTNamedCurves.getName(aSN1ObjectIdentifier);
        }
        if (name == null) {
            name = TeleTrusTNamedCurves.getName(aSN1ObjectIdentifier);
        }
        if (name == null) {
            name = ANSSINamedCurves.getName(aSN1ObjectIdentifier);
        }
        if (name == null) {
            name = ECGOST3410NamedCurves.getName(aSN1ObjectIdentifier);
        }
        if (name == null) {
            name = GMNamedCurves.getName(aSN1ObjectIdentifier);
        }
        if (name == null) {
            name = CustomNamedCurves.getName(aSN1ObjectIdentifier);
        }
        return name;
    }

    public static X9ECParameters getByOID(ASN1ObjectIdentifier aSN1ObjectIdentifier) {
        X9ECParameters byOID = X962NamedCurves.getByOID(aSN1ObjectIdentifier);
        if (byOID == null) {
            byOID = SECNamedCurves.getByOID(aSN1ObjectIdentifier);
        }
        if (byOID == null) {
            byOID = TeleTrusTNamedCurves.getByOID(aSN1ObjectIdentifier);
        }
        if (byOID == null) {
            byOID = ANSSINamedCurves.getByOID(aSN1ObjectIdentifier);
        }
        if (byOID == null) {
            byOID = ECGOST3410NamedCurves.getByOIDX9(aSN1ObjectIdentifier);
        }
        if (byOID == null) {
            byOID = GMNamedCurves.getByOID(aSN1ObjectIdentifier);
        }
        return byOID;
    }

    public static X9ECParametersHolder getByOIDLazy(ASN1ObjectIdentifier aSN1ObjectIdentifier) {
        X9ECParametersHolder byOIDLazy = X962NamedCurves.getByOIDLazy(aSN1ObjectIdentifier);
        if (null == byOIDLazy) {
            byOIDLazy = SECNamedCurves.getByOIDLazy(aSN1ObjectIdentifier);
        }
        if (null == byOIDLazy) {
            byOIDLazy = TeleTrusTNamedCurves.getByOIDLazy(aSN1ObjectIdentifier);
        }
        if (null == byOIDLazy) {
            byOIDLazy = ANSSINamedCurves.getByOIDLazy(aSN1ObjectIdentifier);
        }
        if (null == byOIDLazy) {
            byOIDLazy = ECGOST3410NamedCurves.getByOIDLazy(aSN1ObjectIdentifier);
        }
        if (null == byOIDLazy) {
            byOIDLazy = GMNamedCurves.getByOIDLazy(aSN1ObjectIdentifier);
        }
        return byOIDLazy;
    }

    public static Enumeration getNames() {
        Vector vector = new Vector();
        addEnumeration(vector, X962NamedCurves.getNames());
        addEnumeration(vector, SECNamedCurves.getNames());
        addEnumeration(vector, NISTNamedCurves.getNames());
        addEnumeration(vector, TeleTrusTNamedCurves.getNames());
        addEnumeration(vector, ANSSINamedCurves.getNames());
        addEnumeration(vector, ECGOST3410NamedCurves.getNames());
        addEnumeration(vector, GMNamedCurves.getNames());
        return vector.elements();
    }

    private static void addEnumeration(Vector vector, Enumeration enumeration) {
        while (enumeration.hasMoreElements()) {
            vector.addElement(enumeration.nextElement());
        }
    }
}
