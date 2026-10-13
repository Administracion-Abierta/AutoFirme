package org.apache.pdfbox.filter;

import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import org.apache.pdfbox.cos.COSName;

/* JADX INFO: loaded from: autofirma.jar:org/apache/pdfbox/filter/FilterFactory.class */
public final class FilterFactory {
    public static final FilterFactory INSTANCE = new FilterFactory();
    private final Map<COSName, Filter> filters = new HashMap();

    private FilterFactory() {
        Filter flate = new FlateFilter();
        Filter dct = new DCTFilter();
        Filter ccittFax = new CCITTFaxFilter();
        Filter lzw = new LZWFilter();
        Filter asciiHex = new ASCIIHexFilter();
        Filter ascii85 = new ASCII85Filter();
        Filter runLength = new RunLengthDecodeFilter();
        Filter crypt = new CryptFilter();
        Filter jpx = new JPXFilter();
        Filter jbig2 = new JBIG2Filter();
        this.filters.put(COSName.FLATE_DECODE, flate);
        this.filters.put(COSName.FLATE_DECODE_ABBREVIATION, flate);
        this.filters.put(COSName.DCT_DECODE, dct);
        this.filters.put(COSName.DCT_DECODE_ABBREVIATION, dct);
        this.filters.put(COSName.CCITTFAX_DECODE, ccittFax);
        this.filters.put(COSName.CCITTFAX_DECODE_ABBREVIATION, ccittFax);
        this.filters.put(COSName.LZW_DECODE, lzw);
        this.filters.put(COSName.LZW_DECODE_ABBREVIATION, lzw);
        this.filters.put(COSName.ASCII_HEX_DECODE, asciiHex);
        this.filters.put(COSName.ASCII_HEX_DECODE_ABBREVIATION, asciiHex);
        this.filters.put(COSName.ASCII85_DECODE, ascii85);
        this.filters.put(COSName.ASCII85_DECODE_ABBREVIATION, ascii85);
        this.filters.put(COSName.RUN_LENGTH_DECODE, runLength);
        this.filters.put(COSName.RUN_LENGTH_DECODE_ABBREVIATION, runLength);
        this.filters.put(COSName.CRYPT, crypt);
        this.filters.put(COSName.JPX_DECODE, jpx);
        this.filters.put(COSName.JBIG2_DECODE, jbig2);
    }

    public Filter getFilter(String filterName) throws IOException {
        return getFilter(COSName.getPDFName(filterName));
    }

    public Filter getFilter(COSName filterName) throws IOException {
        Filter filter = this.filters.get(filterName);
        if (filter == null) {
            throw new IOException("Invalid filter: " + filterName);
        }
        return filter;
    }

    Collection<Filter> getAllFilters() {
        return this.filters.values();
    }
}
