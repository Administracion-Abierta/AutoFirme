package org.apache.pdfbox.pdfparser;

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
import org.apache.pdfbox.io.RandomAccessRead;
import org.apache.pdfbox.io.ScratchFile;
import org.apache.pdfbox.pdmodel.PDDocument;

/* JADX INFO: loaded from: autofirma.jar:org/apache/pdfbox/pdfparser/PDFParser.class */
public class PDFParser extends COSParser {
    private static final Log LOG = LogFactory.getLog(PDFParser.class);

    public PDFParser(RandomAccessRead source) throws IOException {
        this(source, "", ScratchFile.getMainMemoryOnlyInstance());
    }

    public PDFParser(RandomAccessRead source, ScratchFile scratchFile) throws IOException {
        this(source, "", scratchFile);
    }

    public PDFParser(RandomAccessRead source, String decryptionPassword) throws IOException {
        this(source, decryptionPassword, ScratchFile.getMainMemoryOnlyInstance());
    }

    public PDFParser(RandomAccessRead source, String decryptionPassword, ScratchFile scratchFile) throws IOException {
        this(source, decryptionPassword, null, null, scratchFile);
    }

    public PDFParser(RandomAccessRead source, String decryptionPassword, InputStream keyStore, String alias) throws IOException {
        this(source, decryptionPassword, keyStore, alias, ScratchFile.getMainMemoryOnlyInstance());
    }

    public PDFParser(RandomAccessRead source, String decryptionPassword, InputStream keyStore, String alias, ScratchFile scratchFile) throws IOException {
        super(source, decryptionPassword, keyStore, alias);
        this.fileLen = source.length();
        init(scratchFile);
    }

    private void init(ScratchFile scratchFile) {
        String eofLookupRangeStr = System.getProperty(COSParser.SYSPROP_EOFLOOKUPRANGE);
        if (eofLookupRangeStr != null) {
            try {
                setEOFLookupRange(Integer.parseInt(eofLookupRangeStr));
            } catch (NumberFormatException e) {
                LOG.warn("System property org.apache.pdfbox.pdfparser.nonSequentialPDFParser.eofLookupRange does not contain an integer value, but: '" + eofLookupRangeStr + OperatorName.SHOW_TEXT_LINE);
            }
        }
        this.document = new COSDocument(scratchFile);
    }

    public PDDocument getPDDocument() throws IOException {
        PDDocument doc = new PDDocument(getDocument(), this.source, getAccessPermission());
        doc.setEncryptionDictionary(getEncryption());
        return doc;
    }

    protected void initialParse() throws IOException {
        COSDictionary trailer = retrieveTrailer();
        COSBase base = parseTrailerValuesDynamically(trailer);
        if (!(base instanceof COSDictionary)) {
            throw new IOException("Expected root dictionary, but got this: " + base);
        }
        COSDictionary root = (COSDictionary) base;
        if (isLenient() && !root.containsKey(COSName.TYPE)) {
            root.setItem(COSName.TYPE, (COSBase) COSName.CATALOG);
        }
        parseDictObjects(root, (COSName[]) null);
        COSBase infoBase = trailer.getDictionaryObject(COSName.INFO);
        if (infoBase instanceof COSDictionary) {
            parseDictObjects((COSDictionary) infoBase, (COSName[]) null);
        }
        checkPages(root);
        if (!(root.getDictionaryObject(COSName.PAGES) instanceof COSDictionary)) {
            throw new IOException("Page tree root must be a dictionary");
        }
        this.document.setDecrypted();
        this.initialParseDone = true;
    }

    public void parse() throws IOException {
        try {
            if (!parsePDFHeader() && !parseFDFHeader()) {
                throw new IOException("Error: Header doesn't contain versioninfo");
            }
            if (!this.initialParseDone) {
                initialParse();
            }
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
