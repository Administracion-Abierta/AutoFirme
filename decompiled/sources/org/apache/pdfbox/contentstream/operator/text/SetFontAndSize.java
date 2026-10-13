package org.apache.pdfbox.contentstream.operator.text;

import java.io.IOException;
import java.util.List;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.pdfbox.contentstream.operator.MissingOperandException;
import org.apache.pdfbox.contentstream.operator.Operator;
import org.apache.pdfbox.contentstream.operator.OperatorName;
import org.apache.pdfbox.contentstream.operator.OperatorProcessor;
import org.apache.pdfbox.cos.COSBase;
import org.apache.pdfbox.cos.COSName;
import org.apache.pdfbox.cos.COSNumber;
import org.apache.pdfbox.pdmodel.font.PDFont;

/* JADX INFO: loaded from: autofirma.jar:org/apache/pdfbox/contentstream/operator/text/SetFontAndSize.class */
public class SetFontAndSize extends OperatorProcessor {
    private static final Log LOG = LogFactory.getLog(SetFontAndSize.class);

    @Override // org.apache.pdfbox.contentstream.operator.OperatorProcessor
    public void process(Operator operator, List<COSBase> arguments) throws IOException {
        if (arguments.size() < 2) {
            throw new MissingOperandException(operator, arguments);
        }
        COSBase base0 = arguments.get(0);
        COSBase base1 = arguments.get(1);
        if (!(base0 instanceof COSName) || !(base1 instanceof COSNumber)) {
            return;
        }
        COSName fontName = (COSName) base0;
        float fontSize = ((COSNumber) base1).floatValue();
        this.context.getGraphicsState().getTextState().setFontSize(fontSize);
        PDFont font = this.context.getResources().getFont(fontName);
        if (font == null) {
            LOG.warn("font '" + fontName.getName() + "' not found in resources");
        }
        this.context.getGraphicsState().getTextState().setFont(font);
    }

    @Override // org.apache.pdfbox.contentstream.operator.OperatorProcessor
    public String getName() {
        return OperatorName.SET_FONT_AND_SIZE;
    }
}
