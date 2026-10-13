package com.aowagie.text.pdf;

import com.aowagie.text.Document;
import com.aowagie.text.DocumentException;
import com.aowagie.text.ExceptionConverter;
import com.aowagie.text.exceptions.BadPasswordException;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

/* JADX INFO: loaded from: autofirma.jar:com/aowagie/text/pdf/PdfCopyFieldsImp.class */
class PdfCopyFieldsImp extends PdfWriter {
    private final ArrayList readers;
    HashMap readers2intrefs;
    private final HashMap pages2intrefs;
    private final HashMap visited;
    ArrayList fields;
    private RandomAccessFileOrArray file;
    private final HashMap fieldTree;
    private final ArrayList pageRefs;
    private final ArrayList pageDics;
    private final PdfDictionary resources;
    private PdfDictionary form;
    private boolean closing;
    private final Document nd;
    private HashMap tabOrder;
    private final ArrayList calculationOrder;
    private ArrayList calculationOrderRefs;
    private boolean hasSignature;
    private static final PdfName iTextTag = new PdfName("_iTextTag_");
    private static final Integer zero = 0;
    private static final HashMap widgetKeys = new LinkedHashMap();
    protected static final HashMap fieldKeys = new LinkedHashMap();

    static {
        widgetKeys.put(PdfName.SUBTYPE, 1);
        widgetKeys.put(PdfName.CONTENTS, 1);
        widgetKeys.put(PdfName.RECT, 1);
        widgetKeys.put(PdfName.NM, 1);
        widgetKeys.put(PdfName.M, 1);
        widgetKeys.put(PdfName.F, 1);
        widgetKeys.put(PdfName.BS, 1);
        widgetKeys.put(PdfName.BORDER, 1);
        widgetKeys.put(PdfName.AP, 1);
        widgetKeys.put(PdfName.AS, 1);
        widgetKeys.put(PdfName.C, 1);
        widgetKeys.put(PdfName.A, 1);
        widgetKeys.put(PdfName.STRUCTPARENT, 1);
        widgetKeys.put(PdfName.OC, 1);
        widgetKeys.put(PdfName.H, 1);
        widgetKeys.put(PdfName.MK, 1);
        widgetKeys.put(PdfName.DA, 1);
        widgetKeys.put(PdfName.Q, 1);
        fieldKeys.put(PdfName.AA, 1);
        fieldKeys.put(PdfName.FT, 1);
        fieldKeys.put(PdfName.TU, 1);
        fieldKeys.put(PdfName.TM, 1);
        fieldKeys.put(PdfName.FF, 1);
        fieldKeys.put(PdfName.V, 1);
        fieldKeys.put(PdfName.DV, 1);
        fieldKeys.put(PdfName.DS, 1);
        fieldKeys.put(PdfName.RV, 1);
        fieldKeys.put(PdfName.OPT, 1);
        fieldKeys.put(PdfName.MAXLEN, 1);
        fieldKeys.put(PdfName.TI, 1);
        fieldKeys.put(PdfName.I, 1);
        fieldKeys.put(PdfName.LOCK, 1);
        fieldKeys.put(PdfName.SV, 1);
    }

    PdfCopyFieldsImp(OutputStream os, Calendar globalDate) throws DocumentException {
        this(os, (char) 0, globalDate);
    }

    private PdfCopyFieldsImp(OutputStream os, char pdfVersion, Calendar globalDate) throws DocumentException {
        super(new PdfDocument(globalDate), os);
        this.readers = new ArrayList();
        this.readers2intrefs = new LinkedHashMap();
        this.pages2intrefs = new LinkedHashMap();
        this.visited = new LinkedHashMap();
        this.fields = new ArrayList();
        this.fieldTree = new LinkedHashMap();
        this.pageRefs = new ArrayList();
        this.pageDics = new ArrayList();
        this.resources = new PdfDictionary();
        this.closing = false;
        this.calculationOrder = new ArrayList();
        this.pdf.addWriter(this);
        if (pdfVersion != 0) {
            super.setPdfVersion(pdfVersion);
        }
        this.nd = new Document();
        this.nd.addDocListener(this.pdf);
    }

    void addDocument(PdfReader reader, List pagesToKeep) throws IOException, DocumentException {
        if (!this.readers2intrefs.containsKey(reader) && reader.isTampered()) {
            throw new DocumentException("The document was reused.");
        }
        PdfReader reader2 = new PdfReader(reader);
        reader2.selectPages(pagesToKeep);
        if (reader2.getNumberOfPages() == 0) {
            return;
        }
        reader2.setTampered(false);
        addDocument(reader2);
    }

    void addDocument(PdfReader reader) throws IOException, DocumentException {
        if (!reader.isOpenedWithFullPermissions()) {
            throw new BadPasswordException("PdfReader not opened with owner password");
        }
        openDoc();
        if (this.readers2intrefs.containsKey(reader)) {
            reader = new PdfReader(reader);
        } else {
            if (reader.isTampered()) {
                throw new DocumentException("The document was reused.");
            }
            reader.consolidateNamedDestinations();
            reader.setTampered(true);
        }
        reader.shuffleSubsetNames();
        this.readers2intrefs.put(reader, new IntHashtable());
        this.readers.add(reader);
        int len = reader.getNumberOfPages();
        IntHashtable refs = new IntHashtable();
        for (int p = 1; p <= len; p++) {
            refs.put(reader.getPageOrigRef(p).getNumber(), 1);
            reader.releasePage(p);
        }
        this.pages2intrefs.put(reader, refs);
        this.visited.put(reader, new IntHashtable());
        this.fields.add(reader.getAcroFields());
        updateCalculationOrder(reader);
    }

    private static String getCOName(PdfReader reader, PRIndirectReference ref) {
        PdfObject obj;
        String name = "";
        while (ref != null && (obj = PdfReader.getPdfObject(ref)) != null && obj.type() == 6) {
            PdfDictionary dic = (PdfDictionary) obj;
            PdfString t = dic.getAsString(PdfName.T);
            if (t != null) {
                name = t.toUnicodeString() + "." + name;
            }
            ref = (PRIndirectReference) dic.get(PdfName.PARENT);
        }
        if (name.endsWith(".")) {
            name = name.substring(0, name.length() - 1);
        }
        return name;
    }

    protected void updateCalculationOrder(PdfReader reader) {
        PdfArray co;
        PdfDictionary catalog = reader.getCatalog();
        PdfDictionary acro = catalog.getAsDict(PdfName.ACROFORM);
        if (acro == null || (co = acro.getAsArray(PdfName.CO)) == null || co.size() == 0) {
            return;
        }
        AcroFields af = reader.getAcroFields();
        for (int k = 0; k < co.size(); k++) {
            PdfObject obj = co.getPdfObject(k);
            if (obj != null && obj.isIndirect()) {
                String name = getCOName(reader, (PRIndirectReference) obj);
                if (af.getFieldItem(name) != null) {
                    String name2 = "." + name;
                    if (!this.calculationOrder.contains(name2)) {
                        this.calculationOrder.add(name2);
                    }
                }
            }
        }
    }

    private void propagate(PdfObject obj, PdfIndirectReference refo, boolean restricted) throws IOException {
        if (obj == null || (obj instanceof PdfIndirectReference)) {
            return;
        }
        switch (obj.type()) {
            case 5:
                Iterator it = ((PdfArray) obj).listIterator();
                while (it.hasNext()) {
                    PdfObject ob = it.next();
                    if (ob != null && ob.isIndirect()) {
                        PRIndirectReference ind = (PRIndirectReference) ob;
                        if (!isVisited(ind) && !isPage(ind)) {
                            PdfIndirectReference ref = getNewReference(ind);
                            propagate(PdfReader.getPdfObjectRelease(ind), ref, restricted);
                        }
                    } else {
                        propagate(ob, null, restricted);
                    }
                }
                return;
            case 6:
            case 7:
                PdfDictionary dic = (PdfDictionary) obj;
                for (Object element : dic.getKeys()) {
                    PdfName key = (PdfName) element;
                    if (!restricted || (!key.equals(PdfName.PARENT) && !key.equals(PdfName.KIDS))) {
                        PdfObject ob2 = dic.get(key);
                        if (ob2 != null && ob2.isIndirect()) {
                            PRIndirectReference ind2 = (PRIndirectReference) ob2;
                            if (!setVisited(ind2) && !isPage(ind2)) {
                                PdfIndirectReference ref2 = getNewReference(ind2);
                                propagate(PdfReader.getPdfObjectRelease(ind2), ref2, restricted);
                            }
                        } else {
                            propagate(ob2, null, restricted);
                        }
                    }
                }
                return;
            case 8:
            case 9:
            default:
                return;
            case 10:
                throw new RuntimeException("Reference pointing to reference.");
        }
    }

    private void adjustTabOrder(PdfArray annots, PdfIndirectReference ind, PdfNumber nn) {
        int v = nn.intValue();
        ArrayList t = (ArrayList) this.tabOrder.get(annots);
        if (t == null) {
            ArrayList t2 = new ArrayList();
            int size = annots.size() - 1;
            for (int k = 0; k < size; k++) {
                t2.add(zero);
            }
            t2.add(Integer.valueOf(v));
            this.tabOrder.put(annots, t2);
            annots.add(ind);
            return;
        }
        int size2 = t.size() - 1;
        for (int k2 = size2; k2 >= 0; k2--) {
            if (((Integer) t.get(k2)).intValue() <= v) {
                t.add(k2 + 1, Integer.valueOf(v));
                annots.add(k2 + 1, ind);
                size2 = -2;
                break;
            }
        }
        if (size2 != -2) {
            t.add(0, Integer.valueOf(v));
            annots.add(0, ind);
        }
    }

    private PdfArray branchForm(HashMap level, PdfIndirectReference parent, String fname) throws IOException {
        PdfArray arr = new PdfArray();
        for (Map.Entry entry : level.entrySet()) {
            String name = (String) entry.getKey();
            Object obj = entry.getValue();
            PdfIndirectReference ind = getPdfIndirectReference();
            PdfDictionary pdfDictionary = new PdfDictionary();
            if (parent != null) {
                pdfDictionary.put(PdfName.PARENT, parent);
            }
            pdfDictionary.put(PdfName.T, new PdfString(name, PdfObject.TEXT_UNICODE));
            String fname2 = fname + "." + name;
            int coidx = this.calculationOrder.indexOf(fname2);
            if (coidx >= 0) {
                this.calculationOrderRefs.set(coidx, ind);
            }
            if (obj instanceof HashMap) {
                pdfDictionary.put(PdfName.KIDS, branchForm((HashMap) obj, ind, fname2));
                arr.add(ind);
                addToBody(pdfDictionary, ind);
            } else {
                ArrayList list = (ArrayList) obj;
                pdfDictionary.mergeDifferent((PdfDictionary) list.get(0));
                if (list.size() == 3) {
                    pdfDictionary.mergeDifferent((PdfDictionary) list.get(2));
                    int page = ((Integer) list.get(1)).intValue();
                    PdfDictionary pageDic = (PdfDictionary) this.pageDics.get(page - 1);
                    PdfArray annots = pageDic.getAsArray(PdfName.ANNOTS);
                    if (annots == null) {
                        annots = new PdfArray();
                        pageDic.put(PdfName.ANNOTS, annots);
                    }
                    PdfNumber nn = (PdfNumber) pdfDictionary.get(iTextTag);
                    pdfDictionary.remove(iTextTag);
                    adjustTabOrder(annots, ind, nn);
                } else {
                    PdfArray kids = new PdfArray();
                    for (int k = 1; k < list.size(); k += 2) {
                        int page2 = ((Integer) list.get(k)).intValue();
                        PdfDictionary pageDic2 = (PdfDictionary) this.pageDics.get(page2 - 1);
                        PdfArray annots2 = pageDic2.getAsArray(PdfName.ANNOTS);
                        if (annots2 == null) {
                            annots2 = new PdfArray();
                            pageDic2.put(PdfName.ANNOTS, annots2);
                        }
                        PdfDictionary widget = new PdfDictionary();
                        widget.merge((PdfDictionary) list.get(k + 1));
                        widget.put(PdfName.PARENT, ind);
                        PdfNumber nn2 = (PdfNumber) widget.get(iTextTag);
                        widget.remove(iTextTag);
                        PdfIndirectReference wref = addToBody(widget).getIndirectReference();
                        adjustTabOrder(annots2, wref, nn2);
                        kids.add(wref);
                        propagate(widget, null, false);
                    }
                    pdfDictionary.put(PdfName.KIDS, kids);
                }
                arr.add(ind);
                addToBody(pdfDictionary, ind);
                propagate(pdfDictionary, null, false);
            }
        }
        return arr;
    }

    private void createAcroForms() throws IOException {
        if (this.fieldTree.isEmpty()) {
            return;
        }
        this.form = new PdfDictionary();
        this.form.put(PdfName.DR, this.resources);
        propagate(this.resources, null, false);
        this.form.put(PdfName.DA, new PdfString("/Helv 0 Tf 0 g "));
        this.tabOrder = new LinkedHashMap();
        this.calculationOrderRefs = new ArrayList(this.calculationOrder);
        this.form.put(PdfName.FIELDS, branchForm(this.fieldTree, null, ""));
        if (this.hasSignature) {
            this.form.put(PdfName.SIGFLAGS, new PdfNumber(3));
        }
        PdfArray co = new PdfArray();
        for (int k = 0; k < this.calculationOrderRefs.size(); k++) {
            Object obj = this.calculationOrderRefs.get(k);
            if (obj instanceof PdfIndirectReference) {
                co.add((PdfIndirectReference) obj);
            }
        }
        if (co.size() > 0) {
            this.form.put(PdfName.CO, co);
        }
    }

    @Override // com.aowagie.text.pdf.PdfWriter, com.aowagie.text.DocWriter, com.aowagie.text.DocListener
    public void close() {
        if (this.closing) {
            super.close();
            return;
        }
        this.closing = true;
        try {
            closeIt();
        } catch (Exception e) {
            throw new ExceptionConverter(e);
        }
    }

    private void closeIt() throws IOException {
        for (int k = 0; k < this.readers.size(); k++) {
            ((PdfReader) this.readers.get(k)).removeFields();
        }
        for (int r = 0; r < this.readers.size(); r++) {
            PdfReader reader = (PdfReader) this.readers.get(r);
            for (int page = 1; page <= reader.getNumberOfPages(); page++) {
                this.pageRefs.add(getNewReference(reader.getPageOrigRef(page)));
                this.pageDics.add(reader.getPageN(page));
            }
        }
        mergeFields();
        createAcroForms();
        for (int r2 = 0; r2 < this.readers.size(); r2++) {
            PdfReader reader2 = (PdfReader) this.readers.get(r2);
            for (int page2 = 1; page2 <= reader2.getNumberOfPages(); page2++) {
                PdfDictionary dic = reader2.getPageN(page2);
                PdfIndirectReference pageRef = getNewReference(reader2.getPageOrigRef(page2));
                PdfIndirectReference parent = this.root.addPageRef(pageRef);
                dic.put(PdfName.PARENT, parent);
                propagate(dic, pageRef, false);
            }
        }
        for (Map.Entry entry : this.readers2intrefs.entrySet()) {
            PdfReader reader3 = (PdfReader) entry.getKey();
            try {
                this.file = reader3.getSafeFile();
                this.file.reOpen();
                IntHashtable t = (IntHashtable) entry.getValue();
                int[] keys = t.toOrderedKeys();
                for (int key : keys) {
                    PRIndirectReference ref = new PRIndirectReference(reader3, key);
                    addToBody(PdfReader.getPdfObjectRelease(ref), t.get(key));
                }
                try {
                    this.file.close();
                    reader3.close();
                } catch (Exception e) {
                }
            } catch (Throwable th) {
                try {
                    this.file.close();
                    reader3.close();
                } catch (Exception e2) {
                }
                throw th;
            }
        }
        this.pdf.close();
    }

    private void addPageOffsetToField(HashMap fd, int pageOffset) {
        if (pageOffset == 0) {
            return;
        }
        for (AcroFields.Item item : fd.values()) {
            for (int k = 0; k < item.size(); k++) {
                int p = item.getPage(k).intValue();
                item.forcePage(k, p + pageOffset);
            }
        }
    }

    private void createWidgets(ArrayList list, AcroFields.Item item) {
        for (int k = 0; k < item.size(); k++) {
            list.add(item.getPage(k));
            PdfDictionary merged = item.getMerged(k);
            PdfObject dr = merged.get(PdfName.DR);
            if (dr != null) {
                PdfFormField.mergeResources(this.resources, (PdfDictionary) PdfReader.getPdfObject(dr));
            }
            PdfDictionary widget = new PdfDictionary();
            for (Object element : merged.getKeys()) {
                PdfName key = (PdfName) element;
                if (widgetKeys.containsKey(key)) {
                    widget.put(key, merged.get(key));
                }
            }
            widget.put(iTextTag, new PdfNumber(item.getTabOrder(k).intValue() + 1));
            list.add(widget);
        }
    }

    private void mergeField(String name, AcroFields.Item item) {
        HashMap map = this.fieldTree;
        StringTokenizer tk = new StringTokenizer(name, ".");
        if (!tk.hasMoreTokens()) {
            return;
        }
        while (true) {
            String s = tk.nextToken();
            Object obj = map.get(s);
            if (tk.hasMoreTokens()) {
                if (obj == null) {
                    LinkedHashMap linkedHashMap = new LinkedHashMap();
                    map.put(s, linkedHashMap);
                    map = linkedHashMap;
                } else if (obj instanceof HashMap) {
                    map = (HashMap) obj;
                } else {
                    return;
                }
            } else {
                if (obj instanceof HashMap) {
                    return;
                }
                PdfDictionary merged = item.getMerged(0);
                if (obj == null) {
                    PdfDictionary field = new PdfDictionary();
                    if (PdfName.SIG.equals(merged.get(PdfName.FT))) {
                        this.hasSignature = true;
                    }
                    for (Object element : merged.getKeys()) {
                        PdfName key = (PdfName) element;
                        if (fieldKeys.containsKey(key)) {
                            field.put(key, merged.get(key));
                        }
                    }
                    ArrayList list = new ArrayList();
                    list.add(field);
                    createWidgets(list, item);
                    map.put(s, list);
                    return;
                }
                ArrayList list2 = (ArrayList) obj;
                PdfDictionary field2 = (PdfDictionary) list2.get(0);
                PdfName type1 = (PdfName) field2.get(PdfName.FT);
                PdfName type2 = (PdfName) merged.get(PdfName.FT);
                if (type1 == null || !type1.equals(type2)) {
                    return;
                }
                int flag1 = 0;
                PdfObject f1 = field2.get(PdfName.FF);
                if (f1 != null && f1.isNumber()) {
                    flag1 = ((PdfNumber) f1).intValue();
                }
                int flag2 = 0;
                PdfObject f2 = merged.get(PdfName.FF);
                if (f2 != null && f2.isNumber()) {
                    flag2 = ((PdfNumber) f2).intValue();
                }
                if (type1.equals(PdfName.BTN)) {
                    if (((flag1 ^ flag2) & 65536) != 0) {
                        return;
                    }
                    if ((flag1 & 65536) == 0 && ((flag1 ^ flag2) & 32768) != 0) {
                        return;
                    }
                } else if (type1.equals(PdfName.CH) && ((flag1 ^ flag2) & 131072) != 0) {
                    return;
                }
                createWidgets(list2, item);
                return;
            }
        }
    }

    void mergeWithMaster(HashMap fd) {
        for (Map.Entry entry : fd.entrySet()) {
            String name = (String) entry.getKey();
            mergeField(name, (AcroFields.Item) entry.getValue());
        }
    }

    void mergeFields() {
        int pageOffset = 0;
        for (int k = 0; k < this.fields.size(); k++) {
            HashMap<String, AcroFields.Item> fields = ((AcroFields) this.fields.get(k)).getFields();
            addPageOffsetToField(fields, pageOffset);
            mergeWithMaster(fields);
            pageOffset += ((PdfReader) this.readers.get(k)).getNumberOfPages();
        }
    }

    @Override // com.aowagie.text.pdf.PdfWriter
    public PdfIndirectReference getPageReference(int page) {
        return (PdfIndirectReference) this.pageRefs.get(page - 1);
    }

    @Override // com.aowagie.text.pdf.PdfWriter
    protected PdfDictionary getCatalog(PdfIndirectReference rootObj) {
        try {
            PdfDictionary cat = this.pdf.getCatalog(rootObj);
            if (this.form != null) {
                PdfIndirectReference ref = addToBody(this.form).getIndirectReference();
                cat.put(PdfName.ACROFORM, ref);
            }
            return cat;
        } catch (IOException e) {
            throw new ExceptionConverter(e);
        }
    }

    private PdfIndirectReference getNewReference(PRIndirectReference ref) {
        return new PdfIndirectReference(0, getNewObjectNumber(ref.getReader(), ref.getNumber(), 0));
    }

    @Override // com.aowagie.text.pdf.PdfWriter
    protected int getNewObjectNumber(PdfReader reader, int number, int generation) {
        IntHashtable refs = (IntHashtable) this.readers2intrefs.get(reader);
        int n = refs.get(number);
        if (n == 0) {
            n = getIndirectReferenceNumber();
            refs.put(number, n);
        }
        return n;
    }

    private boolean setVisited(PRIndirectReference ref) {
        IntHashtable refs = (IntHashtable) this.visited.get(ref.getReader());
        return (refs == null || refs.put(ref.getNumber(), 1) == 0) ? false : true;
    }

    private boolean isVisited(PRIndirectReference ref) {
        IntHashtable refs = (IntHashtable) this.visited.get(ref.getReader());
        if (refs != null) {
            return refs.containsKey(ref.getNumber());
        }
        return false;
    }

    private boolean isPage(PRIndirectReference ref) {
        IntHashtable refs = (IntHashtable) this.pages2intrefs.get(ref.getReader());
        if (refs != null) {
            return refs.containsKey(ref.getNumber());
        }
        return false;
    }

    @Override // com.aowagie.text.pdf.PdfWriter
    RandomAccessFileOrArray getReaderFile(PdfReader reader) {
        return this.file;
    }

    public void openDoc() {
        if (!this.nd.isOpen()) {
            this.nd.open();
        }
    }
}
