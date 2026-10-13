package nu.xom.jaxen.expr;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import nu.xom.jaxen.Context;
import nu.xom.jaxen.ContextSupport;
import nu.xom.jaxen.JaxenException;
import nu.xom.jaxen.function.BooleanFunction;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:nu/xom/jaxen/expr/PredicateSet.class */
public class PredicateSet implements Serializable {
    private static final long serialVersionUID = -7166491740228977853L;
    private List predicates = Collections.EMPTY_LIST;

    public void addPredicate(Predicate predicate) {
        if (this.predicates == Collections.EMPTY_LIST) {
            this.predicates = new ArrayList();
        }
        this.predicates.add(predicate);
    }

    public List getPredicates() {
        return this.predicates;
    }

    public void simplify() {
        Iterator it = this.predicates.iterator();
        while (it.hasNext()) {
            ((Predicate) it.next()).simplify();
        }
    }

    public String getText() {
        StringBuffer stringBuffer = new StringBuffer();
        Iterator it = this.predicates.iterator();
        while (it.hasNext()) {
            stringBuffer.append(((Predicate) it.next()).getText());
        }
        return stringBuffer.toString();
    }

    protected boolean evaluateAsBoolean(List list, ContextSupport contextSupport) throws JaxenException {
        return anyMatchingNode(list, contextSupport);
    }

    private boolean anyMatchingNode(List list, ContextSupport contextSupport) throws JaxenException {
        if (this.predicates.size() == 0) {
            return false;
        }
        Iterator it = this.predicates.iterator();
        while (it.hasNext()) {
            int size = list.size();
            Context context = new Context(contextSupport);
            ArrayList arrayList = new ArrayList(1);
            context.setNodeSet(arrayList);
            for (int i = 0; i < size; i++) {
                Object obj = list.get(i);
                arrayList.clear();
                arrayList.add(obj);
                context.setNodeSet(arrayList);
                context.setPosition(i + 1);
                context.setSize(size);
                Object objEvaluate = ((Predicate) it.next()).evaluate(context);
                if (objEvaluate instanceof Number) {
                    if (((Number) objEvaluate).intValue() == i + 1) {
                        return true;
                    }
                } else if (BooleanFunction.evaluate(objEvaluate, context.getNavigator()).booleanValue()) {
                    return true;
                }
            }
        }
        return false;
    }

    protected List evaluatePredicates(List list, ContextSupport contextSupport) throws JaxenException {
        if (this.predicates.size() == 0) {
            return list;
        }
        Iterator it = this.predicates.iterator();
        List listApplyPredicate = list;
        while (true) {
            List list2 = listApplyPredicate;
            if (!it.hasNext()) {
                return list2;
            }
            listApplyPredicate = applyPredicate((Predicate) it.next(), list2, contextSupport);
        }
    }

    public List applyPredicate(Predicate predicate, List list, ContextSupport contextSupport) throws JaxenException {
        int size = list.size();
        ArrayList arrayList = new ArrayList(size);
        Context context = new Context(contextSupport);
        ArrayList arrayList2 = new ArrayList(1);
        context.setNodeSet(arrayList2);
        for (int i = 0; i < size; i++) {
            Object obj = list.get(i);
            arrayList2.clear();
            arrayList2.add(obj);
            context.setNodeSet(arrayList2);
            context.setPosition(i + 1);
            context.setSize(size);
            Object objEvaluate = predicate.evaluate(context);
            if (objEvaluate instanceof Number) {
                if (((Number) objEvaluate).intValue() == i + 1) {
                    arrayList.add(obj);
                }
            } else if (BooleanFunction.evaluate(objEvaluate, context.getNavigator()).booleanValue()) {
                arrayList.add(obj);
            }
        }
        return arrayList;
    }
}
