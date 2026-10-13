package es.gob.afirma.signvalidation;

/* JADX INFO: loaded from: autofirma.jar:es/gob/afirma/signvalidation/SignValidity.class */
public final class SignValidity {
    private final SIGN_DETAIL_TYPE validity;
    private final VALIDITY_ERROR error;
    private final Exception errorException;

    /* JADX INFO: loaded from: autofirma.jar:es/gob/afirma/signvalidation/SignValidity$SIGN_DETAIL_TYPE.class */
    public enum SIGN_DETAIL_TYPE {
        OK,
        KO,
        UNKNOWN,
        PENDING_CONFIRM_BY_USER,
        GENERATED
    }

    /* JADX INFO: loaded from: autofirma.jar:es/gob/afirma/signvalidation/SignValidity$VALIDITY_ERROR.class */
    public enum VALIDITY_ERROR {
        NO_DATA,
        CORRUPTED_SIGN,
        NO_MATCH_DATA,
        NO_SIGN,
        CERTIFICATE_PROBLEM,
        CERTIFICATE_EXPIRED,
        CERTIFICATE_NOT_VALID_YET,
        CANT_VALIDATE_CERT,
        ALGORITHM_NOT_SUPPORTED,
        CA_NOT_SUPPORTED,
        CRL_PROBLEM,
        PDF_UNKOWN_VALIDITY,
        OOXML_UNKOWN_VALIDITY,
        ODF_UNKOWN_VALIDITY,
        UNKOWN_ERROR,
        UNKOWN_SIGNATURE_FORMAT,
        MODIFIED_FORM,
        MODIFIED_DOCUMENT,
        OVERLAPPING_SIGNATURE,
        SUSPECTED_SIGNATURE,
        SIGN_PROFILE_NOT_CHECKED,
        CANT_VALIDATE_EXTERNALLY_DETACHED,
        BAD_BUILD_SIGN,
        CERTIFIED_SIGN_REVISION
    }

    public String toString() {
        String strValidityTypeToString = validityTypeToString();
        if (this.error == null) {
            return strValidityTypeToString;
        }
        switch (this.error) {
            case NO_DATA:
                return strValidityTypeToString + ": no se puede comprobar la validez por no tener los datos firmados";
            case CORRUPTED_SIGN:
                return strValidityTypeToString + ": la informaci&oacute;n contenida en la firma no es consistente (certificados corruptos, etc.)";
            case NO_MATCH_DATA:
                return strValidityTypeToString + ": la firma no se corresponde con los datos firmados";
            case NO_SIGN:
                return strValidityTypeToString + ": no se encuentra la firma dentro del documento";
            case CERTIFICATE_PROBLEM:
                return strValidityTypeToString + ": no se puede extraer un certificado o este no es v&aacute;lido";
            case CERTIFICATE_EXPIRED:
                return strValidityTypeToString + ": existe un certificado de firma caducado";
            case CERTIFICATE_NOT_VALID_YET:
                return strValidityTypeToString + ": existe un certificado de firma que aun no es v&aacute;lido";
            case CANT_VALIDATE_CERT:
                return strValidityTypeToString + ": no se ha podido validar el certificado correctamente";
            case ALGORITHM_NOT_SUPPORTED:
                return strValidityTypeToString + ": la firma contiene un algoritmo no reconocido o no soportado";
            case CA_NOT_SUPPORTED:
                return strValidityTypeToString + ": no se soporta la CA de expedici&oacute;n";
            case CRL_PROBLEM:
                return strValidityTypeToString + ": existe algun problema con las CRLs incrustadas en la firma";
            case PDF_UNKOWN_VALIDITY:
                return strValidityTypeToString + ": no se puede comprobar la validez de la firma PDF";
            case OOXML_UNKOWN_VALIDITY:
                return strValidityTypeToString + ": no se puede comprobar la validez de la firma OOXML";
            case ODF_UNKOWN_VALIDITY:
                return strValidityTypeToString + ": no se puede comprobar la validez de la firma ODF";
            case UNKOWN_ERROR:
                return strValidityTypeToString + ": no se puede comprobar la validez de la firma";
            case UNKOWN_SIGNATURE_FORMAT:
                return strValidityTypeToString + ": los datos proporcionados no se corresponden con ning&uacute;n formato de firma reconocido";
            case MODIFIED_DOCUMENT:
                return strValidityTypeToString + ": el documento PDF es sospechoso de haberse modificado tras la firma";
            case OVERLAPPING_SIGNATURE:
                return strValidityTypeToString + ": las firmas del documento se solapan pudiendo ocultar informaci&oacute;n";
            case MODIFIED_FORM:
                return strValidityTypeToString + ": un formulario del documento fue modificado tras la firma";
            case SUSPECTED_SIGNATURE:
                return strValidityTypeToString + ": el documento es sospechoso y el usuario debe revisar la firma";
            case SIGN_PROFILE_NOT_CHECKED:
                return strValidityTypeToString + ": la firma contiene atributos longevos que Autofirma no puede validar. Para una validaci&oacute;n completa recurra a VALIDe.";
            case CANT_VALIDATE_EXTERNALLY_DETACHED:
                return strValidityTypeToString + ": no se permite la validaci&oacute;n de firmas con referencias externas";
            case BAD_BUILD_SIGN:
                return strValidityTypeToString + ": la firma no est&aacute; correctamente formada";
            case CERTIFIED_SIGN_REVISION:
                return strValidityTypeToString + ": El documento certificado no permit&iacute;a nuevas firmas";
            default:
                return strValidityTypeToString;
        }
    }

    public SignValidity(SIGN_DETAIL_TYPE sign_detail_type, VALIDITY_ERROR validity_error, Exception exc) {
        this.validity = sign_detail_type;
        this.error = validity_error;
        this.errorException = exc;
    }

    public SignValidity(SIGN_DETAIL_TYPE sign_detail_type, VALIDITY_ERROR validity_error) {
        this(sign_detail_type, validity_error, null);
    }

    public SIGN_DETAIL_TYPE getValidity() {
        return this.validity;
    }

    public VALIDITY_ERROR getError() {
        return this.error;
    }

    public Exception getErrorException() {
        return this.errorException;
    }

    public String validityTypeToString() {
        if (this.validity.equals(SIGN_DETAIL_TYPE.OK) || this.validity.equals(SIGN_DETAIL_TYPE.GENERATED)) {
            return "Firma valida";
        }
        if (this.validity.equals(SIGN_DETAIL_TYPE.UNKNOWN)) {
            return "Validación incompleta";
        }
        if (this.validity.equals(SIGN_DETAIL_TYPE.PENDING_CONFIRM_BY_USER)) {
            return "Validez a confirmar por el usuario";
        }
        if (this.validity.equals(SIGN_DETAIL_TYPE.KO)) {
            return "Firma no valida";
        }
        return null;
    }
}
