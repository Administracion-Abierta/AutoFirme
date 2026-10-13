package org.mozilla.javascript.xmlimpl;

import org.mozilla.javascript.Context;
import org.mozilla.javascript.IdFunctionObject;
import org.mozilla.javascript.ScriptRuntime;
import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.ScriptableObject;
import org.mozilla.javascript.Undefined;

/* JADX INFO: loaded from: autofirma.jar:org/mozilla/javascript/xmlimpl/XMLCtor.class */
class XMLCtor extends IdFunctionObject {
    static final long serialVersionUID = -8708195078359817341L;
    private static final Object XMLCTOR_TAG = "XMLCtor";
    private XmlProcessor options;
    private static final int Id_ignoreComments = 1;
    private static final int Id_ignoreProcessingInstructions = 2;
    private static final int Id_ignoreWhitespace = 3;
    private static final int Id_prettyIndent = 4;
    private static final int Id_prettyPrinting = 5;
    private static final int MAX_INSTANCE_ID = 5;
    private static final int Id_defaultSettings = 1;
    private static final int Id_settings = 2;
    private static final int Id_setSettings = 3;
    private static final int MAX_FUNCTION_ID = 3;

    XMLCtor(XML xml, Object tag, int id, int arity) {
        super(xml, tag, id, arity);
        this.options = xml.getProcessor();
        activatePrototypeMap(3);
    }

    private void writeSetting(Scriptable target) {
        for (int i = 1; i <= 5; i++) {
            int id = super.getMaxInstanceId() + i;
            String name = getInstanceIdName(id);
            Object value = getInstanceIdValue(id);
            ScriptableObject.putProperty(target, name, value);
        }
    }

    /* JADX WARN: Code duplicated, block: B:18:0x006a  */
    private void readSettings(Scriptable source) {
        for (int i = 1; i <= 5; i++) {
            int id = super.getMaxInstanceId() + i;
            String name = getInstanceIdName(id);
            Object value = ScriptableObject.getProperty(source, name);
            if (value != Scriptable.NOT_FOUND) {
                switch (i) {
                    case 1:
                    case 2:
                    case 3:
                    case 5:
                        if (value instanceof Boolean) {
                            setInstanceIdValue(id, value);
                        }
                        break;
                    case 4:
                        if (value instanceof Number) {
                            setInstanceIdValue(id, value);
                        }
                        break;
                    default:
                        throw new IllegalStateException();
                }
            }
        }
    }

    @Override // org.mozilla.javascript.BaseFunction, org.mozilla.javascript.IdScriptableObject
    protected int getMaxInstanceId() {
        return super.getMaxInstanceId() + 5;
    }

    @Override // org.mozilla.javascript.BaseFunction, org.mozilla.javascript.IdScriptableObject
    protected int findInstanceIdInfo(String s) {
        int id = 0;
        String X = null;
        switch (s.length()) {
            case 12:
                X = "prettyIndent";
                id = 4;
                break;
            case 14:
                int c = s.charAt(0);
                if (c == 105) {
                    X = "ignoreComments";
                    id = 1;
                } else if (c == 112) {
                    X = "prettyPrinting";
                    id = 5;
                }
                break;
            case 16:
                X = "ignoreWhitespace";
                id = 3;
                break;
            case 28:
                X = "ignoreProcessingInstructions";
                id = 2;
                break;
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
            case 3:
            case 4:
            case 5:
                return instanceIdInfo(6, super.getMaxInstanceId() + id);
            default:
                throw new IllegalStateException();
        }
    }

    @Override // org.mozilla.javascript.BaseFunction, org.mozilla.javascript.IdScriptableObject
    protected String getInstanceIdName(int id) {
        switch (id - super.getMaxInstanceId()) {
            case 1:
                return "ignoreComments";
            case 2:
                return "ignoreProcessingInstructions";
            case 3:
                return "ignoreWhitespace";
            case 4:
                return "prettyIndent";
            case 5:
                return "prettyPrinting";
            default:
                return super.getInstanceIdName(id);
        }
    }

    @Override // org.mozilla.javascript.BaseFunction, org.mozilla.javascript.IdScriptableObject
    protected Object getInstanceIdValue(int id) {
        switch (id - super.getMaxInstanceId()) {
            case 1:
                return ScriptRuntime.wrapBoolean(this.options.isIgnoreComments());
            case 2:
                return ScriptRuntime.wrapBoolean(this.options.isIgnoreProcessingInstructions());
            case 3:
                return ScriptRuntime.wrapBoolean(this.options.isIgnoreWhitespace());
            case 4:
                return ScriptRuntime.wrapInt(this.options.getPrettyIndent());
            case 5:
                return ScriptRuntime.wrapBoolean(this.options.isPrettyPrinting());
            default:
                return super.getInstanceIdValue(id);
        }
    }

    @Override // org.mozilla.javascript.BaseFunction, org.mozilla.javascript.IdScriptableObject
    protected void setInstanceIdValue(int id, Object value) {
        switch (id - super.getMaxInstanceId()) {
            case 1:
                this.options.setIgnoreComments(ScriptRuntime.toBoolean(value));
                break;
            case 2:
                this.options.setIgnoreProcessingInstructions(ScriptRuntime.toBoolean(value));
                break;
            case 3:
                this.options.setIgnoreWhitespace(ScriptRuntime.toBoolean(value));
                break;
            case 4:
                this.options.setPrettyIndent(ScriptRuntime.toInt32(value));
                break;
            case 5:
                this.options.setPrettyPrinting(ScriptRuntime.toBoolean(value));
                break;
            default:
                super.setInstanceIdValue(id, value);
                break;
        }
    }

    @Override // org.mozilla.javascript.BaseFunction, org.mozilla.javascript.IdScriptableObject
    protected int findPrototypeId(String s) {
        int id = 0;
        String X = null;
        int s_length = s.length();
        if (s_length == 8) {
            X = "settings";
            id = 2;
        } else if (s_length == 11) {
            X = "setSettings";
            id = 3;
        } else if (s_length == 15) {
            X = "defaultSettings";
            id = 1;
        }
        if (X != null && X != s && !X.equals(s)) {
            id = 0;
        }
        return id;
    }

    @Override // org.mozilla.javascript.BaseFunction, org.mozilla.javascript.IdScriptableObject
    protected void initPrototypeId(int id) {
        int arity;
        String s;
        switch (id) {
            case 1:
                arity = 0;
                s = "defaultSettings";
                break;
            case 2:
                arity = 0;
                s = "settings";
                break;
            case 3:
                arity = 1;
                s = "setSettings";
                break;
            default:
                throw new IllegalArgumentException(String.valueOf(id));
        }
        initPrototypeMethod(XMLCTOR_TAG, id, s, arity);
    }

    @Override // org.mozilla.javascript.BaseFunction, org.mozilla.javascript.IdScriptableObject, org.mozilla.javascript.IdFunctionCall
    public Object execIdCall(IdFunctionObject f, Context cx, Scriptable scope, Scriptable thisObj, Object[] args) {
        if (!f.hasTag(XMLCTOR_TAG)) {
            return super.execIdCall(f, cx, scope, thisObj, args);
        }
        int id = f.methodId();
        switch (id) {
            case 1:
                this.options.setDefault();
                Scriptable obj = cx.newObject(scope);
                writeSetting(obj);
                return obj;
            case 2:
                Scriptable obj2 = cx.newObject(scope);
                writeSetting(obj2);
                return obj2;
            case 3:
                if (args.length == 0 || args[0] == null || args[0] == Undefined.instance) {
                    this.options.setDefault();
                } else if (args[0] instanceof Scriptable) {
                    readSettings((Scriptable) args[0]);
                }
                return Undefined.instance;
            default:
                throw new IllegalArgumentException(String.valueOf(id));
        }
    }

    @Override // org.mozilla.javascript.BaseFunction, org.mozilla.javascript.ScriptableObject, org.mozilla.javascript.Scriptable
    public boolean hasInstance(Scriptable instance) {
        return (instance instanceof XML) || (instance instanceof XMLList);
    }
}
