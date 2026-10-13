package org.spongycastle.crypto.tls;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:org/spongycastle/crypto/tls/TlsFatalAlert.class */
public class TlsFatalAlert extends TlsException {
    protected short alertDescription;

    public TlsFatalAlert(short alertDescription) {
        this(alertDescription, null);
    }

    public TlsFatalAlert(short alertDescription, Throwable alertCause) {
        super(AlertDescription.getText(alertDescription), alertCause);
        this.alertDescription = alertDescription;
    }

    public short getAlertDescription() {
        return this.alertDescription;
    }
}
