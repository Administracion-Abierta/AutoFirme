package org.mozilla.javascript;

import com.ctc.wstx.shaded.msv_core.datatype.xsd.XSDatatype;

/* JADX INFO: loaded from: autofirma.jar:org/mozilla/javascript/BaseFunction.class */
public class BaseFunction extends IdScriptableObject implements Function {
    private static final long serialVersionUID = 5311394446546053859L;
    static final String GENERATOR_FUNCTION_CLASS = "__GeneratorFunction";
    private static final int Id_length = 1;
    private static final int Id_arity = 2;
    private static final int Id_name = 3;
    private static final int Id_prototype = 4;
    private static final int Id_arguments = 5;
    private static final int MAX_INSTANCE_ID = 5;
    private static final int Id_constructor = 1;
    private static final int Id_toString = 2;
    private static final int Id_toSource = 3;
    private static final int Id_apply = 4;
    private static final int Id_call = 5;
    private static final int Id_bind = 6;
    private static final int MAX_PROTOTYPE_ID = 6;
    private Object prototypeProperty;
    private Object argumentsObj;
    private boolean isGeneratorFunction;
    private int prototypePropertyAttributes;
    private int argumentsAttributes;
    private static final String FUNCTION_CLASS = "Function";
    private static final Object FUNCTION_TAG = FUNCTION_CLASS;

    static void init(Scriptable scope, boolean sealed) {
        BaseFunction obj = new BaseFunction();
        obj.prototypePropertyAttributes = 7;
        obj.exportAsJSClass(6, scope, sealed);
    }

    static Object initAsGeneratorFunction(Scriptable scope, boolean sealed) {
        BaseFunction obj = new BaseFunction(true);
        obj.prototypePropertyAttributes = 5;
        obj.exportAsJSClass(6, scope, sealed);
        return ScriptableObject.getProperty(scope, GENERATOR_FUNCTION_CLASS);
    }

    public BaseFunction() {
        this.argumentsObj = NOT_FOUND;
        this.isGeneratorFunction = false;
        this.prototypePropertyAttributes = 6;
        this.argumentsAttributes = 6;
    }

    public BaseFunction(boolean isGenerator) {
        this.argumentsObj = NOT_FOUND;
        this.isGeneratorFunction = false;
        this.prototypePropertyAttributes = 6;
        this.argumentsAttributes = 6;
        this.isGeneratorFunction = isGenerator;
    }

    public BaseFunction(Scriptable scope, Scriptable prototype) {
        super(scope, prototype);
        this.argumentsObj = NOT_FOUND;
        this.isGeneratorFunction = false;
        this.prototypePropertyAttributes = 6;
        this.argumentsAttributes = 6;
    }

    @Override // org.mozilla.javascript.ScriptableObject, org.mozilla.javascript.Scriptable
    public String getClassName() {
        return isGeneratorFunction() ? GENERATOR_FUNCTION_CLASS : FUNCTION_CLASS;
    }

    protected boolean isGeneratorFunction() {
        return this.isGeneratorFunction;
    }

    @Override // org.mozilla.javascript.ScriptableObject
    public String getTypeOf() {
        return avoidObjectDetection() ? "undefined" : "function";
    }

    @Override // org.mozilla.javascript.ScriptableObject, org.mozilla.javascript.Scriptable
    public boolean hasInstance(Scriptable instance) {
        Object protoProp = ScriptableObject.getProperty(this, "prototype");
        if (protoProp instanceof Scriptable) {
            return ScriptRuntime.jsDelegatesTo(instance, (Scriptable) protoProp);
        }
        throw ScriptRuntime.typeError1("msg.instanceof.bad.prototype", getFunctionName());
    }

    @Override // org.mozilla.javascript.IdScriptableObject
    protected int getMaxInstanceId() {
        return 5;
    }

    @Override // org.mozilla.javascript.IdScriptableObject
    protected int findInstanceIdInfo(String s) {
        int attr;
        int id = 0;
        String X = null;
        switch (s.length()) {
            case 4:
                X = "name";
                id = 3;
                break;
            case 5:
                X = "arity";
                id = 2;
                break;
            case 6:
                X = XSDatatype.FACET_LENGTH;
                id = 1;
                break;
            case 9:
                int c = s.charAt(0);
                if (c == 97) {
                    X = "arguments";
                    id = 5;
                } else if (c == 112) {
                    X = "prototype";
                    id = 4;
                }
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
                attr = 7;
                break;
            case 4:
                if (!hasPrototypeProperty()) {
                    return 0;
                }
                attr = this.prototypePropertyAttributes;
                break;
                break;
            case 5:
                attr = this.argumentsAttributes;
                break;
            default:
                throw new IllegalStateException();
        }
        return instanceIdInfo(attr, id);
    }

    @Override // org.mozilla.javascript.IdScriptableObject
    protected String getInstanceIdName(int id) {
        switch (id) {
            case 1:
                return XSDatatype.FACET_LENGTH;
            case 2:
                return "arity";
            case 3:
                return "name";
            case 4:
                return "prototype";
            case 5:
                return "arguments";
            default:
                return super.getInstanceIdName(id);
        }
    }

    @Override // org.mozilla.javascript.IdScriptableObject
    protected Object getInstanceIdValue(int id) {
        switch (id) {
            case 1:
                return ScriptRuntime.wrapInt(getLength());
            case 2:
                return ScriptRuntime.wrapInt(getArity());
            case 3:
                return getFunctionName();
            case 4:
                return getPrototypeProperty();
            case 5:
                return getArguments();
            default:
                return super.getInstanceIdValue(id);
        }
    }

    @Override // org.mozilla.javascript.IdScriptableObject
    protected void setInstanceIdValue(int id, Object value) {
        switch (id) {
            case 1:
            case 2:
            case 3:
                break;
            case 4:
                if ((this.prototypePropertyAttributes & 1) == 0) {
                    this.prototypeProperty = value != null ? value : UniqueTag.NULL_VALUE;
                }
                break;
            case 5:
                if (value == NOT_FOUND) {
                    Kit.codeBug();
                }
                if (defaultHas("arguments")) {
                    defaultPut("arguments", value);
                } else if ((this.argumentsAttributes & 1) == 0) {
                    this.argumentsObj = value;
                }
                break;
            default:
                super.setInstanceIdValue(id, value);
                break;
        }
    }

    @Override // org.mozilla.javascript.IdScriptableObject
    protected void setInstanceIdAttributes(int id, int attr) {
        switch (id) {
            case 4:
                this.prototypePropertyAttributes = attr;
                break;
            case 5:
                this.argumentsAttributes = attr;
                break;
            default:
                super.setInstanceIdAttributes(id, attr);
                break;
        }
    }

    @Override // org.mozilla.javascript.IdScriptableObject
    protected void fillConstructorProperties(IdFunctionObject ctor) {
        ctor.setPrototype(this);
        super.fillConstructorProperties(ctor);
    }

    @Override // org.mozilla.javascript.IdScriptableObject
    protected void initPrototypeId(int id) {
        int arity;
        String s;
        switch (id) {
            case 1:
                arity = 1;
                s = "constructor";
                break;
            case 2:
                arity = 0;
                s = "toString";
                break;
            case 3:
                arity = 1;
                s = "toSource";
                break;
            case 4:
                arity = 2;
                s = "apply";
                break;
            case 5:
                arity = 1;
                s = "call";
                break;
            case 6:
                arity = 1;
                s = "bind";
                break;
            default:
                throw new IllegalArgumentException(String.valueOf(id));
        }
        initPrototypeMethod(FUNCTION_TAG, id, s, arity);
    }

    static boolean isApply(IdFunctionObject f) {
        return f.hasTag(FUNCTION_TAG) && f.methodId() == 4;
    }

    static boolean isApplyOrCall(IdFunctionObject f) {
        if (f.hasTag(FUNCTION_TAG)) {
            switch (f.methodId()) {
                case 4:
                case 5:
                    return true;
                default:
                    return false;
            }
        }
        return false;
    }

    @Override // org.mozilla.javascript.IdScriptableObject, org.mozilla.javascript.IdFunctionCall
    public Object execIdCall(IdFunctionObject f, Context cx, Scriptable scope, Scriptable thisObj, Object[] args) {
        Scriptable boundThis;
        Object[] boundArgs;
        if (!f.hasTag(FUNCTION_TAG)) {
            return super.execIdCall(f, cx, scope, thisObj, args);
        }
        int id = f.methodId();
        switch (id) {
            case 1:
                return jsConstructor(cx, scope, args);
            case 2:
                BaseFunction realf = realFunction(thisObj, f);
                int indent = ScriptRuntime.toInt32(args, 0);
                return realf.decompile(indent, 0);
            case 3:
                BaseFunction realf2 = realFunction(thisObj, f);
                int indent2 = 0;
                int flags = 2;
                if (args.length != 0) {
                    indent2 = ScriptRuntime.toInt32(args[0]);
                    if (indent2 >= 0) {
                        flags = 0;
                    } else {
                        indent2 = 0;
                    }
                }
                return realf2.decompile(indent2, flags);
            case 4:
            case 5:
                return ScriptRuntime.applyOrCall(id == 4, cx, scope, thisObj, args);
            case 6:
                if (!(thisObj instanceof Callable)) {
                    throw ScriptRuntime.notFunctionError(thisObj);
                }
                Callable targetFunction = (Callable) thisObj;
                int argc = args.length;
                if (argc > 0) {
                    boundThis = ScriptRuntime.toObjectOrNull(cx, args[0], scope);
                    boundArgs = new Object[argc - 1];
                    System.arraycopy(args, 1, boundArgs, 0, argc - 1);
                } else {
                    boundThis = null;
                    boundArgs = ScriptRuntime.emptyArgs;
                }
                return new BoundFunction(cx, scope, targetFunction, boundThis, boundArgs);
            default:
                throw new IllegalArgumentException(String.valueOf(id));
        }
    }

    private static BaseFunction realFunction(Scriptable thisObj, IdFunctionObject f) {
        Object x = thisObj.getDefaultValue(ScriptRuntime.FunctionClass);
        if (x instanceof Delegator) {
            x = ((Delegator) x).getDelegee();
        }
        if (x instanceof BaseFunction) {
            return (BaseFunction) x;
        }
        throw ScriptRuntime.typeError1("msg.incompat.call", f.getFunctionName());
    }

    public void setImmunePrototypeProperty(Object value) {
        if ((this.prototypePropertyAttributes & 1) != 0) {
            throw new IllegalStateException();
        }
        this.prototypeProperty = value != null ? value : UniqueTag.NULL_VALUE;
        this.prototypePropertyAttributes = 7;
    }

    protected Scriptable getClassPrototype() {
        Object protoVal = getPrototypeProperty();
        if (protoVal instanceof Scriptable) {
            return (Scriptable) protoVal;
        }
        return ScriptableObject.getObjectPrototype(this);
    }

    public Object call(Context cx, Scriptable scope, Scriptable thisObj, Object[] args) {
        return Undefined.instance;
    }

    public Scriptable construct(Context cx, Scriptable scope, Object[] args) {
        Scriptable parent;
        Scriptable proto;
        Scriptable result = createObject(cx, scope);
        if (result != null) {
            Object val = call(cx, scope, result, args);
            if (val instanceof Scriptable) {
                result = (Scriptable) val;
            }
        } else {
            Object val2 = call(cx, scope, null, args);
            if (!(val2 instanceof Scriptable)) {
                throw new IllegalStateException("Bad implementaion of call as constructor, name=" + getFunctionName() + " in " + getClass().getName());
            }
            result = (Scriptable) val2;
            if (result.getPrototype() == null && result != (proto = getClassPrototype())) {
                result.setPrototype(proto);
            }
            if (result.getParentScope() == null && result != (parent = getParentScope())) {
                result.setParentScope(parent);
            }
        }
        return result;
    }

    public Scriptable createObject(Context cx, Scriptable scope) {
        Scriptable newInstance = new NativeObject();
        newInstance.setPrototype(getClassPrototype());
        newInstance.setParentScope(getParentScope());
        return newInstance;
    }

    String decompile(int indent, int flags) {
        StringBuilder sb = new StringBuilder();
        boolean justbody = 0 != (flags & 1);
        if (!justbody) {
            sb.append("function ");
            sb.append(getFunctionName());
            sb.append("() {\n\t");
        }
        sb.append("[native code, arity=");
        sb.append(getArity());
        sb.append("]\n");
        if (!justbody) {
            sb.append("}\n");
        }
        return sb.toString();
    }

    public int getArity() {
        return 0;
    }

    public int getLength() {
        return 0;
    }

    public String getFunctionName() {
        return "";
    }

    protected boolean hasPrototypeProperty() {
        return this.prototypeProperty != null || (this instanceof NativeFunction);
    }

    protected Object getPrototypeProperty() {
        Object result = this.prototypeProperty;
        if (result == null) {
            if (this instanceof NativeFunction) {
                result = setupDefaultPrototype();
            } else {
                result = Undefined.instance;
            }
        } else if (result == UniqueTag.NULL_VALUE) {
            result = null;
        }
        return result;
    }

    private synchronized Object setupDefaultPrototype() {
        if (this.prototypeProperty != null) {
            return this.prototypeProperty;
        }
        NativeObject obj = new NativeObject();
        obj.defineProperty("constructor", this, 2);
        this.prototypeProperty = obj;
        Scriptable proto = getObjectPrototype(this);
        if (proto != obj) {
            obj.setPrototype(proto);
        }
        return obj;
    }

    private Object getArguments() {
        Object value = defaultHas("arguments") ? defaultGet("arguments") : this.argumentsObj;
        if (value != NOT_FOUND) {
            return value;
        }
        Context cx = Context.getContext();
        NativeCall activation = ScriptRuntime.findFunctionActivation(cx, this);
        if (activation == null) {
            return null;
        }
        return activation.get("arguments", activation);
    }

    private Object jsConstructor(Context cx, Scriptable scope, Object[] args) {
        int arglen = args.length;
        StringBuilder sourceBuf = new StringBuilder();
        sourceBuf.append("function ");
        if (isGeneratorFunction()) {
            sourceBuf.append("* ");
        }
        if (cx.getLanguageVersion() != 120) {
            sourceBuf.append("anonymous");
        }
        sourceBuf.append('(');
        for (int i = 0; i < arglen - 1; i++) {
            if (i > 0) {
                sourceBuf.append(',');
            }
            sourceBuf.append(ScriptRuntime.toString(args[i]));
        }
        sourceBuf.append(") {");
        if (arglen != 0) {
            String funBody = ScriptRuntime.toString(args[arglen - 1]);
            sourceBuf.append(funBody);
        }
        sourceBuf.append("\n}");
        String source = sourceBuf.toString();
        int[] linep = new int[1];
        String filename = Context.getSourcePositionFromStack(linep);
        if (filename == null) {
            filename = "<eval'ed string>";
            linep[0] = 1;
        }
        String sourceURI = ScriptRuntime.makeUrlForGeneratedScript(false, filename, linep[0]);
        Scriptable global = ScriptableObject.getTopLevelScope(scope);
        ErrorReporter reporter = DefaultErrorReporter.forEval(cx.getErrorReporter());
        Evaluator evaluator = Context.createInterpreter();
        if (evaluator == null) {
            throw new JavaScriptException("Interpreter not present", filename, linep[0]);
        }
        return cx.compileFunction(global, source, evaluator, reporter, sourceURI, 1, null);
    }

    @Override // org.mozilla.javascript.IdScriptableObject
    protected int findPrototypeId(String s) {
        int id = 0;
        String X = null;
        switch (s.length()) {
            case 4:
                int c = s.charAt(0);
                if (c == 98) {
                    X = "bind";
                    id = 6;
                } else if (c == 99) {
                    X = "call";
                    id = 5;
                }
                break;
            case 5:
                X = "apply";
                id = 4;
                break;
            case 8:
                int c2 = s.charAt(3);
                if (c2 == 111) {
                    X = "toSource";
                    id = 3;
                } else if (c2 == 116) {
                    X = "toString";
                    id = 2;
                }
                break;
            case 11:
                X = "constructor";
                id = 1;
                break;
        }
        if (X != null && X != s && !X.equals(s)) {
            id = 0;
        }
        return id;
    }
}
