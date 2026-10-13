package com.sun.jna.platform.win32.COM.tlb.imp;

import com.sun.jna.platform.win32.COM.TypeInfoUtil;
import com.sun.jna.platform.win32.COM.TypeLibUtil;
import com.sun.jna.platform.win32.OaIdl;

/* JADX INFO: loaded from: autofirma.jar:com/sun/jna/platform/win32/COM/tlb/imp/TlbFunctionDispId.class */
public class TlbFunctionDispId extends TlbAbstractMethod {
    public TlbFunctionDispId(int count, int index, TypeLibUtil typeLibUtil, OaIdl.FUNCDESC funcDesc, TypeInfoUtil typeInfoUtil) {
        String returnValue;
        super(index, typeLibUtil, funcDesc, typeInfoUtil);
        String[] names = typeInfoUtil.getNames(funcDesc.memid, this.paramCount + 1);
        for (int i = 0; i < this.paramCount; i++) {
            OaIdl.ELEMDESC elemdesc = funcDesc.lprgelemdescParam.elemDescArg[i];
            String methodName = names[i + 1].toLowerCase();
            String type = getType(elemdesc.tdesc);
            String _methodName = replaceJavaKeyword(methodName);
            this.methodparams += type + " " + _methodName;
            if (type.equals("VARIANT")) {
                this.methodvariables += _methodName;
            } else {
                this.methodvariables += "new VARIANT(" + _methodName + ")";
            }
            if (i < this.paramCount - 1) {
                this.methodparams += ", ";
                this.methodvariables += ", ";
            }
        }
        if (this.returnType.equalsIgnoreCase("VARIANT")) {
            returnValue = "pResult";
        } else {
            returnValue = "((" + this.returnType + ") pResult.getValue())";
        }
        replaceVariable("helpstring", this.docStr);
        replaceVariable("returntype", this.returnType);
        replaceVariable("returnvalue", returnValue);
        replaceVariable("methodname", this.methodName);
        replaceVariable("methodparams", this.methodparams);
        replaceVariable("methodvariables", this.methodvariables);
        replaceVariable("vtableid", String.valueOf((int) this.vtableId));
        replaceVariable("memberid", String.valueOf(this.memberid));
        replaceVariable("functionCount", String.valueOf(count));
    }

    @Override // com.sun.jna.platform.win32.COM.tlb.imp.TlbBase
    protected String getClassTemplate() {
        return "com/sun/jna/platform/win32/COM/tlb/imp/TlbFunctionDispId.template";
    }
}
