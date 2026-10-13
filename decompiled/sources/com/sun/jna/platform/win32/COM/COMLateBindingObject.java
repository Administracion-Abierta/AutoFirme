package com.sun.jna.platform.win32.COM;

import com.sun.jna.platform.win32.Guid;
import com.sun.jna.platform.win32.OleAuto;
import com.sun.jna.platform.win32.Variant;
import java.util.Date;

/* JADX INFO: loaded from: autofirma.jar:com/sun/jna/platform/win32/COM/COMLateBindingObject.class */
public class COMLateBindingObject extends COMBindingBaseObject {
    public COMLateBindingObject(IDispatch iDispatch) {
        super(iDispatch);
    }

    public COMLateBindingObject(Guid.CLSID clsid, boolean useActiveInstance) {
        super(clsid, useActiveInstance);
    }

    public COMLateBindingObject(String progId, boolean useActiveInstance) throws COMException {
        super(progId, useActiveInstance);
    }

    protected IDispatch getAutomationProperty(String propertyName) {
        Variant.VARIANT.ByReference result = new Variant.VARIANT.ByReference();
        oleMethod(2, result, propertyName);
        return (IDispatch) result.getValue();
    }

    protected IDispatch getAutomationProperty(String propertyName, Variant.VARIANT value) {
        Variant.VARIANT.ByReference result = new Variant.VARIANT.ByReference();
        oleMethod(2, result, propertyName, value);
        return (IDispatch) result.getValue();
    }

    @Deprecated
    protected IDispatch getAutomationProperty(String propertyName, COMLateBindingObject comObject) {
        Variant.VARIANT.ByReference result = new Variant.VARIANT.ByReference();
        oleMethod(2, result, propertyName);
        return (IDispatch) result.getValue();
    }

    @Deprecated
    protected IDispatch getAutomationProperty(String propertyName, COMLateBindingObject comObject, Variant.VARIANT value) {
        Variant.VARIANT.ByReference result = new Variant.VARIANT.ByReference();
        oleMethod(2, result, propertyName, value);
        return (IDispatch) result.getValue();
    }

    @Deprecated
    protected IDispatch getAutomationProperty(String propertyName, IDispatch iDispatch) {
        Variant.VARIANT.ByReference result = new Variant.VARIANT.ByReference();
        oleMethod(2, result, propertyName);
        return (IDispatch) result.getValue();
    }

    protected boolean getBooleanProperty(String propertyName) {
        Variant.VARIANT.ByReference result = new Variant.VARIANT.ByReference();
        oleMethod(2, result, propertyName);
        return result.booleanValue();
    }

    protected Date getDateProperty(String propertyName) {
        Variant.VARIANT.ByReference result = new Variant.VARIANT.ByReference();
        oleMethod(2, result, propertyName);
        return result.dateValue();
    }

    protected int getIntProperty(String propertyName) {
        Variant.VARIANT.ByReference result = new Variant.VARIANT.ByReference();
        oleMethod(2, result, propertyName);
        return result.intValue();
    }

    protected short getShortProperty(String propertyName) {
        Variant.VARIANT.ByReference result = new Variant.VARIANT.ByReference();
        oleMethod(2, result, propertyName);
        return result.shortValue();
    }

    protected String getStringProperty(String propertyName) {
        Variant.VARIANT.ByReference result = new Variant.VARIANT.ByReference();
        oleMethod(2, result, propertyName);
        String res = result.stringValue();
        OleAuto.INSTANCE.VariantClear(result);
        return res;
    }

    protected Variant.VARIANT invoke(String methodName) {
        Variant.VARIANT.ByReference result = new Variant.VARIANT.ByReference();
        oleMethod(1, result, methodName);
        return result;
    }

    protected Variant.VARIANT invoke(String methodName, Variant.VARIANT arg) {
        Variant.VARIANT.ByReference result = new Variant.VARIANT.ByReference();
        oleMethod(1, result, methodName, arg);
        return result;
    }

    protected Variant.VARIANT invoke(String methodName, Variant.VARIANT[] args) {
        Variant.VARIANT.ByReference result = new Variant.VARIANT.ByReference();
        oleMethod(1, result, methodName, args);
        return result;
    }

    protected Variant.VARIANT invoke(String methodName, Variant.VARIANT arg1, Variant.VARIANT arg2) {
        return invoke(methodName, new Variant.VARIANT[]{arg1, arg2});
    }

    protected Variant.VARIANT invoke(String methodName, Variant.VARIANT arg1, Variant.VARIANT arg2, Variant.VARIANT arg3) {
        return invoke(methodName, new Variant.VARIANT[]{arg1, arg2, arg3});
    }

    protected Variant.VARIANT invoke(String methodName, Variant.VARIANT arg1, Variant.VARIANT arg2, Variant.VARIANT arg3, Variant.VARIANT arg4) {
        return invoke(methodName, new Variant.VARIANT[]{arg1, arg2, arg3, arg4});
    }

    @Deprecated
    protected void invokeNoReply(String methodName, IDispatch dispatch) {
        oleMethod(1, (Variant.VARIANT.ByReference) null, dispatch, methodName);
    }

    @Deprecated
    protected void invokeNoReply(String methodName, COMLateBindingObject comObject) {
        oleMethod(1, (Variant.VARIANT.ByReference) null, comObject.getIDispatch(), methodName);
    }

    protected void invokeNoReply(String methodName, Variant.VARIANT arg) {
        oleMethod(1, (Variant.VARIANT.ByReference) null, methodName, arg);
    }

    @Deprecated
    protected void invokeNoReply(String methodName, IDispatch dispatch, Variant.VARIANT arg) {
        oleMethod(1, (Variant.VARIANT.ByReference) null, dispatch, methodName, arg);
    }

    @Deprecated
    protected void invokeNoReply(String methodName, IDispatch dispatch, Variant.VARIANT arg1, Variant.VARIANT arg2) {
        oleMethod(1, (Variant.VARIANT.ByReference) null, dispatch, methodName, new Variant.VARIANT[]{arg1, arg2});
    }

    @Deprecated
    protected void invokeNoReply(String methodName, COMLateBindingObject comObject, Variant.VARIANT arg1, Variant.VARIANT arg2) {
        oleMethod(1, (Variant.VARIANT.ByReference) null, comObject.getIDispatch(), methodName, new Variant.VARIANT[]{arg1, arg2});
    }

    @Deprecated
    protected void invokeNoReply(String methodName, COMLateBindingObject comObject, Variant.VARIANT arg) {
        oleMethod(1, (Variant.VARIANT.ByReference) null, comObject.getIDispatch(), methodName, arg);
    }

    @Deprecated
    protected void invokeNoReply(String methodName, IDispatch dispatch, Variant.VARIANT[] args) {
        oleMethod(1, (Variant.VARIANT.ByReference) null, dispatch, methodName, args);
    }

    protected void invokeNoReply(String methodName) {
        Variant.VARIANT.ByReference result = new Variant.VARIANT.ByReference();
        oleMethod(1, result, methodName);
    }

    protected void invokeNoReply(String methodName, Variant.VARIANT[] args) {
        Variant.VARIANT.ByReference result = new Variant.VARIANT.ByReference();
        oleMethod(1, result, methodName, args);
    }

    protected void invokeNoReply(String methodName, Variant.VARIANT arg1, Variant.VARIANT arg2) {
        invokeNoReply(methodName, new Variant.VARIANT[]{arg1, arg2});
    }

    protected void invokeNoReply(String methodName, Variant.VARIANT arg1, Variant.VARIANT arg2, Variant.VARIANT arg3) {
        invokeNoReply(methodName, new Variant.VARIANT[]{arg1, arg2, arg3});
    }

    protected void invokeNoReply(String methodName, Variant.VARIANT arg1, Variant.VARIANT arg2, Variant.VARIANT arg3, Variant.VARIANT arg4) {
        invokeNoReply(methodName, new Variant.VARIANT[]{arg1, arg2, arg3, arg4});
    }

    protected void setProperty(String propertyName, boolean value) {
        oleMethod(4, (Variant.VARIANT.ByReference) null, propertyName, new Variant.VARIANT(value));
    }

    protected void setProperty(String propertyName, Date value) {
        oleMethod(4, (Variant.VARIANT.ByReference) null, propertyName, new Variant.VARIANT(value));
    }

    protected void setProperty(String propertyName, Dispatch value) {
        oleMethod(4, (Variant.VARIANT.ByReference) null, propertyName, new Variant.VARIANT(value));
    }

    @Deprecated
    protected void setProperty(String propertyName, IDispatch value) {
        oleMethod(4, (Variant.VARIANT.ByReference) null, propertyName, new Variant.VARIANT(value));
    }

    protected void setProperty(String propertyName, int value) {
        oleMethod(4, (Variant.VARIANT.ByReference) null, propertyName, new Variant.VARIANT(value));
    }

    protected void setProperty(String propertyName, short value) {
        oleMethod(4, (Variant.VARIANT.ByReference) null, propertyName, new Variant.VARIANT(value));
    }

    protected void setProperty(String propertyName, String value) {
        Variant.VARIANT wrappedValue = new Variant.VARIANT(value);
        try {
            oleMethod(4, (Variant.VARIANT.ByReference) null, propertyName, wrappedValue);
        } finally {
            OleAuto.INSTANCE.VariantClear(wrappedValue);
        }
    }

    protected void setProperty(String propertyName, Variant.VARIANT value) {
        oleMethod(4, (Variant.VARIANT.ByReference) null, propertyName, value);
    }

    @Deprecated
    protected void setProperty(String propertyName, IDispatch iDispatch, Variant.VARIANT value) {
        oleMethod(4, (Variant.VARIANT.ByReference) null, iDispatch, propertyName, value);
    }

    @Deprecated
    protected void setProperty(String propertyName, COMLateBindingObject comObject, Variant.VARIANT value) {
        oleMethod(4, (Variant.VARIANT.ByReference) null, comObject.getIDispatch(), propertyName, value);
    }

    public Variant.VARIANT toVariant() {
        return new Variant.VARIANT(getIDispatch());
    }
}
