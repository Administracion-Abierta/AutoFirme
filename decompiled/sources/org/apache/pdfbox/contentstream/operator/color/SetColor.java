package org.apache.pdfbox.contentstream.operator.color;

import java.io.IOException;
import java.util.List;
import org.apache.pdfbox.contentstream.operator.MissingOperandException;
import org.apache.pdfbox.contentstream.operator.Operator;
import org.apache.pdfbox.contentstream.operator.OperatorProcessor;
import org.apache.pdfbox.cos.COSArray;
import org.apache.pdfbox.cos.COSBase;
import org.apache.pdfbox.cos.COSNumber;
import org.apache.pdfbox.pdmodel.graphics.color.PDColor;
import org.apache.pdfbox.pdmodel.graphics.color.PDColorSpace;
import org.apache.pdfbox.pdmodel.graphics.color.PDPattern;

/* JADX INFO: loaded from: autofirma.jar:org/apache/pdfbox/contentstream/operator/color/SetColor.class */
public abstract class SetColor extends OperatorProcessor {
    protected abstract PDColor getColor();

    protected abstract void setColor(PDColor pDColor);

    protected abstract PDColorSpace getColorSpace();

    @Override // org.apache.pdfbox.contentstream.operator.OperatorProcessor
    public void process(Operator operator, List<COSBase> arguments) throws IOException {
        PDColorSpace colorSpace = getColorSpace();
        if (!(colorSpace instanceof PDPattern)) {
            if (arguments.size() < colorSpace.getNumberOfComponents()) {
                throw new MissingOperandException(operator, arguments);
            }
            if (!checkArrayTypesClass(arguments, COSNumber.class)) {
                return;
            }
        }
        COSArray array = new COSArray();
        array.addAll(arguments);
        setColor(new PDColor(array, colorSpace));
    }
}
