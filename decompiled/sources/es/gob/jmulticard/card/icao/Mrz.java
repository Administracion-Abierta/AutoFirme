package es.gob.jmulticard.card.icao;

import com.sun.jna.platform.win32.COM.tlb.imp.TlbBase;
import es.gob.jmulticard.CryptoHelper;
import es.gob.jmulticard.DigestAlgorithm;
import es.gob.jmulticard.HexUtils;
import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/* JADX INFO: loaded from: autofirma.jar:es/gob/jmulticard/card/icao/Mrz.class */
public final class Mrz {
    private final SimpleDateFormat sdFormat;
    private final String mrzString;
    private final byte[] rawData;
    private final String documentCode;
    private final IcaoDocumentType documentType;
    private final String issuerCountry;
    private final String documentNumber;
    private final String name;
    private final String surname;
    private final String dateOfBirth;
    private final String sex;
    private final String dateOfExpiry;
    private final String nationality;
    private final String subjectNumber;
    private final IcaoDocumentVariant documentVariant;

    public Mrz(String mrzStr) throws IOException {
        this(mrzStr == null ? null : mrzStr.trim().replace("\n", "").replace("\r", "").replace(TlbBase.TAB, "").getBytes(StandardCharsets.UTF_8));
    }

    public Mrz(byte[] rawBytes) throws IOException {
        this.sdFormat = new SimpleDateFormat("yyMMdd");
        if (rawBytes == null || rawBytes.length < 3) {
            throw new IllegalArgumentException("La MRZ no puede ser nula ni vacia");
        }
        this.rawData = (byte[]) rawBytes.clone();
        if (rawBytes[0] == 97 && rawBytes[2] == 95 && rawBytes[3] == 31) {
            byte[] mrzBytes = new byte[this.rawData[4]];
            System.arraycopy(this.rawData, 5, mrzBytes, 0, mrzBytes.length);
            this.mrzString = new String(mrzBytes);
        } else {
            this.mrzString = new String(rawBytes);
        }
        DataInputStream dataIn = new DataInputStream(new ByteArrayInputStream(this.mrzString.getBytes(StandardCharsets.UTF_8)));
        this.documentCode = readStringWithFillers(dataIn, 2);
        this.documentType = IcaoDocumentType.getIcaoDocumentType(this.documentCode);
        this.documentVariant = IcaoDocumentVariant.getIcaoDocumentVariant(this.documentCode);
        this.issuerCountry = readString(dataIn, 3);
        switch (this.documentType) {
            case DOC_TYPE_ID1:
                String tmpDocNum = readString(dataIn, 9);
                char documentNumberCheckDigit = (char) dataIn.readUnsignedByte();
                String optionalData1 = readStringWithFillers(dataIn, 15);
                if (documentNumberCheckDigit == '<') {
                    tmpDocNum = tmpDocNum + optionalData1.substring(0, optionalData1.length() - 1);
                    optionalData1 = null;
                }
                this.subjectNumber = optionalData1;
                this.documentNumber = trimFillerChars(tmpDocNum);
                this.dateOfBirth = readString(dataIn, 6);
                dataIn.readUnsignedByte();
                this.sex = readString(dataIn, 1);
                this.dateOfExpiry = readString(dataIn, 6);
                dataIn.readUnsignedByte();
                this.nationality = readString(dataIn, 3);
                readString(dataIn, 12);
                String mrzNameString = readString(dataIn, 30);
                int delimIndex = mrzNameString.indexOf("<<");
                if (delimIndex < 0) {
                    this.name = trimFillerChars(mrzNameString);
                    this.surname = null;
                    return;
                } else {
                    this.surname = trimFillerChars(mrzNameString.substring(0, delimIndex));
                    this.name = trimFillerChars(mrzNameString.substring(delimIndex));
                    return;
                }
            case DOC_TYPE_ID3:
                String mrzNameString2 = readString(dataIn, 39);
                int delimIndex2 = mrzNameString2.indexOf("<<");
                if (delimIndex2 < 0) {
                    this.name = trimFillerChars(mrzNameString2);
                    this.surname = null;
                } else {
                    this.surname = trimFillerChars(mrzNameString2.substring(0, delimIndex2));
                    this.name = trimFillerChars(mrzNameString2.substring(delimIndex2));
                }
                this.documentNumber = trimFillerChars(readString(dataIn, 9));
                this.nationality = readString(dataIn, 3);
                this.dateOfBirth = readString(dataIn, 6);
                dataIn.readUnsignedByte();
                this.sex = readString(dataIn, 1);
                this.dateOfExpiry = readString(dataIn, 6);
                dataIn.readUnsignedByte();
                this.subjectNumber = trimFillerChars(readString(dataIn, 14));
                return;
            default:
                throw new IllegalArgumentException("Tipo de documento no soportado: " + this.documentType);
        }
    }

    public String toString() {
        return this.mrzString;
    }

    public byte[] getBytes() {
        return (byte[]) this.rawData.clone();
    }

    private static String readString(DataInputStream stringStream, int count) throws IOException {
        byte[] data = new byte[count];
        stringStream.readFully(data);
        return new String(data).trim();
    }

    private static String trimFillerChars(String str) {
        byte[] chars = str.trim().getBytes();
        for (int i = 0; i < chars.length; i++) {
            if (chars[i] == 60) {
                chars[i] = 32;
            }
        }
        return new String(chars).trim();
    }

    private static String readStringWithFillers(DataInputStream stringStream, int count) throws IOException {
        return trimFillerChars(readString(stringStream, count));
    }

    public String getName() {
        return this.name;
    }

    public String getSurname() {
        return this.surname;
    }

    public Date getDateOfBirth() throws ParseException {
        return this.sdFormat.parse(this.dateOfBirth);
    }

    public String getNationality() {
        String c = CountryCodes.getCountryName(this.nationality);
        return c != null ? c : "Desconocido";
    }

    public Gender getSex() {
        return Gender.getGender(this.sex);
    }

    public synchronized Date getDateOfExpiry() throws ParseException {
        return this.sdFormat.parse(this.dateOfExpiry);
    }

    public String getDocumentNumber() {
        return this.documentNumber;
    }

    public String getIssuerCountry() {
        String c = CountryCodes.getCountryName(this.issuerCountry);
        return c != null ? c : "Desconocido";
    }

    public IcaoDocumentType getDocumentType() {
        return this.documentType;
    }

    public String getSubjectNumber() {
        return this.subjectNumber;
    }

    public byte[] getRawData() {
        return (byte[]) this.rawData.clone();
    }

    public byte[] getMrzInformation() {
        byte[] numberBytes = getDocumentNumber().getBytes();
        byte[] numberCheck = {(byte) checkDigit(getDocumentNumber())};
        byte[] birthBytes = this.dateOfBirth.getBytes();
        byte[] birthCheck = {(byte) checkDigit(this.dateOfBirth)};
        byte[] expiryBytes = this.dateOfExpiry.getBytes();
        byte[] expiryCheck = {(byte) checkDigit(this.dateOfExpiry)};
        return HexUtils.concatenateByteArrays(numberBytes, numberCheck, birthBytes, birthCheck, expiryBytes, expiryCheck);
    }

    private static char checkDigit(String str) {
        return checkDigit(str, false);
    }

    private static char checkDigit(String str, boolean preferFillerOverZero) {
        try {
            byte[] chars = str == null ? new byte[0] : str.getBytes(StandardCharsets.UTF_8);
            int[] weights = {7, 3, 1};
            int result = 0;
            for (int i = 0; i < chars.length; i++) {
                result = (result + (weights[i % 3] * decodeMrzDigit(chars[i]))) % 10;
            }
            String checkDigitString = Integer.toString(result);
            if (checkDigitString.length() != 1) {
                throw new IllegalStateException("Error calculando el digito de control");
            }
            char checkDigit = (char) checkDigitString.getBytes(StandardCharsets.UTF_8)[0];
            if (preferFillerOverZero && checkDigit == '0') {
                checkDigit = '<';
            }
            return checkDigit;
        } catch (NumberFormatException nfe) {
            throw new IllegalStateException("Error calculando el digito de control", nfe);
        } catch (Exception e) {
            throw new IllegalArgumentException("Error calculando el digito de control", e);
        }
    }

    private static int decodeMrzDigit(byte ch) {
        switch (ch) {
            case 48:
            case 60:
                return 0;
            case 49:
                return 1;
            case 50:
                return 2;
            case 51:
                return 3;
            case 52:
                return 4;
            case 53:
                return 5;
            case 54:
                return 6;
            case 55:
                return 7;
            case 56:
                return 8;
            case 57:
                return 9;
            case 58:
            case 59:
            case 61:
            case 62:
            case 63:
            case 64:
            case 91:
            case 92:
            case 93:
            case 94:
            case 95:
            case 96:
            default:
                throw new NumberFormatException("No se ha podido decodificar el caracter del MRZ '" + ((int) ch) + "' ('" + Character.toString((char) ch) + "')");
            case 65:
            case 97:
                return 10;
            case 66:
            case 98:
                return 11;
            case 67:
            case 99:
                return 12;
            case 68:
            case 100:
                return 13;
            case 69:
            case 101:
                return 14;
            case 70:
            case 102:
                return 15;
            case 71:
            case 103:
                return 16;
            case 72:
            case 104:
                return 17;
            case 73:
            case 105:
                return 18;
            case 74:
            case 106:
                return 19;
            case 75:
            case 107:
                return 20;
            case 76:
            case 108:
                return 21;
            case 77:
            case 109:
                return 22;
            case 78:
            case 110:
                return 23;
            case 79:
            case 111:
                return 24;
            case 80:
            case 112:
                return 25;
            case 81:
            case 113:
                return 26;
            case 82:
            case 114:
                return 27;
            case 83:
            case 115:
                return 28;
            case 84:
            case 116:
                return 29;
            case 85:
            case 117:
                return 30;
            case 86:
            case 118:
                return 31;
            case 87:
            case 119:
                return 32;
            case 88:
            case 120:
                return 33;
            case 89:
            case 121:
                return 34;
            case 90:
            case 122:
                return 35;
        }
    }

    public byte[] getMrzPswd(CryptoHelper cryptoHelper) throws IOException {
        return cryptoHelper.digest(DigestAlgorithm.SHA1, getMrzInformation());
    }

    public IcaoDocumentVariant getDocumentVariant() {
        return this.documentVariant;
    }

    /* JADX INFO: loaded from: autofirma.jar:es/gob/jmulticard/card/icao/Mrz$IcaoDocumentVariant.class */
    public enum IcaoDocumentVariant {
        DOC_VAR_IR("Permiso de residencia o asilo"),
        DOC_VAR_ID("Tarjeta de identidad"),
        DOC_VAR_IM("Tarjeta de residencia de familiar de ciudadano de la UE"),
        DOC_VAR_IT("Permiso de estancia temporal"),
        DOC_VAR_IX("Tarjeta de trabajador transfronterizo"),
        DOC_VAR_IS("Documento de identidad del marino"),
        DOC_VAR_P("Pasaporte"),
        DOC_VAR_OTHER("Documento de identidad no especificado");

        private final String description;

        IcaoDocumentVariant(String d) {
            this.description = d;
        }

        @Override // java.lang.Enum
        public String toString() {
            return this.description;
        }

        static IcaoDocumentVariant getIcaoDocumentVariant(String documentCode) {
            if (documentCode.startsWith("IR")) {
                return DOC_VAR_IR;
            }
            if (documentCode.startsWith("ID")) {
                return DOC_VAR_ID;
            }
            if (documentCode.startsWith("IM")) {
                return DOC_VAR_IM;
            }
            if (documentCode.startsWith("IT")) {
                return DOC_VAR_IT;
            }
            if (documentCode.startsWith("IX")) {
                return DOC_VAR_IX;
            }
            if (documentCode.startsWith("IS")) {
                return DOC_VAR_IS;
            }
            if (documentCode.startsWith("P")) {
                return DOC_VAR_P;
            }
            return DOC_VAR_OTHER;
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:es/gob/jmulticard/card/icao/Mrz$IcaoDocumentType.class */
    public enum IcaoDocumentType {
        DOC_TYPE_ID1,
        DOC_TYPE_ID3,
        DOC_TYPE_UNSPECIFIED;

        static IcaoDocumentType getIcaoDocumentType(String documentCode) {
            if (documentCode == null || documentCode.length() < 1 || documentCode.length() > 2) {
                throw new IllegalArgumentException("El tipo de documento debe tener uno o dos caracteres, pero se recibio: " + documentCode);
            }
            if (documentCode.startsWith("A") || documentCode.startsWith("C") || documentCode.startsWith("I") || documentCode.startsWith("V")) {
                return DOC_TYPE_ID1;
            }
            if (documentCode.startsWith("P")) {
                return DOC_TYPE_ID3;
            }
            return DOC_TYPE_UNSPECIFIED;
        }
    }
}
