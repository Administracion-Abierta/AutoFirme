package es.gob.jmulticard.jse.provider;

import es.gob.jmulticard.connection.ApduConnection;
import java.lang.reflect.InvocationTargetException;
import java.security.NoSuchAlgorithmException;
import java.security.Provider;
import java.security.Security;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

/* JADX INFO: loaded from: autofirma.jar:es/gob/jmulticard/jse/provider/ProviderUtil.class */
public final class ProviderUtil {
    public static final String DEFAULT_PROVIDER_CLASSNAME = "es.gob.jmulticard.jse.smartcardio.SmartcardIoConnection";
    private static final List<String> FORBIDDEN_PROVIDERS = Arrays.asList("Ceres430JCAProvider", "SunMSCAPI", "DNIeJCAProvider");

    private ProviderUtil() {
    }

    public static ApduConnection getDefaultConnection() {
        try {
            return (ApduConnection) Class.forName(DEFAULT_PROVIDER_CLASSNAME).getConstructor(new Class[0]).newInstance(new Object[0]);
        } catch (ClassNotFoundException | IllegalAccessException | IllegalArgumentException | InstantiationException | NoSuchMethodException | SecurityException | InvocationTargetException e) {
            throw new IllegalStateException("No se ha podido instanciar la conexion es.gob.jmulticard.jse.smartcardio.SmartcardIoConnection", e);
        }
    }

    public static String getDefaultOtherProvider(String serviceName, String serviceAlgorithm) throws NoSuchAlgorithmException {
        Provider[] providerList = Security.getProviders();
        for (Provider provider : providerList) {
            Set<Provider.Service> serviceList = provider.getServices();
            for (Provider.Service service : serviceList) {
                if (serviceName.equals(service.getType()) && serviceAlgorithm.equals(service.getAlgorithm())) {
                    String providerName = provider.getName();
                    if (!FORBIDDEN_PROVIDERS.contains(providerName) && !providerName.contains("PKCS11")) {
                        return providerName;
                    }
                }
            }
        }
        throw new NoSuchAlgorithmException("No hay proveedor adicional para el servicio " + serviceName + " y el algoritmo " + serviceAlgorithm);
    }
}
