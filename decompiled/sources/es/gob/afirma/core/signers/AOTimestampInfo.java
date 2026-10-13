package es.gob.afirma.core.signers;

import es.gob.afirma.core.misc.AOUtil;
import java.security.cert.X509Certificate;
import java.util.Date;

/* JADX INFO: loaded from: autofirma.jar:es/gob/afirma/core/signers/AOTimestampInfo.class */
public final class AOTimestampInfo {
    private final X509Certificate issuer;
    private final Date date;

    public AOTimestampInfo(X509Certificate x509Certificate, Date date) {
        this.issuer = x509Certificate;
        this.date = (Date) date.clone();
    }

    public Date getDate() {
        return (Date) this.date.clone();
    }

    public X509Certificate getIssuer() {
        return this.issuer;
    }

    public String toString() {
        return "Sello de tiempo emitido por '" + (this.issuer != null ? AOUtil.getCN(this.issuer) : "DESCONOCIDO") + "' con fecha " + this.date;
    }
}
