package es.gob.jmulticard.asn1.icao;

import es.gob.jmulticard.JmcLogger;
import es.gob.jmulticard.asn1.Asn1Exception;
import es.gob.jmulticard.asn1.DecoderObject;
import es.gob.jmulticard.asn1.TlvException;
import es.gob.jmulticard.card.icao.Gender;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.regex.Pattern;

/* JADX INFO: loaded from: autofirma.jar:es/gob/jmulticard/asn1/icao/OptionalDetails.class */
public class OptionalDetails extends DecoderObject {
    private static final byte TAG = 109;
    public static final String SPAIN = "ESPAÑA";
    private static final Pattern CONTROL_CHARACTER_WORD = Pattern.compile("\\p{Cc}{2}");
    private static final List<String> SPANISH_PROVINCES = Arrays.asList("ARABA/ALAVA", "ALBACETE", "ALICANTE-ALACANT", "ALMERIA", "ASTURIAS", "AVILA", "BADAJOZ", "BARCELONA", "BURGOS", "CACERES", "CADIZ", "CANTABRIA", "CIUDAD REAL", "CORDOBA", "A CORUÑA", "CUENCA", "GIRONA", "GRANADA", "GUADALAJARA", "GIPUZKOA", "HUELVA", "HUESCA", "ILLES BALEARS", "JAEN", "LEON", "LLEIDA", "LUGO", "MADRID", "MALAGA", "MURCIA", "NAVARRA", "OURENSE", "PALENCIA", "LAS PALMAS", "PONTEVEDRA", "LA RIOJA", "SEGOVIA", "SEVILLA", "SORIA", "TARRAGONA", "SANTA CRUZ DE TENERIFE", "TERUEL", "TOLEDA", "VALENCIA-VALENCIA", "VALLADOLID", "BIZKAIA", "ZAMORA", "ZARAGOZA", "CEUTA", "MELILLA");
    private final SimpleDateFormat dateFormat = new SimpleDateFormat("dd MM yyyy");
    private String[] parsedValues = null;

    @Override // es.gob.jmulticard.asn1.DecoderObject
    protected byte getDefaultTag() {
        return (byte) 109;
    }

    public String getName() {
        if (this.parsedValues != null && this.parsedValues.length > 3) {
            return this.parsedValues[3];
        }
        return "";
    }

    public String getSecondSurname() {
        if (this.parsedValues != null && this.parsedValues.length > 2) {
            return this.parsedValues[2];
        }
        return "";
    }

    public String getFirstSurname() {
        if (this.parsedValues != null && this.parsedValues.length > 1) {
            return this.parsedValues[1];
        }
        return "";
    }

    public String getIdNumber() {
        if (this.parsedValues != null && this.parsedValues.length > 4) {
            return this.parsedValues[4];
        }
        return "";
    }

    public synchronized Date getBirthDate() throws ParseException {
        if (this.parsedValues != null && this.parsedValues.length > 5) {
            return this.dateFormat.parse(this.parsedValues[5]);
        }
        return new Date();
    }

    public String getNationality() {
        if (this.parsedValues != null && this.parsedValues.length > 6) {
            return this.parsedValues[6];
        }
        return "";
    }

    public Date getExpirationDate() throws ParseException {
        if (this.parsedValues != null && this.parsedValues.length > 7) {
            return this.dateFormat.parse(this.parsedValues[7]);
        }
        return new Date();
    }

    public String getSupportNumber() {
        if (this.parsedValues != null && this.parsedValues.length > 7) {
            return this.parsedValues[8];
        }
        return "";
    }

    public Gender getSex() {
        if (this.parsedValues != null && this.parsedValues.length > 9) {
            return Gender.getGender(this.parsedValues[9]);
        }
        return Gender.OTHER;
    }

    public String getBirthCity() {
        if (this.parsedValues != null && this.parsedValues.length > 10) {
            return this.parsedValues[10];
        }
        return "";
    }

    public String getBirthProvince() {
        if (this.parsedValues != null && this.parsedValues.length > 11) {
            if (SPANISH_PROVINCES.contains(this.parsedValues[11].toUpperCase())) {
                return this.parsedValues[11];
            }
            return this.parsedValues[10];
        }
        return "";
    }

    public String getBirthCountry() {
        if (this.parsedValues != null && this.parsedValues.length > 11) {
            if (SPANISH_PROVINCES.contains(this.parsedValues[11].toUpperCase())) {
                return SPAIN;
            }
            return this.parsedValues[11];
        }
        return "";
    }

    public String getParentsNames() {
        if (this.parsedValues != null && this.parsedValues.length > 12) {
            return this.parsedValues[12];
        }
        return "";
    }

    public String getAddress() {
        if (this.parsedValues != null && this.parsedValues.length > 13) {
            return this.parsedValues[13];
        }
        return "";
    }

    public String getCity() {
        if (this.parsedValues != null && this.parsedValues.length > 14) {
            return this.parsedValues[14];
        }
        return "";
    }

    public String getCountry() {
        if (this.parsedValues != null && this.parsedValues.length > 16) {
            if (SPANISH_PROVINCES.contains(this.parsedValues[16].toUpperCase())) {
                return SPAIN;
            }
            return this.parsedValues[16];
        }
        return "";
    }

    public String getProvince() {
        if (this.parsedValues != null && this.parsedValues.length > 16) {
            if (!this.parsedValues[15].isEmpty()) {
                return this.parsedValues[15];
            }
            return this.parsedValues[16];
        }
        return "";
    }

    @Override // es.gob.jmulticard.asn1.DecoderObject
    protected void decodeValue() throws TlvException, Asn1Exception {
        checkTag(getBytes()[0]);
        try {
            this.parsedValues = new String(getBytes()).split(CONTROL_CHARACTER_WORD.pattern());
        } catch (Exception e) {
            JmcLogger.warning("El DG13 esta en un formato no soportado: " + e);
        }
    }

    public String toString() {
        String birthDate;
        String expirationDate;
        try {
            birthDate = this.dateFormat.format(getBirthDate());
        } catch (ParseException e) {
            birthDate = "Error: " + e;
        }
        try {
            expirationDate = this.dateFormat.format(getExpirationDate());
        } catch (ParseException e2) {
            expirationDate = "Error: " + e2;
        }
        return "Detalles opcionales (DG13):\n  Nombre del titular: " + getName() + "\n  Primer apellido del titular: " + getFirstSurname() + "\n  Segundo apellido del titular: " + getSecondSurname() + "\n  Numero del DNI del titular: " + getIdNumber() + "\n  Nacionalidad del titular: " + getNationality() + "\n  Fecha de nacimiento del titular: " + birthDate + "\n  Ciudad de nacimiento del titular: " + getBirthCity() + "\n  Provincia de nacimiento del titular: " + getBirthProvince() + "\n  Pais de nacimiento del titular: " + getBirthCountry() + "\n  Nombres de los padres del titular: " + getParentsNames() + "\n  Sexo del titular: " + getSex() + "\n  Direccion de residencia del titular: " + getAddress() + "\n  Ciudad de residencia del titular: " + getCity() + "\n  Provincia de residencia del titular: " + getProvince() + "\n  Pais de residencia del titular: " + getCountry() + "\n  Fecha de caducidad del DNI: " + expirationDate + "\n  Numero de soporte del DNI: " + getSupportNumber();
    }
}
