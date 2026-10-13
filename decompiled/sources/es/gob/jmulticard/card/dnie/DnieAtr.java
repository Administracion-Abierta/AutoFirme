package es.gob.jmulticard.card.dnie;

import es.gob.jmulticard.HexUtils;
import es.gob.jmulticard.card.Atr;

/* JADX INFO: loaded from: autofirma.jar:es/gob/jmulticard/card/dnie/DnieAtr.class */
public final class DnieAtr extends Atr {
    private static final long serialVersionUID = -4364467785373401604L;
    private static final String UNKNOWN = "desconocido";

    DnieAtr(Atr cardAtr) {
        super(cardAtr.getBytes(), cardAtr.getMask());
    }

    @Override // es.gob.jmulticard.card.Atr
    public String toString() {
        if (this.atrBytes.length != 20 || this.atrBytes[0] != 59) {
            return "ATR de la tarjeta: " + super.toString();
        }
        StringBuilder sb = new StringBuilder("ATR del DNIe: ");
        sb.append(super.toString());
        sb.append('\n');
        sb.append("  Vpp (voltaje de programacion): ");
        sb.append((int) HexUtils.getShort(new byte[]{this.atrBytes[3]}, 0));
        if (this.atrBytes[3] == 0) {
            sb.append(" (no requerido)");
        }
        sb.append('\n');
        sb.append("  Tiempo de espera adicional: ");
        sb.append((int) HexUtils.getShort(new byte[]{this.atrBytes[4]}, 0));
        if (this.atrBytes[4] == 0) {
            sb.append(" (no requerido)");
        }
        sb.append('\n');
        sb.append("  Nombre de la tarjeta: ");
        sb.append(new String(new byte[]{this.atrBytes[7], this.atrBytes[8], this.atrBytes[9], this.atrBytes[10]}));
        sb.append('\n');
        sb.append("  Fabricante de la tecnologia 'Match-on-Card' incorporada: ");
        if (this.atrBytes[11] == 16) {
            sb.append("Sagem / Morpho");
        } else if (this.atrBytes[11] == 32) {
            sb.append("Siemens / Atos");
        } else {
            sb.append(UNKNOWN);
        }
        sb.append('\n');
        sb.append("  Fabricante del chip: ");
        if (this.atrBytes[12] == 1) {
            sb.append("Infineon");
        } else if (this.atrBytes[12] == 2) {
            sb.append("STMicroelectronics");
        } else {
            sb.append(UNKNOWN);
        }
        sb.append('\n');
        sb.append("  Tipo de chip: ");
        if (this.atrBytes[13] == 1 && this.atrBytes[14] == 85) {
            sb.append("SLE78CLX408AP");
        } else if (this.atrBytes[13] == 76 && this.atrBytes[14] == 52) {
            sb.append("19WL34");
        } else {
            sb.append(UNKNOWN);
        }
        sb.append('\n');
        sb.append("  Version del sistema operativo: ");
        sb.append(HexUtils.hexify(new byte[]{this.atrBytes[15]}, false));
        sb.append('.');
        sb.append(HexUtils.hexify(new byte[]{this.atrBytes[16]}, false));
        sb.append('\n');
        sb.append("  Fase del ciclo de vida: ");
        switch (this.atrBytes[17]) {
            case 0:
                sb.append("prepersonalizacion");
                break;
            case 1:
                sb.append("personalizacion");
                break;
            case 3:
                sb.append("usuario");
                break;
            case 15:
                sb.append("final");
                break;
            default:
                sb.append(UNKNOWN);
                break;
        }
        sb.append('\n');
        sb.append("  Estado: ");
        if (this.atrBytes[18] == -112 && this.atrBytes[19] == 0) {
            sb.append("correcto");
        } else if (this.atrBytes[18] == 101 && this.atrBytes[19] == 129) {
            sb.append("memoria volatil borrada");
        } else {
            sb.append(UNKNOWN);
        }
        return sb.toString();
    }
}
