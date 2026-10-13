package com.aowagie.text.pdf;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;

/* JADX INFO: loaded from: autofirma.jar:com/aowagie/text/pdf/PRAcroForm.class */
public class PRAcroForm extends PdfDictionary {
    private final PdfReader reader;
    private final ArrayList fields = new ArrayList();
    private final HashMap fieldByName = new LinkedHashMap();
    private final ArrayList stack = new ArrayList();

    /* JADX INFO: loaded from: autofirma.jar:com/aowagie/text/pdf/PRAcroForm$FieldInformation.class */
    public static class FieldInformation {
        private final String name;
        private final PdfDictionary info;
        private final PRIndirectReference ref;

        private FieldInformation(String name, PdfDictionary info, PRIndirectReference ref) {
            this.name = name;
            this.info = info;
            this.ref = ref;
        }

        public String getName() {
            return this.name;
        }

        public PdfDictionary getInfo() {
            return this.info;
        }

        public PRIndirectReference getRef() {
            return this.ref;
        }
    }

    PRAcroForm(PdfReader reader) {
        this.reader = reader;
    }

    @Override // com.aowagie.text.pdf.PdfDictionary
    public int size() {
        return this.fields.size();
    }

    public ArrayList getFields() {
        return this.fields;
    }

    void readAcroForm(PdfDictionary root) {
        if (root == null) {
            return;
        }
        this.hashMap = root.hashMap;
        pushAttrib(root);
        PdfArray fieldlist = (PdfArray) PdfReader.getPdfObjectRelease(root.get(PdfName.FIELDS));
        iterateFields(fieldlist, null, null);
    }

    private void iterateFields(PdfArray fieldlist, PRIndirectReference fieldDict, String title) {
        Iterator it = fieldlist.listIterator();
        while (it.hasNext()) {
            PRIndirectReference ref = (PRIndirectReference) it.next();
            PdfDictionary dict = (PdfDictionary) PdfReader.getPdfObjectRelease(ref);
            PRIndirectReference myFieldDict = fieldDict;
            String myTitle = title;
            PdfString tField = (PdfString) dict.get(PdfName.T);
            boolean isFieldDict = tField != null;
            if (isFieldDict) {
                myFieldDict = ref;
                if (title == null) {
                    myTitle = tField.toString();
                } else {
                    myTitle = title + '.' + tField.toString();
                }
            }
            PdfArray kids = (PdfArray) dict.get(PdfName.KIDS);
            if (kids != null) {
                pushAttrib(dict);
                iterateFields(kids, myFieldDict, myTitle);
                this.stack.remove(this.stack.size() - 1);
            } else if (myFieldDict != null) {
                PdfDictionary mergedDict = (PdfDictionary) this.stack.get(this.stack.size() - 1);
                if (isFieldDict) {
                    mergedDict = mergeAttrib(mergedDict, dict);
                }
                mergedDict.put(PdfName.T, new PdfString(myTitle));
                FieldInformation fi = new FieldInformation(myTitle, mergedDict, myFieldDict);
                this.fields.add(fi);
                this.fieldByName.put(myTitle, fi);
            }
        }
    }

    private PdfDictionary mergeAttrib(PdfDictionary parent, PdfDictionary child) {
        PdfDictionary targ = new PdfDictionary();
        if (parent != null) {
            targ.putAll(parent);
        }
        for (Object element : child.getKeys()) {
            PdfName key = (PdfName) element;
            if (key.equals(PdfName.DR) || key.equals(PdfName.DA) || key.equals(PdfName.Q) || key.equals(PdfName.FF) || key.equals(PdfName.DV) || key.equals(PdfName.V) || key.equals(PdfName.FT) || key.equals(PdfName.F)) {
                targ.put(key, child.get(key));
            }
        }
        return targ;
    }

    private void pushAttrib(PdfDictionary dict) {
        PdfDictionary dic = null;
        if (!this.stack.isEmpty()) {
            dic = (PdfDictionary) this.stack.get(this.stack.size() - 1);
        }
        this.stack.add(mergeAttrib(dic, dict));
    }
}
