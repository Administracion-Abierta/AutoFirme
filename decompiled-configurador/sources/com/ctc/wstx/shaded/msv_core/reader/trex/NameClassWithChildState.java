package com.ctc.wstx.shaded.msv_core.reader.trex;

import com.ctc.wstx.shaded.msv_core.grammar.NameClass;
import com.ctc.wstx.shaded.msv_core.reader.State;
import com.ctc.wstx.shaded.msv_core.util.StartTagInfo;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:com/ctc/wstx/shaded/msv_core/reader/trex/NameClassWithChildState.class */
public abstract class NameClassWithChildState extends NameClassState implements NameClassOwner {
    protected NameClass nameClass = null;
    protected boolean allowNullChild = false;

    protected abstract NameClass castNameClass(NameClass nameClass, NameClass nameClass2);

    @Override // com.ctc.wstx.shaded.msv_core.reader.trex.NameClassOwner
    public final void onEndChild(NameClass childNameClass) {
        this.nameClass = castNameClass(this.nameClass, childNameClass);
    }

    @Override // com.ctc.wstx.shaded.msv_core.reader.trex.NameClassState
    protected final NameClass makeNameClass() {
        if (this.nameClass == null && !this.allowNullChild) {
            this.reader.reportError(TREXBaseReader.ERR_MISSING_CHILD_NAMECLASS);
            this.nameClass = NameClass.ALL;
        }
        return annealNameClass(this.nameClass);
    }

    @Override // com.ctc.wstx.shaded.msv_core.reader.SimpleState
    protected State createChildState(StartTagInfo tag) {
        return ((TREXBaseReader) this.reader).createNameClassChildState(this, tag);
    }

    protected NameClass annealNameClass(NameClass nameClass) {
        return nameClass;
    }
}
