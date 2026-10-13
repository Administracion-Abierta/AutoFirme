package org.apache.pdfbox.contentstream.operator.graphics;

import java.io.IOException;
import java.util.List;
import org.apache.pdfbox.contentstream.operator.Operator;
import org.apache.pdfbox.contentstream.operator.OperatorName;
import org.apache.pdfbox.cos.COSBase;

/* JADX INFO: loaded from: autofirma.jar:org/apache/pdfbox/contentstream/operator/graphics/CloseFillEvenOddAndStrokePath.class */
public final class CloseFillEvenOddAndStrokePath extends GraphicsOperatorProcessor {
    @Override // org.apache.pdfbox.contentstream.operator.OperatorProcessor
    public void process(Operator operator, List<COSBase> operands) throws IOException {
        this.context.processOperator(OperatorName.CLOSE_PATH, operands);
        this.context.processOperator(OperatorName.FILL_EVEN_ODD_AND_STROKE, operands);
    }

    @Override // org.apache.pdfbox.contentstream.operator.OperatorProcessor
    public String getName() {
        return OperatorName.CLOSE_FILL_EVEN_ODD_AND_STROKE;
    }
}
