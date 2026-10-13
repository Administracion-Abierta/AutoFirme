package nu.xom.jaxen.expr;

import java.io.Serializable;
import java.util.List;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:nu/xom/jaxen/expr/Predicated.class */
public interface Predicated extends Serializable {
    void addPredicate(Predicate predicate);

    List getPredicates();

    PredicateSet getPredicateSet();
}
