package org.bouncycastle.est;

import java.io.IOException;
import java.io.OutputStream;
import java.net.URL;
import java.util.Map;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/est/ESTRequest.class */
public class ESTRequest {
    final String method;
    final URL url;
    HttpUtil.Headers headers;
    final byte[] data;
    final ESTHijacker hijacker;
    final ESTClient estClient;
    final ESTSourceConnectionListener listener;

    ESTRequest(String str, URL url, byte[] bArr, ESTHijacker eSTHijacker, ESTSourceConnectionListener eSTSourceConnectionListener, HttpUtil.Headers headers, ESTClient eSTClient) {
        this.headers = new HttpUtil.Headers();
        this.method = str;
        this.url = url;
        this.data = bArr;
        this.hijacker = eSTHijacker;
        this.listener = eSTSourceConnectionListener;
        this.headers = headers;
        this.estClient = eSTClient;
    }

    public String getMethod() {
        return this.method;
    }

    public URL getURL() {
        return this.url;
    }

    public Map<String, String[]> getHeaders() {
        return (Map) this.headers.clone();
    }

    public ESTHijacker getHijacker() {
        return this.hijacker;
    }

    public ESTClient getClient() {
        return this.estClient;
    }

    public ESTSourceConnectionListener getListener() {
        return this.listener;
    }

    public void writeData(OutputStream outputStream) throws IOException {
        if (this.data != null) {
            outputStream.write(this.data);
        }
    }
}
