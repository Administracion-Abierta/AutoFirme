package es.gob.afirma.standalone.protocol;

import es.gob.afirma.standalone.configurator.common.PreferencesManager;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.java_websocket.server.DefaultSSLWebSocketServerFactory;

/* JADX INFO: loaded from: autofirma.jar:es/gob/afirma/standalone/protocol/AfirmaWebSocketServerManager.class */
public class AfirmaWebSocketServerManager {
    private static final int PROTOCOL_VERSION_3 = 3;
    private static final int PROTOCOL_VERSION_4 = 4;
    private static final int CURRENT_PROTOCOL_VERSION = 4;
    private static final String SYSTEM_PROPERTY_OPTIMIZED_FOR_VDI = "websockets.optimizedForVdi";
    static final Logger LOGGER = Logger.getLogger("es.gob.afirma");
    private static final int[] SUPPORTED_PROTOCOL_VERSIONS = {3, 4};
    private static int protocolVersion = -1;
    static AfirmaWebSocketServer instance = null;

    /* JADX WARN: Code duplicated, block: B:17:0x00d1  */
    /* JADX WARN: Code duplicated, block: B:19:0x00db A[RETURN] */
    public static void startService(ChannelInfo channelInfo, int i) throws SocketOperationException, UnsupportedProtocolException {
        checkSupportProtocol(i);
        protocolVersion = i;
        System.setProperty(SYSTEM_PROPERTY_OPTIMIZED_FOR_VDI, Boolean.toString(PreferencesManager.getBoolean(PreferencesManager.PREFERENCE_GENERAL_VDI_OPTIMIZATION)));
        int i2 = 0;
        int[] ports = channelInfo.getPorts();
        do {
            LOGGER.info("Tratamos de abrir el socket en el puerto: " + ports[i2]);
            try {
                switch (protocolVersion) {
                    case 4:
                        instance = new AfirmaWebSocketServerV4(ports[i2], channelInfo.getIdSession());
                        break;
                    default:
                        instance = new AfirmaWebSocketServer(ports[i2], channelInfo.getIdSession());
                        break;
                }
                instance.setWebSocketFactory(new DefaultSSLWebSocketServerFactory(SecureSocketUtils.getSecureSSLContext()));
                instance.start();
            } catch (Exception e) {
                LOGGER.log(Level.WARNING, "No se ha podido abrir un socket en el puerto: " + ports[i2], (Throwable) e);
                instance = null;
            }
            i2++;
            if (instance == null) {
            }
            if (instance == null) {
                throw new SocketOperationException("No se ha podido abrir ningun socket. Se aborta la comunicacion.");
            }
        } while (i2 < ports.length);
        if (instance == null) {
            throw new SocketOperationException("No se ha podido abrir ningun socket. Se aborta la comunicacion.");
        }
    }

    private static void checkSupportProtocol(int i) throws UnsupportedProtocolException {
        for (int i2 : SUPPORTED_PROTOCOL_VERSIONS) {
            if (i2 == i) {
                return;
            }
        }
        throw new UnsupportedProtocolException(i, i > 4);
    }
}
