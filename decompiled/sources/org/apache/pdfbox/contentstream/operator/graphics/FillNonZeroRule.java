package org.apache.pdfbox.contentstream.operator.graphics;

import java.io.IOException;
import java.util.List;
import org.apache.pdfbox.contentstream.operator.Operator;
import org.apache.pdfbox.contentstream.operator.OperatorName;
import org.apache.pdfbox.cos.COSBase;

/* JADX INFO: loaded from: autofirma.jar:org/apache/pdfbox/contentstream/operator/graphics/FillNonZeroRule.class */
public class FillNonZeroRule extends GraphicsOperatorProcessor {
    @Override // org.apache.pdfbox.contentstream.operator.OperatorProcessor
    public final void process(Operator operator, List<COSBase> operands) throws IOException {
        this.context.fillPath(1);
    }

    @Override // org.apache.pdfbox.contentstream.operator.OperatorProcessor
    public String getName() {
        return OperatorName.FILL_NON_ZERO;
    }
}
