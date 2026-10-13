package com.ctc.wstx.dtd;

import java.util.BitSet;
import java.util.List;

/* JADX INFO: loaded from: autofirma.jar:com/ctc/wstx/dtd/ModelNode.class */
public abstract class ModelNode {
    public abstract ModelNode cloneModel();

    public abstract boolean isNullable();

    public abstract void indexTokens(List<TokenModel> list);

    public abstract void addFirstPos(BitSet bitSet);

    public abstract void addLastPos(BitSet bitSet);

    public abstract void calcFollowPos(BitSet[] bitSetArr);
}
