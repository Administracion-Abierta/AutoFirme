package org.spongycastle.asn1.cms;

import java.io.IOException;
import org.spongycastle.asn1.ASN1Encodable;
import org.spongycastle.asn1.ASN1Integer;
import org.spongycastle.asn1.ASN1SequenceParser;
import org.spongycastle.asn1.ASN1SetParser;
import org.spongycastle.asn1.ASN1TaggedObjectParser;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:org/spongycastle/asn1/cms/EnvelopedDataParser.class */
public class EnvelopedDataParser {
    private ASN1SequenceParser _seq;
    private ASN1Integer _version;
    private ASN1Encodable _nextObject;
    private boolean _originatorInfoCalled;

    public EnvelopedDataParser(ASN1SequenceParser seq) throws IOException {
        this._seq = seq;
        this._version = ASN1Integer.getInstance(seq.readObject());
    }

    public ASN1Integer getVersion() {
        return this._version;
    }

    public OriginatorInfo getOriginatorInfo() throws IOException {
        this._originatorInfoCalled = true;
        if (this._nextObject == null) {
            this._nextObject = this._seq.readObject();
        }
        if ((this._nextObject instanceof ASN1TaggedObjectParser) && ((ASN1TaggedObjectParser) this._nextObject).getTagNo() == 0) {
            ASN1SequenceParser originatorInfo = (ASN1SequenceParser) ((ASN1TaggedObjectParser) this._nextObject).getObjectParser(16, false);
            this._nextObject = null;
            return OriginatorInfo.getInstance(originatorInfo.toASN1Primitive());
        }
        return null;
    }

    public ASN1SetParser getRecipientInfos() throws IOException {
        if (!this._originatorInfoCalled) {
            getOriginatorInfo();
        }
        if (this._nextObject == null) {
            this._nextObject = this._seq.readObject();
        }
        ASN1SetParser recipientInfos = (ASN1SetParser) this._nextObject;
        this._nextObject = null;
        return recipientInfos;
    }

    public EncryptedContentInfoParser getEncryptedContentInfo() throws IOException {
        if (this._nextObject == null) {
            this._nextObject = this._seq.readObject();
        }
        if (this._nextObject != null) {
            ASN1SequenceParser o = (ASN1SequenceParser) this._nextObject;
            this._nextObject = null;
            return new EncryptedContentInfoParser(o);
        }
        return null;
    }

    public ASN1SetParser getUnprotectedAttrs() throws IOException {
        if (this._nextObject == null) {
            this._nextObject = this._seq.readObject();
        }
        if (this._nextObject != null) {
            ASN1Encodable o = this._nextObject;
            this._nextObject = null;
            return (ASN1SetParser) ((ASN1TaggedObjectParser) o).getObjectParser(17, false);
        }
        return null;
    }
}
