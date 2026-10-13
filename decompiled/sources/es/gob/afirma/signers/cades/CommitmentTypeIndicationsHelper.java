package es.gob.afirma.signers.cades;

import com.sun.jna.platform.win32.COM.tlb.imp.TlbConst;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.logging.Logger;
import java.util.regex.Pattern;
import org.spongycastle.asn1.ASN1ObjectIdentifier;
import org.spongycastle.asn1.DERSequence;
import org.spongycastle.asn1.esf.CommitmentTypeIdentifier;
import org.spongycastle.asn1.esf.CommitmentTypeIndication;

/* JADX INFO: loaded from: autofirma.jar:es/gob/afirma/signers/cades/CommitmentTypeIndicationsHelper.class */
public final class CommitmentTypeIndicationsHelper {
    private static final ASN1ObjectIdentifier COMMITMENT_TYPE_IDENTIFIER_PROOF_OF_ORIGIN = CommitmentTypeIdentifier.proofOfOrigin;
    private static final ASN1ObjectIdentifier COMMITMENT_TYPE_IDENTIFIER_PROOF_OF_RECEIPT = CommitmentTypeIdentifier.proofOfReceipt;
    private static final ASN1ObjectIdentifier COMMITMENT_TYPE_IDENTIFIER_PROOF_OF_DELIVERY = CommitmentTypeIdentifier.proofOfDelivery;
    private static final ASN1ObjectIdentifier COMMITMENT_TYPE_IDENTIFIER_PROOF_OF_SENDER = CommitmentTypeIdentifier.proofOfSender;
    private static final ASN1ObjectIdentifier COMMITMENT_TYPE_IDENTIFIER_PROOF_OF_APPROVAL = CommitmentTypeIdentifier.proofOfApproval;
    private static final ASN1ObjectIdentifier COMMITMENT_TYPE_IDENTIFIER_PROOF_OF_CREATION = CommitmentTypeIdentifier.proofOfCreation;
    private static final Map<String, ASN1ObjectIdentifier> COMMITMENT_TYPE_IDENTIFIERS = new HashMap(6);
    private static final Logger LOGGER;

    static {
        COMMITMENT_TYPE_IDENTIFIERS.put("1", COMMITMENT_TYPE_IDENTIFIER_PROOF_OF_ORIGIN);
        COMMITMENT_TYPE_IDENTIFIERS.put("2", COMMITMENT_TYPE_IDENTIFIER_PROOF_OF_RECEIPT);
        COMMITMENT_TYPE_IDENTIFIERS.put("3", COMMITMENT_TYPE_IDENTIFIER_PROOF_OF_DELIVERY);
        COMMITMENT_TYPE_IDENTIFIERS.put(TlbConst.TYPELIB_MINOR_VERSION_WORD, COMMITMENT_TYPE_IDENTIFIER_PROOF_OF_SENDER);
        COMMITMENT_TYPE_IDENTIFIERS.put(TlbConst.TYPELIB_MINOR_VERSION_OFFICE, COMMITMENT_TYPE_IDENTIFIER_PROOF_OF_APPROVAL);
        COMMITMENT_TYPE_IDENTIFIERS.put("6", COMMITMENT_TYPE_IDENTIFIER_PROOF_OF_CREATION);
        LOGGER = Logger.getLogger("es.gob.afirma");
    }

    private CommitmentTypeIndicationsHelper() {
    }

    public static List<CommitmentTypeIndicationBean> getCommitmentTypeIndications(Properties properties) {
        ArrayList arrayList;
        ArrayList arrayList2 = new ArrayList();
        if (properties == null) {
            return arrayList2;
        }
        String property = properties.getProperty("commitmentTypeIndications");
        if (property == null) {
            return arrayList2;
        }
        try {
            int i = Integer.parseInt(property);
            if (i < 1) {
                throw new NumberFormatException();
            }
            for (int i2 = 0; i2 <= i; i2++) {
                String property2 = properties.getProperty("commitmentTypeIndication" + Integer.toString(i2) + "Identifier");
                if (property2 != null) {
                    ASN1ObjectIdentifier aSN1ObjectIdentifier = COMMITMENT_TYPE_IDENTIFIERS.get(property2);
                    if (aSN1ObjectIdentifier == null) {
                        LOGGER.severe("El identificador del CommitmentTypeIndication " + i2 + " no es un tipo soportado (" + property2 + "), se omitira y se continuara con el siguiente");
                    } else {
                        String string = aSN1ObjectIdentifier.toString();
                        String property3 = properties.getProperty("commitmentTypeIndication" + Integer.toString(i2) + "CommitmentTypeQualifiers");
                        if (property3 == null) {
                            arrayList = null;
                        } else {
                            arrayList = new ArrayList();
                            for (String str : property3.split(Pattern.quote("|"))) {
                                arrayList.add(str);
                            }
                        }
                        arrayList2.add(new CommitmentTypeIndicationBean(string, arrayList));
                    }
                }
            }
            return arrayList2;
        } catch (Exception e) {
            LOGGER.severe("El parametro adicional 'CommitmentTypeIndications' debe contener un valor numerico entero (el valor actual es " + property + "), no se anadira el CommitmentTypeIndication: " + e);
            return arrayList2;
        }
    }

    static CommitmentTypeIndication generateCommitmentTypeIndication(CommitmentTypeIndicationBean commitmentTypeIndicationBean) {
        if (commitmentTypeIndicationBean == null) {
            throw new IllegalArgumentException("El CommitmentTypeIndicationBean no puede ser nulo");
        }
        try {
            ASN1ObjectIdentifier aSN1ObjectIdentifier = new ASN1ObjectIdentifier(commitmentTypeIndicationBean.getIdentifier());
            if (commitmentTypeIndicationBean.getQualifiers() == null || commitmentTypeIndicationBean.getQualifiers().size() < 1) {
                return new CommitmentTypeIndication(aSN1ObjectIdentifier);
            }
            String[] strArr = (String[]) commitmentTypeIndicationBean.getQualifiers().toArray(new String[0]);
            ASN1ObjectIdentifier[] aSN1ObjectIdentifierArr = new ASN1ObjectIdentifier[strArr.length];
            for (int i = 0; i < strArr.length; i++) {
                try {
                    aSN1ObjectIdentifierArr[i] = new ASN1ObjectIdentifier(strArr[i]);
                } catch (Exception e) {
                    throw new IllegalArgumentException("El calificador proporcionado no es un OID (" + strArr[i] + "): " + e, e);
                }
            }
            return new CommitmentTypeIndication(aSN1ObjectIdentifier, new DERSequence(aSN1ObjectIdentifierArr));
        } catch (Exception e2) {
            throw new IllegalArgumentException("El identificador del CommitmentTypeIndication debe ser un OID valido: " + e2, e2);
        }
    }
}
