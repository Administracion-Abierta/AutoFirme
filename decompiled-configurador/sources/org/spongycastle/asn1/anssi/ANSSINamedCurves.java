package org.spongycastle.asn1.anssi;

import java.math.BigInteger;
import java.util.Enumeration;
import java.util.Hashtable;
import org.spongycastle.asn1.ASN1ObjectIdentifier;
import org.spongycastle.asn1.x9.X9ECParameters;
import org.spongycastle.asn1.x9.X9ECParametersHolder;
import org.spongycastle.asn1.x9.X9ECPoint;
import org.spongycastle.math.ec.ECCurve;
import org.spongycastle.util.Strings;
import org.spongycastle.util.encoders.Hex;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:org/spongycastle/asn1/anssi/ANSSINamedCurves.class */
public class ANSSINamedCurves {
    static X9ECParametersHolder FRP256v1 = new X9ECParametersHolder() { // from class: org.spongycastle.asn1.anssi.ANSSINamedCurves.1
        @Override // org.spongycastle.asn1.x9.X9ECParametersHolder
        protected X9ECParameters createParameters() {
            BigInteger p = ANSSINamedCurves.fromHex("F1FD178C0B3AD58F10126DE8CE42435B3961ADBCABC8CA6DE8FCF353D86E9C03");
            BigInteger a = ANSSINamedCurves.fromHex("F1FD178C0B3AD58F10126DE8CE42435B3961ADBCABC8CA6DE8FCF353D86E9C00");
            BigInteger b = ANSSINamedCurves.fromHex("EE353FCA5428A9300D4ABA754A44C00FDFEC0C9AE4B1A1803075ED967B7BB73F");
            BigInteger n = ANSSINamedCurves.fromHex("F1FD178C0B3AD58F10126DE8CE42435B53DC67E140D2BF941FFDD459C6D655E1");
            BigInteger h = BigInteger.valueOf(1L);
            ECCurve curve = ANSSINamedCurves.configureCurve(new ECCurve.Fp(p, a, b, n, h));
            X9ECPoint G = new X9ECPoint(curve, Hex.decode("04B6B3D4C356C139EB31183D4749D423958C27D2DCAF98B70164C97A2DD98F5CFF6142E0F7C8B204911F9271F0F3ECEF8C2701C307E8E4C9E183115A1554062CFB"));
            return new X9ECParameters(curve, G, n, h, (byte[]) null);
        }
    };
    static final Hashtable objIds = new Hashtable();
    static final Hashtable curves = new Hashtable();
    static final Hashtable names = new Hashtable();

    /* JADX INFO: Access modifiers changed from: private */
    public static ECCurve configureCurve(ECCurve curve) {
        return curve;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static BigInteger fromHex(String hex) {
        return new BigInteger(1, Hex.decode(hex));
    }

    static {
        defineCurve("FRP256v1", ANSSIObjectIdentifiers.FRP256v1, FRP256v1);
    }

    static void defineCurve(String name, ASN1ObjectIdentifier oid, X9ECParametersHolder holder) {
        objIds.put(Strings.toLowerCase(name), oid);
        names.put(oid, name);
        curves.put(oid, holder);
    }

    public static X9ECParameters getByName(String name) {
        ASN1ObjectIdentifier oid = getOID(name);
        if (oid == null) {
            return null;
        }
        return getByOID(oid);
    }

    public static X9ECParameters getByOID(ASN1ObjectIdentifier oid) {
        X9ECParametersHolder holder = (X9ECParametersHolder) curves.get(oid);
        if (holder == null) {
            return null;
        }
        return holder.getParameters();
    }

    public static ASN1ObjectIdentifier getOID(String name) {
        return (ASN1ObjectIdentifier) objIds.get(Strings.toLowerCase(name));
    }

    public static String getName(ASN1ObjectIdentifier oid) {
        return (String) names.get(oid);
    }

    public static Enumeration getNames() {
        return names.elements();
    }
}
