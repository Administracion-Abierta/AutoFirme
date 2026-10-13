package es.gob.afirma.signers.cades;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;
import org.spongycastle.asn1.ASN1Encodable;
import org.spongycastle.asn1.DERSequence;
import org.spongycastle.asn1.DERUTF8String;
import org.spongycastle.asn1.esf.SignerLocation;

/* JADX INFO: loaded from: autofirma.jar:es/gob/afirma/signers/cades/CAdESSignerMetadataHelper.class */
public final class CAdESSignerMetadataHelper {
    private CAdESSignerMetadataHelper() {
    }

    public static CAdESSignerMetadata getCAdESSignerMetadata(Properties properties) {
        if (properties == null) {
            return null;
        }
        String property = properties.getProperty("signatureProductionPostalCode");
        List listAsList = property == null ? null : Arrays.asList(property.split("\n"));
        String property2 = properties.getProperty("signatureProductionCountry");
        String property3 = properties.getProperty("signatureProductionCity");
        if (listAsList != null || property2 != null || property3 != null) {
            return new CAdESSignerMetadata(property2, property3, listAsList);
        }
        return null;
    }

    public static SignerLocation getSignerLocation(CAdESSignerMetadata.CAdESSignerLocation cAdESSignerLocation) {
        if (cAdESSignerLocation == null) {
            return null;
        }
        if (cAdESSignerLocation.getCountryName() == null && cAdESSignerLocation.getLocalityName() == null && cAdESSignerLocation.getPostalAddress() == null) {
            return null;
        }
        List<String> postalAddress = cAdESSignerLocation.getPostalAddress();
        ArrayList arrayList = null;
        if (postalAddress != null) {
            arrayList = new ArrayList(postalAddress.size());
            Iterator<String> it = postalAddress.iterator();
            while (it.hasNext()) {
                arrayList.add(new DERUTF8String(it.next()));
            }
        }
        return new SignerLocation(cAdESSignerLocation.getCountryName() != null ? new DERUTF8String(cAdESSignerLocation.getCountryName()) : null, cAdESSignerLocation.getLocalityName() != null ? new DERUTF8String(cAdESSignerLocation.getLocalityName()) : null, arrayList != null ? new DERSequence((ASN1Encodable[]) arrayList.toArray(new ASN1Encodable[0])) : null);
    }
}
