package es.gob.afirma.keystores.mozilla.bintutil;

import es.gob.afirma.core.misc.AOUtil;
import java.util.logging.Logger;

/* JADX INFO: loaded from: autofirma.jar:es/gob/afirma/keystores/mozilla/bintutil/MsPortableExecutable.class */
public final class MsPortableExecutable {
    private static final byte PE_SIGNATURE_OFFSET = 60;
    private final String description;
    private final PeMachineType machine;

    public PeMachineType getPeMachineType() {
        return this.machine;
    }

    public String toString() {
        return this.description;
    }

    public MsPortableExecutable(byte[] bArr) throws PEParserException {
        int u2 = AOBinUtil.getU2(bArr, 60) + 4;
        if (!"PE\u0000\u0000".equals(new String(new byte[]{bArr[u2 - 4], bArr[u2 - 3], bArr[u2 - 2], bArr[u2 - 1]}))) {
            throw new PEParserException("Cabecera PE\\0\\0 no encontrada en el offset " + AOUtil.hexify(new byte[]{60}, false));
        }
        StringBuilder sb = new StringBuilder("Fichero Microsoft PE");
        sb.append("\n Cabecera PE\\0\\0 encontrada en el offset " + AOUtil.hexify(new byte[]{60}, false));
        this.machine = PeMachineType.getPeMachineType(AOUtil.hexify(new byte[]{bArr[u2 + 1], bArr[u2]}, true));
        sb.append("\n Tipo de maquina: " + this.machine);
        sb.append("\n Numero de secciones de la tabla de secciones: " + AOBinUtil.getU2(bArr, u2 + 2));
        if (AOBinUtil.getInt(bArr, u2 + 8) != 0) {
            Logger.getLogger("es.gob.afirma").warning("El puntero a la informacion de depuracion del COFF no es cero (se implementa una caracteristica deprecada)");
        }
        if (AOBinUtil.getInt(bArr, u2 + 12) != 0) {
            Logger.getLogger("es.gob.afirma").warning("El numero de entradas de la tabla de simbolos no es cero (se implementa una caracteristica deprecada)");
        }
        int u3 = AOBinUtil.getU2(bArr, u2 + 16);
        if (u3 == 0) {
            sb.append("\n El fichero es un objeto (no tiene cabecera opcional)");
        } else {
            sb.append("\n El fichero es ejecutable (tamano de la cabecera opcional: " + u3 + ")");
        }
        byte[] bArr2 = {bArr[u2 + 19], bArr[u2 + 18]};
        if (0 != ((byte) (bArr2[1] & 1))) {
            sb.append("\n El fichero no contiene relocalizaciones de base");
        }
        if (0 != ((byte) (bArr2[1] & 2))) {
            sb.append("\n Imagen valida");
        }
        if (0 != ((byte) (bArr2[1] & 4))) {
            Logger.getLogger("es.gob.afirma").warning("Numeros de linea de la imagen COFF eliminados (se implementa una caracteristica deprecada");
        }
        if (0 != ((byte) (bArr2[1] & 8))) {
            Logger.getLogger("es.gob.afirma").warning("Entradas de la tabla de simbolos de la imagen COFF para simbolos locales eliminadas (se implementa una caracteristica deprecada)");
        }
        if (0 != ((byte) (bArr2[1] & 16))) {
            Logger.getLogger("es.gob.afirma").warning("Conjunto truncando agresivamente (se implementa una caracteristica deprecada para Windows 2000 y posteriores)");
        }
        if (0 != ((byte) (bArr2[1] & 32))) {
            sb.append("\n La aplicacion puede gestionar direccionamiento superior a 2GB");
        }
        if (0 != ((byte) (bArr2[1] & 64))) {
            Logger.getLogger("es.gob.afirma").warning("Se hace uso de un bit reservado para uso futuro");
        }
        if (0 != ((byte) (bArr2[1] & (-128)))) {
            sb.append("\n Arquitectura Little Endian");
        }
        if (0 != ((byte) (bArr2[0] & 1))) {
            sb.append("\n Arquitectura con palabras de 32 bits");
        }
        if (0 != ((byte) (bArr2[0] & 2))) {
            sb.append("\n Informacion de depuracion eliminada de la imagen");
        }
        if (0 != ((byte) (bArr2[0] & 4))) {
            sb.append("\n Si la imagen se encuentra en almacenamiento extraible, debe cargarse completamente y copiarse al fichero de intercambio");
        }
        if (0 != ((byte) (bArr2[0] & 8))) {
            sb.append("\n Si la imagen se encuantra en almacenamiento en red, debe cargarse completamente y copiarse al fichero de intercambio");
        }
        if (0 != ((byte) (bArr2[0] & 16))) {
            sb.append("\n La imagen es un fichero de sistema, no un programa de usuario");
        }
        if (0 != ((byte) (bArr2[0] & 32))) {
            sb.append("\n La imagen es una biblioteca de enlace dinamico");
        }
        if (0 != ((byte) (bArr2[0] & 64))) {
            sb.append("\n La imagen debe ejecutarse en una maquina con un unico procesador");
        }
        if (0 != ((byte) (bArr2[0] & (-128)))) {
            sb.append("\n Arquitectura Big Endian");
        }
        if (u3 > 0) {
            boolean z = false;
            if (bArr[u2 + 20 + 1] == 1) {
                if (bArr[u2 + 20 + 0] == 11) {
                    sb.append("\n La imagen es un ejecutable normal (PE32)");
                } else if (bArr[u2 + 20 + 0] == 7) {
                    sb.append("\n La imagen es una ROM");
                } else {
                    Logger.getLogger("es.gob.afirma").warning("El fichero de imagen es de un tipo desconocido (Magic: " + AOUtil.hexify(new byte[]{bArr[u2 + 21], bArr[u2 + 20]}, true) + ")");
                }
            } else if (bArr[u2 + 20 + 1] == 2 && bArr[u2 + 20] == 11) {
                sb.append("\n La imagen es un ejecutable PE32+");
                z = true;
            } else {
                Logger.getLogger("es.gob.afirma").warning("El fichero de imagen es de un tipo desconocido (Magic: " + AOUtil.hexify(new byte[]{bArr[u2 + 21], bArr[u2 + 20]}, true) + ")");
            }
            sb.append("\n Version del enlazador usado para crear la imagen: " + ((int) bArr[u2 + 20 + 2]) + "." + ((int) bArr[u2 + 20 + 3]));
            sb.append("\n Version requerida del sistema operativo: " + AOBinUtil.getU2(bArr, u2 + 20 + 40) + "." + AOBinUtil.getU2(bArr, u2 + 20 + 42));
            sb.append("\n Version de la imagen: " + AOBinUtil.getU2(bArr, u2 + 20 + 44) + "." + AOBinUtil.getU2(bArr, u2 + 20 + 46));
            sb.append("\n Version del subsistema: " + AOBinUtil.getU2(bArr, u2 + 20 + 48) + "." + AOBinUtil.getU2(bArr, u2 + 20 + 50));
            sb.append("\n Tamano de la imagen en memoria: " + AOUtil.hexify(new byte[]{bArr[u2 + 20 + 56], bArr[u2 + 20 + 57], bArr[u2 + 20 + 58], bArr[u2 + 20 + 59]}, false));
            sb.append('h');
            sb.append("\n Tamano de las cabeceras (DOS Stub, cabecera PE y cabeceras de seccion, en valor redondeado a un multiplo de 'FileAlignment'): " + AOUtil.hexify(new byte[]{bArr[u2 + 20 + 60], bArr[u2 + 20 + 61], bArr[u2 + 20 + 62], bArr[u2 + 20 + 63]}, false));
            sb.append('h');
            sb.append("\n Checksum de la imagen: " + AOUtil.hexify(new byte[]{bArr[u2 + 20 + 64], bArr[u2 + 20 + 65], bArr[u2 + 20 + 66], bArr[u2 + 20 + 67]}, false));
            sb.append("\n Tipo de subsistema Windows: ");
            switch (AOBinUtil.getU2(bArr, u2 + 20 + 68)) {
                case 0:
                    sb.append(" Desconocido");
                    break;
                case 1:
                    sb.append(" Controlador de dispositiovo o proceso nativo Windows");
                    break;
                case 2:
                    sb.append(" Windows en modo grafico (GUI)");
                    break;
                case 3:
                    sb.append(" Windows en modo texto");
                    break;
                case 4:
                case 5:
                case 6:
                case 8:
                default:
                    sb.append(" No valido");
                    break;
                case 7:
                    sb.append(" POSIX en modo texto");
                    break;
                case 9:
                    sb.append(" Windows CE");
                    break;
                case 10:
                    sb.append(" Aplicacion EFI (Extensible Firmware Interface)");
                    break;
                case 11:
                    sb.append(" Controlador EFI (Extensible Firmware Interface) con servicios de arranque");
                    break;
                case 12:
                    sb.append(" Controlador EFI (Extensible Firmware Interface) con servicios de ejecucion");
                    break;
                case 13:
                    sb.append(" Imagen ROM EFI (Extensible Firmware Interface)");
                    break;
                case 14:
                    sb.append(" XBOX");
                    break;
            }
            int i = z ? 128 : 112;
            int dWord = AOBinUtil.getDWord(bArr, u2 + 20 + i);
            sb.append("\n Tamano (en memoria) de la tabla de recursos: " + AOBinUtil.getDWord(bArr, u2 + 20 + i + 4) + " (" + AOUtil.hexify(new byte[]{bArr[u2 + 20 + i + 7], bArr[u2 + 20 + i + 6], bArr[u2 + 20 + i + 5], bArr[u2 + 20 + i + 4]}, false) + "h)");
            sb.append("\n Offset (virtual) de la tabla de recursos: " + dWord + " (" + AOUtil.hexify(new byte[]{bArr[u2 + 20 + i + 3], bArr[u2 + 20 + i + 2], bArr[u2 + 20 + i + 1], bArr[u2 + 20 + i]}, false) + "h)");
        }
        this.description = sb.toString();
    }
}
