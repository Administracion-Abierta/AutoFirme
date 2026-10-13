package com.aowagie.text.pdf;

import com.aowagie.text.Document;
import com.aowagie.text.ExceptionConverter;
import java.io.IOException;
import java.util.ArrayList;

/* JADX INFO: loaded from: autofirma.jar:com/aowagie/text/pdf/PdfPages.class */
class PdfPages {
    private final ArrayList pages = new ArrayList();
    private final ArrayList parents = new ArrayList();
    private final int leafSize = 10;
    private final PdfWriter writer;
    private PdfIndirectReference topParent;

    PdfPages(PdfWriter writer) {
        this.writer = writer;
    }

    void addPage(PdfDictionary page) {
        try {
            int size = this.pages.size();
            getClass();
            if (size % 10 == 0) {
                this.parents.add(this.writer.getPdfIndirectReference());
            }
            PdfIndirectReference parent = (PdfIndirectReference) this.parents.get(this.parents.size() - 1);
            page.put(PdfName.PARENT, parent);
            PdfIndirectReference current = this.writer.getCurrentPage();
            this.writer.addToBody(page, current);
            this.pages.add(current);
        } catch (Exception e) {
            throw new ExceptionConverter(e);
        }
    }

    PdfIndirectReference addPageRef(PdfIndirectReference pageRef) {
        try {
            int size = this.pages.size();
            getClass();
            if (size % 10 == 0) {
                this.parents.add(this.writer.getPdfIndirectReference());
            }
            this.pages.add(pageRef);
            return (PdfIndirectReference) this.parents.get(this.parents.size() - 1);
        } catch (Exception e) {
            throw new ExceptionConverter(e);
        }
    }

    PdfIndirectReference writePageTree() throws IOException {
        int count;
        if (this.pages.isEmpty()) {
            throw new IOException("The document has no pages.");
        }
        int leaf = 1;
        ArrayList tParents = this.parents;
        ArrayList tPages = this.pages;
        ArrayList arrayList = new ArrayList();
        while (true) {
            ArrayList nextParents = arrayList;
            getClass();
            leaf *= 10;
            getClass();
            int size = tPages.size();
            getClass();
            int rightCount = size % 10;
            if (rightCount == 0) {
                getClass();
                rightCount = 10;
            }
            for (int p = 0; p < tParents.size(); p++) {
                int thisLeaf = leaf;
                if (p == tParents.size() - 1) {
                    count = rightCount;
                    thisLeaf = this.pages.size() % leaf;
                    if (thisLeaf == 0) {
                        thisLeaf = leaf;
                    }
                } else {
                    count = 10;
                }
                PdfDictionary top = new PdfDictionary(PdfName.PAGES);
                top.put(PdfName.COUNT, new PdfNumber(thisLeaf));
                PdfArray kids = new PdfArray();
                ArrayList internal = kids.getArrayList();
                internal.addAll(tPages.subList(p * 10, (p * 10) + count));
                top.put(PdfName.KIDS, kids);
                if (tParents.size() > 1) {
                    getClass();
                    if (p % 10 == 0) {
                        nextParents.add(this.writer.getPdfIndirectReference());
                    }
                    PdfName pdfName = PdfName.PARENT;
                    getClass();
                    top.put(pdfName, (PdfIndirectReference) nextParents.get(p / 10));
                } else {
                    top.put(PdfName.ITXT, new PdfString(Document.getRelease()));
                }
                this.writer.addToBody(top, (PdfIndirectReference) tParents.get(p));
            }
            if (tParents.size() == 1) {
                this.topParent = (PdfIndirectReference) tParents.get(0);
                return this.topParent;
            }
            tPages = tParents;
            tParents = nextParents;
            arrayList = new ArrayList();
        }
    }
}
