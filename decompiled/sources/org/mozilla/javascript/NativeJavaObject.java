package org.mozilla.javascript;

import com.aowagie.text.pdf.ColumnText;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.lang.reflect.Array;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Date;
import java.util.Map;

/* JADX INFO: loaded from: autofirma.jar:org/mozilla/javascript/NativeJavaObject.class */
public class NativeJavaObject implements Scriptable, SymbolScriptable, Wrapper, Serializable {
    private static final long serialVersionUID = -6948590651130498591L;
    private static final int JSTYPE_UNDEFINED = 0;
    private static final int JSTYPE_NULL = 1;
    private static final int JSTYPE_BOOLEAN = 2;
    private static final int JSTYPE_NUMBER = 3;
    private static final int JSTYPE_STRING = 4;
    private static final int JSTYPE_JAVA_CLASS = 5;
    private static final int JSTYPE_JAVA_OBJECT = 6;
    private static final int JSTYPE_JAVA_ARRAY = 7;
    private static final int JSTYPE_OBJECT = 8;
    static final byte CONVERSION_TRIVIAL = 1;
    static final byte CONVERSION_NONTRIVIAL = 0;
    static final byte CONVERSION_NONE = 99;
    protected Scriptable prototype;
    protected Scriptable parent;
    protected transient Object javaObject;
    protected transient Class<?> staticType;
    protected transient JavaMembers members;
    private transient Map<String, FieldAndMethods> fieldAndMethods;
    protected transient boolean isAdapter;
    private static final Object COERCED_INTERFACE_KEY = "Coerced Interface";
    private static Method adapter_writeAdapterObject;
    private static Method adapter_readAdapterObject;

    public NativeJavaObject() {
    }

    public NativeJavaObject(Scriptable scope, Object javaObject, Class<?> staticType) {
        this(scope, javaObject, staticType, false);
    }

    public NativeJavaObject(Scriptable scope, Object javaObject, Class<?> staticType, boolean isAdapter) {
        this.parent = scope;
        this.javaObject = javaObject;
        this.staticType = staticType;
        this.isAdapter = isAdapter;
        initMembers();
    }

    protected void initMembers() {
        Class<?> dynamicType;
        if (this.javaObject != null) {
            dynamicType = this.javaObject.getClass();
        } else {
            dynamicType = this.staticType;
        }
        this.members = JavaMembers.lookupClass(this.parent, dynamicType, this.staticType, this.isAdapter);
        this.fieldAndMethods = this.members.getFieldAndMethodsObjects(this, this.javaObject, false);
    }

    @Override // org.mozilla.javascript.Scriptable
    public boolean has(String name, Scriptable start) {
        return this.members.has(name, false);
    }

    @Override // org.mozilla.javascript.Scriptable
    public boolean has(int index, Scriptable start) {
        return false;
    }

    public boolean has(Symbol key, Scriptable start) {
        return false;
    }

    @Override // org.mozilla.javascript.Scriptable
    public Object get(String name, Scriptable start) {
        Object result;
        if (this.fieldAndMethods != null && (result = this.fieldAndMethods.get(name)) != null) {
            return result;
        }
        return this.members.get(this, name, this.javaObject, false);
    }

    public Object get(Symbol key, Scriptable start) {
        return Scriptable.NOT_FOUND;
    }

    @Override // org.mozilla.javascript.Scriptable
    public Object get(int index, Scriptable start) {
        throw this.members.reportMemberNotFound(Integer.toString(index));
    }

    @Override // org.mozilla.javascript.Scriptable
    public void put(String name, Scriptable start, Object value) {
        if (this.prototype == null || this.members.has(name, false)) {
            this.members.put(this, name, this.javaObject, value, false);
        } else {
            this.prototype.put(name, this.prototype, value);
        }
    }

    @Override // org.mozilla.javascript.SymbolScriptable
    public void put(Symbol symbol, Scriptable start, Object value) {
        String name = symbol.toString();
        if (this.prototype == null || this.members.has(name, false)) {
            this.members.put(this, name, this.javaObject, value, false);
        } else if (this.prototype instanceof SymbolScriptable) {
            ((SymbolScriptable) this.prototype).put(symbol, this.prototype, value);
        }
    }

    @Override // org.mozilla.javascript.Scriptable
    public void put(int index, Scriptable start, Object value) {
        throw this.members.reportMemberNotFound(Integer.toString(index));
    }

    @Override // org.mozilla.javascript.Scriptable
    public boolean hasInstance(Scriptable value) {
        return false;
    }

    @Override // org.mozilla.javascript.Scriptable
    public void delete(String name) {
    }

    public void delete(Symbol key) {
    }

    @Override // org.mozilla.javascript.Scriptable
    public void delete(int index) {
    }

    @Override // org.mozilla.javascript.Scriptable
    public Scriptable getPrototype() {
        if (this.prototype == null && (this.javaObject instanceof String)) {
            return TopLevel.getBuiltinPrototype(ScriptableObject.getTopLevelScope(this.parent), TopLevel.Builtins.String);
        }
        return this.prototype;
    }

    @Override // org.mozilla.javascript.Scriptable
    public void setPrototype(Scriptable m) {
        this.prototype = m;
    }

    @Override // org.mozilla.javascript.Scriptable
    public Scriptable getParentScope() {
        return this.parent;
    }

    @Override // org.mozilla.javascript.Scriptable
    public void setParentScope(Scriptable m) {
        this.parent = m;
    }

    @Override // org.mozilla.javascript.Scriptable
    public Object[] getIds() {
        return this.members.getIds(false);
    }

    @Deprecated
    public static Object wrap(Scriptable scope, Object obj, Class<?> staticType) {
        Context cx = Context.getContext();
        return cx.getWrapFactory().wrap(cx, scope, obj, staticType);
    }

    public Object unwrap() {
        return this.javaObject;
    }

    @Override // org.mozilla.javascript.Scriptable
    public String getClassName() {
        return "JavaObject";
    }

    @Override // org.mozilla.javascript.Scriptable
    public Object getDefaultValue(Class<?> hint) {
        Object value;
        String converterName;
        if (hint == null) {
            if (this.javaObject instanceof Boolean) {
                hint = ScriptRuntime.BooleanClass;
            }
            if (this.javaObject instanceof Number) {
                hint = ScriptRuntime.NumberClass;
            }
        }
        if (hint == null || hint == ScriptRuntime.StringClass) {
            value = this.javaObject.toString();
        } else {
            if (hint == ScriptRuntime.BooleanClass) {
                converterName = "booleanValue";
            } else if (hint == ScriptRuntime.NumberClass) {
                converterName = "doubleValue";
            } else {
                throw Context.reportRuntimeError0("msg.default.value");
            }
            Object converterObject = get(converterName, this);
            if (converterObject instanceof Function) {
                Function f = (Function) converterObject;
                value = f.call(Context.getContext(), f.getParentScope(), this, ScriptRuntime.emptyArgs);
            } else if (hint == ScriptRuntime.NumberClass && (this.javaObject instanceof Boolean)) {
                boolean b = ((Boolean) this.javaObject).booleanValue();
                value = b ? ScriptRuntime.wrapNumber(1.0d) : ScriptRuntime.zeroObj;
            } else {
                value = this.javaObject.toString();
            }
        }
        return value;
    }

    public static boolean canConvert(Object fromObj, Class<?> to) {
        int weight = getConversionWeight(fromObj, to);
        return weight < 99;
    }

    static int getConversionWeight(Object fromObj, Class<?> to) {
        int fromCode = getJSTypeCode(fromObj);
        switch (fromCode) {
            case 0:
                if (to == ScriptRuntime.StringClass || to == ScriptRuntime.ObjectClass) {
                    return 1;
                }
                return 99;
            case 1:
                if (!to.isPrimitive()) {
                    return 1;
                }
                return 99;
            case 2:
                if (to == Boolean.TYPE) {
                    return 1;
                }
                if (to == ScriptRuntime.BooleanClass) {
                    return 2;
                }
                if (to == ScriptRuntime.ObjectClass) {
                    return 3;
                }
                if (to == ScriptRuntime.StringClass) {
                    return 4;
                }
                return 99;
            case 3:
                if (to.isPrimitive()) {
                    if (to == Double.TYPE) {
                        return 1;
                    }
                    if (to != Boolean.TYPE) {
                        return 1 + getSizeRank(to);
                    }
                    return 99;
                }
                if (to == ScriptRuntime.StringClass) {
                    return 9;
                }
                if (to == ScriptRuntime.ObjectClass) {
                    return 10;
                }
                if (ScriptRuntime.NumberClass.isAssignableFrom(to)) {
                    return 2;
                }
                return 99;
            case 4:
                if (to == ScriptRuntime.StringClass) {
                    return 1;
                }
                if (to.isInstance(fromObj)) {
                    return 2;
                }
                if (to.isPrimitive()) {
                    if (to == Character.TYPE) {
                        return 3;
                    }
                    if (to != Boolean.TYPE) {
                        return 4;
                    }
                    return 99;
                }
                return 99;
            case 5:
                if (to == ScriptRuntime.ClassClass) {
                    return 1;
                }
                if (to == ScriptRuntime.ObjectClass) {
                    return 3;
                }
                if (to == ScriptRuntime.StringClass) {
                    return 4;
                }
                return 99;
            case 6:
            case 7:
                Object javaObj = fromObj;
                if (javaObj instanceof Wrapper) {
                    javaObj = ((Wrapper) javaObj).unwrap();
                }
                if (to.isInstance(javaObj)) {
                    return 0;
                }
                if (to == ScriptRuntime.StringClass) {
                    return 2;
                }
                if (!to.isPrimitive() || to == Boolean.TYPE || fromCode == 7) {
                    return 99;
                }
                return 2 + getSizeRank(to);
            case 8:
                if (to != ScriptRuntime.ObjectClass && to.isInstance(fromObj)) {
                    return 1;
                }
                if (to.isArray()) {
                    if (fromObj instanceof NativeArray) {
                        return 2;
                    }
                    return 99;
                }
                if (to == ScriptRuntime.ObjectClass) {
                    return 3;
                }
                if (to == ScriptRuntime.StringClass) {
                    return 4;
                }
                if (to == ScriptRuntime.DateClass) {
                    if (fromObj instanceof NativeDate) {
                        return 1;
                    }
                    return 99;
                }
                if (to.isInterface()) {
                    if (fromObj instanceof NativeFunction) {
                        return 1;
                    }
                    if (fromObj instanceof NativeObject) {
                        return 2;
                    }
                    return 12;
                }
                if (to.isPrimitive() && to != Boolean.TYPE) {
                    return 4 + getSizeRank(to);
                }
                return 99;
            default:
                return 99;
        }
    }

    static int getSizeRank(Class<?> aType) {
        if (aType == Double.TYPE) {
            return 1;
        }
        if (aType == Float.TYPE) {
            return 2;
        }
        if (aType == Long.TYPE) {
            return 3;
        }
        if (aType == Integer.TYPE) {
            return 4;
        }
        if (aType == Short.TYPE) {
            return 5;
        }
        if (aType == Character.TYPE) {
            return 6;
        }
        if (aType == Byte.TYPE) {
            return 7;
        }
        if (aType == Boolean.TYPE) {
            return 99;
        }
        return 8;
    }

    private static int getJSTypeCode(Object value) {
        if (value == null) {
            return 1;
        }
        if (value == Undefined.instance) {
            return 0;
        }
        if (value instanceof CharSequence) {
            return 4;
        }
        if (value instanceof Number) {
            return 3;
        }
        if (value instanceof Boolean) {
            return 2;
        }
        if (value instanceof Scriptable) {
            if (value instanceof NativeJavaClass) {
                return 5;
            }
            if (value instanceof NativeJavaArray) {
                return 7;
            }
            if (value instanceof Wrapper) {
                return 6;
            }
            return 8;
        }
        if (value instanceof Class) {
            return 5;
        }
        Class<?> valueClass = value.getClass();
        if (valueClass.isArray()) {
            return 7;
        }
        return 6;
    }

    @Deprecated
    public static Object coerceType(Class<?> type, Object value) {
        return coerceTypeImpl(type, value);
    }

    static Object coerceTypeImpl(Class<?> type, Object value) {
        if (value != null && value.getClass() == type) {
            return value;
        }
        switch (getJSTypeCode(value)) {
            case 0:
                if (type == ScriptRuntime.StringClass || type == ScriptRuntime.ObjectClass) {
                    return "undefined";
                }
                reportConversionError("undefined", type);
                break;
                break;
            case 1:
                if (type.isPrimitive()) {
                    reportConversionError(value, type);
                    return null;
                }
                return null;
            case 2:
                if (type == Boolean.TYPE || type == ScriptRuntime.BooleanClass || type == ScriptRuntime.ObjectClass) {
                    return value;
                }
                if (type == ScriptRuntime.StringClass) {
                    return value.toString();
                }
                reportConversionError(value, type);
                break;
                break;
            case 3:
                if (type == ScriptRuntime.StringClass) {
                    return ScriptRuntime.toString(value);
                }
                if (type == ScriptRuntime.ObjectClass) {
                    Context context = Context.getCurrentContext();
                    if (context != null && context.hasFeature(18)) {
                        long roundedValue = Math.round(toDouble(value));
                        if (roundedValue == toDouble(value)) {
                            return coerceToNumber(Long.TYPE, value);
                        }
                    }
                    return coerceToNumber(Double.TYPE, value);
                }
                if ((type.isPrimitive() && type != Boolean.TYPE) || ScriptRuntime.NumberClass.isAssignableFrom(type)) {
                    return coerceToNumber(type, value);
                }
                reportConversionError(value, type);
                break;
                break;
            case 4:
                if (type == ScriptRuntime.StringClass || type.isInstance(value)) {
                    return value.toString();
                }
                if (type == Character.TYPE || type == ScriptRuntime.CharacterClass) {
                    if (((CharSequence) value).length() == 1) {
                        return Character.valueOf(((CharSequence) value).charAt(0));
                    }
                    return coerceToNumber(type, value);
                }
                if ((type.isPrimitive() && type != Boolean.TYPE) || ScriptRuntime.NumberClass.isAssignableFrom(type)) {
                    return coerceToNumber(type, value);
                }
                reportConversionError(value, type);
                break;
                break;
            case 5:
                if (value instanceof Wrapper) {
                    value = ((Wrapper) value).unwrap();
                }
                if (type == ScriptRuntime.ClassClass || type == ScriptRuntime.ObjectClass) {
                    return value;
                }
                if (type == ScriptRuntime.StringClass) {
                    return value.toString();
                }
                reportConversionError(value, type);
                break;
                break;
            case 6:
            case 7:
                if (value instanceof Wrapper) {
                    value = ((Wrapper) value).unwrap();
                }
                if (type.isPrimitive()) {
                    if (type == Boolean.TYPE) {
                        reportConversionError(value, type);
                    }
                    return coerceToNumber(type, value);
                }
                if (type == ScriptRuntime.StringClass) {
                    return value.toString();
                }
                if (type.isInstance(value)) {
                    return value;
                }
                reportConversionError(value, type);
                break;
                break;
            case 8:
                if (type == ScriptRuntime.StringClass) {
                    return ScriptRuntime.toString(value);
                }
                if (type.isPrimitive()) {
                    if (type == Boolean.TYPE) {
                        reportConversionError(value, type);
                    }
                    return coerceToNumber(type, value);
                }
                if (type.isInstance(value)) {
                    return value;
                }
                if (type == ScriptRuntime.DateClass && (value instanceof NativeDate)) {
                    double time = ((NativeDate) value).getJSTimeValue();
                    return new Date((long) time);
                }
                if (type.isArray() && (value instanceof NativeArray)) {
                    NativeArray nativeArray = (NativeArray) value;
                    long length = nativeArray.getLength();
                    Class<?> arrayType = type.getComponentType();
                    Object Result = Array.newInstance(arrayType, (int) length);
                    for (int i = 0; i < length; i++) {
                        try {
                            Array.set(Result, i, coerceTypeImpl(arrayType, nativeArray.get(i, nativeArray)));
                        } catch (EvaluatorException e) {
                            reportConversionError(value, type);
                        }
                    }
                    return Result;
                }
                if (value instanceof Wrapper) {
                    value = ((Wrapper) value).unwrap();
                    if (type.isInstance(value)) {
                        return value;
                    }
                    reportConversionError(value, type);
                } else {
                    if (type.isInterface() && ((value instanceof NativeObject) || (value instanceof NativeFunction) || (value instanceof ArrowFunction))) {
                        return createInterfaceAdapter(type, (ScriptableObject) value);
                    }
                    reportConversionError(value, type);
                }
                break;
                break;
        }
        return value;
    }

    protected static Object createInterfaceAdapter(Class<?> type, ScriptableObject so) {
        Object key = Kit.makeHashKeyFromPair(COERCED_INTERFACE_KEY, type);
        Object old = so.getAssociatedValue(key);
        if (old != null) {
            return old;
        }
        Context cx = Context.getContext();
        Object glue = InterfaceAdapter.create(cx, type, so);
        return so.associateValue(key, glue);
    }

    private static Object coerceToNumber(Class<?> type, Object value) {
        Class<?> valueClass = value.getClass();
        if (type == Character.TYPE || type == ScriptRuntime.CharacterClass) {
            if (valueClass == ScriptRuntime.CharacterClass) {
                return value;
            }
            return Character.valueOf((char) toInteger(value, ScriptRuntime.CharacterClass, 0.0d, 65535.0d));
        }
        if (type == ScriptRuntime.ObjectClass || type == ScriptRuntime.DoubleClass || type == Double.TYPE) {
            return valueClass == ScriptRuntime.DoubleClass ? value : Double.valueOf(toDouble(value));
        }
        if (type == ScriptRuntime.FloatClass || type == Float.TYPE) {
            if (valueClass == ScriptRuntime.FloatClass) {
                return value;
            }
            double number = toDouble(value);
            if (Double.isInfinite(number) || Double.isNaN(number) || number == 0.0d) {
                return Float.valueOf((float) number);
            }
            double absNumber = Math.abs(number);
            if (absNumber < 1.401298464324817E-45d) {
                return Float.valueOf(number > 0.0d ? ColumnText.GLOBAL_SPACE_CHAR_RATIO : -0.0f);
            }
            if (absNumber > 3.4028234663852886E38d) {
                return Float.valueOf(number > 0.0d ? Float.POSITIVE_INFINITY : Float.NEGATIVE_INFINITY);
            }
            return Float.valueOf((float) number);
        }
        if (type == ScriptRuntime.IntegerClass || type == Integer.TYPE) {
            if (valueClass == ScriptRuntime.IntegerClass) {
                return value;
            }
            return Integer.valueOf((int) toInteger(value, ScriptRuntime.IntegerClass, -2.147483648E9d, 2.147483647E9d));
        }
        if (type == ScriptRuntime.LongClass || type == Long.TYPE) {
            if (valueClass == ScriptRuntime.LongClass) {
                return value;
            }
            double max = Double.longBitsToDouble(4890909195324358655L);
            double min = Double.longBitsToDouble(-4332462841530417152L);
            return Long.valueOf(toInteger(value, ScriptRuntime.LongClass, min, max));
        }
        if (type == ScriptRuntime.ShortClass || type == Short.TYPE) {
            if (valueClass == ScriptRuntime.ShortClass) {
                return value;
            }
            return Short.valueOf((short) toInteger(value, ScriptRuntime.ShortClass, -32768.0d, 32767.0d));
        }
        if (type == ScriptRuntime.ByteClass || type == Byte.TYPE) {
            if (valueClass == ScriptRuntime.ByteClass) {
                return value;
            }
            return Byte.valueOf((byte) toInteger(value, ScriptRuntime.ByteClass, -128.0d, 127.0d));
        }
        return Double.valueOf(toDouble(value));
    }

    private static double toDouble(Object value) {
        Method meth;
        if (value instanceof Number) {
            return ((Number) value).doubleValue();
        }
        if (value instanceof String) {
            return ScriptRuntime.toNumber((String) value);
        }
        if (value instanceof Scriptable) {
            if (value instanceof Wrapper) {
                return toDouble(((Wrapper) value).unwrap());
            }
            return ScriptRuntime.toNumber(value);
        }
        try {
            meth = value.getClass().getMethod("doubleValue", (Class[]) null);
        } catch (NoSuchMethodException e) {
            meth = null;
        } catch (SecurityException e2) {
            meth = null;
        }
        if (meth != null) {
            try {
                return ((Number) meth.invoke(value, (Object[]) null)).doubleValue();
            } catch (IllegalAccessException e3) {
                reportConversionError(value, Double.TYPE);
            } catch (InvocationTargetException e4) {
                reportConversionError(value, Double.TYPE);
            }
        }
        return ScriptRuntime.toNumber(value.toString());
    }

    private static long toInteger(Object value, Class<?> type, double min, double max) {
        double d;
        double d2 = toDouble(value);
        if (Double.isInfinite(d2) || Double.isNaN(d2)) {
            reportConversionError(ScriptRuntime.toString(value), type);
        }
        if (d2 > 0.0d) {
            d = Math.floor(d2);
        } else {
            d = Math.ceil(d2);
        }
        if (d < min || d > max) {
            reportConversionError(ScriptRuntime.toString(value), type);
        }
        return (long) d;
    }

    static void reportConversionError(Object value, Class<?> type) {
        throw Context.reportRuntimeError2("msg.conversion.not.allowed", String.valueOf(value), JavaMembers.javaSignature(type));
    }

    private void writeObject(ObjectOutputStream out) throws IOException {
        out.defaultWriteObject();
        out.writeBoolean(this.isAdapter);
        if (this.isAdapter) {
            if (adapter_writeAdapterObject == null) {
                throw new IOException();
            }
            Object[] args = {this.javaObject, out};
            try {
                adapter_writeAdapterObject.invoke(null, args);
            } catch (Exception e) {
                throw new IOException();
            }
        } else {
            out.writeObject(this.javaObject);
        }
        if (this.staticType != null) {
            out.writeObject(this.staticType.getName());
        } else {
            out.writeObject(null);
        }
    }

    private void readObject(ObjectInputStream in) throws ClassNotFoundException, IOException {
        in.defaultReadObject();
        this.isAdapter = in.readBoolean();
        if (this.isAdapter) {
            if (adapter_readAdapterObject == null) {
                throw new ClassNotFoundException();
            }
            Object[] args = {this, in};
            try {
                this.javaObject = adapter_readAdapterObject.invoke(null, args);
            } catch (Exception e) {
                throw new IOException();
            }
        } else {
            this.javaObject = in.readObject();
        }
        String className = (String) in.readObject();
        if (className != null) {
            this.staticType = Class.forName(className);
        } else {
            this.staticType = null;
        }
        initMembers();
    }

    static {
        Class<?>[] sig2 = new Class[2];
        Class<?> cl = Kit.classOrNull("org.mozilla.javascript.JavaAdapter");
        if (cl != null) {
            try {
                sig2[0] = ScriptRuntime.ObjectClass;
                sig2[1] = Kit.classOrNull("java.io.ObjectOutputStream");
                adapter_writeAdapterObject = cl.getMethod("writeAdapterObject", sig2);
                sig2[0] = ScriptRuntime.ScriptableClass;
                sig2[1] = Kit.classOrNull("java.io.ObjectInputStream");
                adapter_readAdapterObject = cl.getMethod("readAdapterObject", sig2);
            } catch (NoSuchMethodException e) {
                adapter_writeAdapterObject = null;
                adapter_readAdapterObject = null;
            }
        }
    }
}
