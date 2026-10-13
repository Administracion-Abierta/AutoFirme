package org.apache.pdfbox.contentstream.operator.color;

import java.io.IOException;
import java.util.List;
import org.apache.pdfbox.contentstream.operator.Operator;
import org.apache.pdfbox.contentstream.operator.OperatorName;
import org.apache.pdfbox.cos.COSBase;
import org.apache.pdfbox.cos.COSName;
import org.apache.pdfbox.pdmodel.graphics.color.PDColorSpace;

/* JADX INFO: loaded from: autofirma.jar:org/apache/pdfbox/contentstream/operator/color/SetNonStrokingDeviceRGBColor.class */
public class SetNonStrokingDeviceRGBColor extends SetNonStrokingColor {
    @Override // org.apache.pdfbox.contentstream.operator.color.SetColor, org.apache.pdfbox.contentstream.operator.OperatorProcessor
    public void process(Operator operator, List<COSBase> arguments) throws IOException {
        PDColorSpace cs = this.context.getResources().getColorSpace(COSName.DEVICERGB);
        this.context.getGraphicsState().setNonStrokingColorSpace(cs);
        super.process(operator, arguments);
    }

    @Override // org.apache.pdfbox.contentstream.operator.color.SetNonStrokingColor, org.apache.pdfbox.contentstream.operator.OperatorProcessor
    public String getName() {
        return OperatorName.NON_STROKING_RGB;
    }
}
