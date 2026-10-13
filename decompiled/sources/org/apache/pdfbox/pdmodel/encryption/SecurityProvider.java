package org.apache.pdfbox.pdmodel.encryption;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.security.Provider;

/* JADX INFO: loaded from: autofirma.jar:org/apache/pdfbox/pdmodel/encryption/SecurityProvider.class */
public class SecurityProvider {
    private static Provider provider = null;

    private SecurityProvider() {
    }

    public static Provider getProvider() throws IOException {
        if (provider == null) {
            try {
                provider = (Provider) Class.forName("org.bouncycastle.jce.provider.BouncyCastleProvider").getDeclaredConstructor(new Class[0]).newInstance(new Object[0]);
            } catch (ClassNotFoundException ex) {
                throw new IOException(ex);
            } catch (IllegalAccessException ex2) {
                throw new IOException(ex2);
            } catch (InstantiationException ex3) {
                throw new IOException(ex3);
            } catch (NoSuchMethodException ex4) {
                throw new IOException(ex4);
            } catch (InvocationTargetException ex5) {
                throw new IOException(ex5);
            }
        }
        return provider;
    }

    public static void setProvider(Provider provider2) {
        provider = provider2;
    }
}
