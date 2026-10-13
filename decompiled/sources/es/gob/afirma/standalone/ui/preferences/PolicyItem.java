package es.gob.afirma.standalone.ui.preferences;

import es.gob.afirma.core.signers.AdESPolicy;

/* JADX INFO: loaded from: autofirma.jar:es/gob/afirma/standalone/ui/preferences/PolicyItem.class */
final class PolicyItem {
    private final String name;
    private AdESPolicy policy;

    PolicyItem(String str, AdESPolicy adESPolicy) {
        this.name = str;
        this.policy = adESPolicy;
    }

    public AdESPolicy getPolicy() {
        return this.policy;
    }

    void setPolicy(AdESPolicy adESPolicy) {
        this.policy = adESPolicy;
    }

    public boolean equals(Object obj) {
        if (obj instanceof AdESPolicy) {
            return this.policy != null && this.policy.equals(obj);
        }
        if (obj instanceof PolicyItem) {
            PolicyItem policyItem = (PolicyItem) obj;
            return this.name.equals(policyItem.name) && ((this.policy == null && policyItem.getPolicy() == null) || this.policy.equals(policyItem.getPolicy()));
        }
        return false;
    }

    public String toString() {
        return this.name;
    }

    public int hashCode() {
        return super.hashCode();
    }

    public String getName() {
        return this.name;
    }
}
