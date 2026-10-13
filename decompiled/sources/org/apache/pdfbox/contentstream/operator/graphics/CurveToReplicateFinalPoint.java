package org.apache.pdfbox.contentstream.operator.graphics;

import java.awt.geom.Point2D;
import java.io.IOException;
import java.util.List;
import org.apache.pdfbox.contentstream.operator.MissingOperandException;
import org.apache.pdfbox.contentstream.operator.Operator;
import org.apache.pdfbox.contentstream.operator.OperatorName;
import org.apache.pdfbox.cos.COSBase;
import org.apache.pdfbox.cos.COSNumber;

/* JADX INFO: loaded from: autofirma.jar:org/apache/pdfbox/contentstream/operator/graphics/CurveToReplicateFinalPoint.class */
public final class CurveToReplicateFinalPoint extends GraphicsOperatorProcessor {
    @Override // org.apache.pdfbox.contentstream.operator.OperatorProcessor
    public void process(Operator operator, List<COSBase> operands) throws IOException {
        if (operands.size() < 4) {
            throw new MissingOperandException(operator, operands);
        }
        if (!checkArrayTypesClass(operands, COSNumber.class)) {
            return;
        }
        COSNumber x1 = (COSNumber) operands.get(0);
        COSNumber y1 = (COSNumber) operands.get(1);
        COSNumber x3 = (COSNumber) operands.get(2);
        COSNumber y3 = (COSNumber) operands.get(3);
        Point2D.Float point1 = this.context.transformedPoint(x1.floatValue(), y1.floatValue());
        Point2D.Float point3 = this.context.transformedPoint(x3.floatValue(), y3.floatValue());
        this.context.curveTo(point1.x, point1.y, point3.x, point3.y, point3.x, point3.y);
    }

    @Override // org.apache.pdfbox.contentstream.operator.OperatorProcessor
    public String getName() {
        return OperatorName.CURVE_TO_REPLICATE_FINAL_POINT;
    }
}
