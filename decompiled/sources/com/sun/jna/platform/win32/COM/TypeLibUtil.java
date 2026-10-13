package com.sun.jna.platform.win32.COM;

import com.sun.jna.Native;
import com.sun.jna.Pointer;
import com.sun.jna.platform.win32.Guid;
import com.sun.jna.platform.win32.Kernel32;
import com.sun.jna.platform.win32.OaIdl;
import com.sun.jna.platform.win32.Ole32;
import com.sun.jna.platform.win32.OleAuto;
import com.sun.jna.platform.win32.WTypes;
import com.sun.jna.platform.win32.WinDef;
import com.sun.jna.platform.win32.WinNT;
import com.sun.jna.ptr.PointerByReference;

/* JADX INFO: loaded from: autofirma.jar:com/sun/jna/platform/win32/COM/TypeLibUtil.class */
public class TypeLibUtil {
    public static final OleAuto OLEAUTO = OleAuto.INSTANCE;
    private ITypeLib typelib;
    private WinDef.LCID lcid = Kernel32.INSTANCE.GetUserDefaultLCID();
    private String name;
    private String docString;
    private int helpContext;
    private String helpFile;

    public TypeLibUtil(String clsidStr, int wVerMajor, int wVerMinor) {
        Guid.CLSID.ByReference clsid = new Guid.CLSID.ByReference();
        WinNT.HRESULT hr = Ole32.INSTANCE.CLSIDFromString(clsidStr, clsid);
        COMUtils.checkRC(hr);
        PointerByReference pTypeLib = new PointerByReference();
        WinNT.HRESULT hr2 = OleAuto.INSTANCE.LoadRegTypeLib(clsid, wVerMajor, wVerMinor, this.lcid, pTypeLib);
        COMUtils.checkRC(hr2);
        this.typelib = new TypeLib(pTypeLib.getValue());
        initTypeLibInfo();
    }

    public TypeLibUtil(String file) {
        PointerByReference pTypeLib = new PointerByReference();
        WinNT.HRESULT hr = OleAuto.INSTANCE.LoadTypeLib(file, pTypeLib);
        COMUtils.checkRC(hr);
        this.typelib = new TypeLib(pTypeLib.getValue());
        initTypeLibInfo();
    }

    private void initTypeLibInfo() {
        TypeLibDoc documentation = getDocumentation(-1);
        this.name = documentation.getName();
        this.docString = documentation.getDocString();
        this.helpContext = documentation.getHelpContext();
        this.helpFile = documentation.getHelpFile();
    }

    public int getTypeInfoCount() {
        return this.typelib.GetTypeInfoCount().intValue();
    }

    public OaIdl.TYPEKIND getTypeInfoType(int index) {
        OaIdl.TYPEKIND.ByReference typekind = new OaIdl.TYPEKIND.ByReference();
        WinNT.HRESULT hr = this.typelib.GetTypeInfoType(new WinDef.UINT(index), typekind);
        COMUtils.checkRC(hr);
        return typekind;
    }

    public ITypeInfo getTypeInfo(int index) {
        PointerByReference ppTInfo = new PointerByReference();
        WinNT.HRESULT hr = this.typelib.GetTypeInfo(new WinDef.UINT(index), ppTInfo);
        COMUtils.checkRC(hr);
        return new TypeInfo(ppTInfo.getValue());
    }

    public TypeInfoUtil getTypeInfoUtil(int index) {
        return new TypeInfoUtil(getTypeInfo(index));
    }

    public OaIdl.TLIBATTR getLibAttr() {
        PointerByReference ppTLibAttr = new PointerByReference();
        WinNT.HRESULT hr = this.typelib.GetLibAttr(ppTLibAttr);
        COMUtils.checkRC(hr);
        return new OaIdl.TLIBATTR(ppTLibAttr.getValue());
    }

    public TypeComp GetTypeComp() {
        PointerByReference ppTComp = new PointerByReference();
        WinNT.HRESULT hr = this.typelib.GetTypeComp(ppTComp);
        COMUtils.checkRC(hr);
        return new TypeComp(ppTComp.getValue());
    }

    public TypeLibDoc getDocumentation(int index) {
        WTypes.BSTRByReference pBstrName = new WTypes.BSTRByReference();
        WTypes.BSTRByReference pBstrDocString = new WTypes.BSTRByReference();
        WinDef.DWORDByReference pdwHelpContext = new WinDef.DWORDByReference();
        WTypes.BSTRByReference pBstrHelpFile = new WTypes.BSTRByReference();
        WinNT.HRESULT hr = this.typelib.GetDocumentation(index, pBstrName, pBstrDocString, pdwHelpContext, pBstrHelpFile);
        COMUtils.checkRC(hr);
        TypeLibDoc typeLibDoc = new TypeLibDoc(pBstrName.getString(), pBstrDocString.getString(), pdwHelpContext.getValue().intValue(), pBstrHelpFile.getString());
        OLEAUTO.SysFreeString(pBstrName.getValue());
        OLEAUTO.SysFreeString(pBstrDocString.getValue());
        OLEAUTO.SysFreeString(pBstrHelpFile.getValue());
        return typeLibDoc;
    }

    /* JADX INFO: loaded from: autofirma.jar:com/sun/jna/platform/win32/COM/TypeLibUtil$TypeLibDoc.class */
    public static class TypeLibDoc {
        private String name;
        private String docString;
        private int helpContext;
        private String helpFile;

        public TypeLibDoc(String name, String docString, int helpContext, String helpFile) {
            this.name = name;
            this.docString = docString;
            this.helpContext = helpContext;
            this.helpFile = helpFile;
        }

        public String getName() {
            return this.name;
        }

        public String getDocString() {
            return this.docString;
        }

        public int getHelpContext() {
            return this.helpContext;
        }

        public String getHelpFile() {
            return this.helpFile;
        }
    }

    public IsName IsName(String nameBuf, int hashVal) {
        WTypes.LPOLESTR szNameBuf = new WTypes.LPOLESTR(nameBuf);
        WinDef.ULONG lHashVal = new WinDef.ULONG(hashVal);
        WinDef.BOOLByReference pfName = new WinDef.BOOLByReference();
        WinNT.HRESULT hr = this.typelib.IsName(szNameBuf, lHashVal, pfName);
        COMUtils.checkRC(hr);
        return new IsName(szNameBuf.getValue(), pfName.getValue().booleanValue());
    }

    /* JADX INFO: loaded from: autofirma.jar:com/sun/jna/platform/win32/COM/TypeLibUtil$IsName.class */
    public static class IsName {
        private String nameBuf;
        private boolean name;

        public IsName(String nameBuf, boolean name) {
            this.nameBuf = nameBuf;
            this.name = name;
        }

        public String getNameBuf() {
            return this.nameBuf;
        }

        public boolean isName() {
            return this.name;
        }
    }

    public FindName FindName(String name, int hashVal, short maxResult) {
        Pointer p = Ole32.INSTANCE.CoTaskMemAlloc((((long) name.length()) + 1) * ((long) Native.WCHAR_SIZE));
        WTypes.LPOLESTR olestr = new WTypes.LPOLESTR(p);
        olestr.setValue(name);
        WinDef.ULONG lHashVal = new WinDef.ULONG(hashVal);
        WinDef.USHORTByReference pcFound = new WinDef.USHORTByReference(maxResult);
        Pointer[] ppTInfo = new Pointer[maxResult];
        OaIdl.MEMBERID[] rgMemId = new OaIdl.MEMBERID[maxResult];
        WinNT.HRESULT hr = this.typelib.FindName(olestr, lHashVal, ppTInfo, rgMemId, pcFound);
        COMUtils.checkRC(hr);
        FindName findName = new FindName(olestr.getValue(), ppTInfo, rgMemId, pcFound.getValue().shortValue());
        Ole32.INSTANCE.CoTaskMemFree(p);
        return findName;
    }

    /* JADX INFO: loaded from: autofirma.jar:com/sun/jna/platform/win32/COM/TypeLibUtil$FindName.class */
    public static class FindName {
        private String nameBuf;
        private Pointer[] pTInfo;
        private OaIdl.MEMBERID[] rgMemId;
        private short pcFound;

        FindName(String nameBuf, Pointer[] pTInfo, OaIdl.MEMBERID[] rgMemId, short pcFound) {
            this.nameBuf = nameBuf;
            this.pTInfo = new Pointer[pcFound];
            this.rgMemId = new OaIdl.MEMBERID[pcFound];
            this.pcFound = pcFound;
            System.arraycopy(pTInfo, 0, this.pTInfo, 0, pcFound);
            System.arraycopy(rgMemId, 0, this.rgMemId, 0, pcFound);
        }

        public String getNameBuf() {
            return this.nameBuf;
        }

        public ITypeInfo[] getTInfo() {
            ITypeInfo[] values = new ITypeInfo[this.pcFound];
            for (int i = 0; i < this.pcFound; i++) {
                values[i] = new TypeInfo(this.pTInfo[i]);
            }
            return values;
        }

        public OaIdl.MEMBERID[] getMemId() {
            return this.rgMemId;
        }

        public short getFound() {
            return this.pcFound;
        }
    }

    public void ReleaseTLibAttr(OaIdl.TLIBATTR pTLibAttr) {
        this.typelib.ReleaseTLibAttr(pTLibAttr);
    }

    public WinDef.LCID getLcid() {
        return this.lcid;
    }

    public ITypeLib getTypelib() {
        return this.typelib;
    }

    public String getName() {
        return this.name;
    }

    public String getDocString() {
        return this.docString;
    }

    public int getHelpContext() {
        return this.helpContext;
    }

    public String getHelpFile() {
        return this.helpFile;
    }
}
