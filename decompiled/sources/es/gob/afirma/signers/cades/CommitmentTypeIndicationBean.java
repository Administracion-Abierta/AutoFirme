package es.gob.afirma.signers.cades;

import java.util.ArrayList;
import java.util.List;

/* JADX INFO: loaded from: autofirma.jar:es/gob/afirma/signers/cades/CommitmentTypeIndicationBean.class */
public final class CommitmentTypeIndicationBean {
    private final String identifier;
    private final List<String> qualifiers;

    CommitmentTypeIndicationBean(String str, List<String> list) {
        if (str == null) {
            throw new IllegalArgumentException("El obligatorio proporcionar un identificador no nulo");
        }
        this.identifier = str;
        this.qualifiers = list != null ? new ArrayList(list) : null;
    }

    String getIdentifier() {
        return this.identifier;
    }

    List<String> getQualifiers() {
        if (this.qualifiers == null) {
            return new ArrayList(0);
        }
        return new ArrayList(this.qualifiers);
    }
}
