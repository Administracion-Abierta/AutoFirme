package com.twelvemonkeys.util;

/* JADX INFO: loaded from: autofirma.jar:com/twelvemonkeys/util/DuplicateHandler.class */
public interface DuplicateHandler<T> {
    public static final DuplicateHandler<?> USE_FIRST_VALUE = new DuplicateHandler() { // from class: com.twelvemonkeys.util.DuplicateHandler.1
        @Override // com.twelvemonkeys.util.DuplicateHandler
        public Object resolve(Object obj, Object obj2) {
            return obj;
        }
    };
    public static final DuplicateHandler<?> USE_LAST_VALUE = new DuplicateHandler() { // from class: com.twelvemonkeys.util.DuplicateHandler.2
        @Override // com.twelvemonkeys.util.DuplicateHandler
        public Object resolve(Object obj, Object obj2) {
            return obj2;
        }
    };
    public static final DuplicateHandler<?> DUPLICATES_AS_ARRAY = new DuplicateHandler() { // from class: com.twelvemonkeys.util.DuplicateHandler.3
        @Override // com.twelvemonkeys.util.DuplicateHandler
        public Object resolve(Object obj, Object obj2) {
            Object[] objArr;
            if (obj instanceof Object[]) {
                Object[] objArr2 = (Object[]) obj;
                objArr = new Object[objArr2.length + 1];
                System.arraycopy(objArr2, 0, objArr, 0, objArr2.length);
                objArr[objArr2.length] = obj2;
            } else {
                objArr = new Object[]{obj, obj2};
            }
            return objArr;
        }
    };
    public static final DuplicateHandler<String> DUPLICATES_AS_CSV = new DuplicateHandler<String>() { // from class: com.twelvemonkeys.util.DuplicateHandler.4
        @Override // com.twelvemonkeys.util.DuplicateHandler
        public String resolve(String str, String str2) {
            return String.valueOf(str) + ',' + str2;
        }
    };

    T resolve(T t, T t2);
}
