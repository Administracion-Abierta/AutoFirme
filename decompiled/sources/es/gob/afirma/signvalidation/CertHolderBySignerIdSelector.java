package es.gob.afirma.signvalidation;

import org.spongycastle.cert.X509CertificateHolder;
import org.spongycastle.cms.SignerId;
import org.spongycastle.util.Selector;

/* JADX INFO: loaded from: autofirma.jar:es/gob/afirma/signvalidation/CertHolderBySignerIdSelector.class */
public final class CertHolderBySignerIdSelector implements Selector<X509CertificateHolder> {
    private final SignerId signerId;

    public CertHolderBySignerIdSelector(SignerId signerId) {
        if (signerId == null) {
            throw new IllegalArgumentException("El ID del firmante no puede ser nulo");
        }
        this.signerId = signerId;
    }

    @Override // org.spongycastle.util.Selector
    public boolean match(X509CertificateHolder x509CertificateHolder) {
        return this.signerId.getSerialNumber().equals(x509CertificateHolder.getSerialNumber());
    }

    @Override // org.spongycastle.util.Selector
    public Object clone() {
        throw new UnsupportedOperationException();
    }
}
