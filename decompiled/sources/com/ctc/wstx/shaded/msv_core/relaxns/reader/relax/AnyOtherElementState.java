package com.ctc.wstx.shaded.msv_core.relaxns.reader.relax;

import com.ctc.wstx.shaded.msv_core.grammar.Expression;
import com.ctc.wstx.shaded.msv_core.reader.ExpressionWithoutChildState;
import com.ctc.wstx.shaded.msv_core.reader.GrammarReader;
import com.ctc.wstx.shaded.msv_core.relaxns.grammar.relax.AnyOtherElementExp;
import org.xml.sax.Locator;

/* JADX INFO: loaded from: autofirma.jar:com/ctc/wstx/shaded/msv_core/relaxns/reader/relax/AnyOtherElementState.class */
public class AnyOtherElementState extends ExpressionWithoutChildState {
    @Override // com.ctc.wstx.shaded.msv_core.reader.ExpressionState
    protected Expression makeExpression() {
        String in = this.startTag.getAttribute("includeNamespace");
        String ex = this.startTag.getAttribute("excludeNamespace");
        if (in != null && ex != null) {
            this.reader.reportError(new Locator[]{this.location}, GrammarReader.ERR_CONFLICTING_ATTRIBUTES, new Object[]{"includeNamespace", "excludeNamespace"});
            ex = null;
        }
        if (in == null && ex == null) {
            ex = "";
        }
        AnyOtherElementExp exp = new AnyOtherElementExp(this.location, in, ex);
        ((RELAXCoreIslandSchemaReader) this.reader).pendingAnyOtherElements.add(exp);
        return exp;
    }
}
