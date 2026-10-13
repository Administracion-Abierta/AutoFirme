package com.ctc.wstx.shaded.msv_core.reader.trex.classic;

import com.ctc.wstx.shaded.msv_core.grammar.Expression;
import com.ctc.wstx.shaded.msv_core.grammar.ReferenceExp;
import com.ctc.wstx.shaded.msv_core.reader.trex.TREXBaseReader;
import org.xml.sax.Locator;

/* JADX INFO: loaded from: autofirma.jar:com/ctc/wstx/shaded/msv_core/reader/trex/classic/DefineState.class */
public class DefineState extends com.ctc.wstx.shaded.msv_core.reader.trex.DefineState {
    @Override // com.ctc.wstx.shaded.msv_core.reader.trex.DefineState
    protected Expression doCombine(ReferenceExp baseExp, Expression newExp, String combine) {
        TREXGrammarReader reader = (TREXGrammarReader) this.reader;
        if (baseExp.exp == null) {
            if (combine != null) {
                reader.reportWarning(TREXBaseReader.WRN_COMBINE_IGNORED, baseExp.name);
            }
            return newExp;
        }
        if (reader.getDeclaredLocationOf(baseExp).getSystemId().equals(reader.getLocator().getSystemId())) {
            reader.reportError(TREXBaseReader.ERR_DUPLICATE_DEFINITION, baseExp.name);
            return baseExp.exp;
        }
        if (combine == null) {
            reader.reportError(new Locator[]{this.location, reader.getDeclaredLocationOf(baseExp)}, TREXBaseReader.ERR_COMBINE_MISSING, new Object[]{baseExp.name});
            return baseExp.exp;
        }
        if (combine.equals("group")) {
            return reader.pool.createSequence(baseExp.exp, newExp);
        }
        if (combine.equals("choice")) {
            return reader.pool.createChoice(baseExp.exp, newExp);
        }
        if (combine.equals("replace")) {
            return this.exp;
        }
        if (combine.equals("interleave")) {
            return reader.pool.createInterleave(baseExp.exp, newExp);
        }
        if (combine.equals("concur")) {
            return reader.pool.createConcur(baseExp.exp, newExp);
        }
        return null;
    }
}
