package org.apache.pdfbox.contentstream.operator.text;

import java.io.IOException;
import java.util.List;
import org.apache.pdfbox.contentstream.operator.Operator;
import org.apache.pdfbox.contentstream.operator.OperatorName;
import org.apache.pdfbox.contentstream.operator.OperatorProcessor;
import org.apache.pdfbox.cos.COSBase;

/* JADX INFO: loaded from: autofirma.jar:org/apache/pdfbox/contentstream/operator/text/ShowTextLine.class */
public class ShowTextLine extends OperatorProcessor {
    @Override // org.apache.pdfbox.contentstream.operator.OperatorProcessor
    public void process(Operator operator, List<COSBase> arguments) throws IOException {
        this.context.processOperator(OperatorName.NEXT_LINE, (List<COSBase>) null);
        this.context.processOperator(OperatorName.SHOW_TEXT, arguments);
    }

    @Override // org.apache.pdfbox.contentstream.operator.OperatorProcessor
    public String getName() {
        return OperatorName.SHOW_TEXT_LINE;
    }
}
