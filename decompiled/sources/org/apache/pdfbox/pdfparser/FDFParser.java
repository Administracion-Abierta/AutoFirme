package org.apache.pdfbox.pdfparser;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.pdfbox.contentstream.operator.OperatorName;
import org.apache.pdfbox.cos.COSBase;
import org.apache.pdfbox.cos.COSDictionary;
import org.apache.pdfbox.cos.COSDocument;
import org.apache.pdfbox.cos.COSName;
import org.apache.pdfbox.io.IOUtils;
import org.apache.pdfbox.io.RandomAccessBuffer;
import org.apache.pdfbox.io.RandomAccessFile;
import org.apache.pdfbox.pdmodel.common.PDPageLabelRange;

/* JADX INFO: loaded from: autofirma.jar:org/apache/pdfbox/pdfparser/FDFParser.class */
public class FDFParser extends COSParser {
    private static final Log LOG = LogFactory.getLog(FDFParser.class);

    public FDFParser(String filename) throws IOException {
        this(new File(filename));
    }

    public FDFParser(File file) throws IOException {
        super(new RandomAccessFile(file, PDPageLabelRange.STYLE_ROMAN_LOWER));
        this.fileLen = file.length();
        init();
    }

    public FDFParser(InputStream input) throws IOException {
        super(new RandomAccessBuffer(input));
        this.fileLen = this.source.length();
        init();
    }

    @Override // org.apache.pdfbox.pdfparser.COSParser
    protected final boolean isCatalog(COSDictionary dictionary) {
        return dictionary.containsKey(COSName.FDF);
    }

    private void init() {
        String eofLookupRangeStr = System.getProperty(COSParser.SYSPROP_EOFLOOKUPRANGE);
        if (eofLookupRangeStr != null) {
            try {
                setEOFLookupRange(Integer.parseInt(eofLookupRangeStr));
            } catch (NumberFormatException e) {
                LOG.warn("System property org.apache.pdfbox.pdfparser.nonSequentialPDFParser.eofLookupRange does not contain an integer value, but: '" + eofLookupRangeStr + OperatorName.SHOW_TEXT_LINE);
            }
        }
        this.document = new COSDocument();
    }

    private void initialParse() throws IOException {
        COSDictionary trailer = null;
        boolean rebuildTrailer = false;
        try {
            long startXRefOffset = getStartxrefOffset();
            if (startXRefOffset > 0) {
                trailer = parseXref(startXRefOffset);
            } else if (isLenient()) {
                rebuildTrailer = true;
            }
        } catch (IOException exception) {
            if (isLenient()) {
                rebuildTrailer = true;
            } else {
                throw exception;
            }
        }
        if (rebuildTrailer) {
            trailer = rebuildTrailer();
        }
        COSBase rootObject = parseTrailerValuesDynamically(trailer);
        if (rootObject instanceof COSDictionary) {
            parseDictObjects((COSDictionary) rootObject, (COSName[]) null);
        }
        this.initialParseDone = true;
    }

    public void parse() throws IOException {
        try {
            if (!parseFDFHeader()) {
                throw new IOException("Error: Header doesn't contain versioninfo");
            }
            initialParse();
            if (0 != 0 && this.document != null) {
                IOUtils.closeQuietly(this.document);
                this.document = null;
            }
        } catch (Throwable th) {
            if (1 != 0 && this.document != null) {
                IOUtils.closeQuietly(this.document);
                this.document = null;
            }
            throw th;
        }
    }
}
