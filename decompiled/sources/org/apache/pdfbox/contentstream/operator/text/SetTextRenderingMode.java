package org.apache.pdfbox.contentstream.operator.text;

import java.io.IOException;
import java.util.List;
import org.apache.pdfbox.contentstream.operator.MissingOperandException;
import org.apache.pdfbox.contentstream.operator.Operator;
import org.apache.pdfbox.contentstream.operator.OperatorName;
import org.apache.pdfbox.contentstream.operator.OperatorProcessor;
import org.apache.pdfbox.cos.COSBase;
import org.apache.pdfbox.cos.COSNumber;
import org.apache.pdfbox.pdmodel.graphics.state.RenderingMode;

/* JADX INFO: loaded from: autofirma.jar:org/apache/pdfbox/contentstream/operator/text/SetTextRenderingMode.class */
public class SetTextRenderingMode extends OperatorProcessor {
    @Override // org.apache.pdfbox.contentstream.operator.OperatorProcessor
    public void process(Operator operator, List<COSBase> arguments) throws IOException {
        if (arguments.isEmpty()) {
            throw new MissingOperandException(operator, arguments);
        }
        COSBase base0 = arguments.get(0);
        if (!(base0 instanceof COSNumber)) {
            return;
        }
        COSNumber mode = (COSNumber) base0;
        int val = mode.intValue();
        if (val < 0 || val >= RenderingMode.values().length) {
            return;
        }
        RenderingMode renderingMode = RenderingMode.fromInt(val);
        this.context.getGraphicsState().getTextState().setRenderingMode(renderingMode);
    }

    @Override // org.apache.pdfbox.contentstream.operator.OperatorProcessor
    public String getName() {
        return OperatorName.SET_TEXT_RENDERINGMODE;
    }
}
