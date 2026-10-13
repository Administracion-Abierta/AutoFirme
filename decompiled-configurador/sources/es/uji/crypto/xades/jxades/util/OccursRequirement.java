package es.uji.crypto.xades.jxades.util;

import java.util.Collection;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:es/uji/crypto/xades/jxades/util/OccursRequirement.class */
public final class OccursRequirement extends ObjectId {
    public static final OccursRequirement EXACTLY_ONE = new OccursRequirement(1, 1);
    public static final OccursRequirement ZERO_OR_ONE = new OccursRequirement(0, 1);
    public static final OccursRequirement ONE_OR_MORE = new OccursRequirement(1);
    public static final OccursRequirement ZERO_OR_MORE = new OccursRequirement(0);

    public OccursRequirement(int minOccurs) {
        this(minOccurs, Integer.MAX_VALUE);
    }

    public OccursRequirement(int minOccurs, int maxOccurs) {
        super(new int[]{minOccurs, maxOccurs});
    }

    public final int getMinOccurs() {
        return this.components[0];
    }

    public final int getMaxOccurs() {
        return this.components[1];
    }

    public final boolean isValid(Object object) {
        if (object == null) {
            return this.components[0] == 0;
        }
        if (object instanceof Number) {
            return isValid(((Number) object).intValue());
        }
        if (object instanceof Collection) {
            return isValid(((Collection) object).size());
        }
        if (object instanceof Object[]) {
            return isValid(((Object[]) object).length);
        }
        return isValid(1);
    }

    public final boolean isValid(int count) {
        return count >= this.components[0] && count <= this.components[1];
    }
}
