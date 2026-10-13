package org.spongycastle.x509;

import org.spongycastle.util.Selector;

/* JADX INFO: loaded from: autofirma.jar:org/spongycastle/x509/X509CertPairStoreSelector.class */
public class X509CertPairStoreSelector implements Selector {
    private X509CertStoreSelector forwardSelector;
    private X509CertStoreSelector reverseSelector;
    private X509CertificatePair certPair;

    public X509CertificatePair getCertPair() {
        return this.certPair;
    }

    public void setCertPair(X509CertificatePair certPair) {
        this.certPair = certPair;
    }

    public void setForwardSelector(X509CertStoreSelector forwardSelector) {
        this.forwardSelector = forwardSelector;
    }

    public void setReverseSelector(X509CertStoreSelector reverseSelector) {
        this.reverseSelector = reverseSelector;
    }

    @Override // org.spongycastle.util.Selector
    public Object clone() {
        X509CertPairStoreSelector cln = new X509CertPairStoreSelector();
        cln.certPair = this.certPair;
        if (this.forwardSelector != null) {
            cln.setForwardSelector((X509CertStoreSelector) this.forwardSelector.clone());
        }
        if (this.reverseSelector != null) {
            cln.setReverseSelector((X509CertStoreSelector) this.reverseSelector.clone());
        }
        return cln;
    }

    @Override // org.spongycastle.util.Selector
    public boolean match(Object obj) {
        try {
            if (!(obj instanceof X509CertificatePair)) {
                return false;
            }
            X509CertificatePair pair = (X509CertificatePair) obj;
            if (this.forwardSelector != null && !this.forwardSelector.match((Object) pair.getForward())) {
                return false;
            }
            if (this.reverseSelector != null && !this.reverseSelector.match((Object) pair.getReverse())) {
                return false;
            }
            if (this.certPair != null) {
                return this.certPair.equals(obj);
            }
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public X509CertStoreSelector getForwardSelector() {
        return this.forwardSelector;
    }

    public X509CertStoreSelector getReverseSelector() {
        return this.reverseSelector;
    }
}
