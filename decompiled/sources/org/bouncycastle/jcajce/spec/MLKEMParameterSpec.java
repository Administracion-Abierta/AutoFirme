package org.bouncycastle.jcajce.spec;

import java.security.spec.AlgorithmParameterSpec;
import java.util.HashMap;
import java.util.Map;
import org.bouncycastle.util.Strings;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/jcajce/spec/MLKEMParameterSpec.class */
public class MLKEMParameterSpec implements AlgorithmParameterSpec {
    public static final MLKEMParameterSpec ml_kem_512 = new MLKEMParameterSpec("ML-KEM-512");
    public static final MLKEMParameterSpec ml_kem_768 = new MLKEMParameterSpec("ML-KEM-768");
    public static final MLKEMParameterSpec ml_kem_1024 = new MLKEMParameterSpec("ML-KEM-1024");
    private static Map parameters = new HashMap();
    private final String name;

    private MLKEMParameterSpec(String str) {
        this.name = str;
    }

    public String getName() {
        return this.name;
    }

    public static MLKEMParameterSpec fromName(String str) {
        if (str == null) {
            throw new NullPointerException("name cannot be null");
        }
        MLKEMParameterSpec mLKEMParameterSpec = (MLKEMParameterSpec) parameters.get(Strings.toLowerCase(str));
        if (mLKEMParameterSpec == null) {
            throw new IllegalArgumentException("unknown parameter name: " + str);
        }
        return mLKEMParameterSpec;
    }

    static {
        parameters.put("ml-kem-512", ml_kem_512);
        parameters.put("ml-kem-768", ml_kem_768);
        parameters.put("ml-kem-1024", ml_kem_1024);
        parameters.put("kyber512", ml_kem_512);
        parameters.put("kyber768", ml_kem_768);
        parameters.put("kyber1024", ml_kem_1024);
    }
}
