package org.mozilla.javascript;

import java.lang.reflect.Array;
import java.lang.reflect.Modifier;
import java.util.Map;

/* JADX INFO: loaded from: autofirma.jar:org/mozilla/javascript/NativeJavaClass.class */
public class NativeJavaClass extends NativeJavaObject implements Function {
    private static final long serialVersionUID = -6460763940409461664L;
    static final String javaClassPropertyName = "__javaObject__";
    private Map<String, FieldAndMethods> staticFieldAndMethods;

    public NativeJavaClass() {
    }

    public NativeJavaClass(Scriptable scope, Class<?> cl) {
        this(scope, cl, false);
    }

    public NativeJavaClass(Scriptable scope, Class<?> cl, boolean isAdapter) {
        super(scope, cl, null, isAdapter);
    }

    @Override // org.mozilla.javascript.NativeJavaObject
    protected void initMembers() {
        Class<?> cl = (Class) this.javaObject;
        this.members = JavaMembers.lookupClass(this.parent, cl, cl, this.isAdapter);
        this.staticFieldAndMethods = this.members.getFieldAndMethodsObjects(this, cl, true);
    }

    @Override // org.mozilla.javascript.NativeJavaObject, org.mozilla.javascript.Scriptable
    public String getClassName() {
        return "JavaClass";
    }

    @Override // org.mozilla.javascript.NativeJavaObject, org.mozilla.javascript.Scriptable
    public boolean has(String name, Scriptable start) {
        return this.members.has(name, true) || javaClassPropertyName.equals(name);
    }

    @Override // org.mozilla.javascript.NativeJavaObject, org.mozilla.javascript.Scriptable
    public Object get(String name, Scriptable start) {
        Object result;
        if (name.equals("prototype")) {
            return null;
        }
        if (this.staticFieldAndMethods != null && (result = this.staticFieldAndMethods.get(name)) != null) {
            return result;
        }
        if (this.members.has(name, true)) {
            return this.members.get(this, name, this.javaObject, true);
        }
        Context cx = Context.getContext();
        Scriptable scope = ScriptableObject.getTopLevelScope(start);
        WrapFactory wrapFactory = cx.getWrapFactory();
        if (javaClassPropertyName.equals(name)) {
            return wrapFactory.wrap(cx, scope, this.javaObject, ScriptRuntime.ClassClass);
        }
        Class<?> nestedClass = findNestedClass(getClassObject(), name);
        if (nestedClass != null) {
            Scriptable nestedValue = wrapFactory.wrapJavaClass(cx, scope, nestedClass);
            nestedValue.setParentScope(this);
            return nestedValue;
        }
        throw this.members.reportMemberNotFound(name);
    }

    @Override // org.mozilla.javascript.NativeJavaObject, org.mozilla.javascript.Scriptable
    public void put(String name, Scriptable start, Object value) {
        this.members.put(this, name, this.javaObject, value, true);
    }

    @Override // org.mozilla.javascript.NativeJavaObject, org.mozilla.javascript.Scriptable
    public Object[] getIds() {
        return this.members.getIds(true);
    }

    public Class<?> getClassObject() {
        return (Class) super.unwrap();
    }

    @Override // org.mozilla.javascript.NativeJavaObject, org.mozilla.javascript.Scriptable
    public Object getDefaultValue(Class<?> hint) {
        if (hint == null || hint == ScriptRuntime.StringClass) {
            return toString();
        }
        if (hint == ScriptRuntime.BooleanClass) {
            return Boolean.TRUE;
        }
        if (hint == ScriptRuntime.NumberClass) {
            return ScriptRuntime.NaNobj;
        }
        return this;
    }

    @Override // org.mozilla.javascript.Function, org.mozilla.javascript.Callable
    public Object call(Context cx, Scriptable scope, Scriptable thisObj, Object[] args) {
        if (args.length == 1 && (args[0] instanceof Scriptable)) {
            Class<?> c = getClassObject();
            Scriptable p = (Scriptable) args[0];
            do {
                if (p instanceof Wrapper) {
                    Object o = ((Wrapper) p).unwrap();
                    if (c.isInstance(o)) {
                        return p;
                    }
                }
                p = p.getPrototype();
            } while (p != null);
        }
        return construct(cx, scope, args);
    }

    @Override // org.mozilla.javascript.Function
    public Scriptable construct(Context cx, Scriptable scope, Object[] args) {
        Class<?> classObject = getClassObject();
        int modifiers = classObject.getModifiers();
        if (!Modifier.isInterface(modifiers) && !Modifier.isAbstract(modifiers)) {
            NativeJavaMethod ctors = this.members.ctors;
            int index = ctors.findCachedFunction(cx, args);
            if (index < 0) {
                String sig = NativeJavaMethod.scriptSignature(args);
                throw Context.reportRuntimeError2("msg.no.java.ctor", classObject.getName(), sig);
            }
            return constructSpecific(cx, scope, args, ctors.methods[index]);
        }
        if (args.length == 0) {
            throw Context.reportRuntimeError0("msg.adapter.zero.args");
        }
        Scriptable topLevel = ScriptableObject.getTopLevelScope(this);
        String msg = "";
        try {
            if ("Dalvik".equals(System.getProperty("java.vm.name")) && classObject.isInterface()) {
                Object obj = createInterfaceAdapter(classObject, ScriptableObject.ensureScriptableObject(args[0]));
                return cx.getWrapFactory().wrapAsJavaObject(cx, scope, obj, null);
            }
            Object v = topLevel.get("JavaAdapter", topLevel);
            if (v != NOT_FOUND) {
                Function f = (Function) v;
                Object[] adapterArgs = {this, args[0]};
                return f.construct(cx, topLevel, adapterArgs);
            }
            throw Context.reportRuntimeError2("msg.cant.instantiate", msg, classObject.getName());
        } catch (Exception ex) {
            String m = ex.getMessage();
            if (m != null) {
                msg = m;
            }
        }
    }

    static Scriptable constructSpecific(Context cx, Scriptable scope, Object[] args, MemberBox ctor) {
        Object instance = constructInternal(args, ctor);
        Scriptable topLevel = ScriptableObject.getTopLevelScope(scope);
        return cx.getWrapFactory().wrapNewObject(cx, topLevel, instance);
    }

    static Object constructInternal(Object[] args, MemberBox ctor) {
        Object varArgs;
        Class<?>[] argTypes = ctor.argTypes;
        if (ctor.vararg) {
            Object[] newArgs = new Object[argTypes.length];
            for (int i = 0; i < argTypes.length - 1; i++) {
                newArgs[i] = Context.jsToJava(args[i], argTypes[i]);
            }
            if (args.length == argTypes.length && (args[args.length - 1] == null || (args[args.length - 1] instanceof NativeArray) || (args[args.length - 1] instanceof NativeJavaArray))) {
                varArgs = Context.jsToJava(args[args.length - 1], argTypes[argTypes.length - 1]);
            } else {
                Class<?> componentType = argTypes[argTypes.length - 1].getComponentType();
                varArgs = Array.newInstance(componentType, (args.length - argTypes.length) + 1);
                for (int i2 = 0; i2 < Array.getLength(varArgs); i2++) {
                    Object value = Context.jsToJava(args[(argTypes.length - 1) + i2], componentType);
                    Array.set(varArgs, i2, value);
                }
            }
            newArgs[argTypes.length - 1] = varArgs;
            args = newArgs;
        } else {
            for (int i3 = 0; i3 < args.length; i3++) {
                Object arg = args[i3];
                Object x = Context.jsToJava(arg, argTypes[i3]);
                if (x != arg) {
                    if (args == args) {
                        args = (Object[]) args.clone();
                    }
                    args[i3] = x;
                }
            }
        }
        return ctor.newInstance(args);
    }

    public String toString() {
        return "[JavaClass " + getClassObject().getName() + "]";
    }

    @Override // org.mozilla.javascript.NativeJavaObject, org.mozilla.javascript.Scriptable
    public boolean hasInstance(Scriptable value) {
        if ((value instanceof Wrapper) && !(value instanceof NativeJavaClass)) {
            Object instance = ((Wrapper) value).unwrap();
            return getClassObject().isInstance(instance);
        }
        return false;
    }

    private static Class<?> findNestedClass(Class<?> parentClass, String name) {
        String nestedClassName = parentClass.getName() + '$' + name;
        ClassLoader loader = parentClass.getClassLoader();
        if (loader == null) {
            return Kit.classOrNull(nestedClassName);
        }
        return Kit.classOrNull(loader, nestedClassName);
    }
}
