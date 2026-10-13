package org.spongycastle.dvcs;

import org.spongycastle.asn1.dvcs.TargetEtcChain;

/* JADX INFO: loaded from: autofirma.jar:org/spongycastle/dvcs/TargetChain.class */
public class TargetChain {
    private final TargetEtcChain certs;

    public TargetChain(TargetEtcChain certs) {
        this.certs = certs;
    }

    public TargetEtcChain toASN1Structure() {
        return this.certs;
    }
}
