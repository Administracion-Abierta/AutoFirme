package org.apache.pdfbox.contentstream.operator.text;

import com.aowagie.text.pdf.ColumnText;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.apache.pdfbox.contentstream.operator.Operator;
import org.apache.pdfbox.contentstream.operator.OperatorName;
import org.apache.pdfbox.contentstream.operator.OperatorProcessor;
import org.apache.pdfbox.cos.COSBase;
import org.apache.pdfbox.cos.COSFloat;

/* JADX INFO: loaded from: autofirma.jar:org/apache/pdfbox/contentstream/operator/text/NextLine.class */
public class NextLine extends OperatorProcessor {
    @Override // org.apache.pdfbox.contentstream.operator.OperatorProcessor
    public void process(Operator operator, List<COSBase> arguments) throws IOException {
        List<COSBase> args = new ArrayList<>(2);
        args.add(new COSFloat(ColumnText.GLOBAL_SPACE_CHAR_RATIO));
        args.add(new COSFloat(-this.context.getGraphicsState().getTextState().getLeading()));
        this.context.processOperator(OperatorName.MOVE_TEXT, args);
    }

    @Override // org.apache.pdfbox.contentstream.operator.OperatorProcessor
    public String getName() {
        return OperatorName.NEXT_LINE;
    }
}
