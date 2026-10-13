package org.bouncycastle.oer;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1Integer;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/oer/OERDefinition.class */
public class OERDefinition {
    static final BigInteger[] uIntMax = {new BigInteger("256"), new BigInteger("65536"), new BigInteger("4294967296"), new BigInteger("18446744073709551616")};
    static final BigInteger[][] sIntRange = {new BigInteger[]{new BigInteger("-128"), new BigInteger("127")}, new BigInteger[]{new BigInteger("-32768"), new BigInteger("32767")}, new BigInteger[]{new BigInteger("-2147483648"), new BigInteger("2147483647")}, new BigInteger[]{new BigInteger("-9223372036854775808"), new BigInteger("9223372036854775807")}};

    /* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/oer/OERDefinition$BaseType.class */
    public enum BaseType {
        SEQ,
        SEQ_OF,
        CHOICE,
        ENUM,
        INT,
        OCTET_STRING,
        OPAQUE,
        UTF8_STRING,
        BIT_STRING,
        NULL,
        EXTENSION,
        ENUM_ITEM,
        BOOLEAN,
        IS0646String,
        PrintableString,
        NumericString,
        BMPString,
        UniversalString,
        IA5String,
        VisibleString,
        Switch,
        Supplier
    }

    /* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/oer/OERDefinition$Builder.class */
    public static class Builder {
        protected final BaseType baseType;
        protected String typeName;
        protected String label;
        protected BigInteger upperBound;
        protected BigInteger lowerBound;
        protected BigInteger enumValue;
        protected ASN1Encodable defaultValue;
        protected Builder placeholderValue;
        protected Boolean inScope;
        protected Switch aSwitch;
        protected ElementSupplier elementSupplier;
        protected boolean mayRecurse;
        protected int block;
        protected ArrayList<Builder> children = new ArrayList<>();
        protected boolean explicit = true;
        protected ArrayList<ASN1Encodable> validSwitchValues = new ArrayList<>();
        protected Map<String, ElementSupplier> supplierMap = new HashMap();
        private final ItemProvider defaultItemProvider = new ItemProvider() { // from class: org.bouncycastle.oer.OERDefinition.Builder.1
            @Override // org.bouncycastle.oer.OERDefinition.ItemProvider
            public Builder existingChild(int i, Builder builder) {
                return builder.copy(Builder.this.defaultItemProvider);
            }
        };

        public Builder(BaseType baseType) {
            this.baseType = baseType;
        }

        /* JADX INFO: Access modifiers changed from: private */
        public Builder copy(ItemProvider itemProvider) {
            Builder builder = new Builder(this.baseType);
            int i = 0;
            Iterator<Builder> it = this.children.iterator();
            while (it.hasNext()) {
                int i2 = i;
                i++;
                builder.children.add(itemProvider.existingChild(i2, it.next()));
            }
            builder.explicit = this.explicit;
            builder.label = this.label;
            builder.upperBound = this.upperBound;
            builder.lowerBound = this.lowerBound;
            builder.defaultValue = this.defaultValue;
            builder.enumValue = this.enumValue;
            builder.inScope = this.inScope;
            builder.aSwitch = this.aSwitch;
            builder.validSwitchValues = new ArrayList<>(this.validSwitchValues);
            builder.elementSupplier = this.elementSupplier;
            builder.mayRecurse = this.mayRecurse;
            builder.typeName = this.typeName;
            builder.supplierMap = new HashMap(this.supplierMap);
            builder.block = this.block;
            return builder;
        }

        protected Builder block(int i) {
            Builder builderCopy = copy();
            builderCopy.block = i;
            return builderCopy;
        }

        public Builder copy() {
            return copy(this.defaultItemProvider);
        }

        public Builder elementSupplier(ElementSupplier elementSupplier) {
            Builder builderCopy = copy();
            builderCopy.elementSupplier = elementSupplier;
            return builderCopy;
        }

        public Builder validSwitchValue(ASN1Encodable... aSN1EncodableArr) {
            Builder builderCopy = copy();
            builderCopy.validSwitchValues.addAll(Arrays.asList(aSN1EncodableArr));
            return builderCopy;
        }

        public Builder inScope(boolean z) {
            Builder builderCopy = copy();
            builderCopy.inScope = Boolean.valueOf(z);
            return builderCopy;
        }

        public Builder limitScopeTo(String... strArr) {
            Builder builderCopy = copy();
            HashSet hashSet = new HashSet();
            hashSet.addAll(Arrays.asList(strArr));
            ArrayList<Builder> arrayList = new ArrayList<>();
            for (Builder builder : this.children) {
                arrayList.add(builder.copy().inScope(hashSet.contains(builder.label)));
            }
            builderCopy.children = arrayList;
            return builderCopy;
        }

        public Builder typeName(String str) {
            Builder builderCopy = copy();
            builderCopy.typeName = str;
            if (builderCopy.label == null) {
                builderCopy.label = str;
            }
            return builderCopy;
        }

        public Builder unbounded() {
            Builder builderCopy = copy();
            builderCopy.lowerBound = null;
            builderCopy.upperBound = null;
            return builderCopy;
        }

        public Builder decodeSwitch(Switch r4) {
            Builder builderCopy = copy();
            builderCopy.aSwitch = r4;
            return builderCopy;
        }

        public Builder labelPrefix(String str) {
            Builder builderCopy = copy();
            builderCopy.label = str + " " + this.label;
            return builderCopy;
        }

        public Builder explicit(boolean z) {
            Builder builderCopy = copy();
            builderCopy.explicit = z;
            return builderCopy;
        }

        public Builder defaultValue(ASN1Encodable aSN1Encodable) {
            Builder builderCopy = copy();
            builderCopy.defaultValue = aSN1Encodable;
            return builderCopy;
        }

        protected Builder wrap(boolean z, Object obj) {
            if (obj instanceof Builder) {
                return ((Builder) obj).explicit(z);
            }
            if (obj instanceof BaseType) {
                return new Builder((BaseType) obj).explicit(z);
            }
            if (obj instanceof String) {
                return OERDefinition.enumItem((String) obj);
            }
            throw new IllegalStateException("Unable to wrap item in builder");
        }

        protected void addExtensions(Builder builder, ExtensionList extensionList) {
            if (extensionList.isEmpty()) {
                Builder builder2 = new Builder(BaseType.EXTENSION);
                builder2.block = extensionList.block;
                builder.children.add(builder2);
                return;
            }
            for (Object obj : extensionList) {
                if (obj instanceof OptionalList) {
                    addOptionals(builder, extensionList.block, (OptionalList) obj);
                } else {
                    Builder builderWrap = wrap(true, obj);
                    builderWrap.block = extensionList.block;
                    builder.children.add(builderWrap);
                }
            }
        }

        protected void addOptionals(Builder builder, int i, OptionalList optionalList) {
            for (Object obj : optionalList) {
                if (obj instanceof ExtensionList) {
                    addExtensions(builder, (ExtensionList) obj);
                } else {
                    Builder builderWrap = wrap(false, obj);
                    builderWrap.block = i;
                    builder.children.add(builderWrap);
                }
            }
        }

        public Builder items(Object... objArr) {
            Builder builderCopy = copy();
            for (int i = 0; i != objArr.length; i++) {
                Object obj = objArr[i];
                if (obj instanceof ExtensionList) {
                    addExtensions(builderCopy, (ExtensionList) obj);
                } else if (obj instanceof OptionalList) {
                    addOptionals(builderCopy, builderCopy.block, (OptionalList) obj);
                } else if (obj.getClass().isArray()) {
                    for (int i2 = 0; i2 < ((Object[]) obj).length; i2++) {
                        builderCopy.children.add(wrap(true, ((Object[]) obj)[i2]));
                    }
                } else {
                    builderCopy.children.add(wrap(true, obj));
                }
            }
            return builderCopy;
        }

        public Builder label(String str) {
            Builder builderCopy = copy();
            builderCopy.label = str;
            return builderCopy;
        }

        public Builder mayRecurse(boolean z) {
            Builder builderCopy = copy();
            builderCopy.mayRecurse = z;
            return builderCopy;
        }

        public Element build() {
            ArrayList arrayList = new ArrayList();
            boolean z = false;
            if (this.baseType == BaseType.ENUM) {
                int i = 0;
                HashSet hashSet = new HashSet();
                for (int i2 = 0; i2 < this.children.size(); i2++) {
                    Builder builder = this.children.get(i2);
                    if (builder.enumValue == null) {
                        builder.enumValue = BigInteger.valueOf(i);
                        i++;
                    }
                    if (hashSet.contains(builder.enumValue)) {
                        throw new IllegalStateException("duplicate enum value at index " + i2);
                    }
                    hashSet.add(builder.enumValue);
                }
            }
            int i3 = 0;
            boolean z2 = false;
            for (Builder builder2 : this.children) {
                if (!z && builder2.block > 0) {
                    z = true;
                }
                if (!builder2.explicit) {
                    i3++;
                }
                if (!z2 && builder2.defaultValue != null) {
                    z2 = true;
                }
                arrayList.add(builder2.build());
            }
            return new Element(this.baseType, arrayList, this.defaultValue == null && this.explicit, this.label, this.lowerBound, this.upperBound, z, this.enumValue, this.defaultValue, this.aSwitch, this.validSwitchValues.isEmpty() ? null : this.validSwitchValues, this.elementSupplier, this.mayRecurse, this.typeName, this.supplierMap.isEmpty() ? null : this.supplierMap, this.block, i3, z2);
        }

        public Builder range(BigInteger bigInteger, BigInteger bigInteger2) {
            Builder builderCopy = copy();
            builderCopy.lowerBound = bigInteger;
            builderCopy.upperBound = bigInteger2;
            return builderCopy;
        }

        public Builder rangeToMAXFrom(long j) {
            Builder builderCopy = copy();
            builderCopy.lowerBound = BigInteger.valueOf(j);
            builderCopy.upperBound = null;
            return builderCopy;
        }

        public Builder rangeZeroTo(long j) {
            Builder builderCopy = copy();
            builderCopy.upperBound = BigInteger.valueOf(j);
            builderCopy.lowerBound = BigInteger.ZERO;
            return builderCopy;
        }

        public Builder fixedSize(long j) {
            Builder builderCopy = copy();
            builderCopy.upperBound = BigInteger.valueOf(j);
            builderCopy.lowerBound = BigInteger.valueOf(j);
            return builderCopy;
        }

        public Builder range(long j, long j2, ASN1Encodable aSN1Encodable) {
            Builder builderCopy = copy();
            builderCopy.lowerBound = BigInteger.valueOf(j);
            builderCopy.upperBound = BigInteger.valueOf(j2);
            builderCopy.defaultValue = aSN1Encodable;
            return builderCopy;
        }

        public Builder enumValue(BigInteger bigInteger) {
            Builder builderCopy = copy();
            this.enumValue = bigInteger;
            return builderCopy;
        }

        public Builder replaceChild(final int i, final Builder builder) {
            return copy(new ItemProvider() { // from class: org.bouncycastle.oer.OERDefinition.Builder.2
                @Override // org.bouncycastle.oer.OERDefinition.ItemProvider
                public Builder existingChild(int i2, Builder builder2) {
                    return i == i2 ? builder : builder2;
                }
            });
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/oer/OERDefinition$ExtensionList.class */
    public static class ExtensionList extends ArrayList<Object> {
        protected final int block;

        public ExtensionList(int i, List<Object> list) {
            this.block = i;
            addAll(list);
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/oer/OERDefinition$ItemProvider.class */
    public interface ItemProvider {
        Builder existingChild(int i, Builder builder);
    }

    /* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/oer/OERDefinition$MutableBuilder.class */
    public static class MutableBuilder extends Builder {
        private boolean frozen;

        public MutableBuilder(BaseType baseType) {
            super(baseType);
            this.frozen = false;
        }

        @Override // org.bouncycastle.oer.OERDefinition.Builder
        public MutableBuilder label(String str) {
            this.label = str;
            return this;
        }

        /* JADX WARN: Multi-variable type inference failed */
        public MutableBuilder addItemsAndFreeze(Builder... builderArr) {
            if (this.frozen) {
                throw new IllegalStateException("build cannot be modified and must be copied only");
            }
            for (int i = 0; i != builderArr.length; i++) {
                Object[] objArr = builderArr[i];
                if (objArr instanceof OptionalList) {
                    Iterator it = ((List) objArr).iterator();
                    while (it.hasNext()) {
                        this.children.add(wrap(false, it.next()));
                    }
                } else if (objArr.getClass().isArray()) {
                    for (Object obj : objArr) {
                        this.children.add(wrap(true, obj));
                    }
                } else {
                    this.children.add(wrap(true, objArr));
                }
            }
            this.frozen = true;
            return this;
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/oer/OERDefinition$OptionalList.class */
    private static class OptionalList extends ArrayList<Object> {
        public OptionalList(List<Object> list) {
            addAll(list);
        }
    }

    public static Builder bool() {
        return new Builder(BaseType.BOOLEAN);
    }

    public static Builder integer() {
        return new Builder(BaseType.INT);
    }

    public static Builder integer(long j) {
        return new Builder(BaseType.INT).defaultValue(new ASN1Integer(j));
    }

    public static Builder bitString(long j) {
        return new Builder(BaseType.BIT_STRING).fixedSize(j);
    }

    public static Builder integer(BigInteger bigInteger, BigInteger bigInteger2) {
        return new Builder(BaseType.INT).range(bigInteger, bigInteger2);
    }

    public static Builder integer(long j, long j2) {
        return new Builder(BaseType.INT).range(BigInteger.valueOf(j), BigInteger.valueOf(j2));
    }

    public static Builder integer(long j, long j2, ASN1Encodable aSN1Encodable) {
        return new Builder(BaseType.INT).range(j, j2, aSN1Encodable);
    }

    public static Builder nullValue() {
        return new Builder(BaseType.NULL);
    }

    public static Builder seq() {
        return new Builder(BaseType.SEQ);
    }

    public static Builder seq(Object... objArr) {
        return new Builder(BaseType.SEQ).items(objArr);
    }

    public static Builder aSwitch(Switch r4) {
        return new Builder(BaseType.Switch).decodeSwitch(r4);
    }

    public static Builder enumItem(String str) {
        return new Builder(BaseType.ENUM_ITEM).label(str);
    }

    public static Builder enumItem(String str, BigInteger bigInteger) {
        return new Builder(BaseType.ENUM_ITEM).enumValue(bigInteger).label(str);
    }

    public static Builder enumeration(Object... objArr) {
        return new Builder(BaseType.ENUM).items(objArr);
    }

    public static Builder choice(Object... objArr) {
        return new Builder(BaseType.CHOICE).items(objArr);
    }

    public static Builder placeholder() {
        return new Builder(null);
    }

    public static Builder seqof(Object... objArr) {
        return new Builder(BaseType.SEQ_OF).items(objArr);
    }

    public static Builder octets() {
        return new Builder(BaseType.OCTET_STRING).unbounded();
    }

    public static Builder octets(int i) {
        return new Builder(BaseType.OCTET_STRING).fixedSize(i);
    }

    public static Builder octets(int i, int i2) {
        return new Builder(BaseType.OCTET_STRING).range(BigInteger.valueOf(i), BigInteger.valueOf(i2));
    }

    public static Builder ia5String() {
        return new Builder(BaseType.IA5String);
    }

    public static Builder utf8String() {
        return new Builder(BaseType.UTF8_STRING);
    }

    public static Builder utf8String(int i) {
        return new Builder(BaseType.UTF8_STRING).rangeToMAXFrom(i);
    }

    public static Builder utf8String(int i, int i2) {
        return new Builder(BaseType.UTF8_STRING).range(BigInteger.valueOf(i), BigInteger.valueOf(i2));
    }

    public static Builder opaque() {
        return new Builder(BaseType.OPAQUE);
    }

    public static List<Object> optional(Object... objArr) {
        return new OptionalList(Arrays.asList(objArr));
    }

    public static ExtensionList extension(Object... objArr) {
        return new ExtensionList(1, Arrays.asList(objArr));
    }

    public static ExtensionList extension(int i, Object... objArr) {
        return new ExtensionList(i, Arrays.asList(objArr));
    }

    public static Builder deferred(ElementSupplier elementSupplier) {
        return new Builder(BaseType.Supplier).elementSupplier(elementSupplier);
    }
}
