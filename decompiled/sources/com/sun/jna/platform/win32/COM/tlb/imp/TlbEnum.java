package com.sun.jna.platform.win32.COM.tlb.imp;

import com.sun.jna.platform.win32.COM.TypeInfoUtil;
import com.sun.jna.platform.win32.COM.TypeLibUtil;
import com.sun.jna.platform.win32.OaIdl;
import com.sun.jna.platform.win32.Variant;
import es.gob.afirma.keystores.filters.PolicyIdFilter;

/* JADX INFO: loaded from: autofirma.jar:com/sun/jna/platform/win32/COM/tlb/imp/TlbEnum.class */
public class TlbEnum extends TlbBase {
    public TlbEnum(int index, String packagename, TypeLibUtil typeLibUtil) {
        super(index, typeLibUtil, null);
        TypeLibUtil.TypeLibDoc typeLibDoc = this.typeLibUtil.getDocumentation(index);
        String docString = typeLibDoc.getDocString();
        if (typeLibDoc.getName().length() > 0) {
            this.name = typeLibDoc.getName();
        }
        logInfo("Type of kind 'Enum' found: " + this.name);
        createPackageName(packagename);
        createClassName(this.name);
        setFilename(this.name);
        TypeInfoUtil typeInfoUtil = typeLibUtil.getTypeInfoUtil(index);
        OaIdl.TYPEATTR typeAttr = typeInfoUtil.getTypeAttr();
        createJavaDocHeader(typeAttr.guid.toGuidString(), docString);
        int cVars = typeAttr.cVars.intValue();
        for (int i = 0; i < cVars; i++) {
            OaIdl.VARDESC varDesc = typeInfoUtil.getVarDesc(i);
            Variant.VARIANT constValue = varDesc._vardesc.lpvarValue;
            Object value = constValue.getValue();
            OaIdl.MEMBERID memberID = varDesc.memid;
            TypeInfoUtil.TypeInfoDoc typeInfoDoc2 = typeInfoUtil.getDocumentation(memberID);
            this.content += "\t\t//" + typeInfoDoc2.getName() + "\n";
            this.content += "\t\tpublic static final int " + typeInfoDoc2.getName() + " = " + value.toString() + PolicyIdFilter.OID_SEPARATOR;
            if (i < cVars - 1) {
                this.content += "\n";
            }
            typeInfoUtil.ReleaseVarDesc(varDesc);
        }
        createContent(this.content);
    }

    protected void createJavaDocHeader(String guid, String helpstring) {
        replaceVariable("uuid", guid);
        replaceVariable("helpstring", helpstring);
    }

    @Override // com.sun.jna.platform.win32.COM.tlb.imp.TlbBase
    protected String getClassTemplate() {
        return "com/sun/jna/platform/win32/COM/tlb/imp/TlbEnum.template";
    }
}
