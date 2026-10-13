package org.apache.pdfbox.cos;

import java.io.IOException;
import org.apache.pdfbox.pdmodel.common.COSObjectable;

/* JADX INFO: loaded from: autofirma.jar:org/apache/pdfbox/cos/COSBase.class */
public abstract class COSBase implements COSObjectable {
    private boolean direct;

    public abstract Object accept(ICOSVisitor iCOSVisitor) throws IOException;

    @Override // org.apache.pdfbox.pdmodel.common.COSObjectable
    public COSBase getCOSObject() {
        return this;
    }

    public boolean isDirect() {
        return this.direct;
    }

    public void setDirect(boolean direct) {
        this.direct = direct;
    }
}
