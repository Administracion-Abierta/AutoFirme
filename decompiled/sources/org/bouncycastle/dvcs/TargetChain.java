package org.bouncycastle.dvcs;

import org.bouncycastle.asn1.dvcs.TargetEtcChain;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/dvcs/TargetChain.class */
public class TargetChain {
    private final TargetEtcChain certs;

    public TargetChain(TargetEtcChain targetEtcChain) {
        this.certs = targetEtcChain;
    }

    public TargetEtcChain toASN1Structure() {
        return this.certs;
    }
}
