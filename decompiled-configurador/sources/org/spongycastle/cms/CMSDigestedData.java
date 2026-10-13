package org.spongycastle.cms;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import org.spongycastle.asn1.ASN1ObjectIdentifier;
import org.spongycastle.asn1.ASN1OctetString;
import org.spongycastle.asn1.cms.ContentInfo;
import org.spongycastle.asn1.cms.DigestedData;
import org.spongycastle.asn1.x509.AlgorithmIdentifier;
import org.spongycastle.operator.DigestCalculator;
import org.spongycastle.operator.DigestCalculatorProvider;
import org.spongycastle.operator.OperatorCreationException;
import org.spongycastle.util.Arrays;
import org.spongycastle.util.Encodable;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:org/spongycastle/cms/CMSDigestedData.class */
public class CMSDigestedData implements Encodable {
    private ContentInfo contentInfo;
    private DigestedData digestedData;

    public CMSDigestedData(byte[] compressedData) throws CMSException {
        this(CMSUtils.readContentInfo(compressedData));
    }

    public CMSDigestedData(InputStream compressedData) throws CMSException {
        this(CMSUtils.readContentInfo(compressedData));
    }

    public CMSDigestedData(ContentInfo contentInfo) throws CMSException {
        this.contentInfo = contentInfo;
        try {
            this.digestedData = DigestedData.getInstance(contentInfo.getContent());
        } catch (ClassCastException e) {
            throw new CMSException("Malformed content.", e);
        } catch (IllegalArgumentException e2) {
            throw new CMSException("Malformed content.", e2);
        }
    }

    public ASN1ObjectIdentifier getContentType() {
        return this.contentInfo.getContentType();
    }

    public AlgorithmIdentifier getDigestAlgorithm() {
        return this.digestedData.getDigestAlgorithm();
    }

    public CMSProcessable getDigestedContent() throws CMSException {
        ContentInfo content = this.digestedData.getEncapContentInfo();
        try {
            return new CMSProcessableByteArray(content.getContentType(), ((ASN1OctetString) content.getContent()).getOctets());
        } catch (Exception e) {
            throw new CMSException("exception reading digested stream.", e);
        }
    }

    public ContentInfo toASN1Structure() {
        return this.contentInfo;
    }

    @Override // org.spongycastle.util.Encodable
    public byte[] getEncoded() throws IOException {
        return this.contentInfo.getEncoded();
    }

    public boolean verify(DigestCalculatorProvider calculatorProvider) throws CMSException {
        try {
            ContentInfo content = this.digestedData.getEncapContentInfo();
            DigestCalculator calc = calculatorProvider.get(this.digestedData.getDigestAlgorithm());
            OutputStream dOut = calc.getOutputStream();
            dOut.write(((ASN1OctetString) content.getContent()).getOctets());
            return Arrays.areEqual(this.digestedData.getDigest(), calc.getDigest());
        } catch (IOException e) {
            throw new CMSException("unable process content: " + e.getMessage(), e);
        } catch (OperatorCreationException e2) {
            throw new CMSException("unable to create digest calculator: " + e2.getMessage(), e2);
        }
    }
}
