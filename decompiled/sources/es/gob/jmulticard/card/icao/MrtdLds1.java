package es.gob.jmulticard.card.icao;

import es.gob.jmulticard.asn1.Asn1Exception;
import es.gob.jmulticard.asn1.TlvException;
import es.gob.jmulticard.asn1.icao.AdditionalPersonalDetails;
import es.gob.jmulticard.asn1.icao.Com;
import es.gob.jmulticard.asn1.icao.OptionalDetails;
import es.gob.jmulticard.asn1.icao.SecurityOptions;
import es.gob.jmulticard.asn1.icao.Sod;
import es.gob.jmulticard.asn1.icao.SubjectFacePhoto;
import es.gob.jmulticard.asn1.icao.SubjectSignaturePhoto;
import es.gob.jmulticard.card.Location;
import java.io.IOException;
import java.security.SignatureException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

/* JADX INFO: loaded from: autofirma.jar:es/gob/jmulticard/card/icao/MrtdLds1.class */
public interface MrtdLds1 {
    public static final Location FILE_DG01_LOCATION = new Location("3F010101");
    public static final Location FILE_DG02_LOCATION = new Location("3F010102");
    public static final Location FILE_DG03_LOCATION = new Location("3F010103");
    public static final Location FILE_DG04_LOCATION = new Location("3F010104");
    public static final Location FILE_DG05_LOCATION = new Location("3F010105");
    public static final Location FILE_DG06_LOCATION = new Location("3F010106");
    public static final Location FILE_DG07_LOCATION = new Location("3F010107");
    public static final Location FILE_DG08_LOCATION = new Location("3F010108");
    public static final Location FILE_DG09_LOCATION = new Location("3F010109");
    public static final Location FILE_DG10_LOCATION = new Location("3F01010A");
    public static final Location FILE_DG11_LOCATION = new Location("3F01010B");
    public static final Location FILE_DG12_LOCATION = new Location("3F01010C");
    public static final Location FILE_DG13_LOCATION = new Location("3F01010D");
    public static final Location FILE_DG14_LOCATION = new Location("3F01010E");
    public static final Location FILE_DG15_LOCATION = new Location("3F01010F");
    public static final Location FILE_DG16_LOCATION = new Location("3F010110");
    public static final Location FILE_SOD_LOCATION = new Location("3F01011D");
    public static final Location FILE_COM_LOCATION = new Location("3F01011E");
    public static final Location FILE_CARD_ACCESS_LOCATION = new Location("011C");
    public static final Location FILE_ATR_INFO_LOCATION = new Location("2F01");
    public static final Location FILE_DIR_LOCATION = new Location("2F00");
    public static final Location FILE_CARD_SECURITY_LOCATION = new Location("011D");

    byte[] getDir() throws IOException;

    Mrz getDg1() throws IOException;

    SubjectFacePhoto getDg2() throws IOException;

    byte[] getDg3() throws IOException;

    byte[] getDg4() throws IOException;

    byte[] getDg5() throws IOException;

    byte[] getDg6() throws IOException;

    SubjectSignaturePhoto getDg7() throws IOException;

    byte[] getDg8() throws IOException;

    byte[] getDg9() throws IOException;

    byte[] getDg10() throws IOException;

    AdditionalPersonalDetails getDg11() throws IOException;

    byte[] getDg12() throws IOException;

    OptionalDetails getDg13() throws IOException;

    SecurityOptions getDg14() throws IOException;

    byte[] getDg15() throws IOException;

    byte[] getDg16() throws IOException;

    Sod getSod() throws IOException;

    Com getCom() throws IOException;

    byte[] getCardAccess() throws IOException;

    byte[] getCardSecurity() throws IOException;

    byte[] getAtrInfo() throws IOException;

    X509Certificate[] checkSecurityObjects() throws TlvException, SignatureException, IOException, Asn1Exception, CertificateException;
}
