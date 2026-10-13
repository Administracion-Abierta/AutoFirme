package com.sun.jna.platform.win32.COM;

import com.sun.jna.platform.win32.Guid;
import com.sun.jna.platform.win32.OaIdl;
import com.sun.jna.platform.win32.OleAuto;
import com.sun.jna.platform.win32.Variant;
import com.sun.jna.platform.win32.WTypes;
import com.sun.jna.platform.win32.WinDef;
import com.sun.jna.platform.win32.WinNT;
import com.sun.jna.ptr.IntByReference;
import com.sun.jna.ptr.PointerByReference;

/* JADX INFO: loaded from: autofirma.jar:com/sun/jna/platform/win32/COM/TypeInfoUtil.class */
public class TypeInfoUtil {
    public static final OleAuto OLEAUTO = OleAuto.INSTANCE;
    private ITypeInfo typeInfo;

    public TypeInfoUtil(ITypeInfo typeInfo) {
        this.typeInfo = typeInfo;
    }

    public OaIdl.TYPEATTR getTypeAttr() {
        PointerByReference ppTypeAttr = new PointerByReference();
        WinNT.HRESULT hr = this.typeInfo.GetTypeAttr(ppTypeAttr);
        COMUtils.checkRC(hr);
        return new OaIdl.TYPEATTR(ppTypeAttr.getValue());
    }

    public TypeComp getTypeComp() {
        PointerByReference ppTypeAttr = new PointerByReference();
        WinNT.HRESULT hr = this.typeInfo.GetTypeComp(ppTypeAttr);
        COMUtils.checkRC(hr);
        return new TypeComp(ppTypeAttr.getValue());
    }

    public OaIdl.FUNCDESC getFuncDesc(int index) {
        PointerByReference ppFuncDesc = new PointerByReference();
        WinNT.HRESULT hr = this.typeInfo.GetFuncDesc(new WinDef.UINT(index), ppFuncDesc);
        COMUtils.checkRC(hr);
        return new OaIdl.FUNCDESC(ppFuncDesc.getValue());
    }

    public OaIdl.VARDESC getVarDesc(int index) {
        PointerByReference ppVarDesc = new PointerByReference();
        WinNT.HRESULT hr = this.typeInfo.GetVarDesc(new WinDef.UINT(index), ppVarDesc);
        COMUtils.checkRC(hr);
        return new OaIdl.VARDESC(ppVarDesc.getValue());
    }

    public String[] getNames(OaIdl.MEMBERID memid, int maxNames) {
        WTypes.BSTR[] rgBstrNames = new WTypes.BSTR[maxNames];
        WinDef.UINTByReference pcNames = new WinDef.UINTByReference();
        WinNT.HRESULT hr = this.typeInfo.GetNames(memid, rgBstrNames, new WinDef.UINT(maxNames), pcNames);
        COMUtils.checkRC(hr);
        int cNames = pcNames.getValue().intValue();
        String[] result = new String[cNames];
        for (int i = 0; i < result.length; i++) {
            result[i] = rgBstrNames[i].getValue();
            OLEAUTO.SysFreeString(rgBstrNames[i]);
        }
        return result;
    }

    public OaIdl.HREFTYPE getRefTypeOfImplType(int index) {
        OaIdl.HREFTYPEByReference ppTInfo = new OaIdl.HREFTYPEByReference();
        WinNT.HRESULT hr = this.typeInfo.GetRefTypeOfImplType(new WinDef.UINT(index), ppTInfo);
        COMUtils.checkRC(hr);
        return ppTInfo.getValue();
    }

    public int getImplTypeFlags(int index) {
        IntByReference pImplTypeFlags = new IntByReference();
        WinNT.HRESULT hr = this.typeInfo.GetImplTypeFlags(new WinDef.UINT(index), pImplTypeFlags);
        COMUtils.checkRC(hr);
        return pImplTypeFlags.getValue();
    }

    public OaIdl.MEMBERID[] getIDsOfNames(WTypes.LPOLESTR[] rgszNames, int cNames) {
        OaIdl.MEMBERID[] pMemId = new OaIdl.MEMBERID[cNames];
        WinNT.HRESULT hr = this.typeInfo.GetIDsOfNames(rgszNames, new WinDef.UINT(cNames), pMemId);
        COMUtils.checkRC(hr);
        return pMemId;
    }

    public Invoke Invoke(WinDef.PVOID pvInstance, OaIdl.MEMBERID memid, WinDef.WORD wFlags, OleAuto.DISPPARAMS.ByReference pDispParams) {
        Variant.VARIANT.ByReference pVarResult = new Variant.VARIANT.ByReference();
        OaIdl.EXCEPINFO.ByReference pExcepInfo = new OaIdl.EXCEPINFO.ByReference();
        WinDef.UINTByReference puArgErr = new WinDef.UINTByReference();
        WinNT.HRESULT hr = this.typeInfo.Invoke(pvInstance, memid, wFlags, pDispParams, pVarResult, pExcepInfo, puArgErr);
        COMUtils.checkRC(hr);
        return new Invoke(pVarResult, pExcepInfo, puArgErr.getValue().intValue());
    }

    /* JADX INFO: loaded from: autofirma.jar:com/sun/jna/platform/win32/COM/TypeInfoUtil$Invoke.class */
    public static class Invoke {
        private Variant.VARIANT.ByReference pVarResult;
        private OaIdl.EXCEPINFO.ByReference pExcepInfo;
        private int puArgErr;

        public Invoke(Variant.VARIANT.ByReference pVarResult, OaIdl.EXCEPINFO.ByReference pExcepInfo, int puArgErr) {
            this.pVarResult = pVarResult;
            this.pExcepInfo = pExcepInfo;
            this.puArgErr = puArgErr;
        }

        public Variant.VARIANT.ByReference getpVarResult() {
            return this.pVarResult;
        }

        public OaIdl.EXCEPINFO.ByReference getpExcepInfo() {
            return this.pExcepInfo;
        }

        public int getPuArgErr() {
            return this.puArgErr;
        }
    }

    public TypeInfoDoc getDocumentation(OaIdl.MEMBERID memid) {
        WTypes.BSTRByReference pBstrName = new WTypes.BSTRByReference();
        WTypes.BSTRByReference pBstrDocString = new WTypes.BSTRByReference();
        WinDef.DWORDByReference pdwHelpContext = new WinDef.DWORDByReference();
        WTypes.BSTRByReference pBstrHelpFile = new WTypes.BSTRByReference();
        WinNT.HRESULT hr = this.typeInfo.GetDocumentation(memid, pBstrName, pBstrDocString, pdwHelpContext, pBstrHelpFile);
        COMUtils.checkRC(hr);
        TypeInfoDoc TypeInfoDoc2 = new TypeInfoDoc(pBstrName.getString(), pBstrDocString.getString(), pdwHelpContext.getValue().intValue(), pBstrHelpFile.getString());
        OLEAUTO.SysFreeString(pBstrName.getValue());
        OLEAUTO.SysFreeString(pBstrDocString.getValue());
        OLEAUTO.SysFreeString(pBstrHelpFile.getValue());
        return TypeInfoDoc2;
    }

    /* JADX INFO: loaded from: autofirma.jar:com/sun/jna/platform/win32/COM/TypeInfoUtil$TypeInfoDoc.class */
    public static class TypeInfoDoc {
        private String name;
        private String docString;
        private int helpContext;
        private String helpFile;

        public TypeInfoDoc(String name, String docString, int helpContext, String helpFile) {
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

    public DllEntry GetDllEntry(OaIdl.MEMBERID memid, OaIdl.INVOKEKIND invKind) {
        WTypes.BSTRByReference pBstrDllName = new WTypes.BSTRByReference();
        WTypes.BSTRByReference pBstrName = new WTypes.BSTRByReference();
        WinDef.WORDByReference pwOrdinal = new WinDef.WORDByReference();
        WinNT.HRESULT hr = this.typeInfo.GetDllEntry(memid, invKind, pBstrDllName, pBstrName, pwOrdinal);
        COMUtils.checkRC(hr);
        OLEAUTO.SysFreeString(pBstrDllName.getValue());
        OLEAUTO.SysFreeString(pBstrName.getValue());
        return new DllEntry(pBstrDllName.getString(), pBstrName.getString(), pwOrdinal.getValue().intValue());
    }

    /* JADX INFO: loaded from: autofirma.jar:com/sun/jna/platform/win32/COM/TypeInfoUtil$DllEntry.class */
    public static class DllEntry {
        private String dllName;
        private String name;
        private int ordinal;

        public DllEntry(String dllName, String name, int ordinal) {
            this.dllName = dllName;
            this.name = name;
            this.ordinal = ordinal;
        }

        public String getDllName() {
            return this.dllName;
        }

        public void setDllName(String dllName) {
            this.dllName = dllName;
        }

        public String getName() {
            return this.name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public int getOrdinal() {
            return this.ordinal;
        }

        public void setOrdinal(int ordinal) {
            this.ordinal = ordinal;
        }
    }

    public ITypeInfo getRefTypeInfo(OaIdl.HREFTYPE hreftype) {
        PointerByReference ppTInfo = new PointerByReference();
        WinNT.HRESULT hr = this.typeInfo.GetRefTypeInfo(hreftype, ppTInfo);
        COMUtils.checkRC(hr);
        return new TypeInfo(ppTInfo.getValue());
    }

    public PointerByReference AddressOfMember(OaIdl.MEMBERID memid, OaIdl.INVOKEKIND invKind) {
        PointerByReference ppv = new PointerByReference();
        WinNT.HRESULT hr = this.typeInfo.AddressOfMember(memid, invKind, ppv);
        COMUtils.checkRC(hr);
        return ppv;
    }

    public PointerByReference CreateInstance(IUnknown pUnkOuter, Guid.REFIID riid) {
        PointerByReference ppvObj = new PointerByReference();
        WinNT.HRESULT hr = this.typeInfo.CreateInstance(pUnkOuter, riid, ppvObj);
        COMUtils.checkRC(hr);
        return ppvObj;
    }

    public String GetMops(OaIdl.MEMBERID memid) {
        WTypes.BSTRByReference pBstrMops = new WTypes.BSTRByReference();
        WinNT.HRESULT hr = this.typeInfo.GetMops(memid, pBstrMops);
        COMUtils.checkRC(hr);
        return pBstrMops.getString();
    }

    public ContainingTypeLib GetContainingTypeLib() {
        PointerByReference ppTLib = new PointerByReference();
        WinDef.UINTByReference pIndex = new WinDef.UINTByReference();
        WinNT.HRESULT hr = this.typeInfo.GetContainingTypeLib(ppTLib, pIndex);
        COMUtils.checkRC(hr);
        return new ContainingTypeLib(new TypeLib(ppTLib.getValue()), pIndex.getValue().intValue());
    }

    /* JADX INFO: loaded from: autofirma.jar:com/sun/jna/platform/win32/COM/TypeInfoUtil$ContainingTypeLib.class */
    public static class ContainingTypeLib {
        private ITypeLib typeLib;
        private int index;

        public ContainingTypeLib(ITypeLib typeLib, int index) {
            this.typeLib = typeLib;
            this.index = index;
        }

        public ITypeLib getTypeLib() {
            return this.typeLib;
        }

        public void setTypeLib(ITypeLib typeLib) {
            this.typeLib = typeLib;
        }

        public int getIndex() {
            return this.index;
        }

        public void setIndex(int index) {
            this.index = index;
        }
    }

    public void ReleaseTypeAttr(OaIdl.TYPEATTR pTypeAttr) {
        this.typeInfo.ReleaseTypeAttr(pTypeAttr);
    }

    public void ReleaseFuncDesc(OaIdl.FUNCDESC pFuncDesc) {
        this.typeInfo.ReleaseFuncDesc(pFuncDesc);
    }

    public void ReleaseVarDesc(OaIdl.VARDESC pVarDesc) {
        this.typeInfo.ReleaseVarDesc(pVarDesc);
    }
}
