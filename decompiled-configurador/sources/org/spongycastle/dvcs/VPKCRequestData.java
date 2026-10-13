package org.spongycastle.dvcs;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.spongycastle.asn1.dvcs.Data;
import org.spongycastle.asn1.dvcs.TargetEtcChain;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:org/spongycastle/dvcs/VPKCRequestData.class */
public class VPKCRequestData extends DVCSRequestData {
    private List chains;

    VPKCRequestData(Data data) throws DVCSConstructionException {
        super(data);
        TargetEtcChain[] certs = data.getCerts();
        if (certs == null) {
            throw new DVCSConstructionException("DVCSRequest.data.certs should be specified for VPKC service");
        }
        this.chains = new ArrayList(certs.length);
        for (int i = 0; i != certs.length; i++) {
            this.chains.add(new TargetChain(certs[i]));
        }
    }

    public List getCerts() {
        return Collections.unmodifiableList(this.chains);
    }
}
