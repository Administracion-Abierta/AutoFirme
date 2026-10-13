package org.spongycastle.pqc.jcajce.spec;

import java.security.spec.AlgorithmParameterSpec;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:org/spongycastle/pqc/jcajce/spec/XMSSMTParameterSpec.class */
public class XMSSMTParameterSpec implements AlgorithmParameterSpec {
    public static final String SHA256 = "SHA256";
    public static final String SHA512 = "SHA512";
    public static final String SHAKE128 = "SHAKE128";
    public static final String SHAKE256 = "SHAKE256";
    private final int height;
    private final int layers;
    private final String treeDigest;

    public XMSSMTParameterSpec(int height, int layers, String treeDigest) {
        this.height = height;
        this.layers = layers;
        this.treeDigest = treeDigest;
    }

    public String getTreeDigest() {
        return this.treeDigest;
    }

    public int getHeight() {
        return this.height;
    }

    public int getLayers() {
        return this.layers;
    }
}
