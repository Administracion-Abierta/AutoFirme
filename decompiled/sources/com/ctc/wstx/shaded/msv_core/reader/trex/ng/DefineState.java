package com.ctc.wstx.shaded.msv_core.reader.trex.ng;

import com.ctc.wstx.shaded.msv_core.grammar.Expression;
import com.ctc.wstx.shaded.msv_core.grammar.ReferenceExp;
import com.ctc.wstx.shaded.msv_core.reader.trex.TREXBaseReader;
import org.xml.sax.Locator;

/* JADX INFO: loaded from: autofirma.jar:com/ctc/wstx/shaded/msv_core/reader/trex/ng/DefineState.class */
public class DefineState extends com.ctc.wstx.shaded.msv_core.reader.trex.DefineState {
    private RELAXNGReader.RefExpParseInfo prevNamedPattern;
    private boolean previousDirectRefernce;

    @Override // com.ctc.wstx.shaded.msv_core.reader.trex.DefineState, com.ctc.wstx.shaded.msv_core.reader.State
    protected void startSelf() {
        RELAXNGReader reader = (RELAXNGReader) this.reader;
        super.startSelf();
        this.prevNamedPattern = reader.currentNamedPattern;
        this.previousDirectRefernce = reader.directRefernce;
        reader.directRefernce = true;
        ReferenceExp exp = getReference();
        if (exp == null) {
            reader.currentNamedPattern = null;
            return;
        }
        reader.currentNamedPattern = reader.getRefExpParseInfo(exp);
        if (reader.currentNamedPattern.redefinition != RELAXNGReader.RefExpParseInfo.notBeingRedefined) {
            reader.currentNamedPattern = null;
        }
    }

    @Override // com.ctc.wstx.shaded.msv_core.reader.trex.DefineState, com.ctc.wstx.shaded.msv_core.reader.SimpleState
    protected void endSelf() {
        RELAXNGReader reader = (RELAXNGReader) this.reader;
        reader.currentNamedPattern = this.prevNamedPattern;
        reader.directRefernce = this.previousDirectRefernce;
        super.endSelf();
    }

    @Override // com.ctc.wstx.shaded.msv_core.reader.trex.DefineState
    protected Expression doCombine(ReferenceExp baseExp, Expression newExp, String combine) {
        RELAXNGReader reader = (RELAXNGReader) this.reader;
        RELAXNGReader.RefExpParseInfo info = reader.getRefExpParseInfo(baseExp);
        if (combine == null) {
            if (info.haveHead) {
                reader.reportError(TREXBaseReader.ERR_COMBINE_MISSING, baseExp.name);
                return baseExp.exp;
            }
            info.haveHead = true;
        } else if (info.combineMethod == null) {
            info.combineMethod = combine.trim();
            if (!info.combineMethod.equals("choice") && !info.combineMethod.equals("interleave")) {
                reader.reportError(TREXBaseReader.ERR_BAD_COMBINE, info.combineMethod);
            }
        } else if (!info.combineMethod.equals(combine)) {
            reader.reportError(new Locator[]{this.location, reader.getDeclaredLocationOf(baseExp)}, RELAXNGReader.ERR_INCONSISTENT_COMBINE, new Object[]{baseExp.name});
            info.combineMethod = null;
            return baseExp.exp;
        }
        if (baseExp.exp == null) {
            return newExp;
        }
        if (info.redefinition != RELAXNGReader.RefExpParseInfo.notBeingRedefined) {
            info.redefinition = RELAXNGReader.RefExpParseInfo.originalFound;
            return baseExp.exp;
        }
        if (info.combineMethod.equals("choice")) {
            return reader.pool.createChoice(baseExp.exp, newExp);
        }
        if (info.combineMethod.equals("interleave")) {
            return reader.pool.createInterleave(baseExp.exp, newExp);
        }
        return null;
    }
}
