package com.ctc.wstx.dtd;

import com.ctc.wstx.util.PrefixedName;
import com.ctc.wstx.util.StringUtil;
import java.util.TreeSet;

/* JADX INFO: loaded from: autofirma.jar:com/ctc/wstx/dtd/DFAValidator.class */
public final class DFAValidator extends StructValidator {
    DFAState mState;

    public DFAValidator(DFAState initialState) {
        this.mState = initialState;
    }

    @Override // com.ctc.wstx.dtd.StructValidator
    public StructValidator newInstance() {
        return new DFAValidator(this.mState);
    }

    @Override // com.ctc.wstx.dtd.StructValidator
    public String tryToValidate(PrefixedName elemName) {
        DFAState next = this.mState.findNext(elemName);
        if (next == null) {
            TreeSet<PrefixedName> names = this.mState.getNextNames();
            if (names.size() == 0) {
                return "Expected $END";
            }
            if (this.mState.isAcceptingState()) {
                return "Expected <" + StringUtil.concatEntries(names, ">, <", null) + "> or $END";
            }
            return "Expected <" + StringUtil.concatEntries(names, ">, <", "> or <") + ">";
        }
        this.mState = next;
        return null;
    }

    @Override // com.ctc.wstx.dtd.StructValidator
    public String fullyValid() {
        if (this.mState.isAcceptingState()) {
            return null;
        }
        TreeSet<PrefixedName> names = this.mState.getNextNames();
        return "Expected <" + StringUtil.concatEntries(names, ">, <", "> or <") + ">";
    }
}
