package com.aowagie.text.pdf;

import com.aowagie.text.Document;
import com.aowagie.text.DocumentException;
import com.aowagie.text.ExceptionConverter;
import com.aowagie.text.Rectangle;
import com.twelvemonkeys.imageio.metadata.tiff.TIFF;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;

/* JADX INFO: loaded from: autofirma.jar:com/aowagie/text/pdf/PdfCopy.class */
class PdfCopy extends PdfWriter {
    private HashMap indirects;
    private final HashMap indirectMap;
    private PdfReader reader;
    private PdfIndirectReference acroForm;
    private final int[] namePtr;
    private boolean rotateContents;
    private PdfArray fieldArray;
    private HashMap fieldTemplates;

    /* JADX INFO: loaded from: autofirma.jar:com/aowagie/text/pdf/PdfCopy$IndirectReferences.class */
    private static class IndirectReferences {
        private final PdfIndirectReference theRef;
        private boolean hasCopied;

        private IndirectReferences(PdfIndirectReference ref) {
            this.theRef = ref;
            this.hasCopied = false;
        }

        /* JADX INFO: Access modifiers changed from: private */
        public void setCopied() {
            this.hasCopied = true;
        }

        /* JADX INFO: Access modifiers changed from: private */
        public boolean getCopied() {
            return this.hasCopied;
        }

        /* JADX INFO: Access modifiers changed from: private */
        public PdfIndirectReference getRef() {
            return this.theRef;
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:com/aowagie/text/pdf/PdfCopy$RefKey.class */
    private static class RefKey {
        private final int num;
        private final int gen;

        private RefKey(PRIndirectReference ref) {
            this.num = ref.getNumber();
            this.gen = ref.getGeneration();
        }

        public int hashCode() {
            return (this.gen << 16) + this.num;
        }

        public boolean equals(Object o) {
            if (!(o instanceof RefKey)) {
                return false;
            }
            RefKey other = (RefKey) o;
            return this.gen == other.gen && this.num == other.num;
        }

        public String toString() {
            return Integer.toString(this.num) + ' ' + this.gen;
        }
    }

    PdfCopy(Document document, OutputStream os, Calendar globalDate) throws DocumentException {
        super(new PdfDocument(globalDate), os);
        this.namePtr = new int[]{0};
        this.rotateContents = true;
        document.addDocListener(this.pdf);
        this.pdf.addWriter(this);
        this.indirectMap = new HashMap();
    }

    public boolean isRotateContents() {
        return this.rotateContents;
    }

    public void setRotateContents(boolean rotateContents) {
        this.rotateContents = rotateContents;
    }

    @Override // com.aowagie.text.pdf.PdfWriter
    public PdfImportedPage getImportedPage(PdfReader reader, int pageNumber) {
        if (this.currentPdfReaderInstance != null) {
            if (this.currentPdfReaderInstance.getReader() != reader) {
                try {
                    this.currentPdfReaderInstance.getReader().close();
                    this.currentPdfReaderInstance.getReaderFile().close();
                } catch (IOException e) {
                }
                this.currentPdfReaderInstance = reader.getPdfReaderInstance(this);
            }
        } else {
            this.currentPdfReaderInstance = reader.getPdfReaderInstance(this);
        }
        return this.currentPdfReaderInstance.getImportedPage(pageNumber);
    }

    private PdfIndirectReference copyIndirect(PRIndirectReference in) throws BadPdfFormatException, IOException {
        PdfIndirectReference theRef;
        PdfObject type;
        RefKey key = new RefKey(in);
        IndirectReferences iRef = (IndirectReferences) this.indirects.get(key);
        if (iRef != null) {
            theRef = iRef.getRef();
            if (iRef.getCopied()) {
                return theRef;
            }
        } else {
            theRef = this.body.getPdfIndirectReference();
            iRef = new IndirectReferences(theRef);
            this.indirects.put(key, iRef);
        }
        PdfObject obj = PdfReader.getPdfObjectRelease(in);
        if (obj == null || !obj.isDictionary() || (type = PdfReader.getPdfObjectRelease(((PdfDictionary) obj).get(PdfName.TYPE))) == null || !PdfName.PAGE.equals(type)) {
            iRef.setCopied();
            addToBody(copyObject(obj), theRef);
            return theRef;
        }
        return theRef;
    }

    private PdfDictionary copyDictionary(PdfDictionary in) throws BadPdfFormatException, IOException {
        PdfDictionary out = new PdfDictionary();
        PdfObject type = PdfReader.getPdfObjectRelease(in.get(PdfName.TYPE));
        for (Object element : in.getKeys()) {
            PdfName key = (PdfName) element;
            PdfObject value = in.get(key);
            if (type != null && PdfName.PAGE.equals(type)) {
                if (!key.equals(PdfName.B) && !key.equals(PdfName.PARENT)) {
                    out.put(key, copyObject(value));
                }
            } else {
                out.put(key, copyObject(value));
            }
        }
        return out;
    }

    private PdfStream copyStream(PRStream in) throws BadPdfFormatException, IOException {
        PRStream out = new PRStream(in, (PdfDictionary) null);
        for (Object element : in.getKeys()) {
            PdfName key = (PdfName) element;
            PdfObject value = in.get(key);
            out.put(key, copyObject(value));
        }
        return out;
    }

    private PdfArray copyArray(PdfArray in) throws BadPdfFormatException, IOException {
        PdfArray out = new PdfArray();
        Iterator i = in.listIterator();
        while (i.hasNext()) {
            PdfObject value = i.next();
            out.add(copyObject(value));
        }
        return out;
    }

    private PdfObject copyObject(PdfObject in) throws BadPdfFormatException, IOException {
        if (in == null) {
            return PdfNull.PDFNULL;
        }
        switch (in.type) {
            case 0:
            case 1:
            case 2:
            case 3:
            case 4:
            case 8:
                return in;
            case 5:
                return copyArray((PdfArray) in);
            case 6:
                return copyDictionary((PdfDictionary) in);
            case 7:
                return copyStream((PRStream) in);
            case 9:
            default:
                if (in.type < 0) {
                    String lit = ((PdfLiteral) in).toString();
                    if (lit.equals("true") || lit.equals("false")) {
                        return new PdfBoolean(lit);
                    }
                    return new PdfLiteral(lit);
                }
                System.out.println("CANNOT COPY type " + in.type);
                return null;
            case 10:
                return copyIndirect((PRIndirectReference) in);
        }
    }

    private int setFromIPage(PdfImportedPage iPage) {
        int pageNum = iPage.getPageNumber();
        PdfReaderInstance inst = iPage.getPdfReaderInstance();
        this.currentPdfReaderInstance = inst;
        this.reader = inst.getReader();
        setFromReader(this.reader);
        return pageNum;
    }

    private void setFromReader(PdfReader reader) {
        this.reader = reader;
        this.indirects = (HashMap) this.indirectMap.get(reader);
        if (this.indirects == null) {
            this.indirects = new HashMap();
            this.indirectMap.put(reader, this.indirects);
            PdfDictionary catalog = reader.getCatalog();
            PdfObject o = catalog.get(PdfName.ACROFORM);
            if (o == null || o.type() != 10) {
                return;
            }
            PRIndirectReference ref = (PRIndirectReference) o;
            if (this.acroForm == null) {
                this.acroForm = this.body.getPdfIndirectReference();
            }
            this.indirects.put(new RefKey(ref), new IndirectReferences(this.acroForm));
        }
    }

    @Override // com.aowagie.text.pdf.PdfWriter
    protected PdfDictionary getCatalog(PdfIndirectReference rootObj) {
        try {
            PdfDictionary theCat = this.pdf.getCatalog(rootObj);
            if (this.fieldArray == null) {
                if (this.acroForm != null) {
                    theCat.put(PdfName.ACROFORM, this.acroForm);
                }
            } else {
                addFieldResources(theCat);
            }
            return theCat;
        } catch (IOException e) {
            throw new ExceptionConverter(e);
        }
    }

    private void addFieldResources(PdfDictionary pdfDictionary) throws IOException {
        if (this.fieldArray == null) {
            return;
        }
        PdfDictionary acroForm = new PdfDictionary();
        pdfDictionary.put(PdfName.ACROFORM, acroForm);
        acroForm.put(PdfName.FIELDS, this.fieldArray);
        acroForm.put(PdfName.DA, new PdfString("/Helv 0 Tf 0 g "));
        if (this.fieldTemplates.isEmpty()) {
            return;
        }
        PdfDictionary dr = new PdfDictionary();
        acroForm.put(PdfName.DR, dr);
        for (PdfTemplate template : this.fieldTemplates.keySet()) {
            PdfFormField.mergeResources(dr, (PdfDictionary) template.getResources());
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
    }

    @Override // com.aowagie.text.pdf.PdfWriter, com.aowagie.text.DocWriter, com.aowagie.text.DocListener
    public void close() {
        if (this.open) {
            PdfReaderInstance ri = this.currentPdfReaderInstance;
            this.pdf.close();
            super.close();
            if (ri != null) {
                try {
                    ri.getReader().close();
                    ri.getReaderFile().close();
                } catch (IOException e) {
                }
            }
        }
    }

    @Override // com.aowagie.text.pdf.PdfWriter, com.aowagie.text.pdf.interfaces.PdfAnnotations
    public void addAnnotation(PdfAnnotation annot) {
    }

    @Override // com.aowagie.text.pdf.PdfWriter
    PdfIndirectReference add(PdfPage page, PdfContents contents) throws PdfException {
        return null;
    }

    @Override // com.aowagie.text.pdf.PdfWriter
    public void freeReader(PdfReader reader) throws IOException {
        this.indirectMap.remove(reader);
        if (this.currentPdfReaderInstance != null && this.currentPdfReaderInstance.getReader() == reader) {
            try {
                this.currentPdfReaderInstance.getReader().close();
                this.currentPdfReaderInstance.getReaderFile().close();
            } catch (IOException e) {
            }
            this.currentPdfReaderInstance = null;
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:com/aowagie/text/pdf/PdfCopy$PageStamp.class */
    private static class PageStamp {
        private final PdfDictionary pageN;
        private StampContent under;
        private StampContent over;
        private PageResources pageResources;
        private final PdfReader reader;
        private final PdfCopy cstp;

        private PageStamp(PdfReader reader, PdfDictionary pageN, PdfCopy cstp) {
            this.pageN = pageN;
            this.reader = reader;
            this.cstp = cstp;
        }

        public PdfContentByte getUnderContent() {
            if (this.under == null) {
                if (this.pageResources == null) {
                    this.pageResources = new PageResources();
                    PdfDictionary resources = this.pageN.getAsDict(PdfName.RESOURCES);
                    this.pageResources.setOriginalResources(resources, this.cstp.namePtr);
                }
                this.under = new StampContent(this.cstp, this.pageResources);
            }
            return this.under;
        }

        public PdfContentByte getOverContent() {
            if (this.over == null) {
                if (this.pageResources == null) {
                    this.pageResources = new PageResources();
                    PdfDictionary resources = this.pageN.getAsDict(PdfName.RESOURCES);
                    this.pageResources.setOriginalResources(resources, this.cstp.namePtr);
                }
                this.over = new StampContent(this.cstp, this.pageResources);
            }
            return this.over;
        }

        private void applyRotation(PdfDictionary pageN, ByteBuffer out) {
            if (!this.cstp.rotateContents) {
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

        private void addDocumentField(PdfIndirectReference ref) {
            if (this.cstp.fieldArray == null) {
                this.cstp.fieldArray = new PdfArray();
            }
            this.cstp.fieldArray.add(ref);
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

        public void addAnnotation(PdfAnnotation annot) {
            PdfArray annots;
            PdfRectangle rect;
            HashMap templates;
            try {
                ArrayList allAnnots = new ArrayList();
                if (annot.isForm()) {
                    PdfFormField field = (PdfFormField) annot;
                    if (field.getParent() != null) {
                        return;
                    }
                    expandFields(field, allAnnots);
                    if (this.cstp.fieldTemplates == null) {
                        this.cstp.fieldTemplates = new LinkedHashMap();
                    }
                } else {
                    allAnnots.add(annot);
                }
                for (int k = 0; k < allAnnots.size(); k++) {
                    PdfAnnotation annot2 = (PdfAnnotation) allAnnots.get(k);
                    if (annot2.isForm()) {
                        if (!annot2.isUsed() && (templates = annot2.getTemplates()) != null) {
                            this.cstp.fieldTemplates.putAll(templates);
                        }
                        PdfFormField field2 = (PdfFormField) annot2;
                        if (field2.getParent() == null) {
                            addDocumentField(field2.getIndirectReference());
                        }
                    }
                    if (annot2.isAnnotation()) {
                        PdfObject pdfobj = PdfReader.getPdfObject(this.pageN.get(PdfName.ANNOTS), this.pageN);
                        if (pdfobj == null || !pdfobj.isArray()) {
                            annots = new PdfArray();
                            this.pageN.put(PdfName.ANNOTS, annots);
                        } else {
                            annots = (PdfArray) pdfobj;
                        }
                        annots.add(annot2.getIndirectReference());
                        if (!annot2.isUsed() && (rect = (PdfRectangle) annot2.get(PdfName.RECT)) != null && (rect.left() != ColumnText.GLOBAL_SPACE_CHAR_RATIO || rect.right() != ColumnText.GLOBAL_SPACE_CHAR_RATIO || rect.top() != ColumnText.GLOBAL_SPACE_CHAR_RATIO || rect.bottom() != ColumnText.GLOBAL_SPACE_CHAR_RATIO)) {
                            int rotation = this.reader.getPageRotation(this.pageN);
                            Rectangle pageSize = this.reader.getPageSizeWithRotation(this.pageN);
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
                        this.cstp.addToBody(annot2, annot2.getIndirectReference());
                    }
                }
            } catch (IOException e) {
                throw new ExceptionConverter(e);
            }
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:com/aowagie/text/pdf/PdfCopy$StampContent.class */
    private static class StampContent extends PdfContentByte {
        private final PageResources pageResources;

        private StampContent(PdfWriter writer, PageResources pageResources) {
            super(writer);
            this.pageResources = pageResources;
        }

        @Override // com.aowagie.text.pdf.PdfContentByte
        public PdfContentByte getDuplicate() {
            return new StampContent(this.writer, this.pageResources);
        }

        @Override // com.aowagie.text.pdf.PdfContentByte
        PageResources getPageResources() {
            return this.pageResources;
        }
    }
}
