package com.sun.jna.platform.win32.COM.util;

import com.sun.jna.platform.win32.COM.COMException;
import com.sun.jna.platform.win32.COM.IDispatchCallback;
import com.sun.jna.platform.win32.COM.util.annotation.ComObject;
import com.sun.jna.platform.win32.Guid;
import com.sun.jna.platform.win32.OaIdl;
import com.sun.jna.platform.win32.OleAuto;
import com.sun.jna.platform.win32.Variant;
import com.sun.jna.platform.win32.WinDef;
import com.sun.jna.platform.win32.WinNT;
import com.sun.jna.ptr.IntByReference;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

/* JADX INFO: loaded from: autofirma.jar:com/sun/jna/platform/win32/COM/util/Factory.class */
public class Factory extends ObjectFactory {
    private ComThread comThread;

    public Factory() {
        this(new ComThread("Default Factory COM Thread", 5000L, new Thread.UncaughtExceptionHandler() { // from class: com.sun.jna.platform.win32.COM.util.Factory.1
            @Override // java.lang.Thread.UncaughtExceptionHandler
            public void uncaughtException(Thread t, Throwable e) {
            }
        }));
    }

    public Factory(ComThread comThread) {
        this.comThread = comThread;
    }

    /* JADX INFO: loaded from: autofirma.jar:com/sun/jna/platform/win32/COM/util/Factory$ProxyObject2.class */
    private class ProxyObject2 implements InvocationHandler {
        private final Object delegate;

        public ProxyObject2(Object delegate) {
            this.delegate = delegate;
        }

        @Override // java.lang.reflect.InvocationHandler
        public Object invoke(Object proxy, final Method method, final Object[] args) throws Throwable {
            if (args != null) {
                for (int i = 0; i < args.length; i++) {
                    if (args[i] != null && Proxy.isProxyClass(args[i].getClass())) {
                        InvocationHandler ih = Proxy.getInvocationHandler(args[i]);
                        if (ih instanceof ProxyObject2) {
                            args[i] = ((ProxyObject2) ih).delegate;
                        }
                    }
                }
            }
            return Factory.this.runInComThread(new Callable<Object>() { // from class: com.sun.jna.platform.win32.COM.util.Factory.ProxyObject2.1
                @Override // java.util.concurrent.Callable
                public Object call() throws Exception {
                    return method.invoke(ProxyObject2.this.delegate, args);
                }
            });
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:com/sun/jna/platform/win32/COM/util/Factory$CallbackProxy2.class */
    private class CallbackProxy2 extends CallbackProxy {
        public CallbackProxy2(ObjectFactory factory, Class<?> comEventCallbackInterface, IComEventCallbackListener comEventCallbackListener) {
            super(factory, comEventCallbackInterface, comEventCallbackListener);
        }

        @Override // com.sun.jna.platform.win32.COM.util.CallbackProxy, com.sun.jna.platform.win32.COM.IDispatch
        public WinNT.HRESULT Invoke(OaIdl.DISPID dispIdMember, Guid.REFIID riid, WinDef.LCID lcid, WinDef.WORD wFlags, OleAuto.DISPPARAMS.ByReference pDispParams, Variant.VARIANT.ByReference pVarResult, OaIdl.EXCEPINFO.ByReference pExcepInfo, IntByReference puArgErr) {
            ComThread.setComThread(true);
            try {
                return super.Invoke(dispIdMember, riid, lcid, wFlags, pDispParams, pVarResult, pExcepInfo, puArgErr);
            } finally {
                ComThread.setComThread(false);
            }
        }
    }

    @Override // com.sun.jna.platform.win32.COM.util.ObjectFactory
    public <T> T createProxy(Class<T> cls, com.sun.jna.platform.win32.COM.IDispatch iDispatch) {
        return (T) Proxy.newProxyInstance(cls.getClassLoader(), new Class[]{cls}, new ProxyObject2(super.createProxy(cls, iDispatch)));
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    @Override // com.sun.jna.platform.win32.COM.util.ObjectFactory
    public Guid.GUID discoverClsId(final ComObject annotation) {
        return (Guid.GUID) runInComThread(new Callable<Guid.GUID>() { // from class: com.sun.jna.platform.win32.COM.util.Factory.2
            /* JADX WARN: Can't rename method to resolve collision */
            @Override // java.util.concurrent.Callable
            public Guid.GUID call() throws Exception {
                return Factory.super.discoverClsId(annotation);
            }
        });
    }

    @Override // com.sun.jna.platform.win32.COM.util.ObjectFactory
    public <T> T fetchObject(final Class<T> cls) throws COMException {
        return (T) runInComThread(new Callable<T>() { // from class: com.sun.jna.platform.win32.COM.util.Factory.3
            @Override // java.util.concurrent.Callable
            public T call() throws Exception {
                return (T) Factory.super.fetchObject(cls);
            }
        });
    }

    @Override // com.sun.jna.platform.win32.COM.util.ObjectFactory
    public <T> T createObject(final Class<T> cls) {
        return (T) runInComThread(new Callable<T>() { // from class: com.sun.jna.platform.win32.COM.util.Factory.4
            @Override // java.util.concurrent.Callable
            public T call() throws Exception {
                return (T) Factory.super.createObject(cls);
            }
        });
    }

    @Override // com.sun.jna.platform.win32.COM.util.ObjectFactory
    IDispatchCallback createDispatchCallback(Class<?> comEventCallbackInterface, IComEventCallbackListener comEventCallbackListener) {
        return new CallbackProxy2(this, comEventCallbackInterface, comEventCallbackListener);
    }

    @Override // com.sun.jna.platform.win32.COM.util.ObjectFactory
    public IRunningObjectTable getRunningObjectTable() {
        return super.getRunningObjectTable();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public <T> T runInComThread(Callable<T> callable) {
        try {
            return (T) this.comThread.execute(callable);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } catch (ExecutionException e2) {
            Throwable cause = e2.getCause();
            if (cause instanceof RuntimeException) {
                appendStacktrace(e2, cause);
                throw ((RuntimeException) cause);
            }
            if (cause instanceof InvocationTargetException) {
                Throwable targetException = ((InvocationTargetException) cause).getTargetException();
                if (targetException instanceof RuntimeException) {
                    appendStacktrace(e2, targetException);
                    throw ((RuntimeException) targetException);
                }
            }
            throw new RuntimeException(e2);
        } catch (TimeoutException e3) {
            throw new RuntimeException(e3);
        }
    }

    private static void appendStacktrace(Exception caughtException, Throwable toBeThrown) {
        StackTraceElement[] upperTrace = caughtException.getStackTrace();
        StackTraceElement[] lowerTrace = toBeThrown.getStackTrace();
        StackTraceElement[] trace = new StackTraceElement[upperTrace.length + lowerTrace.length];
        System.arraycopy(upperTrace, 0, trace, lowerTrace.length, upperTrace.length);
        System.arraycopy(lowerTrace, 0, trace, 0, lowerTrace.length);
        toBeThrown.setStackTrace(trace);
    }

    public ComThread getComThread() {
        return this.comThread;
    }
}
