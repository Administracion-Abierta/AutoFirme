package com.ctc.wstx.shaded.msv_core.datatype.xsd;

import com.ctc.wstx.shaded.msv.relaxng_datatype.DatatypeException;
import com.ctc.wstx.shaded.msv.relaxng_datatype.ValidationContext;
import com.sun.jna.platform.win32.WinNT;
import java.io.PrintStream;
import java.math.BigInteger;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Vector;

/* JADX INFO: loaded from: autofirma.jar:com/ctc/wstx/shaded/msv_core/datatype/xsd/TypeIncubator.class */
public class TypeIncubator {
    private final Map impl = new HashMap();
    private final XSDatatypeImpl baseType;
    private static final String[][] exclusiveFacetPairs = {new String[]{XSDatatype.FACET_LENGTH, XSDatatype.FACET_MINLENGTH}, new String[]{XSDatatype.FACET_LENGTH, XSDatatype.FACET_MAXLENGTH}, new String[]{XSDatatype.FACET_MAXINCLUSIVE, XSDatatype.FACET_MAXEXCLUSIVE}, new String[]{XSDatatype.FACET_MININCLUSIVE, XSDatatype.FACET_MINEXCLUSIVE}};

    public TypeIncubator(XSDatatype baseType) {
        this.baseType = (XSDatatypeImpl) baseType;
        if (baseType == null) {
            throw new IllegalArgumentException();
        }
    }

    public void add(String name, String strValue, boolean fixed, ValidationContext context) throws DatatypeException {
        addFacet(name, strValue, fixed, context);
    }

    public void addFacet(String name, String strValue, boolean fixed, ValidationContext context) throws DatatypeException {
        Object value;
        FacetInfo fi;
        if (this.baseType instanceof ErrorType) {
            return;
        }
        switch (this.baseType.isFacetApplicable(name)) {
            case -2:
                throw new DatatypeException(XSDatatypeImpl.localize(XSDatatypeImpl.ERR_NOT_APPLICABLE_FACET, name));
            case -1:
                return;
            case 0:
                if (isValueFacet(name)) {
                    value = this.baseType.createValue(strValue, context);
                    if (value == null) {
                        throw new DatatypeException(XSDatatypeImpl.localize(XSDatatypeImpl.ERR_INVALID_VALUE_FOR_THIS_TYPE, strValue, this.baseType.displayName()));
                    }
                } else {
                    value = strValue;
                }
                if (isRepeatable(name)) {
                    if (this.impl.containsKey(name)) {
                        fi = (FacetInfo) this.impl.get(name);
                    } else {
                        Map map = this.impl;
                        FacetInfo facetInfo = new FacetInfo(new Vector(), fixed);
                        fi = facetInfo;
                        map.put(name, facetInfo);
                    }
                    ((Vector) fi.value).add(value);
                    fi.fixed |= fixed;
                    return;
                }
                if (this.impl.containsKey(name)) {
                    throw new DatatypeException(XSDatatypeImpl.localize(XSDatatypeImpl.ERR_DUPLICATE_FACET, name));
                }
                this.impl.put(name, new FacetInfo(value, fixed));
                return;
            default:
                throw new Error();
        }
    }

    public XSDatatypeImpl derive(String newName) throws DatatypeException {
        return derive("", newName);
    }

    public XSDatatypeImpl derive(String newNameUri, String newLocalName) throws DatatypeException {
        if (this.baseType instanceof ErrorType) {
            return this.baseType;
        }
        if (this.baseType.isFinal(1)) {
            throw new DatatypeException(XSDatatypeImpl.localize(XSDatatypeImpl.ERR_INVALID_BASE_TYPE, this.baseType.displayName()));
        }
        if (isEmpty()) {
            if (newNameUri == null && newLocalName == null) {
                return this.baseType;
            }
            return new FinalComponent(newNameUri, newLocalName, this.baseType, 0);
        }
        XSDatatypeImpl r = this.baseType;
        for (int i = 0; i < exclusiveFacetPairs.length; i++) {
            if (contains(exclusiveFacetPairs[i][0]) && contains(exclusiveFacetPairs[i][1])) {
                throw new DatatypeException(XSDatatypeImpl.localize(XSDatatypeImpl.ERR_X_AND_Y_ARE_EXCLUSIVE, exclusiveFacetPairs[i][0], exclusiveFacetPairs[i][1]));
            }
        }
        if (contains(XSDatatype.FACET_TOTALDIGITS)) {
            r = new TotalDigitsFacet(newNameUri, newLocalName, r, getPositiveInteger(XSDatatype.FACET_TOTALDIGITS), isFixed(XSDatatype.FACET_TOTALDIGITS));
        }
        if (contains(XSDatatype.FACET_FRACTIONDIGITS)) {
            r = new FractionDigitsFacet(newNameUri, newLocalName, r, getNonNegativeInteger(XSDatatype.FACET_FRACTIONDIGITS), isFixed(XSDatatype.FACET_FRACTIONDIGITS));
        }
        if (contains(XSDatatype.FACET_MININCLUSIVE)) {
            r = new MinInclusiveFacet(newNameUri, newLocalName, r, getFacet(XSDatatype.FACET_MININCLUSIVE), isFixed(XSDatatype.FACET_MININCLUSIVE));
        }
        if (contains(XSDatatype.FACET_MAXINCLUSIVE)) {
            r = new MaxInclusiveFacet(newNameUri, newLocalName, r, getFacet(XSDatatype.FACET_MAXINCLUSIVE), isFixed(XSDatatype.FACET_MAXINCLUSIVE));
        }
        if (contains(XSDatatype.FACET_MINEXCLUSIVE)) {
            r = new MinExclusiveFacet(newNameUri, newLocalName, r, getFacet(XSDatatype.FACET_MINEXCLUSIVE), isFixed(XSDatatype.FACET_MINEXCLUSIVE));
        }
        if (contains(XSDatatype.FACET_MAXEXCLUSIVE)) {
            r = new MaxExclusiveFacet(newNameUri, newLocalName, r, getFacet(XSDatatype.FACET_MAXEXCLUSIVE), isFixed(XSDatatype.FACET_MAXEXCLUSIVE));
        }
        if (contains(XSDatatype.FACET_LENGTH)) {
            r = new LengthFacet(newNameUri, newLocalName, r, this);
        }
        if (contains(XSDatatype.FACET_MINLENGTH)) {
            r = new MinLengthFacet(newNameUri, newLocalName, r, this);
        }
        if (contains(XSDatatype.FACET_MAXLENGTH)) {
            r = new MaxLengthFacet(newNameUri, newLocalName, r, this);
        }
        if (contains(XSDatatype.FACET_WHITESPACE)) {
            r = new WhiteSpaceFacet(newNameUri, newLocalName, r, this);
        }
        if (contains(XSDatatype.FACET_PATTERN)) {
            r = new PatternFacet(newNameUri, newLocalName, r, this);
        }
        if (contains(XSDatatype.FACET_ENUMERATION)) {
            r = new EnumerationFacet(newNameUri, newLocalName, r, getVector(XSDatatype.FACET_ENUMERATION), isFixed(XSDatatype.FACET_ENUMERATION));
        }
        DataTypeWithFacet o1 = r.getFacetObject(XSDatatype.FACET_MAXLENGTH);
        DataTypeWithFacet o2 = r.getFacetObject(XSDatatype.FACET_MINLENGTH);
        if (o1 != null && o2 != null && ((MaxLengthFacet) o1).maxLength < ((MinLengthFacet) o2).minLength) {
            throw reportFacetInconsistency(newLocalName, o1, XSDatatype.FACET_MAXLENGTH, o2, XSDatatype.FACET_MINLENGTH);
        }
        DataTypeWithFacet o3 = r.getFacetObject(XSDatatype.FACET_FRACTIONDIGITS);
        DataTypeWithFacet o4 = r.getFacetObject(XSDatatype.FACET_TOTALDIGITS);
        if (o3 != null && o4 != null && ((FractionDigitsFacet) o3).scale > ((TotalDigitsFacet) o4).precision) {
            throw reportFacetInconsistency(newLocalName, o3, XSDatatype.FACET_FRACTIONDIGITS, o4, XSDatatype.FACET_TOTALDIGITS);
        }
        checkRangeConsistency(r, XSDatatype.FACET_MININCLUSIVE, XSDatatype.FACET_MAXINCLUSIVE);
        checkRangeConsistency(r, XSDatatype.FACET_MINEXCLUSIVE, XSDatatype.FACET_MAXEXCLUSIVE);
        checkRangeConsistency(r, XSDatatype.FACET_MININCLUSIVE, XSDatatype.FACET_MAXEXCLUSIVE);
        checkRangeConsistency(r, XSDatatype.FACET_MINEXCLUSIVE, XSDatatype.FACET_MAXINCLUSIVE);
        return r;
    }

    private static void checkRangeConsistency(XSDatatypeImpl newType, String facetName1, String facetName2) throws DatatypeException {
        DataTypeWithFacet o1 = newType.getFacetObject(facetName1);
        DataTypeWithFacet o2 = newType.getFacetObject(facetName2);
        if (o1 != null && o2 != null) {
            int c = ((Comparator) o1.getConcreteType()).compare(((RangeFacet) o1).limitValue, ((RangeFacet) o2).limitValue);
            if (c == 1) {
                throw reportFacetInconsistency(newType.displayName(), o1, facetName1, o2, facetName2);
            }
        }
    }

    private static DatatypeException reportFacetInconsistency(String newName, DataTypeWithFacet o1, String facetName1, DataTypeWithFacet o2, String facetName2) {
        String o1typeName = o1.getName();
        String o2typeName = o2.getName();
        if (o1typeName.equals(o2typeName)) {
            return new DatatypeException(XSDatatypeImpl.localize(XSDatatypeImpl.ERR_INCONSISTENT_FACETS_1, facetName1, facetName2));
        }
        if (o1typeName.equals(newName)) {
            return new DatatypeException(XSDatatypeImpl.localize(XSDatatypeImpl.ERR_INCONSISTENT_FACETS_2, facetName1, o2.displayName(), facetName2));
        }
        if (o2typeName.equals(newName)) {
            return new DatatypeException(XSDatatypeImpl.localize(XSDatatypeImpl.ERR_INCONSISTENT_FACETS_2, facetName2, o1.displayName(), facetName1));
        }
        throw new IllegalStateException();
    }

    private static boolean isValueFacet(String facetName) {
        return facetName.equals(XSDatatype.FACET_ENUMERATION) || facetName.equals(XSDatatype.FACET_MAXEXCLUSIVE) || facetName.equals(XSDatatype.FACET_MINEXCLUSIVE) || facetName.equals(XSDatatype.FACET_MAXINCLUSIVE) || facetName.equals(XSDatatype.FACET_MININCLUSIVE);
    }

    private static boolean isRepeatable(String facetName) {
        return facetName.equals(XSDatatype.FACET_ENUMERATION) || facetName.equals(XSDatatype.FACET_PATTERN);
    }

    public boolean isFixed(String facetName) {
        return ((FacetInfo) this.impl.get(facetName)).fixed;
    }

    public Object getFacet(String facetName) {
        return ((FacetInfo) this.impl.get(facetName)).value;
    }

    public Vector getVector(String facetName) {
        return (Vector) ((FacetInfo) this.impl.get(facetName)).value;
    }

    public int getPositiveInteger(String facetName) throws DatatypeException {
        try {
            int value = Integer.parseInt((String) getFacet(facetName));
            if (value > 0) {
                return value;
            }
        } catch (NumberFormatException e) {
            try {
                if (new BigInteger((String) getFacet(facetName)).signum() > 0) {
                    return WinNT.MAXLONG;
                }
            } catch (NumberFormatException e2) {
            }
        }
        throw new DatatypeException(XSDatatypeImpl.localize(XSDatatypeImpl.ERR_FACET_MUST_BE_POSITIVE_INTEGER, facetName));
    }

    public int getNonNegativeInteger(String facetName) throws DatatypeException {
        try {
            int value = Integer.parseInt((String) getFacet(facetName));
            if (value >= 0) {
                return value;
            }
        } catch (NumberFormatException e) {
        }
        throw new DatatypeException(XSDatatypeImpl.localize(XSDatatypeImpl.ERR_FACET_MUST_BE_NON_NEGATIVE_INTEGER, facetName));
    }

    private boolean contains(String facetName) {
        return this.impl.containsKey(facetName);
    }

    public boolean isEmpty() {
        return this.impl.isEmpty();
    }

    /* JADX INFO: loaded from: autofirma.jar:com/ctc/wstx/shaded/msv_core/datatype/xsd/TypeIncubator$FacetInfo.class */
    private static class FacetInfo {
        public Object value;
        public boolean fixed;

        public FacetInfo(Object value, boolean fixed) {
            this.value = value;
            this.fixed = fixed;
        }
    }

    public void dump(PrintStream out) {
        for (String facetName : this.impl.keySet()) {
            FacetInfo fi = (FacetInfo) this.impl.get(facetName);
            if (fi.value instanceof Vector) {
                out.println(facetName + " :");
                Vector v = (Vector) fi.value;
                for (int i = 0; i < v.size(); i++) {
                    out.println("  " + v.elementAt(i));
                }
            } else {
                out.println(facetName + " : " + fi.value);
            }
        }
    }

    public String getFacetNames() {
        String r = "";
        Iterator itr = this.impl.keySet().iterator();
        while (itr.hasNext()) {
            if (r.length() != 0) {
                r = r + ", ";
            }
            r = r + ((String) itr.next());
        }
        return r;
    }
}
