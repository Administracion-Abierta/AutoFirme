package com.ctc.wstx.api;

import java.io.IOException;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:com/ctc/wstx/api/InvalidCharHandler.class */
public interface InvalidCharHandler {
    char convertInvalidChar(int i) throws IOException;

    /* JADX INFO: loaded from: autofirmaConfigurador.jar:com/ctc/wstx/api/InvalidCharHandler$FailingHandler.class */
    public static class FailingHandler implements InvalidCharHandler {
        public static final int SURR1_FIRST = 55296;
        public static final int SURR1_LAST = 56319;
        public static final int SURR2_FIRST = 56320;
        public static final int SURR2_LAST = 57343;
        private static final FailingHandler sInstance = new FailingHandler();

        protected FailingHandler() {
        }

        public static FailingHandler getInstance() {
            return sInstance;
        }

        @Override // com.ctc.wstx.api.InvalidCharHandler
        public char convertInvalidChar(int c) throws IOException {
            if (c == 0) {
                throw new IOException("Invalid null character in text to output");
            }
            if (c < 32 || (c >= 127 && c <= 159)) {
                String msg = "Invalid white space character (0x" + Integer.toHexString(c) + ") in text to output (in xml 1.1, could output as a character entity)";
                throw new IOException(msg);
            }
            if (c > 1114111) {
                throw new IOException("Illegal unicode character point (0x" + Integer.toHexString(c) + ") to output; max is 0x10FFFF as per RFC 3629");
            }
            if (c >= 55296 && c <= 57343) {
                throw new IOException("Illegal surrogate pair -- can only be output via character entities, which are not allowed in this content");
            }
            throw new IOException("Invalid XML character (0x" + Integer.toHexString(c) + ") in text to output");
        }
    }

    /* JADX INFO: loaded from: autofirmaConfigurador.jar:com/ctc/wstx/api/InvalidCharHandler$ReplacingHandler.class */
    public static class ReplacingHandler implements InvalidCharHandler {
        final char mReplacementChar;

        public ReplacingHandler(char c) {
            this.mReplacementChar = c;
        }

        @Override // com.ctc.wstx.api.InvalidCharHandler
        public char convertInvalidChar(int c) throws IOException {
            return this.mReplacementChar;
        }
    }
}
