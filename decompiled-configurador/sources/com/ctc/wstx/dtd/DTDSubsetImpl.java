package com.ctc.wstx.dtd;

import com.ctc.wstx.cfg.ErrorConsts;
import com.ctc.wstx.ent.EntityDecl;
import com.ctc.wstx.exc.WstxParsingException;
import com.ctc.wstx.sr.InputProblemReporter;
import com.ctc.wstx.util.DataUtil;
import com.ctc.wstx.util.PrefixedName;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.xml.stream.Location;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.NotationDeclaration;
import org.codehaus.stax2.validation.ValidationContext;
import org.codehaus.stax2.validation.XMLValidator;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:com/ctc/wstx/dtd/DTDSubsetImpl.class */
public final class DTDSubsetImpl extends DTDSubset {
    final boolean mIsCachable;
    final boolean mFullyValidating;
    final boolean mHasNsDefaults;
    final HashMap<String, EntityDecl> mGeneralEntities;
    final Set<String> mRefdGEs;
    final HashMap<String, EntityDecl> mDefinedPEs;
    final Set<String> mRefdPEs;
    final HashMap<String, NotationDeclaration> mNotations;
    final HashMap<PrefixedName, DTDElement> mElements;
    volatile transient List<EntityDecl> mGeneralEntityList = null;
    transient List<NotationDeclaration> mNotationList = null;

    private DTDSubsetImpl(boolean cachable, HashMap<String, EntityDecl> genEnt, Set<String> refdGEs, HashMap<String, EntityDecl> paramEnt, Set<String> peRefs, HashMap<String, NotationDeclaration> notations, HashMap<PrefixedName, DTDElement> elements, boolean fullyValidating) {
        this.mIsCachable = cachable;
        this.mGeneralEntities = genEnt;
        this.mRefdGEs = refdGEs;
        this.mDefinedPEs = paramEnt;
        this.mRefdPEs = peRefs;
        this.mNotations = notations;
        this.mElements = elements;
        this.mFullyValidating = fullyValidating;
        boolean anyNsDefs = false;
        if (elements != null) {
            for (DTDElement elem : elements.values()) {
                if (elem.hasNsDefaults()) {
                    anyNsDefs = true;
                    break;
                }
            }
        }
        this.mHasNsDefaults = anyNsDefs;
    }

    public static DTDSubsetImpl constructInstance(boolean cachable, HashMap<String, EntityDecl> genEnt, Set<String> refdGEs, HashMap<String, EntityDecl> paramEnt, Set<String> refdPEs, HashMap<String, NotationDeclaration> notations, HashMap<PrefixedName, DTDElement> elements, boolean fullyValidating) {
        return new DTDSubsetImpl(cachable, genEnt, refdGEs, paramEnt, refdPEs, notations, elements, fullyValidating);
    }

    /* JADX INFO: Thrown type has an unknown type hierarchy: javax.xml.stream.XMLStreamException */
    @Override // com.ctc.wstx.dtd.DTDSubset
    public DTDSubset combineWithExternalSubset(InputProblemReporter rep, DTDSubset extSubset) throws XMLStreamException {
        HashMap<String, EntityDecl> ge1 = getGeneralEntityMap();
        HashMap<String, EntityDecl> ge2 = extSubset.getGeneralEntityMap();
        if (ge1 == null || ge1.isEmpty()) {
            ge1 = ge2;
        } else if (ge2 != null && !ge2.isEmpty()) {
            combineMaps(ge1, ge2);
        }
        HashMap<String, NotationDeclaration> n1 = getNotationMap();
        HashMap<String, NotationDeclaration> n2 = extSubset.getNotationMap();
        if (n1 == null || n1.isEmpty()) {
            n1 = n2;
        } else if (n2 != null && !n2.isEmpty()) {
            checkNotations(n1, n2);
            combineMaps(n1, n2);
        }
        HashMap<PrefixedName, DTDElement> e1 = getElementMap();
        HashMap<PrefixedName, DTDElement> e2 = extSubset.getElementMap();
        if (e1 == null || e1.isEmpty()) {
            e1 = e2;
        } else if (e2 != null && !e2.isEmpty()) {
            combineElements(rep, e1, e2);
        }
        return constructInstance(false, ge1, null, null, null, n1, e1, this.mFullyValidating);
    }

    @Override // com.ctc.wstx.dtd.DTDSubset, org.codehaus.stax2.validation.XMLValidationSchema
    public XMLValidator createValidator(ValidationContext ctxt) throws XMLStreamException {
        if (this.mFullyValidating) {
            return new DTDValidator(this, ctxt, this.mHasNsDefaults, getElementMap(), getGeneralEntityMap());
        }
        return new DTDTypingNonValidator(this, ctxt, this.mHasNsDefaults, getElementMap(), getGeneralEntityMap());
    }

    @Override // com.ctc.wstx.dtd.DTDSubset, org.codehaus.stax2.validation.DTDValidationSchema
    public int getEntityCount() {
        if (this.mGeneralEntities == null) {
            return 0;
        }
        return this.mGeneralEntities.size();
    }

    @Override // com.ctc.wstx.dtd.DTDSubset, org.codehaus.stax2.validation.DTDValidationSchema
    public int getNotationCount() {
        if (this.mNotations == null) {
            return 0;
        }
        return this.mNotations.size();
    }

    @Override // com.ctc.wstx.dtd.DTDSubset
    public boolean isCachable() {
        return this.mIsCachable;
    }

    @Override // com.ctc.wstx.dtd.DTDSubset
    public HashMap<String, EntityDecl> getGeneralEntityMap() {
        return this.mGeneralEntities;
    }

    @Override // com.ctc.wstx.dtd.DTDSubset
    public List<EntityDecl> getGeneralEntityList() {
        List<EntityDecl> l = this.mGeneralEntityList;
        if (l == null) {
            if (this.mGeneralEntities == null || this.mGeneralEntities.size() == 0) {
                l = Collections.emptyList();
            } else {
                l = Collections.unmodifiableList(new ArrayList(this.mGeneralEntities.values()));
            }
            this.mGeneralEntityList = l;
        }
        return l;
    }

    @Override // com.ctc.wstx.dtd.DTDSubset
    public HashMap<String, EntityDecl> getParameterEntityMap() {
        return this.mDefinedPEs;
    }

    @Override // com.ctc.wstx.dtd.DTDSubset
    public HashMap<String, NotationDeclaration> getNotationMap() {
        return this.mNotations;
    }

    @Override // com.ctc.wstx.dtd.DTDSubset
    public synchronized List<NotationDeclaration> getNotationList() {
        List<NotationDeclaration> l = this.mNotationList;
        if (l == null) {
            if (this.mNotations == null || this.mNotations.size() == 0) {
                l = Collections.emptyList();
            } else {
                l = Collections.unmodifiableList(new ArrayList(this.mNotations.values()));
            }
            this.mNotationList = l;
        }
        return l;
    }

    @Override // com.ctc.wstx.dtd.DTDSubset
    public HashMap<PrefixedName, DTDElement> getElementMap() {
        return this.mElements;
    }

    @Override // com.ctc.wstx.dtd.DTDSubset
    public boolean isReusableWith(DTDSubset intSubset) {
        HashMap<String, EntityDecl> intGEs;
        HashMap<String, EntityDecl> intPEs;
        Set<String> refdPEs = this.mRefdPEs;
        if (refdPEs != null && refdPEs.size() > 0 && (intPEs = intSubset.getParameterEntityMap()) != null && intPEs.size() > 0 && DataUtil.anyValuesInCommon(refdPEs, intPEs.keySet())) {
            return false;
        }
        Set<String> refdGEs = this.mRefdGEs;
        if (refdGEs != null && refdGEs.size() > 0 && (intGEs = intSubset.getGeneralEntityMap()) != null && intGEs.size() > 0 && DataUtil.anyValuesInCommon(refdGEs, intGEs.keySet())) {
            return false;
        }
        return true;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("[DTDSubset: ");
        int count = getEntityCount();
        sb.append(count);
        sb.append(" general entities");
        sb.append(']');
        return sb.toString();
    }

    /* JADX INFO: Thrown type has an unknown type hierarchy: javax.xml.stream.XMLStreamException */
    public static void throwNotationException(NotationDeclaration oldDecl, NotationDeclaration newDecl) throws XMLStreamException {
        throw new WstxParsingException(MessageFormat.format(ErrorConsts.ERR_DTD_NOTATION_REDEFD, newDecl.getName(), oldDecl.getLocation().toString()), newDecl.getLocation());
    }

    /* JADX INFO: Thrown type has an unknown type hierarchy: javax.xml.stream.XMLStreamException */
    public static void throwElementException(DTDElement oldElem, Location loc) throws XMLStreamException {
        throw new WstxParsingException(MessageFormat.format(ErrorConsts.ERR_DTD_ELEM_REDEFD, oldElem.getDisplayName(), oldElem.getLocation().toString()), loc);
    }

    private static <K, V> void combineMaps(Map<K, V> m1, Map<K, V> m2) {
        for (Map.Entry<K, V> me : m2.entrySet()) {
            K key = me.getKey();
            V old = m1.put(key, me.getValue());
            if (old != null) {
                m1.put(key, old);
            }
        }
    }

    /* JADX INFO: Thrown type has an unknown type hierarchy: javax.xml.stream.XMLStreamException */
    private void combineElements(InputProblemReporter rep, HashMap<PrefixedName, DTDElement> intElems, HashMap<PrefixedName, DTDElement> extElems) throws XMLStreamException {
        for (Map.Entry<PrefixedName, DTDElement> me : extElems.entrySet()) {
            PrefixedName key = me.getKey();
            DTDElement extElem = me.getValue();
            DTDElement intElem = intElems.get(key);
            if (intElem == null) {
                intElems.put(key, extElem);
            } else if (extElem.isDefined()) {
                if (intElem.isDefined()) {
                    throwElementException(intElem, extElem.getLocation());
                } else {
                    intElem.defineFrom(rep, extElem, this.mFullyValidating);
                }
            } else if (!intElem.isDefined()) {
                rep.reportProblem(intElem.getLocation(), ErrorConsts.WT_ENT_DECL, ErrorConsts.W_UNDEFINED_ELEM, extElem.getDisplayName(), null);
            } else {
                intElem.mergeMissingAttributesFrom(rep, extElem, this.mFullyValidating);
            }
        }
    }

    /* JADX INFO: Thrown type has an unknown type hierarchy: javax.xml.stream.XMLStreamException */
    private static void checkNotations(HashMap<String, NotationDeclaration> fromInt, HashMap<String, NotationDeclaration> fromExt) throws XMLStreamException {
        for (Map.Entry<String, NotationDeclaration> en : fromExt.entrySet()) {
            if (fromInt.containsKey(en.getKey())) {
                throwNotationException(fromInt.get(en.getKey()), en.getValue());
            }
        }
    }
}
