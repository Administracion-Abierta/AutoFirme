package org.apache.pdfbox.contentstream.operator.graphics;

import java.awt.geom.Point2D;
import java.io.IOException;
import java.util.List;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.pdfbox.contentstream.operator.MissingOperandException;
import org.apache.pdfbox.contentstream.operator.Operator;
import org.apache.pdfbox.contentstream.operator.OperatorName;
import org.apache.pdfbox.cos.COSBase;
import org.apache.pdfbox.cos.COSNumber;

/* JADX INFO: loaded from: autofirma.jar:org/apache/pdfbox/contentstream/operator/graphics/LineTo.class */
public class LineTo extends GraphicsOperatorProcessor {
    private static final Log LOG = LogFactory.getLog(LineTo.class);

    @Override // org.apache.pdfbox.contentstream.operator.OperatorProcessor
    public void process(Operator operator, List<COSBase> operands) throws IOException {
        if (operands.size() < 2) {
            throw new MissingOperandException(operator, operands);
        }
        COSBase base0 = operands.get(0);
        if (!(base0 instanceof COSNumber)) {
            return;
        }
        COSBase base1 = operands.get(1);
        if (!(base1 instanceof COSNumber)) {
            return;
        }
        COSNumber x = (COSNumber) base0;
        COSNumber y = (COSNumber) base1;
        Point2D.Float pos = this.context.transformedPoint(x.floatValue(), y.floatValue());
        if (this.context.getCurrentPoint() == null) {
            LOG.warn("LineTo (" + pos.x + "," + pos.y + ") without initial MoveTo");
            this.context.moveTo(pos.x, pos.y);
        } else {
            this.context.lineTo(pos.x, pos.y);
        }
    }

    @Override // org.apache.pdfbox.contentstream.operator.OperatorProcessor
    public String getName() {
        return OperatorName.LINE_TO;
    }
}
