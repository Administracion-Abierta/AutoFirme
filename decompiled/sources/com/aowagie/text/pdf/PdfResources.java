package com.aowagie.text.pdf;

/* JADX INFO: loaded from: autofirma.jar:com/aowagie/text/pdf/PdfResources.class */
class PdfResources extends PdfDictionary {
    PdfResources() {
    }

    void add(PdfName key, PdfDictionary resource) {
        if (resource.size() == 0) {
            return;
        }
        PdfDictionary dic = getAsDict(key);
        if (dic == null) {
            put(key, resource);
        } else {
            dic.putAll(resource);
        }
    }
}
