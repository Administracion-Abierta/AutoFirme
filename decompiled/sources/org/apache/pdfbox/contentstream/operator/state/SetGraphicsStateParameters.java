package org.apache.pdfbox.contentstream.operator.state;

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
import org.apache.pdfbox.pdmodel.graphics.state.PDExtendedGraphicsState;

/* JADX INFO: loaded from: autofirma.jar:org/apache/pdfbox/contentstream/operator/state/SetGraphicsStateParameters.class */
public class SetGraphicsStateParameters extends OperatorProcessor {
    private static final Log LOG = LogFactory.getLog(SetGraphicsStateParameters.class);

    @Override // org.apache.pdfbox.contentstream.operator.OperatorProcessor
    public void process(Operator operator, List<COSBase> arguments) throws IOException {
        if (arguments.isEmpty()) {
            throw new MissingOperandException(operator, arguments);
        }
        COSBase base0 = arguments.get(0);
        if (!(base0 instanceof COSName)) {
            return;
        }
        COSName graphicsName = (COSName) base0;
        PDExtendedGraphicsState gs = this.context.getResources().getExtGState(graphicsName);
        if (gs == null) {
            LOG.error("name for 'gs' operator not found in resources: /" + graphicsName.getName());
        } else {
            gs.copyIntoGraphicsState(this.context.getGraphicsState());
        }
    }

    @Override // org.apache.pdfbox.contentstream.operator.OperatorProcessor
    public String getName() {
        return OperatorName.SET_GRAPHICS_STATE_PARAMS;
    }
}
