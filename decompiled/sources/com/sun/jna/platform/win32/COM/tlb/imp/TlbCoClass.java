package com.sun.jna.platform.win32.COM.tlb.imp;

import com.ctc.wstx.cfg.XmlConsts;
import com.sun.jna.platform.win32.COM.ITypeInfo;
import com.sun.jna.platform.win32.COM.TypeInfoUtil;
import com.sun.jna.platform.win32.COM.TypeLibUtil;
import com.sun.jna.platform.win32.OaIdl;

/* JADX INFO: loaded from: autofirma.jar:com/sun/jna/platform/win32/COM/tlb/imp/TlbCoClass.class */
public class TlbCoClass extends TlbBase {
    public TlbCoClass(int index, String packagename, TypeLibUtil typeLibUtil, String bindingMode) {
        super(index, typeLibUtil, null);
        TypeInfoUtil typeInfoUtil = typeLibUtil.getTypeInfoUtil(index);
        TypeLibUtil.TypeLibDoc typeLibDoc = this.typeLibUtil.getDocumentation(index);
        String docString = typeLibDoc.getDocString();
        if (typeLibDoc.getName().length() > 0) {
            this.name = typeLibDoc.getName();
        }
        logInfo("Type of kind 'CoClass' found: " + this.name);
        createPackageName(packagename);
        createClassName(this.name);
        setFilename(this.name);
        String guidStr = this.typeLibUtil.getLibAttr().guid.toGuidString();
        int majorVerNum = this.typeLibUtil.getLibAttr().wMajorVerNum.intValue();
        int minorVerNum = this.typeLibUtil.getLibAttr().wMinorVerNum.intValue();
        String version = majorVerNum + "." + minorVerNum;
        String clsid = typeInfoUtil.getTypeAttr().guid.toGuidString();
        createJavaDocHeader(guidStr, version, docString);
        createCLSID(clsid);
        createCLSIDName(this.name);
        OaIdl.TYPEATTR typeAttr = typeInfoUtil.getTypeAttr();
        int cImplTypes = typeAttr.cImplTypes.intValue();
        String interfaces = "";
        for (int i = 0; i < cImplTypes; i++) {
            OaIdl.HREFTYPE refTypeOfImplType = typeInfoUtil.getRefTypeOfImplType(i);
            ITypeInfo refTypeInfo = typeInfoUtil.getRefTypeInfo(refTypeOfImplType);
            TypeInfoUtil refTypeInfoUtil = new TypeInfoUtil(refTypeInfo);
            createFunctions(refTypeInfoUtil, bindingMode);
            TypeInfoUtil.TypeInfoDoc documentation = refTypeInfoUtil.getDocumentation(new OaIdl.MEMBERID(-1));
            interfaces = interfaces + documentation.getName();
            if (i < cImplTypes - 1) {
                interfaces = interfaces + ", ";
            }
        }
        createInterfaces(interfaces);
        createContent(this.content);
    }

    protected void createFunctions(TypeInfoUtil typeInfoUtil, String bindingMode) {
        OaIdl.TYPEATTR typeAttr = typeInfoUtil.getTypeAttr();
        int cFuncs = typeAttr.cFuncs.intValue();
        int i = 0;
        while (i < cFuncs) {
            OaIdl.FUNCDESC funcDesc = typeInfoUtil.getFuncDesc(i);
            TlbAbstractMethod method = null;
            if (funcDesc.invkind.value == OaIdl.INVOKEKIND.INVOKE_FUNC.value) {
                if (isVTableMode()) {
                    method = new TlbFunctionVTable(i, this.index, this.typeLibUtil, funcDesc, typeInfoUtil);
                } else {
                    method = new TlbFunctionDispId(i, this.index, this.typeLibUtil, funcDesc, typeInfoUtil);
                }
            } else if (funcDesc.invkind.value == OaIdl.INVOKEKIND.INVOKE_PROPERTYGET.value) {
                method = new TlbPropertyGet(i, this.index, this.typeLibUtil, funcDesc, typeInfoUtil);
            } else if (funcDesc.invkind.value == OaIdl.INVOKEKIND.INVOKE_PROPERTYPUT.value || funcDesc.invkind.value == OaIdl.INVOKEKIND.INVOKE_PROPERTYPUTREF.value) {
                method = new TlbPropertyPut(i, this.index, this.typeLibUtil, funcDesc, typeInfoUtil);
            }
            if (!isReservedMethod(method.getMethodName())) {
                this.content += ((Object) method.getClassBuffer());
                if (i < cFuncs - 1) {
                    this.content += "\n";
                }
            }
            typeInfoUtil.ReleaseFuncDesc(funcDesc);
            i++;
        }
    }

    protected void createJavaDocHeader(String guid, String version, String helpstring) {
        replaceVariable("uuid", guid);
        replaceVariable(XmlConsts.XML_DECL_KW_VERSION, version);
        replaceVariable("helpstring", helpstring);
    }

    protected void createCLSIDName(String clsidName) {
        replaceVariable("clsidname", clsidName.toUpperCase());
    }

    protected void createCLSID(String clsid) {
        replaceVariable("clsid", clsid);
    }

    protected void createInterfaces(String interfaces) {
        replaceVariable("interfaces", interfaces);
    }

    @Override // com.sun.jna.platform.win32.COM.tlb.imp.TlbBase
    protected String getClassTemplate() {
        return "com/sun/jna/platform/win32/COM/tlb/imp/TlbCoClass.template";
    }
}
