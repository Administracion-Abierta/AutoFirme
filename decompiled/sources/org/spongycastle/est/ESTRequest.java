package org.spongycastle.est;

import java.io.IOException;
import java.io.OutputStream;
import java.net.URL;
import java.util.Map;

/* JADX INFO: loaded from: autofirma.jar:org/spongycastle/est/ESTRequest.class */
public class ESTRequest {
    final String method;
    final URL url;
    HttpUtil.Headers headers;
    final byte[] data;
    final ESTHijacker hijacker;
    final ESTClient estClient;
    final ESTSourceConnectionListener listener;

    ESTRequest(String method, URL url, byte[] data, ESTHijacker hijacker, ESTSourceConnectionListener listener, HttpUtil.Headers headers, ESTClient estClient) {
        this.headers = new HttpUtil.Headers();
        this.method = method;
        this.url = url;
        this.data = data;
        this.hijacker = hijacker;
        this.listener = listener;
        this.headers = headers;
        this.estClient = estClient;
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

    public void writeData(OutputStream os) throws IOException {
        if (this.data != null) {
            os.write(this.data);
        }
    }
}
