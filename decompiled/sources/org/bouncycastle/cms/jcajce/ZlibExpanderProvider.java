package org.bouncycastle.cms.jcajce;

import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.zip.InflaterInputStream;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.operator.InputExpander;
import org.bouncycastle.operator.InputExpanderProvider;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/cms/jcajce/ZlibExpanderProvider.class */
public class ZlibExpanderProvider implements InputExpanderProvider {
    private final long limit;

    /* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/cms/jcajce/ZlibExpanderProvider$LimitedInputStream.class */
    private static class LimitedInputStream extends FilterInputStream {
        private long remaining;

        public LimitedInputStream(InputStream inputStream, long j) {
            super(inputStream);
            this.remaining = j;
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
                org.bouncycastle.util.io.StreamOverflowException r0 = new org.bouncycastle.util.io.StreamOverflowException
                r1 = r0
                java.lang.String r2 = "expanded byte limit exceeded"
                r1.<init>(r2)
                throw r0
            */
            throw new UnsupportedOperationException("Method not decompiled: org.bouncycastle.cms.jcajce.ZlibExpanderProvider.LimitedInputStream.read():int");
        }

        @Override // java.io.FilterInputStream, java.io.InputStream
        public int read(byte[] bArr, int i, int i2) throws IOException {
            if (i2 < 1) {
                return super.read(bArr, i, i2);
            }
            if (this.remaining < 1) {
                read();
                return -1;
            }
            int i3 = ((FilterInputStream) this).in.read(bArr, i, this.remaining > ((long) i2) ? i2 : (int) this.remaining);
            if (i3 > 0) {
                this.remaining -= (long) i3;
            }
            return i3;
        }
    }

    public ZlibExpanderProvider() {
        this.limit = -1L;
    }

    public ZlibExpanderProvider(long j) {
        this.limit = j;
    }

    @Override // org.bouncycastle.operator.InputExpanderProvider
    public InputExpander get(final AlgorithmIdentifier algorithmIdentifier) {
        return new InputExpander() { // from class: org.bouncycastle.cms.jcajce.ZlibExpanderProvider.1
            @Override // org.bouncycastle.operator.InputExpander
            public AlgorithmIdentifier getAlgorithmIdentifier() {
                return algorithmIdentifier;
            }

            @Override // org.bouncycastle.operator.InputExpander
            public InputStream getInputStream(InputStream inputStream) {
                InputStream inflaterInputStream = new InflaterInputStream(inputStream);
                if (ZlibExpanderProvider.this.limit >= 0) {
                    inflaterInputStream = new LimitedInputStream(inflaterInputStream, ZlibExpanderProvider.this.limit);
                }
                return inflaterInputStream;
            }
        };
    }
}
