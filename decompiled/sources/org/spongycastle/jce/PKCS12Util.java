package org.spongycastle.jce;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import javax.crypto.Mac;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.PBEParameterSpec;
import org.spongycastle.asn1.ASN1InputStream;
import org.spongycastle.asn1.ASN1ObjectIdentifier;
import org.spongycastle.asn1.ASN1OctetString;
import org.spongycastle.asn1.ASN1Primitive;
import org.spongycastle.asn1.DERNull;
import org.spongycastle.asn1.DEROctetString;
import org.spongycastle.asn1.DEROutputStream;
import org.spongycastle.asn1.pkcs.ContentInfo;
import org.spongycastle.asn1.pkcs.MacData;
import org.spongycastle.asn1.pkcs.Pfx;
import org.spongycastle.asn1.x509.AlgorithmIdentifier;
import org.spongycastle.asn1.x509.DigestInfo;

/* JADX INFO: loaded from: autofirma.jar:org/spongycastle/jce/PKCS12Util.class */
public class PKCS12Util {
    public static byte[] convertToDefiniteLength(byte[] berPKCS12File) throws IOException {
        ByteArrayOutputStream bOut = new ByteArrayOutputStream();
        DEROutputStream dOut = new DEROutputStream(bOut);
        Pfx pfx = Pfx.getInstance(berPKCS12File);
        bOut.reset();
        dOut.writeObject(pfx);
        return bOut.toByteArray();
    }

    public static byte[] convertToDefiniteLength(byte[] berPKCS12File, char[] passwd, String provider) throws IOException {
        Pfx pfx = Pfx.getInstance(berPKCS12File);
        ContentInfo info = pfx.getAuthSafe();
        ASN1OctetString content = ASN1OctetString.getInstance(info.getContent());
        ByteArrayOutputStream bOut = new ByteArrayOutputStream();
        DEROutputStream dOut = new DEROutputStream(bOut);
        ASN1InputStream contentIn = new ASN1InputStream(content.getOctets());
        ASN1Primitive obj = contentIn.readObject();
        dOut.writeObject(obj);
        ContentInfo info2 = new ContentInfo(info.getContentType(), new DEROctetString(bOut.toByteArray()));
        MacData mData = pfx.getMacData();
        try {
            int itCount = mData.getIterationCount().intValue();
            byte[] data = ASN1OctetString.getInstance(info2.getContent()).getOctets();
            byte[] res = calculatePbeMac(mData.getMac().getAlgorithmId().getAlgorithm(), mData.getSalt(), itCount, passwd, data, provider);
            AlgorithmIdentifier algId = new AlgorithmIdentifier(mData.getMac().getAlgorithmId().getAlgorithm(), DERNull.INSTANCE);
            DigestInfo dInfo = new DigestInfo(algId, res);
            Pfx pfx2 = new Pfx(info2, new MacData(dInfo, mData.getSalt(), itCount));
            bOut.reset();
            dOut.writeObject(pfx2);
            return bOut.toByteArray();
        } catch (Exception e) {
            throw new IOException("error constructing MAC: " + e.toString());
        }
    }

    private static byte[] calculatePbeMac(ASN1ObjectIdentifier oid, byte[] salt, int itCount, char[] password, byte[] data, String provider) throws Exception {
        SecretKeyFactory keyFact = SecretKeyFactory.getInstance(oid.getId(), provider);
        PBEParameterSpec defParams = new PBEParameterSpec(salt, itCount);
        PBEKeySpec pbeSpec = new PBEKeySpec(password);
        SecretKey key = keyFact.generateSecret(pbeSpec);
        Mac mac = Mac.getInstance(oid.getId(), provider);
        mac.init(key, defParams);
        mac.update(data);
        return mac.doFinal();
    }
}
