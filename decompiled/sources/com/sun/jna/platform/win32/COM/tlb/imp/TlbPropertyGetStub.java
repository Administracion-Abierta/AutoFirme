package com.sun.jna.platform.win32.COM.tlb.imp;

import com.sun.jna.platform.win32.COM.TypeInfoUtil;
import com.sun.jna.platform.win32.COM.TypeLibUtil;
import com.sun.jna.platform.win32.OaIdl;

/* JADX INFO: loaded from: autofirma.jar:com/sun/jna/platform/win32/COM/tlb/imp/TlbPropertyGetStub.class */
public class TlbPropertyGetStub extends TlbAbstractMethod {
    public TlbPropertyGetStub(int index, TypeLibUtil typeLibUtil, OaIdl.FUNCDESC funcDesc, TypeInfoUtil typeInfoUtil) {
        super(index, typeLibUtil, funcDesc, typeInfoUtil);
        TypeInfoUtil.TypeInfoDoc typeInfoDoc = typeInfoUtil.getDocumentation(funcDesc.memid);
        String docStr = typeInfoDoc.getDocString();
        String methodname = "get" + typeInfoDoc.getName();
        replaceVariable("helpstring", docStr);
        replaceVariable("returntype", this.returnType);
        replaceVariable("methodname", methodname);
        replaceVariable("vtableid", String.valueOf((int) this.vtableId));
        replaceVariable("memberid", String.valueOf(this.memberid));
    }

    @Override // com.sun.jna.platform.win32.COM.tlb.imp.TlbBase
    protected String getClassTemplate() {
        return "com/sun/jna/platform/win32/COM/tlb/imp/TlbPropertyGetStub.template";
    }
}
