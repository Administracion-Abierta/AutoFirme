package org.bouncycastle.est.jcajce;

import com.sun.jna.platform.win32.WinError;
import com.twelvemonkeys.imageio.metadata.tiff.TIFF;
import es.gob.afirma.signers.pades.common.PdfExtraParams;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.Map;
import java.util.Set;
import org.bouncycastle.est.ESTClient;
import org.bouncycastle.est.ESTClientSourceProvider;
import org.bouncycastle.est.ESTException;
import org.bouncycastle.est.ESTRequest;
import org.bouncycastle.est.ESTRequestBuilder;
import org.bouncycastle.est.ESTResponse;
import org.bouncycastle.est.Source;
import org.bouncycastle.util.Properties;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/est/jcajce/DefaultESTClient.class */
class DefaultESTClient implements ESTClient {
    private static final Charset utf8 = Charset.forName("UTF-8");
    private static byte[] CRLF = {13, 10};
    private final ESTClientSourceProvider sslSocketProvider;

    /* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/est/jcajce/DefaultESTClient$PrintingOutputStream.class */
    private static class PrintingOutputStream extends OutputStream {
        private final OutputStream tgt;

        public PrintingOutputStream(OutputStream outputStream) {
            this.tgt = outputStream;
        }

        @Override // java.io.OutputStream
        public void write(int i) throws IOException {
            System.out.print(String.valueOf((char) i));
            this.tgt.write(i);
        }
    }

    public DefaultESTClient(ESTClientSourceProvider eSTClientSourceProvider) {
        this.sslSocketProvider = eSTClientSourceProvider;
    }

    private static void writeLine(OutputStream outputStream, String str) throws IOException {
        outputStream.write(str.getBytes());
        outputStream.write(CRLF);
    }

    @Override // org.bouncycastle.est.ESTClient
    public ESTResponse doRequest(ESTRequest eSTRequest) throws IOException {
        ESTResponse eSTResponsePerformRequest;
        ESTRequest eSTRequestRedirectURL = eSTRequest;
        int i = 15;
        do {
            eSTResponsePerformRequest = performRequest(eSTRequestRedirectURL);
            eSTRequestRedirectURL = redirectURL(eSTResponsePerformRequest);
            if (eSTRequestRedirectURL == null) {
                break;
            }
            i--;
        } while (i > 0);
        if (i == 0) {
            throw new ESTException("Too many redirects..");
        }
        return eSTResponsePerformRequest;
    }

    protected ESTRequest redirectURL(ESTResponse eSTResponse) throws IOException {
        ESTRequest eSTRequestBuild = null;
        if (eSTResponse.getStatusCode() >= 300 && eSTResponse.getStatusCode() <= 399) {
            switch (eSTResponse.getStatusCode()) {
                case 301:
                case WinError.ERROR_DISK_TOO_FRAGMENTED /* 302 */:
                case WinError.ERROR_DELETE_PENDING /* 303 */:
                case 306:
                case 307:
                    String header = eSTResponse.getHeader("Location");
                    if ("".equals(header)) {
                        throw new ESTException("Redirect status type: " + eSTResponse.getStatusCode() + " but no location header");
                    }
                    ESTRequestBuilder eSTRequestBuilder = new ESTRequestBuilder(eSTResponse.getOriginalRequest());
                    if (!header.startsWith("http")) {
                        URL url = eSTResponse.getOriginalRequest().getURL();
                        eSTRequestBuild = eSTRequestBuilder.withURL(new URL(url.getProtocol(), url.getHost(), url.getPort(), header)).build();
                    } else {
                        eSTRequestBuild = eSTRequestBuilder.withURL(new URL(header)).build();
                    }
                    break;
                case 304:
                case TIFF.TAG_SOFTWARE /* 305 */:
                default:
                    throw new ESTException("Client does not handle http status code: " + eSTResponse.getStatusCode());
            }
        }
        if (eSTRequestBuild != null) {
            eSTResponse.close();
        }
        return eSTRequestBuild;
    }

    public ESTResponse performRequest(ESTRequest eSTRequest) throws IOException {
        Source source = null;
        try {
            Source sourceMakeSource = this.sslSocketProvider.makeSource(eSTRequest.getURL().getHost(), eSTRequest.getURL().getPort());
            if (eSTRequest.getListener() != null) {
                eSTRequest = eSTRequest.getListener().onConnection(sourceMakeSource, eSTRequest);
            }
            Set<String> setAsKeySet = Properties.asKeySet("org.bouncycastle.debug.est");
            OutputStream printingOutputStream = (setAsKeySet.contains("output") || setAsKeySet.contains(PdfExtraParams.PAGES_TO_CHECK_PSA_VALUE_ALL)) ? new PrintingOutputStream(sourceMakeSource.getOutputStream()) : sourceMakeSource.getOutputStream();
            String str = eSTRequest.getURL().getPath() + (eSTRequest.getURL().getQuery() != null ? eSTRequest.getURL().getQuery() : "");
            ESTRequestBuilder eSTRequestBuilder = new ESTRequestBuilder(eSTRequest);
            if (!eSTRequest.getHeaders().containsKey("Connection")) {
                eSTRequestBuilder.addHeader("Connection", "close");
            }
            URL url = eSTRequest.getURL();
            if (url.getPort() > -1) {
                eSTRequestBuilder.setHeader("Host", String.format("%s:%d", url.getHost(), Integer.valueOf(url.getPort())));
            } else {
                eSTRequestBuilder.setHeader("Host", url.getHost());
            }
            ESTRequest eSTRequestBuild = eSTRequestBuilder.build();
            writeLine(printingOutputStream, eSTRequestBuild.getMethod() + " " + str + " HTTP/1.1");
            for (Map.Entry<String, String[]> entry : eSTRequestBuild.getHeaders().entrySet()) {
                String[] value = entry.getValue();
                for (int i = 0; i != value.length; i++) {
                    writeLine(printingOutputStream, entry.getKey() + ": " + value[i]);
                }
            }
            printingOutputStream.write(CRLF);
            printingOutputStream.flush();
            eSTRequestBuild.writeData(printingOutputStream);
            printingOutputStream.flush();
            if (eSTRequestBuild.getHijacker() != null) {
                ESTResponse eSTResponseHijack = eSTRequestBuild.getHijacker().hijack(eSTRequestBuild, sourceMakeSource);
                if (sourceMakeSource != null && eSTResponseHijack == null) {
                    sourceMakeSource.close();
                }
                return eSTResponseHijack;
            }
            ESTResponse eSTResponse = new ESTResponse(eSTRequestBuild, sourceMakeSource);
            if (sourceMakeSource != null && eSTResponse == null) {
                sourceMakeSource.close();
            }
            return eSTResponse;
        } catch (Throwable th) {
            if (0 != 0 && 0 == 0) {
                source.close();
            }
            throw th;
        }
    }
}
