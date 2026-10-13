package org.spongycastle.crypto.tls;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:org/spongycastle/crypto/tls/TlsFatalAlertReceived.class */
public class TlsFatalAlertReceived extends TlsException {
    protected short alertDescription;

    public TlsFatalAlertReceived(short alertDescription) {
        super(AlertDescription.getText(alertDescription), null);
        this.alertDescription = alertDescription;
    }

    public short getAlertDescription() {
        return this.alertDescription;
    }
}
