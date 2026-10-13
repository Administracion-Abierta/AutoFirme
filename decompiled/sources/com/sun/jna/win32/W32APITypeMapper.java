package com.sun.jna.win32;

import com.sun.jna.DefaultTypeMapper;
import com.sun.jna.FromNativeContext;
import com.sun.jna.StringArray;
import com.sun.jna.ToNativeContext;
import com.sun.jna.TypeConverter;
import com.sun.jna.TypeMapper;
import com.sun.jna.WString;

/* JADX INFO: loaded from: autofirma.jar:com/sun/jna/win32/W32APITypeMapper.class */
public class W32APITypeMapper extends DefaultTypeMapper {
    public static final TypeMapper UNICODE = new W32APITypeMapper(true);
    public static final TypeMapper ASCII = new W32APITypeMapper(false);
    public static final TypeMapper DEFAULT;

    static {
        DEFAULT = Boolean.getBoolean("w32.ascii") ? ASCII : UNICODE;
    }

    protected W32APITypeMapper(boolean unicode) {
        if (unicode) {
            TypeConverter stringConverter = new TypeConverter() { // from class: com.sun.jna.win32.W32APITypeMapper.1
                @Override // com.sun.jna.ToNativeConverter
                public Object toNative(Object value, ToNativeContext context) {
                    if (value == null) {
                        return null;
                    }
                    if (value instanceof String[]) {
                        return new StringArray((String[]) value, true);
                    }
                    return new WString(value.toString());
                }

                @Override // com.sun.jna.FromNativeConverter
                public Object fromNative(Object value, FromNativeContext context) {
                    if (value == null) {
                        return null;
                    }
                    return value.toString();
                }

                @Override // com.sun.jna.FromNativeConverter, com.sun.jna.ToNativeConverter
                public Class<?> nativeType() {
                    return WString.class;
                }
            };
            addTypeConverter(String.class, stringConverter);
            addToNativeConverter(String[].class, stringConverter);
        }
        TypeConverter booleanConverter = new TypeConverter() { // from class: com.sun.jna.win32.W32APITypeMapper.2
            @Override // com.sun.jna.ToNativeConverter
            public Object toNative(Object value, ToNativeContext context) {
                return Integer.valueOf(Boolean.TRUE.equals(value) ? 1 : 0);
            }

            @Override // com.sun.jna.FromNativeConverter
            public Object fromNative(Object value, FromNativeContext context) {
                return ((Integer) value).intValue() != 0 ? Boolean.TRUE : Boolean.FALSE;
            }

            @Override // com.sun.jna.FromNativeConverter, com.sun.jna.ToNativeConverter
            public Class<?> nativeType() {
                return Integer.class;
            }
        };
        addTypeConverter(Boolean.class, booleanConverter);
    }
}
