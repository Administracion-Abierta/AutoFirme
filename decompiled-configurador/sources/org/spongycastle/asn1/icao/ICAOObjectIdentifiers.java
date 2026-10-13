package org.spongycastle.asn1.icao;

import es.gob.afirma.signers.tsp.pkcs7.TsaParams;
import org.spongycastle.asn1.ASN1ObjectIdentifier;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:org/spongycastle/asn1/icao/ICAOObjectIdentifiers.class */
public interface ICAOObjectIdentifiers {
    public static final ASN1ObjectIdentifier id_icao = new ASN1ObjectIdentifier("2.23.136");
    public static final ASN1ObjectIdentifier id_icao_mrtd = id_icao.branch(TsaParams.TS_SIGN);
    public static final ASN1ObjectIdentifier id_icao_mrtd_security = id_icao_mrtd.branch(TsaParams.TS_SIGN);
    public static final ASN1ObjectIdentifier id_icao_ldsSecurityObject = id_icao_mrtd_security.branch(TsaParams.TS_SIGN);
    public static final ASN1ObjectIdentifier id_icao_cscaMasterList = id_icao_mrtd_security.branch(TsaParams.TS_DOC);
    public static final ASN1ObjectIdentifier id_icao_cscaMasterListSigningKey = id_icao_mrtd_security.branch(TsaParams.TS_SIGN_DOC);
    public static final ASN1ObjectIdentifier id_icao_documentTypeList = id_icao_mrtd_security.branch("4");
    public static final ASN1ObjectIdentifier id_icao_aaProtocolObject = id_icao_mrtd_security.branch("5");
    public static final ASN1ObjectIdentifier id_icao_extensions = id_icao_mrtd_security.branch("6");
    public static final ASN1ObjectIdentifier id_icao_extensions_namechangekeyrollover = id_icao_extensions.branch(TsaParams.TS_SIGN);
}
