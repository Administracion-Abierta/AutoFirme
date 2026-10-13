package es.gob.afirma.standalone.protocol;

import es.gob.afirma.core.misc.Platform;
import es.gob.afirma.standalone.so.macos.MacUtils;
import java.io.IOException;
import java.net.BindException;
import java.net.SocketTimeoutException;
import java.security.GeneralSecurityException;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.net.ssl.SSLServerSocket;
import javax.net.ssl.SSLServerSocketFactory;
import javax.swing.Timer;

/* JADX INFO: loaded from: autofirma.jar:es/gob/afirma/standalone/protocol/ServiceInvocationManager.class */
public final class ServiceInvocationManager {
    private static final int CURRENT_PROTOCOL_VERSION = 3;
    static final Logger LOGGER = Logger.getLogger("es.gob.afirma");
    private static int SOCKET_TIMEOUT = 90000;
    private static final int[] SUPPORTED_PROTOCOL_VERSIONS = {1, 2, 3};
    private static final String[] ENABLED_CIPHER_SUITES = {"TLS_ECDHE_ECDSA_WITH_AES_256_GCM_SHA384", "TLS_ECDHE_ECDSA_WITH_AES_128_GCM_SHA256", "TLS_ECDHE_RSA_WITH_AES_256_GCM_SHA384", "TLS_RSA_WITH_AES_256_GCM_SHA384", "TLS_ECDH_ECDSA_WITH_AES_256_GCM_SHA384", "TLS_ECDH_RSA_WITH_AES_256_GCM_SHA384", "TLS_DHE_RSA_WITH_AES_256_GCM_SHA384", "TLS_DHE_DSS_WITH_AES_256_GCM_SHA384", "TLS_ECDHE_RSA_WITH_AES_128_GCM_SHA256", "TLS_RSA_WITH_AES_128_GCM_SHA256", "TLS_ECDH_ECDSA_WITH_AES_128_GCM_SHA256", "TLS_ECDH_RSA_WITH_AES_128_GCM_SHA256", "TLS_DHE_RSA_WITH_AES_128_GCM_SHA256", "TLS_DHE_DSS_WITH_AES_128_GCM_SHA256", "TLS_ECDHE_ECDSA_WITH_AES_256_CBC_SHA384", "TLS_ECDHE_RSA_WITH_AES_256_CBC_SHA384", "TLS_RSA_WITH_AES_256_CBC_SHA256", "TLS_ECDH_ECDSA_WITH_AES_256_CBC_SHA384", "TLS_ECDH_RSA_WITH_AES_256_CBC_SHA384", "TLS_DHE_RSA_WITH_AES_256_CBC_SHA256", "TLS_DHE_DSS_WITH_AES_256_CBC_SHA256", "TLS_ECDHE_ECDSA_WITH_AES_256_CBC_SHA", "TLS_ECDHE_RSA_WITH_AES_256_CBC_SHA", "TLS_RSA_WITH_AES_256_CBC_SHA", "TLS_ECDH_ECDSA_WITH_AES_256_CBC_SHA", "TLS_ECDH_RSA_WITH_AES_256_CBC_SHA", "TLS_DHE_RSA_WITH_AES_256_CBC_SHA", "TLS_DHE_DSS_WITH_AES_256_CBC_SHA", "TLS_ECDHE_ECDSA_WITH_AES_128_CBC_SHA256", "TLS_ECDHE_RSA_WITH_AES_128_CBC_SHA256", "TLS_RSA_WITH_AES_128_CBC_SHA256", "TLS_ECDH_ECDSA_WITH_AES_128_CBC_SHA256", "TLS_ECDH_RSA_WITH_AES_128_CBC_SHA256", "TLS_DHE_RSA_WITH_AES_128_CBC_SHA256", "TLS_DHE_DSS_WITH_AES_128_CBC_SHA256", "TLS_ECDHE_ECDSA_WITH_AES_128_CBC_SHA", "TLS_ECDHE_RSA_WITH_AES_128_CBC_SHA", "TLS_RSA_WITH_AES_128_CBC_SHA", "TLS_ECDH_ECDSA_WITH_AES_128_CBC_SHA", "TLS_ECDH_RSA_WITH_AES_128_CBC_SHA", "TLS_DHE_RSA_WITH_AES_128_CBC_SHA", "TLS_DHE_DSS_WITH_AES_128_CBC_SHA"};

    private ServiceInvocationManager() {
    }

    static void startService(ChannelInfo channelInfo, int i) throws UnsupportedProtocolException {
        checkSupportProtocol(i);
        try {
            SSLServerSocket sSLServerSocketTryPorts = tryPorts(channelInfo.getPorts(), SecureSocketUtils.getSecureSSLContext().getServerSocketFactory());
            sSLServerSocketTryPorts.setReuseAddress(true);
            sSLServerSocketTryPorts.setEnabledCipherSuites(ENABLED_CIPHER_SUITES);
            Timer timer = new Timer(SOCKET_TIMEOUT, actionEvent -> {
                LOGGER.warning("Se ha caducado la conexion. Se deja de escuchar en el puerto...");
                if (Platform.OS.MACOSX.equals(Platform.getOS())) {
                    MacUtils.closeMacService(channelInfo.getIdSession());
                }
                Runtime.getRuntime().halt(0);
            });
            timer.start();
            while (true) {
                try {
                    new CommandProcessorThread(sSLServerSocketTryPorts.accept(), timer, channelInfo.getIdSession(), i).start();
                } catch (SocketTimeoutException e) {
                    LOGGER.severe("Tiempo de espera del socket terminado: " + e);
                }
            }
        } catch (IOException e2) {
            LOGGER.log(Level.SEVERE, "Error en la comunicacion a traves del socket", (Throwable) e2);
        } catch (KeyManagementException e3) {
            LOGGER.severe("Error con el KeyManager: " + e3);
        } catch (KeyStoreException e4) {
            LOGGER.severe("Error con el keyStore: " + e4);
        } catch (NoSuchAlgorithmException e5) {
            LOGGER.severe("Error con el algoritmo del  certificado: " + e5);
        } catch (UnrecoverableKeyException e6) {
            LOGGER.severe("Error al recuperar la key: " + e6);
        } catch (CertificateException e7) {
            LOGGER.severe("Error con el certificado: " + e7);
        } catch (GeneralSecurityException e8) {
            LOGGER.severe("Error durante la carga del almacen de claves SSL: " + e8);
        }
    }

    private static SSLServerSocket tryPorts(int[] iArr, SSLServerSocketFactory sSLServerSocketFactory) throws IOException {
        checkNullParameter(iArr, "La lista de puertos no puede ser nula");
        checkNullParameter(sSLServerSocketFactory, "El socket servidor no puede ser nulo");
        for (int i : iArr) {
            try {
                SSLServerSocket sSLServerSocket = (SSLServerSocket) sSLServerSocketFactory.createServerSocket(i);
                LOGGER.info("Establecido el puerto " + i + " para el servicio Autofirma");
                return sSLServerSocket;
            } catch (BindException e) {
                LOGGER.warning("El puerto " + i + " parece estar en uso, se continua con el siguiente: " + e);
            } catch (Exception e2) {
                LOGGER.warning("No se ha podido conectar al puerto " + i + ", se intentara con el siguiente: " + e2);
            }
        }
        throw new IOException("No se ha podido ligar el socket servidor a ningun puerto");
    }

    private static void checkNullParameter(Object obj, String str) {
        if (obj == null) {
            throw new IllegalArgumentException(str);
        }
    }

    private static void checkSupportProtocol(int i) throws UnsupportedProtocolException {
        for (int i2 : SUPPORTED_PROTOCOL_VERSIONS) {
            if (i2 == i) {
                return;
            }
        }
        throw new UnsupportedProtocolException(i, i > 3);
    }
}
