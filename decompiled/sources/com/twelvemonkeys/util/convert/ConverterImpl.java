package com.twelvemonkeys.util.convert;

import org.apache.pdfbox.contentstream.operator.OperatorName;

/* JADX INFO: loaded from: autofirma.jar:com/twelvemonkeys/util/convert/ConverterImpl.class */
class ConverterImpl extends Converter {
    ConverterImpl() {
    }

    private PropertyConverter getConverterForType(Class cls) {
        Class superclass;
        Class cls2 = cls;
        do {
            PropertyConverter propertyConverter = getInstance().converters.get(cls2);
            if (propertyConverter != null) {
                return propertyConverter;
            }
            superclass = cls2.getSuperclass();
            cls2 = superclass;
        } while (superclass != null);
        return null;
    }

    @Override // com.twelvemonkeys.util.convert.Converter, com.twelvemonkeys.util.convert.PropertyConverter
    public Object toObject(String str, Class cls, String str2) throws ConversionException {
        if (str == null) {
            return null;
        }
        if (cls == null) {
            throw new MissingTypeException();
        }
        PropertyConverter converterForType = getConverterForType(cls);
        if (converterForType == null) {
            throw new NoAvailableConverterException("Cannot convert to object, no converter available for type \"" + cls.getName() + OperatorName.SHOW_TEXT_LINE_AND_SPACE);
        }
        return converterForType.toObject(str, cls, str2);
    }

    @Override // com.twelvemonkeys.util.convert.Converter, com.twelvemonkeys.util.convert.PropertyConverter
    public String toString(Object obj, String str) throws ConversionException {
        if (obj == null) {
            return null;
        }
        PropertyConverter converterForType = getConverterForType(obj.getClass());
        if (converterForType == null) {
            throw new NoAvailableConverterException("Cannot object to string, no converter available for type \"" + obj.getClass().getName() + OperatorName.SHOW_TEXT_LINE_AND_SPACE);
        }
        return converterForType.toString(obj, str);
    }
}
