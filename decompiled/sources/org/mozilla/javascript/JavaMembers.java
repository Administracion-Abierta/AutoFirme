package org.mozilla.javascript;

import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Member;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/* JADX INFO: loaded from: autofirma.jar:org/mozilla/javascript/JavaMembers.class */
class JavaMembers {
    private Class<?> cl;
    private Map<String, Object> members;
    private Map<String, FieldAndMethods> fieldAndMethods;
    private Map<String, Object> staticMembers;
    private Map<String, FieldAndMethods> staticFieldAndMethods;
    NativeJavaMethod ctors;

    JavaMembers(Scriptable scope, Class<?> cl) {
        this(scope, cl, false);
    }

    JavaMembers(Scriptable scope, Class<?> cl, boolean includeProtected) {
        try {
            Context cx = ContextFactory.getGlobal().enterContext();
            ClassShutter shutter = cx.getClassShutter();
            if (shutter != null && !shutter.visibleToScripts(cl.getName())) {
                throw Context.reportRuntimeError1("msg.access.prohibited", cl.getName());
            }
            this.members = new HashMap();
            this.staticMembers = new HashMap();
            this.cl = cl;
            boolean includePrivate = cx.hasFeature(13);
            reflect(scope, includeProtected, includePrivate);
            Context.exit();
        } catch (Throwable th) {
            Context.exit();
            throw th;
        }
    }

    boolean has(String name, boolean isStatic) {
        Map<String, Object> ht = isStatic ? this.staticMembers : this.members;
        Object obj = ht.get(name);
        return (obj == null && findExplicitFunction(name, isStatic) == null) ? false : true;
    }

    Object get(Scriptable scope, String name, Object javaObject, boolean isStatic) {
        Object rval;
        Class<?> type;
        Map<String, Object> ht = isStatic ? this.staticMembers : this.members;
        Object member = ht.get(name);
        if (!isStatic && member == null) {
            member = this.staticMembers.get(name);
        }
        if (member == null) {
            member = getExplicitFunction(scope, name, javaObject, isStatic);
            if (member == null) {
                return Scriptable.NOT_FOUND;
            }
        }
        if (member instanceof Scriptable) {
            return member;
        }
        Context cx = Context.getContext();
        try {
            if (member instanceof BeanProperty) {
                BeanProperty bp = (BeanProperty) member;
                if (bp.getter == null) {
                    return Scriptable.NOT_FOUND;
                }
                rval = bp.getter.invoke(javaObject, Context.emptyArgs);
                type = bp.getter.method().getReturnType();
            } else {
                Field field = (Field) member;
                rval = field.get(isStatic ? null : javaObject);
                type = field.getType();
            }
            return cx.getWrapFactory().wrap(cx, ScriptableObject.getTopLevelScope(scope), rval, type);
        } catch (Exception ex) {
            throw Context.throwAsScriptRuntimeEx(ex);
        }
    }

    void put(Scriptable scope, String name, Object javaObject, Object value, boolean isStatic) {
        Map<String, Object> ht = isStatic ? this.staticMembers : this.members;
        Object member = ht.get(name);
        if (!isStatic && member == null) {
            member = this.staticMembers.get(name);
        }
        if (member == null) {
            throw reportMemberNotFound(name);
        }
        if (member instanceof FieldAndMethods) {
            FieldAndMethods fam = (FieldAndMethods) ht.get(name);
            member = fam.field;
        }
        if (member instanceof BeanProperty) {
            BeanProperty bp = (BeanProperty) member;
            if (bp.setter == null) {
                throw reportMemberNotFound(name);
            }
            if (bp.setters == null || value == null) {
                Class<?> setType = bp.setter.argTypes[0];
                Object[] args = {Context.jsToJava(value, setType)};
                try {
                    bp.setter.invoke(javaObject, args);
                    return;
                } catch (Exception ex) {
                    throw Context.throwAsScriptRuntimeEx(ex);
                }
            }
            Object[] args2 = {value};
            bp.setters.call(Context.getContext(), ScriptableObject.getTopLevelScope(scope), scope, args2);
            return;
        }
        if (!(member instanceof Field)) {
            String str = member == null ? "msg.java.internal.private" : "msg.java.method.assign";
            throw Context.reportRuntimeError1(str, name);
        }
        Field field = (Field) member;
        Object javaValue = Context.jsToJava(value, field.getType());
        try {
            field.set(javaObject, javaValue);
        } catch (IllegalAccessException accessEx) {
            if ((field.getModifiers() & 16) != 0) {
            } else {
                throw Context.throwAsScriptRuntimeEx(accessEx);
            }
        } catch (IllegalArgumentException e) {
            throw Context.reportRuntimeError3("msg.java.internal.field.type", value.getClass().getName(), field, javaObject.getClass().getName());
        }
    }

    Object[] getIds(boolean isStatic) {
        Map<String, Object> map = isStatic ? this.staticMembers : this.members;
        return map.keySet().toArray(new Object[map.size()]);
    }

    static String javaSignature(Class<?> type) {
        if (!type.isArray()) {
            return type.getName();
        }
        int arrayDimension = 0;
        do {
            arrayDimension++;
            type = type.getComponentType();
        } while (type.isArray());
        String name = type.getName();
        if (arrayDimension != 1) {
            int length = name.length() + (arrayDimension * "[]".length());
            StringBuilder sb = new StringBuilder(length);
            sb.append(name);
            while (arrayDimension != 0) {
                arrayDimension--;
                sb.append("[]");
            }
            return sb.toString();
        }
        return name.concat("[]");
    }

    static String liveConnectSignature(Class<?>[] argTypes) {
        int N = argTypes.length;
        if (N == 0) {
            return "()";
        }
        StringBuilder sb = new StringBuilder();
        sb.append('(');
        for (int i = 0; i != N; i++) {
            if (i != 0) {
                sb.append(',');
            }
            sb.append(javaSignature(argTypes[i]));
        }
        sb.append(')');
        return sb.toString();
    }

    private MemberBox findExplicitFunction(String name, boolean isStatic) {
        int sigStart = name.indexOf(40);
        if (sigStart < 0) {
            return null;
        }
        Map<String, Object> ht = isStatic ? this.staticMembers : this.members;
        MemberBox[] methodsOrCtors = null;
        boolean isCtor = isStatic && sigStart == 0;
        if (isCtor) {
            methodsOrCtors = this.ctors.methods;
        } else {
            String trueName = name.substring(0, sigStart);
            Object obj = ht.get(trueName);
            if (!isStatic && obj == null) {
                obj = this.staticMembers.get(trueName);
            }
            if (obj instanceof NativeJavaMethod) {
                NativeJavaMethod njm = (NativeJavaMethod) obj;
                methodsOrCtors = njm.methods;
            }
        }
        if (methodsOrCtors != null) {
            for (MemberBox methodsOrCtor : methodsOrCtors) {
                Class<?>[] type = methodsOrCtor.argTypes;
                String sig = liveConnectSignature(type);
                if (sigStart + sig.length() == name.length() && name.regionMatches(sigStart, sig, 0, sig.length())) {
                    return methodsOrCtor;
                }
            }
            return null;
        }
        return null;
    }

    private Object getExplicitFunction(Scriptable scope, String name, Object javaObject, boolean isStatic) {
        Map<String, Object> ht = isStatic ? this.staticMembers : this.members;
        Object member = null;
        MemberBox methodOrCtor = findExplicitFunction(name, isStatic);
        if (methodOrCtor != null) {
            Scriptable prototype = ScriptableObject.getFunctionPrototype(scope);
            if (methodOrCtor.isCtor()) {
                NativeJavaConstructor fun = new NativeJavaConstructor(methodOrCtor);
                fun.setPrototype(prototype);
                member = fun;
                ht.put(name, fun);
            } else {
                String trueName = methodOrCtor.getName();
                member = ht.get(trueName);
                if ((member instanceof NativeJavaMethod) && ((NativeJavaMethod) member).methods.length > 1) {
                    NativeJavaMethod fun2 = new NativeJavaMethod(methodOrCtor, name);
                    fun2.setPrototype(prototype);
                    ht.put(name, fun2);
                    member = fun2;
                }
            }
        }
        return member;
    }

    private static Method[] discoverAccessibleMethods(Class<?> clazz, boolean includeProtected, boolean includePrivate) {
        Map<MethodSignature, Method> map = new HashMap<>();
        discoverAccessibleMethods(clazz, map, includeProtected, includePrivate);
        return (Method[]) map.values().toArray(new Method[map.size()]);
    }

    private static void discoverAccessibleMethods(Class<?> clazz, Map<MethodSignature, Method> map, boolean includeProtected, boolean includePrivate) {
        if (Modifier.isPublic(clazz.getModifiers()) || includePrivate) {
            try {
                if (includeProtected || includePrivate) {
                    while (clazz != null) {
                        try {
                            Method[] methods = clazz.getDeclaredMethods();
                            for (Method method : methods) {
                                int mods = method.getModifiers();
                                if (Modifier.isPublic(mods) || Modifier.isProtected(mods) || includePrivate) {
                                    MethodSignature sig = new MethodSignature(method);
                                    if (!map.containsKey(sig)) {
                                        if (includePrivate && !method.isAccessible()) {
                                            method.setAccessible(true);
                                        }
                                        map.put(sig, method);
                                    }
                                }
                            }
                            Class<?>[] interfaces = clazz.getInterfaces();
                            for (Class<?> intface : interfaces) {
                                discoverAccessibleMethods(intface, map, includeProtected, includePrivate);
                            }
                            clazz = clazz.getSuperclass();
                        } catch (SecurityException e) {
                            Method[] methods2 = clazz.getMethods();
                            for (Method method2 : methods2) {
                                MethodSignature sig2 = new MethodSignature(method2);
                                if (!map.containsKey(sig2)) {
                                    map.put(sig2, method2);
                                }
                            }
                            return;
                        }
                    }
                    return;
                }
                Method[] methods3 = clazz.getMethods();
                for (Method method3 : methods3) {
                    MethodSignature sig3 = new MethodSignature(method3);
                    if (!map.containsKey(sig3)) {
                        map.put(sig3, method3);
                    }
                }
                return;
            } catch (SecurityException e2) {
                Context.reportWarning("Could not discover accessible methods of class " + clazz.getName() + " due to lack of privileges, attemping superclasses/interfaces.");
            }
        }
        Class<?>[] interfaces2 = clazz.getInterfaces();
        for (Class<?> intface2 : interfaces2) {
            discoverAccessibleMethods(intface2, map, includeProtected, includePrivate);
        }
        Class<?> superclass = clazz.getSuperclass();
        if (superclass != null) {
            discoverAccessibleMethods(superclass, map, includeProtected, includePrivate);
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:org/mozilla/javascript/JavaMembers$MethodSignature.class */
    private static final class MethodSignature {
        private final String name;
        private final Class<?>[] args;

        private MethodSignature(String name, Class<?>[] args) {
            this.name = name;
            this.args = args;
        }

        MethodSignature(Method method) {
            this(method.getName(), method.getParameterTypes());
        }

        public boolean equals(Object o) {
            if (o instanceof MethodSignature) {
                MethodSignature ms = (MethodSignature) o;
                return ms.name.equals(this.name) && Arrays.equals(this.args, ms.args);
            }
            return false;
        }

        public int hashCode() {
            return this.name.hashCode() ^ this.args.length;
        }
    }

    private void reflect(Scriptable scope, boolean includeProtected, boolean includePrivate) {
        Object v;
        MemberBox[] methodBoxes;
        ObjArray overloadedMethods;
        Method[] methods = discoverAccessibleMethods(this.cl, includeProtected, includePrivate);
        for (Method method : methods) {
            int mods = method.getModifiers();
            Map<String, Object> ht = Modifier.isStatic(mods) ? this.staticMembers : this.members;
            String name = method.getName();
            Object value = ht.get(name);
            if (value == null) {
                ht.put(name, method);
            } else {
                if (value instanceof ObjArray) {
                    overloadedMethods = (ObjArray) value;
                } else {
                    if (!(value instanceof Method)) {
                        Kit.codeBug();
                    }
                    overloadedMethods = new ObjArray();
                    overloadedMethods.add(value);
                    ht.put(name, overloadedMethods);
                }
                overloadedMethods.add(method);
            }
        }
        int tableCursor = 0;
        while (tableCursor != 2) {
            Map<String, Object> ht2 = tableCursor == 0 ? this.staticMembers : this.members;
            for (Map.Entry<String, Object> entry : ht2.entrySet()) {
                Object value2 = entry.getValue();
                if (value2 instanceof Method) {
                    methodBoxes = new MemberBox[]{new MemberBox((Method) value2)};
                } else {
                    ObjArray overloadedMethods2 = (ObjArray) value2;
                    int N = overloadedMethods2.size();
                    if (N < 2) {
                        Kit.codeBug();
                    }
                    methodBoxes = new MemberBox[N];
                    for (int i = 0; i != N; i++) {
                        Method method2 = (Method) overloadedMethods2.get(i);
                        methodBoxes[i] = new MemberBox(method2);
                    }
                }
                NativeJavaMethod fun = new NativeJavaMethod(methodBoxes);
                if (scope != null) {
                    ScriptRuntime.setFunctionProtoAndParent(fun, scope);
                }
                ht2.put(entry.getKey(), fun);
            }
            tableCursor++;
        }
        Field[] fields = getAccessibleFields(includeProtected, includePrivate);
        for (Field field : fields) {
            String name2 = field.getName();
            int mods2 = field.getModifiers();
            try {
                boolean isStatic = Modifier.isStatic(mods2);
                Map<String, Object> ht3 = isStatic ? this.staticMembers : this.members;
                Object member = ht3.get(name2);
                if (member == null) {
                    ht3.put(name2, field);
                } else if (member instanceof NativeJavaMethod) {
                    NativeJavaMethod method3 = (NativeJavaMethod) member;
                    FieldAndMethods fam = new FieldAndMethods(scope, method3.methods, field);
                    Map<String, FieldAndMethods> fmht = isStatic ? this.staticFieldAndMethods : this.fieldAndMethods;
                    if (fmht == null) {
                        fmht = new HashMap();
                        if (isStatic) {
                            this.staticFieldAndMethods = fmht;
                        } else {
                            this.fieldAndMethods = fmht;
                        }
                    }
                    fmht.put(name2, fam);
                    ht3.put(name2, fam);
                } else if (member instanceof Field) {
                    Field oldField = (Field) member;
                    if (oldField.getDeclaringClass().isAssignableFrom(field.getDeclaringClass())) {
                        ht3.put(name2, field);
                    }
                } else {
                    Kit.codeBug();
                }
            } catch (SecurityException e) {
                Context.reportWarning("Could not access field " + name2 + " of class " + this.cl.getName() + " due to lack of privileges.");
            }
        }
        int tableCursor2 = 0;
        while (tableCursor2 != 2) {
            boolean isStatic2 = tableCursor2 == 0;
            Map<String, Object> ht4 = isStatic2 ? this.staticMembers : this.members;
            Map<String, BeanProperty> toAdd = new HashMap<>();
            for (String name3 : ht4.keySet()) {
                boolean memberIsGetMethod = name3.startsWith("get");
                boolean memberIsSetMethod = name3.startsWith("set");
                boolean memberIsIsMethod = name3.startsWith("is");
                if (memberIsGetMethod || memberIsIsMethod || memberIsSetMethod) {
                    String nameComponent = name3.substring(memberIsIsMethod ? 2 : 3);
                    if (nameComponent.length() != 0) {
                        String beanPropertyName = nameComponent;
                        char ch0 = nameComponent.charAt(0);
                        if (Character.isUpperCase(ch0)) {
                            if (nameComponent.length() == 1) {
                                beanPropertyName = nameComponent.toLowerCase();
                            } else {
                                char ch1 = nameComponent.charAt(1);
                                if (!Character.isUpperCase(ch1)) {
                                    beanPropertyName = Character.toLowerCase(ch0) + nameComponent.substring(1);
                                }
                            }
                        }
                        if (!toAdd.containsKey(beanPropertyName) && ((v = ht4.get(beanPropertyName)) == null || (includePrivate && (v instanceof Member) && Modifier.isPrivate(((Member) v).getModifiers())))) {
                            MemberBox getter = findGetter(isStatic2, ht4, "get", nameComponent);
                            if (getter == null) {
                                getter = findGetter(isStatic2, ht4, "is", nameComponent);
                            }
                            MemberBox setter = null;
                            NativeJavaMethod setters = null;
                            String setterName = "set".concat(nameComponent);
                            if (ht4.containsKey(setterName)) {
                                Object member2 = ht4.get(setterName);
                                if (member2 instanceof NativeJavaMethod) {
                                    NativeJavaMethod njmSet = (NativeJavaMethod) member2;
                                    if (getter != null) {
                                        Class<?> type = getter.method().getReturnType();
                                        setter = extractSetMethod(type, njmSet.methods, isStatic2);
                                    } else {
                                        setter = extractSetMethod(njmSet.methods, isStatic2);
                                    }
                                    if (njmSet.methods.length > 1) {
                                        setters = njmSet;
                                    }
                                }
                            }
                            BeanProperty bp = new BeanProperty(getter, setter, setters);
                            toAdd.put(beanPropertyName, bp);
                        }
                    }
                }
            }
            ht4.putAll(toAdd);
            tableCursor2++;
        }
        Constructor<?>[] constructors = getAccessibleConstructors(includePrivate);
        MemberBox[] ctorMembers = new MemberBox[constructors.length];
        for (int i2 = 0; i2 != constructors.length; i2++) {
            ctorMembers[i2] = new MemberBox(constructors[i2]);
        }
        this.ctors = new NativeJavaMethod(ctorMembers, this.cl.getSimpleName());
    }

    private Constructor<?>[] getAccessibleConstructors(boolean includePrivate) {
        if (includePrivate && this.cl != ScriptRuntime.ClassClass) {
            try {
                Constructor<?>[] cons = this.cl.getDeclaredConstructors();
                AccessibleObject.setAccessible(cons, true);
                return cons;
            } catch (SecurityException e) {
                Context.reportWarning("Could not access constructor  of class " + this.cl.getName() + " due to lack of privileges.");
            }
        }
        return this.cl.getConstructors();
    }

    private Field[] getAccessibleFields(boolean includeProtected, boolean includePrivate) {
        if (includePrivate || includeProtected) {
            try {
                List<Field> fieldsList = new ArrayList<>();
                for (Class<?> currentClass = this.cl; currentClass != null; currentClass = currentClass.getSuperclass()) {
                    Field[] declared = currentClass.getDeclaredFields();
                    for (Field field : declared) {
                        int mod = field.getModifiers();
                        if (includePrivate || Modifier.isPublic(mod) || Modifier.isProtected(mod)) {
                            if (!field.isAccessible()) {
                                field.setAccessible(true);
                            }
                            fieldsList.add(field);
                        }
                    }
                }
                return (Field[]) fieldsList.toArray(new Field[fieldsList.size()]);
            } catch (SecurityException e) {
            }
        }
        return this.cl.getFields();
    }

    private static MemberBox findGetter(boolean isStatic, Map<String, Object> ht, String prefix, String propertyName) {
        String getterName = prefix.concat(propertyName);
        if (ht.containsKey(getterName)) {
            Object member = ht.get(getterName);
            if (member instanceof NativeJavaMethod) {
                NativeJavaMethod njmGet = (NativeJavaMethod) member;
                return extractGetMethod(njmGet.methods, isStatic);
            }
            return null;
        }
        return null;
    }

    private static MemberBox extractGetMethod(MemberBox[] methods, boolean isStatic) {
        for (MemberBox method : methods) {
            if (method.argTypes.length == 0 && (!isStatic || method.isStatic())) {
                Class<?> type = method.method().getReturnType();
                if (type != Void.TYPE) {
                    return method;
                }
                return null;
            }
        }
        return null;
    }

    private static MemberBox extractSetMethod(Class<?> type, MemberBox[] methods, boolean isStatic) {
        for (int pass = 1; pass <= 2; pass++) {
            for (MemberBox method : methods) {
                if (!isStatic || method.isStatic()) {
                    Class<?>[] params = method.argTypes;
                    if (params.length != 1) {
                        continue;
                    } else if (pass == 1) {
                        if (params[0] == type) {
                            return method;
                        }
                    } else {
                        if (pass != 2) {
                            Kit.codeBug();
                        }
                        if (params[0].isAssignableFrom(type)) {
                            return method;
                        }
                    }
                }
            }
        }
        return null;
    }

    private static MemberBox extractSetMethod(MemberBox[] methods, boolean isStatic) {
        for (MemberBox method : methods) {
            if ((!isStatic || method.isStatic()) && method.method().getReturnType() == Void.TYPE && method.argTypes.length == 1) {
                return method;
            }
        }
        return null;
    }

    Map<String, FieldAndMethods> getFieldAndMethodsObjects(Scriptable scope, Object javaObject, boolean isStatic) {
        Map<String, FieldAndMethods> ht = isStatic ? this.staticFieldAndMethods : this.fieldAndMethods;
        if (ht == null) {
            return null;
        }
        int len = ht.size();
        Map<String, FieldAndMethods> result = new HashMap<>(len);
        for (FieldAndMethods fam : ht.values()) {
            FieldAndMethods famNew = new FieldAndMethods(scope, fam.methods, fam.field);
            famNew.javaObject = javaObject;
            result.put(fam.field.getName(), famNew);
        }
        return result;
    }

    static JavaMembers lookupClass(Scriptable scope, Class<?> dynamicType, Class<?> staticType, boolean includeProtected) {
        ClassCache cache = ClassCache.get(scope);
        Map<Class<?>, JavaMembers> ct = cache.getClassCacheMap();
        Class<?> cl = dynamicType;
        while (true) {
            JavaMembers members = ct.get(cl);
            if (members != null) {
                if (cl != dynamicType) {
                    ct.put(dynamicType, members);
                }
                return members;
            }
            try {
                JavaMembers members2 = new JavaMembers(cache.getAssociatedScope(), cl, includeProtected);
                if (cache.isCachingEnabled()) {
                    ct.put(cl, members2);
                    if (cl != dynamicType) {
                        ct.put(dynamicType, members2);
                    }
                }
                return members2;
            } catch (SecurityException e) {
                if (staticType != null && staticType.isInterface()) {
                    cl = staticType;
                    staticType = null;
                } else {
                    Class<?> parent = cl.getSuperclass();
                    if (parent == null) {
                        if (cl.isInterface()) {
                            parent = ScriptRuntime.ObjectClass;
                        } else {
                            throw e;
                        }
                    }
                    cl = parent;
                }
            }
        }
    }

    RuntimeException reportMemberNotFound(String memberName) {
        return Context.reportRuntimeError2("msg.java.member.not.found", this.cl.getName(), memberName);
    }
}
