package com.aowagie.text.pdf;

import com.aowagie.text.DocWriter;
import com.aowagie.text.Document;
import com.aowagie.text.DocumentException;
import com.aowagie.text.ExceptionConverter;
import com.aowagie.text.Image;
import com.aowagie.text.ImgJBIG2;
import com.aowagie.text.ImgWMF;
import com.aowagie.text.Rectangle;
import com.aowagie.text.Table;
import com.aowagie.text.pdf.collection.PdfCollection;
import com.aowagie.text.pdf.events.PdfPageEventForwarder;
import com.aowagie.text.pdf.interfaces.PdfAnnotations;
import com.aowagie.text.pdf.interfaces.PdfDocumentActions;
import com.aowagie.text.pdf.interfaces.PdfEncryptionSettings;
import com.aowagie.text.pdf.interfaces.PdfPageActions;
import com.aowagie.text.pdf.interfaces.PdfRunDirection;
import com.aowagie.text.pdf.interfaces.PdfVersion;
import com.aowagie.text.pdf.interfaces.PdfViewerPreferences;
import com.aowagie.text.pdf.interfaces.PdfXConformance;
import com.aowagie.text.pdf.internal.PdfVersionImp;
import com.aowagie.text.pdf.internal.PdfXConformanceImp;
import com.aowagie.text.xml.xmp.XmpWriter;
import java.awt.Color;
import java.awt.color.ICC_Profile;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.security.cert.Certificate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.TreeSet;
import org.apache.pdfbox.contentstream.operator.OperatorName;

/* JADX INFO: loaded from: autofirma.jar:com/aowagie/text/pdf/PdfWriter.class */
public class PdfWriter extends DocWriter implements PdfViewerPreferences, PdfEncryptionSettings, PdfVersion, PdfDocumentActions, PdfPageActions, PdfXConformance, PdfRunDirection, PdfAnnotations {
    static final int GENERATION_MAX = 65535;
    protected PdfDocument pdf;
    private PdfContentByte directContent;
    private PdfContentByte directContentUnder;
    protected PdfBody body;
    private PdfDictionary extraCatalog;
    protected PdfPages root;
    private final ArrayList pageReferences;
    private int currentPageNumber;
    private PdfName tabs;
    private PdfPageEvent pageEvent;
    protected int prevxref;
    protected List newBookmarks;
    public static final char VERSION_1_2 = '2';
    public static final char VERSION_1_3 = '3';
    public static final char VERSION_1_4 = '4';
    public static final char VERSION_1_5 = '5';
    public static final char VERSION_1_6 = '6';
    public static final char VERSION_1_7 = '7';
    protected PdfVersionImp pdf_version;
    public static final int PageLayoutSinglePage = 1;
    public static final int PageLayoutOneColumn = 2;
    public static final int PageLayoutTwoColumnLeft = 4;
    public static final int PageLayoutTwoColumnRight = 8;
    public static final int PageLayoutTwoPageLeft = 16;
    public static final int PageLayoutTwoPageRight = 32;
    public static final int PageModeUseNone = 64;
    public static final int PageModeUseOutlines = 128;
    public static final int PageModeUseThumbs = 256;
    public static final int PageModeFullScreen = 512;
    public static final int PageModeUseOC = 1024;
    public static final int PageModeUseAttachments = 2048;
    public static final int HideToolbar = 4096;
    public static final int HideMenubar = 8192;
    public static final int HideWindowUI = 16384;
    public static final int FitWindow = 32768;
    public static final int CenterWindow = 65536;
    public static final int DisplayDocTitle = 131072;
    public static final int NonFullScreenPageModeUseNone = 262144;
    public static final int NonFullScreenPageModeUseOutlines = 524288;
    public static final int NonFullScreenPageModeUseThumbs = 1048576;
    public static final int NonFullScreenPageModeUseOC = 2097152;
    public static final int DirectionL2R = 4194304;
    public static final int DirectionR2L = 8388608;
    public static final int PrintScalingNone = 16777216;
    protected byte[] xmpMetadata;
    public static final int PDFXNONE = 0;
    public static final int PDFX1A2001 = 1;
    public static final int PDFX32002 = 2;
    public static final int PDFA1A = 3;
    public static final int PDFA1B = 4;
    public static final int PDFA2A = 5;
    public static final int PDFA2B = 6;
    private final PdfXConformanceImp pdfxConformance;
    static final int STANDARD_ENCRYPTION_40 = 0;
    static final int STANDARD_ENCRYPTION_128 = 1;
    static final int ENCRYPTION_AES_128 = 2;
    public static final int ENCRYPTION_AES_256_V3 = 4;
    static final int ENCRYPTION_MASK = 7;
    static final int DO_NOT_ENCRYPT_METADATA = 8;
    static final int EMBEDDED_FILES_ONLY = 24;
    static final int ALLOW_PRINTING = 2052;
    static final int ALLOW_MODIFY_CONTENTS = 8;
    static final int ALLOW_COPY = 16;
    static final int ALLOW_MODIFY_ANNOTATIONS = 32;
    static final int ALLOW_FILL_IN = 256;
    static final int ALLOW_SCREENREADERS = 512;
    static final int ALLOW_ASSEMBLY = 1024;
    static final int ALLOW_DEGRADED_PRINTING = 4;
    protected PdfEncryption crypto;
    protected boolean fullCompression;
    protected int compressionLevel;
    private final LinkedHashMap documentFonts;
    private int fontNumber;
    private final HashMap formXObjects;
    private int formXObjectsCounter;
    private final HashMap importedPages;
    protected PdfReaderInstance currentPdfReaderInstance;
    private final HashMap documentColors;
    private int colorNumber;
    private final HashMap documentPatterns;
    private int patternNumber;
    private final HashMap documentShadingPatterns;
    private final HashMap documentShadings;
    private final HashMap documentExtGState;
    private final HashMap documentProperties;
    private boolean tagged;
    private PdfStructureTreeRoot structureTreeRoot;
    protected HashSet documentOCG;
    private final ArrayList documentOCGorder;
    protected PdfOCProperties OCProperties;
    protected PdfArray OCGRadioGroup;
    protected PdfArray OCGLocked;
    private PdfDictionary group;
    private static final float SPACE_CHAR_RATIO_DEFAULT = 2.5f;
    private float spaceCharRatio;
    public static final int RUN_DIRECTION_DEFAULT = 0;
    static final int RUN_DIRECTION_NO_BIDI = 1;
    static final int RUN_DIRECTION_LTR = 2;
    static final int RUN_DIRECTION_RTL = 3;
    private int runDirection;
    private float userunit;
    private final PdfDictionary defaultColorspace;
    private final HashMap documentSpotPatterns;
    private ColorDetails patternColorspaceRGB;
    private ColorDetails patternColorspaceGRAY;
    private ColorDetails patternColorspaceCMYK;
    private final PdfDictionary imageDictionary;
    private final HashMap images;
    private final HashMap JBIG2Globals;
    private boolean userProperties;
    private boolean rgbTransparencyBlending;
    public static final PdfName PDF_VERSION_1_2 = new PdfName("1.2");
    public static final PdfName PDF_VERSION_1_3 = new PdfName("1.3");
    public static final PdfName PDF_VERSION_1_4 = new PdfName("1.4");
    public static final PdfName PDF_VERSION_1_5 = new PdfName("1.5");
    public static final PdfName PDF_VERSION_1_6 = new PdfName("1.6");
    public static final PdfName PDF_VERSION_1_7 = new PdfName("1.7");
    static final PdfName DOCUMENT_CLOSE = PdfName.WC;
    static final PdfName WILL_SAVE = PdfName.WS;
    static final PdfName DID_SAVE = PdfName.DS;
    static final PdfName WILL_PRINT = PdfName.WP;
    static final PdfName DID_PRINT = PdfName.DP;
    private static final PdfName PAGE_OPEN = PdfName.O;
    private static final PdfName PAGE_CLOSE = PdfName.C;

    public static PdfWriter getInstance(Document document, OutputStream os) throws DocumentException {
        PdfDocument pdf = new PdfDocument(new GregorianCalendar());
        document.addDocListener(pdf);
        PdfWriter writer = new PdfWriter(pdf, os);
        pdf.addWriter(writer);
        return writer;
    }

    /* JADX INFO: loaded from: autofirma.jar:com/aowagie/text/pdf/PdfWriter$PdfBody.class */
    static class PdfBody {
        private static final int OBJSINSTREAM = 200;
        private int refnum;
        private int position;
        private final PdfWriter writer;
        private ByteBuffer index;
        private ByteBuffer streamObjects;
        private int currentObjNum;
        private int numObj = 0;
        private final TreeSet xrefs = new TreeSet();

        /* JADX INFO: loaded from: autofirma.jar:com/aowagie/text/pdf/PdfWriter$PdfBody$PdfCrossReference.class */
        private static class PdfCrossReference implements Comparable {
            private final int type;
            private final int offset;
            private final int refnum;
            private final int generation;

            private PdfCrossReference(int refnum, int offset, int generation) {
                this.type = 0;
                this.offset = offset;
                this.refnum = refnum;
                this.generation = generation;
            }

            private PdfCrossReference(int refnum, int offset) {
                this.type = 1;
                this.offset = offset;
                this.refnum = refnum;
                this.generation = 0;
            }

            private PdfCrossReference(int type, int refnum, int offset, int generation) {
                this.type = type;
                this.offset = offset;
                this.refnum = refnum;
                this.generation = generation;
            }

            /* JADX INFO: Access modifiers changed from: private */
            public int getRefnum() {
                return this.refnum;
            }

            /* JADX INFO: Access modifiers changed from: private */
            public void toPdf(OutputStream os) throws IOException {
                StringBuffer off = new StringBuffer("0000000000").append(this.offset);
                off.delete(0, off.length() - 10);
                StringBuffer gen = new StringBuffer("00000").append(this.generation);
                gen.delete(0, gen.length() - 5);
                off.append(' ').append(gen).append(this.generation == 65535 ? " f \n" : " n \n");
                os.write(DocWriter.getISOBytes(off.toString()));
            }

            /* JADX INFO: Access modifiers changed from: private */
            public void toPdf(int midSize, OutputStream os) throws IOException {
                os.write((byte) this.type);
                while (true) {
                    midSize--;
                    if (midSize >= 0) {
                        os.write((byte) ((this.offset >>> (8 * midSize)) & 255));
                    } else {
                        os.write((byte) ((this.generation >>> 8) & 255));
                        os.write((byte) (this.generation & 255));
                        return;
                    }
                }
            }

            @Override // java.lang.Comparable
            public int compareTo(Object o) {
                PdfCrossReference other = (PdfCrossReference) o;
                if (this.refnum < other.refnum) {
                    return -1;
                }
                return this.refnum == other.refnum ? 0 : 1;
            }

            public boolean equals(Object obj) {
                if (obj instanceof PdfCrossReference) {
                    PdfCrossReference other = (PdfCrossReference) obj;
                    return this.refnum == other.refnum;
                }
                return false;
            }

            public int hashCode() {
                return this.refnum;
            }
        }

        PdfBody(PdfWriter writer) {
            this.xrefs.add(new PdfCrossReference(0, 0, 65535));
            this.position = writer.getOs().getCounter();
            this.refnum = 1;
            this.writer = writer;
        }

        void setRefnum(int refnum) {
            this.refnum = refnum;
        }

        private PdfCrossReference addToObjStm(PdfObject obj, int nObj) throws IOException {
            if (this.numObj >= 200) {
                flushObjStm();
            }
            if (this.index == null) {
                this.index = new ByteBuffer();
                this.streamObjects = new ByteBuffer();
                this.currentObjNum = getIndirectReferenceNumber();
                this.numObj = 0;
            }
            int p = this.streamObjects.size();
            int idx = this.numObj;
            this.numObj = idx + 1;
            PdfEncryption enc = this.writer.crypto;
            this.writer.crypto = null;
            obj.toPdf(this.writer, this.streamObjects);
            this.writer.crypto = enc;
            this.streamObjects.append(' ');
            this.index.append(nObj).append(' ').append(p).append(' ');
            return new PdfCrossReference(2, nObj, this.currentObjNum, idx);
        }

        /* JADX INFO: Access modifiers changed from: private */
        public void flushObjStm() throws IOException {
            if (this.numObj == 0) {
                return;
            }
            int first = this.index.size();
            this.index.append(this.streamObjects);
            PdfStream stream = new PdfStream(this.index.toByteArray());
            stream.flateCompress(this.writer.getCompressionLevel());
            stream.put(PdfName.TYPE, PdfName.OBJSTM);
            stream.put(PdfName.N, new PdfNumber(this.numObj));
            stream.put(PdfName.FIRST, new PdfNumber(first));
            add(stream, this.currentObjNum);
            this.index = null;
            this.streamObjects = null;
            this.numObj = 0;
        }

        PdfIndirectObject add(PdfObject object) throws IOException {
            return add(object, getIndirectReferenceNumber());
        }

        /* JADX INFO: Access modifiers changed from: private */
        public PdfIndirectObject add(PdfObject object, boolean inObjStm) throws IOException {
            return add(object, getIndirectReferenceNumber(), inObjStm);
        }

        PdfIndirectReference getPdfIndirectReference() {
            return new PdfIndirectReference(0, getIndirectReferenceNumber());
        }

        int getIndirectReferenceNumber() {
            int n = this.refnum;
            this.refnum = n + 1;
            this.xrefs.add(new PdfCrossReference(n, 0, 65535));
            return n;
        }

        PdfIndirectObject add(PdfObject object, PdfIndirectReference ref) throws IOException {
            return add(object, ref.getNumber());
        }

        /* JADX INFO: Access modifiers changed from: private */
        public PdfIndirectObject add(PdfObject object, PdfIndirectReference ref, boolean inObjStm) throws IOException {
            return add(object, ref.getNumber(), inObjStm);
        }

        /* JADX INFO: Access modifiers changed from: private */
        public PdfIndirectObject add(PdfObject object, int refNumber) throws IOException {
            return add(object, refNumber, true);
        }

        /* JADX INFO: Access modifiers changed from: private */
        public PdfIndirectObject add(PdfObject object, int refNumber, boolean inObjStm) throws IOException {
            if (inObjStm && object.canBeInObjStm() && this.writer.isFullCompression()) {
                PdfCrossReference pxref = addToObjStm(object, refNumber);
                PdfIndirectObject indirect = new PdfIndirectObject(refNumber, object, this.writer);
                if (!this.xrefs.add(pxref)) {
                    this.xrefs.remove(pxref);
                    this.xrefs.add(pxref);
                }
                return indirect;
            }
            PdfIndirectObject indirect2 = new PdfIndirectObject(refNumber, object, this.writer);
            PdfCrossReference pxref2 = new PdfCrossReference(refNumber, this.position);
            if (!this.xrefs.add(pxref2)) {
                this.xrefs.remove(pxref2);
                this.xrefs.add(pxref2);
            }
            indirect2.writeTo(this.writer.getOs());
            this.position = this.writer.getOs().getCounter();
            return indirect2;
        }

        int offset() {
            return this.position;
        }

        int size() {
            return Math.max(((PdfCrossReference) this.xrefs.last()).getRefnum() + 1, this.refnum);
        }

        void writeCrossReferenceTable(OutputStream os, PdfIndirectReference root, PdfIndirectReference info, PdfIndirectReference encryption, PdfObject fileID, int prevxref) throws IOException {
            int refNumber = 0;
            if (this.writer.isFullCompression()) {
                flushObjStm();
                refNumber = getIndirectReferenceNumber();
                this.xrefs.add(new PdfCrossReference(refNumber, this.position));
            }
            PdfCrossReference entry = (PdfCrossReference) this.xrefs.first();
            int first = entry.getRefnum();
            int len = 0;
            ArrayList sections = new ArrayList();
            for (PdfCrossReference entry2 : this.xrefs) {
                if (first + len == entry2.getRefnum()) {
                    len++;
                } else {
                    sections.add(Integer.valueOf(first));
                    sections.add(Integer.valueOf(len));
                    first = entry2.getRefnum();
                    len = 1;
                }
            }
            sections.add(Integer.valueOf(first));
            sections.add(Integer.valueOf(len));
            if (this.writer.isFullCompression()) {
                int mid = 4;
                int mask = -16777216;
                while (mid > 1 && (mask & this.position) == 0) {
                    mask >>>= 8;
                    mid--;
                }
                ByteBuffer buf = new ByteBuffer();
                for (PdfCrossReference entry3 : this.xrefs) {
                    entry3.toPdf(mid, buf);
                }
                PdfStream xr = new PdfStream(buf.toByteArray());
                xr.flateCompress(this.writer.getCompressionLevel());
                xr.put(PdfName.SIZE, new PdfNumber(size()));
                xr.put(PdfName.ROOT, root);
                if (info != null) {
                    xr.put(PdfName.INFO, info);
                }
                if (encryption != null) {
                    xr.put(PdfName.ENCRYPT, encryption);
                }
                if (fileID != null) {
                    xr.put(PdfName.ID, fileID);
                }
                xr.put(PdfName.W, new PdfArray(new int[]{1, mid, 2}));
                xr.put(PdfName.TYPE, PdfName.XREF);
                PdfArray idx = new PdfArray();
                for (int k = 0; k < sections.size(); k++) {
                    idx.add(new PdfNumber(((Integer) sections.get(k)).intValue()));
                }
                xr.put(PdfName.INDEX, idx);
                if (prevxref > 0) {
                    xr.put(PdfName.PREV, new PdfNumber(prevxref));
                }
                PdfEncryption enc = this.writer.crypto;
                this.writer.crypto = null;
                PdfIndirectObject indirect = new PdfIndirectObject(refNumber, xr, this.writer);
                indirect.writeTo(this.writer.getOs());
                this.writer.crypto = enc;
                return;
            }
            os.write(DocWriter.getISOBytes("xref\n"));
            Iterator i = this.xrefs.iterator();
            for (int k2 = 0; k2 < sections.size(); k2 += 2) {
                int first2 = ((Integer) sections.get(k2)).intValue();
                int len2 = ((Integer) sections.get(k2 + 1)).intValue();
                os.write(DocWriter.getISOBytes(String.valueOf(first2)));
                os.write(DocWriter.getISOBytes(" "));
                os.write(DocWriter.getISOBytes(String.valueOf(len2)));
                os.write(10);
                while (true) {
                    int i2 = len2;
                    len2--;
                    if (i2 > 0) {
                        PdfCrossReference entry4 = (PdfCrossReference) i.next();
                        entry4.toPdf(os);
                    }
                }
            }
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:com/aowagie/text/pdf/PdfWriter$PdfTrailer.class */
    static class PdfTrailer extends PdfDictionary {
        private final int offset;

        PdfTrailer(int size, int offset, PdfIndirectReference root, PdfIndirectReference info, PdfIndirectReference encryption, PdfObject fileID, int prevxref) {
            this.offset = offset;
            put(PdfName.SIZE, new PdfNumber(size));
            put(PdfName.ROOT, root);
            if (info != null) {
                put(PdfName.INFO, info);
            }
            if (encryption != null) {
                put(PdfName.ENCRYPT, encryption);
            }
            if (fileID != null) {
                put(PdfName.ID, fileID);
            }
            if (prevxref > 0) {
                put(PdfName.PREV, new PdfNumber(prevxref));
            }
        }

        @Override // com.aowagie.text.pdf.PdfDictionary, com.aowagie.text.pdf.PdfObject
        public void toPdf(PdfWriter writer, OutputStream os) throws IOException {
            os.write(DocWriter.getISOBytes("trailer\n"));
            super.toPdf(null, os);
            os.write(DocWriter.getISOBytes("\nstartxref\n"));
            os.write(DocWriter.getISOBytes(String.valueOf(this.offset)));
            os.write(DocWriter.getISOBytes("\n%%EOF\n"));
        }
    }

    protected PdfWriter() {
        this.root = new PdfPages(this);
        this.pageReferences = new ArrayList();
        this.currentPageNumber = 1;
        this.tabs = null;
        this.prevxref = 0;
        this.pdf_version = new PdfVersionImp();
        this.xmpMetadata = null;
        this.pdfxConformance = new PdfXConformanceImp();
        this.fullCompression = false;
        this.compressionLevel = -1;
        this.documentFonts = new LinkedHashMap();
        this.fontNumber = 1;
        this.formXObjects = new LinkedHashMap();
        this.formXObjectsCounter = 1;
        this.importedPages = new LinkedHashMap();
        this.documentColors = new LinkedHashMap();
        this.colorNumber = 1;
        this.documentPatterns = new LinkedHashMap();
        this.patternNumber = 1;
        this.documentShadingPatterns = new LinkedHashMap();
        this.documentShadings = new LinkedHashMap();
        this.documentExtGState = new LinkedHashMap();
        this.documentProperties = new LinkedHashMap();
        this.tagged = false;
        this.documentOCG = new HashSet();
        this.documentOCGorder = new ArrayList();
        this.OCGRadioGroup = new PdfArray();
        this.OCGLocked = new PdfArray();
        this.spaceCharRatio = SPACE_CHAR_RATIO_DEFAULT;
        this.runDirection = 1;
        this.userunit = ColumnText.GLOBAL_SPACE_CHAR_RATIO;
        this.defaultColorspace = new PdfDictionary();
        this.documentSpotPatterns = new LinkedHashMap();
        this.imageDictionary = new PdfDictionary();
        this.images = new LinkedHashMap();
        this.JBIG2Globals = new LinkedHashMap();
    }

    protected PdfWriter(PdfDocument document, OutputStream os) {
        super(document, os);
        this.root = new PdfPages(this);
        this.pageReferences = new ArrayList();
        this.currentPageNumber = 1;
        this.tabs = null;
        this.prevxref = 0;
        this.pdf_version = new PdfVersionImp();
        this.xmpMetadata = null;
        this.pdfxConformance = new PdfXConformanceImp();
        this.fullCompression = false;
        this.compressionLevel = -1;
        this.documentFonts = new LinkedHashMap();
        this.fontNumber = 1;
        this.formXObjects = new LinkedHashMap();
        this.formXObjectsCounter = 1;
        this.importedPages = new LinkedHashMap();
        this.documentColors = new LinkedHashMap();
        this.colorNumber = 1;
        this.documentPatterns = new LinkedHashMap();
        this.patternNumber = 1;
        this.documentShadingPatterns = new LinkedHashMap();
        this.documentShadings = new LinkedHashMap();
        this.documentExtGState = new LinkedHashMap();
        this.documentProperties = new LinkedHashMap();
        this.tagged = false;
        this.documentOCG = new HashSet();
        this.documentOCGorder = new ArrayList();
        this.OCGRadioGroup = new PdfArray();
        this.OCGLocked = new PdfArray();
        this.spaceCharRatio = SPACE_CHAR_RATIO_DEFAULT;
        this.runDirection = 1;
        this.userunit = ColumnText.GLOBAL_SPACE_CHAR_RATIO;
        this.defaultColorspace = new PdfDictionary();
        this.documentSpotPatterns = new LinkedHashMap();
        this.imageDictionary = new PdfDictionary();
        this.images = new LinkedHashMap();
        this.JBIG2Globals = new LinkedHashMap();
        this.pdf = document;
        this.directContent = new PdfContentByte(this);
        this.directContentUnder = new PdfContentByte(this);
    }

    PdfDocument getPdfDocument() {
        return this.pdf;
    }

    public PdfDictionary getInfo() {
        return this.pdf.getInfo();
    }

    public float getVerticalPosition(boolean ensureNewLine) {
        return this.pdf.getVerticalPosition(ensureNewLine);
    }

    public void setInitialLeading(float leading) throws DocumentException {
        if (this.open) {
            throw new DocumentException("You can't set the initial leading if the document is already open.");
        }
        this.pdf.setLeading(leading);
    }

    public PdfContentByte getDirectContent() {
        if (!this.open) {
            throw new RuntimeException("The document is not open.");
        }
        return this.directContent;
    }

    public PdfContentByte getDirectContentUnder() {
        if (!this.open) {
            throw new RuntimeException("The document is not open.");
        }
        return this.directContentUnder;
    }

    void resetContent() {
        this.directContent.reset();
        this.directContentUnder.reset();
    }

    void addLocalDestinations(TreeMap dest) throws IOException {
        for (Map.Entry entry : dest.entrySet()) {
            String name = (String) entry.getKey();
            Object[] obj = (Object[]) entry.getValue();
            PdfDestination destination = (PdfDestination) obj[2];
            if (obj[1] == null) {
                obj[1] = getPdfIndirectReference();
            }
            if (destination == null) {
                addToBody(new PdfString("invalid_" + name), (PdfIndirectReference) obj[1]);
            } else {
                addToBody(destination, (PdfIndirectReference) obj[1]);
            }
        }
    }

    PdfIndirectObject addToBody(PdfObject object) throws IOException {
        PdfIndirectObject iobj = this.body.add(object);
        return iobj;
    }

    public PdfIndirectObject addToBody(PdfObject object, boolean inObjStm) throws IOException {
        PdfIndirectObject iobj = this.body.add(object, inObjStm);
        return iobj;
    }

    public PdfIndirectObject addToBody(PdfObject object, PdfIndirectReference ref) throws IOException {
        PdfIndirectObject iobj = this.body.add(object, ref);
        return iobj;
    }

    PdfIndirectObject addToBody(PdfObject object, PdfIndirectReference ref, boolean inObjStm) throws IOException {
        PdfIndirectObject iobj = this.body.add(object, ref, inObjStm);
        return iobj;
    }

    PdfIndirectObject addToBody(PdfObject object, int refNumber) throws IOException {
        PdfIndirectObject iobj = this.body.add(object, refNumber);
        return iobj;
    }

    PdfIndirectObject addToBody(PdfObject object, int refNumber, boolean inObjStm) throws IOException {
        PdfIndirectObject iobj = this.body.add(object, refNumber, inObjStm);
        return iobj;
    }

    public PdfIndirectReference getPdfIndirectReference() {
        return this.body.getPdfIndirectReference();
    }

    int getIndirectReferenceNumber() {
        return this.body.getIndirectReferenceNumber();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public OutputStreamCounter getOs() {
        return this.os;
    }

    protected PdfDictionary getCatalog(PdfIndirectReference rootObj) {
        PdfDictionary catalog = this.pdf.getCatalog(rootObj);
        if (this.tagged) {
            try {
                getStructureTreeRoot().buildTree();
                catalog.put(PdfName.STRUCTTREEROOT, this.structureTreeRoot.getReference());
                PdfDictionary mi = new PdfDictionary();
                mi.put(PdfName.MARKED, PdfBoolean.PDFTRUE);
                if (this.userProperties) {
                    mi.put(PdfName.USERPROPERTIES, PdfBoolean.PDFTRUE);
                }
                catalog.put(PdfName.MARKINFO, mi);
            } catch (Exception e) {
                throw new ExceptionConverter(e);
            }
        }
        if (!this.documentOCG.isEmpty()) {
            fillOCProperties(false);
            catalog.put(PdfName.OCPROPERTIES, this.OCProperties);
        }
        return catalog;
    }

    public PdfDictionary getExtraCatalog() {
        if (this.extraCatalog == null) {
            this.extraCatalog = new PdfDictionary();
        }
        return this.extraCatalog;
    }

    public PdfIndirectReference getPageReference(int page) {
        PdfIndirectReference ref;
        int page2 = page - 1;
        if (page2 < 0) {
            throw new IndexOutOfBoundsException("The page numbers start at 1.");
        }
        if (page2 < this.pageReferences.size()) {
            ref = (PdfIndirectReference) this.pageReferences.get(page2);
            if (ref == null) {
                ref = this.body.getPdfIndirectReference();
                this.pageReferences.set(page2, ref);
            }
        } else {
            int empty = page2 - this.pageReferences.size();
            for (int k = 0; k < empty; k++) {
                this.pageReferences.add(null);
            }
            ref = this.body.getPdfIndirectReference();
            this.pageReferences.add(ref);
        }
        return ref;
    }

    public int getPageNumber() {
        return this.pdf.getPageNumber();
    }

    PdfIndirectReference getCurrentPage() {
        return getPageReference(this.currentPageNumber);
    }

    public int getCurrentPageNumber() {
        return this.currentPageNumber;
    }

    public void setTabs(PdfName tabs) {
        this.tabs = tabs;
    }

    public PdfName getTabs() {
        return this.tabs;
    }

    PdfIndirectReference add(PdfPage page, PdfContents contents) throws PdfException {
        if (!this.open) {
            throw new PdfException("The document isn't open.");
        }
        try {
            PdfIndirectObject object = addToBody(contents);
            page.add(object.getIndirectReference());
            if (this.group != null) {
                page.put(PdfName.GROUP, this.group);
                this.group = null;
            } else if (this.rgbTransparencyBlending) {
                PdfDictionary pp = new PdfDictionary();
                pp.put(PdfName.TYPE, PdfName.GROUP);
                pp.put(PdfName.S, PdfName.TRANSPARENCY);
                pp.put(PdfName.CS, PdfName.DEVICERGB);
                page.put(PdfName.GROUP, pp);
            }
            this.root.addPage(page);
            this.currentPageNumber++;
            return null;
        } catch (IOException ioe) {
            throw new ExceptionConverter(ioe);
        }
    }

    public void setPageEvent(PdfPageEvent event) {
        if (event == null) {
            this.pageEvent = null;
            return;
        }
        if (this.pageEvent == null) {
            this.pageEvent = event;
            return;
        }
        if (this.pageEvent instanceof PdfPageEventForwarder) {
            ((PdfPageEventForwarder) this.pageEvent).addPageEvent(event);
            return;
        }
        PdfPageEventForwarder forward = new PdfPageEventForwarder();
        forward.addPageEvent(this.pageEvent);
        forward.addPageEvent(event);
        this.pageEvent = forward;
    }

    public PdfPageEvent getPageEvent() {
        return this.pageEvent;
    }

    @Override // com.aowagie.text.DocWriter, com.aowagie.text.DocListener
    public void open() {
        super.open();
        try {
            this.pdf_version.writeHeader(this.os);
            this.body = new PdfBody(this);
            if (this.pdfxConformance.isPdfX32002()) {
                PdfDictionary sec = new PdfDictionary();
                sec.put(PdfName.GAMMA, new PdfArray(new float[]{2.2f, 2.2f, 2.2f}));
                sec.put(PdfName.MATRIX, new PdfArray(new float[]{0.4124f, 0.2126f, 0.0193f, 0.3576f, 0.7152f, 0.1192f, 0.1805f, 0.0722f, 0.9505f}));
                sec.put(PdfName.WHITEPOINT, new PdfArray(new float[]{0.9505f, 1.0f, 1.089f}));
                PdfArray arr = new PdfArray(PdfName.CALRGB);
                arr.add(sec);
                setDefaultColorspace(PdfName.DEFAULTRGB, addToBody(arr).getIndirectReference());
            }
        } catch (IOException ioe) {
            throw new ExceptionConverter(ioe);
        }
    }

    @Override // com.aowagie.text.DocWriter, com.aowagie.text.DocListener
    public void close() {
        PdfObject fileID;
        if (this.open) {
            if (this.currentPageNumber - 1 != this.pageReferences.size()) {
                throw new RuntimeException("The page " + this.pageReferences.size() + " was requested but the document has only " + (this.currentPageNumber - 1) + " pages.");
            }
            this.pdf.close();
            try {
                addSharedObjectsToBody();
                PdfIndirectReference rootRef = this.root.writePageTree();
                PdfDictionary catalog = getCatalog(rootRef);
                if (this.xmpMetadata != null) {
                    PdfStream xmp = new PdfStream(this.xmpMetadata);
                    xmp.put(PdfName.TYPE, PdfName.METADATA);
                    xmp.put(PdfName.SUBTYPE, PdfName.XML);
                    if (this.crypto != null && !this.crypto.isMetadataEncrypted()) {
                        PdfArray ar = new PdfArray();
                        ar.add(PdfName.CRYPT);
                        xmp.put(PdfName.FILTER, ar);
                    }
                    catalog.put(PdfName.METADATA, this.body.add(xmp).getIndirectReference());
                }
                if (isPdfX()) {
                    this.pdfxConformance.completeInfoDictionary(getInfo());
                    this.pdfxConformance.completeExtraCatalog(getExtraCatalog());
                }
                if (this.extraCatalog != null) {
                    catalog.mergeDifferent(this.extraCatalog);
                }
                writeOutlines(catalog, false);
                PdfIndirectObject indirectCatalog = addToBody((PdfObject) catalog, false);
                PdfIndirectObject infoObj = addToBody((PdfObject) getInfo(), false);
                PdfIndirectReference encryption = null;
                this.body.flushObjStm();
                if (this.crypto != null) {
                    PdfIndirectObject encryptionObject = addToBody((PdfObject) this.crypto.getEncryptionDictionary(), false);
                    encryption = encryptionObject.getIndirectReference();
                    fileID = this.crypto.getFileID();
                } else {
                    fileID = PdfEncryption.createInfoId(PdfEncryption.createDocumentId());
                }
                this.body.writeCrossReferenceTable(this.os, indirectCatalog.getIndirectReference(), infoObj.getIndirectReference(), encryption, fileID, this.prevxref);
                if (this.fullCompression) {
                    this.os.write(getISOBytes("startxref\n"));
                    this.os.write(getISOBytes(String.valueOf(this.body.offset())));
                    this.os.write(getISOBytes("\n%%EOF\n"));
                } else {
                    PdfTrailer trailer = new PdfTrailer(this.body.size(), this.body.offset(), indirectCatalog.getIndirectReference(), infoObj.getIndirectReference(), encryption, fileID, this.prevxref);
                    trailer.toPdf(this, this.os);
                }
                super.close();
            } catch (IOException ioe) {
                throw new ExceptionConverter(ioe);
            }
        }
    }

    protected void addSharedObjectsToBody() throws IOException {
        for (FontDetails details : this.documentFonts.values()) {
            details.writeFont(this);
        }
        for (Object[] objs : this.formXObjects.values()) {
            PdfTemplate template = (PdfTemplate) objs[1];
            if (template == null || !(template.getIndirectReference() instanceof PRIndirectReference)) {
                if (template != null && template.getType() == 1) {
                    addToBody(template.getFormXObject(this.compressionLevel), template.getIndirectReference());
                }
            }
        }
        Iterator it = this.importedPages.values().iterator();
        while (it.hasNext()) {
            this.currentPdfReaderInstance = (PdfReaderInstance) it.next();
            this.currentPdfReaderInstance.writeAllPages();
        }
        this.currentPdfReaderInstance = null;
        for (ColorDetails color : this.documentColors.values()) {
            addToBody(color.getSpotColor(this), color.getIndirectReference());
        }
        for (PdfPatternPainter pat : this.documentPatterns.keySet()) {
            addToBody(pat.getPattern(this.compressionLevel), pat.getIndirectReference());
        }
        for (PdfShadingPattern shadingPattern : this.documentShadingPatterns.keySet()) {
            shadingPattern.addToBody();
        }
        for (PdfShading shading : this.documentShadings.keySet()) {
            shading.addToBody();
        }
        for (Map.Entry entry : this.documentExtGState.entrySet()) {
            PdfDictionary gstate = (PdfDictionary) entry.getKey();
            PdfObject[] obj = (PdfObject[]) entry.getValue();
            addToBody(gstate, (PdfIndirectReference) obj[1]);
        }
        for (Map.Entry entry2 : this.documentProperties.entrySet()) {
            Object prop = entry2.getKey();
            PdfObject[] obj2 = (PdfObject[]) entry2.getValue();
            if (prop instanceof PdfLayerMembership) {
                PdfLayerMembership layer = (PdfLayerMembership) prop;
                addToBody(layer.getPdfObject(), layer.getRef());
            } else if ((prop instanceof PdfDictionary) && !(prop instanceof PdfLayer)) {
                addToBody((PdfDictionary) prop, (PdfIndirectReference) obj2[1]);
            }
        }
        for (PdfOCG layer2 : this.documentOCG) {
            addToBody(layer2.getPdfObject(), layer2.getRef());
        }
    }

    public PdfOutline getRootOutline() {
        return this.directContent.getRootOutline();
    }

    public void setOutlines(List outlines) {
        this.newBookmarks = outlines;
    }

    protected void writeOutlines(PdfDictionary catalog, boolean namedAsNames) throws IOException {
        if (this.newBookmarks == null || this.newBookmarks.isEmpty()) {
            return;
        }
        PdfDictionary top = new PdfDictionary();
        PdfIndirectReference topRef = getPdfIndirectReference();
        Object[] kids = SimpleBookmark.iterateOutlines(this, topRef, this.newBookmarks, namedAsNames);
        top.put(PdfName.FIRST, (PdfIndirectReference) kids[0]);
        top.put(PdfName.LAST, (PdfIndirectReference) kids[1]);
        top.put(PdfName.COUNT, new PdfNumber(((Integer) kids[2]).intValue()));
        addToBody(top, topRef);
        catalog.put(PdfName.OUTLINES, topRef);
    }

    @Override // com.aowagie.text.pdf.interfaces.PdfVersion
    public void setPdfVersion(char version) {
        this.pdf_version.setPdfVersion(version);
    }

    @Override // com.aowagie.text.pdf.interfaces.PdfVersion
    public void setAtLeastPdfVersion(char version) {
        this.pdf_version.setAtLeastPdfVersion(version);
    }

    @Override // com.aowagie.text.pdf.interfaces.PdfVersion
    public void setPdfVersion(PdfName version) {
        this.pdf_version.setPdfVersion(version);
    }

    @Override // com.aowagie.text.pdf.interfaces.PdfVersion
    public void addDeveloperExtension(PdfDeveloperExtension de) {
        this.pdf_version.addDeveloperExtension(de);
    }

    PdfVersionImp getPdfVersion() {
        return this.pdf_version;
    }

    public void setViewerPreferences(int preferences) {
        this.pdf.setViewerPreferences(preferences);
    }

    public void addViewerPreference(PdfName key, PdfObject value) {
        this.pdf.addViewerPreference(key, value);
    }

    public void setPageLabels(PdfPageLabels pageLabels) {
        this.pdf.setPageLabels(pageLabels);
    }

    private void addJavaScript(PdfAction js) {
        this.pdf.addJavaScript(js);
    }

    void addJavaScript(String code, boolean unicode) {
        addJavaScript(PdfAction.javaScript(code, this, unicode));
    }

    private void addJavaScript(String name, PdfAction js) {
        this.pdf.addJavaScript(name, js);
    }

    private void addJavaScript(String name, String code, boolean unicode) {
        addJavaScript(name, PdfAction.javaScript(code, this, unicode));
    }

    public void addFileAttachment(String description, byte[] fileStore, String file, String fileDisplay) throws IOException {
        addFileAttachment(description, PdfFileSpecification.fileEmbedded(this, file, fileDisplay, fileStore));
    }

    void addFileAttachment(String description, PdfFileSpecification fs) throws IOException {
        this.pdf.addFileAttachment(description, fs);
    }

    public void setOpenAction(String name) {
        this.pdf.setOpenAction(name);
    }

    public void setOpenAction(PdfAction action) {
        this.pdf.setOpenAction(action);
    }

    public void setAdditionalAction(PdfName actionType, PdfAction action) throws DocumentException {
        if (!actionType.equals(DOCUMENT_CLOSE) && !actionType.equals(WILL_SAVE) && !actionType.equals(DID_SAVE) && !actionType.equals(WILL_PRINT) && !actionType.equals(DID_PRINT)) {
            throw new DocumentException("Invalid additional action type: " + actionType.toString());
        }
        this.pdf.addAdditionalAction(actionType, action);
    }

    public void setCollection(PdfCollection collection) {
        setAtLeastPdfVersion('7');
        this.pdf.setCollection(collection);
    }

    @Override // com.aowagie.text.pdf.interfaces.PdfAnnotations
    public PdfAcroForm getAcroForm() {
        return this.pdf.getAcroForm();
    }

    public void addAnnotation(PdfAnnotation annot) {
        this.pdf.addAnnotation(annot);
    }

    void addAnnotation(PdfAnnotation annot, int page) {
        addAnnotation(annot);
    }

    public void setSigFlags(int f) {
        this.pdf.setSigFlags(f);
    }

    public void setXmpMetadata(byte[] xmpMetadata) {
        this.xmpMetadata = xmpMetadata;
    }

    public void setPageXmpMetadata(byte[] xmpMetadata) {
        this.pdf.setXmpMetadata(xmpMetadata);
    }

    public void createXmpMetadata() {
        setXmpMetadata(createXmpMetadataBytes());
    }

    private byte[] createXmpMetadataBytes() {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try {
            XmpWriter xmp = new XmpWriter(baos, this.pdf.getInfo(), this.pdfxConformance.getPDFXConformance());
            xmp.close();
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
        return baos.toByteArray();
    }

    @Override // com.aowagie.text.pdf.interfaces.PdfXConformance
    public void setPDFXConformance(int pdfx) {
        if (this.pdfxConformance.getPDFXConformance() == pdfx) {
            return;
        }
        if (this.pdf.isOpen()) {
            throw new PdfXConformanceException("PDFX conformance can only be set before opening the document.");
        }
        if (this.crypto != null) {
            throw new PdfXConformanceException("A PDFX conforming document cannot be encrypted.");
        }
        if (pdfx == 3 || pdfx == 4 || pdfx == 5 || pdfx == 6) {
            setPdfVersion('4');
        } else if (pdfx != 0) {
            setPdfVersion('3');
        }
        this.pdfxConformance.setPDFXConformance(pdfx);
    }

    @Override // com.aowagie.text.pdf.interfaces.PdfXConformance
    public int getPDFXConformance() {
        return this.pdfxConformance.getPDFXConformance();
    }

    @Override // com.aowagie.text.pdf.interfaces.PdfXConformance
    public boolean isPdfX() {
        return this.pdfxConformance.isPdfX();
    }

    public void setOutputIntents(String outputConditionIdentifier, String outputCondition, String registryName, String info, ICC_Profile colorProfile) throws IOException {
        PdfName intentSubtype;
        getExtraCatalog();
        PdfDictionary out = new PdfDictionary(PdfName.OUTPUTINTENT);
        if (outputCondition != null) {
            out.put(PdfName.OUTPUTCONDITION, new PdfString(outputCondition, PdfObject.TEXT_UNICODE));
        }
        if (outputConditionIdentifier != null) {
            out.put(PdfName.OUTPUTCONDITIONIDENTIFIER, new PdfString(outputConditionIdentifier, PdfObject.TEXT_UNICODE));
        }
        if (registryName != null) {
            out.put(PdfName.REGISTRYNAME, new PdfString(registryName, PdfObject.TEXT_UNICODE));
        }
        if (info != null) {
            out.put(PdfName.INFO, new PdfString(info, PdfObject.TEXT_UNICODE));
        }
        if (colorProfile != null) {
            PdfStream stream = new PdfICCBased(colorProfile, this.compressionLevel);
            out.put(PdfName.DESTOUTPUTPROFILE, addToBody(stream).getIndirectReference());
        }
        if (this.pdfxConformance.isPdfA1() || "PDFA/1".equals(outputCondition)) {
            intentSubtype = PdfName.GTS_PDFA1;
        } else {
            intentSubtype = PdfName.GTS_PDFX;
        }
        out.put(PdfName.S, intentSubtype);
        this.extraCatalog.put(PdfName.OUTPUTINTENTS, new PdfArray(out));
    }

    private void setOutputIntents(String outputConditionIdentifier, String outputCondition, String registryName, String info, byte[] destOutputProfile) throws IOException {
        ICC_Profile colorProfile = destOutputProfile == null ? null : ICC_Profile.getInstance(destOutputProfile);
        setOutputIntents(outputConditionIdentifier, outputCondition, registryName, info, colorProfile);
    }

    private static String getNameString(PdfDictionary dic, PdfName key) {
        PdfObject obj = PdfReader.getPdfObject(dic.get(key));
        if (obj == null || !obj.isString()) {
            return null;
        }
        return ((PdfString) obj).toUnicodeString();
    }

    PdfEncryption getEncryption() {
        return this.crypto;
    }

    @Override // com.aowagie.text.pdf.interfaces.PdfEncryptionSettings
    public void setEncryption(byte[] userPassword, byte[] ownerPassword, int permissions, int encryptionType) throws DocumentException {
        if (this.pdf.isOpen()) {
            throw new DocumentException("Encryption can only be added before opening the document.");
        }
        this.crypto = new PdfEncryption();
        this.crypto.setCryptoMode(encryptionType, 0);
        this.crypto.setupAllKeys(userPassword, ownerPassword, permissions);
    }

    @Override // com.aowagie.text.pdf.interfaces.PdfEncryptionSettings
    public void setEncryption(Certificate[] certs, int[] permissions, int encryptionType) throws DocumentException {
        if (this.pdf.isOpen()) {
            throw new DocumentException("Encryption can only be added before opening the document.");
        }
        this.crypto = new PdfEncryption();
        if (certs != null) {
            for (int i = 0; i < certs.length; i++) {
                this.crypto.addRecipient(certs[i], permissions[i]);
            }
        }
        this.crypto.setCryptoMode(encryptionType, 0);
        this.crypto.getEncryptionDictionary();
    }

    public boolean isFullCompression() {
        return this.fullCompression;
    }

    void setFullCompression() {
        this.fullCompression = true;
        setAtLeastPdfVersion('5');
    }

    public int getCompressionLevel() {
        return this.compressionLevel;
    }

    public void setCompressionLevel(int compressionLevel) {
        if (compressionLevel < 0 || compressionLevel > 9) {
            this.compressionLevel = -1;
        } else {
            this.compressionLevel = compressionLevel;
        }
    }

    FontDetails addSimple(BaseFont bf) {
        if (bf.getFontType() == 4) {
            StringBuilder sbAppend = new StringBuilder().append("F");
            int i = this.fontNumber;
            this.fontNumber = i + 1;
            return new FontDetails(new PdfName(sbAppend.append(i).toString()), ((DocumentFont) bf).getIndirectReference(), bf);
        }
        FontDetails ret = (FontDetails) this.documentFonts.get(bf);
        if (ret == null) {
            PdfXConformanceImp.checkPDFXConformance(this, 4, bf);
            StringBuilder sbAppend2 = new StringBuilder().append("F");
            int i2 = this.fontNumber;
            this.fontNumber = i2 + 1;
            ret = new FontDetails(new PdfName(sbAppend2.append(i2).toString()), this.body.getPdfIndirectReference(), bf);
            this.documentFonts.put(bf, ret);
        }
        return ret;
    }

    void eliminateFontSubset(PdfDictionary fonts) {
        for (FontDetails ft : this.documentFonts.values()) {
            if (fonts.get(ft.getFontName()) != null) {
                ft.setSubset(false);
            }
        }
    }

    PdfName addDirectTemplateSimple(PdfTemplate template, PdfName forcedName) {
        PdfName name;
        PdfIndirectReference ref = template.getIndirectReference();
        Object[] obj = (Object[]) this.formXObjects.get(ref);
        try {
            if (obj == null) {
                if (forcedName == null) {
                    name = new PdfName("Xf" + this.formXObjectsCounter);
                    this.formXObjectsCounter++;
                } else {
                    name = forcedName;
                }
                if (template.getType() == 2) {
                    PdfImportedPage ip = (PdfImportedPage) template;
                    PdfReader r = ip.getPdfReaderInstance().getReader();
                    if (!this.importedPages.containsKey(r)) {
                        this.importedPages.put(r, ip.getPdfReaderInstance());
                    }
                    template = null;
                }
                this.formXObjects.put(ref, new Object[]{name, template});
            } else {
                name = (PdfName) obj[0];
            }
            return name;
        } catch (Exception e) {
            throw new ExceptionConverter(e);
        }
    }

    void releaseTemplate(PdfTemplate tp) throws IOException {
        PdfIndirectReference ref = tp.getIndirectReference();
        Object[] objs = (Object[]) this.formXObjects.get(ref);
        if (objs == null || objs[1] == null) {
            return;
        }
        PdfTemplate template = (PdfTemplate) objs[1];
        if (!(template.getIndirectReference() instanceof PRIndirectReference) && template.getType() == 1) {
            addToBody(template.getFormXObject(this.compressionLevel), template.getIndirectReference());
            objs[1] = null;
        }
    }

    public PdfImportedPage getImportedPage(PdfReader reader, int pageNumber) {
        PdfReaderInstance inst = (PdfReaderInstance) this.importedPages.get(reader);
        if (inst == null) {
            inst = reader.getPdfReaderInstance(this);
            this.importedPages.put(reader, inst);
        }
        return inst.getImportedPage(pageNumber);
    }

    public void freeReader(PdfReader reader) throws IOException {
        this.currentPdfReaderInstance = (PdfReaderInstance) this.importedPages.get(reader);
        if (this.currentPdfReaderInstance == null) {
            return;
        }
        this.currentPdfReaderInstance.writeAllPages();
        this.currentPdfReaderInstance = null;
        this.importedPages.remove(reader);
    }

    public int getCurrentDocumentSize() {
        return this.body.offset() + (this.body.size() * 20) + 72;
    }

    protected int getNewObjectNumber(PdfReader reader, int number, int generation) {
        return this.currentPdfReaderInstance.getNewObjectNumber(number, generation);
    }

    RandomAccessFileOrArray getReaderFile(PdfReader reader) {
        return this.currentPdfReaderInstance.getReaderFile();
    }

    PdfName getColorspaceName() {
        StringBuilder sbAppend = new StringBuilder().append(OperatorName.STROKING_COLORSPACE);
        int i = this.colorNumber;
        this.colorNumber = i + 1;
        return new PdfName(sbAppend.append(i).toString());
    }

    ColorDetails addSimple(PdfSpotColor spc) {
        ColorDetails ret = (ColorDetails) this.documentColors.get(spc);
        if (ret == null) {
            ret = new ColorDetails(getColorspaceName(), this.body.getPdfIndirectReference(), spc);
            this.documentColors.put(spc, ret);
        }
        return ret;
    }

    PdfName addSimplePattern(PdfPatternPainter painter) {
        PdfName name = (PdfName) this.documentPatterns.get(painter);
        if (name == null) {
            try {
                name = new PdfName("P" + this.patternNumber);
                this.patternNumber++;
                this.documentPatterns.put(painter, name);
            } catch (Exception e) {
                throw new ExceptionConverter(e);
            }
        }
        return name;
    }

    void addSimpleShadingPattern(PdfShadingPattern shading) {
        if (!this.documentShadingPatterns.containsKey(shading)) {
            shading.setName(this.patternNumber);
            this.patternNumber++;
            this.documentShadingPatterns.put(shading, null);
            addSimpleShading(shading.getShading());
        }
    }

    void addSimpleShading(PdfShading shading) {
        if (!this.documentShadings.containsKey(shading)) {
            this.documentShadings.put(shading, null);
            shading.setName(this.documentShadings.size());
        }
    }

    PdfObject[] addSimpleExtGState(PdfDictionary gstate) {
        if (!this.documentExtGState.containsKey(gstate)) {
            PdfXConformanceImp.checkPDFXConformance(this, 6, gstate);
            this.documentExtGState.put(gstate, new PdfObject[]{new PdfName("GS" + (this.documentExtGState.size() + 1)), getPdfIndirectReference()});
        }
        return (PdfObject[]) this.documentExtGState.get(gstate);
    }

    PdfObject[] addSimpleProperty(Object prop, PdfIndirectReference refi) {
        if (!this.documentProperties.containsKey(prop)) {
            if (prop instanceof PdfOCG) {
                PdfXConformanceImp.checkPDFXConformance(this, 7, null);
            }
            this.documentProperties.put(prop, new PdfObject[]{new PdfName("Pr" + (this.documentProperties.size() + 1)), refi});
        }
        return (PdfObject[]) this.documentProperties.get(prop);
    }

    boolean propertyExists(Object prop) {
        return this.documentProperties.containsKey(prop);
    }

    public void setTagged() {
        if (this.open) {
            throw new IllegalArgumentException("Tagging must be set before opening the document.");
        }
        this.tagged = true;
    }

    public boolean isTagged() {
        return this.tagged;
    }

    public PdfStructureTreeRoot getStructureTreeRoot() {
        if (this.tagged && this.structureTreeRoot == null) {
            this.structureTreeRoot = new PdfStructureTreeRoot(this);
        }
        return this.structureTreeRoot;
    }

    public PdfOCProperties getOCProperties() {
        fillOCProperties(true);
        return this.OCProperties;
    }

    private static void getOCGOrder(PdfArray order, PdfLayer layer) {
        if (!layer.isOnPanel()) {
            return;
        }
        if (layer.getTitle() == null) {
            order.add(layer.getRef());
        }
        ArrayList children = layer.getChildren();
        if (children == null) {
            return;
        }
        PdfArray kids = new PdfArray();
        if (layer.getTitle() != null) {
            kids.add(new PdfString(layer.getTitle(), PdfObject.TEXT_UNICODE));
        }
        for (int k = 0; k < children.size(); k++) {
            getOCGOrder(kids, (PdfLayer) children.get(k));
        }
        if (kids.size() > 0) {
            order.add(kids);
        }
    }

    private void addASEvent(PdfName event, PdfName category) {
        PdfArray arr = new PdfArray();
        for (PdfLayer layer : this.documentOCG) {
            PdfDictionary usage = (PdfDictionary) layer.get(PdfName.USAGE);
            if (usage != null && usage.get(category) != null) {
                arr.add(layer.getRef());
            }
        }
        if (arr.size() == 0) {
            return;
        }
        PdfDictionary d = (PdfDictionary) this.OCProperties.get(PdfName.D);
        PdfArray arras = (PdfArray) d.get(PdfName.AS);
        if (arras == null) {
            arras = new PdfArray();
            d.put(PdfName.AS, arras);
        }
        PdfDictionary as = new PdfDictionary();
        as.put(PdfName.EVENT, event);
        as.put(PdfName.CATEGORY, new PdfArray(category));
        as.put(PdfName.OCGS, arr);
        arras.add(as);
    }

    protected void fillOCProperties(boolean erase) {
        if (this.OCProperties == null) {
            this.OCProperties = new PdfOCProperties();
        }
        if (erase) {
            this.OCProperties.remove(PdfName.OCGS);
            this.OCProperties.remove(PdfName.D);
        }
        if (this.OCProperties.get(PdfName.OCGS) == null) {
            PdfArray gr = new PdfArray();
            Iterator it = this.documentOCG.iterator();
            while (it.hasNext()) {
                gr.add(((PdfLayer) it.next()).getRef());
            }
            this.OCProperties.put(PdfName.OCGS, gr);
        }
        if (this.OCProperties.get(PdfName.D) != null) {
            return;
        }
        ArrayList docOrder = new ArrayList(this.documentOCGorder);
        Iterator it2 = docOrder.iterator();
        while (it2.hasNext()) {
            if (((PdfLayer) it2.next()).getParent() != null) {
                it2.remove();
            }
        }
        PdfArray order = new PdfArray();
        Iterator it3 = docOrder.iterator();
        while (it3.hasNext()) {
            getOCGOrder(order, (PdfLayer) it3.next());
        }
        PdfDictionary d = new PdfDictionary();
        this.OCProperties.put(PdfName.D, d);
        d.put(PdfName.ORDER, order);
        PdfArray gr2 = new PdfArray();
        for (PdfLayer layer : this.documentOCG) {
            if (!layer.isOn()) {
                gr2.add(layer.getRef());
            }
        }
        if (gr2.size() > 0) {
            d.put(PdfName.OFF, gr2);
        }
        if (this.OCGRadioGroup.size() > 0) {
            d.put(PdfName.RBGROUPS, this.OCGRadioGroup);
        }
        if (this.OCGLocked.size() > 0) {
            d.put(PdfName.LOCKED, this.OCGLocked);
        }
        addASEvent(PdfName.VIEW, PdfName.ZOOM);
        addASEvent(PdfName.VIEW, PdfName.VIEW);
        addASEvent(PdfName.PRINT, PdfName.PRINT);
        addASEvent(PdfName.EXPORT, PdfName.EXPORT);
        d.put(PdfName.LISTMODE, PdfName.VISIBLEPAGES);
    }

    void registerLayer(PdfOCG layer) {
        PdfXConformanceImp.checkPDFXConformance(this, 7, null);
        if (layer instanceof PdfLayer) {
            PdfLayer la = (PdfLayer) layer;
            if (la.getTitle() == null) {
                if (!this.documentOCG.contains(layer)) {
                    this.documentOCG.add(layer);
                    this.documentOCGorder.add(layer);
                    return;
                }
                return;
            }
            this.documentOCGorder.add(layer);
            return;
        }
        throw new IllegalArgumentException("Only PdfLayer is accepted.");
    }

    public Rectangle getPageSize() {
        return this.pdf.getPageSize();
    }

    public void setCropBoxSize(Rectangle crop) {
        this.pdf.setCropBoxSize(crop);
    }

    public void setPageEmpty(boolean pageEmpty) {
        this.pdf.setPageEmpty(pageEmpty);
    }

    public void setPageAction(PdfName actionType, PdfAction action) throws DocumentException {
        if (!actionType.equals(PAGE_OPEN) && !actionType.equals(PAGE_CLOSE)) {
            throw new DocumentException("Invalid page additional action type: " + actionType.toString());
        }
        this.pdf.setPageAction(actionType, action);
    }

    public void setDuration(int seconds) {
        this.pdf.setDuration(seconds);
    }

    public void setTransition(PdfTransition transition) {
        this.pdf.setTransition(transition);
    }

    public void setThumbnail(Image image) throws DocumentException {
        this.pdf.setThumbnail(image);
    }

    public PdfDictionary getGroup() {
        return this.group;
    }

    public void setGroup(PdfDictionary group) {
        this.group = group;
    }

    public float getSpaceCharRatio() {
        return this.spaceCharRatio;
    }

    public void setSpaceCharRatio(float spaceCharRatio) {
        if (spaceCharRatio < 0.001f) {
            this.spaceCharRatio = 0.001f;
        } else {
            this.spaceCharRatio = spaceCharRatio;
        }
    }

    @Override // com.aowagie.text.pdf.interfaces.PdfRunDirection
    public void setRunDirection(int runDirection) {
        if (runDirection < 1 || runDirection > 3) {
            throw new RuntimeException("Invalid run direction: " + runDirection);
        }
        this.runDirection = runDirection;
    }

    @Override // com.aowagie.text.pdf.interfaces.PdfRunDirection
    public int getRunDirection() {
        return this.runDirection;
    }

    public float getUserunit() {
        return this.userunit;
    }

    public void setUserunit(float userunit) throws DocumentException {
        if (userunit < 1.0f || userunit > 75000.0f) {
            throw new DocumentException("UserUnit should be a value between 1 and 75000.");
        }
        this.userunit = userunit;
        setAtLeastPdfVersion('6');
    }

    public PdfDictionary getDefaultColorspace() {
        return this.defaultColorspace;
    }

    private void setDefaultColorspace(PdfName key, PdfObject cs) {
        if (cs == null || cs.isNull()) {
            this.defaultColorspace.remove(key);
        }
        this.defaultColorspace.put(key, cs);
    }

    ColorDetails addSimplePatternColorspace(Color color) {
        int type = ExtendedColor.getType(color);
        if (type == 4 || type == 5) {
            throw new RuntimeException("An uncolored tile pattern can not have another pattern or shading as color.");
        }
        try {
            switch (type) {
                case 0:
                    if (this.patternColorspaceRGB == null) {
                        this.patternColorspaceRGB = new ColorDetails(getColorspaceName(), this.body.getPdfIndirectReference(), null);
                        PdfArray array = new PdfArray(PdfName.PATTERN);
                        array.add(PdfName.DEVICERGB);
                        addToBody(array, this.patternColorspaceRGB.getIndirectReference());
                    }
                    return this.patternColorspaceRGB;
                case 1:
                    if (this.patternColorspaceGRAY == null) {
                        this.patternColorspaceGRAY = new ColorDetails(getColorspaceName(), this.body.getPdfIndirectReference(), null);
                        PdfArray array2 = new PdfArray(PdfName.PATTERN);
                        array2.add(PdfName.DEVICEGRAY);
                        addToBody(array2, this.patternColorspaceGRAY.getIndirectReference());
                    }
                    return this.patternColorspaceGRAY;
                case 2:
                    if (this.patternColorspaceCMYK == null) {
                        this.patternColorspaceCMYK = new ColorDetails(getColorspaceName(), this.body.getPdfIndirectReference(), null);
                        PdfArray array3 = new PdfArray(PdfName.PATTERN);
                        array3.add(PdfName.DEVICECMYK);
                        addToBody(array3, this.patternColorspaceCMYK.getIndirectReference());
                    }
                    return this.patternColorspaceCMYK;
                case 3:
                    ColorDetails details = addSimple(((SpotColor) color).getPdfSpotColor());
                    ColorDetails patternDetails = (ColorDetails) this.documentSpotPatterns.get(details);
                    if (patternDetails == null) {
                        patternDetails = new ColorDetails(getColorspaceName(), this.body.getPdfIndirectReference(), null);
                        PdfArray array4 = new PdfArray(PdfName.PATTERN);
                        array4.add(details.getIndirectReference());
                        addToBody(array4, patternDetails.getIndirectReference());
                        this.documentSpotPatterns.put(details, patternDetails);
                    }
                    return patternDetails;
                default:
                    throw new RuntimeException("Invalid color type in PdfWriter.addSimplePatternColorspace().");
            }
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    public boolean isStrictImageSequence() {
        return this.pdf.isStrictImageSequence();
    }

    public void setStrictImageSequence(boolean strictImageSequence) {
        this.pdf.setStrictImageSequence(strictImageSequence);
    }

    PdfName addDirectImageSimple(Image image) throws DocumentException {
        return addDirectImageSimple(image, null);
    }

    private PdfName addDirectImageSimple(Image image, PdfIndirectReference fixedRef) throws DocumentException {
        PdfName name;
        byte[] globals;
        if (this.images.containsKey(image.getMySerialId())) {
            name = (PdfName) this.images.get(image.getMySerialId());
        } else {
            if (image.isImgTemplate()) {
                name = new PdfName("img" + this.images.size());
                if (image instanceof ImgWMF) {
                    try {
                        ImgWMF wmf = (ImgWMF) image;
                        wmf.readWMF(PdfTemplate.createTemplate(this, ColumnText.GLOBAL_SPACE_CHAR_RATIO, ColumnText.GLOBAL_SPACE_CHAR_RATIO));
                    } catch (Exception e) {
                        throw new DocumentException(e);
                    }
                }
            } else {
                PdfIndirectReference dref = image.getDirectReference();
                if (dref != null) {
                    PdfName rname = new PdfName("img" + this.images.size());
                    this.images.put(image.getMySerialId(), rname);
                    this.imageDictionary.put(rname, dref);
                    return rname;
                }
                Image maskImage = image.getImageMask();
                PdfIndirectReference maskRef = null;
                if (maskImage != null) {
                    PdfName mname = (PdfName) this.images.get(maskImage.getMySerialId());
                    maskRef = getImageReference(mname);
                }
                PdfImage i = new PdfImage(image, "img" + this.images.size(), maskRef);
                if ((image instanceof ImgJBIG2) && (globals = ((ImgJBIG2) image).getGlobalBytes()) != null) {
                    PdfDictionary decodeparms = new PdfDictionary();
                    decodeparms.put(PdfName.JBIG2GLOBALS, getReferenceJBIG2Globals(globals));
                    i.put(PdfName.DECODEPARMS, decodeparms);
                }
                if (image.hasICCProfile()) {
                    PdfICCBased icc = new PdfICCBased(image.getICCProfile(), image.getCompressionLevel());
                    PdfIndirectReference iccRef = add(icc);
                    PdfArray iccArray = new PdfArray();
                    iccArray.add(PdfName.ICCBASED);
                    iccArray.add(iccRef);
                    PdfArray colorspace = i.getAsArray(PdfName.COLORSPACE);
                    if (colorspace != null && colorspace.size() > 1 && PdfName.INDEXED.equals(colorspace.getPdfObject(0))) {
                        colorspace.set(1, iccArray);
                    } else {
                        i.put(PdfName.COLORSPACE, iccArray);
                    }
                }
                add(i, fixedRef);
                name = i.name();
            }
            this.images.put(image.getMySerialId(), name);
        }
        return name;
    }

    private PdfIndirectReference add(PdfImage pdfImage, PdfIndirectReference fixedRef) throws PdfException {
        if (!this.imageDictionary.contains(pdfImage.name())) {
            PdfXConformanceImp.checkPDFXConformance(this, 5, pdfImage);
            if (fixedRef instanceof PRIndirectReference) {
                PRIndirectReference r2 = (PRIndirectReference) fixedRef;
                fixedRef = new PdfIndirectReference(0, getNewObjectNumber(r2.getReader(), r2.getNumber(), r2.getGeneration()));
            }
            try {
                if (fixedRef == null) {
                    fixedRef = addToBody(pdfImage).getIndirectReference();
                } else {
                    addToBody(pdfImage, fixedRef);
                }
                this.imageDictionary.put(pdfImage.name(), fixedRef);
                return fixedRef;
            } catch (IOException ioe) {
                throw new ExceptionConverter(ioe);
            }
        }
        return (PdfIndirectReference) this.imageDictionary.get(pdfImage.name());
    }

    PdfIndirectReference getImageReference(PdfName name) {
        return (PdfIndirectReference) this.imageDictionary.get(name);
    }

    private PdfIndirectReference add(PdfICCBased icc) {
        try {
            PdfIndirectObject object = addToBody(icc);
            return object.getIndirectReference();
        } catch (IOException ioe) {
            throw new ExceptionConverter(ioe);
        }
    }

    protected PdfIndirectReference getReferenceJBIG2Globals(byte[] content) {
        if (content == null) {
            return null;
        }
        for (PdfStream stream : this.JBIG2Globals.keySet()) {
            if (Arrays.equals(content, stream.getBytes())) {
                return (PdfIndirectReference) this.JBIG2Globals.get(stream);
            }
        }
        PdfStream stream2 = new PdfStream(content);
        try {
            PdfIndirectObject ref = addToBody(stream2);
            this.JBIG2Globals.put(stream2, ref.getIndirectReference());
            return ref.getIndirectReference();
        } catch (IOException e) {
            return null;
        }
    }

    private boolean fitsPage(Table table, float margin) {
        return this.pdf.bottom(table) > this.pdf.indentBottom() + margin;
    }

    public boolean isUserProperties() {
        return this.userProperties;
    }

    public void setUserProperties(boolean userProperties) {
        this.userProperties = userProperties;
    }

    public boolean isRgbTransparencyBlending() {
        return this.rgbTransparencyBlending;
    }

    public void setRgbTransparencyBlending(boolean rgbTransparencyBlending) {
        this.rgbTransparencyBlending = rgbTransparencyBlending;
    }
}
