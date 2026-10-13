package com.aowagie.text.pdf;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;

/* JADX INFO: loaded from: autofirma.jar:com/aowagie/text/pdf/PdfReaderInstance.class */
class PdfReaderInstance {
    private static final PdfLiteral IDENTITYMATRIX = new PdfLiteral("[1 0 0 1 0 0]");
    private static final PdfNumber ONE = new PdfNumber(1);
    private final int[] myXref;
    private final PdfReader reader;
    private final RandomAccessFileOrArray file;
    private final PdfWriter writer;
    private final HashMap importedPages = new LinkedHashMap();
    private final HashMap visited = new LinkedHashMap();
    private ArrayList nextRound = new ArrayList();

    PdfReaderInstance(PdfReader reader, PdfWriter writer) {
        this.reader = reader;
        this.writer = writer;
        this.file = reader.getSafeFile();
        this.myXref = new int[reader.getXrefSize()];
    }

    PdfReader getReader() {
        return this.reader;
    }

    PdfImportedPage getImportedPage(int pageNumber) {
        if (!this.reader.isOpenedWithFullPermissions()) {
            throw new IllegalArgumentException("PdfReader not opened with owner password");
        }
        if (pageNumber < 1 || pageNumber > this.reader.getNumberOfPages()) {
            throw new IllegalArgumentException("Invalid page number: " + pageNumber);
        }
        Integer i = Integer.valueOf(pageNumber);
        PdfImportedPage pageT = (PdfImportedPage) this.importedPages.get(i);
        if (pageT == null) {
            pageT = new PdfImportedPage(this, this.writer, pageNumber);
            this.importedPages.put(i, pageT);
        }
        return pageT;
    }

    int getNewObjectNumber(int number, int generation) {
        if (this.myXref[number] == 0) {
            this.myXref[number] = this.writer.getIndirectReferenceNumber();
            this.nextRound.add(Integer.valueOf(number));
        }
        return this.myXref[number];
    }

    RandomAccessFileOrArray getReaderFile() {
        return this.file;
    }

    PdfObject getResources(int pageNumber) {
        PdfObject obj = PdfReader.getPdfObjectRelease(this.reader.getPageNRelease(pageNumber).get(PdfName.RESOURCES));
        return obj;
    }

    PdfStream getFormXObject(int pageNumber, int compressionLevel) throws IOException {
        PRStream stream;
        PdfDictionary page = this.reader.getPageNRelease(pageNumber);
        PdfObject contents = PdfReader.getPdfObjectRelease(page.get(PdfName.CONTENTS));
        PdfDictionary dic = new PdfDictionary();
        byte[] bout = null;
        if (contents != null) {
            if (contents.isStream()) {
                dic.putAll((PRStream) contents);
            } else {
                bout = this.reader.getPageContent(pageNumber, this.file);
            }
        } else {
            bout = new byte[0];
        }
        dic.put(PdfName.RESOURCES, PdfReader.getPdfObjectRelease(page.get(PdfName.RESOURCES)));
        dic.put(PdfName.TYPE, PdfName.XOBJECT);
        dic.put(PdfName.SUBTYPE, PdfName.FORM);
        PdfImportedPage impPage = (PdfImportedPage) this.importedPages.get(Integer.valueOf(pageNumber));
        dic.put(PdfName.BBOX, new PdfRectangle(impPage.getBoundingBox()));
        PdfArray matrix = impPage.getMatrix();
        if (matrix == null) {
            dic.put(PdfName.MATRIX, IDENTITYMATRIX);
        } else {
            dic.put(PdfName.MATRIX, matrix);
        }
        dic.put(PdfName.FORMTYPE, ONE);
        if (bout == null) {
            stream = new PRStream((PRStream) contents, dic);
        } else {
            stream = new PRStream(this.reader, bout, compressionLevel);
            stream.putAll(dic);
        }
        return stream;
    }

    private void writeAllVisited() throws IOException {
        while (!this.nextRound.isEmpty()) {
            ArrayList vec = this.nextRound;
            this.nextRound = new ArrayList();
            for (int k = 0; k < vec.size(); k++) {
                Integer i = (Integer) vec.get(k);
                if (!this.visited.containsKey(i)) {
                    this.visited.put(i, null);
                    int n = i.intValue();
                    this.writer.addToBody(this.reader.getPdfObjectRelease(n), this.myXref[n]);
                }
            }
        }
    }

    void writeAllPages() throws IOException {
        try {
            this.file.reOpen();
            for (PdfImportedPage ip : this.importedPages.values()) {
                this.writer.addToBody(ip.getFormXObject(this.writer.getCompressionLevel()), ip.getIndirectReference());
            }
            writeAllVisited();
        } finally {
            try {
                this.reader.close();
                this.file.close();
            } catch (Exception e) {
            }
        }
    }
}
