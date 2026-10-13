package org.bouncycastle.pqc.jcajce.spec;

import java.security.spec.AlgorithmParameterSpec;
import java.util.HashMap;
import java.util.Map;
import org.bouncycastle.pqc.crypto.hqc.HQCParameters;
import org.bouncycastle.util.Strings;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/pqc/jcajce/spec/HQCParameterSpec.class */
public class HQCParameterSpec implements AlgorithmParameterSpec {
    public static final HQCParameterSpec hqc128 = new HQCParameterSpec(HQCParameters.hqc128);
    public static final HQCParameterSpec hqc192 = new HQCParameterSpec(HQCParameters.hqc192);
    public static final HQCParameterSpec hqc256 = new HQCParameterSpec(HQCParameters.hqc256);
    private static Map parameters = new HashMap();
    private final String name;

    private HQCParameterSpec(HQCParameters hQCParameters) {
        this.name = hQCParameters.getName();
    }

    public String getName() {
        return this.name;
    }

    public static HQCParameterSpec fromName(String str) {
        return (HQCParameterSpec) parameters.get(Strings.toLowerCase(str));
    }

    static {
        parameters.put("hqc128", hqc128);
        parameters.put("hqc192", hqc192);
        parameters.put("hqc256", hqc256);
    }
}
