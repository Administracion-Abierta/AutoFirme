package org.mozilla.javascript.xmlimpl;

import org.mozilla.javascript.Context;
import org.mozilla.javascript.IdFunctionObject;
import org.mozilla.javascript.IdScriptableObject;
import org.mozilla.javascript.ScriptRuntime;
import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.Undefined;

/* JADX INFO: loaded from: autofirma.jar:org/mozilla/javascript/xmlimpl/Namespace.class */
class Namespace extends IdScriptableObject {
    static final long serialVersionUID = -5765755238131301744L;
    private static final Object NAMESPACE_TAG = "Namespace";
    private Namespace prototype;
    private XmlNode.Namespace ns;
    private static final int Id_prefix = 1;
    private static final int Id_uri = 2;
    private static final int MAX_INSTANCE_ID = 2;
    private static final int Id_constructor = 1;
    private static final int Id_toString = 2;
    private static final int Id_toSource = 3;
    private static final int MAX_PROTOTYPE_ID = 3;

    private Namespace() {
    }

    static Namespace create(Scriptable scope, Namespace prototype, XmlNode.Namespace namespace) {
        Namespace rv = new Namespace();
        rv.setParentScope(scope);
        rv.prototype = prototype;
        rv.setPrototype(prototype);
        rv.ns = namespace;
        return rv;
    }

    final XmlNode.Namespace getDelegate() {
        return this.ns;
    }

    public void exportAsJSClass(boolean sealed) {
        exportAsJSClass(3, getParentScope(), sealed);
    }

    public String uri() {
        return this.ns.getUri();
    }

    public String prefix() {
        return this.ns.getPrefix();
    }

    public String toString() {
        return uri();
    }

    public String toLocaleString() {
        return toString();
    }

    private boolean equals(Namespace n) {
        return uri().equals(n.uri());
    }

    public boolean equals(Object obj) {
        if (obj instanceof Namespace) {
            return equals((Namespace) obj);
        }
        return false;
    }

    public int hashCode() {
        return uri().hashCode();
    }

    @Override // org.mozilla.javascript.ScriptableObject
    protected Object equivalentValues(Object value) {
        if (!(value instanceof Namespace)) {
            return Scriptable.NOT_FOUND;
        }
        boolean result = equals((Namespace) value);
        return result ? Boolean.TRUE : Boolean.FALSE;
    }

    @Override // org.mozilla.javascript.ScriptableObject, org.mozilla.javascript.Scriptable
    public String getClassName() {
        return "Namespace";
    }

    @Override // org.mozilla.javascript.ScriptableObject, org.mozilla.javascript.Scriptable
    public Object getDefaultValue(Class<?> hint) {
        return uri();
    }

    @Override // org.mozilla.javascript.IdScriptableObject
    protected int getMaxInstanceId() {
        return super.getMaxInstanceId() + 2;
    }

    @Override // org.mozilla.javascript.IdScriptableObject
    protected int findInstanceIdInfo(String s) {
        int id = 0;
        String X = null;
        int s_length = s.length();
        if (s_length == 3) {
            X = "uri";
            id = 2;
        } else if (s_length == 6) {
            X = "prefix";
            id = 1;
        }
        if (X != null && X != s && !X.equals(s)) {
            id = 0;
        }
        if (id == 0) {
            return super.findInstanceIdInfo(s);
        }
        switch (id) {
            case 1:
            case 2:
                return instanceIdInfo(5, super.getMaxInstanceId() + id);
            default:
                throw new IllegalStateException();
        }
    }

    @Override // org.mozilla.javascript.IdScriptableObject
    protected String getInstanceIdName(int id) {
        switch (id - super.getMaxInstanceId()) {
            case 1:
                return "prefix";
            case 2:
                return "uri";
            default:
                return super.getInstanceIdName(id);
        }
    }

    @Override // org.mozilla.javascript.IdScriptableObject
    protected Object getInstanceIdValue(int id) {
        switch (id - super.getMaxInstanceId()) {
            case 1:
                return this.ns.getPrefix() == null ? Undefined.instance : this.ns.getPrefix();
            case 2:
                return this.ns.getUri();
            default:
                return super.getInstanceIdValue(id);
        }
    }

    @Override // org.mozilla.javascript.IdScriptableObject
    protected int findPrototypeId(String s) {
        int id = 0;
        String X = null;
        int s_length = s.length();
        if (s_length == 8) {
            int c = s.charAt(3);
            if (c == 111) {
                X = "toSource";
                id = 3;
            } else if (c == 116) {
                X = "toString";
                id = 2;
            }
        } else if (s_length == 11) {
            X = "constructor";
            id = 1;
        }
        if (X != null && X != s && !X.equals(s)) {
            id = 0;
        }
        return id;
    }

    @Override // org.mozilla.javascript.IdScriptableObject
    protected void initPrototypeId(int id) {
        int arity;
        String s;
        switch (id) {
            case 1:
                arity = 2;
                s = "constructor";
                break;
            case 2:
                arity = 0;
                s = "toString";
                break;
            case 3:
                arity = 0;
                s = "toSource";
                break;
            default:
                throw new IllegalArgumentException(String.valueOf(id));
        }
        initPrototypeMethod(NAMESPACE_TAG, id, s, arity);
    }

    @Override // org.mozilla.javascript.IdScriptableObject, org.mozilla.javascript.IdFunctionCall
    public Object execIdCall(IdFunctionObject f, Context cx, Scriptable scope, Scriptable thisObj, Object[] args) {
        if (!f.hasTag(NAMESPACE_TAG)) {
            return super.execIdCall(f, cx, scope, thisObj, args);
        }
        int id = f.methodId();
        switch (id) {
            case 1:
                return jsConstructor(cx, thisObj == null, args);
            case 2:
                return realThis(thisObj, f).toString();
            case 3:
                return realThis(thisObj, f).js_toSource();
            default:
                throw new IllegalArgumentException(String.valueOf(id));
        }
    }

    private Namespace realThis(Scriptable thisObj, IdFunctionObject f) {
        if (!(thisObj instanceof Namespace)) {
            throw incompatibleCallError(f);
        }
        return (Namespace) thisObj;
    }

    Namespace newNamespace(String uri) {
        Namespace prototype = this.prototype == null ? this : this.prototype;
        return create(getParentScope(), prototype, XmlNode.Namespace.create(uri));
    }

    Namespace newNamespace(String prefix, String uri) {
        if (prefix == null) {
            return newNamespace(uri);
        }
        Namespace prototype = this.prototype == null ? this : this.prototype;
        return create(getParentScope(), prototype, XmlNode.Namespace.create(prefix, uri));
    }

    Namespace constructNamespace(Object uriValue) {
        String uri;
        String prefix;
        if (uriValue instanceof Namespace) {
            Namespace ns = (Namespace) uriValue;
            prefix = ns.prefix();
            uri = ns.uri();
        } else if (uriValue instanceof QName) {
            QName qname = (QName) uriValue;
            uri = qname.uri();
            if (uri != null) {
                prefix = qname.prefix();
            } else {
                uri = qname.toString();
                prefix = null;
            }
        } else {
            uri = ScriptRuntime.toString(uriValue);
            prefix = uri.length() == 0 ? "" : null;
        }
        return newNamespace(prefix, uri);
    }

    Namespace castToNamespace(Object namespaceObj) {
        if (namespaceObj instanceof Namespace) {
            return (Namespace) namespaceObj;
        }
        return constructNamespace(namespaceObj);
    }

    private Namespace constructNamespace(Object prefixValue, Object uriValue) {
        String uri;
        String prefix;
        if (uriValue instanceof QName) {
            QName qname = (QName) uriValue;
            uri = qname.uri();
            if (uri == null) {
                uri = qname.toString();
            }
        } else {
            uri = ScriptRuntime.toString(uriValue);
        }
        if (uri.length() == 0) {
            if (prefixValue == Undefined.instance) {
                prefix = "";
            } else {
                prefix = ScriptRuntime.toString(prefixValue);
                if (prefix.length() != 0) {
                    throw ScriptRuntime.typeError("Illegal prefix '" + prefix + "' for 'no namespace'.");
                }
            }
        } else if (prefixValue == Undefined.instance || !XMLName.accept(prefixValue)) {
            prefix = "";
        } else {
            prefix = ScriptRuntime.toString(prefixValue);
        }
        return newNamespace(prefix, uri);
    }

    private Namespace constructNamespace() {
        return newNamespace("", "");
    }

    private Object jsConstructor(Context cx, boolean inNewExpr, Object[] args) {
        if (!inNewExpr && args.length == 1) {
            return castToNamespace(args[0]);
        }
        if (args.length == 0) {
            return constructNamespace();
        }
        if (args.length == 1) {
            return constructNamespace(args[0]);
        }
        return constructNamespace(args[0], args[1]);
    }

    private String js_toSource() {
        StringBuilder sb = new StringBuilder();
        sb.append('(');
        toSourceImpl(this.ns.getPrefix(), this.ns.getUri(), sb);
        sb.append(')');
        return sb.toString();
    }

    static void toSourceImpl(String prefix, String uri, StringBuilder sb) {
        sb.append("new Namespace(");
        if (uri.length() == 0) {
            if (!"".equals(prefix)) {
                throw new IllegalArgumentException(prefix);
            }
        } else {
            sb.append('\'');
            if (prefix != null) {
                sb.append(ScriptRuntime.escapeString(prefix, '\''));
                sb.append("', '");
            }
            sb.append(ScriptRuntime.escapeString(uri, '\''));
            sb.append('\'');
        }
        sb.append(')');
    }
}
