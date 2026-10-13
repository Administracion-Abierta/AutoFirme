package org.mozilla.javascript;

import java.lang.reflect.Array;
import java.lang.reflect.Method;
import java.util.concurrent.CopyOnWriteArrayList;

/* JADX INFO: loaded from: autofirma.jar:org/mozilla/javascript/NativeJavaMethod.class */
public class NativeJavaMethod extends BaseFunction {
    private static final long serialVersionUID = -3440381785576412928L;
    private static final int PREFERENCE_EQUAL = 0;
    private static final int PREFERENCE_FIRST_ARG = 1;
    private static final int PREFERENCE_SECOND_ARG = 2;
    private static final int PREFERENCE_AMBIGUOUS = 3;
    private static final boolean debug = false;
    MemberBox[] methods;
    private String functionName;
    private final transient CopyOnWriteArrayList<ResolvedOverload> overloadCache;

    NativeJavaMethod(MemberBox[] methods) {
        this.overloadCache = new CopyOnWriteArrayList<>();
        this.functionName = methods[0].getName();
        this.methods = methods;
    }

    NativeJavaMethod(MemberBox[] methods, String name) {
        this.overloadCache = new CopyOnWriteArrayList<>();
        this.functionName = name;
        this.methods = methods;
    }

    NativeJavaMethod(MemberBox method, String name) {
        this.overloadCache = new CopyOnWriteArrayList<>();
        this.functionName = name;
        this.methods = new MemberBox[]{method};
    }

    public NativeJavaMethod(Method method, String name) {
        this(new MemberBox(method), name);
    }

    @Override // org.mozilla.javascript.BaseFunction
    public String getFunctionName() {
        return this.functionName;
    }

    static String scriptSignature(Object[] values) {
        String s;
        StringBuilder sig = new StringBuilder();
        for (int i = 0; i != values.length; i++) {
            Object value = values[i];
            if (value == null) {
                s = "null";
            } else if (value instanceof Boolean) {
                s = "boolean";
            } else if (value instanceof String) {
                s = "string";
            } else if (value instanceof Number) {
                s = "number";
            } else if (value instanceof Scriptable) {
                if (value instanceof Undefined) {
                    s = "undefined";
                } else if (value instanceof Wrapper) {
                    Object wrapped = ((Wrapper) value).unwrap();
                    s = wrapped.getClass().getName();
                } else if (value instanceof Function) {
                    s = "function";
                } else {
                    s = "object";
                }
            } else {
                s = JavaMembers.javaSignature(value.getClass());
            }
            if (i != 0) {
                sig.append(',');
            }
            sig.append(s);
        }
        return sig.toString();
    }

    @Override // org.mozilla.javascript.BaseFunction
    String decompile(int indent, int flags) {
        StringBuilder sb = new StringBuilder();
        boolean justbody = 0 != (flags & 1);
        if (!justbody) {
            sb.append("function ");
            sb.append(getFunctionName());
            sb.append("() {");
        }
        sb.append("/*\n");
        sb.append(toString());
        sb.append(justbody ? "*/\n" : "*/}\n");
        return sb.toString();
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        int N = this.methods.length;
        for (int i = 0; i != N; i++) {
            if (this.methods[i].isMethod()) {
                Method method = this.methods[i].method();
                sb.append(JavaMembers.javaSignature(method.getReturnType()));
                sb.append(' ');
                sb.append(method.getName());
            } else {
                sb.append(this.methods[i].getName());
            }
            sb.append(JavaMembers.liveConnectSignature(this.methods[i].argTypes));
            sb.append('\n');
        }
        return sb.toString();
    }

    @Override // org.mozilla.javascript.BaseFunction, org.mozilla.javascript.Function, org.mozilla.javascript.Callable
    public Object call(Context cx, Scriptable scope, Scriptable thisObj, Object[] args) {
        Object javaObject;
        Object varArgs;
        if (this.methods.length == 0) {
            throw new RuntimeException("No methods defined for call");
        }
        int index = findCachedFunction(cx, args);
        if (index < 0) {
            String sig = this.methods[0].method().getDeclaringClass().getName() + '.' + getFunctionName() + '(' + scriptSignature(args) + ')';
            throw Context.reportRuntimeError1("msg.java.no_such_method", sig);
        }
        MemberBox meth = this.methods[index];
        Class<?>[] argTypes = meth.argTypes;
        if (meth.vararg) {
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
                Object coerced = Context.jsToJava(arg, argTypes[i3]);
                if (coerced != arg) {
                    if (args == args) {
                        args = (Object[]) args.clone();
                    }
                    args[i3] = coerced;
                }
            }
        }
        if (meth.isStatic()) {
            javaObject = null;
        } else {
            Scriptable o = thisObj;
            Class<?> c = meth.getDeclaringClass();
            while (true) {
                if (o == null) {
                    throw Context.reportRuntimeError3("msg.nonjava.method", getFunctionName(), ScriptRuntime.toString(thisObj), c.getName());
                }
                if (o instanceof Wrapper) {
                    javaObject = ((Wrapper) o).unwrap();
                    if (c.isInstance(javaObject)) {
                        break;
                    }
                }
                o = o.getPrototype();
            }
        }
        Object retval = meth.invoke(javaObject, args);
        Class<?> staticType = meth.method().getReturnType();
        Object wrapped = cx.getWrapFactory().wrap(cx, scope, retval, staticType);
        if (wrapped == null && staticType == Void.TYPE) {
            wrapped = Undefined.instance;
        }
        return wrapped;
    }

    int findCachedFunction(Context cx, Object[] args) {
        if (this.methods.length > 1) {
            for (ResolvedOverload ovl : this.overloadCache) {
                if (ovl.matches(args)) {
                    return ovl.index;
                }
            }
            int index = findFunction(cx, this.methods, args);
            if (this.overloadCache.size() < this.methods.length * 2) {
                this.overloadCache.addIfAbsent(new ResolvedOverload(args, index));
            }
            return index;
        }
        return findFunction(cx, this.methods, args);
    }

    /* JADX WARN: Code duplicated, block: B:123:0x01c7 A[ADDED_TO_REGION, EDGE_INSN: B:123:0x01c7->B:95:0x01c7 BREAK  A[LOOP:2: B:40:0x00a1->B:45:0x00ba], REMOVE, SYNTHETIC] */
    /* JADX WARN: Code duplicated, block: B:130:0x01c7 A[EDGE_INSN: B:130:0x01c7->B:95:0x01c7 BREAK  A[LOOP:2: B:40:0x00a1->B:45:0x00ba], SYNTHETIC] */
    /* JADX WARN: Code duplicated, block: B:131:0x01c7 A[EDGE_INSN: B:131:0x01c7->B:95:0x01c7 BREAK  A[LOOP:2: B:40:0x00a1->B:45:0x00ba], SYNTHETIC] */
    /* JADX WARN: Code duplicated, block: B:132:0x00c0 A[SYNTHETIC] */
    /* JADX WARN: Code duplicated, block: B:133:0x0192 A[SYNTHETIC] */
    /* JADX WARN: Code duplicated, block: B:134:0x0155 A[SYNTHETIC] */
    /* JADX WARN: Code duplicated, block: B:135:0x0192 A[SYNTHETIC] */
    /* JADX WARN: Code duplicated, block: B:39:0x009e A[PHI: r15
      0x009e: PHI (r15v2 'alength' int) = (r15v1 'alength' int), (r15v3 'alength' int) binds: [B:37:0x0098, B:34:0x008e] A[DONT_GENERATE, DONT_INLINE]] */
    /* JADX WARN: Code duplicated, block: B:42:0x00a8  */
    /* JADX WARN: Code duplicated, block: B:45:0x00ba A[LOOP:2: B:40:0x00a1->B:45:0x00ba, LOOP_END] */
    /* JADX WARN: Code duplicated, block: B:48:0x00c4  */
    /* JADX WARN: Code duplicated, block: B:49:0x00ca  */
    /* JADX WARN: Code duplicated, block: B:52:0x00da  */
    /* JADX WARN: Code duplicated, block: B:54:0x00e0  */
    /* JADX WARN: Code duplicated, block: B:55:0x00e6  */
    /* JADX WARN: Code duplicated, block: B:64:0x011d  */
    /* JADX WARN: Code duplicated, block: B:67:0x013d  */
    /* JADX WARN: Code duplicated, block: B:69:0x0143  */
    /* JADX WARN: Code duplicated, block: B:70:0x0149  */
    /* JADX WARN: Code duplicated, block: B:72:0x014f  */
    /* JADX WARN: Code duplicated, block: B:75:0x015a  */
    /* JADX WARN: Code duplicated, block: B:78:0x0166  */
    /* JADX WARN: Code duplicated, block: B:87:0x019b  */
    /* JADX WARN: Code duplicated, block: B:88:0x01a4  */
    /* JADX WARN: Code duplicated, block: B:91:0x01b0  */
    /* JADX WARN: Code duplicated, block: B:93:0x01b5  */
    static int findFunction(Context cx, MemberBox[] methodsOrCtors, Object[] args) {
        int i;
        int j;
        int betterCount;
        int worseCount;
        int j2;
        int bestFitIndex;
        MemberBox bestFit;
        int preference;
        if (methodsOrCtors.length == 0) {
            return -1;
        }
        if (methodsOrCtors.length == 1) {
            MemberBox member = methodsOrCtors[0];
            Class<?>[] argTypes = member.argTypes;
            int alength = argTypes.length;
            if (member.vararg) {
                alength--;
                if (alength > args.length) {
                    return -1;
                }
            } else if (alength != args.length) {
                return -1;
            }
            for (int j3 = 0; j3 != alength; j3++) {
                if (!NativeJavaObject.canConvert(args[j3], argTypes[j3])) {
                    return -1;
                }
            }
            return 0;
        }
        int firstBestFit = -1;
        int[] extraBestFits = null;
        int extraBestFitsCount = 0;
        for (int i2 = 0; i2 < methodsOrCtors.length; i2++) {
            MemberBox member2 = methodsOrCtors[i2];
            Class<?>[] argTypes2 = member2.argTypes;
            int alength2 = argTypes2.length;
            if (member2.vararg) {
                alength2--;
                if (alength2 <= args.length) {
                    j = 0;
                    while (true) {
                        if (j < alength2) {
                            if (!NativeJavaObject.canConvert(args[j], argTypes2[j])) {
                                break;
                            }
                            j++;
                        } else {
                            if (firstBestFit < 0) {
                                firstBestFit = i2;
                                break;
                            }
                            betterCount = 0;
                            worseCount = 0;
                            j2 = -1;
                            while (true) {
                                if (j2 == extraBestFitsCount) {
                                    if (j2 == -1) {
                                        bestFitIndex = firstBestFit;
                                    } else {
                                        bestFitIndex = extraBestFits[j2];
                                    }
                                    bestFit = methodsOrCtors[bestFitIndex];
                                    if (!cx.hasFeature(13) && bestFit.isPublic() != member2.isPublic()) {
                                        if (!bestFit.isPublic()) {
                                            betterCount++;
                                        } else {
                                            worseCount++;
                                        }
                                    } else {
                                        preference = preferSignature(args, argTypes2, member2.vararg, bestFit.argTypes, bestFit.vararg);
                                        if (preference != 3) {
                                            if (preference == 1) {
                                                betterCount++;
                                            } else if (preference == 2) {
                                                worseCount++;
                                            } else {
                                                if (preference != 0) {
                                                    Kit.codeBug();
                                                }
                                                if (!bestFit.isStatic() && bestFit.getDeclaringClass().isAssignableFrom(member2.getDeclaringClass())) {
                                                    if (j2 == -1) {
                                                        firstBestFit = i2;
                                                        break;
                                                    }
                                                    extraBestFits[j2] = i2;
                                                    break;
                                                }
                                                break;
                                            }
                                        }
                                    }
                                    j2++;
                                }
                                if (betterCount == 1 + extraBestFitsCount) {
                                    firstBestFit = i2;
                                    extraBestFitsCount = 0;
                                    break;
                                }
                                if (worseCount != 1 + extraBestFitsCount) {
                                    break;
                                }
                                if (extraBestFits == null) {
                                    extraBestFits = new int[methodsOrCtors.length - 1];
                                }
                                extraBestFits[extraBestFitsCount] = i2;
                                extraBestFitsCount++;
                                break;
                            }
                        }
                    }
                }
            } else if (alength2 == args.length) {
                j = 0;
                while (true) {
                    if (j < alength2) {
                        if (!NativeJavaObject.canConvert(args[j], argTypes2[j])) {
                            break;
                            break;
                        }
                        j++;
                    } else {
                        if (firstBestFit < 0) {
                            firstBestFit = i2;
                            break;
                            break;
                        }
                        betterCount = 0;
                        worseCount = 0;
                        j2 = -1;
                        while (true) {
                            if (j2 == extraBestFitsCount) {
                                if (j2 == -1) {
                                    bestFitIndex = firstBestFit;
                                } else {
                                    bestFitIndex = extraBestFits[j2];
                                }
                                bestFit = methodsOrCtors[bestFitIndex];
                                if (!cx.hasFeature(13)) {
                                    preference = preferSignature(args, argTypes2, member2.vararg, bestFit.argTypes, bestFit.vararg);
                                    if (preference != 3) {
                                        if (preference == 1) {
                                            betterCount++;
                                        } else if (preference == 2) {
                                            worseCount++;
                                        } else {
                                            if (preference != 0) {
                                                Kit.codeBug();
                                            }
                                            if (!bestFit.isStatic()) {
                                                break;
                                            }
                                            break;
                                            break;
                                        }
                                        j2++;
                                    }
                                } else {
                                    preference = preferSignature(args, argTypes2, member2.vararg, bestFit.argTypes, bestFit.vararg);
                                    if (preference != 3) {
                                        if (preference == 1) {
                                            betterCount++;
                                        } else if (preference == 2) {
                                            worseCount++;
                                        } else {
                                            if (preference != 0) {
                                                Kit.codeBug();
                                            }
                                            if (!bestFit.isStatic()) {
                                                break;
                                                break;
                                            }
                                            break;
                                            break;
                                        }
                                        j2++;
                                    }
                                }
                            }
                            if (betterCount == 1 + extraBestFitsCount) {
                                firstBestFit = i2;
                                extraBestFitsCount = 0;
                                break;
                                break;
                            }
                            if (worseCount != 1 + extraBestFitsCount) {
                                break;
                                break;
                            }
                            if (extraBestFits == null) {
                                extraBestFits = new int[methodsOrCtors.length - 1];
                            }
                            extraBestFits[extraBestFitsCount] = i2;
                            extraBestFitsCount++;
                            break;
                            break;
                        }
                    }
                }
            }
        }
        if (firstBestFit < 0) {
            return -1;
        }
        if (extraBestFitsCount == 0) {
            return firstBestFit;
        }
        StringBuilder buf = new StringBuilder();
        for (int j4 = -1; j4 != extraBestFitsCount; j4++) {
            if (j4 == -1) {
                i = firstBestFit;
            } else {
                i = extraBestFits[j4];
            }
            int bestFitIndex2 = i;
            buf.append("\n    ");
            buf.append(methodsOrCtors[bestFitIndex2].toJavaDeclaration());
        }
        MemberBox firstFitMember = methodsOrCtors[firstBestFit];
        String memberName = firstFitMember.getName();
        String memberClass = firstFitMember.getDeclaringClass().getName();
        if (methodsOrCtors[0].isCtor()) {
            throw Context.reportRuntimeError3("msg.constructor.ambiguous", memberName, scriptSignature(args), buf.toString());
        }
        throw Context.reportRuntimeError4("msg.method.ambiguous", memberClass, memberName, scriptSignature(args), buf.toString());
    }

    private static int preferSignature(Object[] args, Class<?>[] sig1, boolean vararg1, Class<?>[] sig2, boolean vararg2) {
        int preference;
        int totalPreference = 0;
        int j = 0;
        while (j < args.length) {
            Class<?> type1 = (!vararg1 || j < sig1.length) ? sig1[j] : sig1[sig1.length - 1];
            Class<?> type2 = (!vararg2 || j < sig2.length) ? sig2[j] : sig2[sig2.length - 1];
            if (type1 != type2) {
                Object arg = args[j];
                int rank1 = NativeJavaObject.getConversionWeight(arg, type1);
                int rank2 = NativeJavaObject.getConversionWeight(arg, type2);
                if (rank1 < rank2) {
                    preference = 1;
                } else if (rank1 > rank2) {
                    preference = 2;
                } else if (rank1 == 0) {
                    if (type1.isAssignableFrom(type2)) {
                        preference = 2;
                    } else if (type2.isAssignableFrom(type1)) {
                        preference = 1;
                    } else {
                        preference = 3;
                    }
                } else {
                    preference = 3;
                }
                totalPreference |= preference;
                if (totalPreference == 3) {
                    break;
                }
            }
            j++;
        }
        return totalPreference;
    }

    private static void printDebug(String msg, MemberBox member, Object[] args) {
    }
}
