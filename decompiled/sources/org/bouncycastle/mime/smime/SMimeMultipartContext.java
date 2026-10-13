package org.bouncycastle.mime.smime;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.mime.CanonicalOutputStream;
import org.bouncycastle.mime.Headers;
import org.bouncycastle.mime.MimeContext;
import org.bouncycastle.mime.MimeMultipartContext;
import org.bouncycastle.mime.MimeParserContext;
import org.bouncycastle.operator.DigestCalculator;
import org.bouncycastle.operator.OperatorCreationException;
import org.bouncycastle.util.io.TeeInputStream;
import org.bouncycastle.util.io.TeeOutputStream;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/mime/smime/SMimeMultipartContext.class */
public class SMimeMultipartContext implements MimeMultipartContext {
    private final SMimeParserContext parserContext;
    private DigestCalculator[] calculators;

    public SMimeMultipartContext(MimeParserContext mimeParserContext, Headers headers) {
        this.parserContext = (SMimeParserContext) mimeParserContext;
        this.calculators = createDigestCalculators(headers);
    }

    DigestCalculator[] getDigestCalculators() {
        return this.calculators;
    }

    OutputStream getDigestOutputStream() {
        if (this.calculators.length == 1) {
            return this.calculators[0].getOutputStream();
        }
        OutputStream outputStream = this.calculators[0].getOutputStream();
        for (int i = 1; i < this.calculators.length; i++) {
            outputStream = new TeeOutputStream(this.calculators[i].getOutputStream(), outputStream);
        }
        return outputStream;
    }

    private DigestCalculator[] createDigestCalculators(Headers headers) {
        try {
            String str = headers.getContentTypeAttributes().get("micalg");
            if (str == null) {
                throw new IllegalStateException("No micalg field on content-type header");
            }
            String[] strArrSplit = str.substring(str.indexOf(61) + 1).split(",");
            DigestCalculator[] digestCalculatorArr = new DigestCalculator[strArrSplit.length];
            for (int i = 0; i < strArrSplit.length; i++) {
                digestCalculatorArr[i] = this.parserContext.getDigestCalculatorProvider().get(new AlgorithmIdentifier(SMimeUtils.getDigestOID(SMimeUtils.lessQuotes(strArrSplit[i]).trim())));
            }
            return digestCalculatorArr;
        } catch (OperatorCreationException e) {
            return null;
        }
    }

    @Override // org.bouncycastle.mime.MimeMultipartContext
    public MimeContext createContext(final int i) throws IOException {
        return new MimeContext() { // from class: org.bouncycastle.mime.smime.SMimeMultipartContext.1
            @Override // org.bouncycastle.mime.MimeContext
            public InputStream applyContext(Headers headers, InputStream inputStream) throws IOException {
                if (i != 0) {
                    return inputStream;
                }
                OutputStream digestOutputStream = SMimeMultipartContext.this.getDigestOutputStream();
                headers.dumpHeaders(digestOutputStream);
                digestOutputStream.write(13);
                digestOutputStream.write(10);
                return new TeeInputStream(inputStream, new CanonicalOutputStream(SMimeMultipartContext.this.parserContext, headers, digestOutputStream));
            }
        };
    }

    @Override // org.bouncycastle.mime.MimeContext
    public InputStream applyContext(Headers headers, InputStream inputStream) throws IOException {
        return inputStream;
    }
}
