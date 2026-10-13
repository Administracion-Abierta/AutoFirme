package org.apache.pdfbox.contentstream.operator;

import java.io.IOException;
import java.util.List;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.pdfbox.cos.COSBase;
import org.apache.pdfbox.cos.COSName;
import org.apache.pdfbox.pdmodel.graphics.PDXObject;
import org.apache.pdfbox.pdmodel.graphics.form.PDFormXObject;
import org.apache.pdfbox.pdmodel.graphics.form.PDTransparencyGroup;

/* JADX INFO: loaded from: autofirma.jar:org/apache/pdfbox/contentstream/operator/DrawObject.class */
public class DrawObject extends OperatorProcessor {
    private static final Log LOG = LogFactory.getLog(DrawObject.class);

    @Override // org.apache.pdfbox.contentstream.operator.OperatorProcessor
    public void process(Operator operator, List<COSBase> arguments) throws IOException {
        if (arguments.isEmpty()) {
            throw new MissingOperandException(operator, arguments);
        }
        COSBase base0 = arguments.get(0);
        if (!(base0 instanceof COSName)) {
            return;
        }
        COSName name = (COSName) base0;
        if (this.context.getResources().isImageXObject(name)) {
            return;
        }
        PDXObject xobject = this.context.getResources().getXObject(name);
        if (xobject instanceof PDFormXObject) {
            try {
                this.context.increaseLevel();
                if (this.context.getLevel() > 50) {
                    LOG.error("recursion is too deep, skipping form XObject");
                    return;
                }
                if (xobject instanceof PDTransparencyGroup) {
                    this.context.showTransparencyGroup((PDTransparencyGroup) xobject);
                } else {
                    this.context.showForm((PDFormXObject) xobject);
                }
            } finally {
                this.context.decreaseLevel();
            }
        }
    }

    @Override // org.apache.pdfbox.contentstream.operator.OperatorProcessor
    public String getName() {
        return OperatorName.DRAW_OBJECT;
    }
}
