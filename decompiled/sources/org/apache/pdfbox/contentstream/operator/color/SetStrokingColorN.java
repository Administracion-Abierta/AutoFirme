package org.apache.pdfbox.contentstream.operator.color;

import org.apache.pdfbox.contentstream.operator.OperatorName;

/* JADX INFO: loaded from: autofirma.jar:org/apache/pdfbox/contentstream/operator/color/SetStrokingColorN.class */
public class SetStrokingColorN extends SetStrokingColor {
    @Override // org.apache.pdfbox.contentstream.operator.color.SetStrokingColor, org.apache.pdfbox.contentstream.operator.OperatorProcessor
    public String getName() {
        return OperatorName.STROKING_COLOR_N;
    }
}
