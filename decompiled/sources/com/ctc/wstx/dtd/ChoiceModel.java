package com.ctc.wstx.dtd;

import java.util.BitSet;
import java.util.List;

/* JADX INFO: loaded from: autofirma.jar:com/ctc/wstx/dtd/ChoiceModel.class */
public class ChoiceModel extends ModelNode {
    final ModelNode[] mSubModels;
    boolean mNullable;
    BitSet mFirstPos;
    BitSet mLastPos;

    protected ChoiceModel(ModelNode[] subModels) {
        this.mNullable = false;
        this.mSubModels = subModels;
        boolean nullable = false;
        for (ModelNode modelNode : subModels) {
            if (modelNode.isNullable()) {
                nullable = true;
                break;
            }
        }
        this.mNullable = nullable;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < this.mSubModels.length; i++) {
            if (i > 0) {
                sb.append(" | ");
            }
            sb.append(this.mSubModels[i].toString());
        }
        sb.append(')');
        return sb.toString();
    }

    @Override // com.ctc.wstx.dtd.ModelNode
    public ModelNode cloneModel() {
        int len = this.mSubModels.length;
        ModelNode[] newModels = new ModelNode[len];
        for (int i = 0; i < len; i++) {
            newModels[i] = this.mSubModels[i].cloneModel();
        }
        return new ChoiceModel(newModels);
    }

    @Override // com.ctc.wstx.dtd.ModelNode
    public boolean isNullable() {
        return this.mNullable;
    }

    @Override // com.ctc.wstx.dtd.ModelNode
    public void indexTokens(List<TokenModel> tokens) {
        int len = this.mSubModels.length;
        for (int i = 0; i < len; i++) {
            this.mSubModels[i].indexTokens(tokens);
        }
    }

    @Override // com.ctc.wstx.dtd.ModelNode
    public void addFirstPos(BitSet firstPos) {
        if (this.mFirstPos == null) {
            this.mFirstPos = new BitSet();
            int len = this.mSubModels.length;
            for (int i = 0; i < len; i++) {
                this.mSubModels[i].addFirstPos(this.mFirstPos);
            }
        }
        firstPos.or(this.mFirstPos);
    }

    @Override // com.ctc.wstx.dtd.ModelNode
    public void addLastPos(BitSet lastPos) {
        if (this.mLastPos == null) {
            this.mLastPos = new BitSet();
            int len = this.mSubModels.length;
            for (int i = 0; i < len; i++) {
                this.mSubModels[i].addLastPos(this.mLastPos);
            }
        }
        lastPos.or(this.mLastPos);
    }

    @Override // com.ctc.wstx.dtd.ModelNode
    public void calcFollowPos(BitSet[] followPosSets) {
        int len = this.mSubModels.length;
        for (int i = 0; i < len; i++) {
            this.mSubModels[i].calcFollowPos(followPosSets);
        }
    }
}
