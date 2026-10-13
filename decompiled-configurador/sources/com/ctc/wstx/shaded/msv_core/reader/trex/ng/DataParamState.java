package com.ctc.wstx.shaded.msv_core.reader.trex.ng;

import com.ctc.wstx.shaded.msv.relaxng_datatype.DatatypeException;
import com.ctc.wstx.shaded.msv_core.reader.ChildlessState;
import com.ctc.wstx.shaded.msv_core.reader.GrammarReader;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:com/ctc/wstx/shaded/msv_core/reader/trex/ng/DataParamState.class */
public class DataParamState extends ChildlessState {
    protected final StringBuffer text = new StringBuffer();

    @Override // com.ctc.wstx.shaded.msv_core.reader.State, org.xml.sax.ContentHandler
    public void characters(char[] buf, int from, int len) {
        this.text.append(buf, from, len);
    }

    @Override // com.ctc.wstx.shaded.msv_core.reader.State, org.xml.sax.ContentHandler
    public void ignorableWhitespace(char[] buf, int from, int len) {
        this.text.append(buf, from, len);
    }

    @Override // com.ctc.wstx.shaded.msv_core.reader.SimpleState
    protected void endSelf() {
        String facet = this.startTag.getAttribute("name");
        if (facet == null) {
            this.reader.reportError(GrammarReader.ERR_MISSING_ATTRIBUTE, this.startTag.qName, "name");
        } else {
            try {
                ((DataState) this.parentState).typeBuilder.addParameter(facet, this.text.toString(), this.reader);
            } catch (DatatypeException dte) {
                this.reader.reportError(RELAXNGReader.ERR_BAD_FACET, facet, dte.getMessage());
            }
        }
        super.endSelf();
    }
}
