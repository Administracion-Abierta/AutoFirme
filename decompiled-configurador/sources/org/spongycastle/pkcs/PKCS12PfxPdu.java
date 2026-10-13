package org.spongycastle.pkcs;

import java.io.IOException;
import org.spongycastle.asn1.ASN1OctetString;
import org.spongycastle.asn1.ASN1Primitive;
import org.spongycastle.asn1.ASN1Sequence;
import org.spongycastle.asn1.pkcs.ContentInfo;
import org.spongycastle.asn1.pkcs.MacData;
import org.spongycastle.asn1.pkcs.PKCS12PBEParams;
import org.spongycastle.asn1.pkcs.Pfx;
import org.spongycastle.asn1.x509.AlgorithmIdentifier;
import org.spongycastle.util.Arrays;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:org/spongycastle/pkcs/PKCS12PfxPdu.class */
public class PKCS12PfxPdu {
    private Pfx pfx;

    private static Pfx parseBytes(byte[] pfxEncoding) throws IOException {
        try {
            return Pfx.getInstance(ASN1Primitive.fromByteArray(pfxEncoding));
        } catch (ClassCastException e) {
            throw new PKCSIOException("malformed data: " + e.getMessage(), e);
        } catch (IllegalArgumentException e2) {
            throw new PKCSIOException("malformed data: " + e2.getMessage(), e2);
        }
    }

    public PKCS12PfxPdu(Pfx pfx) {
        this.pfx = pfx;
    }

    public PKCS12PfxPdu(byte[] pfx) throws IOException {
        this(parseBytes(pfx));
    }

    public ContentInfo[] getContentInfos() {
        ASN1Sequence seq = ASN1Sequence.getInstance(ASN1OctetString.getInstance(this.pfx.getAuthSafe().getContent()).getOctets());
        ContentInfo[] content = new ContentInfo[seq.size()];
        for (int i = 0; i != seq.size(); i++) {
            content[i] = ContentInfo.getInstance(seq.getObjectAt(i));
        }
        return content;
    }

    public boolean hasMac() {
        return this.pfx.getMacData() != null;
    }

    public AlgorithmIdentifier getMacAlgorithmID() {
        MacData md = this.pfx.getMacData();
        if (md != null) {
            return md.getMac().getAlgorithmId();
        }
        return null;
    }

    public boolean isMacValid(PKCS12MacCalculatorBuilderProvider macCalcProviderBuilder, char[] password) throws PKCSException {
        if (hasMac()) {
            MacData pfxmData = this.pfx.getMacData();
            MacDataGenerator mdGen = new MacDataGenerator(macCalcProviderBuilder.get(new AlgorithmIdentifier(pfxmData.getMac().getAlgorithmId().getAlgorithm(), new PKCS12PBEParams(pfxmData.getSalt(), pfxmData.getIterationCount().intValue()))));
            try {
                MacData mData = mdGen.build(password, ASN1OctetString.getInstance(this.pfx.getAuthSafe().getContent()).getOctets());
                return Arrays.constantTimeAreEqual(mData.getEncoded(), this.pfx.getMacData().getEncoded());
            } catch (IOException e) {
                throw new PKCSException("unable to process AuthSafe: " + e.getMessage());
            }
        }
        throw new IllegalStateException("no MAC present on PFX");
    }

    public Pfx toASN1Structure() {
        return this.pfx;
    }

    public byte[] getEncoded() throws IOException {
        return toASN1Structure().getEncoded();
    }

    public byte[] getEncoded(String encoding) throws IOException {
        return toASN1Structure().getEncoded(encoding);
    }
}
