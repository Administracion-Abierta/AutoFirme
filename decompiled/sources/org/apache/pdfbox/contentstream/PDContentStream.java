package org.apache.pdfbox.contentstream;

import java.io.IOException;
import java.io.InputStream;
import org.apache.pdfbox.pdmodel.PDResources;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.util.Matrix;

/* JADX INFO: loaded from: autofirma.jar:org/apache/pdfbox/contentstream/PDContentStream.class */
public interface PDContentStream {
    InputStream getContents() throws IOException;

    PDResources getResources();

    PDRectangle getBBox();

    Matrix getMatrix();
}
