package com.ctc.wstx.shaded.msv_core.datatype.xsd;

import com.ctc.wstx.shaded.msv.relaxng_datatype.DatatypeException;
import com.ctc.wstx.shaded.msv_core.scanner.dtd.DTDParser;
import es.gob.afirma.signers.tsp.pkcs7.TsaParams;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:com/ctc/wstx/shaded/msv_core/datatype/xsd/DatatypeFactory.class */
public class DatatypeFactory {
    private static final Map builtinType = createInitialBuiltinTypesMap();

    private DatatypeFactory() {
    }

    public static XSDatatype deriveByList(String nsUri, String newTypeName, XSDatatype itemType) throws DatatypeException {
        return itemType instanceof ErrorType ? itemType : new ListType(nsUri, newTypeName, (XSDatatypeImpl) itemType);
    }

    public static XSDatatype deriveByList(String newTypeName, XSDatatype itemType) throws DatatypeException {
        return deriveByList("", newTypeName, itemType);
    }

    public static XSDatatype deriveByUnion(String nsUri, String newTypeName, XSDatatype[] memberTypes) throws DatatypeException {
        for (int i = 0; i < memberTypes.length; i++) {
            if (memberTypes[i] instanceof ErrorType) {
                return memberTypes[i];
            }
        }
        return new UnionType(nsUri, newTypeName, memberTypes);
    }

    public static XSDatatype deriveByUnion(String newTypeName, XSDatatype[] memberTypes) throws DatatypeException {
        return deriveByUnion("", newTypeName, memberTypes);
    }

    public static XSDatatype deriveByUnion(String newTypeName, Collection memberTypes) throws DatatypeException {
        return deriveByUnion("", newTypeName, memberTypes);
    }

    public static XSDatatype deriveByUnion(String nsUri, String newTypeName, Collection memberTypes) throws DatatypeException {
        XSDatatypeImpl[] m = new XSDatatypeImpl[memberTypes.size()];
        int n = 0;
        Iterator itr = memberTypes.iterator();
        while (itr.hasNext()) {
            for (int i = 0; i < m.length; i++) {
                m[i] = (XSDatatypeImpl) itr.next();
            }
            n++;
        }
        return deriveByUnion(nsUri, newTypeName, m);
    }

    private static void add(Map m, XSDatatypeImpl type) {
        String name = type.getName();
        if (name == null) {
            throw new IllegalArgumentException("anonymous type");
        }
        if (m.containsKey(name)) {
            throw new IllegalArgumentException("multiple definition");
        }
        m.put(name, type);
    }

    public static synchronized XSDatatype getTypeByName(String dataTypeName) throws DatatypeException {
        XSDatatype dt = (XSDatatype) builtinType.get(dataTypeName);
        if (dt != null) {
            return dt;
        }
        try {
            if (dataTypeName.equals("float")) {
                add(builtinType, FloatType.theInstance);
            } else if (dataTypeName.equals("double")) {
                add(builtinType, DoubleType.theInstance);
            } else if (dataTypeName.equals("duration")) {
                add(builtinType, DurationType.theInstance);
            } else if (dataTypeName.equals("dateTime")) {
                add(builtinType, DateTimeType.theInstance);
            } else if (dataTypeName.equals("time")) {
                add(builtinType, TimeType.theInstance);
            } else if (dataTypeName.equals("date")) {
                add(builtinType, DateType.theInstance);
            } else if (dataTypeName.equals("gYearMonth")) {
                add(builtinType, GYearMonthType.theInstance);
            } else if (dataTypeName.equals("gYear")) {
                add(builtinType, GYearType.theInstance);
            } else if (dataTypeName.equals("gMonthDay")) {
                add(builtinType, GMonthDayType.theInstance);
            } else if (dataTypeName.equals("gDay")) {
                add(builtinType, GDayType.theInstance);
            } else if (dataTypeName.equals("gMonth")) {
                add(builtinType, GMonthType.theInstance);
            } else if (dataTypeName.equals("hexBinary")) {
                add(builtinType, HexBinaryType.theInstance);
            } else if (dataTypeName.equals("base64Binary")) {
                add(builtinType, Base64BinaryType.theInstance);
            } else if (dataTypeName.equals("anyURI")) {
                add(builtinType, AnyURIType.theInstance);
            } else if (dataTypeName.equals(DTDParser.TYPE_ENTITY)) {
                add(builtinType, EntityType.theInstance);
            } else if (dataTypeName.equals("language")) {
                add(builtinType, LanguageType.theInstance);
            } else if (dataTypeName.equals(DTDParser.TYPE_ID)) {
                add(builtinType, IDType.theInstance);
            } else if (dataTypeName.equals(DTDParser.TYPE_IDREF)) {
                add(builtinType, IDREFType.theInstance);
            } else if (dataTypeName.equals(DTDParser.TYPE_IDREFS)) {
                add(builtinType, createBuiltinList(DTDParser.TYPE_IDREFS, IDREFType.theInstance));
            } else if (dataTypeName.equals(DTDParser.TYPE_ENTITIES)) {
                add(builtinType, createBuiltinList(DTDParser.TYPE_ENTITIES, EntityType.theInstance));
            } else if (dataTypeName.equals(DTDParser.TYPE_NMTOKENS)) {
                add(builtinType, createBuiltinList(DTDParser.TYPE_NMTOKENS, NmtokenType.theInstance));
            } else if (dataTypeName.equals(DTDParser.TYPE_NOTATION)) {
                add(builtinType, new StringType(DTDParser.TYPE_NOTATION, WhiteSpaceProcessor.theCollapse, false));
            } else if (dataTypeName.equals("nonPositiveInteger")) {
                add(builtinType, NonPositiveIntegerType.theInstance);
            } else if (dataTypeName.equals("unsignedLong")) {
                add(builtinType, UnsignedLongType.theInstance);
            } else if (dataTypeName.equals("unsignedInt")) {
                add(builtinType, UnsignedIntType.theInstance);
            } else if (dataTypeName.equals("unsignedShort")) {
                add(builtinType, UnsignedShortType.theInstance);
            } else if (dataTypeName.equals("unsignedByte")) {
                add(builtinType, UnsignedByteType.theInstance);
            } else if (dataTypeName.equals("anySimpleType")) {
                add(builtinType, SimpleURType.theInstance);
            }
            XSDatatype dt2 = (XSDatatype) builtinType.get(dataTypeName);
            if (dt2 != null) {
                return dt2;
            }
            throw new DatatypeException("undefined type name:" + dataTypeName);
        } catch (DatatypeException e) {
            throw new Error();
        }
    }

    private static XSDatatypeImpl createBuiltinList(String name, XSDatatypeImpl item) throws DatatypeException {
        TypeIncubator ti = new TypeIncubator(new ListType(null, null, item));
        ti.addFacet(XSDatatype.FACET_MINLENGTH, TsaParams.TS_SIGN, false, null);
        return new Proxy("http://www.w3.org/2001/XMLSchema", name, ti.derive(null, null)) { // from class: com.ctc.wstx.shaded.msv_core.datatype.xsd.DatatypeFactory.1
            private Object readResolve() {
                try {
                    return DatatypeFactory.getTypeByName(getName());
                } catch (DatatypeException e) {
                    e.printStackTrace();
                    throw new InternalError(e.getMessage());
                }
            }
        };
    }

    private static Map createInitialBuiltinTypesMap() {
        Map m = new HashMap();
        add(m, StringType.theInstance);
        add(m, BooleanType.theInstance);
        add(m, NumberType.theInstance);
        add(m, QnameType.theInstance);
        add(m, NormalizedStringType.theInstance);
        add(m, TokenType.theInstance);
        add(m, NmtokenType.theInstance);
        add(m, NameType.theInstance);
        add(m, NcnameType.theInstance);
        add(m, IntegerType.theInstance);
        add(m, NegativeIntegerType.theInstance);
        add(m, LongType.theInstance);
        add(m, IntType.theInstance);
        add(m, ShortType.theInstance);
        add(m, ByteType.theInstance);
        add(m, NonNegativeIntegerType.theInstance);
        add(m, PositiveIntegerType.theInstance);
        return m;
    }
}
