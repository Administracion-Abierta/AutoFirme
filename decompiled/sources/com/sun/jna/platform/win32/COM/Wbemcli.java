package com.sun.jna.platform.win32.COM;

import com.sun.jna.Pointer;
import com.sun.jna.WString;
import com.sun.jna.platform.win32.Guid;
import com.sun.jna.platform.win32.OaIdl;
import com.sun.jna.platform.win32.OaIdlUtil;
import com.sun.jna.platform.win32.Ole32;
import com.sun.jna.platform.win32.OleAuto;
import com.sun.jna.platform.win32.Variant;
import com.sun.jna.platform.win32.WTypes;
import com.sun.jna.platform.win32.WinNT;
import com.sun.jna.ptr.IntByReference;
import com.sun.jna.ptr.PointerByReference;

/* JADX INFO: loaded from: autofirma.jar:com/sun/jna/platform/win32/COM/Wbemcli.class */
public interface Wbemcli {
    public static final int WBEM_FLAG_RETURN_IMMEDIATELY = 16;
    public static final int WBEM_FLAG_FORWARD_ONLY = 32;
    public static final int WBEM_INFINITE = -1;
    public static final int WBEM_S_NO_ERROR = 0;
    public static final int WBEM_S_FALSE = 1;
    public static final int WBEM_S_TIMEDOUT = 262148;
    public static final int WBEM_S_NO_MORE_DATA = 262149;
    public static final int WBEM_E_INVALID_NAMESPACE = -2147217394;
    public static final int WBEM_E_INVALID_CLASS = -2147217392;
    public static final int WBEM_E_INVALID_QUERY = -2147217385;
    public static final int CIM_ILLEGAL = 4095;
    public static final int CIM_EMPTY = 0;
    public static final int CIM_SINT8 = 16;
    public static final int CIM_UINT8 = 17;
    public static final int CIM_SINT16 = 2;
    public static final int CIM_UINT16 = 18;
    public static final int CIM_SINT32 = 3;
    public static final int CIM_UINT32 = 19;
    public static final int CIM_SINT64 = 20;
    public static final int CIM_UINT64 = 21;
    public static final int CIM_REAL32 = 4;
    public static final int CIM_REAL64 = 5;
    public static final int CIM_BOOLEAN = 11;
    public static final int CIM_STRING = 8;
    public static final int CIM_DATETIME = 101;
    public static final int CIM_REFERENCE = 102;
    public static final int CIM_CHAR16 = 103;
    public static final int CIM_OBJECT = 13;
    public static final int CIM_FLAG_ARRAY = 8192;

    /* JADX INFO: loaded from: autofirma.jar:com/sun/jna/platform/win32/COM/Wbemcli$WBEM_CONDITION_FLAG_TYPE.class */
    public interface WBEM_CONDITION_FLAG_TYPE {
        public static final int WBEM_FLAG_ALWAYS = 0;
        public static final int WBEM_FLAG_ONLY_IF_TRUE = 1;
        public static final int WBEM_FLAG_ONLY_IF_FALSE = 2;
        public static final int WBEM_FLAG_ONLY_IF_IDENTICAL = 3;
        public static final int WBEM_MASK_PRIMARY_CONDITION = 3;
        public static final int WBEM_FLAG_KEYS_ONLY = 4;
        public static final int WBEM_FLAG_REFS_ONLY = 8;
        public static final int WBEM_FLAG_LOCAL_ONLY = 16;
        public static final int WBEM_FLAG_PROPAGATED_ONLY = 32;
        public static final int WBEM_FLAG_SYSTEM_ONLY = 48;
        public static final int WBEM_FLAG_NONSYSTEM_ONLY = 64;
        public static final int WBEM_MASK_CONDITION_ORIGIN = 112;
        public static final int WBEM_FLAG_CLASS_OVERRIDES_ONLY = 256;
        public static final int WBEM_FLAG_CLASS_LOCAL_AND_OVERRIDES = 512;
        public static final int WBEM_MASK_CLASS_CONDITION = 768;
    }

    /* JADX INFO: loaded from: autofirma.jar:com/sun/jna/platform/win32/COM/Wbemcli$IWbemClassObject.class */
    public static class IWbemClassObject extends Unknown {
        public IWbemClassObject() {
        }

        public IWbemClassObject(Pointer pvInstance) {
            super(pvInstance);
        }

        public WinNT.HRESULT Get(WString wszName, int lFlags, Variant.VARIANT.ByReference pVal, IntByReference pType, IntByReference plFlavor) {
            return (WinNT.HRESULT) _invokeNativeObject(4, new Object[]{getPointer(), wszName, Integer.valueOf(lFlags), pVal, pType, plFlavor}, WinNT.HRESULT.class);
        }

        public WinNT.HRESULT Get(String wszName, int lFlags, Variant.VARIANT.ByReference pVal, IntByReference pType, IntByReference plFlavor) {
            return Get(wszName == null ? null : new WString(wszName), lFlags, pVal, pType, plFlavor);
        }

        public WinNT.HRESULT GetNames(String wszQualifierName, int lFlags, Variant.VARIANT.ByReference pQualifierVal, PointerByReference pNames) {
            return GetNames(wszQualifierName == null ? null : new WString(wszQualifierName), lFlags, pQualifierVal, pNames);
        }

        public WinNT.HRESULT GetNames(WString wszQualifierName, int lFlags, Variant.VARIANT.ByReference pQualifierVal, PointerByReference pNames) {
            return (WinNT.HRESULT) _invokeNativeObject(7, new Object[]{getPointer(), wszQualifierName, Integer.valueOf(lFlags), pQualifierVal, pNames}, WinNT.HRESULT.class);
        }

        public String[] GetNames(String wszQualifierName, int lFlags, Variant.VARIANT.ByReference pQualifierVal) {
            PointerByReference pbr = new PointerByReference();
            COMUtils.checkRC(GetNames(wszQualifierName, lFlags, pQualifierVal, pbr));
            Object[] nameObjects = (Object[]) OaIdlUtil.toPrimitiveArray(new OaIdl.SAFEARRAY(pbr.getValue()), true);
            String[] names = new String[nameObjects.length];
            for (int i = 0; i < nameObjects.length; i++) {
                names[i] = (String) nameObjects[i];
            }
            return names;
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:com/sun/jna/platform/win32/COM/Wbemcli$IEnumWbemClassObject.class */
    public static class IEnumWbemClassObject extends Unknown {
        public IEnumWbemClassObject() {
        }

        public IEnumWbemClassObject(Pointer pvInstance) {
            super(pvInstance);
        }

        public WinNT.HRESULT Next(int lTimeOut, int uCount, Pointer[] ppObjects, IntByReference puReturned) {
            return (WinNT.HRESULT) _invokeNativeObject(4, new Object[]{getPointer(), Integer.valueOf(lTimeOut), Integer.valueOf(uCount), ppObjects, puReturned}, WinNT.HRESULT.class);
        }

        public IWbemClassObject[] Next(int lTimeOut, int uCount) {
            Pointer[] resultArray = new Pointer[uCount];
            IntByReference resultCount = new IntByReference();
            WinNT.HRESULT result = Next(lTimeOut, uCount, resultArray, resultCount);
            COMUtils.checkRC(result);
            IWbemClassObject[] returnValue = new IWbemClassObject[resultCount.getValue()];
            for (int i = 0; i < resultCount.getValue(); i++) {
                returnValue[i] = new IWbemClassObject(resultArray[i]);
            }
            return returnValue;
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:com/sun/jna/platform/win32/COM/Wbemcli$IWbemLocator.class */
    public static class IWbemLocator extends Unknown {
        public static final Guid.CLSID CLSID_WbemLocator = new Guid.CLSID("4590f811-1d3a-11d0-891f-00aa004b2e24");
        public static final Guid.GUID IID_IWbemLocator = new Guid.GUID("dc12a687-737f-11cf-884d-00aa004b2e24");

        public IWbemLocator() {
        }

        private IWbemLocator(Pointer pvInstance) {
            super(pvInstance);
        }

        public static IWbemLocator create() {
            PointerByReference pbr = new PointerByReference();
            WinNT.HRESULT hres = Ole32.INSTANCE.CoCreateInstance(CLSID_WbemLocator, null, 1, IID_IWbemLocator, pbr);
            if (COMUtils.FAILED(hres)) {
                return null;
            }
            return new IWbemLocator(pbr.getValue());
        }

        public WinNT.HRESULT ConnectServer(WTypes.BSTR strNetworkResource, WTypes.BSTR strUser, WTypes.BSTR strPassword, WTypes.BSTR strLocale, int lSecurityFlags, WTypes.BSTR strAuthority, IWbemContext pCtx, PointerByReference ppNamespace) {
            return (WinNT.HRESULT) _invokeNativeObject(3, new Object[]{getPointer(), strNetworkResource, strUser, strPassword, strLocale, Integer.valueOf(lSecurityFlags), strAuthority, pCtx, ppNamespace}, WinNT.HRESULT.class);
        }

        public IWbemServices ConnectServer(String strNetworkResource, String strUser, String strPassword, String strLocale, int lSecurityFlags, String strAuthority, IWbemContext pCtx) {
            WTypes.BSTR strNetworkResourceBSTR = OleAuto.INSTANCE.SysAllocString(strNetworkResource);
            WTypes.BSTR strUserBSTR = OleAuto.INSTANCE.SysAllocString(strUser);
            WTypes.BSTR strPasswordBSTR = OleAuto.INSTANCE.SysAllocString(strPassword);
            WTypes.BSTR strLocaleBSTR = OleAuto.INSTANCE.SysAllocString(strLocale);
            WTypes.BSTR strAuthorityBSTR = OleAuto.INSTANCE.SysAllocString(strAuthority);
            PointerByReference pbr = new PointerByReference();
            try {
                WinNT.HRESULT result = ConnectServer(strNetworkResourceBSTR, strUserBSTR, strPasswordBSTR, strLocaleBSTR, lSecurityFlags, strAuthorityBSTR, pCtx, pbr);
                COMUtils.checkRC(result);
                IWbemServices iWbemServices = new IWbemServices(pbr.getValue());
                return iWbemServices;
            } finally {
                OleAuto.INSTANCE.SysFreeString(strNetworkResourceBSTR);
                OleAuto.INSTANCE.SysFreeString(strUserBSTR);
                OleAuto.INSTANCE.SysFreeString(strPasswordBSTR);
                OleAuto.INSTANCE.SysFreeString(strLocaleBSTR);
                OleAuto.INSTANCE.SysFreeString(strAuthorityBSTR);
            }
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:com/sun/jna/platform/win32/COM/Wbemcli$IWbemServices.class */
    public static class IWbemServices extends Unknown {
        public IWbemServices() {
        }

        public IWbemServices(Pointer pvInstance) {
            super(pvInstance);
        }

        public WinNT.HRESULT ExecQuery(WTypes.BSTR strQueryLanguage, WTypes.BSTR strQuery, int lFlags, IWbemContext pCtx, PointerByReference ppEnum) {
            return (WinNT.HRESULT) _invokeNativeObject(20, new Object[]{getPointer(), strQueryLanguage, strQuery, Integer.valueOf(lFlags), pCtx, ppEnum}, WinNT.HRESULT.class);
        }

        public IEnumWbemClassObject ExecQuery(String strQueryLanguage, String strQuery, int lFlags, IWbemContext pCtx) {
            WTypes.BSTR strQueryLanguageBSTR = OleAuto.INSTANCE.SysAllocString(strQueryLanguage);
            WTypes.BSTR strQueryBSTR = OleAuto.INSTANCE.SysAllocString(strQuery);
            try {
                PointerByReference pbr = new PointerByReference();
                WinNT.HRESULT res = ExecQuery(strQueryLanguageBSTR, strQueryBSTR, lFlags, pCtx, pbr);
                COMUtils.checkRC(res);
                IEnumWbemClassObject iEnumWbemClassObject = new IEnumWbemClassObject(pbr.getValue());
                return iEnumWbemClassObject;
            } finally {
                OleAuto.INSTANCE.SysFreeString(strQueryLanguageBSTR);
                OleAuto.INSTANCE.SysFreeString(strQueryBSTR);
            }
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:com/sun/jna/platform/win32/COM/Wbemcli$IWbemContext.class */
    public static class IWbemContext extends Unknown {
        public IWbemContext() {
        }

        public IWbemContext(Pointer pvInstance) {
            super(pvInstance);
        }
    }
}
