package es.gob.afirma.signers.xml;

import java.lang.reflect.InvocationTargetException;
import java.security.Provider;
import java.security.Security;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.crypto.NoSuchMechanismException;
import javax.xml.crypto.dsig.XMLSignatureFactory;

/* JADX INFO: loaded from: autofirma.jar:es/gob/afirma/signers/xml/XmlDSigProviderHelper.class */
public class XmlDSigProviderHelper {
    private static final String APACHE_PROVIDER_CLASS = "org.apache.jcp.xml.dsig.internal.dom.XMLDSigRI";
    private static final String SUN_PROVIDER_CLASS = "org.jcp.xml.dsig.internal.dom.XMLDSigRI";
    private static Provider defaultProvider;
    private static final Logger LOGGER = Logger.getLogger("es.gob.afirma");
    private static boolean configured = false;

    public static void configureXmlDSigProvider() {
        configureXmlDSigProvider(false, true);
    }

    public static void configureXmlDSigProvider(boolean z, boolean z2) {
        if (configured && !z) {
            return;
        }
        try {
            defaultProvider = XMLSignatureFactory.getInstance("DOM").getProvider();
        } catch (Exception e) {
            LOGGER.warning("No se encontro un proveedor por defecto para la generacion de firmas XML: " + e);
        }
        System.setProperty("com.sun.org.apache.xml.internal.security.lineFeedOnly", "true");
        System.setProperty("org.apache.xml.security.ignoreLineBreaks", "true");
        try {
            installProvider(z2 ? APACHE_PROVIDER_CLASS : SUN_PROVIDER_CLASS);
        } catch (Throwable th) {
            LOGGER.log(Level.SEVERE, "No se pudo dar preferencia al proveedor configurado", th);
        }
        try {
            Provider provider = XMLSignatureFactory.getInstance("DOM").getProvider();
            LOGGER.info("Se usara el proveedor '" + provider.getName() + "': " + provider.getClass().getName());
        } catch (Exception e2) {
            LOGGER.log(Level.SEVERE, "Error en la verificacion de los proveedores XML", (Throwable) e2);
        } catch (NoSuchMechanismException e3) {
            LOGGER.warning("No hay proveedor instalado para XMLDSig. Se reinstala el proveedor por defecto: " + e3);
            Security.addProvider(defaultProvider);
        }
        configured = true;
    }

    private static void installProvider(String str) throws IllegalAccessException, NoSuchMethodException, InstantiationException, ClassNotFoundException, SecurityException, IllegalArgumentException, InvocationTargetException {
        Provider provider = null;
        Provider[] providers = Security.getProviders();
        for (int i = 0; provider == null && i < providers.length; i++) {
            if (str.equals(providers[i].getClass().getName())) {
                provider = providers[i];
            }
        }
        if (provider != null) {
            Security.removeProvider(provider.getName());
            LOGGER.info("Damos preferencia al proveedor " + provider.getName() + ": " + provider.getClass().getName());
        } else {
            provider = (Provider) Class.forName(str).getDeclaredConstructor(new Class[0]).newInstance(new Object[0]);
            LOGGER.info("Instalamos con la maxima preferencia el proveedor " + provider.getName() + ": " + provider.getClass().getName());
        }
        Security.insertProviderAt(provider, 1);
    }
}
