package com.aowagie.text.pdf;

import com.aowagie.text.Document;
import com.aowagie.text.DocumentException;
import com.aowagie.text.ExceptionConverter;
import com.aowagie.text.Image;
import com.aowagie.text.Rectangle;
import com.aowagie.text.exceptions.BadPasswordException;
import com.aowagie.text.pdf.internal.PdfViewerPreferencesImp;
import com.aowagie.text.xml.xmp.XmpReader;
import com.twelvemonkeys.imageio.metadata.tiff.TIFF;
import com.twelvemonkeys.imageio.metadata.xmp.XMP;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import org.xml.sax.SAXException;

/* JADX INFO: loaded from: autofirma.jar:com/aowagie/text/pdf/PdfStamperImp.class */
public class PdfStamperImp extends PdfWriter {
    private PdfObject pdfFileID;
    private final HashMap readers2intrefs;
    private final HashMap readers2file;
    private final RandomAccessFileOrArray file;
    PdfReader reader;
    private final IntHashtable myXref;
    private final HashMap pagesToContent;
    private boolean closed;
    private boolean rotateContents;
    private AcroFields acroFields;
    private boolean flat;
    private boolean flatFreeText;
    private final int[] namePtr;
    private final HashSet partialFlattening;
    private boolean useVp;
    private final PdfViewerPreferencesImp viewerPreferences;
    private final HashMap fieldTemplates;
    private boolean fieldsAdded;
    private int sigFlags;
    private final boolean append;
    private IntHashtable marked;
    private final int initialXrefSize;
    private PdfAction openAction;

    PdfStamperImp(PdfReader reader, OutputStream os, char pdfVersion, boolean append, Calendar globalDate) throws IOException, DocumentException {
        super(new PdfDocument(globalDate), os);
        this.pdfFileID = null;
        this.readers2intrefs = new LinkedHashMap();
        this.readers2file = new LinkedHashMap();
        this.myXref = new IntHashtable();
        this.pagesToContent = new LinkedHashMap();
        this.closed = false;
        this.rotateContents = true;
        this.flat = false;
        this.flatFreeText = false;
        this.namePtr = new int[]{0};
        this.partialFlattening = new HashSet();
        this.useVp = false;
        this.viewerPreferences = new PdfViewerPreferencesImp();
        this.fieldTemplates = new LinkedHashMap();
        this.fieldsAdded = false;
        this.sigFlags = 0;
        if (!reader.isOpenedWithFullPermissions()) {
            throw new BadPasswordException("PdfReader not opened with owner password");
        }
        if (reader.isTampered()) {
            throw new DocumentException("The original document was reused. Read it again from file.");
        }
        reader.setTampered(true);
        this.reader = reader;
        this.file = reader.getSafeFile();
        this.append = append;
        if (append) {
            if (reader.isRebuilt()) {
                throw new DocumentException("Append mode requires a document without errors even if recovery was possible.");
            }
            if (reader.isEncrypted()) {
                this.crypto = new PdfEncryption(reader.getDecrypt());
            }
            this.pdf_version.setAppendmode(true);
            this.file.reOpen();
            byte[] buf = new byte[8192];
            while (true) {
                int n = this.file.read(buf);
                if (n <= 0) {
                    break;
                } else {
                    this.os.write(buf, 0, n);
                }
            }
            this.file.close();
            this.prevxref = reader.getLastXref();
            reader.setAppendable(true);
        } else if (pdfVersion == 0) {
            super.setPdfVersion(reader.getPdfVersion());
        } else {
            super.setPdfVersion(pdfVersion);
        }
        super.open();
        this.pdf.addWriter(this);
        if (append) {
            this.body.setRefnum(reader.getXrefSize());
            this.marked = new IntHashtable();
            if (reader.isNewXrefType()) {
                this.fullCompression = true;
            }
            if (reader.isHybridXref()) {
                this.fullCompression = false;
            }
        }
        this.initialXrefSize = reader.getXrefSize();
    }

    void close(Map moreInfo, Calendar globalDate) throws IOException {
        PdfStream xmp;
        PdfObject fileID;
        PdfIndirectReference info;
        if (this.closed) {
            return;
        }
        if (this.useVp) {
            this.reader.setViewerPreferences(this.viewerPreferences);
            markUsed(this.reader.getTrailer().get(PdfName.ROOT));
        }
        if (this.flat) {
            flatFields();
        }
        if (this.flatFreeText) {
            flatFreeTextFields();
        }
        addFieldResources();
        PdfDictionary catalog = this.reader.getCatalog();
        PdfDictionary pages = (PdfDictionary) PdfReader.getPdfObject(catalog.get(PdfName.PAGES));
        pages.put(PdfName.ITXT, new PdfString(Document.getRelease()));
        markUsed(pages);
        PdfDictionary acroForm = (PdfDictionary) PdfReader.getPdfObject(catalog.get(PdfName.ACROFORM), this.reader.getCatalog());
        if (this.acroFields != null && this.acroFields.getXfa().isChanged()) {
            markUsed(acroForm);
            if (!this.flat) {
                this.acroFields.getXfa().setXfa(this);
            }
        }
        if (this.sigFlags != 0 && acroForm != null) {
            acroForm.put(PdfName.SIGFLAGS, new PdfNumber(this.sigFlags));
            markUsed(acroForm);
            markUsed(catalog);
        }
        this.closed = true;
        addSharedObjectsToBody();
        setOutlines();
        setJavaScript();
        addFileAttachments();
        if (this.openAction != null) {
            catalog.put(PdfName.OPENACTION, this.openAction);
        }
        if (this.pdf.pageLabels != null) {
            catalog.put(PdfName.PAGELABELS, this.pdf.pageLabels.getDictionary(this));
        }
        if (!this.documentOCG.isEmpty()) {
            fillOCProperties(false);
            PdfDictionary ocdict = catalog.getAsDict(PdfName.OCPROPERTIES);
            if (ocdict == null) {
                this.reader.getCatalog().put(PdfName.OCPROPERTIES, this.OCProperties);
            } else {
                ocdict.put(PdfName.OCGS, this.OCProperties.get(PdfName.OCGS));
                PdfDictionary ddict = ocdict.getAsDict(PdfName.D);
                if (ddict == null) {
                    ddict = new PdfDictionary();
                    ocdict.put(PdfName.D, ddict);
                }
                ddict.put(PdfName.ORDER, this.OCProperties.getAsDict(PdfName.D).get(PdfName.ORDER));
                ddict.put(PdfName.RBGROUPS, this.OCProperties.getAsDict(PdfName.D).get(PdfName.RBGROUPS));
                ddict.put(PdfName.OFF, this.OCProperties.getAsDict(PdfName.D).get(PdfName.OFF));
                ddict.put(PdfName.AS, this.OCProperties.getAsDict(PdfName.D).get(PdfName.AS));
            }
        }
        int skipInfo = -1;
        PRIndirectReference iInfo = (PRIndirectReference) this.reader.getTrailer().get(PdfName.INFO);
        PdfDictionary oldInfo = (PdfDictionary) PdfReader.getPdfObject(iInfo);
        if (iInfo != null) {
            skipInfo = iInfo.getNumber();
        }
        byte[] altMetadata = null;
        PdfObject xmpo = PdfReader.getPdfObject(catalog.get(PdfName.METADATA));
        if (xmpo != null && xmpo.isStream()) {
            altMetadata = PdfReader.getStreamBytesRaw((PRStream) xmpo);
            PdfReader.killIndirect(catalog.get(PdfName.METADATA));
        }
        if (this.xmpMetadata != null) {
            altMetadata = this.xmpMetadata;
        }
        PdfDate date = new PdfDate(globalDate);
        if (altMetadata != null) {
            try {
                XmpReader xmpr = new XmpReader(altMetadata);
                if (!xmpr.replace("rdf:Description", XMP.NS_XAP, "ModifyDate", date.getW3CDate())) {
                    xmpr.add("rdf:Description", XMP.NS_XAP, "xmp:ModifyDate", date.getW3CDate());
                }
                xmpr.replace("rdf:Description", XMP.NS_XAP, "MetadataDate", date.getW3CDate());
                xmp = new PdfStream(xmpr.serializeDoc());
            } catch (IOException e) {
                xmp = new PdfStream(altMetadata);
            } catch (SAXException e2) {
                xmp = new PdfStream(altMetadata);
            }
            xmp.put(PdfName.TYPE, PdfName.METADATA);
            xmp.put(PdfName.SUBTYPE, PdfName.XML);
            if (this.crypto != null && !this.crypto.isMetadataEncrypted()) {
                PdfArray ar = new PdfArray();
                ar.add(PdfName.CRYPT);
                xmp.put(PdfName.FILTER, ar);
            }
            if (this.append && xmpo != null) {
                this.body.add(xmp, xmpo.getIndRef());
            } else {
                catalog.put(PdfName.METADATA, this.body.add(xmp).getIndirectReference());
                markUsed(catalog);
            }
        }
        try {
            this.file.reOpen();
            alterContents();
            int rootN = ((PRIndirectReference) this.reader.trailer.get(PdfName.ROOT)).getNumber();
            if (this.append) {
                int[] keys = this.marked.getKeys();
                int length = keys.length;
                for (int i = 0; i < length; i++) {
                    int j = keys[i];
                    PdfObject obj = this.reader.getPdfObjectRelease(j);
                    if (obj != null && skipInfo != j && j < this.initialXrefSize) {
                        addToBody(obj, j, j != rootN);
                    }
                }
                for (int k = this.initialXrefSize; k < this.reader.getXrefSize(); k++) {
                    PdfObject obj2 = this.reader.getPdfObject(k);
                    if (obj2 != null) {
                        addToBody(obj2, getNewObjectNumber(this.reader, k, 0));
                    }
                }
            } else {
                int k2 = 1;
                while (k2 < this.reader.getXrefSize()) {
                    PdfObject obj3 = this.reader.getPdfObjectRelease(k2);
                    if (obj3 != null && skipInfo != k2) {
                        addToBody(obj3, getNewObjectNumber(this.reader, k2, 0), k2 != rootN);
                    }
                    k2++;
                }
            }
            try {
                this.file.close();
            } catch (Exception e3) {
            }
            PdfIndirectReference encryption = null;
            if (this.crypto != null) {
                if (this.append) {
                    encryption = this.reader.getCryptoRef();
                } else {
                    PdfIndirectObject encryptionObject = addToBody(this.crypto.getEncryptionDictionary(), false);
                    encryption = encryptionObject.getIndirectReference();
                }
                fileID = this.crypto.getFileID();
            } else {
                fileID = PdfEncryption.createInfoId(PdfEncryption.createDocumentId());
            }
            this.pdfFileID = fileID;
            PRIndirectReference iRoot = (PRIndirectReference) this.reader.trailer.get(PdfName.ROOT);
            PdfIndirectReference root = new PdfIndirectReference(0, getNewObjectNumber(this.reader, iRoot.getNumber(), 0));
            PdfDictionary newInfo = new PdfDictionary();
            if (oldInfo != null) {
                for (Object element : oldInfo.getKeys()) {
                    PdfName key = (PdfName) element;
                    newInfo.put(key, PdfReader.getPdfObject(oldInfo.get(key)));
                }
            }
            if (moreInfo != null) {
                for (Map.Entry entry : moreInfo.entrySet()) {
                    PdfName keyName = new PdfName((String) entry.getKey());
                    String value = (String) entry.getValue();
                    if (value == null) {
                        newInfo.remove(keyName);
                    } else {
                        newInfo.put(keyName, new PdfString(value, PdfObject.TEXT_UNICODE));
                    }
                }
            }
            newInfo.put(PdfName.MODDATE, date);
            if (!this.append || iInfo == null) {
                info = addToBody((PdfObject) newInfo, false).getIndirectReference();
            } else {
                info = addToBody((PdfObject) newInfo, iInfo.getNumber(), false).getIndirectReference();
            }
            this.body.writeCrossReferenceTable(this.os, root, info, encryption, fileID, this.prevxref);
            if (this.fullCompression) {
                this.os.write(getISOBytes("startxref\n"));
                this.os.write(getISOBytes(String.valueOf(this.body.offset())));
                this.os.write(getISOBytes("\n%%EOF\n"));
            } else {
                PdfWriter.PdfTrailer trailer = new PdfWriter.PdfTrailer(this.body.size(), this.body.offset(), root, info, encryption, fileID, this.prevxref);
                trailer.toPdf(this, this.os);
            }
            this.os.flush();
            if (isCloseStream()) {
                this.os.close();
            }
            this.reader.close();
        } catch (Throwable th) {
            try {
                this.file.close();
            } catch (Exception e4) {
            }
            throw th;
        }
    }

    private void applyRotation(PdfDictionary pageN, ByteBuffer out) {
        if (!this.rotateContents) {
        }
        Rectangle page = this.reader.getPageSizeWithRotation(pageN);
        int rotation = page.getRotation();
        switch (rotation) {
            case 90:
                out.append(PdfContents.ROTATE90);
                out.append(page.getTop());
                out.append(' ').append('0').append(PdfContents.ROTATEFINAL);
                break;
            case 180:
                out.append(PdfContents.ROTATE180);
                out.append(page.getRight());
                out.append(' ');
                out.append(page.getTop());
                out.append(PdfContents.ROTATEFINAL);
                break;
            case TIFF.TAG_IMAGE_DESCRIPTION /* 270 */:
                out.append(PdfContents.ROTATE270);
                out.append('0').append(' ');
                out.append(page.getRight());
                out.append(PdfContents.ROTATEFINAL);
                break;
        }
    }

    private void alterContents() throws IOException {
        PdfArray ar;
        for (PageStamp ps : this.pagesToContent.values()) {
            PdfDictionary pageN = ps.pageN;
            markUsed(pageN);
            PdfObject content = PdfReader.getPdfObject(pageN.get(PdfName.CONTENTS), pageN);
            if (content == null) {
                ar = new PdfArray();
                pageN.put(PdfName.CONTENTS, ar);
            } else if (content.isArray()) {
                ar = (PdfArray) content;
                markUsed(ar);
            } else if (content.isStream()) {
                ar = new PdfArray();
                ar.add(pageN.get(PdfName.CONTENTS));
                pageN.put(PdfName.CONTENTS, ar);
            } else {
                ar = new PdfArray();
                pageN.put(PdfName.CONTENTS, ar);
            }
            ByteBuffer out = new ByteBuffer();
            if (ps.under != null) {
                out.append(PdfContents.SAVESTATE);
                applyRotation(pageN, out);
                out.append(ps.under.getInternalBuffer());
                out.append(PdfContents.RESTORESTATE);
            }
            if (ps.over != null) {
                out.append(PdfContents.SAVESTATE);
            }
            PdfStream stream = new PdfStream(out.toByteArray());
            stream.flateCompress(this.compressionLevel);
            ar.addFirst(addToBody(stream).getIndirectReference());
            out.reset();
            if (ps.over != null) {
                out.append(' ');
                out.append(PdfContents.RESTORESTATE);
                ByteBuffer buf = ps.over.getInternalBuffer();
                out.append(buf.getBuffer(), 0, 0);
                out.append(PdfContents.SAVESTATE);
                applyRotation(pageN, out);
                out.append(buf.getBuffer(), 0, buf.size() - 0);
                out.append(PdfContents.RESTORESTATE);
                PdfStream stream2 = new PdfStream(out.toByteArray());
                stream2.flateCompress(this.compressionLevel);
                ar.add(addToBody(stream2).getIndirectReference());
            }
            alterResources(ps);
        }
    }

    private void alterResources(PageStamp ps) {
        ps.pageN.put(PdfName.RESOURCES, ps.pageResources.getResources());
    }

    @Override // com.aowagie.text.pdf.PdfWriter
    protected int getNewObjectNumber(PdfReader reader, int number, int generation) {
        IntHashtable ref = (IntHashtable) this.readers2intrefs.get(reader);
        if (ref != null) {
            int n = ref.get(number);
            if (n == 0) {
                n = getIndirectReferenceNumber();
                ref.put(number, n);
            }
            return n;
        }
        if (this.currentPdfReaderInstance == null) {
            if (this.append && number < this.initialXrefSize) {
                return number;
            }
            int n2 = this.myXref.get(number);
            if (n2 == 0) {
                n2 = getIndirectReferenceNumber();
                this.myXref.put(number, n2);
            }
            return n2;
        }
        return this.currentPdfReaderInstance.getNewObjectNumber(number, generation);
    }

    @Override // com.aowagie.text.pdf.PdfWriter
    RandomAccessFileOrArray getReaderFile(PdfReader reader) {
        if (this.readers2intrefs.containsKey(reader)) {
            RandomAccessFileOrArray raf = (RandomAccessFileOrArray) this.readers2file.get(reader);
            if (raf != null) {
                return raf;
            }
            return reader.getSafeFile();
        }
        if (this.currentPdfReaderInstance == null) {
            return this.file;
        }
        return this.currentPdfReaderInstance.getReaderFile();
    }

    private void registerReader(PdfReader reader, boolean openFile) throws IOException {
        if (this.readers2intrefs.containsKey(reader)) {
            return;
        }
        this.readers2intrefs.put(reader, new IntHashtable());
        if (openFile) {
            RandomAccessFileOrArray raf = reader.getSafeFile();
            this.readers2file.put(reader, raf);
            raf.reOpen();
        }
    }

    private static void findAllObjects(PdfReader reader, PdfObject obj, IntHashtable hits) {
        if (obj == null) {
        }
        switch (obj.type()) {
            case 5:
                PdfArray a = (PdfArray) obj;
                for (int k = 0; k < a.size(); k++) {
                    findAllObjects(reader, a.getPdfObject(k), hits);
                }
                break;
            case 6:
            case 7:
                PdfDictionary dic = (PdfDictionary) obj;
                for (Object element : dic.getKeys()) {
                    PdfName name = (PdfName) element;
                    findAllObjects(reader, dic.get(name), hits);
                }
                break;
            case 10:
                PRIndirectReference iref = (PRIndirectReference) obj;
                if (reader == iref.getReader() && !hits.containsKey(iref.getNumber())) {
                    hits.put(iref.getNumber(), 1);
                    findAllObjects(reader, PdfReader.getPdfObject(obj), hits);
                }
                break;
        }
    }

    private PageStamp getPageStamp(int pageNum) {
        PdfDictionary pageN = this.reader.getPageN(pageNum);
        PageStamp ps = (PageStamp) this.pagesToContent.get(pageN);
        if (ps == null) {
            ps = new PageStamp(this.reader, pageN);
            this.pagesToContent.put(pageN, ps);
        }
        return ps;
    }

    PdfContentByte getOverContent(int pageNum) {
        if (pageNum < 1 || pageNum > this.reader.getNumberOfPages()) {
            return null;
        }
        PageStamp ps = getPageStamp(pageNum);
        if (ps.over == null) {
            ps.over = new StampContent(this, ps);
        }
        return ps.over;
    }

    private void correctAcroFieldPages(int page) {
        if (this.acroFields == null || page > this.reader.getNumberOfPages()) {
            return;
        }
        for (AcroFields.Item item : this.acroFields.getFields().values()) {
            for (int k = 0; k < item.size(); k++) {
                int p = item.getPage(k).intValue();
                if (p >= page) {
                    item.forcePage(k, p + 1);
                }
            }
        }
    }

    private static void moveRectangle(PdfDictionary dic2, PdfReader r, int pageImported, PdfName key, String name) {
        Rectangle m = r.getBoxSize(pageImported, name);
        if (m == null) {
            dic2.remove(key);
        } else {
            dic2.put(key, new PdfRectangle(m));
        }
    }

    /* JADX WARN: Type inference fix 'apply assigned field type' failed
    java.lang.UnsupportedOperationException: ArgType.getObject(), call class: class jadx.core.dex.instructions.args.ArgType$UnknownArg
    	at jadx.core.dex.instructions.args.ArgType.getObject(ArgType.java:596)
    	at jadx.core.dex.attributes.nodes.ClassTypeVarsAttr.getTypeVarsMapFor(ClassTypeVarsAttr.java:35)
    	at jadx.core.dex.nodes.utils.TypeUtils.replaceClassGenerics(TypeUtils.java:177)
    	at jadx.core.dex.visitors.typeinference.FixTypesVisitor.insertExplicitUseCast(FixTypesVisitor.java:397)
    	at jadx.core.dex.visitors.typeinference.FixTypesVisitor.tryFieldTypeWithNewCasts(FixTypesVisitor.java:359)
    	at jadx.core.dex.visitors.typeinference.FixTypesVisitor.applyFieldType(FixTypesVisitor.java:309)
    	at jadx.core.dex.visitors.typeinference.FixTypesVisitor.visit(FixTypesVisitor.java:94)
     */
    void insertPage(int pageNumber, Rectangle mediabox) {
        PRIndirectReference parentRef;
        PdfDictionary parent;
        Rectangle media = new Rectangle(mediabox);
        int rotation = media.getRotation() % 360;
        PdfDictionary pdfDictionary = new PdfDictionary(PdfName.PAGE);
        PdfDictionary resources = new PdfDictionary();
        PdfArray procset = new PdfArray();
        procset.add(PdfName.PDF);
        procset.add(PdfName.TEXT);
        procset.add(PdfName.IMAGEB);
        procset.add(PdfName.IMAGEC);
        procset.add(PdfName.IMAGEI);
        resources.put(PdfName.PROCSET, procset);
        pdfDictionary.put(PdfName.RESOURCES, resources);
        pdfDictionary.put(PdfName.ROTATE, new PdfNumber(rotation));
        pdfDictionary.put(PdfName.MEDIABOX, new PdfRectangle(media, rotation));
        PRIndirectReference pref = this.reader.addPdfObject(pdfDictionary);
        if (pageNumber > this.reader.getNumberOfPages()) {
            PdfDictionary lastPage = this.reader.getPageNRelease(this.reader.getNumberOfPages());
            parentRef = new PRIndirectReference(this.reader, ((PRIndirectReference) lastPage.get(PdfName.PARENT)).getNumber());
            parent = (PdfDictionary) PdfReader.getPdfObject(parentRef);
            PdfArray kids = (PdfArray) PdfReader.getPdfObject(parent.get(PdfName.KIDS), parent);
            kids.add(pref);
            markUsed(kids);
            this.reader.pageRefs.insertPage(pageNumber, pref);
        } else {
            if (pageNumber < 1) {
                pageNumber = 1;
            }
            PdfDictionary firstPage = this.reader.getPageN(pageNumber);
            PRIndirectReference firstPageRef = this.reader.getPageOrigRef(pageNumber);
            this.reader.releasePage(pageNumber);
            parentRef = new PRIndirectReference(this.reader, ((PRIndirectReference) firstPage.get(PdfName.PARENT)).getNumber());
            parent = (PdfDictionary) PdfReader.getPdfObject(parentRef);
            PdfArray kids2 = (PdfArray) PdfReader.getPdfObject(parent.get(PdfName.KIDS), parent);
            int len = kids2.size();
            int num = firstPageRef.getNumber();
            for (int k = 0; k < len; k++) {
                PRIndirectReference cur = (PRIndirectReference) kids2.getPdfObject(k);
                if (num == cur.getNumber()) {
                    kids2.add(k, pref);
                    break;
                }
            }
            if (len == kids2.size()) {
                throw new RuntimeException("Internal inconsistence.");
            }
            markUsed(kids2);
            this.reader.pageRefs.insertPage(pageNumber, pref);
            correctAcroFieldPages(pageNumber);
        }
        pdfDictionary.put(PdfName.PARENT, parentRef);
        while (parent != null) {
            markUsed(parent);
            PdfNumber count = (PdfNumber) PdfReader.getPdfObjectRelease(parent.get(PdfName.COUNT));
            parent.put(PdfName.COUNT, new PdfNumber(count.intValue() + 1));
            parent = parent.getAsDict(PdfName.PARENT);
        }
    }

    boolean isRotateContents() {
        return this.rotateContents;
    }

    void setRotateContents(boolean rotateContents) {
        this.rotateContents = rotateContents;
    }

    boolean isContentWritten() {
        return this.body.size() > 1;
    }

    AcroFields getAcroFields() {
        if (this.acroFields == null) {
            this.acroFields = new AcroFields(this.reader, this);
        }
        return this.acroFields;
    }

    void setFormFlattening(boolean flat) {
        this.flat = flat;
    }

    void setFreeTextFlattening(boolean flat) {
        this.flatFreeText = flat;
    }

    private void flatFields() {
        PdfDictionary pageDic;
        PdfArray annots;
        PdfName as;
        PdfIndirectReference iref;
        if (this.append) {
            throw new IllegalArgumentException("Field flattening is not supported in append mode.");
        }
        getAcroFields();
        HashMap<String, AcroFields.Item> fields = this.acroFields.getFields();
        if (this.fieldsAdded && this.partialFlattening.isEmpty()) {
            Iterator<String> it = fields.keySet().iterator();
            while (it.hasNext()) {
                this.partialFlattening.add(it.next());
            }
        }
        PdfDictionary acroForm = this.reader.getCatalog().getAsDict(PdfName.ACROFORM);
        PdfArray acroFds = null;
        if (acroForm != null) {
            acroFds = (PdfArray) PdfReader.getPdfObject(acroForm.get(PdfName.FIELDS), acroForm);
        }
        for (Map.Entry<String, AcroFields.Item> entry : fields.entrySet()) {
            String name = entry.getKey();
            if (this.partialFlattening.isEmpty() || this.partialFlattening.contains(name)) {
                AcroFields.Item item = entry.getValue();
                for (int k = 0; k < item.size(); k++) {
                    PdfDictionary merged = item.getMerged(k);
                    PdfNumber ff = merged.getAsNumber(PdfName.F);
                    int flags = 0;
                    if (ff != null) {
                        flags = ff.intValue();
                    }
                    int page = item.getPage(k).intValue();
                    PdfDictionary appDic = merged.getAsDict(PdfName.AP);
                    if (appDic != null && (flags & 4) != 0 && (flags & 2) == 0) {
                        PdfObject obj = appDic.get(PdfName.N);
                        PdfAppearance app = null;
                        if (obj != null) {
                            PdfObject objReal = PdfReader.getPdfObject(obj);
                            if ((obj instanceof PdfIndirectReference) && !obj.isIndirect()) {
                                app = new PdfAppearance((PdfIndirectReference) obj);
                            } else if (objReal instanceof PdfStream) {
                                ((PdfDictionary) objReal).put(PdfName.SUBTYPE, PdfName.FORM);
                                app = new PdfAppearance((PdfIndirectReference) obj);
                            } else if (objReal != null && objReal.isDictionary() && (as = merged.getAsName(PdfName.AS)) != null && (iref = (PdfIndirectReference) ((PdfDictionary) objReal).get(as)) != null) {
                                app = new PdfAppearance(iref);
                                if (iref.isIndirect()) {
                                    ((PdfDictionary) PdfReader.getPdfObject(iref)).put(PdfName.SUBTYPE, PdfName.FORM);
                                }
                            }
                        }
                        if (app != null) {
                            Rectangle box = PdfReader.getNormalizedRectangle(merged.getAsArray(PdfName.RECT));
                            PdfContentByte cb = getOverContent(page);
                            cb.setLiteral("Q ");
                            cb.addTemplate(app, box.getLeft(), box.getBottom());
                            cb.setLiteral("q ");
                        }
                    }
                    if (!this.partialFlattening.isEmpty() && (annots = (pageDic = this.reader.getPageN(page)).getAsArray(PdfName.ANNOTS)) != null) {
                        int idx = 0;
                        while (idx < annots.size()) {
                            PdfObject ran = annots.getPdfObject(idx);
                            if (ran.isIndirect()) {
                                PdfObject ran2 = item.getWidgetRef(k);
                                if (ran2.isIndirect() && ((PRIndirectReference) ran).getNumber() == ((PRIndirectReference) ran2).getNumber()) {
                                    int i = idx;
                                    idx--;
                                    annots.remove(i);
                                    PRIndirectReference pRIndirectReference = (PRIndirectReference) ran2;
                                    while (true) {
                                        PRIndirectReference wdref = pRIndirectReference;
                                        PdfDictionary wd = (PdfDictionary) PdfReader.getPdfObject(wdref);
                                        PRIndirectReference parentRef = (PRIndirectReference) wd.get(PdfName.PARENT);
                                        PdfReader.killIndirect(wdref);
                                        if (parentRef == null) {
                                            int fr = 0;
                                            while (fr < acroFds.size()) {
                                                PdfObject h = acroFds.getPdfObject(fr);
                                                if (h.isIndirect() && ((PRIndirectReference) h).getNumber() == wdref.getNumber()) {
                                                    acroFds.remove(fr);
                                                    fr--;
                                                }
                                                fr++;
                                            }
                                            break;
                                        }
                                        PdfDictionary parent = (PdfDictionary) PdfReader.getPdfObject(parentRef);
                                        PdfArray kids = parent.getAsArray(PdfName.KIDS);
                                        int fr2 = 0;
                                        while (fr2 < kids.size()) {
                                            PdfObject h2 = kids.getPdfObject(fr2);
                                            if (h2.isIndirect() && ((PRIndirectReference) h2).getNumber() == wdref.getNumber()) {
                                                kids.remove(fr2);
                                                fr2--;
                                            }
                                            fr2++;
                                        }
                                        if (!kids.isEmpty()) {
                                            break;
                                        } else {
                                            pRIndirectReference = parentRef;
                                        }
                                    }
                                }
                            }
                            idx++;
                        }
                        if (annots.isEmpty()) {
                            PdfReader.killIndirect(pageDic.get(PdfName.ANNOTS));
                            pageDic.remove(PdfName.ANNOTS);
                        }
                    }
                }
            }
        }
        if (!this.fieldsAdded && this.partialFlattening.isEmpty()) {
            for (int page2 = 1; page2 <= this.reader.getNumberOfPages(); page2++) {
                PdfDictionary pageDic2 = this.reader.getPageN(page2);
                PdfArray annots2 = pageDic2.getAsArray(PdfName.ANNOTS);
                if (annots2 != null) {
                    int idx2 = 0;
                    while (idx2 < annots2.size()) {
                        PdfObject annoto = annots2.getDirectObject(idx2);
                        if ((!(annoto instanceof PdfIndirectReference) || annoto.isIndirect()) && (!annoto.isDictionary() || PdfName.WIDGET.equals(((PdfDictionary) annoto).get(PdfName.SUBTYPE)))) {
                            annots2.remove(idx2);
                            idx2--;
                        }
                        idx2++;
                    }
                    if (annots2.isEmpty()) {
                        PdfReader.killIndirect(pageDic2.get(PdfName.ANNOTS));
                        pageDic2.remove(PdfName.ANNOTS);
                    }
                }
            }
            eliminateAcroformObjects();
        }
    }

    private void eliminateAcroformObjects() {
        PdfObject acro = this.reader.getCatalog().get(PdfName.ACROFORM);
        if (acro == null) {
            return;
        }
        PdfDictionary acrodic = (PdfDictionary) PdfReader.getPdfObject(acro);
        this.reader.killXref(acrodic.get(PdfName.XFA));
        acrodic.remove(PdfName.XFA);
        PdfObject iFields = acrodic.get(PdfName.FIELDS);
        if (iFields != null) {
            PdfDictionary kids = new PdfDictionary();
            kids.put(PdfName.KIDS, iFields);
            sweepKids(kids);
            PdfReader.killIndirect(iFields);
            acrodic.put(PdfName.FIELDS, new PdfArray());
        }
    }

    private void sweepKids(PdfObject obj) {
        PdfObject oo = PdfReader.killIndirect(obj);
        if (oo == null || !oo.isDictionary()) {
            return;
        }
        PdfDictionary dic = (PdfDictionary) oo;
        PdfArray kids = (PdfArray) PdfReader.killIndirect(dic.get(PdfName.KIDS));
        if (kids == null) {
            return;
        }
        for (int k = 0; k < kids.size(); k++) {
            sweepKids(kids.getPdfObject(k));
        }
    }

    private void flatFreeTextFields() {
        PdfObject obj1;
        PdfName as_p;
        PdfIndirectReference iref;
        if (this.append) {
            throw new IllegalArgumentException("FreeText flattening is not supported in append mode.");
        }
        for (int page = 1; page <= this.reader.getNumberOfPages(); page++) {
            PdfDictionary pageDic = this.reader.getPageN(page);
            PdfArray annots = pageDic.getAsArray(PdfName.ANNOTS);
            if (annots != null) {
                for (int idx = 0; idx < annots.size(); idx++) {
                    PdfObject annoto = annots.getDirectObject(idx);
                    if (!(annoto instanceof PdfIndirectReference) || annoto.isIndirect()) {
                        PdfDictionary annDic = (PdfDictionary) annoto;
                        if (((PdfName) annDic.get(PdfName.SUBTYPE)).equals(PdfName.FREETEXT)) {
                            PdfNumber ff = annDic.getAsNumber(PdfName.F);
                            int flags = ff != null ? ff.intValue() : 0;
                            if ((flags & 4) != 0 && (flags & 2) == 0 && (obj1 = annDic.get(PdfName.AP)) != null) {
                                PdfDictionary appDic = obj1 instanceof PdfIndirectReference ? (PdfDictionary) PdfReader.getPdfObject(obj1) : (PdfDictionary) obj1;
                                PdfObject obj = appDic.get(PdfName.N);
                                PdfAppearance app = null;
                                PdfObject objReal = PdfReader.getPdfObject(obj);
                                if ((obj instanceof PdfIndirectReference) && !obj.isIndirect()) {
                                    app = new PdfAppearance((PdfIndirectReference) obj);
                                } else if (objReal instanceof PdfStream) {
                                    ((PdfDictionary) objReal).put(PdfName.SUBTYPE, PdfName.FORM);
                                    app = new PdfAppearance((PdfIndirectReference) obj);
                                } else if (objReal.isDictionary() && (as_p = appDic.getAsName(PdfName.AS)) != null && (iref = (PdfIndirectReference) ((PdfDictionary) objReal).get(as_p)) != null) {
                                    app = new PdfAppearance(iref);
                                    if (iref.isIndirect()) {
                                        ((PdfDictionary) PdfReader.getPdfObject(iref)).put(PdfName.SUBTYPE, PdfName.FORM);
                                    }
                                }
                                if (app != null) {
                                    Rectangle box = PdfReader.getNormalizedRectangle(annDic.getAsArray(PdfName.RECT));
                                    PdfContentByte cb = getOverContent(page);
                                    cb.setLiteral("Q ");
                                    cb.addTemplate(app, box.getLeft(), box.getBottom());
                                    cb.setLiteral("q ");
                                }
                            }
                        }
                    }
                }
                int idx2 = 0;
                while (idx2 < annots.size()) {
                    PdfDictionary annot = annots.getAsDict(idx2);
                    if (annot != null && PdfName.FREETEXT.equals(annot.get(PdfName.SUBTYPE))) {
                        annots.remove(idx2);
                        idx2--;
                    }
                    idx2++;
                }
                if (annots.isEmpty()) {
                    PdfReader.killIndirect(pageDic.get(PdfName.ANNOTS));
                    pageDic.remove(PdfName.ANNOTS);
                }
            }
        }
    }

    @Override // com.aowagie.text.pdf.PdfWriter
    public PdfIndirectReference getPageReference(int page) {
        PdfIndirectReference ref = this.reader.getPageOrigRef(page);
        if (ref == null) {
            throw new IllegalArgumentException("Invalid page number " + page);
        }
        return ref;
    }

    @Override // com.aowagie.text.pdf.PdfWriter, com.aowagie.text.pdf.interfaces.PdfAnnotations
    public void addAnnotation(PdfAnnotation annot) {
        throw new RuntimeException("Unsupported in this context. Use PdfStamper.addAnnotation()");
    }

    private void addDocumentField(PdfIndirectReference ref) {
        PdfDictionary catalog = this.reader.getCatalog();
        PdfDictionary acroForm = (PdfDictionary) PdfReader.getPdfObject(catalog.get(PdfName.ACROFORM), catalog);
        if (acroForm == null) {
            acroForm = new PdfDictionary();
            catalog.put(PdfName.ACROFORM, acroForm);
            markUsed(catalog);
        }
        PdfArray fields = (PdfArray) PdfReader.getPdfObject(acroForm.get(PdfName.FIELDS), acroForm);
        if (fields == null) {
            fields = new PdfArray();
            acroForm.put(PdfName.FIELDS, fields);
            markUsed(acroForm);
        }
        if (!acroForm.contains(PdfName.DA)) {
            acroForm.put(PdfName.DA, new PdfString("/Helv 0 Tf 0 g "));
            markUsed(acroForm);
        }
        fields.add(ref);
        markUsed(fields);
    }

    private void addFieldResources() throws IOException {
        if (this.fieldTemplates.isEmpty()) {
            return;
        }
        PdfDictionary catalog = this.reader.getCatalog();
        PdfDictionary acroForm = (PdfDictionary) PdfReader.getPdfObject(catalog.get(PdfName.ACROFORM), catalog);
        if (acroForm == null) {
            acroForm = new PdfDictionary();
            catalog.put(PdfName.ACROFORM, acroForm);
            markUsed(catalog);
        }
        PdfDictionary dr = (PdfDictionary) PdfReader.getPdfObject(acroForm.get(PdfName.DR), acroForm);
        if (dr == null) {
            dr = new PdfDictionary();
            acroForm.put(PdfName.DR, dr);
            markUsed(acroForm);
        }
        markUsed(dr);
        for (PdfTemplate template : this.fieldTemplates.keySet()) {
            PdfFormField.mergeResources(dr, (PdfDictionary) template.getResources(), this);
        }
        PdfDictionary fonts = dr.getAsDict(PdfName.FONT);
        if (fonts == null) {
            fonts = new PdfDictionary();
            dr.put(PdfName.FONT, fonts);
        }
        if (!fonts.contains(PdfName.HELV)) {
            PdfDictionary dic = new PdfDictionary(PdfName.FONT);
            dic.put(PdfName.BASEFONT, PdfName.HELVETICA);
            dic.put(PdfName.ENCODING, PdfName.WIN_ANSI_ENCODING);
            dic.put(PdfName.NAME, PdfName.HELV);
            dic.put(PdfName.SUBTYPE, PdfName.TYPE1);
            fonts.put(PdfName.HELV, addToBody(dic).getIndirectReference());
        }
        if (!fonts.contains(PdfName.ZADB)) {
            PdfDictionary dic2 = new PdfDictionary(PdfName.FONT);
            dic2.put(PdfName.BASEFONT, PdfName.ZAPFDINGBATS);
            dic2.put(PdfName.NAME, PdfName.ZADB);
            dic2.put(PdfName.SUBTYPE, PdfName.TYPE1);
            fonts.put(PdfName.ZADB, addToBody(dic2).getIndirectReference());
        }
        if (acroForm.get(PdfName.DA) == null) {
            acroForm.put(PdfName.DA, new PdfString("/Helv 0 Tf 0 g "));
            markUsed(acroForm);
        }
    }

    private void expandFields(PdfFormField field, ArrayList allAnnots) {
        allAnnots.add(field);
        ArrayList kids = field.getKids();
        if (kids != null) {
            for (int k = 0; k < kids.size(); k++) {
                expandFields((PdfFormField) kids.get(k), allAnnots);
            }
        }
    }

    void addAnnotation(PdfAnnotation annot, PdfDictionary pageN) {
        PdfArray annots;
        PdfRectangle rect;
        HashMap templates;
        try {
            ArrayList allAnnots = new ArrayList();
            if (annot.isForm()) {
                this.fieldsAdded = true;
                getAcroFields();
                PdfFormField field = (PdfFormField) annot;
                if (field.getParent() != null) {
                    return;
                } else {
                    expandFields(field, allAnnots);
                }
            } else {
                allAnnots.add(annot);
            }
            for (int k = 0; k < allAnnots.size(); k++) {
                PdfAnnotation annot2 = (PdfAnnotation) allAnnots.get(k);
                if (annot2.getPlaceInPage() > 0) {
                    pageN = this.reader.getPageN(annot2.getPlaceInPage());
                }
                if (annot2.isForm()) {
                    if (!annot2.isUsed() && (templates = annot2.getTemplates()) != null) {
                        this.fieldTemplates.putAll(templates);
                    }
                    PdfFormField field2 = (PdfFormField) annot2;
                    if (field2.getParent() == null) {
                        addDocumentField(field2.getIndirectReference());
                    }
                }
                if (annot2.isAnnotation()) {
                    PdfObject pdfobj = PdfReader.getPdfObject(pageN.get(PdfName.ANNOTS), pageN);
                    if (pdfobj == null || !pdfobj.isArray()) {
                        annots = new PdfArray();
                        pageN.put(PdfName.ANNOTS, annots);
                        markUsed(pageN);
                    } else {
                        annots = (PdfArray) pdfobj;
                    }
                    annots.add(annot2.getIndirectReference());
                    markUsed(annots);
                    if (!annot2.isUsed() && (rect = (PdfRectangle) annot2.get(PdfName.RECT)) != null && (rect.left() != ColumnText.GLOBAL_SPACE_CHAR_RATIO || rect.right() != ColumnText.GLOBAL_SPACE_CHAR_RATIO || rect.top() != ColumnText.GLOBAL_SPACE_CHAR_RATIO || rect.bottom() != ColumnText.GLOBAL_SPACE_CHAR_RATIO)) {
                        int rotation = this.reader.getPageRotation(pageN);
                        Rectangle pageSize = this.reader.getPageSizeWithRotation(pageN);
                        switch (rotation) {
                            case 90:
                                annot2.put(PdfName.RECT, new PdfRectangle(pageSize.getTop() - rect.bottom(), rect.left(), pageSize.getTop() - rect.top(), rect.right()));
                                break;
                            case 180:
                                annot2.put(PdfName.RECT, new PdfRectangle(pageSize.getRight() - rect.left(), pageSize.getTop() - rect.bottom(), pageSize.getRight() - rect.right(), pageSize.getTop() - rect.top()));
                                break;
                            case TIFF.TAG_IMAGE_DESCRIPTION /* 270 */:
                                annot2.put(PdfName.RECT, new PdfRectangle(rect.bottom(), pageSize.getRight() - rect.left(), rect.top(), pageSize.getRight() - rect.right()));
                                break;
                        }
                    }
                }
                if (!annot2.isUsed()) {
                    annot2.setUsed();
                    addToBody(annot2, annot2.getIndirectReference());
                }
            }
        } catch (IOException e) {
            throw new ExceptionConverter(e);
        }
    }

    @Override // com.aowagie.text.pdf.PdfWriter
    void addAnnotation(PdfAnnotation annot, int page) {
        annot.setPage(page);
        addAnnotation(annot, this.reader.getPageN(page));
    }

    private void outlineTravel(PRIndirectReference outline) {
        while (outline != null) {
            PdfDictionary outlineR = (PdfDictionary) PdfReader.getPdfObjectRelease(outline);
            PRIndirectReference first = (PRIndirectReference) outlineR.get(PdfName.FIRST);
            if (first != null) {
                outlineTravel(first);
            }
            PdfReader.killIndirect(outlineR.get(PdfName.DEST));
            PdfReader.killIndirect(outlineR.get(PdfName.A));
            PdfReader.killIndirect(outline);
            outline = (PRIndirectReference) outlineR.get(PdfName.NEXT);
        }
    }

    private void deleteOutlines() {
        PdfDictionary catalog = this.reader.getCatalog();
        PRIndirectReference outlines = (PRIndirectReference) catalog.get(PdfName.OUTLINES);
        if (outlines == null) {
            return;
        }
        outlineTravel(outlines);
        PdfReader.killIndirect(outlines);
        catalog.remove(PdfName.OUTLINES);
        markUsed(catalog);
    }

    private void setJavaScript() throws IOException {
        HashMap djs = this.pdf.getDocumentLevelJS();
        if (djs.isEmpty()) {
            return;
        }
        PdfDictionary catalog = this.reader.getCatalog();
        PdfDictionary names = (PdfDictionary) PdfReader.getPdfObject(catalog.get(PdfName.NAMES), catalog);
        if (names == null) {
            names = new PdfDictionary();
            catalog.put(PdfName.NAMES, names);
            markUsed(catalog);
        }
        markUsed(names);
        PdfDictionary tree = PdfNameTree.writeTree(djs, this);
        names.put(PdfName.JAVASCRIPT, addToBody(tree).getIndirectReference());
    }

    private void addFileAttachments() throws IOException {
        String nn;
        HashMap fs = this.pdf.getDocumentFileAttachment();
        if (fs.isEmpty()) {
            return;
        }
        PdfDictionary catalog = this.reader.getCatalog();
        PdfDictionary names = (PdfDictionary) PdfReader.getPdfObject(catalog.get(PdfName.NAMES), catalog);
        if (names == null) {
            names = new PdfDictionary();
            catalog.put(PdfName.NAMES, names);
            markUsed(catalog);
        }
        markUsed(names);
        HashMap old = PdfNameTree.readTree((PdfDictionary) PdfReader.getPdfObjectRelease(names.get(PdfName.EMBEDDEDFILES)));
        for (Map.Entry entry : fs.entrySet()) {
            String name = (String) entry.getKey();
            int k = 0;
            String str = name;
            while (true) {
                nn = str;
                if (old.containsKey(nn)) {
                    k++;
                    str = nn + " " + k;
                }
            }
            old.put(nn, entry.getValue());
        }
        PdfDictionary tree = PdfNameTree.writeTree(old, this);
        names.put(PdfName.EMBEDDEDFILES, addToBody(tree).getIndirectReference());
    }

    private void setOutlines() throws IOException {
        if (this.newBookmarks == null) {
            return;
        }
        deleteOutlines();
        if (this.newBookmarks.isEmpty()) {
            return;
        }
        PdfDictionary catalog = this.reader.getCatalog();
        boolean namedAsNames = catalog.get(PdfName.DESTS) != null;
        writeOutlines(catalog, namedAsNames);
        markUsed(catalog);
    }

    @Override // com.aowagie.text.pdf.PdfWriter, com.aowagie.text.pdf.interfaces.PdfViewerPreferences
    public void setViewerPreferences(int preferences) {
        this.useVp = true;
        this.viewerPreferences.setViewerPreferences(preferences);
    }

    @Override // com.aowagie.text.pdf.PdfWriter, com.aowagie.text.pdf.interfaces.PdfViewerPreferences
    public void addViewerPreference(PdfName key, PdfObject value) {
        this.useVp = true;
        this.viewerPreferences.addViewerPreference(key, value);
    }

    @Override // com.aowagie.text.pdf.PdfWriter, com.aowagie.text.pdf.interfaces.PdfAnnotations
    public void setSigFlags(int f) {
        this.sigFlags |= f;
    }

    @Override // com.aowagie.text.pdf.PdfWriter
    public void setPageAction(PdfName actionType, PdfAction action) throws PdfException {
        throw new UnsupportedOperationException("Use setPageAction(PdfName actionType, PdfAction action, int page)");
    }

    @Override // com.aowagie.text.pdf.PdfWriter, com.aowagie.text.pdf.interfaces.PdfPageActions
    public void setDuration(int seconds) {
        throw new UnsupportedOperationException("Use setPageAction(PdfName actionType, PdfAction action, int page)");
    }

    @Override // com.aowagie.text.pdf.PdfWriter, com.aowagie.text.pdf.interfaces.PdfPageActions
    public void setTransition(PdfTransition transition) {
        throw new UnsupportedOperationException("Use setPageAction(PdfName actionType, PdfAction action, int page)");
    }

    protected void markUsed(PdfObject obj) {
        PRIndirectReference ref;
        if (this.append && obj != null) {
            if (obj.type() == 10) {
                ref = (PRIndirectReference) obj;
            } else {
                ref = obj.getIndRef();
            }
            if (ref != null) {
                this.marked.put(ref.getNumber(), 1);
            }
        }
    }

    boolean isAppend() {
        return this.append;
    }

    @Override // com.aowagie.text.pdf.PdfWriter
    public void setAdditionalAction(PdfName actionType, PdfAction action) throws PdfException {
        if (!actionType.equals(DOCUMENT_CLOSE) && !actionType.equals(WILL_SAVE) && !actionType.equals(DID_SAVE) && !actionType.equals(WILL_PRINT) && !actionType.equals(DID_PRINT)) {
            throw new PdfException("Invalid additional action type: " + actionType.toString());
        }
        PdfDictionary aa = this.reader.getCatalog().getAsDict(PdfName.AA);
        if (aa == null) {
            if (action == null) {
                return;
            }
            aa = new PdfDictionary();
            this.reader.getCatalog().put(PdfName.AA, aa);
        }
        markUsed(aa);
        if (action == null) {
            aa.remove(actionType);
        } else {
            aa.put(actionType, action);
        }
    }

    @Override // com.aowagie.text.pdf.PdfWriter, com.aowagie.text.pdf.interfaces.PdfDocumentActions
    public void setOpenAction(PdfAction action) {
        this.openAction = action;
    }

    @Override // com.aowagie.text.pdf.PdfWriter, com.aowagie.text.pdf.interfaces.PdfDocumentActions
    public void setOpenAction(String name) {
        throw new UnsupportedOperationException("Open actions by name are not supported.");
    }

    @Override // com.aowagie.text.pdf.PdfWriter
    public void setThumbnail(Image image) {
        throw new UnsupportedOperationException("Use PdfStamper.setThumbnail().");
    }

    @Override // com.aowagie.text.pdf.PdfWriter
    public PdfContentByte getDirectContentUnder() {
        throw new UnsupportedOperationException("Use PdfStamper.getUnderContent() or PdfStamper.getOverContent()");
    }

    @Override // com.aowagie.text.pdf.PdfWriter
    public PdfContentByte getDirectContent() {
        throw new UnsupportedOperationException("Use PdfStamper.getUnderContent() or PdfStamper.getOverContent()");
    }

    private void readOCProperties() {
        PdfDictionary dict;
        if (!this.documentOCG.isEmpty() || (dict = this.reader.getCatalog().getAsDict(PdfName.OCPROPERTIES)) == null) {
            return;
        }
        PdfArray ocgs = dict.getAsArray(PdfName.OCGS);
        HashMap ocgmap = new LinkedHashMap();
        Iterator i = ocgs.listIterator();
        while (i.hasNext()) {
            PdfIndirectReference ref = (PdfIndirectReference) i.next();
            PdfLayer layer = new PdfLayer(null);
            layer.setRef(ref);
            layer.setOnPanel(false);
            layer.merge((PdfDictionary) PdfReader.getPdfObject(ref));
            ocgmap.put(ref.toString(), layer);
        }
        PdfDictionary d = dict.getAsDict(PdfName.D);
        PdfArray off = d.getAsArray(PdfName.OFF);
        if (off != null) {
            Iterator i2 = off.listIterator();
            while (i2.hasNext()) {
                ((PdfLayer) ocgmap.get(((PdfIndirectReference) i2.next()).toString())).setOn(false);
            }
        }
        PdfArray order = d.getAsArray(PdfName.ORDER);
        if (order != null) {
            addOrder(null, order, ocgmap);
        }
        this.documentOCG.addAll(ocgmap.values());
        this.OCGRadioGroup = d.getAsArray(PdfName.RBGROUPS);
        this.OCGLocked = d.getAsArray(PdfName.LOCKED);
        if (this.OCGLocked == null) {
            this.OCGLocked = new PdfArray();
        }
    }

    private void addOrder(PdfLayer parent, PdfArray arr, Map ocgmap) {
        int i = 0;
        while (i < arr.size()) {
            PdfObject obj = arr.getPdfObject(i);
            if (obj.isIndirect()) {
                PdfLayer layer = (PdfLayer) ocgmap.get(obj.toString());
                layer.setOnPanel(true);
                registerLayer(layer);
                if (parent != null) {
                    parent.addChild(layer);
                }
                if (arr.size() > i + 1 && arr.getPdfObject(i + 1).isArray()) {
                    i++;
                    addOrder(layer, (PdfArray) arr.getPdfObject(i), ocgmap);
                }
            } else if (obj.isArray()) {
                PdfArray sub = (PdfArray) obj;
                if (sub.isEmpty()) {
                    return;
                }
                PdfObject obj2 = sub.getPdfObject(0);
                if (obj2.isString()) {
                    PdfLayer layer2 = new PdfLayer(obj2.toString());
                    layer2.setOnPanel(true);
                    registerLayer(layer2);
                    if (parent != null) {
                        parent.addChild(layer2);
                    }
                    PdfArray array = new PdfArray();
                    Iterator j = sub.listIterator();
                    while (j.hasNext()) {
                        array.add(j.next());
                    }
                    addOrder(layer2, array, ocgmap);
                } else {
                    addOrder(parent, (PdfArray) obj2, ocgmap);
                }
            } else {
                continue;
            }
            i++;
        }
    }

    public Map getPdfLayers() {
        String key;
        String tmp;
        if (this.documentOCG.isEmpty()) {
            readOCProperties();
        }
        HashMap map = new LinkedHashMap();
        for (PdfLayer layer : this.documentOCG) {
            if (layer.getTitle() == null) {
                key = layer.getAsString(PdfName.NAME).toString();
            } else {
                key = layer.getTitle();
            }
            if (map.containsKey(key)) {
                int seq = 2;
                String str = key + "(2)";
                while (true) {
                    tmp = str;
                    if (!map.containsKey(tmp)) {
                        break;
                    }
                    seq++;
                    str = key + "(" + seq + ")";
                }
                key = tmp;
            }
            map.put(key, layer);
        }
        return map;
    }

    /* JADX INFO: loaded from: autofirma.jar:com/aowagie/text/pdf/PdfStamperImp$PageStamp.class */
    static class PageStamp {
        PdfDictionary pageN;
        private StampContent under;
        private StampContent over;
        PageResources pageResources;
        private final int replacePoint = 0;

        private PageStamp(PdfStamperImp stamper, PdfReader reader, PdfDictionary pageN) {
            this.replacePoint = 0;
            this.pageN = pageN;
            this.pageResources = new PageResources();
            PdfDictionary resources = pageN.getAsDict(PdfName.RESOURCES);
            this.pageResources.setOriginalResources(resources, stamper.namePtr);
        }
    }

    public PdfObject getFileID() {
        return this.pdfFileID;
    }
}
