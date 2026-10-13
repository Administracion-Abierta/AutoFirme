package org.bouncycastle.pkcs;

import java.io.IOException;
import org.bouncycastle.asn1.ASN1OctetString;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.pkcs.ContentInfo;
import org.bouncycastle.asn1.pkcs.MacData;
import org.bouncycastle.asn1.pkcs.PBMAC1Params;
import org.bouncycastle.asn1.pkcs.PKCS12PBEParams;
import org.bouncycastle.asn1.pkcs.PKCSObjectIdentifiers;
import org.bouncycastle.asn1.pkcs.Pfx;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.util.Arrays;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/pkcs/PKCS12PfxPdu.class */
public class PKCS12PfxPdu {
    private Pfx pfx;

    private static Pfx parseBytes(byte[] bArr) throws IOException {
        try {
            return Pfx.getInstance(ASN1Primitive.fromByteArray(bArr));
        } catch (ClassCastException e) {
            throw new PKCSIOException("malformed data: " + e.getMessage(), e);
        } catch (IllegalArgumentException e2) {
            throw new PKCSIOException("malformed data: " + e2.getMessage(), e2);
        }
    }

    public PKCS12PfxPdu(Pfx pfx) {
        this.pfx = pfx;
    }

    public PKCS12PfxPdu(byte[] bArr) throws IOException {
        this(parseBytes(bArr));
    }

    public ContentInfo[] getContentInfos() {
        ASN1Sequence aSN1Sequence = ASN1Sequence.getInstance(ASN1OctetString.getInstance(this.pfx.getAuthSafe().getContent()).getOctets());
        ContentInfo[] contentInfoArr = new ContentInfo[aSN1Sequence.size()];
        for (int i = 0; i != aSN1Sequence.size(); i++) {
            contentInfoArr[i] = ContentInfo.getInstance(aSN1Sequence.getObjectAt(i));
        }
        return contentInfoArr;
    }

    public boolean hasMac() {
        return this.pfx.getMacData() != null;
    }

    public AlgorithmIdentifier getMacAlgorithmID() {
        MacData macData = this.pfx.getMacData();
        if (macData != null) {
            return macData.getMac().getAlgorithmId();
        }
        return null;
    }

    public boolean isMacValid(PKCS12MacCalculatorBuilderProvider pKCS12MacCalculatorBuilderProvider, char[] cArr) throws PKCSException {
        MacDataGenerator macDataGenerator;
        if (!hasMac()) {
            throw new IllegalStateException("no MAC present on PFX");
        }
        MacData macData = this.pfx.getMacData();
        if (PKCSObjectIdentifiers.id_PBMAC1.equals((ASN1Primitive) macData.getMac().getAlgorithmId().getAlgorithm())) {
            PBMAC1Params pBMAC1Params = PBMAC1Params.getInstance(macData.getMac().getAlgorithmId().getParameters());
            if (pBMAC1Params == null) {
                throw new PKCSException("If the DigestAlgorithmIdentifier is id-PBMAC1, then the parameters field must contain valid PBMAC1-params parameters.");
            }
            macDataGenerator = new MacDataGenerator(pKCS12MacCalculatorBuilderProvider.get(new AlgorithmIdentifier(macData.getMac().getAlgorithmId().getAlgorithm(), pBMAC1Params)));
        } else {
            macDataGenerator = new MacDataGenerator(pKCS12MacCalculatorBuilderProvider.get(new AlgorithmIdentifier(macData.getMac().getAlgorithmId().getAlgorithm(), new PKCS12PBEParams(macData.getSalt(), macData.getIterationCount().intValue()))));
        }
        try {
            return Arrays.constantTimeAreEqual(macDataGenerator.build(cArr, ASN1OctetString.getInstance(this.pfx.getAuthSafe().getContent()).getOctets()).getEncoded(), this.pfx.getMacData().getEncoded());
        } catch (IOException e) {
            throw new PKCSException("unable to process AuthSafe: " + e.getMessage());
        }
    }

    public Pfx toASN1Structure() {
        return this.pfx;
    }

    public byte[] getEncoded() throws IOException {
        return toASN1Structure().getEncoded();
    }

    public byte[] getEncoded(String str) throws IOException {
        return toASN1Structure().getEncoded(str);
    }
}
