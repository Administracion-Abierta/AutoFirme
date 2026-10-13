package org.spongycastle.est;

import es.gob.afirma.signers.pades.common.PdfExtraParams;
import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Set;
import org.spongycastle.util.Properties;
import org.spongycastle.util.Strings;

/* JADX INFO: loaded from: autofirma.jar:org/spongycastle/est/ESTResponse.class */
public class ESTResponse {
    private final ESTRequest originalRequest;
    private final HttpUtil.Headers headers;
    private final byte[] lineBuffer;
    private final Source source;
    private String HttpVersion;
    private int statusCode;
    private String statusMessage;
    private InputStream inputStream;
    private Long contentLength;
    private long read = 0;
    private Long absoluteReadLimit;
    private static final Long ZERO = 0L;

    static /* synthetic */ long access$108(ESTResponse x0) {
        long j = x0.read;
        x0.read = j + 1;
        return j;
    }

    public ESTResponse(ESTRequest originalRequest, Source source) throws IOException {
        this.originalRequest = originalRequest;
        this.source = source;
        if (source instanceof LimitedSource) {
            this.absoluteReadLimit = ((LimitedSource) source).getAbsoluteReadLimit();
        }
        Set<String> opts = Properties.asKeySet("org.spongycastle.debug.est");
        if (opts.contains("input") || opts.contains(PdfExtraParams.PAGES_TO_CHECK_PSA_VALUE_ALL)) {
            this.inputStream = new PrintingInputStream(source.getInputStream());
        } else {
            this.inputStream = source.getInputStream();
        }
        this.headers = new HttpUtil.Headers();
        this.lineBuffer = new byte[1024];
        process();
    }

    private void process() throws IOException {
        this.HttpVersion = readStringIncluding(' ');
        this.statusCode = Integer.parseInt(readStringIncluding(' '));
        this.statusMessage = readStringIncluding('\n');
        String stringIncluding = readStringIncluding('\n');
        while (true) {
            String line = stringIncluding;
            if (line.length() <= 0) {
                break;
            }
            int i = line.indexOf(58);
            if (i > -1) {
                String k = Strings.toLowerCase(line.substring(0, i).trim());
                this.headers.add(k, line.substring(i + 1).trim());
            }
            stringIncluding = readStringIncluding('\n');
        }
        this.contentLength = getContentLength();
        if (this.statusCode == 204 || this.statusCode == 202) {
            if (this.contentLength == null) {
                this.contentLength = 0L;
            } else if (this.statusCode == 204 && this.contentLength.longValue() > 0) {
                throw new IOException("Got HTTP status 204 but Content-length > 0.");
            }
        }
        if (this.contentLength == null) {
            throw new IOException("No Content-length header.");
        }
        if (this.contentLength.equals(ZERO)) {
            this.inputStream = new InputStream() { // from class: org.spongycastle.est.ESTResponse.1
                @Override // java.io.InputStream
                public int read() throws IOException {
                    return -1;
                }
            };
        }
        if (this.contentLength != null) {
            if (this.contentLength.longValue() < 0) {
                throw new IOException("Server returned negative content length: " + this.absoluteReadLimit);
            }
            if (this.absoluteReadLimit != null && this.contentLength.longValue() >= this.absoluteReadLimit.longValue()) {
                throw new IOException("Content length longer than absolute read limit: " + this.absoluteReadLimit + " Content-Length: " + this.contentLength);
            }
        }
        this.inputStream = wrapWithCounter(this.inputStream, this.absoluteReadLimit);
        if ("base64".equalsIgnoreCase(getHeader("content-transfer-encoding"))) {
            this.inputStream = new CTEBase64InputStream(this.inputStream, getContentLength());
        }
    }

    public String getHeader(String key) {
        return this.headers.getFirstValue(key);
    }

    protected InputStream wrapWithCounter(final InputStream in, final Long absoluteReadLimit) {
        return new InputStream() { // from class: org.spongycastle.est.ESTResponse.2
            @Override // java.io.InputStream
            public int read() throws IOException {
                int i = in.read();
                if (i > -1) {
                    ESTResponse.access$108(ESTResponse.this);
                    if (absoluteReadLimit != null && ESTResponse.this.read >= absoluteReadLimit.longValue()) {
                        throw new IOException("Absolute Read Limit exceeded: " + absoluteReadLimit);
                    }
                }
                return i;
            }

            @Override // java.io.InputStream, java.io.Closeable, java.lang.AutoCloseable
            public void close() throws IOException {
                if (ESTResponse.this.contentLength != null && ESTResponse.this.contentLength.longValue() - 1 > ESTResponse.this.read) {
                    throw new IOException("Stream closed before limit fully read, Read: " + ESTResponse.this.read + " ContentLength: " + ESTResponse.this.contentLength);
                }
                if (in.available() > 0) {
                    throw new IOException("Stream closed with extra content in pipe that exceeds content length.");
                }
                in.close();
            }
        };
    }

    protected String readStringIncluding(char until) throws IOException {
        int j;
        int c = 0;
        do {
            j = this.inputStream.read();
            int i = c;
            c++;
            this.lineBuffer[i] = (byte) j;
            if (c >= this.lineBuffer.length) {
                throw new IOException("Server sent line > " + this.lineBuffer.length);
            }
            if (j == until) {
                break;
            }
        } while (j > -1);
        if (j == -1) {
            throw new EOFException();
        }
        return new String(this.lineBuffer, 0, c).trim();
    }

    public ESTRequest getOriginalRequest() {
        return this.originalRequest;
    }

    public HttpUtil.Headers getHeaders() {
        return this.headers;
    }

    public String getHttpVersion() {
        return this.HttpVersion;
    }

    public int getStatusCode() {
        return this.statusCode;
    }

    public String getStatusMessage() {
        return this.statusMessage;
    }

    public InputStream getInputStream() {
        return this.inputStream;
    }

    public Source getSource() {
        return this.source;
    }

    public Long getContentLength() {
        String v = this.headers.getFirstValue("Content-Length");
        if (v == null) {
            return null;
        }
        try {
            return Long.valueOf(Long.parseLong(v));
        } catch (RuntimeException nfe) {
            throw new RuntimeException("Content Length: '" + v + "' invalid. " + nfe.getMessage());
        }
    }

    public void close() throws IOException {
        if (this.inputStream != null) {
            this.inputStream.close();
        }
        this.source.close();
    }

    /* JADX INFO: loaded from: autofirma.jar:org/spongycastle/est/ESTResponse$PrintingInputStream.class */
    private class PrintingInputStream extends InputStream {
        private final InputStream src;

        private PrintingInputStream(InputStream src) {
            this.src = src;
        }

        @Override // java.io.InputStream
        public int read() throws IOException {
            int i = this.src.read();
            System.out.print(String.valueOf((char) i));
            return i;
        }

        @Override // java.io.InputStream
        public int available() throws IOException {
            return this.src.available();
        }

        @Override // java.io.InputStream, java.io.Closeable, java.lang.AutoCloseable
        public void close() throws IOException {
            this.src.close();
        }
    }
}
