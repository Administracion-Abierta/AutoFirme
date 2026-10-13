package com.ctc.wstx.dtd;

import java.util.BitSet;
import java.util.List;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:com/ctc/wstx/dtd/ConcatModel.class */
public class ConcatModel extends ModelNode {
    ModelNode mLeftModel;
    ModelNode mRightModel;
    final boolean mNullable;
    BitSet mFirstPos;
    BitSet mLastPos;

    public ConcatModel(ModelNode left, ModelNode right) {
        this.mLeftModel = left;
        this.mRightModel = right;
        this.mNullable = this.mLeftModel.isNullable() && this.mRightModel.isNullable();
    }

    @Override // com.ctc.wstx.dtd.ModelNode
    public ModelNode cloneModel() {
        return new ConcatModel(this.mLeftModel.cloneModel(), this.mRightModel.cloneModel());
    }

    @Override // com.ctc.wstx.dtd.ModelNode
    public boolean isNullable() {
        return this.mNullable;
    }

    @Override // com.ctc.wstx.dtd.ModelNode
    public void indexTokens(List<TokenModel> tokens) {
        this.mLeftModel.indexTokens(tokens);
        this.mRightModel.indexTokens(tokens);
    }

    @Override // com.ctc.wstx.dtd.ModelNode
    public void addFirstPos(BitSet pos) {
        if (this.mFirstPos == null) {
            this.mFirstPos = new BitSet();
            this.mLeftModel.addFirstPos(this.mFirstPos);
            if (this.mLeftModel.isNullable()) {
                this.mRightModel.addFirstPos(this.mFirstPos);
            }
        }
        pos.or(this.mFirstPos);
    }

    @Override // com.ctc.wstx.dtd.ModelNode
    public void addLastPos(BitSet pos) {
        if (this.mLastPos == null) {
            this.mLastPos = new BitSet();
            this.mRightModel.addLastPos(this.mLastPos);
            if (this.mRightModel.isNullable()) {
                this.mLeftModel.addLastPos(this.mLastPos);
            }
        }
        pos.or(this.mLastPos);
    }

    @Override // com.ctc.wstx.dtd.ModelNode
    public void calcFollowPos(BitSet[] followPosSets) {
        this.mLeftModel.calcFollowPos(followPosSets);
        this.mRightModel.calcFollowPos(followPosSets);
        BitSet foll = new BitSet();
        this.mRightModel.addFirstPos(foll);
        BitSet toAddTo = new BitSet();
        this.mLeftModel.addLastPos(toAddTo);
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
        return '(' + this.mLeftModel.toString() + ", " + this.mRightModel.toString() + ')';
    }
}
