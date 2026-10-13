package com.ctc.wstx.dtd;

import com.ctc.wstx.util.PrefixedName;
import java.util.BitSet;
import java.util.List;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:com/ctc/wstx/dtd/TokenModel.class */
public final class TokenModel extends ModelNode {
    static final TokenModel NULL_TOKEN = new TokenModel(null);
    final PrefixedName mElemName;
    int mTokenIndex = -1;

    static {
        NULL_TOKEN.mTokenIndex = 0;
    }

    public TokenModel(PrefixedName elemName) {
        this.mElemName = elemName;
    }

    public static TokenModel getNullToken() {
        return NULL_TOKEN;
    }

    public PrefixedName getName() {
        return this.mElemName;
    }

    @Override // com.ctc.wstx.dtd.ModelNode
    public ModelNode cloneModel() {
        return new TokenModel(this.mElemName);
    }

    @Override // com.ctc.wstx.dtd.ModelNode
    public boolean isNullable() {
        return false;
    }

    @Override // com.ctc.wstx.dtd.ModelNode
    public void indexTokens(List<TokenModel> tokens) {
        if (this != NULL_TOKEN) {
            int index = tokens.size();
            this.mTokenIndex = index;
            tokens.add(this);
        }
    }

    @Override // com.ctc.wstx.dtd.ModelNode
    public void addFirstPos(BitSet firstPos) {
        firstPos.set(this.mTokenIndex);
    }

    @Override // com.ctc.wstx.dtd.ModelNode
    public void addLastPos(BitSet lastPos) {
        lastPos.set(this.mTokenIndex);
    }

    @Override // com.ctc.wstx.dtd.ModelNode
    public void calcFollowPos(BitSet[] followPosSets) {
    }

    public String toString() {
        return this.mElemName == null ? "[null]" : this.mElemName.toString();
    }
}
