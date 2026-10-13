package es.gob.afirma.signers.pades;

import es.gob.afirma.keystores.filters.PolicyIdFilter;

/* JADX INFO: loaded from: autofirma.jar:es/gob/afirma/signers/pades/PdfTextMask.class */
public class PdfTextMask {
    private static final char DEFAULT_OBFUSCATED_CHAR = '*';
    private static final int DEFAULT_MIN_NUMBER_LENGTH = 3;
    private char obfuscatedChar = '*';
    private int minLength = 3;
    private boolean[] positions = {false, false, false, true, true, true, true};
    private boolean shiftSupported = true;

    PdfTextMask() {
    }

    public void setObfuscatedChar(char c) {
        this.obfuscatedChar = c;
    }

    public char getObfuscatedChar() {
        return this.obfuscatedChar;
    }

    public void setMinLength(int i) {
        this.minLength = i;
    }

    public int getMinLength() {
        return this.minLength;
    }

    public void setPositions(boolean[] zArr) {
        this.positions = zArr != null ? (boolean[]) zArr.clone() : null;
    }

    public boolean[] getPositions() {
        if (this.positions != null) {
            return (boolean[]) this.positions.clone();
        }
        return null;
    }

    public void setShiftSupported(boolean z) {
        this.shiftSupported = z;
    }

    public boolean isShiftSupported() {
        return this.shiftSupported;
    }

    public static PdfTextMask parseParam(String str) throws IllegalArgumentException {
        PdfTextMask pdfTextMask = new PdfTextMask();
        boolean[] zArr = null;
        String[] strArrSplit = str.split(PolicyIdFilter.OID_SEPARATOR);
        if (strArrSplit.length == 4) {
            if (strArrSplit[0].length() != 1) {
                throw new IllegalArgumentException("No se indico un caracter de ofuscacion unico");
            }
            char cCharAt = strArrSplit[0].charAt(0);
            boolean z = true;
            for (char c : strArrSplit[1].toCharArray()) {
                if (!Character.isDigit(c)) {
                    z = false;
                }
            }
            if (!z) {
                throw new IllegalArgumentException("No se indico un numero valido de digitos minimos");
            }
            int i = Integer.parseInt(strArrSplit[1]);
            String[] strArrSplit2 = strArrSplit[2].split(",");
            if (strArrSplit2.length <= 1) {
                throw new IllegalArgumentException("No se indico un listado de posiciones valido");
            }
            int length = strArrSplit2.length - 1;
            while (length >= 0 && !Boolean.parseBoolean(strArrSplit2[length])) {
                length--;
            }
            if (length > 0) {
                zArr = new boolean[length + 1];
                for (int i2 = 0; i2 < zArr.length; i2++) {
                    zArr[i2] = Boolean.parseBoolean(strArrSplit2[i2]);
                }
            }
            boolean z2 = Boolean.parseBoolean(strArrSplit[3]);
            if (cCharAt != 0 && i > -1 && zArr != null) {
                pdfTextMask.setObfuscatedChar(cCharAt);
                pdfTextMask.setMinLength(i);
                pdfTextMask.setPositions(zArr);
                pdfTextMask.setShiftSupported(z2);
            }
            return pdfTextMask;
        }
        throw new IllegalArgumentException("El numero de elementos de la mascara no es valido");
    }
}
