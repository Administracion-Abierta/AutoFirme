package org.spongycastle.asn1.cryptopro;

import es.gob.afirma.signers.tsp.pkcs7.TsaParams;
import java.math.BigInteger;
import java.util.Enumeration;
import java.util.Hashtable;
import org.spongycastle.asn1.ASN1ObjectIdentifier;
import org.spongycastle.asn1.rosstandart.RosstandartObjectIdentifiers;
import org.spongycastle.crypto.params.ECDomainParameters;
import org.spongycastle.math.ec.ECConstants;
import org.spongycastle.math.ec.ECCurve;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:org/spongycastle/asn1/cryptopro/ECGOST3410NamedCurves.class */
public class ECGOST3410NamedCurves {
    static final Hashtable objIds = new Hashtable();
    static final Hashtable params = new Hashtable();
    static final Hashtable names = new Hashtable();

    static {
        BigInteger mod_p = new BigInteger("115792089237316195423570985008687907853269984665640564039457584007913129639319");
        BigInteger mod_q = new BigInteger("115792089237316195423570985008687907853073762908499243225378155805079068850323");
        ECCurve.Fp curve = new ECCurve.Fp(mod_p, new BigInteger("115792089237316195423570985008687907853269984665640564039457584007913129639316"), new BigInteger("166"), mod_q, ECConstants.ONE);
        ECDomainParameters ecParams = new ECDomainParameters(curve, curve.createPoint(new BigInteger(TsaParams.TS_SIGN), new BigInteger("64033881142927202683649881450433473985931760268884941288852745803908878638612")), mod_q);
        params.put(CryptoProObjectIdentifiers.gostR3410_2001_CryptoPro_A, ecParams);
        BigInteger mod_p2 = new BigInteger("115792089237316195423570985008687907853269984665640564039457584007913129639319");
        BigInteger mod_q2 = new BigInteger("115792089237316195423570985008687907853073762908499243225378155805079068850323");
        ECCurve.Fp curve2 = new ECCurve.Fp(mod_p2, new BigInteger("115792089237316195423570985008687907853269984665640564039457584007913129639316"), new BigInteger("166"), mod_q2, ECConstants.ONE);
        ECDomainParameters ecParams2 = new ECDomainParameters(curve2, curve2.createPoint(new BigInteger(TsaParams.TS_SIGN), new BigInteger("64033881142927202683649881450433473985931760268884941288852745803908878638612")), mod_q2);
        params.put(CryptoProObjectIdentifiers.gostR3410_2001_CryptoPro_XchA, ecParams2);
        BigInteger mod_p3 = new BigInteger("57896044618658097711785492504343953926634992332820282019728792003956564823193");
        BigInteger mod_q3 = new BigInteger("57896044618658097711785492504343953927102133160255826820068844496087732066703");
        ECCurve.Fp curve3 = new ECCurve.Fp(mod_p3, new BigInteger("57896044618658097711785492504343953926634992332820282019728792003956564823190"), new BigInteger("28091019353058090096996979000309560759124368558014865957655842872397301267595"), mod_q3, ECConstants.ONE);
        ECDomainParameters ecParams3 = new ECDomainParameters(curve3, curve3.createPoint(new BigInteger(TsaParams.TS_SIGN), new BigInteger("28792665814854611296992347458380284135028636778229113005756334730996303888124")), mod_q3);
        params.put(CryptoProObjectIdentifiers.gostR3410_2001_CryptoPro_B, ecParams3);
        BigInteger mod_p4 = new BigInteger("70390085352083305199547718019018437841079516630045180471284346843705633502619");
        BigInteger mod_q4 = new BigInteger("70390085352083305199547718019018437840920882647164081035322601458352298396601");
        ECCurve.Fp curve4 = new ECCurve.Fp(mod_p4, new BigInteger("70390085352083305199547718019018437841079516630045180471284346843705633502616"), new BigInteger("32858"), mod_q4, ECConstants.ONE);
        ECDomainParameters ecParams4 = new ECDomainParameters(curve4, curve4.createPoint(new BigInteger("0"), new BigInteger("29818893917731240733471273240314769927240550812383695689146495261604565990247")), mod_q4);
        params.put(CryptoProObjectIdentifiers.gostR3410_2001_CryptoPro_XchB, ecParams4);
        BigInteger mod_p5 = new BigInteger("70390085352083305199547718019018437841079516630045180471284346843705633502619");
        BigInteger mod_q5 = new BigInteger("70390085352083305199547718019018437840920882647164081035322601458352298396601");
        ECCurve.Fp curve5 = new ECCurve.Fp(mod_p5, new BigInteger("70390085352083305199547718019018437841079516630045180471284346843705633502616"), new BigInteger("32858"), mod_q5, ECConstants.ONE);
        ECDomainParameters ecParams5 = new ECDomainParameters(curve5, curve5.createPoint(new BigInteger("0"), new BigInteger("29818893917731240733471273240314769927240550812383695689146495261604565990247")), mod_q5);
        params.put(CryptoProObjectIdentifiers.gostR3410_2001_CryptoPro_C, ecParams5);
        BigInteger mod_p6 = new BigInteger("115792089237316195423570985008687907853269984665640564039457584007913129639319");
        BigInteger mod_q6 = new BigInteger("115792089237316195423570985008687907853073762908499243225378155805079068850323");
        ECCurve.Fp curve6 = new ECCurve.Fp(mod_p6, new BigInteger("115792089237316195423570985008687907853269984665640564039457584007913129639316"), new BigInteger("166"), mod_q6, ECConstants.ONE);
        ECDomainParameters ecParams6 = new ECDomainParameters(curve6, curve6.createPoint(new BigInteger(TsaParams.TS_SIGN), new BigInteger("64033881142927202683649881450433473985931760268884941288852745803908878638612")), mod_q6);
        params.put(RosstandartObjectIdentifiers.id_tc26_gost_3410_12_256_paramSetA, ecParams6);
        BigInteger mod_p7 = new BigInteger("FFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFDC7", 16);
        BigInteger mod_q7 = new BigInteger("FFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFF27E69532F48D89116FF22B8D4E0560609B4B38ABFAD2B85DCACDB1411F10B275", 16);
        ECCurve.Fp curve7 = new ECCurve.Fp(mod_p7, new BigInteger("FFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFDC4", 16), new BigInteger("E8C2505DEDFC86DDC1BD0B2B6667F1DA34B82574761CB0E879BD081CFD0B6265EE3CB090F30D27614CB4574010DA90DD862EF9D4EBEE4761503190785A71C760", 16), mod_q7, ECConstants.ONE);
        ECDomainParameters ecParams7 = new ECDomainParameters(curve7, curve7.createPoint(new BigInteger("00000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000003"), new BigInteger("7503CFE87A836AE3A61B8816E25450E6CE5E1C93ACF1ABC1778064FDCBEFA921DF1626BE4FD036E93D75E6A50E3A41E98028FE5FC235F5B889A589CB5215F2A4", 16)), mod_q7);
        params.put(RosstandartObjectIdentifiers.id_tc26_gost_3410_12_512_paramSetA, ecParams7);
        BigInteger mod_p8 = new BigInteger("8000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000006F", 16);
        BigInteger mod_q8 = new BigInteger("800000000000000000000000000000000000000000000000000000000000000149A1EC142565A545ACFDB77BD9D40CFA8B996712101BEA0EC6346C54374F25BD", 16);
        ECCurve.Fp curve8 = new ECCurve.Fp(mod_p8, new BigInteger("8000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000006C", 16), new BigInteger("687D1B459DC841457E3E06CF6F5E2517B97C7D614AF138BCBF85DC806C4B289F3E965D2DB1416D217F8B276FAD1AB69C50F78BEE1FA3106EFB8CCBC7C5140116", 16), mod_q8, ECConstants.ONE);
        ECDomainParameters ecParams8 = new ECDomainParameters(curve8, curve8.createPoint(new BigInteger("00000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000002"), new BigInteger("1A8F7EDA389B094C2C071E3647A8940F3C123B697578C213BE6DD9E6C8EC7335DCB228FD1EDF4A39152CBCAAF8C0398828041055F94CEEEC7E21340780FE41BD", 16)), mod_q8);
        params.put(RosstandartObjectIdentifiers.id_tc26_gost_3410_12_512_paramSetB, ecParams8);
        BigInteger mod_p9 = new BigInteger("FFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFDC7", 16);
        BigInteger mod_q9 = new BigInteger("3FFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFC98CDBA46506AB004C33A9FF5147502CC8EDA9E7A769A12694623CEF47F023ED", 16);
        ECCurve.Fp curve9 = new ECCurve.Fp(mod_p9, new BigInteger("DC9203E514A721875485A529D2C722FB187BC8980EB866644DE41C68E143064546E861C0E2C9EDD92ADE71F46FCF50FF2AD97F951FDA9F2A2EB6546F39689BD3", 16), new BigInteger("B4C4EE28CEBC6C2C8AC12952CF37F16AC7EFB6A9F69F4B57FFDA2E4F0DE5ADE038CBC2FFF719D2C18DE0284B8BFEF3B52B8CC7A5F5BF0A3C8D2319A5312557E1", 16), mod_q9, ECConstants.ONE);
        ECDomainParameters ecParams9 = new ECDomainParameters(curve9, curve9.createPoint(new BigInteger("E2E31EDFC23DE7BDEBE241CE593EF5DE2295B7A9CBAEF021D385F7074CEA043AA27272A7AE602BF2A7B9033DB9ED3610C6FB85487EAE97AAC5BC7928C1950148", 16), new BigInteger("F5CE40D95B5EB899ABBCCFF5911CB8577939804D6527378B8C108C3D2090FF9BE18E2D33E3021ED2EF32D85822423B6304F726AA854BAE07D0396E9A9ADDC40F", 16)), mod_q9);
        params.put(RosstandartObjectIdentifiers.id_tc26_gost_3410_12_512_paramSetC, ecParams9);
        objIds.put("GostR3410-2001-CryptoPro-A", CryptoProObjectIdentifiers.gostR3410_2001_CryptoPro_A);
        objIds.put("GostR3410-2001-CryptoPro-B", CryptoProObjectIdentifiers.gostR3410_2001_CryptoPro_B);
        objIds.put("GostR3410-2001-CryptoPro-C", CryptoProObjectIdentifiers.gostR3410_2001_CryptoPro_C);
        objIds.put("GostR3410-2001-CryptoPro-XchA", CryptoProObjectIdentifiers.gostR3410_2001_CryptoPro_XchA);
        objIds.put("GostR3410-2001-CryptoPro-XchB", CryptoProObjectIdentifiers.gostR3410_2001_CryptoPro_XchB);
        objIds.put("Tc26-Gost-3410-12-256-paramSetA", RosstandartObjectIdentifiers.id_tc26_gost_3410_12_256_paramSetA);
        objIds.put("Tc26-Gost-3410-12-512-paramSetA", RosstandartObjectIdentifiers.id_tc26_gost_3410_12_512_paramSetA);
        objIds.put("Tc26-Gost-3410-12-512-paramSetB", RosstandartObjectIdentifiers.id_tc26_gost_3410_12_512_paramSetB);
        objIds.put("Tc26-Gost-3410-12-512-paramSetC", RosstandartObjectIdentifiers.id_tc26_gost_3410_12_512_paramSetC);
        names.put(CryptoProObjectIdentifiers.gostR3410_2001_CryptoPro_A, "GostR3410-2001-CryptoPro-A");
        names.put(CryptoProObjectIdentifiers.gostR3410_2001_CryptoPro_B, "GostR3410-2001-CryptoPro-B");
        names.put(CryptoProObjectIdentifiers.gostR3410_2001_CryptoPro_C, "GostR3410-2001-CryptoPro-C");
        names.put(CryptoProObjectIdentifiers.gostR3410_2001_CryptoPro_XchA, "GostR3410-2001-CryptoPro-XchA");
        names.put(CryptoProObjectIdentifiers.gostR3410_2001_CryptoPro_XchB, "GostR3410-2001-CryptoPro-XchB");
        names.put(RosstandartObjectIdentifiers.id_tc26_gost_3410_12_256_paramSetA, "Tc26-Gost-3410-12-256-paramSetA");
        names.put(RosstandartObjectIdentifiers.id_tc26_gost_3410_12_512_paramSetA, "Tc26-Gost-3410-12-512-paramSetA");
        names.put(RosstandartObjectIdentifiers.id_tc26_gost_3410_12_512_paramSetB, "Tc26-Gost-3410-12-512-paramSetB");
        names.put(RosstandartObjectIdentifiers.id_tc26_gost_3410_12_512_paramSetC, "Tc26-Gost-3410-12-512-paramSetC");
    }

    public static ECDomainParameters getByOID(ASN1ObjectIdentifier oid) {
        return (ECDomainParameters) params.get(oid);
    }

    public static Enumeration getNames() {
        return names.elements();
    }

    public static ECDomainParameters getByName(String name) {
        ASN1ObjectIdentifier oid = (ASN1ObjectIdentifier) objIds.get(name);
        if (oid != null) {
            return (ECDomainParameters) params.get(oid);
        }
        return null;
    }

    public static String getName(ASN1ObjectIdentifier oid) {
        return (String) names.get(oid);
    }

    public static ASN1ObjectIdentifier getOID(String name) {
        return (ASN1ObjectIdentifier) objIds.get(name);
    }
}
