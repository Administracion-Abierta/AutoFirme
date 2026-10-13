package com.sun.jna.platform.win32.COM.util;

import com.sun.jna.Pointer;
import com.sun.jna.WString;
import com.sun.jna.platform.win32.COM.COMException;
import com.sun.jna.platform.win32.COM.COMUtils;
import com.sun.jna.platform.win32.COM.Dispatch;
import com.sun.jna.platform.win32.COM.DispatchListener;
import com.sun.jna.platform.win32.COM.IDispatchCallback;
import com.sun.jna.platform.win32.COM.Unknown;
import com.sun.jna.platform.win32.COM.util.annotation.ComEventCallback;
import com.sun.jna.platform.win32.COM.util.annotation.ComInterface;
import com.sun.jna.platform.win32.COM.util.annotation.ComMethod;
import com.sun.jna.platform.win32.Guid;
import com.sun.jna.platform.win32.OaIdl;
import com.sun.jna.platform.win32.OleAuto;
import com.sun.jna.platform.win32.Variant;
import com.sun.jna.platform.win32.WinDef;
import com.sun.jna.platform.win32.WinError;
import com.sun.jna.platform.win32.WinNT;
import com.sun.jna.ptr.IntByReference;
import com.sun.jna.ptr.PointerByReference;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/* JADX INFO: loaded from: autofirma.jar:com/sun/jna/platform/win32/COM/util/CallbackProxy.class */
public class CallbackProxy implements IDispatchCallback {
    private static boolean DEFAULT_BOOLEAN;
    private static byte DEFAULT_BYTE;
    private static short DEFAULT_SHORT;
    private static int DEFAULT_INT;
    private static long DEFAULT_LONG;
    private static float DEFAULT_FLOAT;
    private static double DEFAULT_DOUBLE;
    ObjectFactory factory;
    Class<?> comEventCallbackInterface;
    IComEventCallbackListener comEventCallbackListener;
    Guid.REFIID listenedToRiid;
    public DispatchListener dispatchListener = new DispatchListener(this);
    Map<OaIdl.DISPID, Method> dsipIdMap;
    static final /* synthetic */ boolean $assertionsDisabled;

    static {
        $assertionsDisabled = !CallbackProxy.class.desiredAssertionStatus();
    }

    public CallbackProxy(ObjectFactory factory, Class<?> comEventCallbackInterface, IComEventCallbackListener comEventCallbackListener) {
        this.factory = factory;
        this.comEventCallbackInterface = comEventCallbackInterface;
        this.comEventCallbackListener = comEventCallbackListener;
        this.listenedToRiid = createRIID(comEventCallbackInterface);
        this.dsipIdMap = createDispIdMap(comEventCallbackInterface);
    }

    Guid.REFIID createRIID(Class<?> comEventCallbackInterface) {
        ComInterface comInterfaceAnnotation = (ComInterface) comEventCallbackInterface.getAnnotation(ComInterface.class);
        if (null == comInterfaceAnnotation) {
            throw new COMException("advise: Interface must define a value for either iid via the ComInterface annotation");
        }
        String iidStr = comInterfaceAnnotation.iid();
        if (null == iidStr || iidStr.isEmpty()) {
            throw new COMException("ComInterface must define a value for iid");
        }
        return new Guid.REFIID(new Guid.IID(iidStr).getPointer());
    }

    Map<OaIdl.DISPID, Method> createDispIdMap(Class<?> comEventCallbackInterface) {
        Map<OaIdl.DISPID, Method> map = new HashMap<>();
        for (Method meth : comEventCallbackInterface.getMethods()) {
            ComEventCallback callbackAnnotation = (ComEventCallback) meth.getAnnotation(ComEventCallback.class);
            ComMethod methodAnnotation = (ComMethod) meth.getAnnotation(ComMethod.class);
            if (methodAnnotation != null) {
                int dispId = methodAnnotation.dispId();
                if (-1 == dispId) {
                    dispId = fetchDispIdFromName(callbackAnnotation);
                }
                if (dispId == -1) {
                    this.comEventCallbackListener.errorReceivingCallbackEvent("DISPID for " + meth.getName() + " not found", null);
                }
                map.put(new OaIdl.DISPID(dispId), meth);
            } else if (null != callbackAnnotation) {
                int dispId2 = callbackAnnotation.dispid();
                if (-1 == dispId2) {
                    dispId2 = fetchDispIdFromName(callbackAnnotation);
                }
                if (dispId2 == -1) {
                    this.comEventCallbackListener.errorReceivingCallbackEvent("DISPID for " + meth.getName() + " not found", null);
                }
                map.put(new OaIdl.DISPID(dispId2), meth);
            }
        }
        return map;
    }

    int fetchDispIdFromName(ComEventCallback annotation) {
        return -1;
    }

    void invokeOnThread(OaIdl.DISPID dispIdMember, Guid.REFIID riid, WinDef.LCID lcid, WinDef.WORD wFlags, OleAuto.DISPPARAMS.ByReference pDispParams) {
        Variant.VARIANT[] arguments = pDispParams.getArgs();
        Method eventMethod = this.dsipIdMap.get(dispIdMember);
        if (eventMethod == null) {
            this.comEventCallbackListener.errorReceivingCallbackEvent("No method found with dispId = " + dispIdMember, null);
            return;
        }
        OaIdl.DISPID[] positionMap = pDispParams.getRgdispidNamedArgs();
        Class<?>[] paramTypes = eventMethod.getParameterTypes();
        Object[] params = new Object[paramTypes.length];
        for (int i = 0; i < params.length && (arguments.length - positionMap.length) - i > 0; i++) {
            Class<?> cls = paramTypes[i];
            Variant.VARIANT varg = arguments[(arguments.length - i) - 1];
            params[i] = Convert.toJavaObject(varg, cls, this.factory, true, false);
        }
        for (int i2 = 0; i2 < positionMap.length; i2++) {
            int targetPosition = positionMap[i2].intValue();
            if (targetPosition < params.length) {
                Class<?> cls2 = paramTypes[targetPosition];
                Variant.VARIANT varg2 = arguments[i2];
                params[targetPosition] = Convert.toJavaObject(varg2, cls2, this.factory, true, false);
            }
        }
        for (int i3 = 0; i3 < params.length; i3++) {
            if (params[i3] == null && paramTypes[i3].isPrimitive()) {
                if (paramTypes[i3].equals(Boolean.TYPE)) {
                    params[i3] = Boolean.valueOf(DEFAULT_BOOLEAN);
                } else if (paramTypes[i3].equals(Byte.TYPE)) {
                    params[i3] = Byte.valueOf(DEFAULT_BYTE);
                } else if (paramTypes[i3].equals(Short.TYPE)) {
                    params[i3] = Short.valueOf(DEFAULT_SHORT);
                } else if (paramTypes[i3].equals(Integer.TYPE)) {
                    params[i3] = Integer.valueOf(DEFAULT_INT);
                } else if (paramTypes[i3].equals(Long.TYPE)) {
                    params[i3] = Long.valueOf(DEFAULT_LONG);
                } else if (paramTypes[i3].equals(Float.TYPE)) {
                    params[i3] = Float.valueOf(DEFAULT_FLOAT);
                } else if (paramTypes[i3].equals(Double.TYPE)) {
                    params[i3] = Double.valueOf(DEFAULT_DOUBLE);
                } else {
                    throw new IllegalArgumentException("Class type " + paramTypes[i3].getName() + " not mapped to primitive default value.");
                }
            }
        }
        try {
            eventMethod.invoke(this.comEventCallbackListener, params);
        } catch (Exception e) {
            List<String> decodedClassNames = new ArrayList<>(params.length);
            for (Object o : params) {
                if (o == null) {
                    decodedClassNames.add("NULL");
                } else {
                    decodedClassNames.add(o.getClass().getName());
                }
            }
            this.comEventCallbackListener.errorReceivingCallbackEvent("Exception invoking method " + eventMethod + " supplied: " + decodedClassNames.toString(), e);
        }
    }

    @Override // com.sun.jna.platform.win32.COM.IUnknownCallback
    public Pointer getPointer() {
        return this.dispatchListener.getPointer();
    }

    @Override // com.sun.jna.platform.win32.COM.IDispatch
    public WinNT.HRESULT GetTypeInfoCount(WinDef.UINTByReference pctinfo) {
        return new WinNT.HRESULT(WinError.E_NOTIMPL);
    }

    @Override // com.sun.jna.platform.win32.COM.IDispatch
    public WinNT.HRESULT GetTypeInfo(WinDef.UINT iTInfo, WinDef.LCID lcid, PointerByReference ppTInfo) {
        return new WinNT.HRESULT(WinError.E_NOTIMPL);
    }

    @Override // com.sun.jna.platform.win32.COM.IDispatch
    public WinNT.HRESULT GetIDsOfNames(Guid.REFIID riid, WString[] rgszNames, int cNames, WinDef.LCID lcid, OaIdl.DISPIDByReference rgDispId) {
        return new WinNT.HRESULT(WinError.E_NOTIMPL);
    }

    @Override // com.sun.jna.platform.win32.COM.IDispatch
    public WinNT.HRESULT Invoke(OaIdl.DISPID dispIdMember, Guid.REFIID riid, WinDef.LCID lcid, WinDef.WORD wFlags, OleAuto.DISPPARAMS.ByReference pDispParams, Variant.VARIANT.ByReference pVarResult, OaIdl.EXCEPINFO.ByReference pExcepInfo, IntByReference puArgErr) {
        if (!$assertionsDisabled && !COMUtils.comIsInitialized()) {
            throw new AssertionError("Assumption about COM threading broken.");
        }
        invokeOnThread(dispIdMember, riid, lcid, wFlags, pDispParams);
        return WinError.S_OK;
    }

    @Override // com.sun.jna.platform.win32.COM.IUnknown
    public WinNT.HRESULT QueryInterface(Guid.REFIID refid, PointerByReference ppvObject) {
        if (null == ppvObject) {
            return new WinNT.HRESULT(WinError.E_POINTER);
        }
        if (refid.equals(this.listenedToRiid)) {
            ppvObject.setValue(getPointer());
            return WinError.S_OK;
        }
        if (refid.getValue().equals(Unknown.IID_IUNKNOWN)) {
            ppvObject.setValue(getPointer());
            return WinError.S_OK;
        }
        if (refid.getValue().equals(Dispatch.IID_IDISPATCH)) {
            ppvObject.setValue(getPointer());
            return WinError.S_OK;
        }
        return new WinNT.HRESULT(WinError.E_NOINTERFACE);
    }

    @Override // com.sun.jna.platform.win32.COM.IUnknown
    public int AddRef() {
        return 0;
    }

    @Override // com.sun.jna.platform.win32.COM.IUnknown
    public int Release() {
        return 0;
    }
}
