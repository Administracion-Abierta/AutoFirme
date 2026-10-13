package com.sun.jna.platform.win32.COM.util;

import com.sun.jna.platform.win32.COM.COMUtils;
import com.sun.jna.platform.win32.COM.Dispatch;
import com.sun.jna.platform.win32.COM.IEnumMoniker;
import com.sun.jna.platform.win32.COM.Moniker;
import com.sun.jna.platform.win32.WinDef;
import com.sun.jna.platform.win32.WinNT;
import com.sun.jna.ptr.PointerByReference;
import java.util.Iterator;

/* JADX INFO: loaded from: autofirma.jar:com/sun/jna/platform/win32/COM/util/EnumMoniker.class */
public class EnumMoniker implements Iterable<IDispatch> {
    ObjectFactory factory;
    com.sun.jna.platform.win32.COM.IRunningObjectTable rawRot;
    IEnumMoniker raw;
    Moniker rawNext;
    static final /* synthetic */ boolean $assertionsDisabled;

    static {
        $assertionsDisabled = !EnumMoniker.class.desiredAssertionStatus();
    }

    protected EnumMoniker(IEnumMoniker raw, com.sun.jna.platform.win32.COM.IRunningObjectTable rawRot, ObjectFactory factory) {
        if (!$assertionsDisabled && !COMUtils.comIsInitialized()) {
            throw new AssertionError("COM not initialized");
        }
        this.rawRot = rawRot;
        this.raw = raw;
        this.factory = factory;
        WinNT.HRESULT hr = raw.Reset();
        COMUtils.checkRC(hr);
        cacheNext();
    }

    protected void cacheNext() {
        if (!$assertionsDisabled && !COMUtils.comIsInitialized()) {
            throw new AssertionError("COM not initialized");
        }
        PointerByReference rgelt = new PointerByReference();
        WinDef.ULONGByReference pceltFetched = new WinDef.ULONGByReference();
        WinNT.HRESULT hr = this.raw.Next(new WinDef.ULONG(1L), rgelt, pceltFetched);
        if (WinNT.S_OK.equals(hr) && pceltFetched.getValue().intValue() > 0) {
            this.rawNext = new Moniker(rgelt.getValue());
            return;
        }
        if (!WinNT.S_FALSE.equals(hr)) {
            COMUtils.checkRC(hr);
        }
        this.rawNext = null;
    }

    @Override // java.lang.Iterable
    public Iterator<IDispatch> iterator() {
        return new Iterator<IDispatch>() { // from class: com.sun.jna.platform.win32.COM.util.EnumMoniker.1
            static final /* synthetic */ boolean $assertionsDisabled;

            static {
                $assertionsDisabled = !EnumMoniker.class.desiredAssertionStatus();
            }

            @Override // java.util.Iterator
            public boolean hasNext() {
                return null != EnumMoniker.this.rawNext;
            }

            /* JADX WARN: Can't rename method to resolve collision */
            @Override // java.util.Iterator
            public IDispatch next() {
                if (!$assertionsDisabled && !COMUtils.comIsInitialized()) {
                    throw new AssertionError("COM not initialized");
                }
                Moniker moniker = EnumMoniker.this.rawNext;
                PointerByReference ppunkObject = new PointerByReference();
                WinNT.HRESULT hr = EnumMoniker.this.rawRot.GetObject(moniker.getPointer(), ppunkObject);
                COMUtils.checkRC(hr);
                Dispatch dispatch = new Dispatch(ppunkObject.getValue());
                EnumMoniker.this.cacheNext();
                IDispatch d = (IDispatch) EnumMoniker.this.factory.createProxy(IDispatch.class, dispatch);
                dispatch.Release();
                return d;
            }

            @Override // java.util.Iterator
            public void remove() {
                throw new UnsupportedOperationException("remove");
            }
        };
    }
}
