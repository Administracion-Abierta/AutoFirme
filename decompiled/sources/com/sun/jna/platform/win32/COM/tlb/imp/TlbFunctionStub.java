package com.sun.jna.platform.win32.COM.tlb.imp;

import com.sun.jna.platform.win32.COM.TypeInfoUtil;
import com.sun.jna.platform.win32.COM.TypeLibUtil;
import com.sun.jna.platform.win32.OaIdl;

/* JADX INFO: loaded from: autofirma.jar:com/sun/jna/platform/win32/COM/tlb/imp/TlbFunctionStub.class */
public class TlbFunctionStub extends TlbAbstractMethod {
    public TlbFunctionStub(int index, TypeLibUtil typeLibUtil, OaIdl.FUNCDESC funcDesc, TypeInfoUtil typeInfoUtil) {
        super(index, typeLibUtil, funcDesc, typeInfoUtil);
        TypeInfoUtil.TypeInfoDoc typeInfoDoc = typeInfoUtil.getDocumentation(funcDesc.memid);
        String methodname = typeInfoDoc.getName();
        String docStr = typeInfoDoc.getDocString();
        String[] names = typeInfoUtil.getNames(funcDesc.memid, this.paramCount + 1);
        if (this.paramCount > 0) {
            this.methodvariables = ", ";
        }
        for (int i = 0; i < this.paramCount; i++) {
            OaIdl.ELEMDESC elemdesc = funcDesc.lprgelemdescParam.elemDescArg[i];
            String methodName = names[i + 1].toLowerCase();
            this.methodparams += getType(elemdesc.tdesc) + " " + replaceJavaKeyword(methodName);
            this.methodvariables += methodName;
            if (i < this.paramCount - 1) {
                this.methodparams += ", ";
                this.methodvariables += ", ";
            }
        }
        replaceVariable("helpstring", docStr);
        replaceVariable("returntype", this.returnType);
        replaceVariable("methodname", methodname);
        replaceVariable("methodparams", this.methodparams);
        replaceVariable("vtableid", String.valueOf((int) this.vtableId));
        replaceVariable("memberid", String.valueOf(this.memberid));
    }

    @Override // com.sun.jna.platform.win32.COM.tlb.imp.TlbBase
    protected String getClassTemplate() {
        return "com/sun/jna/platform/win32/COM/tlb/imp/TlbFunctionStub.template";
    }
}
