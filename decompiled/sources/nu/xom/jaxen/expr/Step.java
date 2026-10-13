package nu.xom.jaxen.expr;

import java.util.Iterator;
import java.util.List;
import nu.xom.jaxen.Context;
import nu.xom.jaxen.ContextSupport;
import nu.xom.jaxen.JaxenException;
import nu.xom.jaxen.UnsupportedAxisException;

/* JADX INFO: loaded from: autofirma.jar:nu/xom/jaxen/expr/Step.class */
public interface Step extends Predicated {
    boolean matches(Object obj, ContextSupport contextSupport) throws JaxenException;

    String getText();

    void simplify();

    int getAxis();

    Iterator axisIterator(Object obj, ContextSupport contextSupport) throws UnsupportedAxisException;

    List evaluate(Context context) throws JaxenException;
}
