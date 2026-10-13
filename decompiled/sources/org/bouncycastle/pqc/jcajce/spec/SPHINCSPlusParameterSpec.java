package org.bouncycastle.pqc.jcajce.spec;

import java.security.spec.AlgorithmParameterSpec;
import java.util.HashMap;
import java.util.Map;
import org.bouncycastle.util.Strings;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/pqc/jcajce/spec/SPHINCSPlusParameterSpec.class */
public class SPHINCSPlusParameterSpec implements AlgorithmParameterSpec {
    public static final SPHINCSPlusParameterSpec sha2_128f_robust = new SPHINCSPlusParameterSpec("sha2-128f-robust");
    public static final SPHINCSPlusParameterSpec sha2_128s_robust = new SPHINCSPlusParameterSpec("sha2-128s-robust");
    public static final SPHINCSPlusParameterSpec sha2_192f_robust = new SPHINCSPlusParameterSpec("sha2-192f-robust");
    public static final SPHINCSPlusParameterSpec sha2_192s_robust = new SPHINCSPlusParameterSpec("sha2-192s-robust");
    public static final SPHINCSPlusParameterSpec sha2_256f_robust = new SPHINCSPlusParameterSpec("sha2-256f-robust");
    public static final SPHINCSPlusParameterSpec sha2_256s_robust = new SPHINCSPlusParameterSpec("sha2-256s-robust");
    public static final SPHINCSPlusParameterSpec sha2_128f = new SPHINCSPlusParameterSpec("sha2-128f");
    public static final SPHINCSPlusParameterSpec sha2_128s = new SPHINCSPlusParameterSpec("sha2-128s");
    public static final SPHINCSPlusParameterSpec sha2_192f = new SPHINCSPlusParameterSpec("sha2-192f");
    public static final SPHINCSPlusParameterSpec sha2_192s = new SPHINCSPlusParameterSpec("sha2-192s");
    public static final SPHINCSPlusParameterSpec sha2_256f = new SPHINCSPlusParameterSpec("sha2-256f");
    public static final SPHINCSPlusParameterSpec sha2_256s = new SPHINCSPlusParameterSpec("sha2-256s");
    public static final SPHINCSPlusParameterSpec shake_128f_robust = new SPHINCSPlusParameterSpec("shake-128f-robust");
    public static final SPHINCSPlusParameterSpec shake_128s_robust = new SPHINCSPlusParameterSpec("shake-128s-robust");
    public static final SPHINCSPlusParameterSpec shake_192f_robust = new SPHINCSPlusParameterSpec("shake-192f-robust");
    public static final SPHINCSPlusParameterSpec shake_192s_robust = new SPHINCSPlusParameterSpec("shake-192s-robust");
    public static final SPHINCSPlusParameterSpec shake_256f_robust = new SPHINCSPlusParameterSpec("shake-256f-robust");
    public static final SPHINCSPlusParameterSpec shake_256s_robust = new SPHINCSPlusParameterSpec("shake-256s-robust");
    public static final SPHINCSPlusParameterSpec shake_128f = new SPHINCSPlusParameterSpec("shake-128f");
    public static final SPHINCSPlusParameterSpec shake_128s = new SPHINCSPlusParameterSpec("shake-128s");
    public static final SPHINCSPlusParameterSpec shake_192f = new SPHINCSPlusParameterSpec("shake-192f");
    public static final SPHINCSPlusParameterSpec shake_192s = new SPHINCSPlusParameterSpec("shake-192s");
    public static final SPHINCSPlusParameterSpec shake_256f = new SPHINCSPlusParameterSpec("shake-256f");
    public static final SPHINCSPlusParameterSpec shake_256s = new SPHINCSPlusParameterSpec("shake-256s");
    public static final SPHINCSPlusParameterSpec haraka_128f = new SPHINCSPlusParameterSpec("haraka-128f-robust");
    public static final SPHINCSPlusParameterSpec haraka_128s = new SPHINCSPlusParameterSpec("haraka-128s-robust");
    public static final SPHINCSPlusParameterSpec haraka_256f = new SPHINCSPlusParameterSpec("haraka-256f-robust");
    public static final SPHINCSPlusParameterSpec haraka_256s = new SPHINCSPlusParameterSpec("haraka-256s-robust");
    public static final SPHINCSPlusParameterSpec haraka_192f = new SPHINCSPlusParameterSpec("haraka-192f-robust");
    public static final SPHINCSPlusParameterSpec haraka_192s = new SPHINCSPlusParameterSpec("haraka-192s-robust");
    public static final SPHINCSPlusParameterSpec haraka_128f_simple = new SPHINCSPlusParameterSpec("haraka-128f-simple");
    public static final SPHINCSPlusParameterSpec haraka_128s_simple = new SPHINCSPlusParameterSpec("haraka-128s-simple");
    public static final SPHINCSPlusParameterSpec haraka_192f_simple = new SPHINCSPlusParameterSpec("haraka-192f-simple");
    public static final SPHINCSPlusParameterSpec haraka_192s_simple = new SPHINCSPlusParameterSpec("haraka-192s-simple");
    public static final SPHINCSPlusParameterSpec haraka_256f_simple = new SPHINCSPlusParameterSpec("haraka-256f-simple");
    public static final SPHINCSPlusParameterSpec haraka_256s_simple = new SPHINCSPlusParameterSpec("haraka-256s-simple");
    private static Map parameters = new HashMap();
    private final String name;

    private SPHINCSPlusParameterSpec(String str) {
        this.name = str;
    }

    public String getName() {
        return this.name;
    }

    public static SPHINCSPlusParameterSpec fromName(String str) {
        return (SPHINCSPlusParameterSpec) parameters.get(Strings.toLowerCase(str));
    }

    static {
        parameters.put(sha2_128f_robust.getName(), sha2_128f_robust);
        parameters.put(sha2_128s_robust.getName(), sha2_128s_robust);
        parameters.put(sha2_192f_robust.getName(), sha2_192f_robust);
        parameters.put(sha2_192s_robust.getName(), sha2_192s_robust);
        parameters.put(sha2_256f_robust.getName(), sha2_256f_robust);
        parameters.put(sha2_256s_robust.getName(), sha2_256s_robust);
        parameters.put(sha2_128f.getName(), sha2_128f);
        parameters.put(sha2_128s.getName(), sha2_128s);
        parameters.put(sha2_192f.getName(), sha2_192f);
        parameters.put(sha2_192s.getName(), sha2_192s);
        parameters.put(sha2_256f.getName(), sha2_256f);
        parameters.put(sha2_256s.getName(), sha2_256s);
        parameters.put("sha2-128f", sha2_128f);
        parameters.put("sha2-128s", sha2_128s);
        parameters.put("sha2-192f", sha2_192f);
        parameters.put("sha2-192s", sha2_192s);
        parameters.put("sha2-256f", sha2_256f);
        parameters.put("sha2-256s", sha2_256s);
        parameters.put(shake_128f_robust.getName(), shake_128f_robust);
        parameters.put(shake_128s_robust.getName(), shake_128s_robust);
        parameters.put(shake_192f_robust.getName(), shake_192f_robust);
        parameters.put(shake_192s_robust.getName(), shake_192s_robust);
        parameters.put(shake_256f_robust.getName(), shake_256f_robust);
        parameters.put(shake_256s_robust.getName(), shake_256s_robust);
        parameters.put(shake_128f.getName(), shake_128f);
        parameters.put(shake_128s.getName(), shake_128s);
        parameters.put(shake_192f.getName(), shake_192f);
        parameters.put(shake_192s.getName(), shake_192s);
        parameters.put(shake_256f.getName(), shake_256f);
        parameters.put(shake_256s.getName(), shake_256s);
        parameters.put("shake-128f", shake_128f);
        parameters.put("shake-128s", shake_128s);
        parameters.put("shake-192f", shake_192f);
        parameters.put("shake-192s", shake_192s);
        parameters.put("shake-256f", shake_256f);
        parameters.put("shake-256s", shake_256s);
        parameters.put(haraka_128f.getName(), haraka_128f);
        parameters.put(haraka_128s.getName(), haraka_128s);
        parameters.put(haraka_192f.getName(), haraka_192f);
        parameters.put(haraka_192s.getName(), haraka_192s);
        parameters.put(haraka_256f.getName(), haraka_256f);
        parameters.put(haraka_256s.getName(), haraka_256s);
        parameters.put(haraka_128f_simple.getName(), haraka_128f_simple);
        parameters.put(haraka_128s_simple.getName(), haraka_128s_simple);
        parameters.put(haraka_192f_simple.getName(), haraka_192f_simple);
        parameters.put(haraka_192s_simple.getName(), haraka_192s_simple);
        parameters.put(haraka_256f_simple.getName(), haraka_256f_simple);
        parameters.put(haraka_256s_simple.getName(), haraka_256s_simple);
    }
}
