package com.ctc.wstx.shaded.msv_core.grammar.xmlschema;

import com.ctc.wstx.shaded.msv_core.grammar.Expression;
import com.ctc.wstx.shaded.msv_core.grammar.ExpressionPool;
import com.ctc.wstx.shaded.msv_core.grammar.ReferenceContainer;
import com.ctc.wstx.shaded.msv_core.grammar.ReferenceExp;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:com/ctc/wstx/shaded/msv_core/grammar/xmlschema/XMLSchemaSchema.class */
public class XMLSchemaSchema implements Serializable {
    public static final String XMLSchemaInstanceNamespace = "http://www.w3.org/2001/XMLSchema-instance";
    public final String targetNamespace;
    public final ExpressionPool pool;
    public Expression topLevel;
    public final SimpleTypeContainer simpleTypes = new SimpleTypeContainer();
    public final ComplexTypeContainer complexTypes = new ComplexTypeContainer();
    public final AttributeGroupContainer attributeGroups = new AttributeGroupContainer();
    public final AttributeDeclContainer attributeDecls = new AttributeDeclContainer();
    public final ElementDeclContainer elementDecls = new ElementDeclContainer();
    public final GroupDeclContainer groupDecls = new GroupDeclContainer();
    public final IdentityConstraintContainer identityConstraints = new IdentityConstraintContainer();
    private static final long serialVersionUID = 1;

    public XMLSchemaSchema(String targetNamespace, XMLSchemaGrammar parent) {
        this.pool = parent.pool;
        this.targetNamespace = targetNamespace;
        parent.schemata.put(targetNamespace, this);
    }

    /* JADX INFO: loaded from: autofirmaConfigurador.jar:com/ctc/wstx/shaded/msv_core/grammar/xmlschema/XMLSchemaSchema$SimpleTypeContainer.class */
    public final class SimpleTypeContainer extends ReferenceContainer {
        public SimpleTypeContainer() {
        }

        public SimpleTypeExp getOrCreate(String name) {
            return (SimpleTypeExp) super._getOrCreate(name);
        }

        public SimpleTypeExp get(String name) {
            return (SimpleTypeExp) super._get(name);
        }

        @Override // com.ctc.wstx.shaded.msv_core.grammar.ReferenceContainer
        protected ReferenceExp createReference(String name) {
            return new SimpleTypeExp(name);
        }
    }

    /* JADX INFO: loaded from: autofirmaConfigurador.jar:com/ctc/wstx/shaded/msv_core/grammar/xmlschema/XMLSchemaSchema$ComplexTypeContainer.class */
    public final class ComplexTypeContainer extends ReferenceContainer {
        public ComplexTypeContainer() {
        }

        public ComplexTypeExp getOrCreate(String name) {
            return (ComplexTypeExp) super._getOrCreate(name);
        }

        public ComplexTypeExp get(String name) {
            return (ComplexTypeExp) super._get(name);
        }

        @Override // com.ctc.wstx.shaded.msv_core.grammar.ReferenceContainer
        protected ReferenceExp createReference(String name) {
            return new ComplexTypeExp(XMLSchemaSchema.this, name);
        }
    }

    /* JADX INFO: loaded from: autofirmaConfigurador.jar:com/ctc/wstx/shaded/msv_core/grammar/xmlschema/XMLSchemaSchema$AttributeGroupContainer.class */
    public final class AttributeGroupContainer extends ReferenceContainer {
        public AttributeGroupContainer() {
        }

        public AttributeGroupExp getOrCreate(String name) {
            return (AttributeGroupExp) super._getOrCreate(name);
        }

        public AttributeGroupExp get(String name) {
            return (AttributeGroupExp) super._get(name);
        }

        @Override // com.ctc.wstx.shaded.msv_core.grammar.ReferenceContainer
        protected ReferenceExp createReference(String name) {
            return new AttributeGroupExp(name);
        }
    }

    /* JADX INFO: loaded from: autofirmaConfigurador.jar:com/ctc/wstx/shaded/msv_core/grammar/xmlschema/XMLSchemaSchema$AttributeDeclContainer.class */
    public final class AttributeDeclContainer extends ReferenceContainer {
        public AttributeDeclContainer() {
        }

        public AttributeDeclExp getOrCreate(String name) {
            return (AttributeDeclExp) super._getOrCreate(name);
        }

        public AttributeDeclExp get(String name) {
            return (AttributeDeclExp) super._get(name);
        }

        @Override // com.ctc.wstx.shaded.msv_core.grammar.ReferenceContainer
        protected ReferenceExp createReference(String name) {
            return new AttributeDeclExp(name);
        }
    }

    /* JADX INFO: loaded from: autofirmaConfigurador.jar:com/ctc/wstx/shaded/msv_core/grammar/xmlschema/XMLSchemaSchema$ElementDeclContainer.class */
    public final class ElementDeclContainer extends ReferenceContainer {
        public ElementDeclContainer() {
        }

        public ElementDeclExp getOrCreate(String name) {
            return (ElementDeclExp) super._getOrCreate(name);
        }

        public ElementDeclExp get(String name) {
            return (ElementDeclExp) super._get(name);
        }

        @Override // com.ctc.wstx.shaded.msv_core.grammar.ReferenceContainer
        protected ReferenceExp createReference(String name) {
            return new ElementDeclExp(XMLSchemaSchema.this, name);
        }
    }

    /* JADX INFO: loaded from: autofirmaConfigurador.jar:com/ctc/wstx/shaded/msv_core/grammar/xmlschema/XMLSchemaSchema$GroupDeclContainer.class */
    public final class GroupDeclContainer extends ReferenceContainer {
        public GroupDeclContainer() {
        }

        public GroupDeclExp getOrCreate(String name) {
            return (GroupDeclExp) super._getOrCreate(name);
        }

        public GroupDeclExp get(String name) {
            return (GroupDeclExp) super._get(name);
        }

        @Override // com.ctc.wstx.shaded.msv_core.grammar.ReferenceContainer
        protected ReferenceExp createReference(String name) {
            return new GroupDeclExp(name);
        }
    }

    /* JADX INFO: loaded from: autofirmaConfigurador.jar:com/ctc/wstx/shaded/msv_core/grammar/xmlschema/XMLSchemaSchema$IdentityConstraintContainer.class */
    public final class IdentityConstraintContainer implements Serializable {
        private final Map storage = new HashMap();

        public IdentityConstraintContainer() {
        }

        public IdentityConstraint get(String name) {
            return (IdentityConstraint) this.storage.get(name);
        }

        public void add(String name, IdentityConstraint idc) {
            this.storage.put(name, idc);
        }
    }
}
