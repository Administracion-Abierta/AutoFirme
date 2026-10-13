package org.apache.pdfbox.contentstream.operator;

import java.io.IOException;
import java.util.List;
import org.apache.pdfbox.contentstream.PDFStreamEngine;
import org.apache.pdfbox.cos.COSBase;

/* JADX INFO: loaded from: autofirma.jar:org/apache/pdfbox/contentstream/operator/OperatorProcessor.class */
public abstract class OperatorProcessor {
    protected PDFStreamEngine context;

    public abstract void process(Operator operator, List<COSBase> list) throws IOException;

    public abstract String getName();

    protected OperatorProcessor() {
    }

    protected final PDFStreamEngine getContext() {
        return this.context;
    }

    public void setContext(PDFStreamEngine context) {
        this.context = context;
    }

    public boolean checkArrayTypesClass(List<COSBase> operands, Class<?> clazz) {
        for (COSBase base : operands) {
            if (!clazz.isInstance(base)) {
                return false;
            }
        }
        return true;
    }
}
