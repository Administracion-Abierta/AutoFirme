package es.gob.afirma.standalone.protocol;

import java.io.IOException;
import java.util.logging.Logger;

/* JADX INFO: loaded from: autofirma.jar:es/gob/afirma/standalone/protocol/ActiveWaitingThread.class */
public class ActiveWaitingThread extends Thread {
    private static final String WAIT_CONSTANT = "#WAIT";
    private static final long SLEEP_PERIOD = 10000;
    private static final Logger LOGGER = Logger.getLogger("es.gob.afirma");
    private final String storageServiceUrl;
    private final String transactionId;
    private boolean cancelled = false;

    public ActiveWaitingThread(String str, String str2) {
        this.storageServiceUrl = str;
        this.transactionId = str2;
    }

    @Override // java.lang.Thread, java.lang.Runnable
    public void run() {
        while (!this.cancelled) {
            synchronized (IntermediateServerUtil.getUniqueSemaphoreInstance()) {
                if (!this.cancelled) {
                    try {
                        IntermediateServerUtil.sendData(WAIT_CONSTANT, this.storageServiceUrl, this.transactionId);
                    } catch (IOException e) {
                        LOGGER.warning("No se ha podido enviar la peticion de espera: " + e);
                    }
                }
            }
            if (!this.cancelled) {
                try {
                    Thread.sleep(SLEEP_PERIOD);
                } catch (InterruptedException e2) {
                    if (!this.cancelled) {
                        LOGGER.warning("No se ha podido esperar para el envio de la senal de espera activa para el JavaScript: " + e2);
                    }
                }
            }
        }
    }

    @Override // java.lang.Thread
    public void interrupt() {
        this.cancelled = true;
        super.interrupt();
    }
}
