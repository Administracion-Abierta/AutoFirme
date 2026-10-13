package es.gob.jmulticard.jse.provider;

import es.gob.jmulticard.card.Atr;
import es.gob.jmulticard.connection.ApduConnection;
import es.gob.jmulticard.connection.ApduConnectionException;
import es.gob.jmulticard.connection.CardNotPresentException;
import es.gob.jmulticard.jse.provider.ceres.CeresProvider;
import es.gob.jmulticard.jse.provider.gide.SmartCafeProvider;
import java.lang.reflect.InvocationTargetException;
import java.security.Provider;
import java.util.logging.Logger;

/* JADX INFO: loaded from: autofirma.jar:es/gob/jmulticard/jse/provider/JMultiCardProviderFactory.class */
public final class JMultiCardProviderFactory {
    private static final Logger LOGGER = Logger.getLogger(JMultiCardProviderFactory.class.getName());
    private static final byte[] DNI_NFC_ATR_MASK = {-1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 0, -1, 0};
    private static final Atr DNI_NFC_ATR = new Atr(new byte[]{59, -120, -128, 1, -31, -13, 94, 17, 119, -127, -95, 0, 3}, DNI_NFC_ATR_MASK);
    private static final byte[] DNI_ATR_MASK = {-1, -1, 0, -1, -1, -1, -1, -1, -1, -1, -1, 0, 0, 0, 0, 0, 0, 0, -1, -1};
    private static final Atr DNI_ATR = new Atr(new byte[]{59, 127, 0, 0, 0, 0, 106, 68, 78, 73, 101, 0, 0, 0, 0, 0, 0, 0, -112, 0}, DNI_ATR_MASK);
    private static final byte[] TIF_ATR_MASK = DNI_ATR_MASK;
    private static final Atr TIF_ATR = new Atr(new byte[]{59, 127, 0, 0, 0, 0, 106, 84, 73, 70, 49, 0, 0, 0, 0, 0, 0, 0, -112, 0}, TIF_ATR_MASK);
    private static final byte[] FNMT_TC_430_ATR_MASK = DNI_ATR_MASK;
    private static final Atr FNMT_TC_430_ATR = new Atr(new byte[]{59, 127, 0, 0, 0, 0, 106, 70, 78, 77, 84, 0, 0, 0, 0, 0, 0, 0, -112, 0}, FNMT_TC_430_ATR_MASK);
    private static final byte[] CERES_TC_ATR_MASK = {-1, -1, 0, -1, -1, -1, -1, -1, -1, -1, -1, 0, 0, 0, 0, 0, 0, -1, -1, -1};
    private static final Atr CERES_TC_ATR = new Atr(new byte[]{59, 127, 0, 0, 0, 0, 106, 70, 78, 77, 84, 0, 0, 0, 0, 0, 0, 3, -112, 0}, CERES_TC_ATR_MASK);
    private static final byte[] CERES_ST_ATR_MASK = {-1, -1, 0, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 0, 0, -1, -1, -1};
    private static final Atr CERES_ST_ATR = new Atr(new byte[]{59, 127, 0, 0, 0, 0, 106, 67, 69, 82, 69, 83, 2, 44, 52, 0, 0, 3, -112, 0}, CERES_ST_ATR_MASK);
    private static final byte[] CERES_SLE_FN20_ATR_MASK = {-1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1};
    private static final Atr CERES_SLE_FN20_ATR = new Atr(new byte[]{59, -17, 0, 0, 64, 20, -128, 37, 67, 69, 82, 69, 83, 87, 5, 96, 1, 2, 3, -112, 0}, CERES_SLE_FN20_ATR_MASK);
    private static final byte[] CERES_SLE_FN19_ATR_MASK = {-1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1};
    private static final Atr CERES_SLE_FN19_ATR = new Atr(new byte[]{59, -17, 0, 0, 64, 20, -128, 37, 67, 69, 82, 69, 83, 87, 1, 22, 1, 1, 3, -112, 0}, CERES_SLE_FN19_ATR_MASK);
    private static final byte[] GIDE_SCAF_ATR_MASK = {-1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 15};
    private static final Atr GIDE_SCAF_ATR = new Atr(new byte[]{59, -9, 24, 0, 0, -128, 49, -2, 69, 115, 102, 116, 101, 45, 110, 102, -60}, GIDE_SCAF_ATR_MASK);
    private static final byte[] GIDE_SCAF_MSC_ATR_MASK = {-1, -1, -1, -1, -1};
    private static final Atr GIDE_SCAF_MSC_ATR = new Atr(new byte[]{59, -128, -128, 1, 1}, GIDE_SCAF_MSC_ATR_MASK);
    private static final byte[] GIDE_SCAF_TCL_ATR_MASK = {-1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 15};
    private static final Atr GIDE_SCAF_TCL_ATR = new Atr(new byte[]{59, -9, 24, 0, 0, -128, 49, -2, 69, 115, 102, 116, 101, 45, 110, 102, -60}, GIDE_SCAF_TCL_ATR_MASK);

    private JMultiCardProviderFactory() {
    }

    public static Provider getProvider() {
        return getProvider((String) null);
    }

    /* JADX WARN: Code duplicated, block: B:7:0x000f  */
    public static Provider getProvider(String connectionClassName) {
        String str;
        if (connectionClassName != null) {
            try {
                if (connectionClassName.isEmpty()) {
                    str = ProviderUtil.DEFAULT_PROVIDER_CLASSNAME;
                } else {
                    str = connectionClassName;
                }
            } catch (ClassNotFoundException | IllegalAccessException | IllegalArgumentException | InstantiationException | NoSuchMethodException | SecurityException | InvocationTargetException e2) {
                throw new IllegalStateException("No se ha podido instanciar la conexion " + ((connectionClassName == null || connectionClassName.isEmpty()) ? ProviderUtil.DEFAULT_PROVIDER_CLASSNAME : connectionClassName), e2);
            }
        } else {
            str = ProviderUtil.DEFAULT_PROVIDER_CLASSNAME;
        }
        ApduConnection conn = (ApduConnection) Class.forName(str).getConstructor(new Class[0]).newInstance(new Object[0]);
        try {
            long[] terminals = conn.getTerminals(false);
            for (long terminal : terminals) {
                try {
                    conn.setTerminal((int) terminal);
                    try {
                        byte[] atr = conn.reset();
                        Provider provider = getProvider(atr);
                        if (provider != null) {
                            return provider;
                        }
                    } catch (CardNotPresentException e) {
                        LOGGER.info("No hay tarjeta insertada en el lector " + terminal + ": " + e);
                    } catch (Exception e3) {
                        LOGGER.warning("Error reiniciando el lector " + terminal + ": " + e3);
                    }
                } catch (ApduConnectionException e1) {
                    LOGGER.warning("Numero de terminal no valido (" + terminal + "): " + e1);
                }
            }
            return null;
        } catch (ApduConnectionException e4) {
            LOGGER.warning("No se ha podido obtener la lista de lectores de tarjetas del sistema: " + e4);
            return null;
        }
    }

    public static Provider getProvider(byte[] atr) {
        if (atr == null) {
            return null;
        }
        if (isDni(atr) || isCeres430(atr)) {
            return new DnieProvider();
        }
        if (isCeres(atr)) {
            return new CeresProvider();
        }
        if (isGiDeSmartCafe(atr)) {
            return new SmartCafeProvider();
        }
        return null;
    }

    private static boolean isDni(byte[] atr) {
        return DNI_ATR.equals(new Atr(atr, DNI_ATR_MASK)) || TIF_ATR.equals(new Atr(atr, TIF_ATR_MASK)) || DNI_NFC_ATR.equals(new Atr(atr, DNI_NFC_ATR_MASK));
    }

    private static boolean isCeres430(byte[] atr) {
        return FNMT_TC_430_ATR.equals(new Atr(atr, FNMT_TC_430_ATR_MASK)) && atr[15] >= 4 && atr[16] >= 48;
    }

    private static boolean isCeres(byte[] atr) {
        return CERES_TC_ATR.equals(new Atr(atr, CERES_TC_ATR_MASK)) || CERES_ST_ATR.equals(new Atr(atr, CERES_ST_ATR_MASK)) || CERES_SLE_FN20_ATR.equals(new Atr(atr, CERES_SLE_FN20_ATR_MASK)) || CERES_SLE_FN19_ATR.equals(new Atr(atr, CERES_SLE_FN19_ATR_MASK));
    }

    private static boolean isGiDeSmartCafe(byte[] atr) {
        return GIDE_SCAF_ATR.equals(new Atr(atr, GIDE_SCAF_ATR_MASK)) || GIDE_SCAF_MSC_ATR.equals(new Atr(atr, GIDE_SCAF_MSC_ATR_MASK)) || GIDE_SCAF_TCL_ATR.equals(new Atr(atr, GIDE_SCAF_TCL_ATR_MASK));
    }
}
