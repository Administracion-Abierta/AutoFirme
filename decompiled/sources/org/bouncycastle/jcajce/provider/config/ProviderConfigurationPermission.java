package org.bouncycastle.jcajce.provider.config;

import java.security.BasicPermission;
import java.security.Permission;
import java.util.StringTokenizer;
import org.bouncycastle.util.Strings;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/jcajce/provider/config/ProviderConfigurationPermission.class */
public class ProviderConfigurationPermission extends BasicPermission {
    private static final int THREAD_LOCAL_EC_IMPLICITLY_CA = 1;
    private static final int EC_IMPLICITLY_CA = 2;
    private static final int THREAD_LOCAL_DH_DEFAULT_PARAMS = 4;
    private static final int DH_DEFAULT_PARAMS = 8;
    private static final int ACCEPTABLE_EC_CURVES = 16;
    private static final int ADDITIONAL_EC_PARAMETERS = 32;
    private static final int ALL = 63;
    private static final String THREAD_LOCAL_EC_IMPLICITLY_CA_STR = "threadlocalecimplicitlyca";
    private static final String EC_IMPLICITLY_CA_STR = "ecimplicitlyca";
    private static final String THREAD_LOCAL_DH_DEFAULT_PARAMS_STR = "threadlocaldhdefaultparams";
    private static final String DH_DEFAULT_PARAMS_STR = "dhdefaultparams";
    private static final String ACCEPTABLE_EC_CURVES_STR = "acceptableeccurves";
    private static final String ADDITIONAL_EC_PARAMETERS_STR = "additionalecparameters";
    private static final String ALL_STR = "all";
    private final String actions;
    private final int permissionMask;

    public ProviderConfigurationPermission(String str) {
        super(str);
        this.actions = "all";
        this.permissionMask = 63;
    }

    public ProviderConfigurationPermission(String str, String str2) {
        super(str, str2);
        this.actions = str2;
        this.permissionMask = calculateMask(str2);
    }

    private int calculateMask(String str) {
        StringTokenizer stringTokenizer = new StringTokenizer(Strings.toLowerCase(str), " ,");
        int i = 0;
        while (stringTokenizer.hasMoreTokens()) {
            String strNextToken = stringTokenizer.nextToken();
            if (strNextToken.equals(THREAD_LOCAL_EC_IMPLICITLY_CA_STR)) {
                i |= 1;
            } else if (strNextToken.equals(EC_IMPLICITLY_CA_STR)) {
                i |= 2;
            } else if (strNextToken.equals(THREAD_LOCAL_DH_DEFAULT_PARAMS_STR)) {
                i |= 4;
            } else if (strNextToken.equals(DH_DEFAULT_PARAMS_STR)) {
                i |= 8;
            } else if (strNextToken.equals(ACCEPTABLE_EC_CURVES_STR)) {
                i |= 16;
            } else if (strNextToken.equals(ADDITIONAL_EC_PARAMETERS_STR)) {
                i |= 32;
            } else if (strNextToken.equals("all")) {
                i |= 63;
            }
        }
        if (i == 0) {
            throw new IllegalArgumentException("unknown permissions passed to mask");
        }
        return i;
    }

    @Override // java.security.BasicPermission, java.security.Permission
    public String getActions() {
        return this.actions;
    }

    @Override // java.security.BasicPermission, java.security.Permission
    public boolean implies(Permission permission) {
        if (!(permission instanceof ProviderConfigurationPermission) || !getName().equals(permission.getName())) {
            return false;
        }
        ProviderConfigurationPermission providerConfigurationPermission = (ProviderConfigurationPermission) permission;
        return (this.permissionMask & providerConfigurationPermission.permissionMask) == providerConfigurationPermission.permissionMask;
    }

    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof ProviderConfigurationPermission)) {
            return false;
        }
        ProviderConfigurationPermission providerConfigurationPermission = (ProviderConfigurationPermission) obj;
        return this.permissionMask == providerConfigurationPermission.permissionMask && getName().equals(providerConfigurationPermission.getName());
    }

    public int hashCode() {
        return getName().hashCode() + this.permissionMask;
    }
}
