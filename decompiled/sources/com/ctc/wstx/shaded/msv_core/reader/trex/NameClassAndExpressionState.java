package com.ctc.wstx.shaded.msv_core.reader.trex;

import com.ctc.wstx.shaded.msv_core.grammar.NameClass;
import com.ctc.wstx.shaded.msv_core.grammar.SimpleNameClass;
import com.ctc.wstx.shaded.msv_core.reader.SequenceState;
import com.ctc.wstx.shaded.msv_core.reader.State;
import com.ctc.wstx.shaded.msv_core.util.StartTagInfo;

/* JADX INFO: loaded from: autofirma.jar:com/ctc/wstx/shaded/msv_core/reader/trex/NameClassAndExpressionState.class */
public abstract class NameClassAndExpressionState extends SequenceState implements NameClassOwner {
    protected NameClass nameClass = null;

    protected String getNamespace() {
        return ((TREXBaseReader) this.reader).targetNamespace;
    }

    @Override // com.ctc.wstx.shaded.msv_core.reader.ExpressionWithChildState, com.ctc.wstx.shaded.msv_core.reader.State
    protected void startSelf() {
        super.startSelf();
        String name = this.startTag.getCollapsedAttribute("name");
        if (name == null) {
            return;
        }
        int idx = name.indexOf(58);
        if (idx != -1) {
            String[] s = this.reader.splitQName(name);
            if (s == null) {
                this.reader.reportError(TREXBaseReader.ERR_UNDECLARED_PREFIX, name);
                this.nameClass = new SimpleNameClass("", name);
                return;
            } else {
                this.nameClass = new SimpleNameClass(s[0], s[1]);
                return;
            }
        }
        this.nameClass = new SimpleNameClass(getNamespace(), name);
    }

    @Override // com.ctc.wstx.shaded.msv_core.reader.trex.NameClassOwner
    public void onEndChild(NameClass p) {
        if (this.nameClass != null) {
            this.reader.reportError(TREXBaseReader.ERR_MORE_THAN_ONE_NAMECLASS);
        }
        this.nameClass = p;
    }

    @Override // com.ctc.wstx.shaded.msv_core.reader.ExpressionWithChildState, com.ctc.wstx.shaded.msv_core.reader.SimpleState
    protected State createChildState(StartTagInfo tag) {
        if (this.nameClass == null) {
            State nextState = ((TREXBaseReader) this.reader).createNameClassChildState(this, tag);
            if (nextState != null) {
                return nextState;
            }
            State nextState2 = this.reader.createExpressionChildState(this, tag);
            if (nextState2 != null) {
                this.reader.reportError(TREXBaseReader.ERR_MISSING_CHILD_NAMECLASS);
                this.nameClass = NameClass.ALL;
                return nextState2;
            }
            return null;
        }
        return this.reader.createExpressionChildState(this, tag);
    }

    @Override // com.ctc.wstx.shaded.msv_core.reader.ExpressionState, com.ctc.wstx.shaded.msv_core.reader.SimpleState
    protected void endSelf() {
        if (this.nameClass == null) {
            this.reader.reportError(TREXBaseReader.ERR_MISSING_CHILD_NAMECLASS);
            this.nameClass = NameClass.ALL;
        }
        super.endSelf();
    }
}
