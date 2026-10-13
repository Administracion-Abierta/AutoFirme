package org.spongycastle.asn1.x509;

import es.gob.afirma.signers.tsp.pkcs7.TsaParams;
import org.spongycastle.asn1.ASN1ObjectIdentifier;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:org/spongycastle/asn1/x509/X509AttributeIdentifiers.class */
public interface X509AttributeIdentifiers {
    public static final ASN1ObjectIdentifier RoleSyntax = new ASN1ObjectIdentifier("2.5.4.72");
    public static final ASN1ObjectIdentifier id_pe_ac_auditIdentity = X509ObjectIdentifiers.id_pe.branch("4");
    public static final ASN1ObjectIdentifier id_pe_aaControls = X509ObjectIdentifiers.id_pe.branch("6");
    public static final ASN1ObjectIdentifier id_pe_ac_proxying = X509ObjectIdentifiers.id_pe.branch("10");
    public static final ASN1ObjectIdentifier id_ce_targetInformation = X509ObjectIdentifiers.id_ce.branch("55");
    public static final ASN1ObjectIdentifier id_aca = X509ObjectIdentifiers.id_pkix.branch("10");
    public static final ASN1ObjectIdentifier id_aca_authenticationInfo = id_aca.branch(TsaParams.TS_SIGN);
    public static final ASN1ObjectIdentifier id_aca_accessIdentity = id_aca.branch(TsaParams.TS_DOC);
    public static final ASN1ObjectIdentifier id_aca_chargingIdentity = id_aca.branch(TsaParams.TS_SIGN_DOC);
    public static final ASN1ObjectIdentifier id_aca_group = id_aca.branch("4");
    public static final ASN1ObjectIdentifier id_aca_encAttrs = id_aca.branch("6");
    public static final ASN1ObjectIdentifier id_at_role = new ASN1ObjectIdentifier("2.5.4.72");
    public static final ASN1ObjectIdentifier id_at_clearance = new ASN1ObjectIdentifier("2.5.1.5.55");
}
