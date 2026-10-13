package org.mozilla.javascript;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Member;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

/* JADX INFO: loaded from: autofirma.jar:org/mozilla/javascript/MemberBox.class */
final class MemberBox implements Serializable {
    private static final long serialVersionUID = 6358550398665688245L;
    private transient Member memberObject;
    transient Class<?>[] argTypes;
    transient Object delegateTo;
    transient boolean vararg;
    private static final Class<?>[] primitives = {Boolean.TYPE, Byte.TYPE, Character.TYPE, Double.TYPE, Float.TYPE, Integer.TYPE, Long.TYPE, Short.TYPE, Void.TYPE};

    MemberBox(Method method) {
        init(method);
    }

    MemberBox(Constructor<?> constructor) {
        init(constructor);
    }

    private void init(Method method) {
        this.memberObject = method;
        this.argTypes = method.getParameterTypes();
        this.vararg = method.isVarArgs();
    }

    private void init(Constructor<?> constructor) {
        this.memberObject = constructor;
        this.argTypes = constructor.getParameterTypes();
        this.vararg = constructor.isVarArgs();
    }

    Method method() {
        return (Method) this.memberObject;
    }

    Constructor<?> ctor() {
        return (Constructor) this.memberObject;
    }

    Member member() {
        return this.memberObject;
    }

    boolean isMethod() {
        return this.memberObject instanceof Method;
    }

    boolean isCtor() {
        return this.memberObject instanceof Constructor;
    }

    boolean isStatic() {
        return Modifier.isStatic(this.memberObject.getModifiers());
    }

    boolean isPublic() {
        return Modifier.isPublic(this.memberObject.getModifiers());
    }

    String getName() {
        return this.memberObject.getName();
    }

    Class<?> getDeclaringClass() {
        return this.memberObject.getDeclaringClass();
    }

    String toJavaDeclaration() {
        StringBuilder sb = new StringBuilder();
        if (isMethod()) {
            Method method = method();
            sb.append(method.getReturnType());
            sb.append(' ');
            sb.append(method.getName());
        } else {
            Constructor<?> ctor = ctor();
            String name = ctor.getDeclaringClass().getName();
            int lastDot = name.lastIndexOf(46);
            if (lastDot >= 0) {
                name = name.substring(lastDot + 1);
            }
            sb.append(name);
        }
        sb.append(JavaMembers.liveConnectSignature(this.argTypes));
        return sb.toString();
    }

    public String toString() {
        return this.memberObject.toString();
    }

    Object invoke(Object target, Object[] args) {
        Method method = method();
        try {
            try {
                return method.invoke(target, args);
            } catch (IllegalAccessException ex) {
                Method accessible = searchAccessibleMethod(method, this.argTypes);
                if (accessible != null) {
                    this.memberObject = accessible;
                    method = accessible;
                } else if (!VMBridge.instance.tryToMakeAccessible(method)) {
                    throw Context.throwAsScriptRuntimeEx(ex);
                }
                return method.invoke(target, args);
            }
        } catch (InvocationTargetException ite) {
            Throwable e = ite;
            do {
                e = ((InvocationTargetException) e).getTargetException();
            } while (e instanceof InvocationTargetException);
            if (e instanceof ContinuationPending) {
                throw ((ContinuationPending) e);
            }
            throw Context.throwAsScriptRuntimeEx(e);
        } catch (Exception ex2) {
            throw Context.throwAsScriptRuntimeEx(ex2);
        }
    }

    Object newInstance(Object[] args) {
        Constructor<?> ctor = ctor();
        try {
            try {
                return ctor.newInstance(args);
            } catch (IllegalAccessException ex) {
                if (!VMBridge.instance.tryToMakeAccessible(ctor)) {
                    throw Context.throwAsScriptRuntimeEx(ex);
                }
                return ctor.newInstance(args);
            }
        } catch (Exception ex2) {
            throw Context.throwAsScriptRuntimeEx(ex2);
        }
    }

    private static Method searchAccessibleMethod(Method method, Class<?>[] params) {
        int modifiers = method.getModifiers();
        if (Modifier.isPublic(modifiers) && !Modifier.isStatic(modifiers)) {
            Class<?> c = method.getDeclaringClass();
            if (!Modifier.isPublic(c.getModifiers())) {
                String name = method.getName();
                Class<?>[] intfs = c.getInterfaces();
                int N = intfs.length;
                for (int i = 0; i != N; i++) {
                    Class<?> intf = intfs[i];
                    if (Modifier.isPublic(intf.getModifiers())) {
                        try {
                            return intf.getMethod(name, params);
                        } catch (NoSuchMethodException e) {
                        } catch (SecurityException e2) {
                        }
                    }
                }
                while (true) {
                    c = c.getSuperclass();
                    if (c != null) {
                        if (Modifier.isPublic(c.getModifiers())) {
                            try {
                                Method m = c.getMethod(name, params);
                                int mModifiers = m.getModifiers();
                                if (Modifier.isPublic(mModifiers) && !Modifier.isStatic(mModifiers)) {
                                    return m;
                                }
                            } catch (NoSuchMethodException e3) {
                            } catch (SecurityException e4) {
                            }
                        }
                    } else {
                        return null;
                    }
                }
            } else {
                return null;
            }
        } else {
            return null;
        }
    }

    private void readObject(ObjectInputStream in) throws ClassNotFoundException, IOException {
        in.defaultReadObject();
        Member member = readMember(in);
        if (member instanceof Method) {
            init((Method) member);
        } else {
            init((Constructor<?>) member);
        }
    }

    private void writeObject(ObjectOutputStream out) throws IOException {
        out.defaultWriteObject();
        writeMember(out, this.memberObject);
    }

    private static void writeMember(ObjectOutputStream out, Member member) throws IOException {
        if (member == null) {
            out.writeBoolean(false);
            return;
        }
        out.writeBoolean(true);
        if (!(member instanceof Method) && !(member instanceof Constructor)) {
            throw new IllegalArgumentException("not Method or Constructor");
        }
        out.writeBoolean(member instanceof Method);
        out.writeObject(member.getName());
        out.writeObject(member.getDeclaringClass());
        if (member instanceof Method) {
            writeParameters(out, ((Method) member).getParameterTypes());
        } else {
            writeParameters(out, ((Constructor) member).getParameterTypes());
        }
    }

    private static Member readMember(ObjectInputStream in) throws IOException, ClassNotFoundException {
        if (!in.readBoolean()) {
            return null;
        }
        boolean isMethod = in.readBoolean();
        String name = (String) in.readObject();
        Class<?> declaring = (Class) in.readObject();
        Class<?>[] parms = readParameters(in);
        try {
            if (isMethod) {
                return declaring.getMethod(name, parms);
            }
            return declaring.getConstructor(parms);
        } catch (NoSuchMethodException e) {
            throw new IOException("Cannot find member: " + e);
        }
    }

    private static void writeParameters(ObjectOutputStream out, Class<?>[] parms) throws IOException {
        out.writeShort(parms.length);
        for (Class<?> parm : parms) {
            boolean primitive = parm.isPrimitive();
            out.writeBoolean(primitive);
            if (!primitive) {
                out.writeObject(parm);
            } else {
                int j = 0;
                while (true) {
                    if (j < primitives.length) {
                        if (!parm.equals(primitives[j])) {
                            j++;
                        } else {
                            out.writeByte(j);
                            break;
                        }
                    } else {
                        throw new IllegalArgumentException("Primitive " + parm + " not found");
                    }
                }
            }
        }
    }

    private static Class<?>[] readParameters(ObjectInputStream in) throws IOException, ClassNotFoundException {
        Class<?>[] result = new Class[in.readShort()];
        for (int i = 0; i < result.length; i++) {
            if (!in.readBoolean()) {
                result[i] = (Class) in.readObject();
            } else {
                result[i] = primitives[in.readByte()];
            }
        }
        return result;
    }
}
