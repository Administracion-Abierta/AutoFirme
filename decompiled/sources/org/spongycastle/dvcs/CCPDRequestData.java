package org.spongycastle.dvcs;

import org.spongycastle.asn1.dvcs.Data;

/* JADX INFO: loaded from: autofirma.jar:org/spongycastle/dvcs/CCPDRequestData.class */
public class CCPDRequestData extends DVCSRequestData {
    CCPDRequestData(Data data) throws DVCSConstructionException {
        super(data);
        initDigest();
    }

    private void initDigest() throws DVCSConstructionException {
        if (this.data.getMessageImprint() == null) {
            throw new DVCSConstructionException("DVCSRequest.data.messageImprint should be specified for CCPD service");
        }
    }

    public MessageImprint getMessageImprint() {
        return new MessageImprint(this.data.getMessageImprint());
    }
}
