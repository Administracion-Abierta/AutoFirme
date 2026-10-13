package com.sun.jna.platform.win32;

import com.sun.jna.Pointer;
import java.lang.reflect.Array;

/* JADX INFO: loaded from: autofirma.jar:com/sun/jna/platform/win32/OaIdlUtil.class */
public abstract class OaIdlUtil {
    public static Object toPrimitiveArray(OaIdl.SAFEARRAY sa, boolean destruct) {
        Object sourceArray;
        Pointer dataPointer = sa.accessData();
        try {
            int dimensions = sa.getDimensionCount();
            int[] elements = new int[dimensions];
            int[] cumElements = new int[dimensions];
            int varType = sa.getVarType().intValue();
            for (int i = 0; i < dimensions; i++) {
                elements[i] = (sa.getUBound(i) - sa.getLBound(i)) + 1;
            }
            for (int i2 = dimensions - 1; i2 >= 0; i2--) {
                if (i2 == dimensions - 1) {
                    cumElements[i2] = 1;
                } else {
                    cumElements[i2] = cumElements[i2 + 1] * elements[i2 + 1];
                }
            }
            if (dimensions == 0) {
                throw new IllegalArgumentException("Supplied Array has no dimensions.");
            }
            int elementCount = cumElements[0] * elements[0];
            switch (varType) {
                case 2:
                case 11:
                case 18:
                    sourceArray = dataPointer.getShortArray(0L, elementCount);
                    break;
                case 3:
                case 10:
                case 19:
                case 22:
                case 23:
                    sourceArray = dataPointer.getIntArray(0L, elementCount);
                    break;
                case 4:
                    sourceArray = dataPointer.getFloatArray(0L, elementCount);
                    break;
                case 5:
                case 7:
                    sourceArray = dataPointer.getDoubleArray(0L, elementCount);
                    break;
                case 6:
                case 9:
                case 13:
                case 14:
                case 15:
                case 20:
                case 21:
                case 24:
                case 25:
                case 26:
                case 27:
                case 28:
                case 29:
                case 30:
                case 31:
                case 32:
                case 33:
                case 34:
                case 35:
                case 36:
                default:
                    throw new IllegalStateException("Type not supported: " + varType);
                case 8:
                    sourceArray = dataPointer.getPointerArray(0L, elementCount);
                    break;
                case 12:
                    Variant.VARIANT variant = new Variant.VARIANT(dataPointer);
                    sourceArray = variant.toArray(elementCount);
                    break;
                case 16:
                case 17:
                    sourceArray = dataPointer.getByteArray(0L, elementCount);
                    break;
            }
            Object targetArray = Array.newInstance((Class<?>) Object.class, elements);
            toPrimitiveArray(sourceArray, targetArray, elements, cumElements, varType, new int[0]);
            sa.unaccessData();
            if (destruct) {
                sa.destroy();
            }
            return targetArray;
        } catch (Throwable th) {
            sa.unaccessData();
            if (destruct) {
                sa.destroy();
            }
            throw th;
        }
    }

    private static void toPrimitiveArray(Object dataArray, Object targetArray, int[] elements, int[] cumElements, int varType, int[] currentIdx) {
        int dimIdx = currentIdx.length;
        int[] subIdx = new int[currentIdx.length + 1];
        System.arraycopy(currentIdx, 0, subIdx, 0, dimIdx);
        for (int i = 0; i < elements[dimIdx]; i++) {
            subIdx[dimIdx] = i;
            if (dimIdx == elements.length - 1) {
                int offset = 0;
                for (int j = 0; j < dimIdx; j++) {
                    offset += cumElements[j] * currentIdx[j];
                }
                int offset2 = offset + subIdx[dimIdx];
                int targetPos = subIdx[dimIdx];
                switch (varType) {
                    case 2:
                    case 18:
                        Array.set(targetArray, targetPos, Short.valueOf(Array.getShort(dataArray, offset2)));
                        break;
                    case 3:
                    case 19:
                    case 22:
                    case 23:
                        Array.set(targetArray, targetPos, Integer.valueOf(Array.getInt(dataArray, offset2)));
                        break;
                    case 4:
                        Array.set(targetArray, targetPos, Float.valueOf(Array.getFloat(dataArray, offset2)));
                        break;
                    case 5:
                        Array.set(targetArray, targetPos, Double.valueOf(Array.getDouble(dataArray, offset2)));
                        break;
                    case 6:
                    case 9:
                    case 13:
                    case 14:
                    case 15:
                    case 20:
                    case 21:
                    case 24:
                    case 25:
                    case 26:
                    case 27:
                    case 28:
                    case 29:
                    case 30:
                    case 31:
                    case 32:
                    case 33:
                    case 34:
                    case 35:
                    case 36:
                    default:
                        throw new IllegalStateException("Type not supported: " + varType);
                    case 7:
                        Array.set(targetArray, targetPos, new OaIdl.DATE(Array.getDouble(dataArray, offset2)).getAsJavaDate());
                        break;
                    case 8:
                        Array.set(targetArray, targetPos, new WTypes.BSTR((Pointer) Array.get(dataArray, offset2)).getValue());
                        break;
                    case 10:
                        Array.set(targetArray, targetPos, new WinDef.SCODE(Array.getInt(dataArray, offset2)));
                        break;
                    case 11:
                        Array.set(targetArray, targetPos, Boolean.valueOf(Array.getShort(dataArray, offset2) != 0));
                        break;
                    case 12:
                        Variant.VARIANT holder = (Variant.VARIANT) Array.get(dataArray, offset2);
                        switch (holder.getVarType().intValue()) {
                            case 0:
                            case 1:
                                Array.set(targetArray, targetPos, null);
                                break;
                            case 2:
                            case 18:
                                Array.set(targetArray, targetPos, Short.valueOf(holder.shortValue()));
                                break;
                            case 3:
                            case 19:
                            case 22:
                            case 23:
                                Array.set(targetArray, targetPos, Integer.valueOf(holder.intValue()));
                                break;
                            case 4:
                                Array.set(targetArray, targetPos, Float.valueOf(holder.floatValue()));
                                break;
                            case 5:
                                Array.set(targetArray, targetPos, Double.valueOf(holder.doubleValue()));
                                break;
                            case 6:
                            case 9:
                            case 12:
                            case 13:
                            case 14:
                            case 15:
                            case 20:
                            case 21:
                            default:
                                throw new IllegalStateException("Type not supported: " + holder.getVarType().intValue());
                            case 7:
                                Array.set(targetArray, targetPos, holder.dateValue());
                                break;
                            case 8:
                                Array.set(targetArray, targetPos, holder.stringValue());
                                break;
                            case 10:
                                Array.set(targetArray, targetPos, new WinDef.SCODE(holder.intValue()));
                                break;
                            case 11:
                                Array.set(targetArray, targetPos, Boolean.valueOf(holder.booleanValue()));
                                break;
                            case 16:
                            case 17:
                                Array.set(targetArray, targetPos, Byte.valueOf(holder.byteValue()));
                                break;
                        }
                        break;
                    case 16:
                    case 17:
                        Array.set(targetArray, targetPos, Byte.valueOf(Array.getByte(dataArray, offset2)));
                        break;
                }
            } else {
                toPrimitiveArray(dataArray, Array.get(targetArray, i), elements, cumElements, varType, subIdx);
            }
        }
    }
}
