package org.bouncycastle.mime.smime;

import java.io.IOException;
import java.io.InputStream;
import org.bouncycastle.mime.BasicMimeParser;
import org.bouncycastle.mime.Headers;
import org.bouncycastle.mime.MimeParser;
import org.bouncycastle.mime.MimeParserProvider;
import org.bouncycastle.operator.DigestCalculatorProvider;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/mime/smime/SMimeParserProvider.class */
public class SMimeParserProvider implements MimeParserProvider {
    private final String defaultContentTransferEncoding;
    private final DigestCalculatorProvider digestCalculatorProvider;

    public SMimeParserProvider(String str, DigestCalculatorProvider digestCalculatorProvider) {
        this.defaultContentTransferEncoding = str;
        this.digestCalculatorProvider = digestCalculatorProvider;
    }

    @Override // org.bouncycastle.mime.MimeParserProvider
    public MimeParser createParser(InputStream inputStream) throws IOException {
        return new BasicMimeParser(new SMimeParserContext(this.defaultContentTransferEncoding, this.digestCalculatorProvider), SMimeUtils.autoBuffer(inputStream));
    }

    @Override // org.bouncycastle.mime.MimeParserProvider
    public MimeParser createParser(Headers headers, InputStream inputStream) throws IOException {
        return new BasicMimeParser(new SMimeParserContext(this.defaultContentTransferEncoding, this.digestCalculatorProvider), headers, SMimeUtils.autoBuffer(inputStream));
    }
}
