package org.bouncycastle.its;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import org.bouncycastle.its.operator.ECDSAEncoder;
import org.bouncycastle.its.operator.ITSContentVerifierProvider;
import org.bouncycastle.oer.Element;
import org.bouncycastle.oer.OEREncoder;
import org.bouncycastle.oer.OERInputStream;
import org.bouncycastle.oer.its.etsi103097.EtsiTs103097DataSigned;
import org.bouncycastle.oer.its.ieee1609dot2.Ieee1609Dot2Content;
import org.bouncycastle.oer.its.ieee1609dot2.Opaque;
import org.bouncycastle.oer.its.ieee1609dot2.SignedData;
import org.bouncycastle.oer.its.template.etsi103097.EtsiTs103097Module;
import org.bouncycastle.oer.its.template.ieee1609dot2.IEEE1609dot2;
import org.bouncycastle.operator.ContentVerifier;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/its/ETSISignedData.class */
public class ETSISignedData {
    private final SignedData signedData;
    private static final Element oerDef = EtsiTs103097Module.EtsiTs103097Data_Signed.build();

    public ETSISignedData(Opaque opaque) throws IOException {
        this(opaque.getInputStream());
    }

    public ETSISignedData(byte[] bArr) throws IOException {
        this(new ByteArrayInputStream(bArr));
    }

    public ETSISignedData(InputStream inputStream) throws IOException {
        Ieee1609Dot2Content content = EtsiTs103097DataSigned.getInstance((Object) (inputStream instanceof OERInputStream ? (OERInputStream) inputStream : new OERInputStream(inputStream)).parse(oerDef)).getContent();
        if (content.getChoice() != 1) {
            throw new IllegalStateException("EtsiTs103097Data-Signed did not have signed data content");
        }
        this.signedData = SignedData.getInstance(content.getIeee1609Dot2Content());
    }

    public ETSISignedData(EtsiTs103097DataSigned etsiTs103097DataSigned) {
        if (etsiTs103097DataSigned.getContent().getChoice() != 1) {
            throw new IllegalStateException("EtsiTs103097Data-Signed did not have signed data content");
        }
        this.signedData = SignedData.getInstance(etsiTs103097DataSigned.getContent());
    }

    public ETSISignedData(SignedData signedData) {
        this.signedData = signedData;
    }

    public boolean signatureValid(ITSContentVerifierProvider iTSContentVerifierProvider) throws Exception {
        ContentVerifier contentVerifier = iTSContentVerifierProvider.get(this.signedData.getSignature().getChoice());
        OutputStream outputStream = contentVerifier.getOutputStream();
        outputStream.write(OEREncoder.toByteArray(this.signedData.getTbsData(), IEEE1609dot2.ToBeSignedData.build()));
        outputStream.close();
        return contentVerifier.verify(ECDSAEncoder.toX962(this.signedData.getSignature()));
    }

    public byte[] getEncoded() {
        return OEREncoder.toByteArray(new EtsiTs103097DataSigned(Ieee1609Dot2Content.signedData(this.signedData)), EtsiTs103097Module.EtsiTs103097Data_Signed.build());
    }

    public SignedData getSignedData() {
        return this.signedData;
    }
}
