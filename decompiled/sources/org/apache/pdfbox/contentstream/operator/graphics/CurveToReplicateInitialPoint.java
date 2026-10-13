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

/* JADX INFO: loaded from: autofirma.jar:org/apache/pdfbox/contentstream/operator/graphics/CurveToReplicateInitialPoint.class */
public class CurveToReplicateInitialPoint extends GraphicsOperatorProcessor {
    private static final Log LOG = LogFactory.getLog(CurveToReplicateInitialPoint.class);

    @Override // org.apache.pdfbox.contentstream.operator.OperatorProcessor
    public void process(Operator operator, List<COSBase> operands) throws IOException {
        if (operands.size() < 4) {
            throw new MissingOperandException(operator, operands);
        }
        if (!checkArrayTypesClass(operands, COSNumber.class)) {
            return;
        }
        COSNumber x2 = (COSNumber) operands.get(0);
        COSNumber y2 = (COSNumber) operands.get(1);
        COSNumber x3 = (COSNumber) operands.get(2);
        COSNumber y3 = (COSNumber) operands.get(3);
        Point2D currentPoint = this.context.getCurrentPoint();
        Point2D.Float point2 = this.context.transformedPoint(x2.floatValue(), y2.floatValue());
        Point2D.Float point3 = this.context.transformedPoint(x3.floatValue(), y3.floatValue());
        if (currentPoint == null) {
            LOG.warn("curveTo (" + point3.x + "," + point3.y + ") without initial MoveTo");
            this.context.moveTo(point3.x, point3.y);
        } else {
            this.context.curveTo((float) currentPoint.getX(), (float) currentPoint.getY(), point2.x, point2.y, point3.x, point3.y);
        }
    }

    @Override // org.apache.pdfbox.contentstream.operator.OperatorProcessor
    public String getName() {
        return OperatorName.CURVE_TO_REPLICATE_INITIAL_POINT;
    }
}
