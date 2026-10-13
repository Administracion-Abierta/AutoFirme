package com.sun.jna.platform.win32.COM.util;

import com.sun.jna.platform.win32.COM.COMException;
import com.sun.jna.platform.win32.COM.COMUtils;
import com.sun.jna.platform.win32.COM.Dispatch;
import com.sun.jna.platform.win32.COM.IDispatchCallback;
import com.sun.jna.platform.win32.COM.util.annotation.ComObject;
import com.sun.jna.platform.win32.Guid;
import com.sun.jna.platform.win32.Kernel32;
import com.sun.jna.platform.win32.Ole32;
import com.sun.jna.platform.win32.OleAuto;
import com.sun.jna.platform.win32.WinDef;
import com.sun.jna.platform.win32.WinNT;
import com.sun.jna.ptr.PointerByReference;
import java.lang.ref.WeakReference;
import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

/* JADX INFO: loaded from: autofirma.jar:com/sun/jna/platform/win32/COM/util/ObjectFactory.class */
public class ObjectFactory {
    private final List<WeakReference<ProxyObject>> registeredObjects = new LinkedList();
    private static final WinDef.LCID LOCALE_USER_DEFAULT;
    private WinDef.LCID LCID;
    static final /* synthetic */ boolean $assertionsDisabled;

    static {
        $assertionsDisabled = !ObjectFactory.class.desiredAssertionStatus();
        LOCALE_USER_DEFAULT = Kernel32.INSTANCE.GetUserDefaultLCID();
    }

    protected void finalize() throws Throwable {
        try {
            disposeAll();
        } finally {
            super.finalize();
        }
    }

    public IRunningObjectTable getRunningObjectTable() {
        if (!$assertionsDisabled && !COMUtils.comIsInitialized()) {
            throw new AssertionError("COM not initialized");
        }
        PointerByReference rotPtr = new PointerByReference();
        WinNT.HRESULT hr = Ole32.INSTANCE.GetRunningObjectTable(new WinDef.DWORD(0L), rotPtr);
        COMUtils.checkRC(hr);
        com.sun.jna.platform.win32.COM.RunningObjectTable raw = new com.sun.jna.platform.win32.COM.RunningObjectTable(rotPtr.getValue());
        IRunningObjectTable rot = new RunningObjectTable(raw, this);
        return rot;
    }

    public <T> T createProxy(Class<T> comInterface, com.sun.jna.platform.win32.COM.IDispatch dispatch) {
        if (!$assertionsDisabled && !COMUtils.comIsInitialized()) {
            throw new AssertionError("COM not initialized");
        }
        ProxyObject jop = new ProxyObject(comInterface, dispatch, this);
        Object proxy = Proxy.newProxyInstance(comInterface.getClassLoader(), new Class[]{comInterface}, jop);
        T result = comInterface.cast(proxy);
        return result;
    }

    public <T> T createObject(Class<T> cls) {
        if (!$assertionsDisabled && !COMUtils.comIsInitialized()) {
            throw new AssertionError("COM not initialized");
        }
        ComObject comObject = (ComObject) cls.getAnnotation(ComObject.class);
        if (null == comObject) {
            throw new COMException("createObject: Interface must define a value for either clsId or progId via the ComInterface annotation");
        }
        Guid.GUID guidDiscoverClsId = discoverClsId(comObject);
        PointerByReference pointerByReference = new PointerByReference();
        COMUtils.checkRC(Ole32.INSTANCE.CoCreateInstance(guidDiscoverClsId, null, 21, com.sun.jna.platform.win32.COM.IDispatch.IID_IDISPATCH, pointerByReference));
        Dispatch dispatch = new Dispatch(pointerByReference.getValue());
        T t = (T) createProxy(cls, dispatch);
        dispatch.Release();
        return t;
    }

    public <T> T fetchObject(Class<T> cls) throws COMException {
        if (!$assertionsDisabled && !COMUtils.comIsInitialized()) {
            throw new AssertionError("COM not initialized");
        }
        ComObject comObject = (ComObject) cls.getAnnotation(ComObject.class);
        if (null == comObject) {
            throw new COMException("createObject: Interface must define a value for either clsId or progId via the ComInterface annotation");
        }
        Guid.GUID guidDiscoverClsId = discoverClsId(comObject);
        PointerByReference pointerByReference = new PointerByReference();
        COMUtils.checkRC(OleAuto.INSTANCE.GetActiveObject(guidDiscoverClsId, null, pointerByReference));
        Dispatch dispatch = new Dispatch(pointerByReference.getValue());
        T t = (T) createProxy(cls, dispatch);
        dispatch.Release();
        return t;
    }

    Guid.GUID discoverClsId(ComObject annotation) {
        if (!$assertionsDisabled && !COMUtils.comIsInitialized()) {
            throw new AssertionError("COM not initialized");
        }
        String clsIdStr = annotation.clsId();
        String progIdStr = annotation.progId();
        if (null != clsIdStr && !clsIdStr.isEmpty()) {
            return new Guid.CLSID(clsIdStr);
        }
        if (null != progIdStr && !progIdStr.isEmpty()) {
            Guid.CLSID.ByReference rclsid = new Guid.CLSID.ByReference();
            WinNT.HRESULT hr = Ole32.INSTANCE.CLSIDFromProgID(progIdStr, rclsid);
            COMUtils.checkRC(hr);
            return rclsid;
        }
        throw new COMException("ComObject must define a value for either clsId or progId");
    }

    IDispatchCallback createDispatchCallback(Class<?> comEventCallbackInterface, IComEventCallbackListener comEventCallbackListener) {
        return new CallbackProxy(this, comEventCallbackInterface, comEventCallbackListener);
    }

    public void register(ProxyObject proxyObject) {
        synchronized (this.registeredObjects) {
            this.registeredObjects.add(new WeakReference<>(proxyObject));
        }
    }

    public void unregister(ProxyObject proxyObject) {
        synchronized (this.registeredObjects) {
            Iterator<WeakReference<ProxyObject>> iterator = this.registeredObjects.iterator();
            while (iterator.hasNext()) {
                WeakReference<ProxyObject> weakRef = iterator.next();
                ProxyObject po = weakRef.get();
                if (po == null || po == proxyObject) {
                    iterator.remove();
                }
            }
        }
    }

    public void disposeAll() {
        synchronized (this.registeredObjects) {
            List<WeakReference<ProxyObject>> s = new ArrayList<>(this.registeredObjects);
            for (WeakReference<ProxyObject> weakRef : s) {
                ProxyObject po = weakRef.get();
                if (po != null) {
                    po.dispose();
                }
            }
            this.registeredObjects.clear();
        }
    }

    public WinDef.LCID getLCID() {
        if (this.LCID != null) {
            return this.LCID;
        }
        return LOCALE_USER_DEFAULT;
    }

    public void setLCID(WinDef.LCID value) {
        this.LCID = value;
    }
}
