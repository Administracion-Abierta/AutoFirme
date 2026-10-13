package com.sun.jna.platform.win32.COM.tlb.imp;

import com.sun.jna.platform.win32.COM.TypeInfoUtil;
import com.sun.jna.platform.win32.COM.TypeLibUtil;
import com.sun.jna.platform.win32.OaIdl;

/* JADX INFO: loaded from: autofirma.jar:com/sun/jna/platform/win32/COM/tlb/imp/TlbPropertyPutStub.class */
public class TlbPropertyPutStub extends TlbAbstractMethod {
    public TlbPropertyPutStub(int index, TypeLibUtil typeLibUtil, OaIdl.FUNCDESC funcDesc, TypeInfoUtil typeInfoUtil) {
        super(index, typeLibUtil, funcDesc, typeInfoUtil);
        TypeInfoUtil.TypeInfoDoc typeInfoDoc = typeInfoUtil.getDocumentation(funcDesc.memid);
        String docStr = typeInfoDoc.getDocString();
        String methodname = "set" + typeInfoDoc.getName();
        String[] names = typeInfoUtil.getNames(funcDesc.memid, this.paramCount + 1);
        for (int i = 0; i < this.paramCount; i++) {
            OaIdl.ELEMDESC elemdesc = funcDesc.lprgelemdescParam.elemDescArg[i];
            String varType = getType(elemdesc);
            this.methodparams += varType + " " + replaceJavaKeyword(names[i].toLowerCase());
            if (i < this.paramCount - 1) {
                this.methodparams += ", ";
            }
        }
        replaceVariable("helpstring", docStr);
        replaceVariable("methodname", methodname);
        replaceVariable("methodparams", this.methodparams);
        replaceVariable("vtableid", String.valueOf((int) this.vtableId));
        replaceVariable("memberid", String.valueOf(this.memberid));
    }

    @Override // com.sun.jna.platform.win32.COM.tlb.imp.TlbBase
    protected String getClassTemplate() {
        return "com/sun/jna/platform/win32/COM/tlb/imp/TlbPropertyPutStub.template";
    }
}
