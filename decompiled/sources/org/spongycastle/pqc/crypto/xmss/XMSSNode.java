package org.spongycastle.pqc.crypto.xmss;

import java.io.Serializable;

/* JADX INFO: loaded from: autofirma.jar:org/spongycastle/pqc/crypto/xmss/XMSSNode.class */
public final class XMSSNode implements Serializable {
    private static final long serialVersionUID = 1;
    private final int height;
    private final byte[] value;

    protected XMSSNode(int height, byte[] value) {
        this.height = height;
        this.value = value;
    }

    public int getHeight() {
        return this.height;
    }

    public byte[] getValue() {
        return XMSSUtil.cloneArray(this.value);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    /* JADX INFO: renamed from: clone, reason: merged with bridge method [inline-methods] */
    public XMSSNode m2876clone() {
        return new XMSSNode(getHeight(), getValue());
    }
}
