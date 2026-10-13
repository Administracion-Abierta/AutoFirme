package nu.xom.jaxen;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/* JADX INFO: loaded from: autofirma.jar:nu/xom/jaxen/Context.class */
public class Context implements Serializable {
    private static final long serialVersionUID = 2315979994685591055L;
    private ContextSupport contextSupport;
    private List nodeSet = Collections.EMPTY_LIST;
    private int size = 0;
    private int position = 0;

    public Context(ContextSupport contextSupport) {
        this.contextSupport = contextSupport;
    }

    public void setNodeSet(List list) {
        this.nodeSet = list;
        this.size = list.size();
        if (this.position >= this.size) {
            this.position = 0;
        }
    }

    public List getNodeSet() {
        return this.nodeSet;
    }

    public void setContextSupport(ContextSupport contextSupport) {
        this.contextSupport = contextSupport;
    }

    public ContextSupport getContextSupport() {
        return this.contextSupport;
    }

    public Navigator getNavigator() {
        return getContextSupport().getNavigator();
    }

    public String translateNamespacePrefixToUri(String str) {
        return getContextSupport().translateNamespacePrefixToUri(str);
    }

    public Object getVariableValue(String str, String str2, String str3) throws UnresolvableException {
        return getContextSupport().getVariableValue(str, str2, str3);
    }

    public Function getFunction(String str, String str2, String str3) throws UnresolvableException {
        return getContextSupport().getFunction(str, str2, str3);
    }

    public void setSize(int i) {
        this.size = i;
    }

    public int getSize() {
        return this.size;
    }

    public void setPosition(int i) {
        this.position = i;
    }

    public int getPosition() {
        return this.position;
    }

    public Context duplicate() {
        Context context = new Context(getContextSupport());
        List nodeSet = getNodeSet();
        if (nodeSet != null) {
            ArrayList arrayList = new ArrayList(nodeSet.size());
            arrayList.addAll(nodeSet);
            context.setNodeSet(arrayList);
            context.setPosition(this.position);
        }
        return context;
    }
}
