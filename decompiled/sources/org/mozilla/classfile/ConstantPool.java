package org.mozilla.classfile;

import org.mozilla.javascript.ObjToIntMap;
import org.mozilla.javascript.UintMap;

/* JADX INFO: loaded from: autofirma.jar:org/mozilla/classfile/ConstantPool.class */
final class ConstantPool {
    private static final int ConstantPoolSize = 256;
    static final byte CONSTANT_Class = 7;
    static final byte CONSTANT_Fieldref = 9;
    static final byte CONSTANT_Methodref = 10;
    static final byte CONSTANT_InterfaceMethodref = 11;
    static final byte CONSTANT_String = 8;
    static final byte CONSTANT_Integer = 3;
    static final byte CONSTANT_Float = 4;
    static final byte CONSTANT_Long = 5;
    static final byte CONSTANT_Double = 6;
    static final byte CONSTANT_NameAndType = 12;
    static final byte CONSTANT_Utf8 = 1;
    static final byte CONSTANT_MethodType = 16;
    static final byte CONSTANT_MethodHandle = 15;
    static final byte CONSTANT_InvokeDynamic = 18;
    private ClassFileWriter cfw;
    private static final int MAX_UTF_ENCODING_SIZE = 65535;
    private UintMap itsStringConstHash = new UintMap();
    private ObjToIntMap itsUtf8Hash = new ObjToIntMap();
    private ObjToIntMap itsFieldRefHash = new ObjToIntMap();
    private ObjToIntMap itsMethodRefHash = new ObjToIntMap();
    private ObjToIntMap itsClassHash = new ObjToIntMap();
    private ObjToIntMap itsConstantHash = new ObjToIntMap();
    private UintMap itsConstantData = new UintMap();
    private UintMap itsPoolTypes = new UintMap();
    private int itsTopIndex = 1;
    private byte[] itsPool = new byte[256];
    private int itsTop = 0;

    ConstantPool(ClassFileWriter cfw) {
        this.cfw = cfw;
    }

    int write(byte[] data, int offset) {
        int offset2 = ClassFileWriter.putInt16((short) this.itsTopIndex, data, offset);
        System.arraycopy(this.itsPool, 0, data, offset2, this.itsTop);
        return offset2 + this.itsTop;
    }

    int getWriteSize() {
        return 2 + this.itsTop;
    }

    int addConstant(int k) {
        ensure(5);
        byte[] bArr = this.itsPool;
        int i = this.itsTop;
        this.itsTop = i + 1;
        bArr[i] = 3;
        this.itsTop = ClassFileWriter.putInt32(k, this.itsPool, this.itsTop);
        this.itsPoolTypes.put(this.itsTopIndex, 3);
        int i2 = this.itsTopIndex;
        this.itsTopIndex = i2 + 1;
        return (short) i2;
    }

    int addConstant(long k) {
        ensure(9);
        byte[] bArr = this.itsPool;
        int i = this.itsTop;
        this.itsTop = i + 1;
        bArr[i] = 5;
        this.itsTop = ClassFileWriter.putInt64(k, this.itsPool, this.itsTop);
        int index = this.itsTopIndex;
        this.itsTopIndex += 2;
        this.itsPoolTypes.put(index, 5);
        return index;
    }

    int addConstant(float k) {
        ensure(5);
        byte[] bArr = this.itsPool;
        int i = this.itsTop;
        this.itsTop = i + 1;
        bArr[i] = 4;
        int bits = Float.floatToIntBits(k);
        this.itsTop = ClassFileWriter.putInt32(bits, this.itsPool, this.itsTop);
        this.itsPoolTypes.put(this.itsTopIndex, 4);
        int i2 = this.itsTopIndex;
        this.itsTopIndex = i2 + 1;
        return i2;
    }

    int addConstant(double k) {
        ensure(9);
        byte[] bArr = this.itsPool;
        int i = this.itsTop;
        this.itsTop = i + 1;
        bArr[i] = 6;
        long bits = Double.doubleToLongBits(k);
        this.itsTop = ClassFileWriter.putInt64(bits, this.itsPool, this.itsTop);
        int index = this.itsTopIndex;
        this.itsTopIndex += 2;
        this.itsPoolTypes.put(index, 6);
        return index;
    }

    int addConstant(String k) {
        int utf8Index = 65535 & addUtf8(k);
        int theIndex = this.itsStringConstHash.getInt(utf8Index, -1);
        if (theIndex == -1) {
            int i = this.itsTopIndex;
            this.itsTopIndex = i + 1;
            theIndex = i;
            ensure(3);
            byte[] bArr = this.itsPool;
            int i2 = this.itsTop;
            this.itsTop = i2 + 1;
            bArr[i2] = 8;
            this.itsTop = ClassFileWriter.putInt16(utf8Index, this.itsPool, this.itsTop);
            this.itsStringConstHash.put(utf8Index, theIndex);
        }
        this.itsPoolTypes.put(theIndex, 8);
        return theIndex;
    }

    int addConstant(Object value) {
        if ((value instanceof Integer) || (value instanceof Byte) || (value instanceof Short)) {
            return addConstant(((Number) value).intValue());
        }
        if (value instanceof Character) {
            return addConstant((int) ((Character) value).charValue());
        }
        if (value instanceof Boolean) {
            return addConstant(((Boolean) value).booleanValue() ? 1 : 0);
        }
        if (value instanceof Float) {
            return addConstant(((Float) value).floatValue());
        }
        if (value instanceof Long) {
            return addConstant(((Long) value).longValue());
        }
        if (value instanceof Double) {
            return addConstant(((Double) value).doubleValue());
        }
        if (value instanceof String) {
            return addConstant((String) value);
        }
        if (value instanceof ClassFileWriter.MHandle) {
            return addMethodHandle((ClassFileWriter.MHandle) value);
        }
        throw new IllegalArgumentException("value " + value);
    }

    boolean isUnderUtfEncodingLimit(String s) {
        int strLen = s.length();
        if (strLen * 3 <= 65535) {
            return true;
        }
        return strLen <= 65535 && strLen == getUtfEncodingLimit(s, 0, strLen);
    }

    int getUtfEncodingLimit(String s, int start, int end) {
        if ((end - start) * 3 <= 65535) {
            return end;
        }
        int limit = 65535;
        for (int i = start; i != end; i++) {
            int c = s.charAt(i);
            if (0 != c && c <= 127) {
                limit--;
            } else if (c < 2047) {
                limit -= 2;
            } else {
                limit -= 3;
            }
            if (limit < 0) {
                return i;
            }
        }
        return end;
    }

    short addUtf8(String k) {
        boolean tooBigString;
        int theIndex = this.itsUtf8Hash.get(k, -1);
        if (theIndex == -1) {
            int strLen = k.length();
            if (strLen > 65535) {
                tooBigString = true;
            } else {
                tooBigString = false;
                ensure(3 + (strLen * 3));
                int top = this.itsTop;
                this.itsPool[top] = 1;
                int top2 = top + 1 + 2;
                char[] chars = this.cfw.getCharBuffer(strLen);
                k.getChars(0, strLen, chars, 0);
                for (int i = 0; i != strLen; i++) {
                    char c = chars[i];
                    if (c != 0 && c <= 127) {
                        int i2 = top2;
                        top2++;
                        this.itsPool[i2] = (byte) c;
                    } else if (c > 2047) {
                        int i3 = top2;
                        int top3 = top2 + 1;
                        this.itsPool[i3] = (byte) (224 | (c >> '\f'));
                        int top4 = top3 + 1;
                        this.itsPool[top3] = (byte) (128 | ((c >> 6) & 63));
                        top2 = top4 + 1;
                        this.itsPool[top4] = (byte) (128 | (c & '?'));
                    } else {
                        int i4 = top2;
                        int top5 = top2 + 1;
                        this.itsPool[i4] = (byte) (192 | (c >> 6));
                        top2 = top5 + 1;
                        this.itsPool[top5] = (byte) (128 | (c & '?'));
                    }
                }
                int utfLen = top2 - ((this.itsTop + 1) + 2);
                if (utfLen > 65535) {
                    tooBigString = true;
                } else {
                    this.itsPool[this.itsTop + 1] = (byte) (utfLen >>> 8);
                    this.itsPool[this.itsTop + 2] = (byte) utfLen;
                    this.itsTop = top2;
                    int i5 = this.itsTopIndex;
                    this.itsTopIndex = i5 + 1;
                    theIndex = i5;
                    this.itsUtf8Hash.put(k, theIndex);
                }
            }
            if (tooBigString) {
                throw new IllegalArgumentException("Too big string");
            }
        }
        setConstantData(theIndex, k);
        this.itsPoolTypes.put(theIndex, 1);
        return (short) theIndex;
    }

    private short addNameAndType(String name, String type) {
        short nameIndex = addUtf8(name);
        short typeIndex = addUtf8(type);
        ensure(5);
        byte[] bArr = this.itsPool;
        int i = this.itsTop;
        this.itsTop = i + 1;
        bArr[i] = 12;
        this.itsTop = ClassFileWriter.putInt16(nameIndex, this.itsPool, this.itsTop);
        this.itsTop = ClassFileWriter.putInt16(typeIndex, this.itsPool, this.itsTop);
        this.itsPoolTypes.put(this.itsTopIndex, 12);
        int i2 = this.itsTopIndex;
        this.itsTopIndex = i2 + 1;
        return (short) i2;
    }

    short addClass(String className) {
        int theIndex = this.itsClassHash.get(className, -1);
        if (theIndex == -1) {
            String slashed = className;
            if (className.indexOf(46) > 0) {
                slashed = ClassFileWriter.getSlashedForm(className);
                theIndex = this.itsClassHash.get(slashed, -1);
                if (theIndex != -1) {
                    this.itsClassHash.put(className, theIndex);
                }
            }
            if (theIndex == -1) {
                int utf8Index = addUtf8(slashed);
                ensure(3);
                byte[] bArr = this.itsPool;
                int i = this.itsTop;
                this.itsTop = i + 1;
                bArr[i] = 7;
                this.itsTop = ClassFileWriter.putInt16(utf8Index, this.itsPool, this.itsTop);
                int i2 = this.itsTopIndex;
                this.itsTopIndex = i2 + 1;
                theIndex = i2;
                this.itsClassHash.put(slashed, theIndex);
                if (!className.equals(slashed)) {
                    this.itsClassHash.put(className, theIndex);
                }
            }
        }
        setConstantData(theIndex, className);
        this.itsPoolTypes.put(theIndex, 7);
        return (short) theIndex;
    }

    short addFieldRef(String className, String fieldName, String fieldType) {
        FieldOrMethodRef ref = new FieldOrMethodRef(className, fieldName, fieldType);
        int theIndex = this.itsFieldRefHash.get(ref, -1);
        if (theIndex == -1) {
            short ntIndex = addNameAndType(fieldName, fieldType);
            short classIndex = addClass(className);
            ensure(5);
            byte[] bArr = this.itsPool;
            int i = this.itsTop;
            this.itsTop = i + 1;
            bArr[i] = 9;
            this.itsTop = ClassFileWriter.putInt16(classIndex, this.itsPool, this.itsTop);
            this.itsTop = ClassFileWriter.putInt16(ntIndex, this.itsPool, this.itsTop);
            int i2 = this.itsTopIndex;
            this.itsTopIndex = i2 + 1;
            theIndex = i2;
            this.itsFieldRefHash.put(ref, theIndex);
        }
        setConstantData(theIndex, ref);
        this.itsPoolTypes.put(theIndex, 9);
        return (short) theIndex;
    }

    short addMethodRef(String className, String methodName, String methodType) {
        FieldOrMethodRef ref = new FieldOrMethodRef(className, methodName, methodType);
        int theIndex = this.itsMethodRefHash.get(ref, -1);
        if (theIndex == -1) {
            short ntIndex = addNameAndType(methodName, methodType);
            short classIndex = addClass(className);
            ensure(5);
            byte[] bArr = this.itsPool;
            int i = this.itsTop;
            this.itsTop = i + 1;
            bArr[i] = 10;
            this.itsTop = ClassFileWriter.putInt16(classIndex, this.itsPool, this.itsTop);
            this.itsTop = ClassFileWriter.putInt16(ntIndex, this.itsPool, this.itsTop);
            int i2 = this.itsTopIndex;
            this.itsTopIndex = i2 + 1;
            theIndex = i2;
            this.itsMethodRefHash.put(ref, theIndex);
        }
        setConstantData(theIndex, ref);
        this.itsPoolTypes.put(theIndex, 10);
        return (short) theIndex;
    }

    short addInterfaceMethodRef(String className, String methodName, String methodType) {
        short ntIndex = addNameAndType(methodName, methodType);
        short classIndex = addClass(className);
        ensure(5);
        byte[] bArr = this.itsPool;
        int i = this.itsTop;
        this.itsTop = i + 1;
        bArr[i] = 11;
        this.itsTop = ClassFileWriter.putInt16(classIndex, this.itsPool, this.itsTop);
        this.itsTop = ClassFileWriter.putInt16(ntIndex, this.itsPool, this.itsTop);
        FieldOrMethodRef r = new FieldOrMethodRef(className, methodName, methodType);
        setConstantData(this.itsTopIndex, r);
        this.itsPoolTypes.put(this.itsTopIndex, 11);
        int i2 = this.itsTopIndex;
        this.itsTopIndex = i2 + 1;
        return (short) i2;
    }

    short addInvokeDynamic(String methodName, String methodType, int bootstrapIndex) {
        ConstantEntry entry = new ConstantEntry(18, bootstrapIndex, methodName, methodType);
        int theIndex = this.itsConstantHash.get(entry, -1);
        if (theIndex == -1) {
            short nameTypeIndex = addNameAndType(methodName, methodType);
            ensure(5);
            byte[] bArr = this.itsPool;
            int i = this.itsTop;
            this.itsTop = i + 1;
            bArr[i] = 18;
            this.itsTop = ClassFileWriter.putInt16(bootstrapIndex, this.itsPool, this.itsTop);
            this.itsTop = ClassFileWriter.putInt16(nameTypeIndex, this.itsPool, this.itsTop);
            int i2 = this.itsTopIndex;
            this.itsTopIndex = i2 + 1;
            theIndex = i2;
            this.itsConstantHash.put(entry, theIndex);
            setConstantData(theIndex, methodType);
            this.itsPoolTypes.put(theIndex, 18);
        }
        return (short) theIndex;
    }

    short addMethodHandle(ClassFileWriter.MHandle mh) {
        short ref;
        int theIndex = this.itsConstantHash.get(mh, -1);
        if (theIndex == -1) {
            if (mh.tag <= 4) {
                ref = addFieldRef(mh.owner, mh.name, mh.desc);
            } else if (mh.tag == 9) {
                ref = addInterfaceMethodRef(mh.owner, mh.name, mh.desc);
            } else {
                ref = addMethodRef(mh.owner, mh.name, mh.desc);
            }
            ensure(4);
            byte[] bArr = this.itsPool;
            int i = this.itsTop;
            this.itsTop = i + 1;
            bArr[i] = 15;
            byte[] bArr2 = this.itsPool;
            int i2 = this.itsTop;
            this.itsTop = i2 + 1;
            bArr2[i2] = mh.tag;
            this.itsTop = ClassFileWriter.putInt16(ref, this.itsPool, this.itsTop);
            int i3 = this.itsTopIndex;
            this.itsTopIndex = i3 + 1;
            theIndex = i3;
            this.itsConstantHash.put(mh, theIndex);
            this.itsPoolTypes.put(theIndex, 15);
        }
        return (short) theIndex;
    }

    Object getConstantData(int index) {
        return this.itsConstantData.getObject(index);
    }

    void setConstantData(int index, Object data) {
        this.itsConstantData.put(index, data);
    }

    byte getConstantType(int index) {
        return (byte) this.itsPoolTypes.getInt(index, 0);
    }

    private void ensure(int howMuch) {
        if (this.itsTop + howMuch > this.itsPool.length) {
            int newCapacity = this.itsPool.length * 2;
            if (this.itsTop + howMuch > newCapacity) {
                newCapacity = this.itsTop + howMuch;
            }
            byte[] tmp = new byte[newCapacity];
            System.arraycopy(this.itsPool, 0, tmp, 0, this.itsTop);
            this.itsPool = tmp;
        }
    }
}
