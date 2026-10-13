package org.spongycastle.crypto.tls;

import java.io.IOException;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:org/spongycastle/crypto/tls/DTLSTransport.class */
public class DTLSTransport implements DatagramTransport {
    private final DTLSRecordLayer recordLayer;

    DTLSTransport(DTLSRecordLayer recordLayer) {
        this.recordLayer = recordLayer;
    }

    @Override // org.spongycastle.crypto.tls.DatagramTransport
    public int getReceiveLimit() throws IOException {
        return this.recordLayer.getReceiveLimit();
    }

    @Override // org.spongycastle.crypto.tls.DatagramTransport
    public int getSendLimit() throws IOException {
        return this.recordLayer.getSendLimit();
    }

    @Override // org.spongycastle.crypto.tls.DatagramTransport
    public int receive(byte[] buf, int off, int len, int waitMillis) throws IOException {
        try {
            return this.recordLayer.receive(buf, off, len, waitMillis);
        } catch (RuntimeException e) {
            this.recordLayer.fail((short) 80);
            throw new TlsFatalAlert((short) 80, e);
        } catch (TlsFatalAlert fatalAlert) {
            this.recordLayer.fail(fatalAlert.getAlertDescription());
            throw fatalAlert;
        } catch (IOException e2) {
            this.recordLayer.fail((short) 80);
            throw e2;
        }
    }

    @Override // org.spongycastle.crypto.tls.DatagramTransport
    public void send(byte[] buf, int off, int len) throws IOException {
        try {
            this.recordLayer.send(buf, off, len);
        } catch (RuntimeException e) {
            this.recordLayer.fail((short) 80);
            throw new TlsFatalAlert((short) 80, e);
        } catch (TlsFatalAlert fatalAlert) {
            this.recordLayer.fail(fatalAlert.getAlertDescription());
            throw fatalAlert;
        } catch (IOException e2) {
            this.recordLayer.fail((short) 80);
            throw e2;
        }
    }

    @Override // org.spongycastle.crypto.tls.DatagramTransport
    public void close() throws IOException {
        this.recordLayer.close();
    }
}
