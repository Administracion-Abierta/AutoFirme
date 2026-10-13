package com.sun.jna;

import com.sun.jna.internal.Cleaner;
import java.io.Closeable;
import java.lang.ref.Reference;
import java.lang.ref.WeakReference;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.WeakHashMap;
import java.util.concurrent.ConcurrentHashMap;

/* JADX INFO: loaded from: autofirma.jar:com/sun/jna/CallbackReference.class */
public class CallbackReference extends WeakReference<Callback> implements Closeable {
    static final Map<Callback, CallbackReference> callbackMap = new WeakHashMap();
    static final Map<Callback, CallbackReference> directCallbackMap = new WeakHashMap();
    static final Map<Pointer, Reference<Callback>[]> pointerCallbackMap = new WeakHashMap();
    static final Map<Object, Object> allocations = Collections.synchronizedMap(new WeakHashMap());
    private static final Map<Long, Reference<CallbackReference>> allocatedMemory = new ConcurrentHashMap();
    private static final Method PROXY_CALLBACK_METHOD;
    private static final Class<?> DLL_CALLBACK_CLASS;
    private static final Map<Callback, CallbackThreadInitializer> initializers;
    Cleaner.Cleanable cleanable;
    Pointer cbstruct;
    Pointer trampoline;
    CallbackProxy proxy;
    Method method;
    int callingConvention;

    static {
        try {
            PROXY_CALLBACK_METHOD = CallbackProxy.class.getMethod(Callback.METHOD_NAME, Object[].class);
            if (Platform.isWindows()) {
                try {
                    DLL_CALLBACK_CLASS = Class.forName("com.sun.jna.win32.DLLCallback");
                } catch (ClassNotFoundException e) {
                    throw new Error("Error loading DLLCallback class", e);
                }
            } else {
                DLL_CALLBACK_CLASS = null;
            }
            initializers = new WeakHashMap();
        } catch (Exception e2) {
            throw new Error("Error looking up CallbackProxy.callback() method");
        }
    }

    static CallbackThreadInitializer setCallbackThreadInitializer(Callback cb, CallbackThreadInitializer initializer) {
        synchronized (initializers) {
            try {
                if (initializer != null) {
                    return initializers.put(cb, initializer);
                }
                return initializers.remove(cb);
            } catch (Throwable th) {
                throw th;
            }
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:com/sun/jna/CallbackReference$AttachOptions.class */
    static class AttachOptions extends Structure {
        public static final List<String> FIELDS = createFieldsOrder("daemon", "detach", "name");
        public boolean daemon;
        public boolean detach;
        public String name;

        AttachOptions() {
            setStringEncoding("utf8");
        }

        @Override // com.sun.jna.Structure
        protected List<String> getFieldOrder() {
            return FIELDS;
        }
    }

    private static ThreadGroup initializeThread(Callback cb, AttachOptions args) {
        CallbackThreadInitializer init;
        if (cb instanceof DefaultCallbackProxy) {
            cb = ((DefaultCallbackProxy) cb).getCallback();
        }
        synchronized (initializers) {
            init = initializers.get(cb);
        }
        ThreadGroup group = null;
        if (init != null) {
            group = init.getThreadGroup(cb);
            args.name = init.getName(cb);
            args.daemon = init.isDaemon(cb);
            args.detach = init.detach(cb);
            args.write();
        }
        return group;
    }

    public static Callback getCallback(Class<?> type, Pointer p) {
        return getCallback(type, p, false);
    }

    private static Callback getCallback(Class<?> type, Pointer p, boolean direct) {
        if (p == null) {
            return null;
        }
        if (!type.isInterface()) {
            throw new IllegalArgumentException("Callback type must be an interface");
        }
        Map<Callback, CallbackReference> map = direct ? directCallbackMap : callbackMap;
        synchronized (pointerCallbackMap) {
            Reference<Callback>[] array = pointerCallbackMap.get(p);
            Callback cb = getTypeAssignableCallback(type, array);
            if (cb != null) {
                return cb;
            }
            Callback cb2 = createCallback(type, p);
            pointerCallbackMap.put(p, addCallbackToArray(cb2, array));
            map.remove(cb2);
            return cb2;
        }
    }

    private static Callback getTypeAssignableCallback(Class<?> type, Reference<Callback>[] array) {
        if (array != null) {
            for (Reference<Callback> reference : array) {
                Callback cb = reference.get();
                if (cb != null && type.isAssignableFrom(cb.getClass())) {
                    return cb;
                }
            }
            return null;
        }
        return null;
    }

    private static Reference<Callback>[] addCallbackToArray(Callback cb, Reference<Callback>[] array) {
        int reqArraySize = 1;
        if (array != null) {
            for (int i = 0; i < array.length; i++) {
                if (array[i].get() == null) {
                    array[i] = null;
                } else {
                    reqArraySize++;
                }
            }
        }
        Reference<Callback>[] newArray = new Reference[reqArraySize];
        int nidx = 0;
        if (array != null) {
            for (int i2 = 0; i2 < array.length; i2++) {
                if (array[i2] != null) {
                    int i3 = nidx;
                    nidx++;
                    newArray[i3] = array[i2];
                }
            }
        }
        newArray[nidx] = new WeakReference(cb);
        return newArray;
    }

    private static Callback createCallback(Class<?> type, Pointer p) {
        int ctype = AltCallingConvention.class.isAssignableFrom(type) ? 63 : 0;
        Map<String, Object> foptions = new HashMap<>(Native.getLibraryOptions(type));
        foptions.put("invoking-method", getCallbackMethod(type));
        NativeFunctionHandler h = new NativeFunctionHandler(p, ctype, foptions);
        return (Callback) Proxy.newProxyInstance(type.getClassLoader(), new Class[]{type}, h);
    }

    private CallbackReference(Callback callback, int callingConvention, boolean direct) {
        long peer;
        super(callback);
        TypeMapper mapper = Native.getTypeMapper(callback.getClass());
        this.callingConvention = callingConvention;
        boolean ppc = Platform.isPPC();
        if (direct) {
            Method m = getCallbackMethod(callback);
            Class<?>[] ptypes = m.getParameterTypes();
            for (int i = 0; i < ptypes.length; i++) {
                if (ppc && (ptypes[i] == Float.TYPE || ptypes[i] == Double.TYPE)) {
                    direct = false;
                    break;
                } else {
                    if (mapper != null && mapper.getFromNativeConverter(ptypes[i]) != null) {
                        direct = false;
                        break;
                    }
                }
            }
            if (mapper != null && mapper.getToNativeConverter(m.getReturnType()) != null) {
                direct = false;
            }
        }
        String encoding = Native.getStringEncoding(callback.getClass());
        if (direct) {
            this.method = getCallbackMethod(callback);
            Class<?>[] nativeParamTypes = this.method.getParameterTypes();
            Class<?> returnType = this.method.getReturnType();
            int flags = 1;
            if (DLL_CALLBACK_CLASS != null && DLL_CALLBACK_CLASS.isInstance(callback)) {
                flags = 1 | 2;
            }
            peer = Native.createNativeCallback(callback, this.method, nativeParamTypes, returnType, callingConvention, flags, encoding);
        } else {
            if (callback instanceof CallbackProxy) {
                this.proxy = (CallbackProxy) callback;
            } else {
                this.proxy = new DefaultCallbackProxy(getCallbackMethod(callback), mapper, encoding);
            }
            Class<?>[] nativeParamTypes2 = this.proxy.getParameterTypes();
            Class<?> returnType2 = this.proxy.getReturnType();
            if (mapper != null) {
                for (int i2 = 0; i2 < nativeParamTypes2.length; i2++) {
                    FromNativeConverter rc = mapper.getFromNativeConverter(nativeParamTypes2[i2]);
                    if (rc != null) {
                        nativeParamTypes2[i2] = rc.nativeType();
                    }
                }
                ToNativeConverter tn = mapper.getToNativeConverter(returnType2);
                if (tn != null) {
                    returnType2 = tn.nativeType();
                }
            }
            for (int i3 = 0; i3 < nativeParamTypes2.length; i3++) {
                nativeParamTypes2[i3] = getNativeType(nativeParamTypes2[i3]);
                if (!isAllowableNativeType(nativeParamTypes2[i3])) {
                    String msg = "Callback argument " + nativeParamTypes2[i3] + " requires custom type conversion";
                    throw new IllegalArgumentException(msg);
                }
            }
            Class<?> returnType3 = getNativeType(returnType2);
            if (!isAllowableNativeType(returnType3)) {
                String msg2 = "Callback return type " + returnType3 + " requires custom type conversion";
                throw new IllegalArgumentException(msg2);
            }
            peer = Native.createNativeCallback(this.proxy, PROXY_CALLBACK_METHOD, nativeParamTypes2, returnType3, callingConvention, (DLL_CALLBACK_CLASS == null || !DLL_CALLBACK_CLASS.isInstance(callback)) ? 0 : 2, encoding);
        }
        this.cbstruct = peer != 0 ? new Pointer(peer) : null;
        if (peer != 0) {
            allocatedMemory.put(Long.valueOf(peer), new WeakReference(this));
            this.cleanable = Cleaner.getCleaner().register(this, new CallbackReferenceDisposer(this.cbstruct));
        }
    }

    private Class<?> getNativeType(Class<?> cls) {
        if (Structure.class.isAssignableFrom(cls)) {
            Structure.validate(cls);
            if (!Structure.ByValue.class.isAssignableFrom(cls)) {
                return Pointer.class;
            }
        } else {
            if (NativeMapped.class.isAssignableFrom(cls)) {
                return NativeMappedConverter.getInstance(cls).nativeType();
            }
            if (cls == String.class || cls == WString.class || cls == String[].class || cls == WString[].class || Callback.class.isAssignableFrom(cls)) {
                return Pointer.class;
            }
        }
        return cls;
    }

    private static Method checkMethod(Method m) {
        if (m.getParameterTypes().length > 256) {
            String msg = "Method signature exceeds the maximum parameter count: " + m;
            throw new UnsupportedOperationException(msg);
        }
        return m;
    }

    static Class<?> findCallbackClass(Class<?> type) {
        if (!Callback.class.isAssignableFrom(type)) {
            throw new IllegalArgumentException(type.getName() + " is not derived from com.sun.jna.Callback");
        }
        if (type.isInterface()) {
            return type;
        }
        Class<?>[] ifaces = type.getInterfaces();
        for (int i = 0; i < ifaces.length; i++) {
            if (Callback.class.isAssignableFrom(ifaces[i])) {
                try {
                    getCallbackMethod(ifaces[i]);
                    return ifaces[i];
                } catch (IllegalArgumentException e) {
                }
            }
        }
        if (Callback.class.isAssignableFrom(type.getSuperclass())) {
            return findCallbackClass(type.getSuperclass());
        }
        return type;
    }

    private static Method getCallbackMethod(Callback callback) {
        return getCallbackMethod(findCallbackClass(callback.getClass()));
    }

    private static Method getCallbackMethod(Class<?> cls) {
        Method[] pubMethods = cls.getDeclaredMethods();
        Method[] classMethods = cls.getMethods();
        Set<Method> pmethods = new HashSet<>(Arrays.asList(pubMethods));
        pmethods.retainAll(Arrays.asList(classMethods));
        Iterator<Method> i = pmethods.iterator();
        while (i.hasNext()) {
            if (Callback.FORBIDDEN_NAMES.contains(i.next().getName())) {
                i.remove();
            }
        }
        Method[] methods = (Method[]) pmethods.toArray(new Method[0]);
        if (methods.length == 1) {
            return checkMethod(methods[0]);
        }
        for (Method m : methods) {
            if (Callback.METHOD_NAME.equals(m.getName())) {
                return checkMethod(m);
            }
        }
        throw new IllegalArgumentException("Callback must implement a single public method, or one public method named 'callback'");
    }

    private void setCallbackOptions(int options) {
        this.cbstruct.setInt(Native.POINTER_SIZE, options);
    }

    public Pointer getTrampoline() {
        if (this.trampoline == null) {
            this.trampoline = this.cbstruct.getPointer(0L);
        }
        return this.trampoline;
    }

    @Override // java.io.Closeable, java.lang.AutoCloseable
    public void close() {
        if (this.cleanable != null) {
            this.cleanable.clean();
        }
        this.cbstruct = null;
    }

    @Deprecated
    protected void dispose() {
        close();
    }

    static void disposeAll() {
        Collection<Reference<CallbackReference>> refs = new LinkedList<>(allocatedMemory.values());
        for (Reference<CallbackReference> r : refs) {
            CallbackReference ref = r.get();
            if (ref != null) {
                ref.close();
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public Callback getCallback() {
        return (Callback) get();
    }

    private static Pointer getNativeFunctionPointer(Callback cb) {
        if (Proxy.isProxyClass(cb.getClass())) {
            Object handler = Proxy.getInvocationHandler(cb);
            if (handler instanceof NativeFunctionHandler) {
                return ((NativeFunctionHandler) handler).getPointer();
            }
            return null;
        }
        return null;
    }

    public static Pointer getFunctionPointer(Callback cb) {
        return getFunctionPointer(cb, false);
    }

    private static Pointer getFunctionPointer(Callback cb, boolean direct) {
        int iIntValue;
        Pointer trampoline;
        if (cb == null) {
            return null;
        }
        Pointer fp = getNativeFunctionPointer(cb);
        if (fp != null) {
            return fp;
        }
        Map<String, ?> options = Native.getLibraryOptions(cb.getClass());
        if (cb instanceof AltCallingConvention) {
            iIntValue = 63;
        } else {
            iIntValue = (options == null || !options.containsKey(Library.OPTION_CALLING_CONVENTION)) ? 0 : ((Integer) options.get(Library.OPTION_CALLING_CONVENTION)).intValue();
        }
        int callingConvention = iIntValue;
        Map<Callback, CallbackReference> map = direct ? directCallbackMap : callbackMap;
        synchronized (pointerCallbackMap) {
            CallbackReference cbref = map.get(cb);
            if (cbref == null) {
                cbref = new CallbackReference(cb, callingConvention, direct);
                map.put(cb, cbref);
                pointerCallbackMap.put(cbref.getTrampoline(), addCallbackToArray(cb, null));
                if (initializers.containsKey(cb)) {
                    cbref.setCallbackOptions(1);
                }
            }
            trampoline = cbref.getTrampoline();
        }
        return trampoline;
    }

    /* JADX INFO: loaded from: autofirma.jar:com/sun/jna/CallbackReference$DefaultCallbackProxy.class */
    private class DefaultCallbackProxy implements CallbackProxy {
        private final Method callbackMethod;
        private ToNativeConverter toNative;
        private final FromNativeConverter[] fromNative;
        private final String encoding;

        public DefaultCallbackProxy(Method callbackMethod, TypeMapper mapper, String encoding) {
            this.callbackMethod = callbackMethod;
            this.encoding = encoding;
            Class<?>[] argTypes = callbackMethod.getParameterTypes();
            Class<?> returnType = callbackMethod.getReturnType();
            this.fromNative = new FromNativeConverter[argTypes.length];
            if (NativeMapped.class.isAssignableFrom(returnType)) {
                this.toNative = NativeMappedConverter.getInstance(returnType);
            } else if (mapper != null) {
                this.toNative = mapper.getToNativeConverter(returnType);
            }
            for (int i = 0; i < this.fromNative.length; i++) {
                if (NativeMapped.class.isAssignableFrom(argTypes[i])) {
                    this.fromNative[i] = new NativeMappedConverter(argTypes[i]);
                } else if (mapper != null) {
                    this.fromNative[i] = mapper.getFromNativeConverter(argTypes[i]);
                }
            }
            if (!callbackMethod.isAccessible()) {
                try {
                    callbackMethod.setAccessible(true);
                } catch (SecurityException e) {
                    throw new IllegalArgumentException("Callback method is inaccessible, make sure the interface is public: " + callbackMethod);
                }
            }
        }

        public Callback getCallback() {
            return CallbackReference.this.getCallback();
        }

        private Object invokeCallback(Object[] args) {
            Class<?>[] paramTypes = this.callbackMethod.getParameterTypes();
            Object[] callbackArgs = new Object[args.length];
            for (int i = 0; i < args.length; i++) {
                Class<?> type = paramTypes[i];
                Object arg = args[i];
                if (this.fromNative[i] != null) {
                    FromNativeContext context = new CallbackParameterContext(type, this.callbackMethod, args, i);
                    callbackArgs[i] = this.fromNative[i].fromNative(arg, context);
                } else {
                    callbackArgs[i] = convertArgument(arg, type);
                }
            }
            Object result = null;
            Callback cb = getCallback();
            if (cb != null) {
                try {
                    result = convertResult(this.callbackMethod.invoke(cb, callbackArgs));
                } catch (IllegalAccessException e) {
                    Native.getCallbackExceptionHandler().uncaughtException(cb, e);
                } catch (IllegalArgumentException e2) {
                    Native.getCallbackExceptionHandler().uncaughtException(cb, e2);
                } catch (InvocationTargetException e3) {
                    Native.getCallbackExceptionHandler().uncaughtException(cb, e3.getTargetException());
                }
            }
            for (int i2 = 0; i2 < callbackArgs.length; i2++) {
                if ((callbackArgs[i2] instanceof Structure) && !(callbackArgs[i2] instanceof Structure.ByValue)) {
                    ((Structure) callbackArgs[i2]).autoWrite();
                }
            }
            return result;
        }

        @Override // com.sun.jna.CallbackProxy
        public Object callback(Object[] args) {
            try {
                return invokeCallback(args);
            } catch (Throwable t) {
                Native.getCallbackExceptionHandler().uncaughtException(getCallback(), t);
                return null;
            }
        }

        private Object convertArgument(Object value, Class<?> dstType) {
            if (value instanceof Pointer) {
                if (dstType == String.class) {
                    value = ((Pointer) value).getString(0L, this.encoding);
                } else if (dstType == WString.class) {
                    value = new WString(((Pointer) value).getWideString(0L));
                } else if (dstType == String[].class) {
                    value = ((Pointer) value).getStringArray(0L, this.encoding);
                } else if (dstType == WString[].class) {
                    value = ((Pointer) value).getWideStringArray(0L);
                } else if (Callback.class.isAssignableFrom(dstType)) {
                    value = CallbackReference.getCallback(dstType, (Pointer) value);
                } else if (Structure.class.isAssignableFrom(dstType)) {
                    if (Structure.ByValue.class.isAssignableFrom(dstType)) {
                        Structure s = Structure.newInstance(dstType);
                        byte[] buf = new byte[s.size()];
                        ((Pointer) value).read(0L, buf, 0, buf.length);
                        s.getPointer().write(0L, buf, 0, buf.length);
                        s.read();
                        value = s;
                    } else {
                        Structure s2 = Structure.newInstance((Class<Structure>) dstType, (Pointer) value);
                        s2.conditionalAutoRead();
                        value = s2;
                    }
                }
            } else if ((Boolean.TYPE == dstType || Boolean.class == dstType) && (value instanceof Number)) {
                value = Function.valueOf(((Number) value).intValue() != 0);
            }
            return value;
        }

        private Object convertResult(Object value) {
            if (this.toNative != null) {
                value = this.toNative.toNative(value, new CallbackResultContext(this.callbackMethod));
            }
            if (value == null) {
                return null;
            }
            Class<?> cls = value.getClass();
            if (Structure.class.isAssignableFrom(cls)) {
                if (Structure.ByValue.class.isAssignableFrom(cls)) {
                    return value;
                }
                return ((Structure) value).getPointer();
            }
            if (cls == Boolean.TYPE || cls == Boolean.class) {
                return Boolean.TRUE.equals(value) ? Function.INTEGER_TRUE : Function.INTEGER_FALSE;
            }
            if (cls == String.class || cls == WString.class) {
                return CallbackReference.getNativeString(value, cls == WString.class);
            }
            if (cls == String[].class || cls == WString[].class) {
                StringArray sa = cls == String[].class ? new StringArray((String[]) value, this.encoding) : new StringArray((WString[]) value);
                CallbackReference.allocations.put(value, sa);
                return sa;
            }
            if (Callback.class.isAssignableFrom(cls)) {
                return CallbackReference.getFunctionPointer((Callback) value);
            }
            return value;
        }

        @Override // com.sun.jna.CallbackProxy
        public Class<?>[] getParameterTypes() {
            return this.callbackMethod.getParameterTypes();
        }

        @Override // com.sun.jna.CallbackProxy
        public Class<?> getReturnType() {
            return this.callbackMethod.getReturnType();
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:com/sun/jna/CallbackReference$NativeFunctionHandler.class */
    private static class NativeFunctionHandler implements InvocationHandler {
        private final Function function;
        private final Map<String, ?> options;

        public NativeFunctionHandler(Pointer address, int callingConvention, Map<String, ?> options) {
            this.options = options;
            this.function = new Function(address, callingConvention, (String) options.get(Library.OPTION_STRING_ENCODING));
        }

        @Override // java.lang.reflect.InvocationHandler
        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
            if (Library.Handler.OBJECT_TOSTRING.equals(method)) {
                String str = "Proxy interface to " + this.function;
                Method m = (Method) this.options.get("invoking-method");
                Class<?> cls = CallbackReference.findCallbackClass(m.getDeclaringClass());
                return str + " (" + cls.getName() + ")";
            }
            if (Library.Handler.OBJECT_HASHCODE.equals(method)) {
                return Integer.valueOf(hashCode());
            }
            if (Library.Handler.OBJECT_EQUALS.equals(method)) {
                Object o = args[0];
                if (o == null || !Proxy.isProxyClass(o.getClass())) {
                    return Boolean.FALSE;
                }
                return Function.valueOf(Proxy.getInvocationHandler(o) == this);
            }
            if (Function.isVarArgs(method)) {
                args = Function.concatenateVarArgs(args);
            }
            return this.function.invoke(method.getReturnType(), args, this.options);
        }

        public Pointer getPointer() {
            return this.function;
        }
    }

    private static boolean isAllowableNativeType(Class<?> cls) {
        return cls == Void.TYPE || cls == Void.class || cls == Boolean.TYPE || cls == Boolean.class || cls == Byte.TYPE || cls == Byte.class || cls == Short.TYPE || cls == Short.class || cls == Character.TYPE || cls == Character.class || cls == Integer.TYPE || cls == Integer.class || cls == Long.TYPE || cls == Long.class || cls == Float.TYPE || cls == Float.class || cls == Double.TYPE || cls == Double.class || (Structure.ByValue.class.isAssignableFrom(cls) && Structure.class.isAssignableFrom(cls)) || Pointer.class.isAssignableFrom(cls);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static Pointer getNativeString(Object value, boolean wide) {
        if (value != null) {
            NativeString ns = new NativeString(value.toString(), wide);
            allocations.put(value, ns);
            return ns.getPointer();
        }
        return null;
    }

    /* JADX INFO: loaded from: autofirma.jar:com/sun/jna/CallbackReference$CallbackReferenceDisposer.class */
    private static final class CallbackReferenceDisposer implements Runnable {
        private Pointer cbstruct;

        public CallbackReferenceDisposer(Pointer cbstruct) {
            this.cbstruct = cbstruct;
        }

        @Override // java.lang.Runnable
        public synchronized void run() {
            if (this.cbstruct != null) {
                try {
                    Native.freeNativeCallback(this.cbstruct.peer);
                    Map map = CallbackReference.allocatedMemory;
                } finally {
                    CallbackReference.allocatedMemory.remove(Long.valueOf(this.cbstruct.peer));
                    this.cbstruct.peer = 0L;
                    this.cbstruct = null;
                }
            }
        }
    }
}
