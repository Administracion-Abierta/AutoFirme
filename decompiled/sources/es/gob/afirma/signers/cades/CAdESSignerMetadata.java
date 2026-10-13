package es.gob.afirma.signers.cades;

import java.util.ArrayList;
import java.util.List;

/* JADX INFO: loaded from: autofirma.jar:es/gob/afirma/signers/cades/CAdESSignerMetadata.class */
public final class CAdESSignerMetadata {
    private final CAdESSignerLocation signerLocation;
    private static final int POSTAL_ADDRESS_MAX_LINES = 6;

    public CAdESSignerMetadata(String str, String str2, List<String> list) {
        this.signerLocation = new CAdESSignerLocation(str, str2, list);
    }

    public CAdESSignerLocation getSignerLocation() {
        return this.signerLocation;
    }

    /* JADX INFO: loaded from: autofirma.jar:es/gob/afirma/signers/cades/CAdESSignerMetadata$CAdESSignerLocation.class */
    public static final class CAdESSignerLocation {
        private final String countryName;
        private final String localityName;
        private final List<String> postalAddress;

        CAdESSignerLocation(String str, String str2, List<String> list) {
            if (list != null && list.size() > 6) {
                throw new IllegalArgumentException("La direccion postal debe tener un maximo de seis lineas, y se han proporcionado " + list.size());
            }
            this.countryName = str;
            this.localityName = str2;
            this.postalAddress = list != null ? new ArrayList(list) : null;
        }

        public String getCountryName() {
            return this.countryName;
        }

        public String getLocalityName() {
            return this.localityName;
        }

        public List<String> getPostalAddress() {
            if (this.postalAddress != null) {
                return new ArrayList(this.postalAddress);
            }
            return null;
        }
    }
}
