package org.bouncycastle.dvcs;

import org.bouncycastle.asn1.dvcs.DVCSRequestInformationBuilder;
import org.bouncycastle.asn1.dvcs.Data;
import org.bouncycastle.asn1.dvcs.ServiceType;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/dvcs/CPDRequestBuilder.class */
public class CPDRequestBuilder extends DVCSRequestBuilder {
    public CPDRequestBuilder() {
        super(new DVCSRequestInformationBuilder(ServiceType.CPD));
    }

    public DVCSRequest build(byte[] bArr) throws DVCSException {
        return createDVCRequest(new Data(bArr));
    }
}
