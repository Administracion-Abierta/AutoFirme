package org.spongycastle.jcajce.provider.config;

import java.util.Map;
import java.util.Set;
import javax.crypto.spec.DHParameterSpec;
import org.spongycastle.jce.spec.ECParameterSpec;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:org/spongycastle/jcajce/provider/config/ProviderConfiguration.class */
public interface ProviderConfiguration {
    ECParameterSpec getEcImplicitlyCa();

    DHParameterSpec getDHDefaultParameters(int i);

    Set getAcceptableNamedCurves();

    Map getAdditionalECParameters();
}
