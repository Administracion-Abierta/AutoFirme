package com.ctc.wstx.shaded.msv_core.reader.xmlschema;

import com.ctc.wstx.shaded.msv_core.grammar.NameClass;
import com.ctc.wstx.shaded.msv_core.grammar.NamespaceNameClass;
import com.ctc.wstx.shaded.msv_core.grammar.SimpleNameClass;
import com.ctc.wstx.shaded.msv_core.grammar.xmlschema.Field;
import com.ctc.wstx.shaded.msv_core.grammar.xmlschema.IdentityConstraint;
import com.ctc.wstx.shaded.msv_core.grammar.xmlschema.KeyConstraint;
import com.ctc.wstx.shaded.msv_core.grammar.xmlschema.KeyRefConstraint;
import com.ctc.wstx.shaded.msv_core.grammar.xmlschema.UniqueConstraint;
import com.ctc.wstx.shaded.msv_core.grammar.xmlschema.XMLSchemaSchema;
import com.ctc.wstx.shaded.msv_core.grammar.xmlschema.XPath;
import com.ctc.wstx.shaded.msv_core.reader.ChildlessState;
import com.ctc.wstx.shaded.msv_core.reader.GrammarReader;
import com.ctc.wstx.shaded.msv_core.reader.SimpleState;
import com.ctc.wstx.shaded.msv_core.reader.State;
import com.ctc.wstx.shaded.msv_core.util.StartTagInfo;
import java.util.StringTokenizer;
import java.util.Vector;
import org.xml.sax.Locator;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:com/ctc/wstx/shaded/msv_core/reader/xmlschema/IdentityConstraintState.class */
public class IdentityConstraintState extends SimpleState {
    protected XPath[] selector;
    protected final Vector fields = new Vector();

    @Override // com.ctc.wstx.shaded.msv_core.reader.SimpleState
    protected State createChildState(StartTagInfo tag) {
        if (tag.localName.equals("selector")) {
            String v = tag.getAttribute("xpath");
            if (v != null) {
                this.selector = parseSelector(v);
            } else {
                this.reader.reportError(GrammarReader.ERR_MISSING_ATTRIBUTE, "selector", "xpath");
                this.selector = new XPath[0];
            }
            return new ChildlessState();
        }
        if (tag.localName.equals("field")) {
            String v2 = tag.getAttribute("xpath");
            if (v2 != null) {
                this.fields.add(parseField(v2));
            } else {
                this.reader.reportError(GrammarReader.ERR_MISSING_ATTRIBUTE, "field", "xpath");
            }
            return new ChildlessState();
        }
        return null;
    }

    @Override // com.ctc.wstx.shaded.msv_core.reader.SimpleState
    protected void endSelf() {
        createIdentityConstraint();
        super.endSelf();
    }

    protected void createIdentityConstraint() {
        IdentityConstraint id;
        final XMLSchemaReader reader = (XMLSchemaReader) this.reader;
        String name = this.startTag.getAttribute("name");
        if (name == null) {
            reader.reportError(GrammarReader.ERR_MISSING_ATTRIBUTE, this.startTag.localName, "name");
            return;
        }
        Field[] fs = (Field[]) this.fields.toArray(new Field[this.fields.size()]);
        if (this.startTag.localName.equals("key")) {
            id = new KeyConstraint(reader.currentSchema.targetNamespace, name, this.selector, fs);
        } else if (this.startTag.localName.equals("unique")) {
            id = new UniqueConstraint(reader.currentSchema.targetNamespace, name, this.selector, fs);
        } else if (this.startTag.localName.equals("keyref")) {
            final String refer = this.startTag.getAttribute("refer");
            if (refer == null) {
                reader.reportError(GrammarReader.ERR_MISSING_ATTRIBUTE, this.startTag.localName, "refer");
                return;
            }
            final String[] qn = reader.splitQName(refer);
            if (qn == null) {
                reader.reportError(XMLSchemaReader.ERR_UNDECLARED_PREFIX, qn);
                return;
            } else {
                final KeyRefConstraint keyRef = new KeyRefConstraint(reader.currentSchema.targetNamespace, name, this.selector, fs);
                id = keyRef;
                reader.addBackPatchJob(new GrammarReader.BackPatch() { // from class: com.ctc.wstx.shaded.msv_core.reader.xmlschema.IdentityConstraintState.1
                    @Override // com.ctc.wstx.shaded.msv_core.reader.GrammarReader.BackPatch
                    public State getOwnerState() {
                        return IdentityConstraintState.this;
                    }

                    @Override // com.ctc.wstx.shaded.msv_core.reader.GrammarReader.BackPatch
                    public void patch() {
                        XMLSchemaSchema s = reader.grammar.getByNamespace(qn[0]);
                        if (s == null) {
                            reader.reportError(XMLSchemaReader.ERR_UNDEFINED_SCHEMA, qn[0]);
                            return;
                        }
                        IdentityConstraint idc = s.identityConstraints.get(qn[1]);
                        if (idc == null) {
                            reader.reportError(XMLSchemaReader.ERR_UNDEFINED_KEY, refer);
                            return;
                        }
                        if (!(idc instanceof KeyConstraint)) {
                            reader.reportError(XMLSchemaReader.ERR_KEYREF_REFERRING_NON_KEY, refer);
                        } else if (idc.fields.length != keyRef.fields.length) {
                            reader.reportError(new Locator[]{IdentityConstraintState.this.getLocation(), reader.getDeclaredLocationOf(idc)}, XMLSchemaReader.ERR_KEY_FIELD_NUMBER_MISMATCH, new Object[]{idc.localName, new Integer(idc.fields.length), keyRef.localName, new Integer(keyRef.fields.length)});
                        } else {
                            keyRef.key = (KeyConstraint) idc;
                        }
                    }
                });
            }
        } else {
            throw new Error();
        }
        if (reader.currentSchema.identityConstraints.get(name) != null) {
            reader.reportError(new Locator[]{this.location, reader.getDeclaredLocationOf(id)}, XMLSchemaReader.ERR_DUPLICATE_IDENTITY_CONSTRAINT_DEFINITION, new Object[]{name});
        } else {
            reader.currentSchema.identityConstraints.add(name, id);
        }
        reader.setDeclaredLocationOf(id);
        ((ElementDeclState) this.parentState).onIdentityConstraint(id);
    }

    protected XPath[] parseSelector(String xpath) {
        Vector pathObjs = new Vector();
        StringTokenizer paths = new StringTokenizer(xpath, "|");
        while (paths.hasMoreTokens()) {
            XPath pathObj = new XPath();
            pathObjs.add(pathObj);
            if (!parsePath(pathObj, paths.nextToken(), false)) {
                return new XPath[0];
            }
        }
        return (XPath[]) pathObjs.toArray(new XPath[pathObjs.size()]);
    }

    protected Field parseField(String xpath) {
        Vector pathObjs = new Vector();
        Field field = new Field();
        StringTokenizer paths = new StringTokenizer(xpath, "|");
        while (paths.hasMoreTokens()) {
            XPath pathObj = new XPath();
            pathObjs.add(pathObj);
            if (!parsePath(pathObj, paths.nextToken(), true)) {
                return new Field();
            }
        }
        field.paths = (XPath[]) pathObjs.toArray(new XPath[pathObjs.size()]);
        return field;
    }

    protected boolean parsePath(XPath pathObj, String xpath, boolean parseField) {
        Vector stepObjs = new Vector();
        if (xpath.startsWith(".//")) {
            pathObj.isAnyDescendant = true;
            xpath = xpath.substring(3);
        }
        StringTokenizer steps = new StringTokenizer(xpath, "/");
        stepObjs.clear();
        while (steps.hasMoreTokens()) {
            String step = steps.nextToken();
            if (!step.equals(".")) {
                if (step.equals("*")) {
                    stepObjs.add(NameClass.ALL);
                } else {
                    boolean attribute = false;
                    if (step.charAt(0) == '@' && parseField && !steps.hasMoreTokens()) {
                        attribute = true;
                        step = step.substring(1);
                    }
                    String[] qn = this.reader.splitQName(step);
                    if (qn == null) {
                        this.reader.reportError(XMLSchemaReader.ERR_BAD_XPATH, step);
                        return false;
                    }
                    if (attribute && step.indexOf(58) < 0) {
                        qn[0] = "";
                    }
                    NameClass nc = qn[1].equals("*") ? new NamespaceNameClass(qn[0]) : new SimpleNameClass(qn[0], qn[1]);
                    if (attribute) {
                        pathObj.attributeStep = nc;
                    } else {
                        stepObjs.add(nc);
                    }
                }
            }
        }
        pathObj.steps = (NameClass[]) stepObjs.toArray(new NameClass[stepObjs.size()]);
        return true;
    }
}
