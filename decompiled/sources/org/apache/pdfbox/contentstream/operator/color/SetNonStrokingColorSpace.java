package org.apache.pdfbox.contentstream.operator.color;

import java.io.IOException;
import java.util.List;
import org.apache.pdfbox.contentstream.operator.Operator;
import org.apache.pdfbox.contentstream.operator.OperatorName;
import org.apache.pdfbox.contentstream.operator.OperatorProcessor;
import org.apache.pdfbox.cos.COSBase;
import org.apache.pdfbox.cos.COSName;
import org.apache.pdfbox.pdmodel.graphics.color.PDColorSpace;

/* JADX INFO: loaded from: autofirma.jar:org/apache/pdfbox/contentstream/operator/color/SetNonStrokingColorSpace.class */
public class SetNonStrokingColorSpace extends OperatorProcessor {
    @Override // org.apache.pdfbox.contentstream.operator.OperatorProcessor
    public void process(Operator operator, List<COSBase> arguments) throws IOException {
        if (arguments.isEmpty()) {
            return;
        }
        COSBase base = arguments.get(0);
        if (!(base instanceof COSName)) {
            return;
        }
        PDColorSpace cs = this.context.getResources().getColorSpace((COSName) base);
        this.context.getGraphicsState().setNonStrokingColorSpace(cs);
        this.context.getGraphicsState().setNonStrokingColor(cs.getInitialColor());
    }

    @Override // org.apache.pdfbox.contentstream.operator.OperatorProcessor
    public String getName() {
        return OperatorName.NON_STROKING_COLORSPACE;
    }
}
