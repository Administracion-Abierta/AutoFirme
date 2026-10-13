package es.gob.jmulticard.apdu.iso7816four;

import es.gob.jmulticard.HexUtils;
import es.gob.jmulticard.apdu.Apdu;
import es.gob.jmulticard.apdu.ResponseApdu;

/* JADX INFO: loaded from: autofirma.jar:es/gob/jmulticard/apdu/iso7816four/SelectFileApduResponse.class */
public final class SelectFileApduResponse extends ResponseApdu {
    private byte[] dfName;
    private byte[] fileId;
    private byte[] fileLength;

    public SelectFileApduResponse(Apdu apduResponse) {
        super(apduResponse.getBytes());
        this.dfName = null;
        this.fileId = null;
        this.fileLength = null;
        decode();
    }

    private void decode() {
        if (isOk() && getData().length > 0) {
            if (getData().length - 2 == getData()[1]) {
                int propInformationIndex = 2;
                if (getData()[2] == -127) {
                    int propInformationIndex2 = 2 + 1;
                    byte b = getData()[propInformationIndex2];
                    int propInformationIndex3 = propInformationIndex2 + 1;
                    this.fileLength = getBytesFromData(propInformationIndex3, b);
                    propInformationIndex = propInformationIndex3 + b;
                }
                if (getData()[propInformationIndex] == -127) {
                    int propInformationIndex4 = propInformationIndex + 1;
                    byte b2 = getData()[propInformationIndex4];
                    int propInformationIndex5 = propInformationIndex4 + 1;
                    this.fileId = getBytesFromData(propInformationIndex5, b2);
                    propInformationIndex = propInformationIndex5 + b2;
                }
                if (getData()[propInformationIndex] == -124) {
                    int propInformationIndex6 = propInformationIndex + 1;
                    byte b3 = getData()[propInformationIndex6];
                    int propInformationIndex7 = propInformationIndex6 + 1;
                    this.dfName = getBytesFromData(propInformationIndex7, b3);
                    propInformationIndex = propInformationIndex7 + b3;
                }
                if (getData()[propInformationIndex] == -123 && getData()[propInformationIndex + 1] == 10) {
                    this.fileId = getBytesFromData(propInformationIndex + 3, 2);
                    this.fileLength = getBytesFromData(propInformationIndex + 5, 2);
                }
            }
        }
    }

    private byte[] getBytesFromData(int offset, int length) {
        byte[] result = new byte[length];
        System.arraycopy(getData(), offset, result, 0, length);
        return result;
    }

    byte[] getDfName() {
        if (this.dfName != null) {
            byte[] out = new byte[this.dfName.length];
            System.arraycopy(this.dfName, 0, out, 0, this.dfName.length);
            return out;
        }
        return null;
    }

    byte[] getFileId() {
        if (this.fileId != null) {
            byte[] out = new byte[this.fileId.length];
            System.arraycopy(this.fileId, 0, out, 0, this.fileId.length);
            return out;
        }
        return null;
    }

    public int getFileLength() {
        if (this.fileLength != null) {
            return ((this.fileLength[0] & 255) << 8) | (this.fileLength[1] & 255);
        }
        return 0;
    }

    @Override // es.gob.jmulticard.apdu.ResponseApdu
    public boolean isOk() {
        if (getData().length == 0) {
            return super.isOk();
        }
        return super.isOk() && getData()[0] == 111 && getData().length > 2;
    }

    @Override // es.gob.jmulticard.apdu.Apdu
    public String toString() {
        StringBuilder sb = new StringBuilder("Resultado de la seleccion de fichero:\n");
        if (getDfName() != null) {
            sb.append(" Nombre del fichero: " + new String(getDfName()));
            sb.append('\n');
        }
        if (getFileId() != null) {
            sb.append(" Identificador de fichero: " + HexUtils.hexify(getFileId(), true));
            sb.append('\n');
        }
        sb.append(" Longitud del fichero: " + getFileLength());
        return sb.toString();
    }
}
