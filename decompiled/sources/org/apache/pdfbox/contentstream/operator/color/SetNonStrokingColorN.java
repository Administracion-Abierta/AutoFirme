package org.apache.pdfbox.contentstream.operator.color;

import org.apache.pdfbox.contentstream.operator.OperatorName;

/* JADX INFO: loaded from: autofirma.jar:org/apache/pdfbox/contentstream/operator/color/SetNonStrokingColorN.class */
public class SetNonStrokingColorN extends SetNonStrokingColor {
    @Override // org.apache.pdfbox.contentstream.operator.color.SetNonStrokingColor, org.apache.pdfbox.contentstream.operator.OperatorProcessor
    public String getName() {
        return OperatorName.NON_STROKING_COLOR_N;
    }
}
