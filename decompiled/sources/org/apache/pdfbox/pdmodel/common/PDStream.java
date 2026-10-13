package org.apache.pdfbox.pdmodel.common;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.pdfbox.cos.COSArray;
import org.apache.pdfbox.cos.COSBase;
import org.apache.pdfbox.cos.COSDictionary;
import org.apache.pdfbox.cos.COSDocument;
import org.apache.pdfbox.cos.COSInputStream;
import org.apache.pdfbox.cos.COSName;
import org.apache.pdfbox.cos.COSNull;
import org.apache.pdfbox.cos.COSStream;
import org.apache.pdfbox.filter.DecodeOptions;
import org.apache.pdfbox.filter.Filter;
import org.apache.pdfbox.filter.FilterFactory;
import org.apache.pdfbox.io.IOUtils;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.common.filespecification.PDFileSpecification;

/* JADX INFO: loaded from: autofirma.jar:org/apache/pdfbox/pdmodel/common/PDStream.class */
public class PDStream implements COSObjectable {
    private static final Log LOG = LogFactory.getLog(PDStream.class);
    private final COSStream stream;

    public PDStream(PDDocument document) {
        this.stream = document.getDocument().createCOSStream();
    }

    public PDStream(COSDocument document) {
        this.stream = document.createCOSStream();
    }

    public PDStream(COSStream str) {
        this.stream = str;
    }

    public PDStream(PDDocument doc, InputStream input) throws IOException {
        this(doc, input, (COSBase) null);
    }

    public PDStream(PDDocument doc, InputStream input, COSName filter) throws IOException {
        this(doc, input, (COSBase) filter);
    }

    public PDStream(PDDocument doc, InputStream input, COSArray filters) throws IOException {
        this(doc, input, (COSBase) filters);
    }

    private PDStream(PDDocument doc, InputStream input, COSBase filters) throws IOException {
        OutputStream output = null;
        try {
            this.stream = doc.getDocument().createCOSStream();
            OutputStream output2 = this.stream.createOutputStream(filters);
            IOUtils.copy(input, output2);
            if (output2 != null) {
            }
        } finally {
            if (output != null) {
                output.close();
            }
            input.close();
        }
    }

    /* JADX WARN: Bottom block not found for handler: all -> 0x0043 */
    @java.lang.Deprecated
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    public void addCompression() {
        /*
            r5 = this;
            r0 = r5
            java.util.List r0 = r0.getFilters()
            r6 = r0
            r0 = r6
            if (r0 != 0) goto L67
            r0 = r5
            org.apache.pdfbox.cos.COSStream r0 = r0.stream
            long r0 = r0.getLength()
            r1 = 0
            int r0 = (r0 > r1 ? 1 : (r0 == r1 ? 0 : -1))
            if (r0 <= 0) goto L4f
            r0 = 0
            r7 = r0
            r0 = r5
            org.apache.pdfbox.cos.COSStream r0 = r0.stream     // Catch: java.io.IOException -> L39 java.lang.Throwable -> L43
            org.apache.pdfbox.cos.COSInputStream r0 = r0.createInputStream()     // Catch: java.io.IOException -> L39 java.lang.Throwable -> L43
            byte[] r0 = org.apache.pdfbox.io.IOUtils.toByteArray(r0)     // Catch: java.io.IOException -> L39 java.lang.Throwable -> L43
            r8 = r0
            r0 = r5
            org.apache.pdfbox.cos.COSStream r0 = r0.stream     // Catch: java.io.IOException -> L39 java.lang.Throwable -> L43
            org.apache.pdfbox.cos.COSName r1 = org.apache.pdfbox.cos.COSName.FLATE_DECODE     // Catch: java.io.IOException -> L39 java.lang.Throwable -> L43
            java.io.OutputStream r0 = r0.createOutputStream(r1)     // Catch: java.io.IOException -> L39 java.lang.Throwable -> L43
            r7 = r0
            r0 = r7
            r1 = r8
            r0.write(r1)     // Catch: java.io.IOException -> L39 java.lang.Throwable -> L43
            r0 = r7
            org.apache.pdfbox.io.IOUtils.closeQuietly(r0)
            goto L4c
        L39:
            r8 = move-exception
            java.lang.RuntimeException r0 = new java.lang.RuntimeException     // Catch: java.lang.Throwable -> L43
            r1 = r0
            r2 = r8
            r1.<init>(r2)     // Catch: java.lang.Throwable -> L43
            throw r0     // Catch: java.lang.Throwable -> L43
        L43:
            r9 = move-exception
            r0 = r7
            org.apache.pdfbox.io.IOUtils.closeQuietly(r0)
            r0 = r9
            throw r0
        L4c:
            goto L67
        L4f:
            java.util.ArrayList r0 = new java.util.ArrayList
            r1 = r0
            r2 = 1
            r1.<init>(r2)
            r6 = r0
            r0 = r6
            org.apache.pdfbox.cos.COSName r1 = org.apache.pdfbox.cos.COSName.FLATE_DECODE
            boolean r0 = r0.add(r1)
            r0 = r5
            r1 = r6
            r0.setFilters(r1)
        L67:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.apache.pdfbox.pdmodel.common.PDStream.addCompression():void");
    }

    @Override // org.apache.pdfbox.pdmodel.common.COSObjectable
    public COSStream getCOSObject() {
        return this.stream;
    }

    public OutputStream createOutputStream() throws IOException {
        return this.stream.createOutputStream();
    }

    public OutputStream createOutputStream(COSName filter) throws IOException {
        return this.stream.createOutputStream(filter);
    }

    public COSInputStream createInputStream() throws IOException {
        return this.stream.createInputStream();
    }

    public COSInputStream createInputStream(DecodeOptions options) throws IOException {
        return this.stream.createInputStream(options);
    }

    public InputStream createInputStream(List<String> stopFilters) throws IOException {
        InputStream is = this.stream.createRawInputStream();
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        List<COSName> filters = getFilters();
        if (filters != null) {
            for (int i = 0; i < filters.size(); i++) {
                COSName nextFilter = filters.get(i);
                if (stopFilters != null && stopFilters.contains(nextFilter.getName())) {
                    break;
                }
                Filter filter = FilterFactory.INSTANCE.getFilter(nextFilter);
                try {
                    filter.decode(is, os, this.stream, i);
                    IOUtils.closeQuietly(is);
                    is = new ByteArrayInputStream(os.toByteArray());
                    os.reset();
                } catch (Throwable th) {
                    IOUtils.closeQuietly(is);
                    throw th;
                }
            }
        }
        return is;
    }

    @Deprecated
    public COSStream getStream() {
        return this.stream;
    }

    public int getLength() {
        return this.stream.getInt(COSName.LENGTH, 0);
    }

    /* JADX WARN: Multi-variable type inference failed */
    public List<COSName> getFilters() {
        List list = null;
        COSBase filters = this.stream.getFilters();
        if (filters instanceof COSName) {
            COSName name = (COSName) filters;
            list = new COSArrayList(name, name, this.stream, COSName.FILTER);
        } else if (filters instanceof COSArray) {
            list = ((COSArray) filters).toList();
        }
        return list;
    }

    public void setFilters(List<COSName> filters) {
        COSBase obj = COSArrayList.converterToCOSArray(filters);
        this.stream.setItem(COSName.FILTER, obj);
    }

    public List<Object> getDecodeParms() throws IOException {
        return internalGetDecodeParams(COSName.DECODE_PARMS, COSName.DP);
    }

    public List<Object> getFileDecodeParams() throws IOException {
        return internalGetDecodeParams(COSName.F_DECODE_PARMS, null);
    }

    private List<Object> internalGetDecodeParams(COSName name1, COSName name2) throws IOException {
        COSBase dp = this.stream.getDictionaryObject(name1, name2);
        if (dp instanceof COSDictionary) {
            Map<?, ?> map = COSDictionaryMap.convertBasicTypesToMap((COSDictionary) dp);
            return new COSArrayList(map, dp, this.stream, name1);
        }
        if (dp instanceof COSArray) {
            COSArray array = (COSArray) dp;
            List<Object> actuals = new ArrayList<>(array.size());
            for (int i = 0; i < array.size(); i++) {
                COSBase base = array.getObject(i);
                if (base instanceof COSDictionary) {
                    actuals.add(COSDictionaryMap.convertBasicTypesToMap((COSDictionary) base));
                } else {
                    LOG.warn("Expected COSDictionary, got " + base + ", ignored");
                }
            }
            return new COSArrayList(actuals, array);
        }
        return null;
    }

    public void setDecodeParms(List<?> decodeParams) {
        this.stream.setItem(COSName.DECODE_PARMS, (COSBase) COSArrayList.converterToCOSArray(decodeParams));
    }

    public PDFileSpecification getFile() throws IOException {
        COSBase f = this.stream.getDictionaryObject(COSName.F);
        return PDFileSpecification.createFS(f);
    }

    public void setFile(PDFileSpecification f) {
        this.stream.setItem(COSName.F, f);
    }

    public List<String> getFileFilters() {
        List<String> retval = null;
        COSBase filters = this.stream.getDictionaryObject(COSName.F_FILTER);
        if (filters instanceof COSName) {
            COSName name = (COSName) filters;
            retval = new COSArrayList(name.getName(), name, this.stream, COSName.F_FILTER);
        } else if (filters instanceof COSArray) {
            retval = COSArrayList.convertCOSNameCOSArrayToList((COSArray) filters);
        }
        return retval;
    }

    public void setFileFilters(List<String> filters) {
        COSBase obj = COSArrayList.convertStringListToCOSNameCOSArray(filters);
        this.stream.setItem(COSName.F_FILTER, obj);
    }

    public void setFileDecodeParams(List<?> decodeParams) {
        this.stream.setItem(COSName.F_DECODE_PARMS, (COSBase) COSArrayList.converterToCOSArray(decodeParams));
    }

    public byte[] toByteArray() throws IOException {
        InputStream is = null;
        try {
            InputStream is2 = createInputStream();
            byte[] byteArray = IOUtils.toByteArray(is2);
            if (is2 != null) {
            }
            return byteArray;
        } finally {
            if (is != null) {
                is.close();
            }
        }
    }

    public PDMetadata getMetadata() {
        PDMetadata retval = null;
        COSBase mdStream = this.stream.getDictionaryObject(COSName.METADATA);
        if (mdStream != null) {
            if (mdStream instanceof COSStream) {
                retval = new PDMetadata((COSStream) mdStream);
            } else if (!(mdStream instanceof COSNull)) {
                throw new IllegalStateException("Expected a COSStream but was a " + mdStream.getClass().getSimpleName());
            }
        }
        return retval;
    }

    public void setMetadata(PDMetadata meta) {
        this.stream.setItem(COSName.METADATA, meta);
    }

    public int getDecodedStreamLength() {
        return this.stream.getInt(COSName.DL);
    }

    public void setDecodedStreamLength(int decodedStreamLength) {
        this.stream.setInt(COSName.DL, decodedStreamLength);
    }
}
