package com.aowagie.text.pdf;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;

/* JADX INFO: loaded from: autofirma.jar:com/aowagie/text/pdf/PdfStructureTreeRoot.class */
class PdfStructureTreeRoot extends PdfDictionary {
    private final HashMap parentTree;
    private final PdfIndirectReference reference;
    private final PdfWriter writer;

    PdfStructureTreeRoot(PdfWriter writer) {
        super(PdfName.STRUCTTREEROOT);
        this.parentTree = new LinkedHashMap();
        this.writer = writer;
        this.reference = writer.getPdfIndirectReference();
    }

    public PdfWriter getWriter() {
        return this.writer;
    }

    public PdfIndirectReference getReference() {
        return this.reference;
    }

    private void nodeProcess(PdfDictionary struc, PdfIndirectReference reference) throws IOException {
        PdfObject obj = struc.get(PdfName.K);
        if (obj != null && obj.isArray() && !((PdfObject) ((PdfArray) obj).getArrayList().get(0)).isNumber()) {
            PdfArray ar = (PdfArray) obj;
            ArrayList a = ar.getArrayList();
            for (int k = 0; k < a.size(); k++) {
                PdfStructureElement e = (PdfStructureElement) a.get(k);
                a.set(k, e.getReference());
                nodeProcess(e, e.getReference());
            }
        }
        if (reference != null) {
            this.writer.addToBody(struc, reference);
        }
    }

    void buildTree() throws IOException {
        HashMap numTree = new HashMap();
        for (Integer i : this.parentTree.keySet()) {
            PdfArray ar = (PdfArray) this.parentTree.get(i);
            numTree.put(i, this.writer.addToBody(ar).getIndirectReference());
        }
        PdfDictionary dicTree = PdfNumberTree.writeTree(numTree, this.writer);
        if (dicTree != null) {
            put(PdfName.PARENTTREE, this.writer.addToBody(dicTree).getIndirectReference());
        }
        nodeProcess(this, this.reference);
    }
}
