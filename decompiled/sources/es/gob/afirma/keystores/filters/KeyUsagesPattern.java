package es.gob.afirma.keystores.filters;

import java.security.Principal;
import java.util.HashMap;
import java.util.Map;

/* JADX INFO: loaded from: autofirma.jar:es/gob/afirma/keystores/filters/KeyUsagesPattern.class */
public class KeyUsagesPattern {
    private static final Boolean[] DEFAULT_SIGNATURE_KEYUSAGES = {null, Boolean.TRUE, null, null, null, null, null, null};
    private static final Map<String, Boolean[]> SIGNATURE_KEYUSAGES = new HashMap();
    private final Principal issuer;

    static {
        SIGNATURE_KEYUSAGES.put("C=ES, O=Generalitat Valenciana, OU=PKIGVA, CN=ACCV-CA2", new Boolean[]{Boolean.TRUE, Boolean.FALSE, Boolean.FALSE, Boolean.FALSE, Boolean.FALSE, Boolean.FALSE, Boolean.FALSE, Boolean.FALSE});
    }

    public KeyUsagesPattern(Principal principal) {
        this.issuer = principal;
    }

    public Boolean[] getSignaturePattern() {
        if (SIGNATURE_KEYUSAGES.containsKey(this.issuer.toString())) {
            return SIGNATURE_KEYUSAGES.get(this.issuer.toString());
        }
        return (Boolean[]) DEFAULT_SIGNATURE_KEYUSAGES.clone();
    }
}
