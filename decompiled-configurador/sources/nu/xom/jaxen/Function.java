package nu.xom.jaxen;

import java.util.List;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:nu/xom/jaxen/Function.class */
public interface Function {
    Object call(Context context, List list) throws FunctionCallException;
}
