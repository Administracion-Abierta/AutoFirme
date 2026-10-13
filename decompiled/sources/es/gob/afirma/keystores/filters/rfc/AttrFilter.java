package es.gob.afirma.keystores.filters.rfc;

import javax.naming.NamingException;
import javax.naming.directory.Attributes;

/* JADX INFO: loaded from: autofirma.jar:es/gob/afirma/keystores/filters/rfc/AttrFilter.class */
interface AttrFilter {
    boolean check(Attributes attributes) throws NamingException;
}
