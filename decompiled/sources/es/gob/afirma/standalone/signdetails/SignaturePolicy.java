package es.gob.afirma.standalone.signdetails;

import es.gob.afirma.core.signers.AdESPolicy;

/* JADX INFO: loaded from: autofirma.jar:es/gob/afirma/standalone/signdetails/SignaturePolicy.class */
public class SignaturePolicy {
    String name;
    AdESPolicy policy;

    public SignaturePolicy(String str, AdESPolicy adESPolicy) {
        this.name = str;
        this.policy = adESPolicy;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String str) {
        this.name = str;
    }

    public AdESPolicy getPolicy() {
        return this.policy;
    }

    public void setPolicy(AdESPolicy adESPolicy) {
        this.policy = adESPolicy;
    }
}
