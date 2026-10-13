package nu.xom.jaxen.expr;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import nu.xom.jaxen.Context;
import nu.xom.jaxen.ContextSupport;
import nu.xom.jaxen.JaxenException;
import nu.xom.jaxen.UnsupportedAxisException;
import nu.xom.jaxen.expr.iter.IterableAxis;
import nu.xom.jaxen.saxpath.Axis;

/* JADX INFO: loaded from: autofirma.jar:nu/xom/jaxen/expr/DefaultStep.class */
public abstract class DefaultStep implements Step {
    private IterableAxis axis;
    private PredicateSet predicates;

    public DefaultStep(IterableAxis iterableAxis, PredicateSet predicateSet) {
        this.axis = iterableAxis;
        this.predicates = predicateSet;
    }

    @Override // nu.xom.jaxen.expr.Predicated
    public void addPredicate(Predicate predicate) {
        this.predicates.addPredicate(predicate);
    }

    @Override // nu.xom.jaxen.expr.Predicated
    public List getPredicates() {
        return this.predicates.getPredicates();
    }

    @Override // nu.xom.jaxen.expr.Predicated
    public PredicateSet getPredicateSet() {
        return this.predicates;
    }

    @Override // nu.xom.jaxen.expr.Step
    public int getAxis() {
        return this.axis.value();
    }

    public IterableAxis getIterableAxis() {
        return this.axis;
    }

    public String getAxisName() {
        return Axis.lookup(getAxis());
    }

    @Override // nu.xom.jaxen.expr.Step
    public String getText() {
        return this.predicates.getText();
    }

    public String toString() {
        return new StringBuffer().append(getIterableAxis()).append(" ").append(super.toString()).toString();
    }

    @Override // nu.xom.jaxen.expr.Step
    public void simplify() {
        this.predicates.simplify();
    }

    @Override // nu.xom.jaxen.expr.Step
    public Iterator axisIterator(Object obj, ContextSupport contextSupport) throws UnsupportedAxisException {
        return getIterableAxis().iterator(obj, contextSupport);
    }

    @Override // nu.xom.jaxen.expr.Step
    public List evaluate(Context context) throws JaxenException {
        List nodeSet = context.getNodeSet();
        IdentitySet identitySet = new IdentitySet();
        int size = nodeSet.size();
        ArrayList arrayList = new ArrayList();
        ArrayList arrayList2 = new ArrayList();
        ContextSupport contextSupport = context.getContextSupport();
        for (int i = 0; i < size; i++) {
            Iterator it = this.axis.iterator(nodeSet.get(i), contextSupport);
            while (it.hasNext()) {
                Object next = it.next();
                if (!identitySet.contains(next) && matches(next, contextSupport)) {
                    identitySet.add(next);
                    arrayList.add(next);
                }
            }
            arrayList2.addAll(getPredicateSet().evaluatePredicates(arrayList, contextSupport));
            arrayList.clear();
        }
        return arrayList2;
    }
}
