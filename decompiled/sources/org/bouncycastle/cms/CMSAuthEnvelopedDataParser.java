package org.bouncycastle.cms;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.ASN1OctetString;
import org.bouncycastle.asn1.ASN1OctetStringParser;
import org.bouncycastle.asn1.ASN1SequenceParser;
import org.bouncycastle.asn1.ASN1Set;
import org.bouncycastle.asn1.ASN1SetParser;
import org.bouncycastle.asn1.cms.AttributeTable;
import org.bouncycastle.asn1.cms.AuthEnvelopedDataParser;
import org.bouncycastle.asn1.cms.CMSAttributes;
import org.bouncycastle.asn1.cms.EncryptedContentInfoParser;
import org.bouncycastle.asn1.cms.OriginatorInfo;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.util.Arrays;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/cms/CMSAuthEnvelopedDataParser.class */
public class CMSAuthEnvelopedDataParser extends CMSContentInfoParser {
    private final RecipientInformationStore recipientInfoStore;
    private final AuthEnvelopedDataParser authEvnData;
    private final LocalMacProvider localMacProvider;
    private final AlgorithmIdentifier encAlg;
    private AttributeTable authAttrs;
    private ASN1Set authAttrSet;
    private AttributeTable unauthAttrs;
    private boolean authAttrNotRead;
    private boolean unauthAttrNotRead;
    private OriginatorInformation originatorInfo;

    /* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/cms/CMSAuthEnvelopedDataParser$LocalMacProvider.class */
    static class LocalMacProvider implements MACProvider {
        private byte[] mac;
        private final AuthEnvelopedDataParser authEnvData;
        private final CMSAuthEnvelopedDataParser parser;
        private CMSSecureReadableWithAAD readable;

        LocalMacProvider(AuthEnvelopedDataParser authEnvelopedDataParser, CMSAuthEnvelopedDataParser cMSAuthEnvelopedDataParser) {
            this.authEnvData = authEnvelopedDataParser;
            this.parser = cMSAuthEnvelopedDataParser;
        }

        @Override // org.bouncycastle.cms.MACProvider
        public void init() throws IOException {
            this.parser.authAttrs = this.parser.getAuthAttrs();
            if (this.parser.authAttrs != null) {
                this.readable.setAuthAttrSet(this.parser.authAttrSet);
                this.readable.getAADStream().write(this.parser.authAttrs.toASN1Structure().getEncoded("DER"));
            }
            this.mac = this.authEnvData.getMac().getOctets();
        }

        void setSecureReadable(CMSSecureReadableWithAAD cMSSecureReadableWithAAD) {
            this.readable = cMSSecureReadableWithAAD;
        }

        @Override // org.bouncycastle.cms.MACProvider
        public byte[] getMAC() {
            return this.mac;
        }
    }

    public CMSAuthEnvelopedDataParser(byte[] bArr) throws CMSException, IOException {
        this(new ByteArrayInputStream(bArr));
    }

    public CMSAuthEnvelopedDataParser(InputStream inputStream) throws CMSException, IOException {
        super(inputStream);
        this.authAttrNotRead = true;
        this.unauthAttrNotRead = true;
        this.authEvnData = new AuthEnvelopedDataParser((ASN1SequenceParser) this._contentInfo.getContent(16));
        OriginatorInfo originatorInfo = this.authEvnData.getOriginatorInfo();
        if (originatorInfo != null) {
            this.originatorInfo = new OriginatorInformation(originatorInfo);
        }
        ASN1Set aSN1Set = ASN1Set.getInstance(this.authEvnData.getRecipientInfos().toASN1Primitive());
        final EncryptedContentInfoParser authEncryptedContentInfo = this.authEvnData.getAuthEncryptedContentInfo();
        this.encAlg = authEncryptedContentInfo.getContentEncryptionAlgorithm();
        this.localMacProvider = new LocalMacProvider(this.authEvnData, this);
        final CMSProcessableInputStream cMSProcessableInputStream = new CMSProcessableInputStream(new InputStreamWithMAC(((ASN1OctetStringParser) authEncryptedContentInfo.getEncryptedContent(4)).getOctetStream(), this.localMacProvider));
        CMSSecureReadableWithAAD cMSSecureReadableWithAAD = new CMSSecureReadableWithAAD() { // from class: org.bouncycastle.cms.CMSAuthEnvelopedDataParser.1
            private OutputStream aadStream;

            @Override // org.bouncycastle.cms.CMSSecureReadable
            public ASN1ObjectIdentifier getContentType() {
                return authEncryptedContentInfo.getContentType();
            }

            @Override // org.bouncycastle.cms.CMSSecureReadable
            public InputStream getInputStream() throws CMSException, IOException {
                return cMSProcessableInputStream.getInputStream();
            }

            @Override // org.bouncycastle.cms.CMSSecureReadable
            public ASN1Set getAuthAttrSet() {
                return CMSAuthEnvelopedDataParser.this.authAttrSet;
            }

            @Override // org.bouncycastle.cms.CMSSecureReadable
            public void setAuthAttrSet(ASN1Set aSN1Set2) {
            }

            @Override // org.bouncycastle.cms.CMSSecureReadable
            public boolean hasAdditionalData() {
                return true;
            }

            @Override // org.bouncycastle.cms.CMSSecureReadableWithAAD
            public void setAADStream(OutputStream outputStream) {
                this.aadStream = outputStream;
            }

            @Override // org.bouncycastle.cms.CMSSecureReadableWithAAD
            public OutputStream getAADStream() {
                return this.aadStream;
            }

            @Override // org.bouncycastle.cms.CMSSecureReadableWithAAD
            public byte[] getMAC() {
                return Arrays.clone(CMSAuthEnvelopedDataParser.this.localMacProvider.getMAC());
            }
        };
        this.localMacProvider.setSecureReadable(cMSSecureReadableWithAAD);
        this.recipientInfoStore = CMSEnvelopedHelper.buildRecipientInformationStore(aSN1Set, this.encAlg, cMSSecureReadableWithAAD);
    }

    public OriginatorInformation getOriginatorInfo() {
        return this.originatorInfo;
    }

    public AlgorithmIdentifier getEncryptionAlgOID() {
        return this.encAlg;
    }

    public String getEncAlgOID() {
        return this.encAlg.getAlgorithm().toString();
    }

    public byte[] getEncAlgParams() {
        try {
            return CMSUtils.encodeObj(this.encAlg.getParameters());
        } catch (Exception e) {
            throw new RuntimeException("exception getting encryption parameters " + e);
        }
    }

    public RecipientInformationStore getRecipientInfos() {
        return this.recipientInfoStore;
    }

    public byte[] getMac() throws IOException {
        return Arrays.clone(this.localMacProvider.getMAC());
    }

    private ASN1Set getAuthAttrSet() throws IOException {
        if (this.authAttrs == null && this.authAttrNotRead) {
            ASN1SetParser authAttrs = this.authEvnData.getAuthAttrs();
            if (authAttrs != null) {
                this.authAttrSet = (ASN1Set) authAttrs.toASN1Primitive();
            }
            this.authAttrNotRead = false;
        }
        return this.authAttrSet;
    }

    public AttributeTable getAuthAttrs() throws IOException {
        ASN1Set authAttrSet;
        if (this.authAttrs == null && this.authAttrNotRead && (authAttrSet = getAuthAttrSet()) != null) {
            this.authAttrs = new AttributeTable(authAttrSet);
        }
        return this.authAttrs;
    }

    public AttributeTable getUnauthAttrs() throws IOException {
        if (this.unauthAttrs == null && this.unauthAttrNotRead) {
            this.unauthAttrNotRead = false;
            this.unauthAttrs = CMSUtils.getAttributesTable(this.authEvnData.getUnauthAttrs());
        }
        return this.unauthAttrs;
    }

    public byte[] getContentDigest() {
        if (this.authAttrs != null) {
            return ASN1OctetString.getInstance(this.authAttrs.get(CMSAttributes.messageDigest).getAttrValues().getObjectAt(0)).getOctets();
        }
        return null;
    }
}
