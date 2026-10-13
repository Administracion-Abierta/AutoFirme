package org.spongycastle.est.jcajce;

import java.io.IOException;
import java.io.OutputStream;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.Map;
import java.util.Set;
import org.spongycastle.est.ESTClient;
import org.spongycastle.est.ESTClientSourceProvider;
import org.spongycastle.est.ESTException;
import org.spongycastle.est.ESTRequest;
import org.spongycastle.est.ESTRequestBuilder;
import org.spongycastle.est.ESTResponse;
import org.spongycastle.est.Source;
import org.spongycastle.util.Properties;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:org/spongycastle/est/jcajce/DefaultESTClient.class */
class DefaultESTClient implements ESTClient {
    private static final Charset utf8 = Charset.forName("UTF-8");
    private static byte[] CRLF = {13, 10};
    private final ESTClientSourceProvider sslSocketProvider;

    public DefaultESTClient(ESTClientSourceProvider sslSocketProvider) {
        this.sslSocketProvider = sslSocketProvider;
    }

    private static void writeLine(OutputStream os, String s) throws IOException {
        os.write(s.getBytes());
        os.write(CRLF);
    }

    @Override // org.spongycastle.est.ESTClient
    public ESTResponse doRequest(ESTRequest req) throws IOException {
        ESTResponse resp;
        ESTRequest r = req;
        int rcCount = 15;
        do {
            resp = performRequest(r);
            r = redirectURL(resp);
            if (r == null) {
                break;
            }
            rcCount--;
        } while (rcCount > 0);
        if (rcCount == 0) {
            throw new ESTException("Too many redirects..");
        }
        return resp;
    }

    protected ESTRequest redirectURL(ESTResponse response) throws IOException {
        ESTRequest redirectingRequest = null;
        if (response.getStatusCode() >= 300 && response.getStatusCode() <= 399) {
            switch (response.getStatusCode()) {
                case 301:
                case 302:
                case 303:
                case 306:
                case 307:
                    String loc = response.getHeader("Location");
                    if ("".equals(loc)) {
                        throw new ESTException("Redirect status type: " + response.getStatusCode() + " but no location header");
                    }
                    ESTRequestBuilder requestBuilder = new ESTRequestBuilder(response.getOriginalRequest());
                    if (loc.startsWith("http")) {
                        redirectingRequest = requestBuilder.withURL(new URL(loc)).build();
                    } else {
                        URL u = response.getOriginalRequest().getURL();
                        redirectingRequest = requestBuilder.withURL(new URL(u.getProtocol(), u.getHost(), u.getPort(), loc)).build();
                    }
                    break;
                    break;
                case 304:
                case 305:
                default:
                    throw new ESTException("Client does not handle http status code: " + response.getStatusCode());
            }
        }
        if (redirectingRequest != null) {
            response.close();
        }
        return redirectingRequest;
    }

    public ESTResponse performRequest(ESTRequest c) throws IOException {
        OutputStream os;
        Source socketSource = null;
        try {
            Source socketSource2 = this.sslSocketProvider.makeSource(c.getURL().getHost(), c.getURL().getPort());
            if (c.getListener() != null) {
                c = c.getListener().onConnection(socketSource2, c);
            }
            Set<String> opts = Properties.asKeySet("org.spongycastle.debug.est");
            if (opts.contains("output") || opts.contains("all")) {
                os = new PrintingOutputStream(socketSource2.getOutputStream());
            } else {
                os = socketSource2.getOutputStream();
            }
            String req = c.getURL().getPath() + (c.getURL().getQuery() != null ? c.getURL().getQuery() : "");
            ESTRequestBuilder rb = new ESTRequestBuilder(c);
            Map<String, String[]> headers = c.getHeaders();
            if (!headers.containsKey("Connection")) {
                rb.addHeader("Connection", "close");
            }
            URL u = c.getURL();
            if (u.getPort() > -1) {
                rb.setHeader("Host", String.format("%s:%d", u.getHost(), Integer.valueOf(u.getPort())));
            } else {
                rb.setHeader("Host", u.getHost());
            }
            ESTRequest rc = rb.build();
            writeLine(os, rc.getMethod() + " " + req + " HTTP/1.1");
            for (Map.Entry<String, String[]> ent : rc.getHeaders().entrySet()) {
                String[] vs = ent.getValue();
                for (int i = 0; i != vs.length; i++) {
                    writeLine(os, ent.getKey() + ": " + vs[i]);
                }
            }
            os.write(CRLF);
            os.flush();
            rc.writeData(os);
            os.flush();
            if (rc.getHijacker() != null) {
                ESTResponse res = rc.getHijacker().hijack(rc, socketSource2);
                if (socketSource2 != null && res == null) {
                    socketSource2.close();
                }
                return res;
            }
            ESTResponse res2 = new ESTResponse(rc, socketSource2);
            if (socketSource2 != null && res2 == null) {
                socketSource2.close();
            }
            return res2;
        } catch (Throwable th) {
            if (0 != 0 && 0 == 0) {
                socketSource.close();
            }
            throw th;
        }
    }

    /* JADX INFO: loaded from: autofirmaConfigurador.jar:org/spongycastle/est/jcajce/DefaultESTClient$PrintingOutputStream.class */
    private class PrintingOutputStream extends OutputStream {
        private final OutputStream tgt;

        public PrintingOutputStream(OutputStream tgt) {
            this.tgt = tgt;
        }

        @Override // java.io.OutputStream
        public void write(int b) throws IOException {
            System.out.print(String.valueOf((char) b));
            this.tgt.write(b);
        }
    }
}
