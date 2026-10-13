package es.gob.jmulticard.asn1.icao;

import es.gob.jmulticard.HexUtils;
import es.gob.jmulticard.JmcLogger;
import es.gob.jmulticard.asn1.Asn1Exception;
import es.gob.jmulticard.asn1.DecoderObject;
import es.gob.jmulticard.asn1.Tlv;
import es.gob.jmulticard.asn1.TlvException;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.IOException;

/* JADX INFO: loaded from: autofirma.jar:es/gob/jmulticard/asn1/icao/AdditionalPersonalDetails.class */
public final class AdditionalPersonalDetails extends DecoderObject {
    private static final byte TAG = 107;
    private String subjectFullName = null;
    private String personalNumber = null;
    private String fullBirthDate = null;
    private String birthPlace = null;
    private String residenceAddress = null;
    private String residenceCity = null;
    private String residenceProvince = null;

    public String getSubjectFullName() {
        return this.subjectFullName;
    }

    public String getPersonalNumber() {
        return this.personalNumber;
    }

    public String getFullBirthDate() {
        return this.fullBirthDate;
    }

    public String getBirthPlace() {
        return this.birthPlace;
    }

    public String getResidenceAddress() {
        return this.residenceAddress;
    }

    public String getResidenceCity() {
        return this.residenceCity;
    }

    public String getResidenceProvince() {
        return this.residenceProvince;
    }

    @Override // es.gob.jmulticard.asn1.DecoderObject
    protected void decodeValue() throws TlvException, Asn1Exception {
        checkTag(getBytes()[0]);
        DataInputStream dis = new DataInputStream(new ByteArrayInputStream(new Tlv(getBytes()).getValue()));
        while (dis.available() > 2) {
            try {
                byte tag = dis.readByte();
                if (tag == 95) {
                    byte type = dis.readByte();
                    byte len = dis.readByte();
                    processField(type, readNBytes(dis, len));
                } else {
                    byte len2 = dis.readByte();
                    readNBytes(dis, len2);
                }
            } catch (IOException e) {
                throw new TlvException("Error procesando el contenido del DG11", e);
            }
        }
    }

    private static byte[] readNBytes(DataInputStream dis, int nBytes) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        for (int i = 0; i < nBytes; i++) {
            baos.write(dis.readByte());
        }
        return baos.toByteArray();
    }

    @Override // es.gob.jmulticard.asn1.DecoderObject
    protected byte getDefaultTag() {
        return (byte) 107;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder("Detalles personales adidionales:");
        if (this.subjectFullName != null) {
            sb.append("\n  Nombre completo del titular: ");
            sb.append(this.subjectFullName);
        }
        if (this.personalNumber != null) {
            sb.append("\n  Numero personal: ");
            sb.append(this.personalNumber);
        }
        if (this.fullBirthDate != null) {
            sb.append("\n  Fecha de nacimiento completa: ");
            sb.append(this.fullBirthDate);
        }
        if (this.birthPlace != null) {
            sb.append("\n  Lugar de nacimiento: ");
            sb.append(this.birthPlace);
        }
        if (this.residenceAddress != null) {
            sb.append("\n  Direccion: ");
            sb.append(this.residenceAddress);
        }
        if (this.residenceCity != null) {
            sb.append("\n  Ciudad: ");
            sb.append(this.residenceCity);
        }
        if (this.residenceProvince != null) {
            sb.append("\n  Provincia: ");
            sb.append(this.residenceProvince);
        }
        return sb.toString();
    }

    private void processField(byte type, byte[] value) {
        switch (type) {
            case 14:
                this.subjectFullName = new String(value);
                break;
            case 16:
                this.personalNumber = new String(value);
                break;
            case 17:
                this.birthPlace = new String(value);
                break;
            case 43:
                this.fullBirthDate = HexUtils.hexify(value, false);
                break;
            case 66:
                String fullAddress = new String(value);
                String[] addressParticles = fullAddress.split("<");
                this.residenceAddress = addressParticles[0];
                if (addressParticles.length >= 2) {
                    this.residenceCity = addressParticles[1];
                }
                if (addressParticles.length >= 3) {
                    this.residenceProvince = addressParticles[2];
                }
                break;
            default:
                JmcLogger.warning("Rotulo de DG11 no soportado: 5F" + HexUtils.hexify(new byte[]{type}, false));
                break;
        }
    }
}
