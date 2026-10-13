package com.sun.jna.platform.win32.COM.tlb.imp;

import com.sun.jna.platform.win32.COM.TypeInfoUtil;
import com.sun.jna.platform.win32.COM.TypeLibUtil;
import com.sun.jna.platform.win32.OaIdl;

/* JADX INFO: loaded from: autofirma.jar:com/sun/jna/platform/win32/COM/tlb/imp/TlbDispInterface.class */
public class TlbDispInterface extends TlbBase {
    public TlbDispInterface(int index, String packagename, TypeLibUtil typeLibUtil) {
        super(index, typeLibUtil, null);
        TypeLibUtil.TypeLibDoc typeLibDoc = this.typeLibUtil.getDocumentation(index);
        String docString = typeLibDoc.getDocString();
        if (typeLibDoc.getName().length() > 0) {
            this.name = typeLibDoc.getName();
        }
        logInfo("Type of kind 'DispInterface' found: " + this.name);
        createPackageName(packagename);
        createClassName(this.name);
        setFilename(this.name);
        TypeInfoUtil typeInfoUtil = typeLibUtil.getTypeInfoUtil(index);
        OaIdl.TYPEATTR typeAttr = typeInfoUtil.getTypeAttr();
        createJavaDocHeader(typeAttr.guid.toGuidString(), docString);
        int cFuncs = typeAttr.cFuncs.intValue();
        for (int i = 0; i < cFuncs; i++) {
            OaIdl.FUNCDESC funcDesc = typeInfoUtil.getFuncDesc(i);
            OaIdl.MEMBERID memberID = funcDesc.memid;
            TypeInfoUtil.TypeInfoDoc typeInfoDoc2 = typeInfoUtil.getDocumentation(memberID);
            String methodName = typeInfoDoc2.getName();
            TlbAbstractMethod method = null;
            if (!isReservedMethod(methodName)) {
                if (funcDesc.invkind.value == OaIdl.INVOKEKIND.INVOKE_FUNC.value) {
                    method = new TlbFunctionStub(index, typeLibUtil, funcDesc, typeInfoUtil);
                } else if (funcDesc.invkind.value == OaIdl.INVOKEKIND.INVOKE_PROPERTYGET.value) {
                    method = new TlbPropertyGetStub(index, typeLibUtil, funcDesc, typeInfoUtil);
                } else if (funcDesc.invkind.value == OaIdl.INVOKEKIND.INVOKE_PROPERTYPUT.value || funcDesc.invkind.value == OaIdl.INVOKEKIND.INVOKE_PROPERTYPUTREF.value) {
                    method = new TlbPropertyPutStub(index, typeLibUtil, funcDesc, typeInfoUtil);
                }
                this.content += ((Object) method.getClassBuffer());
                if (i < cFuncs - 1) {
                    this.content += "\n";
                }
            }
            typeInfoUtil.ReleaseFuncDesc(funcDesc);
        }
        createContent(this.content);
    }

    protected void createJavaDocHeader(String guid, String helpstring) {
        replaceVariable("uuid", guid);
        replaceVariable("helpstring", helpstring);
    }

    @Override // com.sun.jna.platform.win32.COM.tlb.imp.TlbBase
    protected String getClassTemplate() {
        return "com/sun/jna/platform/win32/COM/tlb/imp/TlbDispInterface.template";
    }
}
