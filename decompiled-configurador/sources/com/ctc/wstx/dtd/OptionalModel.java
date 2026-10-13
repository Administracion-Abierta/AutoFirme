package com.ctc.wstx.dtd;

import java.util.BitSet;
import java.util.List;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:com/ctc/wstx/dtd/OptionalModel.class */
public class OptionalModel extends ModelNode {
    ModelNode mModel;

    public OptionalModel(ModelNode model) {
        this.mModel = model;
    }

    @Override // com.ctc.wstx.dtd.ModelNode
    public ModelNode cloneModel() {
        return new OptionalModel(this.mModel.cloneModel());
    }

    @Override // com.ctc.wstx.dtd.ModelNode
    public boolean isNullable() {
        return true;
    }

    @Override // com.ctc.wstx.dtd.ModelNode
    public void indexTokens(List<TokenModel> tokens) {
        this.mModel.indexTokens(tokens);
    }

    @Override // com.ctc.wstx.dtd.ModelNode
    public void addFirstPos(BitSet pos) {
        this.mModel.addFirstPos(pos);
    }

    @Override // com.ctc.wstx.dtd.ModelNode
    public void addLastPos(BitSet pos) {
        this.mModel.addLastPos(pos);
    }

    @Override // com.ctc.wstx.dtd.ModelNode
    public void calcFollowPos(BitSet[] followPosSets) {
        this.mModel.calcFollowPos(followPosSets);
    }

    public String toString() {
        return this.mModel + "[?]";
    }
}
