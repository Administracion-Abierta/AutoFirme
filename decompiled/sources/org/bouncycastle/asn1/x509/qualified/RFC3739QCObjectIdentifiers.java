package org.bouncycastle.asn1.x509.qualified;

import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.x509.X509ObjectIdentifiers;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/asn1/x509/qualified/RFC3739QCObjectIdentifiers.class */
public interface RFC3739QCObjectIdentifiers {
    public static final ASN1ObjectIdentifier id_qcs = X509ObjectIdentifiers.id_pkix.branch("11");
    public static final ASN1ObjectIdentifier id_qcs_pkixQCSyntax_v1 = id_qcs.branch("1");
    public static final ASN1ObjectIdentifier id_qcs_pkixQCSyntax_v2 = id_qcs.branch("2");
}
