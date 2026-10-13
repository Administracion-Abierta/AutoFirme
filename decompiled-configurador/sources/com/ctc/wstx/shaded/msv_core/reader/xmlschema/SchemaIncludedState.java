package com.ctc.wstx.shaded.msv_core.reader.xmlschema;

import com.ctc.wstx.shaded.msv_core.reader.GrammarReader;
import com.ctc.wstx.shaded.msv_core.reader.IgnoreState;
import com.ctc.wstx.shaded.msv_core.reader.State;
import com.ctc.wstx.shaded.msv_core.util.StartTagInfo;
import java.util.HashSet;
import java.util.Set;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:com/ctc/wstx/shaded/msv_core/reader/xmlschema/SchemaIncludedState.class */
public class SchemaIncludedState extends GlobalDeclState {
    protected String expectedTargetNamespace;
    private String previousElementFormDefault;
    private String previousAttributeFormDefault;
    private String previousFinalDefault;
    private String previousBlockDefault;
    private String previousChameleonTargetNamespace;
    private boolean ignoreContents = false;

    protected SchemaIncludedState(String expectedTargetNamespace) {
        this.expectedTargetNamespace = expectedTargetNamespace;
    }

    @Override // com.ctc.wstx.shaded.msv_core.reader.xmlschema.GlobalDeclState, com.ctc.wstx.shaded.msv_core.reader.SimpleState
    protected State createChildState(StartTagInfo tag) {
        return this.ignoreContents ? new IgnoreState() : super.createChildState(tag);
    }

    @Override // com.ctc.wstx.shaded.msv_core.reader.State
    protected void startSelf() {
        XMLSchemaReader reader = (XMLSchemaReader) this.reader;
        super.startSelf();
        this.previousElementFormDefault = reader.elementFormDefault;
        this.previousAttributeFormDefault = reader.attributeFormDefault;
        this.previousFinalDefault = reader.finalDefault;
        this.previousBlockDefault = reader.blockDefault;
        this.previousChameleonTargetNamespace = reader.chameleonTargetNamespace;
        reader.chameleonTargetNamespace = null;
        String targetNs = this.startTag.getAttribute("targetNamespace");
        if (targetNs == null) {
            if (this.expectedTargetNamespace == null) {
                targetNs = "";
            } else {
                targetNs = this.expectedTargetNamespace;
                reader.chameleonTargetNamespace = this.expectedTargetNamespace;
            }
        } else if (this.expectedTargetNamespace != null && !this.expectedTargetNamespace.equals(targetNs)) {
            reader.reportError(XMLSchemaReader.ERR_INCONSISTENT_TARGETNAMESPACE, targetNs, this.expectedTargetNamespace);
        }
        Set<String> s = reader.parsedFiles.get(targetNs);
        if (s == null) {
            HashSet hashSet = new HashSet();
            s = hashSet;
            reader.parsedFiles.put(targetNs, hashSet);
        }
        if (s.contains(this.location.getSystemId())) {
            this.ignoreContents = true;
        } else {
            s.add(this.location.getSystemId());
        }
        onTargetNamespaceResolved(targetNs, this.ignoreContents);
        String form = this.startTag.getDefaultedAttribute("elementFormDefault", "unqualified");
        if (form.equals("qualified")) {
            reader.elementFormDefault = targetNs;
        } else {
            reader.elementFormDefault = "";
            if (!form.equals("unqualified")) {
                reader.reportError(GrammarReader.ERR_BAD_ATTRIBUTE_VALUE, "elementFormDefault", form);
            }
        }
        String form2 = this.startTag.getDefaultedAttribute("attributeFormDefault", "unqualified");
        if (form2.equals("qualified")) {
            reader.attributeFormDefault = targetNs;
        } else {
            reader.attributeFormDefault = "";
            if (!form2.equals("unqualified")) {
                reader.reportError(GrammarReader.ERR_BAD_ATTRIBUTE_VALUE, "attributeFormDefault", form2);
            }
        }
        reader.finalDefault = this.startTag.getAttribute("finalDefault");
        reader.blockDefault = this.startTag.getAttribute("blockDefault");
    }

    protected void onTargetNamespaceResolved(String targetNs, boolean ignoreContents) {
    }

    @Override // com.ctc.wstx.shaded.msv_core.reader.SimpleState
    protected void endSelf() {
        XMLSchemaReader reader = (XMLSchemaReader) this.reader;
        reader.elementFormDefault = this.previousElementFormDefault;
        reader.attributeFormDefault = this.previousAttributeFormDefault;
        reader.finalDefault = this.previousFinalDefault;
        reader.blockDefault = this.previousBlockDefault;
        reader.chameleonTargetNamespace = this.previousChameleonTargetNamespace;
        super.endSelf();
    }
}
