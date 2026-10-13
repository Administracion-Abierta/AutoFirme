package es.gob.jmulticard.apdu;

import es.gob.jmulticard.HexUtils;
import java.util.AbstractMap;
import java.util.HashMap;

/* JADX INFO: loaded from: autofirma.jar:es/gob/jmulticard/apdu/StandardErrorCodes.class */
final class StandardErrorCodes {
    private static final AbstractMap<StatusWord, String> ERRORS = new HashMap(26);

    private StandardErrorCodes() {
    }

    static {
        ERRORS.put(new StatusWord((byte) 98, (byte) -125), "El fichero seleccionado esta invalidado (6283)");
        ERRORS.put(new StatusWord((byte) 101, (byte) -127), "Fallo en la memoria (6581)");
        ERRORS.put(new StatusWord((byte) 103, (byte) 0), "Longitud incorrecta (6700)");
        ERRORS.put(new StatusWord((byte) 104, (byte) -126), "Securizacion de mensajes no soportada (6882)");
        ERRORS.put(new StatusWord((byte) 105, (byte) -126), "Condiciones de seguridad no satisfechas (6982)");
        ERRORS.put(new StatusWord((byte) 105, (byte) -125), "Metodo de autenticacion bloqueado (6983)");
        ERRORS.put(new StatusWord((byte) 105, (byte) -124), "Dato referenciado invalido (6984)");
        ERRORS.put(new StatusWord((byte) 105, (byte) -123), "Condiciones de uso no satisfechas (6985)");
        ERRORS.put(new StatusWord((byte) 105, (byte) -122), "Comando no permitido [no existe ningun EF seleccionado] (6986)");
        ERRORS.put(new StatusWord((byte) 105, (byte) -121), "Falta un objeto necesario en el mensaje seguro (6987)");
        ERRORS.put(new StatusWord((byte) 105, (byte) -120), "Objetos de datos incorrectos para el mensaje seguro (6988)");
        ERRORS.put(new StatusWord((byte) 106, (byte) -128), "Parametros incorrectos en el campo de datos (6A80)");
        ERRORS.put(new StatusWord((byte) 106, (byte) -127), "Funcion no soportada (6A81)");
        ERRORS.put(new StatusWord((byte) 106, (byte) -126), "No se encuentra el fichero (6A82)");
        ERRORS.put(new StatusWord((byte) 106, (byte) -125), "Registro no encontrado (6A83)");
        ERRORS.put(new StatusWord((byte) 106, (byte) -124), "No hay suficiente espacio de memoria en el fichero (6A84)");
        ERRORS.put(new StatusWord((byte) 106, (byte) -123), "La longitud de datos (Lc) es incompatible con la estructura TLV (6A85)");
        ERRORS.put(new StatusWord((byte) 106, (byte) -122), "Parametros incorrectos en P1 o P2 (6A86)");
        ERRORS.put(new StatusWord((byte) 106, (byte) -121), "La longitud de los datos es inconsistente con P1-P2 (6A87)");
        ERRORS.put(new StatusWord((byte) 106, (byte) -120), "Datos referenciados no encontrados (6A88)");
        ERRORS.put(new StatusWord((byte) 106, (byte) -119), "El fichero ya existe (6A89)");
        ERRORS.put(new StatusWord((byte) 106, (byte) -118), "El nombre del DF ya existe (6A8A)");
        ERRORS.put(new StatusWord((byte) 107, (byte) 0), "Parametro(s) incorrecto(s) P1-P2 (6B00)");
        ERRORS.put(new StatusWord((byte) 110, (byte) 0), "Clase no soportada (6E00)");
        ERRORS.put(new StatusWord((byte) 109, (byte) 0), "Comando no permitido en la fase de vida actual (6D00)");
        ERRORS.put(new StatusWord((byte) 111, (byte) 0), "Diagnostico no preciso (6F00)");
    }

    static String getErrorDescription(StatusWord sw) {
        if (sw == null) {
            return "Palabra de estado nula";
        }
        if (ERRORS.containsKey(sw)) {
            return ERRORS.get(sw);
        }
        return HexUtils.hexify(sw.getBytes(), true);
    }
}
