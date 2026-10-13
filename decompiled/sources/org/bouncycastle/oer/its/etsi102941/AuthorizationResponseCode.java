package org.bouncycastle.oer.its.etsi102941;

import java.math.BigInteger;
import org.bouncycastle.asn1.ASN1Enumerated;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/oer/its/etsi102941/AuthorizationResponseCode.class */
public class AuthorizationResponseCode extends ASN1Enumerated {
    public static final AuthorizationResponseCode ok = new AuthorizationResponseCode(0);
    public static final AuthorizationResponseCode its_aa_cantparse = new AuthorizationResponseCode(1);
    public static final AuthorizationResponseCode its_aa_badcontenttype = new AuthorizationResponseCode(2);
    public static final AuthorizationResponseCode its_aa_imnottherecipient = new AuthorizationResponseCode(3);
    public static final AuthorizationResponseCode its_aa_unknownencryptionalgorithm = new AuthorizationResponseCode(4);
    public static final AuthorizationResponseCode its_aa_decryptionfailed = new AuthorizationResponseCode(5);
    public static final AuthorizationResponseCode its_aa_keysdontmatch = new AuthorizationResponseCode(6);
    public static final AuthorizationResponseCode its_aa_incompleterequest = new AuthorizationResponseCode(7);
    public static final AuthorizationResponseCode its_aa_invalidencryptionkey = new AuthorizationResponseCode(8);
    public static final AuthorizationResponseCode its_aa_outofsyncrequest = new AuthorizationResponseCode(9);
    public static final AuthorizationResponseCode its_aa_unknownea = new AuthorizationResponseCode(10);
    public static final AuthorizationResponseCode its_aa_invalidea = new AuthorizationResponseCode(11);
    public static final AuthorizationResponseCode its_aa_deniedpermissions = new AuthorizationResponseCode(12);
    public static final AuthorizationResponseCode aa_ea_cantreachea = new AuthorizationResponseCode(13);
    public static final AuthorizationResponseCode ea_aa_cantparse = new AuthorizationResponseCode(14);
    public static final AuthorizationResponseCode ea_aa_badcontenttype = new AuthorizationResponseCode(15);
    public static final AuthorizationResponseCode ea_aa_imnottherecipient = new AuthorizationResponseCode(16);
    public static final AuthorizationResponseCode ea_aa_unknownencryptionalgorithm = new AuthorizationResponseCode(17);
    public static final AuthorizationResponseCode ea_aa_decryptionfailed = new AuthorizationResponseCode(18);
    public static final AuthorizationResponseCode invalidaa = new AuthorizationResponseCode(19);
    public static final AuthorizationResponseCode invalidaasignature = new AuthorizationResponseCode(20);
    public static final AuthorizationResponseCode wrongea = new AuthorizationResponseCode(21);
    public static final AuthorizationResponseCode unknownits = new AuthorizationResponseCode(22);
    public static final AuthorizationResponseCode invalidsignature = new AuthorizationResponseCode(23);
    public static final AuthorizationResponseCode invalidencryptionkey = new AuthorizationResponseCode(24);
    public static final AuthorizationResponseCode deniedpermissions = new AuthorizationResponseCode(25);
    public static final AuthorizationResponseCode deniedtoomanycerts = new AuthorizationResponseCode(26);

    public AuthorizationResponseCode(int i) {
        super(i);
        assertValues();
    }

    public AuthorizationResponseCode(BigInteger bigInteger) {
        super(bigInteger);
        assertValues();
    }

    public AuthorizationResponseCode(byte[] bArr) {
        super(bArr);
        assertValues();
    }

    protected void assertValues() {
        if (getValue().intValue() < 0 || getValue().intValue() > 26) {
            throw new IllegalArgumentException("invalid enumeration value " + getValue());
        }
    }

    private AuthorizationResponseCode(ASN1Enumerated aSN1Enumerated) {
        super(aSN1Enumerated.getValue());
        assertValues();
    }

    public static AuthorizationResponseCode getInstance(Object obj) {
        if (obj instanceof AuthorizationResponseCode) {
            return (AuthorizationResponseCode) obj;
        }
        if (obj != null) {
            return new AuthorizationResponseCode(ASN1Enumerated.getInstance(obj));
        }
        return null;
    }
}
