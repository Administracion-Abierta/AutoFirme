package com.ctc.wstx.shaded.msv_core.reader.trex.ng;

import com.ctc.wstx.shaded.msv_core.grammar.DifferenceNameClass;
import com.ctc.wstx.shaded.msv_core.grammar.NameClass;
import com.ctc.wstx.shaded.msv_core.grammar.NamespaceNameClass;
import com.ctc.wstx.shaded.msv_core.reader.State;
import com.ctc.wstx.shaded.msv_core.reader.trex.NameClassWithChildState;
import com.ctc.wstx.shaded.msv_core.util.StartTagInfo;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:com/ctc/wstx/shaded/msv_core/reader/trex/ng/NGNameState.class */
public abstract class NGNameState extends NameClassWithChildState {
    protected abstract NameClass getMainNameClass();

    NGNameState() {
        this.allowNullChild = true;
    }

    @Override // com.ctc.wstx.shaded.msv_core.reader.trex.NameClassWithChildState, com.ctc.wstx.shaded.msv_core.reader.SimpleState
    protected State createChildState(StartTagInfo tag) {
        if (this.nameClass == null && tag.localName.equals("except")) {
            return ((RELAXNGReader) this.reader).getStateFactory().nsExcept(this, tag);
        }
        return null;
    }

    @Override // com.ctc.wstx.shaded.msv_core.reader.trex.NameClassWithChildState
    protected NameClass castNameClass(NameClass halfCastedNameClass, NameClass newChildNameClass) {
        return newChildNameClass;
    }

    @Override // com.ctc.wstx.shaded.msv_core.reader.trex.NameClassWithChildState
    protected NameClass annealNameClass(NameClass nameClass) {
        NameClass r = getMainNameClass();
        if (nameClass != null) {
            r = new DifferenceNameClass(r, nameClass);
        }
        return r;
    }

    /* JADX INFO: loaded from: autofirmaConfigurador.jar:com/ctc/wstx/shaded/msv_core/reader/trex/ng/NGNameState$AnyNameState.class */
    public static class AnyNameState extends NGNameState {
        @Override // com.ctc.wstx.shaded.msv_core.reader.trex.ng.NGNameState
        protected NameClass getMainNameClass() {
            return NameClass.ALL;
        }
    }

    /* JADX INFO: loaded from: autofirmaConfigurador.jar:com/ctc/wstx/shaded/msv_core/reader/trex/ng/NGNameState$NsNameState.class */
    public static class NsNameState extends NGNameState {
        @Override // com.ctc.wstx.shaded.msv_core.reader.trex.ng.NGNameState
        protected NameClass getMainNameClass() {
            return new NamespaceNameClass(getPropagatedNamespace());
        }
    }
}
