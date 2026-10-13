package org.apache.pdfbox.cos;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.apache.pdfbox.filter.DecodeOptions;
import org.apache.pdfbox.filter.DecodeResult;
import org.apache.pdfbox.filter.Filter;
import org.apache.pdfbox.io.RandomAccess;
import org.apache.pdfbox.io.RandomAccessInputStream;
import org.apache.pdfbox.io.RandomAccessOutputStream;
import org.apache.pdfbox.io.ScratchFile;

/* JADX INFO: loaded from: autofirma.jar:org/apache/pdfbox/cos/COSInputStream.class */
public final class COSInputStream extends FilterInputStream {
    private final List<DecodeResult> decodeResults;

    static COSInputStream create(List<Filter> filters, COSDictionary parameters, InputStream in, ScratchFile scratchFile) throws IOException {
        return create(filters, parameters, in, scratchFile, DecodeOptions.DEFAULT);
    }

    static COSInputStream create(List<Filter> filters, COSDictionary parameters, InputStream in, ScratchFile scratchFile, DecodeOptions options) throws IOException {
        InputStream byteArrayInputStream;
        InputStream input = in;
        if (filters.isEmpty()) {
            return new COSInputStream(in, Collections.emptyList());
        }
        List<DecodeResult> results = new ArrayList<>(filters.size());
        if (filters.size() > 1) {
            Set<Filter> filterSet = new HashSet<>(filters);
            if (filterSet.size() != filters.size()) {
                throw new IOException("Duplicate");
            }
        }
        for (int i = 0; i < filters.size(); i++) {
            if (scratchFile != null) {
                final RandomAccess buffer = scratchFile.createBuffer();
                DecodeResult result = filters.get(i).decode(input, new RandomAccessOutputStream(buffer), parameters, i, options);
                results.add(result);
                byteArrayInputStream = new RandomAccessInputStream(buffer) { // from class: org.apache.pdfbox.cos.COSInputStream.1
                    @Override // java.io.InputStream, java.io.Closeable, java.lang.AutoCloseable
                    public void close() throws IOException {
                        buffer.close();
                    }
                };
            } else {
                ByteArrayOutputStream output = new ByteArrayOutputStream();
                DecodeResult result2 = filters.get(i).decode(input, output, parameters, i, options);
                results.add(result2);
                byteArrayInputStream = new ByteArrayInputStream(output.toByteArray());
            }
            input = byteArrayInputStream;
        }
        return new COSInputStream(input, results);
    }

    private COSInputStream(InputStream input, List<DecodeResult> decodeResults) {
        super(input);
        this.decodeResults = decodeResults;
    }

    public DecodeResult getDecodeResult() {
        if (this.decodeResults.isEmpty()) {
            return DecodeResult.DEFAULT;
        }
        return this.decodeResults.get(this.decodeResults.size() - 1);
    }
}
