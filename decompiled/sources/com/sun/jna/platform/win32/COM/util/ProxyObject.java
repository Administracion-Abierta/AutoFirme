package com.sun.jna.platform.win32.COM.util;

import com.sun.jna.Pointer;
import com.sun.jna.WString;
import com.sun.jna.internal.ReflectionUtils;
import com.sun.jna.platform.win32.COM.COMException;
import com.sun.jna.platform.win32.COM.COMUtils;
import com.sun.jna.platform.win32.COM.ConnectionPoint;
import com.sun.jna.platform.win32.COM.ConnectionPointContainer;
import com.sun.jna.platform.win32.COM.Dispatch;
import com.sun.jna.platform.win32.COM.IDispatchCallback;
import com.sun.jna.platform.win32.COM.util.annotation.ComInterface;
import com.sun.jna.platform.win32.COM.util.annotation.ComMethod;
import com.sun.jna.platform.win32.COM.util.annotation.ComProperty;
import com.sun.jna.platform.win32.Guid;
import com.sun.jna.platform.win32.Kernel32Util;
import com.sun.jna.platform.win32.OaIdl;
import com.sun.jna.platform.win32.OleAuto;
import com.sun.jna.platform.win32.Variant;
import com.sun.jna.platform.win32.WinDef;
import com.sun.jna.platform.win32.WinNT;
import com.sun.jna.ptr.IntByReference;
import com.sun.jna.ptr.PointerByReference;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

/* JADX INFO: loaded from: autofirma.jar:com/sun/jna/platform/win32/COM/util/ProxyObject.class */
public class ProxyObject implements InvocationHandler, IDispatch, IRawDispatchHandle, IConnectionPoint {
    private long unknownId = -1;
    private final Class<?> theInterface;
    private final ObjectFactory factory;
    private final com.sun.jna.platform.win32.COM.IDispatch rawDispatch;
    static final /* synthetic */ boolean $assertionsDisabled;

    static {
        $assertionsDisabled = !ProxyObject.class.desiredAssertionStatus();
    }

    public ProxyObject(Class<?> theInterface, com.sun.jna.platform.win32.COM.IDispatch rawDispatch, ObjectFactory factory) {
        this.rawDispatch = rawDispatch;
        this.theInterface = theInterface;
        this.factory = factory;
        this.rawDispatch.AddRef();
        getUnknownId();
        factory.register(this);
    }

    private long getUnknownId() {
        if (!$assertionsDisabled && !COMUtils.comIsInitialized()) {
            throw new AssertionError("COM not initialized");
        }
        if (-1 == this.unknownId) {
            try {
                PointerByReference ppvObject = new PointerByReference();
                Thread current = Thread.currentThread();
                current.getName();
                Guid.IID iid = com.sun.jna.platform.win32.COM.IUnknown.IID_IUNKNOWN;
                WinNT.HRESULT hr = getRawDispatch().QueryInterface(new Guid.REFIID(iid), ppvObject);
                if (WinNT.S_OK.equals(hr)) {
                    Dispatch dispatch = new Dispatch(ppvObject.getValue());
                    this.unknownId = Pointer.nativeValue(dispatch.getPointer());
                    dispatch.Release();
                } else {
                    String formatMessageFromHR = Kernel32Util.formatMessage(hr);
                    throw new COMException("getUnknownId: " + formatMessageFromHR, hr);
                }
            } catch (RuntimeException e) {
                if (e instanceof COMException) {
                    throw e;
                }
                throw new COMException("Error occured when trying get Unknown Id ", e);
            }
        }
        return this.unknownId;
    }

    protected void finalize() throws Throwable {
        dispose();
        super.finalize();
    }

    public synchronized void dispose() {
        if (((Dispatch) this.rawDispatch).getPointer() != Pointer.NULL) {
            this.rawDispatch.Release();
            ((Dispatch) this.rawDispatch).setPointer(Pointer.NULL);
            this.factory.unregister(this);
        }
    }

    @Override // com.sun.jna.platform.win32.COM.util.IRawDispatchHandle
    public com.sun.jna.platform.win32.COM.IDispatch getRawDispatch() {
        return this.rawDispatch;
    }

    public boolean equals(Object arg) {
        if (null == arg) {
            return false;
        }
        if (arg instanceof ProxyObject) {
            ProxyObject other = (ProxyObject) arg;
            return getUnknownId() == other.getUnknownId();
        }
        if (Proxy.isProxyClass(arg.getClass())) {
            InvocationHandler handler = Proxy.getInvocationHandler(arg);
            if (handler instanceof ProxyObject) {
                try {
                    ProxyObject other2 = (ProxyObject) handler;
                    return getUnknownId() == other2.getUnknownId();
                } catch (Exception e) {
                    return false;
                }
            }
            return false;
        }
        return false;
    }

    public int hashCode() {
        long id = getUnknownId();
        return ((int) ((id >>> 32) & (-1))) + ((int) (id & (-1)));
    }

    public String toString() {
        return this.theInterface.getName() + "{unk=" + hashCode() + "}";
    }

    @Override // java.lang.reflect.InvocationHandler
    public Object invoke(Object proxy, Method method, Object[] args) throws Exception {
        boolean declaredAsInterface = (method.getAnnotation(ComMethod.class) == null && method.getAnnotation(ComProperty.class) == null) ? false : true;
        if (!declaredAsInterface && (method.getDeclaringClass().equals(Object.class) || method.getDeclaringClass().equals(IRawDispatchHandle.class) || method.getDeclaringClass().equals(IUnknown.class) || method.getDeclaringClass().equals(IDispatch.class) || method.getDeclaringClass().equals(IConnectionPoint.class))) {
            try {
                return method.invoke(this, args);
            } catch (InvocationTargetException ex) {
                throw ex.getCause();
            }
        }
        if (!declaredAsInterface && ReflectionUtils.isDefault(method)) {
            Object methodHandle = ReflectionUtils.getMethodHandle(method);
            return ReflectionUtils.invokeDefaultMethod(proxy, methodHandle, args);
        }
        Class<?> returnType = method.getReturnType();
        boolean isVoid = Void.TYPE.equals(returnType);
        ComProperty prop = (ComProperty) method.getAnnotation(ComProperty.class);
        if (null != prop) {
            int dispId = prop.dispId();
            Object[] fullLengthArgs = unfoldWhenVarargs(method, args);
            if (isVoid) {
                if (dispId != -1) {
                    setProperty(new OaIdl.DISPID(dispId), fullLengthArgs);
                    return null;
                }
                String propName = getMutatorName(method, prop);
                setProperty(propName, fullLengthArgs);
                return null;
            }
            if (dispId != -1) {
                return getProperty(returnType, new OaIdl.DISPID(dispId), args);
            }
            String propName2 = getAccessorName(method, prop);
            return getProperty(returnType, propName2, args);
        }
        ComMethod meth = (ComMethod) method.getAnnotation(ComMethod.class);
        if (null != meth) {
            Object[] fullLengthArgs2 = unfoldWhenVarargs(method, args);
            int dispId2 = meth.dispId();
            if (dispId2 != -1) {
                return invokeMethod(returnType, new OaIdl.DISPID(dispId2), fullLengthArgs2);
            }
            String methName = getMethodName(method, meth);
            return invokeMethod(returnType, methName, fullLengthArgs2);
        }
        return null;
    }

    private ConnectionPoint fetchRawConnectionPoint(Guid.IID iid) {
        if (!$assertionsDisabled && !COMUtils.comIsInitialized()) {
            throw new AssertionError("COM not initialized");
        }
        IConnectionPointContainer cpc = (IConnectionPointContainer) queryInterface(IConnectionPointContainer.class);
        Dispatch rawCpcDispatch = (Dispatch) cpc.getRawDispatch();
        ConnectionPointContainer rawCpc = new ConnectionPointContainer(rawCpcDispatch.getPointer());
        Guid.REFIID adviseRiid = new Guid.REFIID(iid.getPointer());
        PointerByReference ppCp = new PointerByReference();
        WinNT.HRESULT hr = rawCpc.FindConnectionPoint(adviseRiid, ppCp);
        COMUtils.checkRC(hr);
        ConnectionPoint rawCp = new ConnectionPoint(ppCp.getValue());
        return rawCp;
    }

    @Override // com.sun.jna.platform.win32.COM.util.IConnectionPoint
    public IComEventCallbackCookie advise(Class<?> comEventCallbackInterface, IComEventCallbackListener comEventCallbackListener) throws COMException {
        if (!$assertionsDisabled && !COMUtils.comIsInitialized()) {
            throw new AssertionError("COM not initialized");
        }
        try {
            ComInterface comInterfaceAnnotation = (ComInterface) comEventCallbackInterface.getAnnotation(ComInterface.class);
            if (null == comInterfaceAnnotation) {
                throw new COMException("advise: Interface must define a value for either iid via the ComInterface annotation");
            }
            Guid.IID iid = getIID(comInterfaceAnnotation);
            ConnectionPoint rawCp = fetchRawConnectionPoint(iid);
            IDispatchCallback rawListener = this.factory.createDispatchCallback(comEventCallbackInterface, comEventCallbackListener);
            comEventCallbackListener.setDispatchCallbackListener(rawListener);
            WinDef.DWORDByReference pdwCookie = new WinDef.DWORDByReference();
            WinNT.HRESULT hr = rawCp.Advise(rawListener, pdwCookie);
            rawCp.Release();
            COMUtils.checkRC(hr);
            return new ComEventCallbackCookie(pdwCookie.getValue());
        } catch (RuntimeException e) {
            if (e instanceof COMException) {
                throw e;
            }
            throw new COMException("Error occured in advise when trying to connect the listener " + comEventCallbackListener, e);
        }
    }

    @Override // com.sun.jna.platform.win32.COM.util.IConnectionPoint
    public void unadvise(Class<?> comEventCallbackInterface, IComEventCallbackCookie cookie) throws COMException {
        if (!$assertionsDisabled && !COMUtils.comIsInitialized()) {
            throw new AssertionError("COM not initialized");
        }
        try {
            ComInterface comInterfaceAnnotation = (ComInterface) comEventCallbackInterface.getAnnotation(ComInterface.class);
            if (null == comInterfaceAnnotation) {
                throw new COMException("unadvise: Interface must define a value for iid via the ComInterface annotation");
            }
            Guid.IID iid = getIID(comInterfaceAnnotation);
            ConnectionPoint rawCp = fetchRawConnectionPoint(iid);
            WinNT.HRESULT hr = rawCp.Unadvise(((ComEventCallbackCookie) cookie).getValue());
            rawCp.Release();
            COMUtils.checkRC(hr);
        } catch (RuntimeException e) {
            if (e instanceof COMException) {
                throw e;
            }
            throw new COMException("Error occured in unadvise when trying to disconnect the listener from " + this, e);
        }
    }

    @Override // com.sun.jna.platform.win32.COM.util.IDispatch
    public <T> void setProperty(String name, T value) {
        OaIdl.DISPID dispID = resolveDispId(getRawDispatch(), name);
        setProperty(dispID, value);
    }

    @Override // com.sun.jna.platform.win32.COM.util.IDispatch
    public <T> void setProperty(OaIdl.DISPID dispId, T value) {
        if (!$assertionsDisabled && !COMUtils.comIsInitialized()) {
            throw new AssertionError("COM not initialized");
        }
        Variant.VARIANT v = Convert.toVariant(value);
        WinNT.HRESULT hr = oleMethod(4, (Variant.VARIANT.ByReference) null, getRawDispatch(), dispId, v);
        Convert.free(v, value);
        COMUtils.checkRC(hr);
    }

    private void setProperty(String name, Object... args) {
        if (!$assertionsDisabled && !COMUtils.comIsInitialized()) {
            throw new AssertionError("COM not initialized");
        }
        OaIdl.DISPID dispID = resolveDispId(getRawDispatch(), name);
        setProperty(dispID, args);
    }

    private void setProperty(OaIdl.DISPID dispID, Object... args) {
        Variant.VARIANT[] vargs;
        if (!$assertionsDisabled && !COMUtils.comIsInitialized()) {
            throw new AssertionError("COM not initialized");
        }
        if (null == args) {
            vargs = new Variant.VARIANT[0];
        } else {
            vargs = new Variant.VARIANT[args.length];
        }
        for (int i = 0; i < vargs.length; i++) {
            vargs[i] = Convert.toVariant(args[i]);
        }
        WinNT.HRESULT hr = oleMethod(4, (Variant.VARIANT.ByReference) null, getRawDispatch(), dispID, vargs);
        for (int i2 = 0; i2 < vargs.length; i2++) {
            Convert.free(vargs[i2], args[i2]);
        }
        COMUtils.checkRC(hr);
    }

    @Override // com.sun.jna.platform.win32.COM.util.IDispatch
    public <T> T getProperty(Class<T> cls, String str, Object... objArr) {
        return (T) getProperty(cls, resolveDispId(getRawDispatch(), str), objArr);
    }

    @Override // com.sun.jna.platform.win32.COM.util.IDispatch
    public <T> T getProperty(Class<T> cls, OaIdl.DISPID dispid, Object... objArr) {
        Variant.VARIANT[] variantArr;
        if (null == objArr) {
            variantArr = new Variant.VARIANT[0];
        } else {
            variantArr = new Variant.VARIANT[objArr.length];
        }
        for (int i = 0; i < variantArr.length; i++) {
            variantArr[i] = Convert.toVariant(objArr[i]);
        }
        Variant.VARIANT.ByReference byReference = new Variant.VARIANT.ByReference();
        WinNT.HRESULT hresultOleMethod = oleMethod(2, byReference, getRawDispatch(), dispid, variantArr);
        for (int i2 = 0; i2 < variantArr.length; i2++) {
            Convert.free(variantArr[i2], objArr[i2]);
        }
        COMUtils.checkRC(hresultOleMethod);
        return (T) Convert.toJavaObject(byReference, cls, this.factory, false, true);
    }

    @Override // com.sun.jna.platform.win32.COM.util.IDispatch
    public <T> T invokeMethod(Class<T> cls, String str, Object... objArr) {
        return (T) invokeMethod(cls, resolveDispId(getRawDispatch(), str), objArr);
    }

    @Override // com.sun.jna.platform.win32.COM.util.IDispatch
    public <T> T invokeMethod(Class<T> cls, OaIdl.DISPID dispid, Object... objArr) {
        Variant.VARIANT[] variantArr;
        if (!$assertionsDisabled && !COMUtils.comIsInitialized()) {
            throw new AssertionError("COM not initialized");
        }
        if (null == objArr) {
            variantArr = new Variant.VARIANT[0];
        } else {
            variantArr = new Variant.VARIANT[objArr.length];
        }
        for (int i = 0; i < variantArr.length; i++) {
            variantArr[i] = Convert.toVariant(objArr[i]);
        }
        Variant.VARIANT.ByReference byReference = new Variant.VARIANT.ByReference();
        WinNT.HRESULT hresultOleMethod = oleMethod(1, byReference, getRawDispatch(), dispid, variantArr);
        for (int i2 = 0; i2 < variantArr.length; i2++) {
            Convert.free(variantArr[i2], objArr[i2]);
        }
        COMUtils.checkRC(hresultOleMethod);
        return (T) Convert.toJavaObject(byReference, cls, this.factory, false, true);
    }

    private Object[] unfoldWhenVarargs(Method method, Object[] argParams) {
        if (null == argParams) {
            return null;
        }
        if (argParams.length == 0 || !method.isVarArgs() || !(argParams[argParams.length - 1] instanceof Object[])) {
            return argParams;
        }
        Object[] varargs = (Object[]) argParams[argParams.length - 1];
        Object[] args = new Object[(argParams.length - 1) + varargs.length];
        System.arraycopy(argParams, 0, args, 0, argParams.length - 1);
        System.arraycopy(varargs, 0, args, argParams.length - 1, varargs.length);
        return args;
    }

    @Override // com.sun.jna.platform.win32.COM.util.IUnknown
    public <T> T queryInterface(Class<T> cls) throws COMException {
        if (!$assertionsDisabled && !COMUtils.comIsInitialized()) {
            throw new AssertionError("COM not initialized");
        }
        try {
            ComInterface comInterface = (ComInterface) cls.getAnnotation(ComInterface.class);
            if (null == comInterface) {
                throw new COMException("queryInterface: Interface must define a value for iid via the ComInterface annotation");
            }
            Guid.IID iid = getIID(comInterface);
            PointerByReference pointerByReference = new PointerByReference();
            WinNT.HRESULT hresultQueryInterface = getRawDispatch().QueryInterface(new Guid.REFIID(iid), pointerByReference);
            if (WinNT.S_OK.equals(hresultQueryInterface)) {
                Dispatch dispatch = new Dispatch(pointerByReference.getValue());
                T t = (T) this.factory.createProxy(cls, dispatch);
                dispatch.Release();
                return t;
            }
            throw new COMException("queryInterface: " + Kernel32Util.formatMessage(hresultQueryInterface), hresultQueryInterface);
        } catch (RuntimeException e) {
            if (e instanceof COMException) {
                throw e;
            }
            throw new COMException("Error occured when trying to query for interface " + cls.getName(), e);
        }
    }

    private Guid.IID getIID(ComInterface annotation) {
        String iidStr = annotation.iid();
        if (null != iidStr && !iidStr.isEmpty()) {
            return new Guid.IID(iidStr);
        }
        throw new COMException("ComInterface must define a value for iid");
    }

    private String getAccessorName(Method method, ComProperty prop) {
        if (prop.name().isEmpty()) {
            String methName = method.getName();
            if (methName.startsWith("get")) {
                return methName.replaceFirst("get", "");
            }
            throw new RuntimeException("Property Accessor name must start with 'get', or set the anotation 'name' value");
        }
        return prop.name();
    }

    private String getMutatorName(Method method, ComProperty prop) {
        if (prop.name().isEmpty()) {
            String methName = method.getName();
            if (methName.startsWith("set")) {
                return methName.replaceFirst("set", "");
            }
            throw new RuntimeException("Property Mutator name must start with 'set', or set the anotation 'name' value");
        }
        return prop.name();
    }

    private String getMethodName(Method method, ComMethod meth) {
        if (meth.name().isEmpty()) {
            String methName = method.getName();
            return methName;
        }
        return meth.name();
    }

    protected OaIdl.DISPID resolveDispId(String name) {
        return resolveDispId(getRawDispatch(), name);
    }

    protected WinNT.HRESULT oleMethod(int nType, Variant.VARIANT.ByReference pvResult, String name, Variant.VARIANT pArg) throws COMException {
        return oleMethod(nType, pvResult, name, new Variant.VARIANT[]{pArg});
    }

    protected WinNT.HRESULT oleMethod(int nType, Variant.VARIANT.ByReference pvResult, OaIdl.DISPID dispId, Variant.VARIANT pArg) throws COMException {
        return oleMethod(nType, pvResult, dispId, new Variant.VARIANT[]{pArg});
    }

    protected WinNT.HRESULT oleMethod(int nType, Variant.VARIANT.ByReference pvResult, String name) throws COMException {
        return oleMethod(nType, pvResult, name, (Variant.VARIANT[]) null);
    }

    protected WinNT.HRESULT oleMethod(int nType, Variant.VARIANT.ByReference pvResult, OaIdl.DISPID dispId) throws COMException {
        return oleMethod(nType, pvResult, dispId, (Variant.VARIANT[]) null);
    }

    protected WinNT.HRESULT oleMethod(int nType, Variant.VARIANT.ByReference pvResult, String name, Variant.VARIANT[] pArgs) throws COMException {
        return oleMethod(nType, pvResult, resolveDispId(name), pArgs);
    }

    protected WinNT.HRESULT oleMethod(int nType, Variant.VARIANT.ByReference pvResult, OaIdl.DISPID dispId, Variant.VARIANT[] pArgs) throws COMException {
        return oleMethod(nType, pvResult, getRawDispatch(), dispId, pArgs);
    }

    @Deprecated
    protected OaIdl.DISPID resolveDispId(com.sun.jna.platform.win32.COM.IDispatch pDisp, String name) {
        if (!$assertionsDisabled && !COMUtils.comIsInitialized()) {
            throw new AssertionError("COM not initialized");
        }
        if (pDisp == null) {
            throw new COMException("pDisp (IDispatch) parameter is null!");
        }
        WString[] ptName = {new WString(name)};
        OaIdl.DISPIDByReference pdispID = new OaIdl.DISPIDByReference();
        WinNT.HRESULT hr = pDisp.GetIDsOfNames(new Guid.REFIID(Guid.IID_NULL), ptName, 1, this.factory.getLCID(), pdispID);
        COMUtils.checkRC(hr);
        return pdispID.getValue();
    }

    @Deprecated
    protected WinNT.HRESULT oleMethod(int nType, Variant.VARIANT.ByReference pvResult, com.sun.jna.platform.win32.COM.IDispatch pDisp, String name, Variant.VARIANT pArg) throws COMException {
        return oleMethod(nType, pvResult, pDisp, name, new Variant.VARIANT[]{pArg});
    }

    @Deprecated
    protected WinNT.HRESULT oleMethod(int nType, Variant.VARIANT.ByReference pvResult, com.sun.jna.platform.win32.COM.IDispatch pDisp, OaIdl.DISPID dispId, Variant.VARIANT pArg) throws COMException {
        return oleMethod(nType, pvResult, pDisp, dispId, new Variant.VARIANT[]{pArg});
    }

    @Deprecated
    protected WinNT.HRESULT oleMethod(int nType, Variant.VARIANT.ByReference pvResult, com.sun.jna.platform.win32.COM.IDispatch pDisp, String name) throws COMException {
        return oleMethod(nType, pvResult, pDisp, name, (Variant.VARIANT[]) null);
    }

    @Deprecated
    protected WinNT.HRESULT oleMethod(int nType, Variant.VARIANT.ByReference pvResult, com.sun.jna.platform.win32.COM.IDispatch pDisp, OaIdl.DISPID dispId) throws COMException {
        return oleMethod(nType, pvResult, pDisp, dispId, (Variant.VARIANT[]) null);
    }

    @Deprecated
    protected WinNT.HRESULT oleMethod(int nType, Variant.VARIANT.ByReference pvResult, com.sun.jna.platform.win32.COM.IDispatch pDisp, String name, Variant.VARIANT[] pArgs) throws COMException {
        return oleMethod(nType, pvResult, pDisp, resolveDispId(pDisp, name), pArgs);
    }

    @Deprecated
    protected WinNT.HRESULT oleMethod(int nType, Variant.VARIANT.ByReference pvResult, com.sun.jna.platform.win32.COM.IDispatch pDisp, OaIdl.DISPID dispId, Variant.VARIANT[] pArgs) throws COMException {
        int finalNType;
        if (!$assertionsDisabled && !COMUtils.comIsInitialized()) {
            throw new AssertionError("COM not initialized");
        }
        if (pDisp == null) {
            throw new COMException("pDisp (IDispatch) parameter is null!");
        }
        int _argsLen = 0;
        Variant.VARIANT[] _args = null;
        OleAuto.DISPPARAMS.ByReference dp = new OleAuto.DISPPARAMS.ByReference();
        OaIdl.EXCEPINFO.ByReference pExcepInfo = new OaIdl.EXCEPINFO.ByReference();
        IntByReference puArgErr = new IntByReference();
        if (pArgs != null && pArgs.length > 0) {
            _argsLen = pArgs.length;
            _args = new Variant.VARIANT[_argsLen];
            int revCount = _argsLen;
            for (int i = 0; i < _argsLen; i++) {
                revCount--;
                _args[i] = pArgs[revCount];
            }
        }
        if (nType == 4) {
            dp.setRgdispidNamedArgs(new OaIdl.DISPID[]{OaIdl.DISPID_PROPERTYPUT});
        }
        if (nType == 1 || nType == 2) {
            finalNType = 3;
        } else {
            finalNType = nType;
        }
        if (_argsLen > 0) {
            dp.setArgs(_args);
            dp.write();
        }
        WinNT.HRESULT hr = pDisp.Invoke(dispId, new Guid.REFIID(Guid.IID_NULL), this.factory.getLCID(), new WinDef.WORD(finalNType), dp, pvResult, pExcepInfo, puArgErr);
        COMUtils.checkRC(hr, pExcepInfo, puArgErr);
        return hr;
    }
}
