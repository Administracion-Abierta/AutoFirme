package com.aowagie.text.pdf;

import java.util.ArrayList;

/* JADX INFO: loaded from: autofirma.jar:com/aowagie/text/pdf/PdfLayer.class */
class PdfLayer extends PdfDictionary implements PdfOCG {
    private PdfIndirectReference ref;
    private ArrayList children;
    private PdfLayer parent;
    private final String title;
    private boolean on = true;
    private boolean onPanel = true;

    PdfLayer(String title) {
        this.title = title;
    }

    String getTitle() {
        return this.title;
    }

    void addChild(PdfLayer child) {
        if (child.parent != null) {
            throw new IllegalArgumentException("The layer '" + ((PdfString) child.get(PdfName.NAME)).toUnicodeString() + "' already has a parent.");
        }
        child.parent = this;
        if (this.children == null) {
            this.children = new ArrayList();
        }
        this.children.add(child);
    }

    public PdfLayer getParent() {
        return this.parent;
    }

    public ArrayList getChildren() {
        return this.children;
    }

    @Override // com.aowagie.text.pdf.PdfOCG
    public PdfIndirectReference getRef() {
        return this.ref;
    }

    void setRef(PdfIndirectReference ref) {
        this.ref = ref;
    }

    public void setName(String name) {
        put(PdfName.NAME, new PdfString(name, PdfObject.TEXT_UNICODE));
    }

    @Override // com.aowagie.text.pdf.PdfOCG
    public PdfObject getPdfObject() {
        return this;
    }

    public boolean isOn() {
        return this.on;
    }

    public void setOn(boolean on) {
        this.on = on;
    }

    private PdfDictionary getUsage() {
        PdfDictionary usage = (PdfDictionary) get(PdfName.USAGE);
        if (usage == null) {
            usage = new PdfDictionary();
            put(PdfName.USAGE, usage);
        }
        return usage;
    }

    public void setExport(boolean export) {
        PdfDictionary usage = getUsage();
        PdfDictionary dic = new PdfDictionary();
        dic.put(PdfName.EXPORTSTATE, export ? PdfName.ON : PdfName.OFF);
        usage.put(PdfName.EXPORT, dic);
    }

    public void setView(boolean view) {
        PdfDictionary usage = getUsage();
        PdfDictionary dic = new PdfDictionary();
        dic.put(PdfName.VIEWSTATE, view ? PdfName.ON : PdfName.OFF);
        usage.put(PdfName.VIEW, dic);
    }

    public boolean isOnPanel() {
        return this.onPanel;
    }

    public void setOnPanel(boolean onPanel) {
        this.onPanel = onPanel;
    }
}
