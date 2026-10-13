package com.ctc.wstx.shaded.msv_core.reader.relax.core;

import com.ctc.wstx.shaded.msv_core.grammar.Expression;
import com.ctc.wstx.shaded.msv_core.grammar.ReferenceExp;
import com.ctc.wstx.shaded.msv_core.grammar.relax.AttPoolClause;
import com.ctc.wstx.shaded.msv_core.reader.GrammarReader;
import org.xml.sax.Locator;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:com/ctc/wstx/shaded/msv_core/reader/relax/core/AttPoolState.class */
public class AttPoolState extends ClauseState {
    @Override // com.ctc.wstx.shaded.msv_core.reader.SimpleState
    protected void endSelf() {
        super.endSelf();
        String role = this.startTag.getAttribute("role");
        if (role == null) {
            this.reader.reportError(GrammarReader.ERR_MISSING_ATTRIBUTE, "attPool", "role");
            return;
        }
        if (this.startTag.getAttribute("combine") == null) {
            AttPoolClause c = getReader().module.attPools.getOrCreate(role);
            if (c.exp != null) {
                this.reader.reportError(new Locator[]{getReader().getDeclaredLocationOf(c), this.location}, RELAXCoreReader.ERR_MULTIPLE_ATTPOOL_DECLARATIONS, new Object[]{role});
            }
            c.exp = this.exp;
            getReader().setDeclaredLocationOf(c);
            return;
        }
        ReferenceExp e = getReader().combinedAttPools._getOrCreate(role);
        if (e.exp == null) {
            e.exp = Expression.epsilon;
        }
        e.exp = this.reader.pool.createSequence(this.exp, e.exp);
        this.reader.setDeclaredLocationOf(e);
    }
}
