package org.bouncycastle.oer;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import org.bouncycastle.asn1.ASN1Encodable;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/oer/Element.class */
public class Element {
    private final OERDefinition.BaseType baseType;
    private final List<Element> children;
    private final boolean explicit;
    private final String label;
    private final BigInteger lowerBound;
    private final BigInteger upperBound;
    private final boolean extensionsInDefinition;
    private final BigInteger enumValue;
    private final ASN1Encodable defaultValue;
    private final Switch aSwitch;
    private final boolean defaultValuesInChildren;
    private List<Element> optionalChildrenInOrder;
    private List<ASN1Encodable> validSwitchValues;
    private final ElementSupplier elementSupplier;
    private final boolean mayRecurse;
    private final String typeName;
    private final Map<String, ElementSupplier> supplierMap;
    private Element parent;
    private final int optionals;
    private final int block;

    public Element(OERDefinition.BaseType baseType, List<Element> list, boolean z, String str, BigInteger bigInteger, BigInteger bigInteger2, boolean z2, BigInteger bigInteger3, ASN1Encodable aSN1Encodable, Switch r13, List<ASN1Encodable> list2, ElementSupplier elementSupplier, boolean z3, String str2, Map<String, ElementSupplier> map, int i, int i2, boolean z4) {
        this.baseType = baseType;
        this.children = list;
        this.explicit = z;
        this.label = str;
        this.lowerBound = bigInteger;
        this.upperBound = bigInteger2;
        this.extensionsInDefinition = z2;
        this.enumValue = bigInteger3;
        this.defaultValue = aSN1Encodable;
        this.aSwitch = r13;
        this.validSwitchValues = list2 != null ? Collections.unmodifiableList(list2) : null;
        this.elementSupplier = elementSupplier;
        this.mayRecurse = z3;
        this.typeName = str2;
        this.block = i;
        this.optionals = i2;
        this.defaultValuesInChildren = z4;
        if (map == null) {
            this.supplierMap = Collections.emptyMap();
        } else {
            this.supplierMap = map;
        }
        Iterator<Element> it = list.iterator();
        while (it.hasNext()) {
            it.next().parent = this;
        }
    }

    public Element(Element element, Element element2) {
        this.baseType = element.baseType;
        this.children = new ArrayList(element.children);
        this.explicit = element.explicit;
        this.label = element.label;
        this.lowerBound = element.lowerBound;
        this.upperBound = element.upperBound;
        this.extensionsInDefinition = element.extensionsInDefinition;
        this.enumValue = element.enumValue;
        this.defaultValue = element.defaultValue;
        this.aSwitch = element.aSwitch;
        this.validSwitchValues = element.validSwitchValues;
        this.elementSupplier = element.elementSupplier;
        this.mayRecurse = element.mayRecurse;
        this.typeName = element.typeName;
        this.supplierMap = element.supplierMap;
        this.parent = element2;
        this.block = element.block;
        this.optionals = element.optionals;
        this.defaultValuesInChildren = element.defaultValuesInChildren;
        Iterator<Element> it = this.children.iterator();
        while (it.hasNext()) {
            it.next().parent = this;
        }
    }

    public static Element expandDeferredDefinition(Element element, Element element2) {
        if (element.elementSupplier != null) {
            element = element.elementSupplier.build();
            if (element.getParent() != element2) {
                element = new Element(element, element2);
            }
        }
        return element;
    }

    public String rangeExpression() {
        return "(" + (getLowerBound() != null ? getLowerBound().toString() : "MIN") + " ... " + (getUpperBound() != null ? getUpperBound().toString() : "MAX") + ")";
    }

    public String appendLabel(String str) {
        return "[" + (getLabel() == null ? "" : getLabel()) + (isExplicit() ? " (E)" : "") + "] " + str;
    }

    public List<Element> optionalOrDefaultChildrenInOrder() {
        List<Element> optionalChildrenInOrder;
        synchronized (this) {
            if (getOptionalChildrenInOrder() == null) {
                ArrayList arrayList = new ArrayList();
                for (Element element : getChildren()) {
                    if (!element.isExplicit() || element.getDefaultValue() != null) {
                        arrayList.add(element);
                    }
                }
                this.optionalChildrenInOrder = Collections.unmodifiableList(arrayList);
            }
            optionalChildrenInOrder = getOptionalChildrenInOrder();
        }
        return optionalChildrenInOrder;
    }

    public boolean isUnbounded() {
        return getUpperBound() == null && getLowerBound() == null;
    }

    public boolean isLowerRangeZero() {
        return BigInteger.ZERO.equals(getLowerBound());
    }

    public boolean isUnsignedWithRange() {
        return isLowerRangeZero() && getUpperBound() != null && BigInteger.ZERO.compareTo(getUpperBound()) < 0;
    }

    public boolean canBeNegative() {
        return getLowerBound() != null && BigInteger.ZERO.compareTo(getLowerBound()) > 0;
    }

    public int intBytesForRange() {
        if (getLowerBound() == null || getUpperBound() == null) {
            return 0;
        }
        if (BigInteger.ZERO.equals(getLowerBound())) {
            int i = 0;
            int i2 = 1;
            while (true) {
                int i3 = i2;
                if (i >= OERDefinition.uIntMax.length) {
                    return 0;
                }
                if (getUpperBound().compareTo(OERDefinition.uIntMax[i]) < 0) {
                    return i3;
                }
                i++;
                i2 = i3 * 2;
            }
        } else {
            int i4 = 0;
            int i5 = 1;
            while (true) {
                int i6 = i5;
                if (i4 >= OERDefinition.sIntRange.length) {
                    return 0;
                }
                if (getLowerBound().compareTo(OERDefinition.sIntRange[i4][0]) >= 0 && getUpperBound().compareTo(OERDefinition.sIntRange[i4][1]) < 0) {
                    return -i6;
                }
                i4++;
                i5 = i6 * 2;
            }
        }
    }

    public boolean hasPopulatedExtension() {
        return this.extensionsInDefinition;
    }

    public boolean hasDefaultChildren() {
        return this.defaultValuesInChildren;
    }

    public ASN1Encodable getDefaultValue() {
        return this.defaultValue;
    }

    public Element getFirstChid() {
        return getChildren().get(0);
    }

    public boolean isFixedLength() {
        return getLowerBound() != null && getLowerBound().equals(getUpperBound());
    }

    public String toString() {
        return "[" + this.typeName + " " + this.baseType.name() + " '" + getLabel() + "']";
    }

    public OERDefinition.BaseType getBaseType() {
        return this.baseType;
    }

    public List<Element> getChildren() {
        return this.children;
    }

    public boolean isExplicit() {
        return this.explicit;
    }

    public String getLabel() {
        return this.label;
    }

    public BigInteger getLowerBound() {
        return this.lowerBound;
    }

    public BigInteger getUpperBound() {
        return this.upperBound;
    }

    public boolean isExtensionsInDefinition() {
        return this.extensionsInDefinition;
    }

    public BigInteger getEnumValue() {
        return this.enumValue;
    }

    public Switch getaSwitch() {
        return this.aSwitch;
    }

    public List<Element> getOptionalChildrenInOrder() {
        return this.optionalChildrenInOrder;
    }

    public List<ASN1Encodable> getValidSwitchValues() {
        return this.validSwitchValues;
    }

    public ElementSupplier getElementSupplier() {
        return this.elementSupplier;
    }

    public boolean isMayRecurse() {
        return this.mayRecurse;
    }

    public String getTypeName() {
        return this.typeName;
    }

    public int getOptionals() {
        return this.optionals;
    }

    public int getBlock() {
        return this.block;
    }

    public String getDerivedTypeName() {
        return this.typeName != null ? this.typeName : this.baseType.name();
    }

    public ElementSupplier resolveSupplier() {
        if (this.supplierMap.containsKey(this.label)) {
            return this.supplierMap.get(this.label);
        }
        if (this.parent != null) {
            return this.parent.resolveSupplier(this.label);
        }
        throw new IllegalStateException("unable to resolve: " + this.label);
    }

    protected ElementSupplier resolveSupplier(String str) {
        String str2 = this.label + "." + str;
        if (this.supplierMap.containsKey(str2)) {
            return this.supplierMap.get(str2);
        }
        if (this.parent != null) {
            return this.parent.resolveSupplier(str2);
        }
        throw new IllegalStateException("unable to resolve: " + str2);
    }

    public Element getParent() {
        return this.parent;
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        Element element = (Element) obj;
        if (this.explicit != element.explicit || this.extensionsInDefinition != element.extensionsInDefinition || this.defaultValuesInChildren != element.defaultValuesInChildren || this.mayRecurse != element.mayRecurse || this.optionals != element.optionals || this.block != element.block || this.baseType != element.baseType) {
            return false;
        }
        if (this.children != null) {
            if (!this.children.equals(element.children)) {
                return false;
            }
        } else if (element.children != null) {
            return false;
        }
        if (this.label != null) {
            if (!this.label.equals(element.label)) {
                return false;
            }
        } else if (element.label != null) {
            return false;
        }
        if (this.lowerBound != null) {
            if (!this.lowerBound.equals(element.lowerBound)) {
                return false;
            }
        } else if (element.lowerBound != null) {
            return false;
        }
        if (this.upperBound != null) {
            if (!this.upperBound.equals(element.upperBound)) {
                return false;
            }
        } else if (element.upperBound != null) {
            return false;
        }
        if (this.enumValue != null) {
            if (!this.enumValue.equals(element.enumValue)) {
                return false;
            }
        } else if (element.enumValue != null) {
            return false;
        }
        if (this.defaultValue != null) {
            if (!this.defaultValue.equals(element.defaultValue)) {
                return false;
            }
        } else if (element.defaultValue != null) {
            return false;
        }
        if (this.aSwitch != null) {
            if (!this.aSwitch.equals(element.aSwitch)) {
                return false;
            }
        } else if (element.aSwitch != null) {
            return false;
        }
        if (this.optionalChildrenInOrder != null) {
            if (!this.optionalChildrenInOrder.equals(element.optionalChildrenInOrder)) {
                return false;
            }
        } else if (element.optionalChildrenInOrder != null) {
            return false;
        }
        if (this.validSwitchValues != null) {
            if (!this.validSwitchValues.equals(element.validSwitchValues)) {
                return false;
            }
        } else if (element.validSwitchValues != null) {
            return false;
        }
        if (this.elementSupplier != null) {
            if (!this.elementSupplier.equals(element.elementSupplier)) {
                return false;
            }
        } else if (element.elementSupplier != null) {
            return false;
        }
        if (this.typeName != null) {
            if (!this.typeName.equals(element.typeName)) {
                return false;
            }
        } else if (element.typeName != null) {
            return false;
        }
        if (this.supplierMap != null) {
            return !this.supplierMap.equals(element.supplierMap);
        }
        return element.supplierMap != null;
    }

    public int hashCode() {
        return (31 * ((31 * ((31 * ((31 * ((31 * ((31 * ((31 * ((31 * ((31 * ((31 * ((31 * ((31 * ((31 * ((31 * ((31 * ((31 * ((31 * ((31 * (this.baseType != null ? this.baseType.hashCode() : 0)) + (this.children != null ? this.children.hashCode() : 0))) + (this.explicit ? 1 : 0))) + (this.label != null ? this.label.hashCode() : 0))) + (this.lowerBound != null ? this.lowerBound.hashCode() : 0))) + (this.upperBound != null ? this.upperBound.hashCode() : 0))) + (this.extensionsInDefinition ? 1 : 0))) + (this.enumValue != null ? this.enumValue.hashCode() : 0))) + (this.defaultValue != null ? this.defaultValue.hashCode() : 0))) + (this.aSwitch != null ? this.aSwitch.hashCode() : 0))) + (this.defaultValuesInChildren ? 1 : 0))) + (this.optionalChildrenInOrder != null ? this.optionalChildrenInOrder.hashCode() : 0))) + (this.validSwitchValues != null ? this.validSwitchValues.hashCode() : 0))) + (this.elementSupplier != null ? this.elementSupplier.hashCode() : 0))) + (this.mayRecurse ? 1 : 0))) + (this.typeName != null ? this.typeName.hashCode() : 0))) + (this.supplierMap != null ? this.supplierMap.hashCode() : 0))) + this.optionals)) + this.block;
    }
}
