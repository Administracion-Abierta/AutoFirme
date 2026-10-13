package com.ctc.wstx.dtd;

import java.util.BitSet;
import java.util.List;

/* JADX INFO: loaded from: autofirma.jar:com/ctc/wstx/dtd/StarModel.class */
public class StarModel extends ModelNode {
    ModelNode mModel;

    public StarModel(ModelNode model) {
        this.mModel = model;
    }

    @Override // com.ctc.wstx.dtd.ModelNode
    public ModelNode cloneModel() {
        return new StarModel(this.mModel.cloneModel());
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
        BitSet foll = new BitSet();
        this.mModel.addFirstPos(foll);
        BitSet toAddTo = new BitSet();
        this.mModel.addLastPos(toAddTo);
        int ix = 0;
        while (true) {
            int iNextSetBit = toAddTo.nextSetBit(ix + 1);
            ix = iNextSetBit;
            if (iNextSetBit >= 0) {
                followPosSets[ix].or(foll);
            } else {
                return;
            }
        }
    }

    public String toString() {
        return this.mModel.toString() + "*";
    }
}
