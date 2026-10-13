package com.aowagie.text.pdf;

import java.io.IOException;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Set;

/* JADX INFO: loaded from: autofirma.jar:com/aowagie/text/pdf/PdfDictionary.class */
public class PdfDictionary extends PdfObject {
    private static final PdfName FONT = PdfName.FONT;
    static final PdfName OUTLINES = PdfName.OUTLINES;
    static final PdfName PAGE = PdfName.PAGE;
    private static final PdfName PAGES = PdfName.PAGES;
    static final PdfName CATALOG = PdfName.CATALOG;
    private PdfName dictionaryType;
    protected HashMap<PdfName, PdfObject> hashMap;

    public PdfDictionary() {
        super(6);
        this.dictionaryType = null;
        this.hashMap = new LinkedHashMap();
    }

    public PdfDictionary(PdfName type) {
        this();
        this.dictionaryType = type;
        put(PdfName.TYPE, this.dictionaryType);
    }

    @Override // com.aowagie.text.pdf.PdfObject
    public void toPdf(PdfWriter writer, OutputStream os) throws IOException {
        os.write(60);
        os.write(60);
        for (Object element : this.hashMap.keySet()) {
            PdfName key = (PdfName) element;
            PdfObject value = this.hashMap.get(key);
            key.toPdf(writer, os);
            int type = value.type();
            if (type != 5 && type != 6 && type != 4 && type != 3) {
                os.write(32);
            }
            value.toPdf(writer, os);
        }
        os.write(62);
        os.write(62);
    }

    @Override // com.aowagie.text.pdf.PdfObject
    public String toString() {
        if (get(PdfName.TYPE) == null) {
            return "Dictionary";
        }
        return "Dictionary of type: " + get(PdfName.TYPE);
    }

    public void put(PdfName key, PdfObject object) {
        if (object == null || object.isNull()) {
            this.hashMap.remove(key);
        } else {
            this.hashMap.put(key, object);
        }
    }

    void putAll(PdfDictionary dic) {
        this.hashMap.putAll(dic.hashMap);
    }

    public void remove(PdfName key) {
        this.hashMap.remove(key);
    }

    public PdfObject get(PdfName key) {
        return this.hashMap.get(key);
    }

    public PdfObject getDirectObject(PdfName key) {
        return PdfReader.getPdfObject(get(key));
    }

    public Set<PdfName> getKeys() {
        return this.hashMap.keySet();
    }

    public int size() {
        return this.hashMap.size();
    }

    boolean contains(PdfName key) {
        return this.hashMap.containsKey(key);
    }

    public boolean isFont() {
        return FONT.equals(this.dictionaryType);
    }

    public boolean isPage() {
        return PAGE.equals(this.dictionaryType);
    }

    public boolean isPages() {
        return PAGES.equals(this.dictionaryType);
    }

    public boolean isCatalog() {
        return CATALOG.equals(this.dictionaryType);
    }

    public boolean isOutlineTree() {
        return OUTLINES.equals(this.dictionaryType);
    }

    void merge(PdfDictionary other) {
        this.hashMap.putAll(other.hashMap);
    }

    void mergeDifferent(PdfDictionary other) {
        for (PdfName key : other.hashMap.keySet()) {
            if (!this.hashMap.containsKey(key)) {
                this.hashMap.put(key, other.hashMap.get(key));
            }
        }
    }

    public PdfDictionary getAsDict(PdfName key) {
        PdfDictionary dict = null;
        PdfObject orig = getDirectObject(key);
        if (orig != null && orig.isDictionary()) {
            dict = (PdfDictionary) orig;
        }
        return dict;
    }

    public PdfArray getAsArray(PdfName key) {
        PdfArray array = null;
        PdfObject orig = getDirectObject(key);
        if (orig != null && orig.isArray()) {
            array = (PdfArray) orig;
        }
        return array;
    }

    public PdfString getAsString(PdfName key) {
        PdfString string = null;
        PdfObject orig = getDirectObject(key);
        if (orig != null && orig.isString()) {
            string = (PdfString) orig;
        }
        return string;
    }

    public PdfNumber getAsNumber(PdfName key) {
        PdfNumber number = null;
        PdfObject orig = getDirectObject(key);
        if (orig != null && orig.isNumber()) {
            number = (PdfNumber) orig;
        }
        return number;
    }

    public PdfName getAsName(PdfName key) {
        PdfName name = null;
        PdfObject orig = getDirectObject(key);
        if (orig != null && orig.isName()) {
            name = (PdfName) orig;
        }
        return name;
    }

    PdfBoolean getAsBoolean(PdfName key) {
        PdfBoolean bool = null;
        PdfObject orig = getDirectObject(key);
        if (orig != null && orig.isBoolean()) {
            bool = (PdfBoolean) orig;
        }
        return bool;
    }

    PdfIndirectReference getAsIndirectObject(PdfName key) {
        PdfIndirectReference ref = null;
        PdfObject orig = get(key);
        if (orig != null && orig.isIndirect()) {
            ref = (PdfIndirectReference) orig;
        }
        return ref;
    }
}
