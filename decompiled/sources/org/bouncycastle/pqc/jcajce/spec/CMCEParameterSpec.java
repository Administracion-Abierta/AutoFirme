package org.bouncycastle.pqc.jcajce.spec;

import java.security.spec.AlgorithmParameterSpec;
import java.util.HashMap;
import java.util.Map;
import org.bouncycastle.pqc.crypto.cmce.CMCEParameters;
import org.bouncycastle.util.Strings;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/pqc/jcajce/spec/CMCEParameterSpec.class */
public class CMCEParameterSpec implements AlgorithmParameterSpec {
    public static final CMCEParameterSpec mceliece348864 = new CMCEParameterSpec(CMCEParameters.mceliece348864r3);
    public static final CMCEParameterSpec mceliece348864f = new CMCEParameterSpec(CMCEParameters.mceliece348864fr3);
    public static final CMCEParameterSpec mceliece460896 = new CMCEParameterSpec(CMCEParameters.mceliece460896r3);
    public static final CMCEParameterSpec mceliece460896f = new CMCEParameterSpec(CMCEParameters.mceliece460896fr3);
    public static final CMCEParameterSpec mceliece6688128 = new CMCEParameterSpec(CMCEParameters.mceliece6688128r3);
    public static final CMCEParameterSpec mceliece6688128f = new CMCEParameterSpec(CMCEParameters.mceliece6688128fr3);
    public static final CMCEParameterSpec mceliece6960119 = new CMCEParameterSpec(CMCEParameters.mceliece6960119r3);
    public static final CMCEParameterSpec mceliece6960119f = new CMCEParameterSpec(CMCEParameters.mceliece6960119fr3);
    public static final CMCEParameterSpec mceliece8192128 = new CMCEParameterSpec(CMCEParameters.mceliece8192128r3);
    public static final CMCEParameterSpec mceliece8192128f = new CMCEParameterSpec(CMCEParameters.mceliece8192128fr3);
    private static Map parameters = new HashMap();
    private final String name;

    private CMCEParameterSpec(CMCEParameters cMCEParameters) {
        this.name = cMCEParameters.getName();
    }

    public String getName() {
        return this.name;
    }

    public static CMCEParameterSpec fromName(String str) {
        return (CMCEParameterSpec) parameters.get(Strings.toLowerCase(str));
    }

    static {
        parameters.put("mceliece348864", mceliece348864);
        parameters.put("mceliece348864f", mceliece348864f);
        parameters.put("mceliece460896", mceliece460896);
        parameters.put("mceliece460896f", mceliece460896f);
        parameters.put("mceliece6688128", mceliece6688128);
        parameters.put("mceliece6688128f", mceliece6688128f);
        parameters.put("mceliece6960119", mceliece6960119);
        parameters.put("mceliece6960119f", mceliece6960119f);
        parameters.put("mceliece8192128", mceliece8192128);
        parameters.put("mceliece8192128f", mceliece8192128f);
    }
}
