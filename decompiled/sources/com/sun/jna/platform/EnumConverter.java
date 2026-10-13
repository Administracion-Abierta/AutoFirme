package com.sun.jna.platform;

import com.sun.jna.FromNativeContext;
import com.sun.jna.ToNativeContext;
import com.sun.jna.TypeConverter;
import java.lang.Enum;

/* JADX INFO: loaded from: autofirma.jar:com/sun/jna/platform/EnumConverter.class */
public class EnumConverter<T extends Enum<T>> implements TypeConverter {
    private final Class<T> clazz;

    public EnumConverter(Class<T> clazz) {
        this.clazz = clazz;
    }

    @Override // com.sun.jna.FromNativeConverter
    public T fromNative(Object input, FromNativeContext context) {
        Integer i = (Integer) input;
        T[] vals = this.clazz.getEnumConstants();
        return vals[i.intValue()];
    }

    @Override // com.sun.jna.ToNativeConverter
    public Integer toNative(Object input, ToNativeContext context) {
        T t = this.clazz.cast(input);
        return Integer.valueOf(t.ordinal());
    }

    @Override // com.sun.jna.FromNativeConverter, com.sun.jna.ToNativeConverter
    public Class<Integer> nativeType() {
        return Integer.class;
    }
}
