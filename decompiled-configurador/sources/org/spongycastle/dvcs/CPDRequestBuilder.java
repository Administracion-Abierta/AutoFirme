package org.spongycastle.dvcs;

import org.spongycastle.asn1.dvcs.DVCSRequestInformationBuilder;
import org.spongycastle.asn1.dvcs.Data;
import org.spongycastle.asn1.dvcs.ServiceType;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:org/spongycastle/dvcs/CPDRequestBuilder.class */
public class CPDRequestBuilder extends DVCSRequestBuilder {
    public CPDRequestBuilder() {
        super(new DVCSRequestInformationBuilder(ServiceType.CPD));
    }

    public DVCSRequest build(byte[] messageBytes) throws DVCSException {
        Data data = new Data(messageBytes);
        return createDVCRequest(data);
    }
}
