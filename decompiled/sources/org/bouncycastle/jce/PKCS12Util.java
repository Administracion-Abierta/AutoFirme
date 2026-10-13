package org.bouncycastle.jce;

import java.io.IOException;
import javax.crypto.Mac;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.PBEParameterSpec;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.ASN1OctetString;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.DERNull;
import org.bouncycastle.asn1.DEROctetString;
import org.bouncycastle.asn1.pkcs.ContentInfo;
import org.bouncycastle.asn1.pkcs.MacData;
import org.bouncycastle.asn1.pkcs.Pfx;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.asn1.x509.DigestInfo;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/jce/PKCS12Util.class */
public class PKCS12Util {
    public static byte[] convertToDefiniteLength(byte[] bArr) throws IOException {
        return Pfx.getInstance(bArr).getEncoded("DER");
    }

    public static byte[] convertToDefiniteLength(byte[] bArr, char[] cArr, String str) throws IOException {
        Pfx pfx = Pfx.getInstance(bArr);
        ContentInfo authSafe = pfx.getAuthSafe();
        ContentInfo contentInfo = new ContentInfo(authSafe.getContentType(), new DEROctetString(ASN1Primitive.fromByteArray(ASN1OctetString.getInstance(authSafe.getContent()).getOctets()).getEncoded("DER")));
        MacData macData = pfx.getMacData();
        try {
            int iIntValue = macData.getIterationCount().intValue();
            return new Pfx(contentInfo, new MacData(new DigestInfo(new AlgorithmIdentifier(macData.getMac().getAlgorithmId().getAlgorithm(), DERNull.INSTANCE), calculatePbeMac(macData.getMac().getAlgorithmId().getAlgorithm(), macData.getSalt(), iIntValue, cArr, ASN1OctetString.getInstance(contentInfo.getContent()).getOctets(), str)), macData.getSalt(), iIntValue)).getEncoded("DER");
        } catch (Exception e) {
            throw new IOException("error constructing MAC: " + e.toString());
        }
    }

    private static byte[] calculatePbeMac(ASN1ObjectIdentifier aSN1ObjectIdentifier, byte[] bArr, int i, char[] cArr, byte[] bArr2, String str) throws Exception {
        SecretKeyFactory secretKeyFactory = SecretKeyFactory.getInstance(aSN1ObjectIdentifier.getId(), str);
        PBEParameterSpec pBEParameterSpec = new PBEParameterSpec(bArr, i);
        SecretKey secretKeyGenerateSecret = secretKeyFactory.generateSecret(new PBEKeySpec(cArr));
        Mac mac = Mac.getInstance(aSN1ObjectIdentifier.getId(), str);
        mac.init(secretKeyGenerateSecret, pBEParameterSpec);
        mac.update(bArr2);
        return mac.doFinal();
    }
}
