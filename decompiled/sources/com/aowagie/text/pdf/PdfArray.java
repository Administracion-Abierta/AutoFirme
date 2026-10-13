package com.aowagie.text.pdf;

import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.ListIterator;

/* JADX INFO: loaded from: autofirma.jar:com/aowagie/text/pdf/PdfArray.class */
public class PdfArray extends PdfObject {
    private final ArrayList arrayList;

    public PdfArray() {
        super(5);
        this.arrayList = new ArrayList();
    }

    public PdfArray(PdfObject object) {
        super(5);
        this.arrayList = new ArrayList();
        this.arrayList.add(object);
    }

    PdfArray(float[] values) {
        super(5);
        this.arrayList = new ArrayList();
        add(values);
    }

    PdfArray(int[] values) {
        super(5);
        this.arrayList = new ArrayList();
        add(values);
    }

    PdfArray(PdfArray array) {
        super(5);
        this.arrayList = new ArrayList(array.arrayList);
    }

    @Override // com.aowagie.text.pdf.PdfObject
    public void toPdf(PdfWriter writer, OutputStream os) throws IOException {
        os.write(91);
        Iterator i = this.arrayList.iterator();
        if (i.hasNext()) {
            PdfObject object = (PdfObject) i.next();
            if (object == null) {
                object = PdfNull.PDFNULL;
            }
            object.toPdf(writer, os);
        }
        while (i.hasNext()) {
            PdfObject object2 = (PdfObject) i.next();
            if (object2 == null) {
                object2 = PdfNull.PDFNULL;
            }
            int type = object2.type();
            if (type != 5 && type != 6 && type != 4 && type != 3) {
                os.write(32);
            }
            object2.toPdf(writer, os);
        }
        os.write(93);
    }

    @Override // com.aowagie.text.pdf.PdfObject
    public String toString() {
        return this.arrayList.toString();
    }

    PdfObject set(int idx, PdfObject obj) {
        return (PdfObject) this.arrayList.set(idx, obj);
    }

    PdfObject remove(int idx) {
        return (PdfObject) this.arrayList.remove(idx);
    }

    public ArrayList getArrayList() {
        return this.arrayList;
    }

    public int size() {
        return this.arrayList.size();
    }

    public boolean isEmpty() {
        return this.arrayList.isEmpty();
    }

    public boolean add(PdfObject object) {
        return this.arrayList.add(object);
    }

    public boolean add(float[] values) {
        for (float value : values) {
            this.arrayList.add(new PdfNumber(value));
        }
        return true;
    }

    public boolean add(int[] values) {
        for (int value : values) {
            this.arrayList.add(new PdfNumber(value));
        }
        return true;
    }

    void add(int index, PdfObject element) {
        this.arrayList.add(index, element);
    }

    public void addFirst(PdfObject object) {
        this.arrayList.add(0, object);
    }

    boolean contains(PdfObject object) {
        return this.arrayList.contains(object);
    }

    public ListIterator<PdfObject> listIterator() {
        return this.arrayList.listIterator();
    }

    public PdfObject getPdfObject(int idx) {
        return (PdfObject) this.arrayList.get(idx);
    }

    PdfObject getDirectObject(int idx) {
        return PdfReader.getPdfObject(getPdfObject(idx));
    }

    PdfDictionary getAsDict(int idx) {
        PdfDictionary dict = null;
        PdfObject orig = getDirectObject(idx);
        if (orig != null && orig.isDictionary()) {
            dict = (PdfDictionary) orig;
        }
        return dict;
    }

    PdfString getAsString(int idx) {
        PdfString string = null;
        PdfObject orig = getDirectObject(idx);
        if (orig != null && orig.isString()) {
            string = (PdfString) orig;
        }
        return string;
    }

    public PdfNumber getAsNumber(int idx) {
        PdfNumber number = null;
        PdfObject orig = getDirectObject(idx);
        if (orig != null && orig.isNumber()) {
            number = (PdfNumber) orig;
        }
        return number;
    }

    public PdfName getAsName(int idx) {
        PdfName name = null;
        PdfObject orig = getDirectObject(idx);
        if (orig != null && orig.isName()) {
            name = (PdfName) orig;
        }
        return name;
    }

    PdfIndirectReference getAsIndirectObject(int idx) {
        PdfIndirectReference ref = null;
        PdfObject orig = getPdfObject(idx);
        if (orig != null && orig.isIndirect()) {
            ref = (PdfIndirectReference) orig;
        }
        return ref;
    }
}
