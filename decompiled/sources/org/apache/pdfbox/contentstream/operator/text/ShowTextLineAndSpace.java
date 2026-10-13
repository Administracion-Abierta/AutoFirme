package org.apache.pdfbox.contentstream.operator.text;

import java.io.IOException;
import java.util.List;
import org.apache.pdfbox.contentstream.operator.MissingOperandException;
import org.apache.pdfbox.contentstream.operator.Operator;
import org.apache.pdfbox.contentstream.operator.OperatorName;
import org.apache.pdfbox.contentstream.operator.OperatorProcessor;
import org.apache.pdfbox.cos.COSBase;

/* JADX INFO: loaded from: autofirma.jar:org/apache/pdfbox/contentstream/operator/text/ShowTextLineAndSpace.class */
public class ShowTextLineAndSpace extends OperatorProcessor {
    @Override // org.apache.pdfbox.contentstream.operator.OperatorProcessor
    public void process(Operator operator, List<COSBase> arguments) throws IOException {
        if (arguments.size() < 3) {
            throw new MissingOperandException(operator, arguments);
        }
        this.context.processOperator(OperatorName.SET_WORD_SPACING, arguments.subList(0, 1));
        this.context.processOperator(OperatorName.SET_CHAR_SPACING, arguments.subList(1, 2));
        this.context.processOperator(OperatorName.SHOW_TEXT_LINE, arguments.subList(2, 3));
    }

    @Override // org.apache.pdfbox.contentstream.operator.OperatorProcessor
    public String getName() {
        return OperatorName.SHOW_TEXT_LINE_AND_SPACE;
    }
}
