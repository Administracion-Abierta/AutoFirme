package org.apache.pdfbox.contentstream.operator.graphics;

import java.io.IOException;
import java.util.List;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.pdfbox.contentstream.operator.MissingOperandException;
import org.apache.pdfbox.contentstream.operator.Operator;
import org.apache.pdfbox.contentstream.operator.OperatorName;
import org.apache.pdfbox.cos.COSBase;
import org.apache.pdfbox.cos.COSName;
import org.apache.pdfbox.pdmodel.MissingResourceException;
import org.apache.pdfbox.pdmodel.graphics.PDXObject;
import org.apache.pdfbox.pdmodel.graphics.form.PDFormXObject;
import org.apache.pdfbox.pdmodel.graphics.form.PDTransparencyGroup;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;

/* JADX INFO: loaded from: autofirma.jar:org/apache/pdfbox/contentstream/operator/graphics/DrawObject.class */
public final class DrawObject extends GraphicsOperatorProcessor {
    private static final Log LOG = LogFactory.getLog(DrawObject.class);

    @Override // org.apache.pdfbox.contentstream.operator.OperatorProcessor
    public void process(Operator operator, List<COSBase> operands) throws IOException {
        if (operands.isEmpty()) {
            throw new MissingOperandException(operator, operands);
        }
        COSBase base0 = operands.get(0);
        if (!(base0 instanceof COSName)) {
            return;
        }
        COSName objectName = (COSName) base0;
        PDXObject xobject = this.context.getResources().getXObject(objectName);
        if (xobject == null) {
            throw new MissingResourceException("Missing XObject: " + objectName.getName());
        }
        if (xobject instanceof PDImageXObject) {
            PDImageXObject image = (PDImageXObject) xobject;
            this.context.drawImage(image);
            return;
        }
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
