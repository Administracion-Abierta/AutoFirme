package org.spongycastle.cms;

import java.io.IOException;
import java.io.InputStream;
import org.spongycastle.asn1.ASN1SequenceParser;
import org.spongycastle.asn1.ASN1StreamParser;
import org.spongycastle.asn1.cms.ContentInfoParser;

/* JADX INFO: loaded from: autofirma.jar:org/spongycastle/cms/CMSContentInfoParser.class */
public class CMSContentInfoParser {
    protected ContentInfoParser _contentInfo;
    protected InputStream _data;

    protected CMSContentInfoParser(InputStream data) throws CMSException {
        this._data = data;
        try {
            ASN1StreamParser in = new ASN1StreamParser(data);
            ASN1SequenceParser seqParser = (ASN1SequenceParser) in.readObject();
            if (seqParser == null) {
                throw new CMSException("No content found.");
            }
            this._contentInfo = new ContentInfoParser(seqParser);
        } catch (IOException e) {
            throw new CMSException("IOException reading content.", e);
        } catch (ClassCastException e2) {
            throw new CMSException("Unexpected object reading content.", e2);
        }
    }

    public void close() throws IOException {
        this._data.close();
    }
}
