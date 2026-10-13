package org.bouncycastle.jcajce.spec;

import java.security.spec.AlgorithmParameterSpec;
import java.util.HashMap;
import java.util.Map;
import org.bouncycastle.util.Strings;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/jcajce/spec/MLDSAParameterSpec.class */
public class MLDSAParameterSpec implements AlgorithmParameterSpec {
    public static final MLDSAParameterSpec ml_dsa_44 = new MLDSAParameterSpec("ML-DSA-44");
    public static final MLDSAParameterSpec ml_dsa_65 = new MLDSAParameterSpec("ML-DSA-65");
    public static final MLDSAParameterSpec ml_dsa_87 = new MLDSAParameterSpec("ML-DSA-87");
    public static final MLDSAParameterSpec ml_dsa_44_with_sha512 = new MLDSAParameterSpec("ML-DSA-44-WITH-SHA512");
    public static final MLDSAParameterSpec ml_dsa_65_with_sha512 = new MLDSAParameterSpec("ML-DSA-65-WITH-SHA512");
    public static final MLDSAParameterSpec ml_dsa_87_with_sha512 = new MLDSAParameterSpec("ML-DSA-87-WITH-SHA512");
    private static Map parameters = new HashMap();
    private final String name;

    private MLDSAParameterSpec(String str) {
        this.name = str;
    }

    public String getName() {
        return this.name;
    }

    public static MLDSAParameterSpec fromName(String str) {
        if (str == null) {
            throw new NullPointerException("name cannot be null");
        }
        MLDSAParameterSpec mLDSAParameterSpec = (MLDSAParameterSpec) parameters.get(Strings.toLowerCase(str));
        if (mLDSAParameterSpec == null) {
            throw new IllegalArgumentException("unknown parameter name: " + str);
        }
        return mLDSAParameterSpec;
    }

    static {
        parameters.put("ml-dsa-44", ml_dsa_44);
        parameters.put("ml-dsa-65", ml_dsa_65);
        parameters.put("ml-dsa-87", ml_dsa_87);
        parameters.put("ml-dsa-44-with-sha512", ml_dsa_44_with_sha512);
        parameters.put("ml-dsa-65-with-sha512", ml_dsa_65_with_sha512);
        parameters.put("ml-dsa-87-with-sha512", ml_dsa_87_with_sha512);
    }
}
