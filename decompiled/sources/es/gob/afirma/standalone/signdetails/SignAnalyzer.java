package es.gob.afirma.standalone.signdetails;

import es.gob.afirma.core.signers.AOSigner;
import es.gob.afirma.core.signers.AOSignerFactory;
import es.gob.afirma.core.util.tree.AOTreeModel;
import es.gob.afirma.standalone.crypto.CompleteSignInfo;
import es.gob.afirma.standalone.crypto.TimestampsAnalyzer;
import java.io.IOException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/* JADX INFO: loaded from: autofirma.jar:es/gob/afirma/standalone/signdetails/SignAnalyzer.class */
public interface SignAnalyzer {
    public static final Logger LOGGER = Logger.getLogger("es.gob.afirma");

    AOTreeModel getSignersTree();

    String getSignFormat();

    String getDataLocation();

    List<SignDetails> getAllSignDetails();

    static CompleteSignInfo getSignInfo(byte[] bArr) throws IOException {
        CompleteSignInfo completeSignInfo = new CompleteSignInfo();
        completeSignInfo.setSignData(bArr);
        AOSigner signer = AOSignerFactory.getSigner(bArr);
        if (signer == null) {
            LOGGER.warning("Formato de firma no reconocido");
            throw new IllegalArgumentException("Formato de firma no reconocido");
        }
        try {
            completeSignInfo.setSignInfo(signer.getSignInfo(bArr));
        } catch (Exception e) {
            LOGGER.log(Level.WARNING, "Error al leer la informacion de la firma", (Throwable) e);
        }
        try {
            completeSignInfo.setSignsTree(signer.getSignersStructure(bArr, true));
        } catch (Exception e2) {
            LOGGER.log(Level.WARNING, "Error al extraer el arbol de firmantes", (Throwable) e2);
            completeSignInfo.setSignsTree(null);
        }
        try {
            completeSignInfo.setData(signer.getData(bArr));
        } catch (Exception e3) {
            LOGGER.log(Level.WARNING, "Error al extraer los datos firmados", (Throwable) e3);
        }
        try {
            completeSignInfo.setTimestampsInfo(TimestampsAnalyzer.getTimestamps(bArr));
        } catch (Exception e4) {
            LOGGER.log(Level.WARNING, "Error al extraer los sellos de tiempo", (Throwable) e4);
        }
        return completeSignInfo;
    }
}
