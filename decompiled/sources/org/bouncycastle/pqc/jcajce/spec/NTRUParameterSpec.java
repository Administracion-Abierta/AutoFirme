package org.bouncycastle.pqc.jcajce.spec;

import java.security.spec.AlgorithmParameterSpec;
import java.util.HashMap;
import java.util.Map;
import org.bouncycastle.pqc.crypto.ntru.NTRUParameters;
import org.bouncycastle.util.Strings;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/pqc/jcajce/spec/NTRUParameterSpec.class */
public class NTRUParameterSpec implements AlgorithmParameterSpec {
    public static final NTRUParameterSpec ntruhps2048509 = new NTRUParameterSpec(NTRUParameters.ntruhps2048509);
    public static final NTRUParameterSpec ntruhps2048677 = new NTRUParameterSpec(NTRUParameters.ntruhps2048677);
    public static final NTRUParameterSpec ntruhps4096821 = new NTRUParameterSpec(NTRUParameters.ntruhps4096821);
    public static final NTRUParameterSpec ntruhps40961229 = new NTRUParameterSpec(NTRUParameters.ntruhps40961229);
    public static final NTRUParameterSpec ntruhrss701 = new NTRUParameterSpec(NTRUParameters.ntruhrss701);
    public static final NTRUParameterSpec ntruhrss1373 = new NTRUParameterSpec(NTRUParameters.ntruhrss1373);
    private static Map parameters = new HashMap();
    private final String name;

    private NTRUParameterSpec(NTRUParameters nTRUParameters) {
        this.name = nTRUParameters.getName();
    }

    public String getName() {
        return this.name;
    }

    public static NTRUParameterSpec fromName(String str) {
        return (NTRUParameterSpec) parameters.get(Strings.toLowerCase(str));
    }

    static {
        parameters.put("ntruhps2048509", ntruhps2048509);
        parameters.put("ntruhps2048677", ntruhps2048677);
        parameters.put("ntruhps4096821", ntruhps4096821);
        parameters.put("ntruhps40961229", ntruhps40961229);
        parameters.put("ntruhrss701", ntruhrss701);
        parameters.put("ntruhrss1373", ntruhrss1373);
    }
}
