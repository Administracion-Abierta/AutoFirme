package org.mozilla.javascript;

import com.ctc.wstx.shaded.msv_core.datatype.xsd.XSDatatype;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.security.CodeSource;
import java.security.ProtectionDomain;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Map;
import org.mozilla.classfile.ClassFileWriter;

/* JADX INFO: loaded from: autofirma.jar:org/mozilla/javascript/JavaAdapter.class */
public final class JavaAdapter implements IdFunctionCall {
    private static final Object FTAG = "JavaAdapter";
    private static final int Id_JavaAdapter = 1;

    /* JADX INFO: loaded from: autofirma.jar:org/mozilla/javascript/JavaAdapter$JavaAdapterSignature.class */
    static class JavaAdapterSignature {
        Class<?> superClass;
        Class<?>[] interfaces;
        ObjToIntMap names;

        JavaAdapterSignature(Class<?> superClass, Class<?>[] interfaces, ObjToIntMap names) {
            this.superClass = superClass;
            this.interfaces = interfaces;
            this.names = names;
        }

        public boolean equals(Object obj) {
            if (!(obj instanceof JavaAdapterSignature)) {
                return false;
            }
            JavaAdapterSignature sig = (JavaAdapterSignature) obj;
            if (this.superClass != sig.superClass) {
                return false;
            }
            if (this.interfaces != sig.interfaces) {
                if (this.interfaces.length != sig.interfaces.length) {
                    return false;
                }
                for (int i = 0; i < this.interfaces.length; i++) {
                    if (this.interfaces[i] != sig.interfaces[i]) {
                        return false;
                    }
                }
            }
            if (this.names.size() != sig.names.size()) {
                return false;
            }
            ObjToIntMap.Iterator iter = new ObjToIntMap.Iterator(this.names);
            iter.start();
            while (!iter.done()) {
                String name = (String) iter.getKey();
                int arity = iter.getValue();
                if (arity == sig.names.get(name, arity + 1)) {
                    iter.next();
                } else {
                    return false;
                }
            }
            return true;
        }

        public int hashCode() {
            return (this.superClass.hashCode() + Arrays.hashCode(this.interfaces)) ^ this.names.size();
        }
    }

    public static void init(Context cx, Scriptable scope, boolean sealed) {
        JavaAdapter obj = new JavaAdapter();
        IdFunctionObject ctor = new IdFunctionObject(obj, FTAG, 1, "JavaAdapter", 1, scope);
        ctor.markAsConstructor(null);
        if (sealed) {
            ctor.sealObject();
        }
        ctor.exportAsScopeProperty();
    }

    @Override // org.mozilla.javascript.IdFunctionCall
    public Object execIdCall(IdFunctionObject f, Context cx, Scriptable scope, Scriptable thisObj, Object[] args) {
        if (f.hasTag(FTAG) && f.methodId() == 1) {
            return js_createAdapter(cx, scope, args);
        }
        throw f.unknown();
    }

    public static Object convertResult(Object result, Class<?> c) {
        if (result == Undefined.instance && c != ScriptRuntime.ObjectClass && c != ScriptRuntime.StringClass) {
            return null;
        }
        return Context.jsToJava(result, c);
    }

    public static Scriptable createAdapterWrapper(Scriptable obj, Object adapter) {
        Scriptable scope = ScriptableObject.getTopLevelScope(obj);
        NativeJavaObject res = new NativeJavaObject(scope, adapter, null, true);
        res.setPrototype(obj);
        return res;
    }

    public static Object getAdapterSelf(Class<?> adapterClass, Object adapter) throws IllegalAccessException, NoSuchFieldException {
        Field self = adapterClass.getDeclaredField("self");
        return self.get(adapter);
    }

    static Object js_createAdapter(Context cx, Scriptable scope, Object[] args) {
        Object adapter;
        int N = args.length;
        if (N == 0) {
            throw ScriptRuntime.typeError0("msg.adapter.zero.args");
        }
        int classCount = 0;
        while (classCount < N - 1) {
            Object arg = args[classCount];
            if (arg instanceof NativeObject) {
                break;
            }
            if (arg instanceof NativeJavaClass) {
                classCount++;
            } else {
                throw ScriptRuntime.typeError2("msg.not.java.class.arg", String.valueOf(classCount), ScriptRuntime.toString(arg));
            }
        }
        Class<?> superClass = null;
        Class<?>[] intfs = new Class[classCount];
        int interfaceCount = 0;
        for (int i = 0; i < classCount; i++) {
            Class<?> c = ((NativeJavaClass) args[i]).getClassObject();
            if (!c.isInterface()) {
                if (superClass != null) {
                    throw ScriptRuntime.typeError2("msg.only.one.super", superClass.getName(), c.getName());
                }
                superClass = c;
            } else {
                int i2 = interfaceCount;
                interfaceCount++;
                intfs[i2] = c;
            }
        }
        if (superClass == null) {
            superClass = ScriptRuntime.ObjectClass;
        }
        Class<?>[] interfaces = new Class[interfaceCount];
        System.arraycopy(intfs, 0, interfaces, 0, interfaceCount);
        Scriptable obj = ScriptableObject.ensureScriptable(args[classCount]);
        Class<?> adapterClass = getAdapterClass(scope, superClass, interfaces, obj);
        int argsCount = (N - classCount) - 1;
        try {
            if (argsCount > 0) {
                Object[] ctorArgs = new Object[argsCount + 2];
                ctorArgs[0] = obj;
                ctorArgs[1] = cx.getFactory();
                System.arraycopy(args, classCount + 1, ctorArgs, 2, argsCount);
                NativeJavaClass classWrapper = new NativeJavaClass(scope, adapterClass, true);
                NativeJavaMethod ctors = classWrapper.members.ctors;
                int index = ctors.findCachedFunction(cx, ctorArgs);
                if (index < 0) {
                    String sig = NativeJavaMethod.scriptSignature(args);
                    throw Context.reportRuntimeError2("msg.no.java.ctor", adapterClass.getName(), sig);
                }
                adapter = NativeJavaClass.constructInternal(ctorArgs, ctors.methods[index]);
            } else {
                Class<?>[] ctorParms = {ScriptRuntime.ScriptableClass, ScriptRuntime.ContextFactoryClass};
                adapter = adapterClass.getConstructor(ctorParms).newInstance(obj, cx.getFactory());
            }
            Object self = getAdapterSelf(adapterClass, adapter);
            if (self instanceof Wrapper) {
                Object unwrapped = ((Wrapper) self).unwrap();
                if (unwrapped instanceof Scriptable) {
                    if (unwrapped instanceof ScriptableObject) {
                        ScriptRuntime.setObjectProtoAndParent((ScriptableObject) unwrapped, scope);
                    }
                    return unwrapped;
                }
            }
            return self;
        } catch (Exception ex) {
            throw Context.throwAsScriptRuntimeEx(ex);
        }
    }

    public static void writeAdapterObject(Object javaObject, ObjectOutputStream out) throws IOException {
        Class<?> cl = javaObject.getClass();
        out.writeObject(cl.getSuperclass().getName());
        Class<?>[] interfaces = cl.getInterfaces();
        String[] interfaceNames = new String[interfaces.length];
        for (int i = 0; i < interfaces.length; i++) {
            interfaceNames[i] = interfaces[i].getName();
        }
        out.writeObject(interfaceNames);
        try {
            Object delegee = cl.getField("delegee").get(javaObject);
            out.writeObject(delegee);
        } catch (IllegalAccessException | NoSuchFieldException e) {
            throw new IOException();
        }
    }

    public static Object readAdapterObject(Scriptable self, ObjectInputStream in) throws ClassNotFoundException, IOException {
        ContextFactory factory;
        Context cx = Context.getCurrentContext();
        if (cx != null) {
            factory = cx.getFactory();
        } else {
            factory = null;
        }
        Class<?> superClass = Class.forName((String) in.readObject());
        String[] interfaceNames = (String[]) in.readObject();
        Class<?>[] interfaces = new Class[interfaceNames.length];
        for (int i = 0; i < interfaceNames.length; i++) {
            interfaces[i] = Class.forName(interfaceNames[i]);
        }
        Scriptable delegee = (Scriptable) in.readObject();
        Class<?> adapterClass = getAdapterClass(self, superClass, interfaces, delegee);
        Class<?>[] ctorParms = {ScriptRuntime.ContextFactoryClass, ScriptRuntime.ScriptableClass, ScriptRuntime.ScriptableClass};
        Object[] ctorArgs = {factory, delegee, self};
        try {
            return adapterClass.getConstructor(ctorParms).newInstance(ctorArgs);
        } catch (IllegalAccessException | InstantiationException | NoSuchMethodException | InvocationTargetException e) {
            throw new ClassNotFoundException("adapter");
        }
    }

    private static ObjToIntMap getObjectFunctionNames(Scriptable obj) {
        Object[] ids = ScriptableObject.getPropertyIds(obj);
        ObjToIntMap map = new ObjToIntMap(ids.length);
        for (int i = 0; i != ids.length; i++) {
            if (ids[i] instanceof String) {
                String id = (String) ids[i];
                Object value = ScriptableObject.getProperty(obj, id);
                if (value instanceof Function) {
                    Function f = (Function) value;
                    int length = ScriptRuntime.toInt32(ScriptableObject.getProperty(f, XSDatatype.FACET_LENGTH));
                    if (length < 0) {
                        length = 0;
                    }
                    map.put(id, length);
                }
            }
        }
        return map;
    }

    private static Class<?> getAdapterClass(Scriptable scope, Class<?> superClass, Class<?>[] interfaces, Scriptable obj) {
        ClassCache cache = ClassCache.get(scope);
        Map<JavaAdapterSignature, Class<?>> generated = cache.getInterfaceAdapterCacheMap();
        ObjToIntMap names = getObjectFunctionNames(obj);
        JavaAdapterSignature sig = new JavaAdapterSignature(superClass, interfaces, names);
        Class<?> adapterClass = generated.get(sig);
        if (adapterClass == null) {
            String adapterName = "adapter" + cache.newClassSerialNumber();
            byte[] code = createAdapterCode(names, adapterName, superClass, interfaces, null);
            adapterClass = loadAdapterClass(adapterName, code);
            if (cache.isCachingEnabled()) {
                generated.put(sig, adapterClass);
            }
        }
        return adapterClass;
    }

    /* JADX WARN: Code duplicated, block: B:44:0x0172  */
    /* JADX WARN: Code duplicated, block: B:88:0x0192 A[SYNTHETIC] */
    public static byte[] createAdapterCode(ObjToIntMap functionNames, String adapterName, Class<?> superClass, Class<?>[] interfaces, String scriptClassName) {
        String methodKey;
        ClassFileWriter cfw = new ClassFileWriter(adapterName, superClass.getName(), "<adapter>");
        cfw.addField("factory", "Lorg/mozilla/javascript/ContextFactory;", (short) 17);
        cfw.addField("delegee", "Lorg/mozilla/javascript/Scriptable;", (short) 17);
        cfw.addField("self", "Lorg/mozilla/javascript/Scriptable;", (short) 17);
        int interfacesCount = interfaces == null ? 0 : interfaces.length;
        for (int i = 0; i < interfacesCount; i++) {
            if (interfaces[i] != null) {
                cfw.addInterface(interfaces[i].getName());
            }
        }
        String superName = superClass.getName().replace('.', '/');
        Constructor<?>[] ctors = superClass.getDeclaredConstructors();
        for (Constructor<?> ctor : ctors) {
            int mod = ctor.getModifiers();
            if (Modifier.isPublic(mod) || Modifier.isProtected(mod)) {
                generateCtor(cfw, adapterName, superName, ctor);
            }
        }
        generateSerialCtor(cfw, adapterName, superName);
        if (scriptClassName != null) {
            generateEmptyCtor(cfw, adapterName, superName, scriptClassName);
        }
        ObjToIntMap generatedOverrides = new ObjToIntMap();
        ObjToIntMap generatedMethods = new ObjToIntMap();
        for (int i2 = 0; i2 < interfacesCount; i2++) {
            Method[] methods = interfaces[i2].getMethods();
            for (Method method : methods) {
                int mods = method.getModifiers();
                if (!Modifier.isStatic(mods) && !Modifier.isFinal(mods) && !method.isDefault()) {
                    String methodName = method.getName();
                    Class<?>[] argTypes = method.getParameterTypes();
                    if (!functionNames.has(methodName)) {
                        try {
                            superClass.getMethod(methodName, argTypes);
                        } catch (NoSuchMethodException e) {
                            methodKey = methodName + getMethodSignature(method, argTypes);
                            if (!generatedOverrides.has(methodKey)) {
                                generateMethod(cfw, adapterName, methodName, argTypes, method.getReturnType(), true);
                                generatedOverrides.put(methodKey, 0);
                                generatedMethods.put(methodName, 0);
                            }
                        }
                    } else {
                        methodKey = methodName + getMethodSignature(method, argTypes);
                        if (!generatedOverrides.has(methodKey)) {
                            generateMethod(cfw, adapterName, methodName, argTypes, method.getReturnType(), true);
                            generatedOverrides.put(methodKey, 0);
                            generatedMethods.put(methodName, 0);
                        }
                    }
                }
            }
        }
        Method[] methods2 = getOverridableMethods(superClass);
        for (Method method2 : methods2) {
            boolean isAbstractMethod = Modifier.isAbstract(method2.getModifiers());
            String methodName2 = method2.getName();
            if (isAbstractMethod || functionNames.has(methodName2)) {
                Class<?>[] argTypes2 = method2.getParameterTypes();
                String methodSignature = getMethodSignature(method2, argTypes2);
                String methodKey2 = methodName2 + methodSignature;
                if (!generatedOverrides.has(methodKey2)) {
                    generateMethod(cfw, adapterName, methodName2, argTypes2, method2.getReturnType(), true);
                    generatedOverrides.put(methodKey2, 0);
                    generatedMethods.put(methodName2, 0);
                    if (!isAbstractMethod) {
                        generateSuper(cfw, adapterName, superName, methodName2, methodSignature, argTypes2, method2.getReturnType());
                    }
                }
            }
        }
        ObjToIntMap.Iterator iter = new ObjToIntMap.Iterator(functionNames);
        iter.start();
        while (!iter.done()) {
            String functionName = (String) iter.getKey();
            if (!generatedMethods.has(functionName)) {
                int length = iter.getValue();
                Class<?>[] parms = new Class[length];
                for (int k = 0; k < length; k++) {
                    parms[k] = ScriptRuntime.ObjectClass;
                }
                generateMethod(cfw, adapterName, functionName, parms, ScriptRuntime.ObjectClass, false);
            }
            iter.next();
        }
        return cfw.toByteArray();
    }

    static Method[] getOverridableMethods(Class<?> clazz) {
        ArrayList<Method> list = new ArrayList<>();
        HashSet<String> skip = new HashSet<>();
        Class<?> superclass = clazz;
        while (true) {
            Class<?> c = superclass;
            if (c == null) {
                break;
            }
            appendOverridableMethods(c, list, skip);
            superclass = c.getSuperclass();
        }
        Class<?> superclass2 = clazz;
        while (true) {
            Class<?> c2 = superclass2;
            if (c2 != null) {
                for (Class<?> intf : c2.getInterfaces()) {
                    appendOverridableMethods(intf, list, skip);
                }
                superclass2 = c2.getSuperclass();
            } else {
                return (Method[]) list.toArray(new Method[list.size()]);
            }
        }
    }

    private static void appendOverridableMethods(Class<?> c, ArrayList<Method> list, HashSet<String> skip) {
        Method[] methods = c.getDeclaredMethods();
        for (int i = 0; i < methods.length; i++) {
            String methodKey = methods[i].getName() + getMethodSignature(methods[i], methods[i].getParameterTypes());
            if (!skip.contains(methodKey)) {
                int mods = methods[i].getModifiers();
                if (!Modifier.isStatic(mods)) {
                    if (Modifier.isFinal(mods)) {
                        skip.add(methodKey);
                    } else if (Modifier.isPublic(mods) || Modifier.isProtected(mods)) {
                        list.add(methods[i]);
                        skip.add(methodKey);
                    }
                }
            }
        }
    }

    static Class<?> loadAdapterClass(String className, byte[] classBytes) {
        Object staticDomain;
        Class<?> domainClass = SecurityController.getStaticSecurityDomainClass();
        if (domainClass == CodeSource.class || domainClass == ProtectionDomain.class) {
            ProtectionDomain protectionDomain = SecurityUtilities.getScriptProtectionDomain();
            if (protectionDomain == null) {
                protectionDomain = JavaAdapter.class.getProtectionDomain();
            }
            if (domainClass == CodeSource.class) {
                staticDomain = protectionDomain == null ? null : protectionDomain.getCodeSource();
            } else {
                staticDomain = protectionDomain;
            }
        } else {
            staticDomain = null;
        }
        GeneratedClassLoader loader = SecurityController.createLoader(null, staticDomain);
        Class<?> result = loader.defineClass(className, classBytes);
        loader.linkClass(result);
        return result;
    }

    public static Function getFunction(Scriptable obj, String functionName) {
        Object x = ScriptableObject.getProperty(obj, functionName);
        if (x == Scriptable.NOT_FOUND) {
            return null;
        }
        if (!(x instanceof Function)) {
            throw ScriptRuntime.notFunctionError(x, functionName);
        }
        return (Function) x;
    }

    public static Object callMethod(ContextFactory factory, Scriptable thisObj, Function f, Object[] args, long argsToWrap) {
        if (f == null) {
            return null;
        }
        if (factory == null) {
            factory = ContextFactory.getGlobal();
        }
        Scriptable scope = f.getParentScope();
        if (argsToWrap == 0) {
            return Context.call(factory, f, scope, thisObj, args);
        }
        Context cx = Context.getCurrentContext();
        if (cx != null) {
            return doCall(cx, scope, thisObj, f, args, argsToWrap);
        }
        return factory.call(cx2 -> {
            return doCall(cx2, scope, thisObj, f, args, argsToWrap);
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static Object doCall(Context cx, Scriptable scope, Scriptable thisObj, Function f, Object[] args, long argsToWrap) {
        for (int i = 0; i != args.length; i++) {
            if (0 != (argsToWrap & ((long) (1 << i)))) {
                Object arg = args[i];
                if (!(arg instanceof Scriptable)) {
                    args[i] = cx.getWrapFactory().wrap(cx, scope, arg, null);
                }
            }
        }
        return f.call(cx, scope, thisObj, args);
    }

    public static Scriptable runScript(Script script) {
        return (Scriptable) ContextFactory.getGlobal().call(cx -> {
            ScriptableObject global = ScriptRuntime.getGlobal(cx);
            script.exec(cx, global);
            return global;
        });
    }

    private static void generateCtor(ClassFileWriter cfw, String adapterName, String superName, Constructor<?> superCtor) {
        short locals = 3;
        Class<?>[] parameters = superCtor.getParameterTypes();
        if (parameters.length == 0) {
            cfw.startMethod("<init>", "(Lorg/mozilla/javascript/Scriptable;Lorg/mozilla/javascript/ContextFactory;)V", (short) 1);
            cfw.add(42);
            cfw.addInvoke(183, superName, "<init>", "()V");
        } else {
            StringBuilder sig = new StringBuilder("(Lorg/mozilla/javascript/Scriptable;Lorg/mozilla/javascript/ContextFactory;");
            int marker = sig.length();
            for (Class<?> c : parameters) {
                appendTypeString(sig, c);
            }
            sig.append(")V");
            cfw.startMethod("<init>", sig.toString(), (short) 1);
            cfw.add(42);
            short paramOffset = 3;
            for (Class<?> parameter : parameters) {
                paramOffset = (short) (paramOffset + generatePushParam(cfw, paramOffset, parameter));
            }
            locals = paramOffset;
            sig.delete(1, marker);
            cfw.addInvoke(183, superName, "<init>", sig.toString());
        }
        cfw.add(42);
        cfw.add(43);
        cfw.add(181, adapterName, "delegee", "Lorg/mozilla/javascript/Scriptable;");
        cfw.add(42);
        cfw.add(44);
        cfw.add(181, adapterName, "factory", "Lorg/mozilla/javascript/ContextFactory;");
        cfw.add(42);
        cfw.add(43);
        cfw.add(42);
        cfw.addInvoke(184, "org/mozilla/javascript/JavaAdapter", "createAdapterWrapper", "(Lorg/mozilla/javascript/Scriptable;Ljava/lang/Object;)Lorg/mozilla/javascript/Scriptable;");
        cfw.add(181, adapterName, "self", "Lorg/mozilla/javascript/Scriptable;");
        cfw.add(177);
        cfw.stopMethod(locals);
    }

    private static void generateSerialCtor(ClassFileWriter cfw, String adapterName, String superName) {
        cfw.startMethod("<init>", "(Lorg/mozilla/javascript/ContextFactory;Lorg/mozilla/javascript/Scriptable;Lorg/mozilla/javascript/Scriptable;)V", (short) 1);
        cfw.add(42);
        cfw.addInvoke(183, superName, "<init>", "()V");
        cfw.add(42);
        cfw.add(43);
        cfw.add(181, adapterName, "factory", "Lorg/mozilla/javascript/ContextFactory;");
        cfw.add(42);
        cfw.add(44);
        cfw.add(181, adapterName, "delegee", "Lorg/mozilla/javascript/Scriptable;");
        cfw.add(42);
        cfw.add(45);
        cfw.add(181, adapterName, "self", "Lorg/mozilla/javascript/Scriptable;");
        cfw.add(177);
        cfw.stopMethod((short) 4);
    }

    private static void generateEmptyCtor(ClassFileWriter cfw, String adapterName, String superName, String scriptClassName) {
        cfw.startMethod("<init>", "()V", (short) 1);
        cfw.add(42);
        cfw.addInvoke(183, superName, "<init>", "()V");
        cfw.add(42);
        cfw.add(1);
        cfw.add(181, adapterName, "factory", "Lorg/mozilla/javascript/ContextFactory;");
        cfw.add(187, scriptClassName);
        cfw.add(89);
        cfw.addInvoke(183, scriptClassName, "<init>", "()V");
        cfw.addInvoke(184, "org/mozilla/javascript/JavaAdapter", "runScript", "(Lorg/mozilla/javascript/Script;)Lorg/mozilla/javascript/Scriptable;");
        cfw.add(76);
        cfw.add(42);
        cfw.add(43);
        cfw.add(181, adapterName, "delegee", "Lorg/mozilla/javascript/Scriptable;");
        cfw.add(42);
        cfw.add(43);
        cfw.add(42);
        cfw.addInvoke(184, "org/mozilla/javascript/JavaAdapter", "createAdapterWrapper", "(Lorg/mozilla/javascript/Scriptable;Ljava/lang/Object;)Lorg/mozilla/javascript/Scriptable;");
        cfw.add(181, adapterName, "self", "Lorg/mozilla/javascript/Scriptable;");
        cfw.add(177);
        cfw.stopMethod((short) 2);
    }

    static void generatePushWrappedArgs(ClassFileWriter cfw, Class<?>[] argTypes, int arrayLength) {
        cfw.addPush(arrayLength);
        cfw.add(189, "java/lang/Object");
        int paramOffset = 1;
        for (int i = 0; i != argTypes.length; i++) {
            cfw.add(89);
            cfw.addPush(i);
            paramOffset += generateWrapArg(cfw, paramOffset, argTypes[i]);
            cfw.add(83);
        }
    }

    private static int generateWrapArg(ClassFileWriter cfw, int paramOffset, Class<?> argType) {
        int size = 1;
        if (!argType.isPrimitive()) {
            cfw.add(25, paramOffset);
        } else if (argType == Boolean.TYPE) {
            cfw.add(187, "java/lang/Boolean");
            cfw.add(89);
            cfw.add(21, paramOffset);
            cfw.addInvoke(183, "java/lang/Boolean", "<init>", "(Z)V");
        } else if (argType == Character.TYPE) {
            cfw.add(21, paramOffset);
            cfw.addInvoke(184, "java/lang/String", "valueOf", "(C)Ljava/lang/String;");
        } else {
            cfw.add(187, "java/lang/Double");
            cfw.add(89);
            String typeName = argType.getName();
            switch (typeName.charAt(0)) {
                case 'b':
                case 'i':
                case 's':
                    cfw.add(21, paramOffset);
                    cfw.add(135);
                    break;
                case 'd':
                    cfw.add(24, paramOffset);
                    size = 2;
                    break;
                case 'f':
                    cfw.add(23, paramOffset);
                    cfw.add(141);
                    break;
                case 'l':
                    cfw.add(22, paramOffset);
                    cfw.add(138);
                    size = 2;
                    break;
            }
            cfw.addInvoke(183, "java/lang/Double", "<init>", "(D)V");
        }
        return size;
    }

    static void generateReturnResult(ClassFileWriter cfw, Class<?> retType, boolean callConvertResult) {
        if (retType == Void.TYPE) {
            cfw.add(87);
            cfw.add(177);
            return;
        }
        if (retType == Boolean.TYPE) {
            cfw.addInvoke(184, "org/mozilla/javascript/Context", "toBoolean", "(Ljava/lang/Object;)Z");
            cfw.add(172);
            return;
        }
        if (retType == Character.TYPE) {
            cfw.addInvoke(184, "org/mozilla/javascript/Context", "toString", "(Ljava/lang/Object;)Ljava/lang/String;");
            cfw.add(3);
            cfw.addInvoke(182, "java/lang/String", "charAt", "(I)C");
            cfw.add(172);
            return;
        }
        if (retType.isPrimitive()) {
            cfw.addInvoke(184, "org/mozilla/javascript/Context", "toNumber", "(Ljava/lang/Object;)D");
            String typeName = retType.getName();
            switch (typeName.charAt(0)) {
                case 'b':
                case 'i':
                case 's':
                    cfw.add(142);
                    cfw.add(172);
                    return;
                case 'c':
                case 'e':
                case 'g':
                case 'h':
                case 'j':
                case 'k':
                case 'm':
                case 'n':
                case 'o':
                case 'p':
                case 'q':
                case 'r':
                default:
                    throw new RuntimeException("Unexpected return type " + retType);
                case 'd':
                    cfw.add(175);
                    return;
                case 'f':
                    cfw.add(144);
                    cfw.add(174);
                    return;
                case 'l':
                    cfw.add(143);
                    cfw.add(173);
                    return;
            }
        }
        String retTypeStr = retType.getName();
        if (callConvertResult) {
            cfw.addLoadConstant(retTypeStr);
            cfw.addInvoke(184, "java/lang/Class", "forName", "(Ljava/lang/String;)Ljava/lang/Class;");
            cfw.addInvoke(184, "org/mozilla/javascript/JavaAdapter", "convertResult", "(Ljava/lang/Object;Ljava/lang/Class;)Ljava/lang/Object;");
        }
        cfw.add(192, retTypeStr);
        cfw.add(176);
    }

    private static void generateMethod(ClassFileWriter cfw, String genName, String methodName, Class<?>[] parms, Class<?> returnType, boolean convertResult) {
        StringBuilder sb = new StringBuilder();
        int paramsEnd = appendMethodSignature(parms, returnType, sb);
        String methodSignature = sb.toString();
        cfw.startMethod(methodName, methodSignature, (short) 1);
        cfw.add(42);
        cfw.add(180, genName, "factory", "Lorg/mozilla/javascript/ContextFactory;");
        cfw.add(42);
        cfw.add(180, genName, "self", "Lorg/mozilla/javascript/Scriptable;");
        cfw.add(42);
        cfw.add(180, genName, "delegee", "Lorg/mozilla/javascript/Scriptable;");
        cfw.addPush(methodName);
        cfw.addInvoke(184, "org/mozilla/javascript/JavaAdapter", "getFunction", "(Lorg/mozilla/javascript/Scriptable;Ljava/lang/String;)Lorg/mozilla/javascript/Function;");
        generatePushWrappedArgs(cfw, parms, parms.length);
        if (parms.length > 64) {
            throw Context.reportRuntimeError0("JavaAdapter can not subclass methods with more then 64 arguments.");
        }
        long convertionMask = 0;
        for (int i = 0; i != parms.length; i++) {
            if (!parms[i].isPrimitive()) {
                convertionMask |= (long) (1 << i);
            }
        }
        cfw.addPush(convertionMask);
        cfw.addInvoke(184, "org/mozilla/javascript/JavaAdapter", "callMethod", "(Lorg/mozilla/javascript/ContextFactory;Lorg/mozilla/javascript/Scriptable;Lorg/mozilla/javascript/Function;[Ljava/lang/Object;J)Ljava/lang/Object;");
        generateReturnResult(cfw, returnType, convertResult);
        cfw.stopMethod((short) paramsEnd);
    }

    private static int generatePushParam(ClassFileWriter cfw, int paramOffset, Class<?> paramType) {
        if (!paramType.isPrimitive()) {
            cfw.addALoad(paramOffset);
            return 1;
        }
        String typeName = paramType.getName();
        switch (typeName.charAt(0)) {
            case 'b':
            case 'c':
            case 'i':
            case 's':
            case 'z':
                cfw.addILoad(paramOffset);
                return 1;
            case 'd':
                cfw.addDLoad(paramOffset);
                return 2;
            case 'e':
            case 'g':
            case 'h':
            case 'j':
            case 'k':
            case 'm':
            case 'n':
            case 'o':
            case 'p':
            case 'q':
            case 'r':
            case 't':
            case 'u':
            case 'v':
            case 'w':
            case 'x':
            case 'y':
            default:
                throw Kit.codeBug();
            case 'f':
                cfw.addFLoad(paramOffset);
                return 1;
            case 'l':
                cfw.addLLoad(paramOffset);
                return 2;
        }
    }

    private static void generatePopResult(ClassFileWriter cfw, Class<?> retType) {
        if (retType.isPrimitive()) {
            String typeName = retType.getName();
            switch (typeName.charAt(0)) {
                case 'b':
                case 'c':
                case 'i':
                case 's':
                case 'z':
                    cfw.add(172);
                    break;
                case 'd':
                    cfw.add(175);
                    break;
                case 'f':
                    cfw.add(174);
                    break;
                case 'l':
                    cfw.add(173);
                    break;
            }
        }
        cfw.add(176);
    }

    private static void generateSuper(ClassFileWriter cfw, String genName, String superName, String methodName, String methodSignature, Class<?>[] parms, Class<?> returnType) {
        cfw.startMethod("super$" + methodName, methodSignature, (short) 1);
        cfw.add(25, 0);
        int paramOffset = 1;
        for (Class<?> parm : parms) {
            paramOffset += generatePushParam(cfw, paramOffset, parm);
        }
        cfw.addInvoke(183, superName, methodName, methodSignature);
        if (!returnType.equals(Void.TYPE)) {
            generatePopResult(cfw, returnType);
        } else {
            cfw.add(177);
        }
        cfw.stopMethod((short) (paramOffset + 1));
    }

    private static String getMethodSignature(Method method, Class<?>[] argTypes) {
        StringBuilder sb = new StringBuilder();
        appendMethodSignature(argTypes, method.getReturnType(), sb);
        return sb.toString();
    }

    static int appendMethodSignature(Class<?>[] argTypes, Class<?> returnType, StringBuilder sb) {
        sb.append('(');
        int firstLocal = 1 + argTypes.length;
        for (Class<?> type : argTypes) {
            appendTypeString(sb, type);
            if (type == Long.TYPE || type == Double.TYPE) {
                firstLocal++;
            }
        }
        sb.append(')');
        appendTypeString(sb, returnType);
        return firstLocal;
    }

    private static StringBuilder appendTypeString(StringBuilder sb, Class<?> type) {
        char typeLetter;
        while (type.isArray()) {
            sb.append('[');
            type = type.getComponentType();
        }
        if (type.isPrimitive()) {
            if (type == Boolean.TYPE) {
                typeLetter = 'Z';
            } else if (type == Long.TYPE) {
                typeLetter = 'J';
            } else {
                String typeName = type.getName();
                typeLetter = Character.toUpperCase(typeName.charAt(0));
            }
            sb.append(typeLetter);
        } else {
            sb.append('L');
            sb.append(type.getName().replace('.', '/'));
            sb.append(';');
        }
        return sb;
    }

    static int[] getArgsToConvert(Class<?>[] argTypes) {
        int count = 0;
        for (int i = 0; i != argTypes.length; i++) {
            if (!argTypes[i].isPrimitive()) {
                count++;
            }
        }
        if (count == 0) {
            return null;
        }
        int[] array = new int[count];
        int count2 = 0;
        for (int i2 = 0; i2 != argTypes.length; i2++) {
            if (!argTypes[i2].isPrimitive()) {
                int i3 = count2;
                count2++;
                array[i3] = i2;
            }
        }
        return array;
    }
}
