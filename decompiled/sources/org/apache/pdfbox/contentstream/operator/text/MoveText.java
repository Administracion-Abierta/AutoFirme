package org.apache.pdfbox.contentstream.operator.text;

import com.aowagie.text.pdf.ColumnText;
import java.util.List;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.pdfbox.contentstream.operator.MissingOperandException;
import org.apache.pdfbox.contentstream.operator.Operator;
import org.apache.pdfbox.contentstream.operator.OperatorName;
import org.apache.pdfbox.contentstream.operator.OperatorProcessor;
import org.apache.pdfbox.cos.COSBase;
import org.apache.pdfbox.cos.COSNumber;
import org.apache.pdfbox.util.Matrix;

/* JADX INFO: loaded from: autofirma.jar:org/apache/pdfbox/contentstream/operator/text/MoveText.class */
public class MoveText extends OperatorProcessor {
    private static final Log LOG = LogFactory.getLog(MoveText.class);

    @Override // org.apache.pdfbox.contentstream.operator.OperatorProcessor
    public void process(Operator operator, List<COSBase> arguments) throws MissingOperandException {
        if (arguments.size() < 2) {
            throw new MissingOperandException(operator, arguments);
        }
        Matrix textLineMatrix = this.context.getTextLineMatrix();
        if (textLineMatrix == null) {
            LOG.warn("TextLineMatrix is null, " + getName() + " operator will be ignored");
            return;
        }
        COSBase base0 = arguments.get(0);
        COSBase base1 = arguments.get(1);
        if (!(base0 instanceof COSNumber) || !(base1 instanceof COSNumber)) {
            return;
        }
        COSNumber x = (COSNumber) base0;
        COSNumber y = (COSNumber) base1;
        Matrix matrix = new Matrix(1.0f, ColumnText.GLOBAL_SPACE_CHAR_RATIO, ColumnText.GLOBAL_SPACE_CHAR_RATIO, 1.0f, x.floatValue(), y.floatValue());
        textLineMatrix.concatenate(matrix);
        this.context.setTextMatrix(textLineMatrix.m1288clone());
    }

    @Override // org.apache.pdfbox.contentstream.operator.OperatorProcessor
    public String getName() {
        return OperatorName.MOVE_TEXT;
    }
}
