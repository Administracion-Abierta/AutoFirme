package es.gob.jmulticard.card.icao;

import org.apache.pdfbox.contentstream.operator.OperatorName;

/* JADX INFO: loaded from: autofirma.jar:es/gob/jmulticard/card/icao/Gender.class */
public enum Gender {
    MALE("Hombre"),
    FEMALE("Mujer"),
    OTHER("No conocido");

    private final String desc;

    Gender(String d) {
        this.desc = d;
    }

    @Override // java.lang.Enum
    public String toString() {
        return this.desc;
    }

    public static Gender getGender(String text) {
        if (text == null) {
            throw new IllegalArgumentException("El texto de descripcion del sexo no puede ser nulo");
        }
        if ("F".equalsIgnoreCase(text.trim())) {
            return FEMALE;
        }
        if (OperatorName.SET_LINE_MITERLIMIT.equalsIgnoreCase(text.trim())) {
            return MALE;
        }
        throw new IllegalArgumentException("Sexo indeterminado: " + text);
    }
}
