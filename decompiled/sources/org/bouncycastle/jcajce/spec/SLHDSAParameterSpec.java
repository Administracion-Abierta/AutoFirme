package org.bouncycastle.jcajce.spec;

import java.security.spec.AlgorithmParameterSpec;
import java.util.HashMap;
import java.util.Map;
import org.bouncycastle.util.Strings;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/jcajce/spec/SLHDSAParameterSpec.class */
public class SLHDSAParameterSpec implements AlgorithmParameterSpec {
    public static final SLHDSAParameterSpec slh_dsa_sha2_128f = new SLHDSAParameterSpec("SLH-DSA-SHA2-128F");
    public static final SLHDSAParameterSpec slh_dsa_sha2_128s = new SLHDSAParameterSpec("SLH-DSA-SHA2-128S");
    public static final SLHDSAParameterSpec slh_dsa_sha2_192f = new SLHDSAParameterSpec("SLH-DSA-SHA2-192F");
    public static final SLHDSAParameterSpec slh_dsa_sha2_192s = new SLHDSAParameterSpec("SLH-DSA-SHA2-192S");
    public static final SLHDSAParameterSpec slh_dsa_sha2_256f = new SLHDSAParameterSpec("SLH-DSA-SHA2-256F");
    public static final SLHDSAParameterSpec slh_dsa_sha2_256s = new SLHDSAParameterSpec("SLH-DSA-SHA2-256S");
    public static final SLHDSAParameterSpec slh_dsa_shake_128f = new SLHDSAParameterSpec("SLH-DSA-SHAKE-128F");
    public static final SLHDSAParameterSpec slh_dsa_shake_128s = new SLHDSAParameterSpec("SLH-DSA-SHAKE-128S");
    public static final SLHDSAParameterSpec slh_dsa_shake_192f = new SLHDSAParameterSpec("SLH-DSA-SHAKE-192F");
    public static final SLHDSAParameterSpec slh_dsa_shake_192s = new SLHDSAParameterSpec("SLH-DSA-SHAKE-192S");
    public static final SLHDSAParameterSpec slh_dsa_shake_256f = new SLHDSAParameterSpec("SLH-DSA-SHAKE-256F");
    public static final SLHDSAParameterSpec slh_dsa_shake_256s = new SLHDSAParameterSpec("SLH-DSA-SHAKE-256S");
    public static final SLHDSAParameterSpec slh_dsa_sha2_128f_with_sha256 = new SLHDSAParameterSpec("SLH-DSA-SHA2-128F-WITH-SHA256");
    public static final SLHDSAParameterSpec slh_dsa_sha2_128s_with_sha256 = new SLHDSAParameterSpec("SLH-DSA-SHA2-128S-WITH-SHA256");
    public static final SLHDSAParameterSpec slh_dsa_sha2_192f_with_sha512 = new SLHDSAParameterSpec("SLH-DSA-SHA2-192F-WITH-SHA512");
    public static final SLHDSAParameterSpec slh_dsa_sha2_192s_with_sha512 = new SLHDSAParameterSpec("SLH-DSA-SHA2-192S-WITH-SHA512");
    public static final SLHDSAParameterSpec slh_dsa_sha2_256f_with_sha512 = new SLHDSAParameterSpec("SLH-DSA-SHA2-256F-WITH-SHA512");
    public static final SLHDSAParameterSpec slh_dsa_sha2_256s_with_sha512 = new SLHDSAParameterSpec("SLH-DSA-SHA2-256S-WITH-SHA512");
    public static final SLHDSAParameterSpec slh_dsa_shake_128f_with_shake128 = new SLHDSAParameterSpec("SLH-DSA-SHAKE-128F-WITH-SHAKE128");
    public static final SLHDSAParameterSpec slh_dsa_shake_128s_with_shake128 = new SLHDSAParameterSpec("SLH-DSA-SHAKE-128S-WITH-SHAKE128");
    public static final SLHDSAParameterSpec slh_dsa_shake_192f_with_shake256 = new SLHDSAParameterSpec("SLH-DSA-SHAKE-192F-WITH-SHAKE256");
    public static final SLHDSAParameterSpec slh_dsa_shake_192s_with_shake256 = new SLHDSAParameterSpec("SLH-DSA-SHAKE-192S-WITH-SHAKE256");
    public static final SLHDSAParameterSpec slh_dsa_shake_256f_with_shake256 = new SLHDSAParameterSpec("SLH-DSA-SHAKE-256F-WITH-SHAKE256");
    public static final SLHDSAParameterSpec slh_dsa_shake_256s_with_shake256 = new SLHDSAParameterSpec("SLH-DSA-SHAKE-256S-WITH-SHAKE256");
    private static Map parameters = new HashMap();
    private final String name;

    private SLHDSAParameterSpec(String str) {
        this.name = str;
    }

    public String getName() {
        return this.name;
    }

    public static SLHDSAParameterSpec fromName(String str) {
        if (str == null) {
            throw new NullPointerException("name cannot be null");
        }
        SLHDSAParameterSpec sLHDSAParameterSpec = (SLHDSAParameterSpec) parameters.get(Strings.toLowerCase(str));
        if (sLHDSAParameterSpec == null) {
            throw new IllegalArgumentException("unknown parameter name: " + str);
        }
        return sLHDSAParameterSpec;
    }

    static {
        parameters.put("slh-dsa-sha2-128f", slh_dsa_sha2_128f);
        parameters.put("slh-dsa-sha2-128s", slh_dsa_sha2_128s);
        parameters.put("slh-dsa-sha2-192f", slh_dsa_sha2_192f);
        parameters.put("slh-dsa-sha2-192s", slh_dsa_sha2_192s);
        parameters.put("slh-dsa-sha2-256f", slh_dsa_sha2_256f);
        parameters.put("slh-dsa-sha2-256s", slh_dsa_sha2_256s);
        parameters.put("sha2-128f", slh_dsa_sha2_128f);
        parameters.put("sha2-128s", slh_dsa_sha2_128s);
        parameters.put("sha2-192f", slh_dsa_sha2_192f);
        parameters.put("sha2-192s", slh_dsa_sha2_192s);
        parameters.put("sha2-256f", slh_dsa_sha2_256f);
        parameters.put("sha2-256s", slh_dsa_sha2_256s);
        parameters.put("slh-dsa-shake-128f", slh_dsa_shake_128f);
        parameters.put("slh-dsa-shake-128s", slh_dsa_shake_128s);
        parameters.put("slh-dsa-shake-192f", slh_dsa_shake_192f);
        parameters.put("slh-dsa-shake-192s", slh_dsa_shake_192s);
        parameters.put("slh-dsa-shake-256f", slh_dsa_shake_256f);
        parameters.put("slh-dsa-shake-256s", slh_dsa_shake_256s);
        parameters.put("shake-128f", slh_dsa_shake_128f);
        parameters.put("shake-128s", slh_dsa_shake_128s);
        parameters.put("shake-192f", slh_dsa_shake_192f);
        parameters.put("shake-192s", slh_dsa_shake_192s);
        parameters.put("shake-256f", slh_dsa_shake_256f);
        parameters.put("shake-256s", slh_dsa_shake_256s);
        parameters.put("slh-dsa-sha2-128f-with-sha256", slh_dsa_sha2_128f_with_sha256);
        parameters.put("slh-dsa-sha2-128s-with-sha256", slh_dsa_sha2_128s_with_sha256);
        parameters.put("slh-dsa-sha2-192f-with-sha512", slh_dsa_sha2_192f_with_sha512);
        parameters.put("slh-dsa-sha2-192s-with-sha512", slh_dsa_sha2_192s_with_sha512);
        parameters.put("slh-dsa-sha2-256f-with-sha512", slh_dsa_sha2_256f_with_sha512);
        parameters.put("slh-dsa-sha2-256s-with-sha512", slh_dsa_sha2_256s_with_sha512);
        parameters.put("sha2-128f-with-sha256", slh_dsa_sha2_128f_with_sha256);
        parameters.put("sha2-128s-with-sha256", slh_dsa_sha2_128s_with_sha256);
        parameters.put("sha2-192f-with-sha512", slh_dsa_sha2_192f_with_sha512);
        parameters.put("sha2-192s-with-sha512", slh_dsa_sha2_192s_with_sha512);
        parameters.put("sha2-256f-with-sha512", slh_dsa_sha2_256f_with_sha512);
        parameters.put("sha2-256s-with-sha512", slh_dsa_sha2_256s_with_sha512);
        parameters.put("slh-dsa-shake-128f-with-shake128", slh_dsa_shake_128f_with_shake128);
        parameters.put("slh-dsa-shake-128s-with-shake128", slh_dsa_shake_128s_with_shake128);
        parameters.put("slh-dsa-shake-192f-with-shake256", slh_dsa_shake_192f_with_shake256);
        parameters.put("slh-dsa-shake-192s-with-shake256", slh_dsa_shake_192s_with_shake256);
        parameters.put("slh-dsa-shake-256f-with-shake256", slh_dsa_shake_256f_with_shake256);
        parameters.put("slh-dsa-shake-256s-with-shake256", slh_dsa_shake_256s_with_shake256);
        parameters.put("shake-128f-with-shake128", slh_dsa_shake_128f_with_shake128);
        parameters.put("shake-128s-with-shake128", slh_dsa_shake_128s_with_shake128);
        parameters.put("shake-192f-with-shake256", slh_dsa_shake_192f_with_shake256);
        parameters.put("shake-192s-with-shake256", slh_dsa_shake_192s_with_shake256);
        parameters.put("shake-256f-with-shake256", slh_dsa_shake_256f_with_shake256);
        parameters.put("shake-256s-with-shake256", slh_dsa_shake_256s_with_shake256);
    }
}
