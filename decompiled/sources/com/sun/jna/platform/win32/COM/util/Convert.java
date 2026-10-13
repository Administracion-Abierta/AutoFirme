package com.sun.jna.platform.win32.COM.util;

import com.sun.jna.platform.win32.COM.Dispatch;
import com.sun.jna.platform.win32.Ddeml;
import com.sun.jna.platform.win32.OaIdl;
import com.sun.jna.platform.win32.OleAuto;
import com.sun.jna.platform.win32.Variant;
import com.sun.jna.platform.win32.WTypes;
import com.sun.jna.platform.win32.WinDef;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Proxy;
import java.util.Date;

/* JADX INFO: loaded from: autofirma.jar:com/sun/jna/platform/win32/COM/util/Convert.class */
class Convert {
    Convert() {
    }

    public static Variant.VARIANT toVariant(Object value) {
        if (value instanceof Variant.VARIANT) {
            return (Variant.VARIANT) value;
        }
        if (value instanceof Byte) {
            return new Variant.VARIANT(((Byte) value).byteValue());
        }
        if (value instanceof Character) {
            return new Variant.VARIANT(((Character) value).charValue());
        }
        if (value instanceof Short) {
            return new Variant.VARIANT(((Short) value).shortValue());
        }
        if (value instanceof Integer) {
            return new Variant.VARIANT(((Integer) value).intValue());
        }
        if (value instanceof Long) {
            return new Variant.VARIANT(((Long) value).longValue());
        }
        if (value instanceof Float) {
            return new Variant.VARIANT(((Float) value).floatValue());
        }
        if (value instanceof Double) {
            return new Variant.VARIANT(((Double) value).doubleValue());
        }
        if (value instanceof String) {
            return new Variant.VARIANT((String) value);
        }
        if (value instanceof Boolean) {
            return new Variant.VARIANT(((Boolean) value).booleanValue());
        }
        if (value instanceof Dispatch) {
            return new Variant.VARIANT((Dispatch) value);
        }
        if (value instanceof Date) {
            return new Variant.VARIANT((Date) value);
        }
        if (value instanceof Proxy) {
            InvocationHandler ih = Proxy.getInvocationHandler(value);
            ProxyObject pobj = (ProxyObject) ih;
            return new Variant.VARIANT(pobj.getRawDispatch());
        }
        if (value instanceof IComEnum) {
            IComEnum enm = (IComEnum) value;
            return new Variant.VARIANT(new WinDef.LONG(enm.getValue()));
        }
        Constructor<?> constructor = null;
        if (value != null) {
            for (Constructor<?> constructor2 : Variant.VARIANT.class.getConstructors()) {
                Class<?>[] parameters = constructor2.getParameterTypes();
                if (parameters.length == 1 && parameters[0].isAssignableFrom(value.getClass())) {
                    constructor = constructor2;
                }
            }
        }
        if (constructor != null) {
            try {
                return (Variant.VARIANT) constructor.newInstance(value);
            } catch (Exception ex) {
                throw new RuntimeException(ex);
            }
        }
        return null;
    }

    public static Object toJavaObject(Variant.VARIANT value, Class<?> targetClass, ObjectFactory factory, boolean addReference, boolean freeValue) {
        Object result;
        int varType = value != null ? value.getVarType().intValue() : 1;
        if (varType == 0 || varType == 1) {
            return null;
        }
        if (targetClass != null && !targetClass.isAssignableFrom(Object.class)) {
            if (targetClass.isAssignableFrom(value.getClass())) {
                return value;
            }
            Object vobj = value.getValue();
            if (vobj != null && targetClass.isAssignableFrom(vobj.getClass())) {
                return vobj;
            }
        }
        if (varType == 16396) {
            value = (Variant.VARIANT) value.getValue();
            varType = value.getVarType().intValue();
        }
        if (targetClass == null || targetClass.isAssignableFrom(Object.class)) {
            targetClass = null;
            switch (varType) {
                case 2:
                    targetClass = Short.class;
                    break;
                case 3:
                case 19:
                case 22:
                case 23:
                    targetClass = Integer.class;
                    break;
                case 4:
                    targetClass = Float.class;
                    break;
                case 5:
                    targetClass = Double.class;
                    break;
                case 6:
                    targetClass = OaIdl.CURRENCY.class;
                    break;
                case 7:
                    targetClass = Date.class;
                    break;
                case 8:
                    targetClass = String.class;
                    break;
                case 9:
                    targetClass = IDispatch.class;
                    break;
                case 10:
                    targetClass = WinDef.SCODE.class;
                    break;
                case 11:
                    targetClass = Boolean.class;
                    break;
                case 13:
                    targetClass = com.sun.jna.platform.win32.COM.IUnknown.class;
                    break;
                case 16:
                case 17:
                    targetClass = Byte.class;
                    break;
                case 18:
                    targetClass = Character.class;
                    break;
                case 20:
                case 21:
                    targetClass = Long.class;
                    break;
                case 36:
                default:
                    if ((varType & 8192) > 0) {
                        targetClass = OaIdl.SAFEARRAY.class;
                    }
                    break;
                case 16384:
                    targetClass = WinDef.PVOID.class;
                    break;
                case Ddeml.DMLERR_POSTMSG_FAILED /* 16396 */:
                    targetClass = Variant.class;
                    break;
                case Ddeml.DMLERR_SERVER_DIED /* 16398 */:
                    targetClass = OaIdl.DECIMAL.class;
                    break;
            }
        }
        if (Byte.class.equals(targetClass) || Byte.TYPE.equals(targetClass)) {
            result = Byte.valueOf(value.byteValue());
        } else if (Short.class.equals(targetClass) || Short.TYPE.equals(targetClass)) {
            result = Short.valueOf(value.shortValue());
        } else if (Character.class.equals(targetClass) || Character.TYPE.equals(targetClass)) {
            result = Character.valueOf((char) value.intValue());
        } else if (Integer.class.equals(targetClass) || Integer.TYPE.equals(targetClass)) {
            result = Integer.valueOf(value.intValue());
        } else if (Long.class.equals(targetClass) || Long.TYPE.equals(targetClass) || IComEnum.class.isAssignableFrom(targetClass)) {
            result = Long.valueOf(value.longValue());
        } else if (Float.class.equals(targetClass) || Float.TYPE.equals(targetClass)) {
            result = Float.valueOf(value.floatValue());
        } else if (Double.class.equals(targetClass) || Double.TYPE.equals(targetClass)) {
            result = Double.valueOf(value.doubleValue());
        } else if (Boolean.class.equals(targetClass) || Boolean.TYPE.equals(targetClass)) {
            result = Boolean.valueOf(value.booleanValue());
        } else if (Date.class.equals(targetClass)) {
            result = value.dateValue();
        } else if (String.class.equals(targetClass)) {
            result = value.stringValue();
        } else {
            result = value.getValue();
            if (result instanceof Dispatch) {
                Dispatch d = (Dispatch) result;
                if (targetClass != null && targetClass.isInterface()) {
                    Object proxy = factory.createProxy(targetClass, d);
                    if (!addReference) {
                        d.Release();
                    }
                    result = proxy;
                } else {
                    result = d;
                }
            }
        }
        if (IComEnum.class.isAssignableFrom(targetClass)) {
            result = targetClass.cast(toComEnum(targetClass, result));
        }
        if (freeValue) {
            free(value, result);
        }
        return result;
    }

    public static <T extends IComEnum> T toComEnum(Class<T> cls, Object obj) {
        try {
            for (IComEnum iComEnum : (IComEnum[]) cls.getMethod("values", new Class[0]).invoke(null, new Object[0])) {
                T t = (T) iComEnum;
                if (obj.equals(Long.valueOf(t.getValue()))) {
                    return t;
                }
            }
            return null;
        } catch (IllegalAccessException e) {
            return null;
        } catch (IllegalArgumentException e2) {
            return null;
        } catch (NoSuchMethodException e3) {
            return null;
        } catch (InvocationTargetException e4) {
            return null;
        }
    }

    public static void free(Variant.VARIANT variant, Class<?> javaType) {
        if ((javaType == null || !WTypes.BSTR.class.isAssignableFrom(javaType)) && variant != null && variant.getVarType().intValue() == 8) {
            Object value = variant.getValue();
            if (value instanceof WTypes.BSTR) {
                OleAuto.INSTANCE.SysFreeString((WTypes.BSTR) value);
            }
        }
    }

    public static void free(Variant.VARIANT variant, Object value) {
        free(variant, value == null ? null : value.getClass());
    }
}
