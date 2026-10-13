package com.twelvemonkeys.util.convert;

import com.twelvemonkeys.lang.BeanUtil;
import com.twelvemonkeys.lang.StringUtil;
import java.lang.reflect.Array;
import java.lang.reflect.InvocationTargetException;

/* JADX INFO: loaded from: autofirma.jar:com/twelvemonkeys/util/convert/DefaultConverter.class */
public final class DefaultConverter implements PropertyConverter {
    @Override // com.twelvemonkeys.util.convert.PropertyConverter
    public Object toObject(String str, Class cls, String str2) throws ConversionException {
        if (str == null) {
            return null;
        }
        if (cls == null) {
            throw new MissingTypeException();
        }
        if (cls.isArray()) {
            return toArray(str, cls, str2);
        }
        Class<?> clsUnBoxType = unBoxType(cls);
        try {
            Object objCreateInstance = BeanUtil.createInstance(clsUnBoxType, str);
            if (objCreateInstance == null) {
                objCreateInstance = BeanUtil.invokeStaticMethod(clsUnBoxType, "valueOf", str);
                if (objCreateInstance == null) {
                    throw new ConversionException(String.format("Could not convert String to %1$s: No constructor %1$s(String) or static %1$s.valueOf(String) method found!", clsUnBoxType.getName()));
                }
            }
            return objCreateInstance;
        } catch (ConversionException e) {
            throw e;
        } catch (RuntimeException e2) {
            throw new ConversionException(e2);
        } catch (InvocationTargetException e3) {
            throw new ConversionException(e3.getTargetException());
        }
    }

    private Object toArray(String str, Class cls, String str2) {
        String[] stringArray = StringUtil.toStringArray(str, str2 != null ? str2 : StringUtil.DELIMITER_STRING);
        Class<?> componentType = cls.getComponentType();
        if (componentType == String.class) {
            return stringArray;
        }
        Object objNewInstance = Array.newInstance(componentType, stringArray.length);
        for (int i = 0; i < stringArray.length; i++) {
            try {
                Array.set(objNewInstance, i, Converter.getInstance().toObject(stringArray[i], componentType));
            } catch (ConversionException e) {
                if (str2 != null) {
                    throw new ConversionException(String.format("%s for string \"%s\" with format \"%s\"", e.getMessage(), str, str2), e);
                }
                throw new ConversionException(String.format("%s for string \"%s\"", e.getMessage(), str), e);
            }
        }
        return objNewInstance;
    }

    @Override // com.twelvemonkeys.util.convert.PropertyConverter
    public String toString(Object obj, String str) throws ConversionException {
        if (obj == null) {
            return null;
        }
        try {
            return obj.getClass().isArray() ? arrayToString(toObjectArray(obj), str) : obj.toString();
        } catch (RuntimeException e) {
            throw new ConversionException(e);
        }
    }

    private String arrayToString(Object[] objArr, String str) {
        return str == null ? StringUtil.toCSVString(objArr) : StringUtil.toCSVString(objArr, str);
    }

    private Object[] toObjectArray(Object obj) {
        Object[] objArr;
        Class<?> componentType = obj.getClass().getComponentType();
        if (componentType.isPrimitive()) {
            if (Integer.TYPE == componentType) {
                objArr = new Integer[Array.getLength(obj)];
                for (int i = 0; i < objArr.length; i++) {
                    Array.set(objArr, i, Array.get(obj, i));
                }
            } else if (Short.TYPE == componentType) {
                objArr = new Short[Array.getLength(obj)];
                for (int i2 = 0; i2 < objArr.length; i2++) {
                    Array.set(objArr, i2, Array.get(obj, i2));
                }
            } else if (Long.TYPE == componentType) {
                objArr = new Long[Array.getLength(obj)];
                for (int i3 = 0; i3 < objArr.length; i3++) {
                    Array.set(objArr, i3, Array.get(obj, i3));
                }
            } else if (Float.TYPE == componentType) {
                objArr = new Float[Array.getLength(obj)];
                for (int i4 = 0; i4 < objArr.length; i4++) {
                    Array.set(objArr, i4, Array.get(obj, i4));
                }
            } else if (Double.TYPE == componentType) {
                objArr = new Double[Array.getLength(obj)];
                for (int i5 = 0; i5 < objArr.length; i5++) {
                    Array.set(objArr, i5, Array.get(obj, i5));
                }
            } else if (Boolean.TYPE == componentType) {
                objArr = new Boolean[Array.getLength(obj)];
                for (int i6 = 0; i6 < objArr.length; i6++) {
                    Array.set(objArr, i6, Array.get(obj, i6));
                }
            } else if (Byte.TYPE == componentType) {
                objArr = new Byte[Array.getLength(obj)];
                for (int i7 = 0; i7 < objArr.length; i7++) {
                    Array.set(objArr, i7, Array.get(obj, i7));
                }
            } else if (Character.TYPE == componentType) {
                objArr = new Character[Array.getLength(obj)];
                for (int i8 = 0; i8 < objArr.length; i8++) {
                    Array.set(objArr, i8, Array.get(obj, i8));
                }
            } else {
                throw new IllegalArgumentException("Unknown type " + componentType);
            }
        } else {
            objArr = (Object[]) obj;
        }
        return objArr;
    }

    private Class<?> unBoxType(Class<?> cls) {
        if (cls.isPrimitive()) {
            if (cls == Boolean.TYPE) {
                return Boolean.class;
            }
            if (cls == Byte.TYPE) {
                return Byte.class;
            }
            if (cls == Character.TYPE) {
                return Character.class;
            }
            if (cls == Short.TYPE) {
                return Short.class;
            }
            if (cls == Integer.TYPE) {
                return Integer.class;
            }
            if (cls == Float.TYPE) {
                return Float.class;
            }
            if (cls == Long.TYPE) {
                return Long.class;
            }
            if (cls == Double.TYPE) {
                return Double.class;
            }
            throw new IllegalArgumentException("Unknown type: " + cls);
        }
        return cls;
    }
}
