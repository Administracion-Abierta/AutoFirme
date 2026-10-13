package org.spongycastle.jce.provider;

import java.security.Permission;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import javax.crypto.spec.DHParameterSpec;
import org.spongycastle.jcajce.provider.asymmetric.util.EC5Util;
import org.spongycastle.jcajce.provider.config.ProviderConfiguration;
import org.spongycastle.jcajce.provider.config.ProviderConfigurationPermission;
import org.spongycastle.jce.spec.ECParameterSpec;

/* JADX INFO: loaded from: autofirma.jar:org/spongycastle/jce/provider/BouncyCastleProviderConfiguration.class */
class BouncyCastleProviderConfiguration implements ProviderConfiguration {
    private static Permission BC_EC_LOCAL_PERMISSION = new ProviderConfigurationPermission("SC", "threadLocalEcImplicitlyCa");
    private static Permission BC_EC_PERMISSION = new ProviderConfigurationPermission("SC", "ecImplicitlyCa");
    private static Permission BC_DH_LOCAL_PERMISSION = new ProviderConfigurationPermission("SC", "threadLocalDhDefaultParams");
    private static Permission BC_DH_PERMISSION = new ProviderConfigurationPermission("SC", "DhDefaultParams");
    private static Permission BC_EC_CURVE_PERMISSION = new ProviderConfigurationPermission("SC", "acceptableEcCurves");
    private static Permission BC_ADDITIONAL_EC_CURVE_PERMISSION = new ProviderConfigurationPermission("SC", "additionalEcParameters");
    private volatile ECParameterSpec ecImplicitCaParams;
    private volatile Object dhDefaultParams;
    private ThreadLocal ecThreadSpec = new ThreadLocal();
    private ThreadLocal dhThreadSpec = new ThreadLocal();
    private volatile Set acceptableNamedCurves = new HashSet();
    private volatile Map additionalECParameters = new HashMap();

    BouncyCastleProviderConfiguration() {
    }

    void setParameter(String parameterName, Object parameter) {
        ECParameterSpec curveSpec;
        SecurityManager securityManager = System.getSecurityManager();
        if (parameterName.equals("threadLocalEcImplicitlyCa")) {
            if (securityManager != null) {
                securityManager.checkPermission(BC_EC_LOCAL_PERMISSION);
            }
            if ((parameter instanceof ECParameterSpec) || parameter == null) {
                curveSpec = (ECParameterSpec) parameter;
            } else {
                curveSpec = EC5Util.convertSpec((java.security.spec.ECParameterSpec) parameter, false);
            }
            if (curveSpec == null) {
                this.ecThreadSpec.remove();
                return;
            } else {
                this.ecThreadSpec.set(curveSpec);
                return;
            }
        }
        if (parameterName.equals("ecImplicitlyCa")) {
            if (securityManager != null) {
                securityManager.checkPermission(BC_EC_PERMISSION);
            }
            if ((parameter instanceof ECParameterSpec) || parameter == null) {
                this.ecImplicitCaParams = (ECParameterSpec) parameter;
                return;
            } else {
                this.ecImplicitCaParams = EC5Util.convertSpec((java.security.spec.ECParameterSpec) parameter, false);
                return;
            }
        }
        if (parameterName.equals("threadLocalDhDefaultParams")) {
            if (securityManager != null) {
                securityManager.checkPermission(BC_DH_LOCAL_PERMISSION);
            }
            if (!(parameter instanceof DHParameterSpec) && !(parameter instanceof DHParameterSpec[]) && parameter != null) {
                throw new IllegalArgumentException("not a valid DHParameterSpec");
            }
            if (parameter != null) {
                this.dhThreadSpec.set(parameter);
                return;
            } else {
                this.dhThreadSpec.remove();
                return;
            }
        }
        if (parameterName.equals("DhDefaultParams")) {
            if (securityManager != null) {
                securityManager.checkPermission(BC_DH_PERMISSION);
            }
            if ((parameter instanceof DHParameterSpec) || (parameter instanceof DHParameterSpec[]) || parameter == null) {
                this.dhDefaultParams = parameter;
                return;
            }
            throw new IllegalArgumentException("not a valid DHParameterSpec or DHParameterSpec[]");
        }
        if (parameterName.equals("acceptableEcCurves")) {
            if (securityManager != null) {
                securityManager.checkPermission(BC_EC_CURVE_PERMISSION);
            }
            this.acceptableNamedCurves = (Set) parameter;
        } else if (parameterName.equals("additionalEcParameters")) {
            if (securityManager != null) {
                securityManager.checkPermission(BC_ADDITIONAL_EC_CURVE_PERMISSION);
            }
            this.additionalECParameters = (Map) parameter;
        }
    }

    @Override // org.spongycastle.jcajce.provider.config.ProviderConfiguration
    public ECParameterSpec getEcImplicitlyCa() {
        ECParameterSpec spec = (ECParameterSpec) this.ecThreadSpec.get();
        if (spec != null) {
            return spec;
        }
        return this.ecImplicitCaParams;
    }

    @Override // org.spongycastle.jcajce.provider.config.ProviderConfiguration
    public DHParameterSpec getDHDefaultParameters(int keySize) {
        Object params = this.dhThreadSpec.get();
        if (params == null) {
            params = this.dhDefaultParams;
        }
        if (params instanceof DHParameterSpec) {
            DHParameterSpec spec = (DHParameterSpec) params;
            if (spec.getP().bitLength() == keySize) {
                return spec;
            }
            return null;
        }
        if (params instanceof DHParameterSpec[]) {
            DHParameterSpec[] specs = (DHParameterSpec[]) params;
            for (int i = 0; i != specs.length; i++) {
                if (specs[i].getP().bitLength() == keySize) {
                    return specs[i];
                }
            }
            return null;
        }
        return null;
    }

    @Override // org.spongycastle.jcajce.provider.config.ProviderConfiguration
    public Set getAcceptableNamedCurves() {
        return Collections.unmodifiableSet(this.acceptableNamedCurves);
    }

    @Override // org.spongycastle.jcajce.provider.config.ProviderConfiguration
    public Map getAdditionalECParameters() {
        return Collections.unmodifiableMap(this.additionalECParameters);
    }
}
