package org.apache.pdfbox.pdmodel.font;

import java.io.IOException;

/* JADX INFO: loaded from: autofirma.jar:org/apache/pdfbox/pdmodel/font/Subsetter.class */
interface Subsetter {
    void addToSubset(int i);

    void subset() throws IOException;
}
