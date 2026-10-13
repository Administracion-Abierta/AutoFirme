package es.gob.jmulticard.card;

import es.gob.jmulticard.HexUtils;
import java.util.ArrayList;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.apache.pdfbox.contentstream.operator.OperatorName;
import org.apache.pdfbox.pdmodel.common.PDPageLabelRange;

/* JADX INFO: loaded from: autofirma.jar:es/gob/jmulticard/card/Location.class */
public final class Location {
    private static final int MASTER_FILE_ID = 16128;
    private final ArrayList<Integer> path;
    private static final Map<String, Integer> HEXBYTES = new ConcurrentHashMap();

    static {
        String[] hex = {PDPageLabelRange.STYLE_LETTERS_LOWER, OperatorName.CLOSE_FILL_NON_ZERO_AND_STROKE, "c", OperatorName.SET_LINE_DASHPATTERN, "e", OperatorName.FILL_NON_ZERO};
        for (int i = 0; i <= 9; i++) {
            HEXBYTES.put(String.valueOf(i), Integer.valueOf(String.valueOf(i)));
        }
        for (int i2 = 10; i2 < 16; i2++) {
            HEXBYTES.put(hex[i2 - 10], Integer.valueOf(String.valueOf(i2)));
            HEXBYTES.put(hex[i2 - 10].toUpperCase(), Integer.valueOf(String.valueOf(i2)));
        }
    }

    public Location(String absolutePath) {
        this.path = new ArrayList<>();
        init(absolutePath);
    }

    private Location(ArrayList<Integer> locationPath) {
        if (locationPath != null) {
            int numElements = locationPath.size();
            this.path = new ArrayList<>(numElements);
            for (int i = 0; i < numElements; i++) {
                this.path.add(i, locationPath.get(i));
            }
            return;
        }
        this.path = new ArrayList<>();
    }

    public Location getChild() {
        Location aux = new Location(this.path);
        if (aux.path != null && aux.path.size() > 1) {
            aux.path.remove(0);
            return aux;
        }
        return null;
    }

    public byte[] getFile() {
        int address = this.path.get(0).intValue();
        return new byte[]{(byte) ((address >> 8) & 255), (byte) (address & 255)};
    }

    public byte[] getLastFilePath() {
        if (this.path.isEmpty()) {
            return null;
        }
        int address = this.path.get(this.path.size() - 1).intValue();
        return new byte[]{(byte) ((address >> 8) & 255), (byte) (address & 255)};
    }

    private static void checkValidPath(String absolutePath) {
        if (absolutePath == null) {
            throw new IllegalArgumentException("Ruta nula");
        }
        if (absolutePath.length() == 0) {
            throw new IllegalArgumentException("Ruta vacia");
        }
        if (absolutePath.trim().length() % 4 != 0) {
            throw new IllegalArgumentException("Un location valido debe estar compuesto por grupos pares de octetos: " + absolutePath);
        }
        String aux = absolutePath.toLowerCase();
        for (int i = 0; i < absolutePath.length(); i++) {
            char currentChar = aux.charAt(i);
            if ((currentChar < '0' || currentChar > '9') && (currentChar < 'a' || currentChar > 'f')) {
                throw new IllegalArgumentException("Encontrado el caracter invalido '" + currentChar + "'en la ruta '" + absolutePath + OperatorName.SHOW_TEXT_LINE);
            }
        }
    }

    private void init(String absolutePath) {
        checkValidPath(absolutePath);
        int i = 0;
        while (true) {
            int i2 = i;
            if (i2 < absolutePath.length()) {
                int mm = HEXBYTES.get(absolutePath.substring(i2, i2 + 1)).intValue();
                int ml = HEXBYTES.get(absolutePath.substring(i2 + 1, i2 + 2)).intValue();
                int lm = HEXBYTES.get(absolutePath.substring(i2 + 2, i2 + 3)).intValue();
                int ll = HEXBYTES.get(absolutePath.substring(i2 + 3, i2 + 4)).intValue();
                int id = ll + (lm << 4) + (ml << 8) + ((mm << 4) << 8);
                if (id != MASTER_FILE_ID) {
                    this.path.add(Integer.valueOf(String.valueOf(id)));
                }
                i = i2 + 4;
            } else {
                return;
            }
        }
    }

    public String toString() {
        StringBuilder buffer = new StringBuilder();
        if (this.path != null && !this.path.isEmpty()) {
            buffer.append("3F00");
            for (Integer integer : this.path) {
                buffer.append('/').append(HexUtils.hexify(new byte[]{(byte) (integer.shortValue() >> 8), integer.byteValue()}, false));
            }
        }
        return buffer.toString();
    }
}
