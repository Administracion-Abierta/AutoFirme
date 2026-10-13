package org.spongycastle.cms.jcajce;

import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.zip.InflaterInputStream;
import org.spongycastle.asn1.x509.AlgorithmIdentifier;
import org.spongycastle.operator.InputExpander;
import org.spongycastle.operator.InputExpanderProvider;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:org/spongycastle/cms/jcajce/ZlibExpanderProvider.class */
public class ZlibExpanderProvider implements InputExpanderProvider {
    private final long limit;

    public ZlibExpanderProvider() {
        this.limit = -1L;
    }

    public ZlibExpanderProvider(long limit) {
        this.limit = limit;
    }

    @Override // org.spongycastle.operator.InputExpanderProvider
    public InputExpander get(final AlgorithmIdentifier algorithm) {
        return new InputExpander() { // from class: org.spongycastle.cms.jcajce.ZlibExpanderProvider.1
            @Override // org.spongycastle.operator.InputExpander
            public AlgorithmIdentifier getAlgorithmIdentifier() {
                return algorithm;
            }

            @Override // org.spongycastle.operator.InputExpander
            public InputStream getInputStream(InputStream comIn) {
                InputStream s = new InflaterInputStream(comIn);
                if (ZlibExpanderProvider.this.limit >= 0) {
                    s = new LimitedInputStream(s, ZlibExpanderProvider.this.limit);
                }
                return s;
            }
        };
    }

    /* JADX INFO: loaded from: autofirmaConfigurador.jar:org/spongycastle/cms/jcajce/ZlibExpanderProvider$LimitedInputStream.class */
    private static class LimitedInputStream extends FilterInputStream {
        private long remaining;

        public LimitedInputStream(InputStream input, long limit) {
            super(input);
            this.remaining = limit;
        }

        /* JADX WARN: Code restructure failed: missing block: B:7:0x0022, code lost:
        
            if (r1 >= 0) goto L8;
         */
        @Override // java.io.FilterInputStream, java.io.InputStream
        /*
            Code decompiled incorrectly, please refer to instructions dump.
            To view partially-correct add '--show-bad-code' argument
        */
        public int read() throws java.io.IOException {
            /*
                r6 = this;
                r0 = r6
                long r0 = r0.remaining
                r1 = 0
                int r0 = (r0 > r1 ? 1 : (r0 == r1 ? 0 : -1))
                if (r0 < 0) goto L27
                r0 = r6
                java.io.InputStream r0 = r0.in
                int r0 = r0.read()
                r7 = r0
                r0 = r7
                if (r0 < 0) goto L25
                r0 = r6
                r1 = r0
                long r1 = r1.remaining
                r2 = 1
                long r1 = r1 - r2
                r2 = r1; r1 = r0; r0 = r2; 
                r1.remaining = r2
                r1 = 0
                int r0 = (r0 > r1 ? 1 : (r0 == r1 ? 0 : -1))
                if (r0 < 0) goto L27
            L25:
                r0 = r7
                return r0
            L27:
                org.spongycastle.util.io.StreamOverflowException r0 = new org.spongycastle.util.io.StreamOverflowException
                r1 = r0
                java.lang.String r2 = "expanded byte limit exceeded"
                r1.<init>(r2)
                throw r0
            */
            throw new UnsupportedOperationException("Method not decompiled: org.spongycastle.cms.jcajce.ZlibExpanderProvider.LimitedInputStream.read():int");
        }

        @Override // java.io.FilterInputStream, java.io.InputStream
        public int read(byte[] buf, int off, int len) throws IOException {
            if (len < 1) {
                return super.read(buf, off, len);
            }
            if (this.remaining < 1) {
                read();
                return -1;
            }
            int actualLen = this.remaining > ((long) len) ? len : (int) this.remaining;
            int numRead = ((FilterInputStream) this).in.read(buf, off, actualLen);
            if (numRead > 0) {
                this.remaining -= (long) numRead;
            }
            return numRead;
        }
    }
}
