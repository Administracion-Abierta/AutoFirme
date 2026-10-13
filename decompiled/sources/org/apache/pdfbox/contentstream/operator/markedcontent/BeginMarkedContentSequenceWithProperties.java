package org.apache.pdfbox.contentstream.operator.markedcontent;

import java.io.IOException;
import java.util.List;
import org.apache.pdfbox.contentstream.operator.Operator;
import org.apache.pdfbox.contentstream.operator.OperatorName;
import org.apache.pdfbox.contentstream.operator.OperatorProcessor;
import org.apache.pdfbox.cos.COSBase;
import org.apache.pdfbox.cos.COSDictionary;
import org.apache.pdfbox.cos.COSName;

/* JADX INFO: loaded from: autofirma.jar:org/apache/pdfbox/contentstream/operator/markedcontent/BeginMarkedContentSequenceWithProperties.class */
public class BeginMarkedContentSequenceWithProperties extends OperatorProcessor {
    @Override // org.apache.pdfbox.contentstream.operator.OperatorProcessor
    public void process(Operator operator, List<COSBase> arguments) throws IOException {
        COSName tag = null;
        COSDictionary properties = null;
        for (COSBase argument : arguments) {
            if (argument instanceof COSName) {
                tag = (COSName) argument;
            } else if (argument instanceof COSDictionary) {
                properties = (COSDictionary) argument;
            }
        }
        this.context.beginMarkedContentSequence(tag, properties);
    }

    @Override // org.apache.pdfbox.contentstream.operator.OperatorProcessor
    public String getName() {
        return OperatorName.BEGIN_MARKED_CONTENT_SEQ;
    }
}
