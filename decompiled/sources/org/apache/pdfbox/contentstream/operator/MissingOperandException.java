package org.apache.pdfbox.contentstream.operator;

import java.io.IOException;
import java.util.List;
import org.apache.pdfbox.cos.COSBase;

/* JADX INFO: loaded from: autofirma.jar:org/apache/pdfbox/contentstream/operator/MissingOperandException.class */
public final class MissingOperandException extends IOException {
    public MissingOperandException(Operator operator, List<COSBase> operands) {
        super("Operator " + operator.getName() + " has too few operands: " + operands);
    }
}
