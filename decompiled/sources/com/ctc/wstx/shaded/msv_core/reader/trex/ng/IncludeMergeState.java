package com.ctc.wstx.shaded.msv_core.reader.trex.ng;

import com.ctc.wstx.shaded.msv_core.grammar.Expression;
import com.ctc.wstx.shaded.msv_core.grammar.ReferenceExp;
import com.ctc.wstx.shaded.msv_core.reader.ExpressionOwner;
import com.ctc.wstx.shaded.msv_core.reader.State;
import com.ctc.wstx.shaded.msv_core.util.StartTagInfo;
import java.util.HashSet;
import java.util.Set;

/* JADX INFO: loaded from: autofirma.jar:com/ctc/wstx/shaded/msv_core/reader/trex/ng/IncludeMergeState.class */
public class IncludeMergeState extends com.ctc.wstx.shaded.msv_core.reader.trex.IncludeMergeState implements ExpressionOwner {
    private final Set redefinedPatterns = new HashSet();

    @Override // com.ctc.wstx.shaded.msv_core.reader.trex.IncludeMergeState, com.ctc.wstx.shaded.msv_core.reader.SimpleState
    protected State createChildState(StartTagInfo tag) {
        RELAXNGReader reader = (RELAXNGReader) this.reader;
        if (tag.localName.equals("define")) {
            return reader.getStateFactory().redefine(this, tag);
        }
        if (tag.localName.equals("start")) {
            return reader.getStateFactory().redefineStart(this, tag);
        }
        return null;
    }

    @Override // com.ctc.wstx.shaded.msv_core.reader.ExpressionOwner
    public void onEndChild(Expression child) {
        if (child instanceof ReferenceExp) {
            this.redefinedPatterns.add(child);
        }
    }

    @Override // com.ctc.wstx.shaded.msv_core.reader.trex.IncludeMergeState, com.ctc.wstx.shaded.msv_core.reader.SimpleState
    public void endSelf() {
        RELAXNGReader reader = (RELAXNGReader) this.reader;
        ReferenceExp[] patterns = (ReferenceExp[]) this.redefinedPatterns.toArray(new ReferenceExp[0]);
        RELAXNGReader.RefExpParseInfo[] old = new RELAXNGReader.RefExpParseInfo[patterns.length];
        for (int i = 0; i < patterns.length; i++) {
            RELAXNGReader.RefExpParseInfo info = reader.getRefExpParseInfo(patterns[i]);
            old[i] = new RELAXNGReader.RefExpParseInfo();
            old[i].set(info);
            info.haveHead = false;
            info.combineMethod = null;
            info.redefinition = RELAXNGReader.RefExpParseInfo.originalNotFoundYet;
        }
        super.endSelf();
        for (int i2 = 0; i2 < patterns.length; i2++) {
            if (reader.getRefExpParseInfo(patterns[i2]).redefinition == RELAXNGReader.RefExpParseInfo.originalNotFoundYet) {
                reader.reportError(RELAXNGReader.ERR_REDEFINING_UNDEFINED, patterns[i2].name);
            }
            reader.getRefExpParseInfo(patterns[i2]).set(old[i2]);
        }
    }
}
