package org.bouncycastle.its;

import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.bouncycastle.its.operator.ECDSAEncoder;
import org.bouncycastle.its.operator.ITSContentSigner;
import org.bouncycastle.oer.Element;
import org.bouncycastle.oer.OEREncoder;
import org.bouncycastle.oer.its.ieee1609dot2.Certificate;
import org.bouncycastle.oer.its.ieee1609dot2.HashedData;
import org.bouncycastle.oer.its.ieee1609dot2.HeaderInfo;
import org.bouncycastle.oer.its.ieee1609dot2.Ieee1609Dot2Content;
import org.bouncycastle.oer.its.ieee1609dot2.Ieee1609Dot2Data;
import org.bouncycastle.oer.its.ieee1609dot2.Opaque;
import org.bouncycastle.oer.its.ieee1609dot2.SequenceOfCertificate;
import org.bouncycastle.oer.its.ieee1609dot2.SignedData;
import org.bouncycastle.oer.its.ieee1609dot2.SignedDataPayload;
import org.bouncycastle.oer.its.ieee1609dot2.SignerIdentifier;
import org.bouncycastle.oer.its.ieee1609dot2.ToBeSignedData;
import org.bouncycastle.oer.its.ieee1609dot2.basetypes.HashedId8;
import org.bouncycastle.oer.its.ieee1609dot2.basetypes.Psid;
import org.bouncycastle.oer.its.ieee1609dot2.basetypes.Time64;
import org.bouncycastle.oer.its.ieee1609dot2.basetypes.UINT8;
import org.bouncycastle.oer.its.template.ieee1609dot2.IEEE1609dot2;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/its/ETSISignedDataBuilder.class */
public class ETSISignedDataBuilder {
    private static final Element def = IEEE1609dot2.ToBeSignedData.build();
    private final HeaderInfo headerInfo;
    private Ieee1609Dot2Data data;
    private HashedData extDataHash;

    private ETSISignedDataBuilder(Psid psid) {
        this(HeaderInfo.builder().setPsid(psid).setGenerationTime(Time64.now()).createHeaderInfo());
    }

    private ETSISignedDataBuilder(HeaderInfo headerInfo) {
        this.headerInfo = headerInfo;
    }

    public static ETSISignedDataBuilder builder(Psid psid) {
        return new ETSISignedDataBuilder(psid);
    }

    public static ETSISignedDataBuilder builder(HeaderInfo headerInfo) {
        return new ETSISignedDataBuilder(headerInfo);
    }

    public ETSISignedDataBuilder setData(Ieee1609Dot2Content ieee1609Dot2Content) {
        this.data = Ieee1609Dot2Data.builder().setProtocolVersion(new UINT8(3)).setContent(ieee1609Dot2Content).createIeee1609Dot2Data();
        return this;
    }

    public ETSISignedDataBuilder setUnsecuredData(byte[] bArr) {
        this.data = Ieee1609Dot2Data.builder().setProtocolVersion(new UINT8(3)).setContent(Ieee1609Dot2Content.unsecuredData(new Opaque(bArr))).createEtsiTs103097Data();
        return this;
    }

    public ETSISignedDataBuilder setExtDataHash(HashedData hashedData) {
        this.extDataHash = hashedData;
        return this;
    }

    private ToBeSignedData getToBeSignedData() {
        return ToBeSignedData.builder().setPayload(new SignedDataPayload(this.data, this.extDataHash)).setHeaderInfo(this.headerInfo).createToBeSignedData();
    }

    public ETSISignedData build(ITSContentSigner iTSContentSigner) {
        ToBeSignedData toBeSignedData = getToBeSignedData();
        write(iTSContentSigner.getOutputStream(), OEREncoder.toByteArray(toBeSignedData, def));
        return new ETSISignedData(SignedData.builder().setHashId(ITSAlgorithmUtils.getHashAlgorithm(iTSContentSigner.getDigestAlgorithm().getAlgorithm())).setTbsData(toBeSignedData).setSigner(SignerIdentifier.self()).setSignature(ECDSAEncoder.toITS(iTSContentSigner.getCurveID(), iTSContentSigner.getSignature())).createSignedData());
    }

    public ETSISignedData build(ITSContentSigner iTSContentSigner, List<ITSCertificate> list) {
        ToBeSignedData toBeSignedData = getToBeSignedData();
        write(iTSContentSigner.getOutputStream(), OEREncoder.toByteArray(toBeSignedData, def));
        ArrayList arrayList = new ArrayList();
        Iterator<ITSCertificate> it = list.iterator();
        while (it.hasNext()) {
            arrayList.add(Certificate.getInstance((Object) it.next().toASN1Structure()));
        }
        return new ETSISignedData(SignedData.builder().setHashId(ITSAlgorithmUtils.getHashAlgorithm(iTSContentSigner.getDigestAlgorithm().getAlgorithm())).setTbsData(toBeSignedData).setSigner(SignerIdentifier.certificate(new SequenceOfCertificate(arrayList))).setSignature(ECDSAEncoder.toITS(iTSContentSigner.getCurveID(), iTSContentSigner.getSignature())).createSignedData());
    }

    public ETSISignedData build(ITSContentSigner iTSContentSigner, HashedId8 hashedId8) {
        ToBeSignedData toBeSignedData = getToBeSignedData();
        write(iTSContentSigner.getOutputStream(), OEREncoder.toByteArray(toBeSignedData, def));
        return new ETSISignedData(SignedData.builder().setHashId(ITSAlgorithmUtils.getHashAlgorithm(iTSContentSigner.getDigestAlgorithm().getAlgorithm())).setTbsData(toBeSignedData).setSigner(SignerIdentifier.digest(hashedId8)).setSignature(ECDSAEncoder.toITS(iTSContentSigner.getCurveID(), iTSContentSigner.getSignature())).createSignedData());
    }

    private static void write(OutputStream outputStream, byte[] bArr) {
        try {
            outputStream.write(bArr);
            outputStream.flush();
            outputStream.close();
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }
}
