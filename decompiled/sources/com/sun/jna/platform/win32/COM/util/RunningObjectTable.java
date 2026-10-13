package com.sun.jna.platform.win32.COM.util;

import com.sun.jna.platform.win32.COM.COMException;
import com.sun.jna.platform.win32.COM.COMUtils;
import com.sun.jna.platform.win32.WinNT;
import com.sun.jna.ptr.PointerByReference;
import java.util.ArrayList;
import java.util.List;

/* JADX INFO: loaded from: autofirma.jar:com/sun/jna/platform/win32/COM/util/RunningObjectTable.class */
public class RunningObjectTable implements IRunningObjectTable {
    ObjectFactory factory;
    com.sun.jna.platform.win32.COM.RunningObjectTable raw;
    static final /* synthetic */ boolean $assertionsDisabled;

    static {
        $assertionsDisabled = !RunningObjectTable.class.desiredAssertionStatus();
    }

    protected RunningObjectTable(com.sun.jna.platform.win32.COM.RunningObjectTable raw, ObjectFactory factory) {
        this.raw = raw;
        this.factory = factory;
    }

    @Override // com.sun.jna.platform.win32.COM.util.IRunningObjectTable
    public Iterable<IDispatch> enumRunning() {
        if (!$assertionsDisabled && !COMUtils.comIsInitialized()) {
            throw new AssertionError("COM not initialized");
        }
        PointerByReference ppenumMoniker = new PointerByReference();
        WinNT.HRESULT hr = this.raw.EnumRunning(ppenumMoniker);
        COMUtils.checkRC(hr);
        com.sun.jna.platform.win32.COM.EnumMoniker raw = new com.sun.jna.platform.win32.COM.EnumMoniker(ppenumMoniker.getValue());
        return new EnumMoniker(raw, this.raw, this.factory);
    }

    @Override // com.sun.jna.platform.win32.COM.util.IRunningObjectTable
    public <T> List<T> getActiveObjectsByInterface(Class<T> comInterface) {
        if (!$assertionsDisabled && !COMUtils.comIsInitialized()) {
            throw new AssertionError("COM not initialized");
        }
        ArrayList arrayList = new ArrayList();
        for (IDispatch obj : enumRunning()) {
            try {
                arrayList.add(obj.queryInterface(comInterface));
            } catch (COMException e) {
            }
        }
        return arrayList;
    }
}
