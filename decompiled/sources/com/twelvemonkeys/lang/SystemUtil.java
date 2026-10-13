package com.twelvemonkeys.lang;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Array;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/* JADX INFO: loaded from: autofirma.jar:com/twelvemonkeys/lang/SystemUtil.class */
public final class SystemUtil {
    public static String XML_PROPERTIES = ".xml";
    public static String STD_PROPERTIES = ".properties";

    private SystemUtil() {
    }

    /* JADX INFO: loaded from: autofirma.jar:com/twelvemonkeys/lang/SystemUtil$XMLPropertiesInputStream.class */
    private static class XMLPropertiesInputStream extends FilterInputStream {
        public XMLPropertiesInputStream(InputStream inputStream) {
            super(inputStream);
        }
    }

    private static InputStream getResourceAsStream(ClassLoader classLoader, String str, boolean z) {
        InputStream resourceAsStream;
        if (!z) {
            resourceAsStream = classLoader.getResourceAsStream(str);
            if (resourceAsStream != null && str.endsWith(XML_PROPERTIES)) {
                resourceAsStream = new XMLPropertiesInputStream(resourceAsStream);
            }
        } else {
            resourceAsStream = classLoader.getResourceAsStream(str + STD_PROPERTIES);
            if (resourceAsStream == null) {
                resourceAsStream = classLoader.getResourceAsStream(str + XML_PROPERTIES);
                if (resourceAsStream != null) {
                    resourceAsStream = new XMLPropertiesInputStream(resourceAsStream);
                }
            }
        }
        return resourceAsStream;
    }

    private static InputStream getFileAsStream(String str, boolean z) {
        InputStream xMLPropertiesInputStream = null;
        try {
            if (!z) {
                File file = new File(str);
                if (file.exists()) {
                    xMLPropertiesInputStream = new FileInputStream(file);
                    if (str.endsWith(XML_PROPERTIES)) {
                        xMLPropertiesInputStream = new XMLPropertiesInputStream(xMLPropertiesInputStream);
                    }
                }
            } else {
                File file2 = new File(str + STD_PROPERTIES);
                if (file2.exists()) {
                    xMLPropertiesInputStream = new FileInputStream(file2);
                } else {
                    File file3 = new File(str + XML_PROPERTIES);
                    if (file3.exists()) {
                        xMLPropertiesInputStream = new XMLPropertiesInputStream(new FileInputStream(file3));
                    }
                }
            }
        } catch (FileNotFoundException e) {
        }
        return xMLPropertiesInputStream;
    }

    /* JADX WARN: Code duplicated, block: B:17:0x0040  */
    /* JADX WARN: Code duplicated, block: B:20:0x0051  */
    /* JADX WARN: Code duplicated, block: B:23:0x005f  */
    /* JADX WARN: Code duplicated, block: B:25:0x0063  */
    /* JADX WARN: Code duplicated, block: B:27:0x0087  */
    public static Properties loadProperties(Class cls, String str) throws IOException {
        InputStream resourceAsStream;
        InputStream inputStream;
        InputStream fileAsStream;
        String strReplace = !StringUtil.isEmpty(str) ? str : cls.getName().replace('.', '/');
        boolean z = str == null || str.indexOf(46) < 0;
        if (cls != null) {
            InputStream resourceAsStream2 = getResourceAsStream(cls.getClassLoader(), strReplace, z);
            inputStream = resourceAsStream2;
            if (resourceAsStream2 == null) {
                resourceAsStream = getResourceAsStream(ClassLoader.getSystemClassLoader(), strReplace, z);
                inputStream = resourceAsStream;
                if (resourceAsStream == null) {
                    fileAsStream = getFileAsStream(strReplace, z);
                    inputStream = fileAsStream;
                    if (fileAsStream == null) {
                        if (z) {
                            throw new FileNotFoundException(strReplace + ".properties or " + strReplace + ".xml");
                        }
                        throw new FileNotFoundException(strReplace);
                    }
                }
            }
        } else {
            resourceAsStream = getResourceAsStream(ClassLoader.getSystemClassLoader(), strReplace, z);
            inputStream = resourceAsStream;
            if (resourceAsStream == null) {
                fileAsStream = getFileAsStream(strReplace, z);
                inputStream = fileAsStream;
                if (fileAsStream == null) {
                    if (z) {
                        throw new FileNotFoundException(strReplace + ".properties or " + strReplace + ".xml");
                    }
                    throw new FileNotFoundException(strReplace);
                }
            }
        }
        try {
            return loadProperties(inputStream);
        } finally {
            try {
                inputStream.close();
            } catch (IOException e) {
            }
        }
    }

    public static Properties loadProperties(Class cls) throws IOException {
        return loadProperties(cls, null);
    }

    public static Properties loadProperties(String str) throws IOException {
        return loadProperties(null, str);
    }

    private static Properties loadProperties(InputStream inputStream) throws IOException {
        if (inputStream == null) {
            throw new IllegalArgumentException("InputStream == null!");
        }
        Properties properties = new Properties();
        properties.load(inputStream);
        return properties;
    }

    /* JADX WARN: Multi-variable type inference failed */
    public static Object clone(Cloneable cloneable) throws CloneNotSupportedException {
        Class<? super Object> superclass;
        if (cloneable == 0) {
            return null;
        }
        if (cloneable instanceof Object[]) {
            return ((Object[]) cloneable).clone();
        }
        if (cloneable.getClass().isArray()) {
            int length = Array.getLength(cloneable);
            Object objNewInstance = Array.newInstance(cloneable.getClass().getComponentType(), length);
            System.arraycopy(cloneable, 0, objNewInstance, 0, length);
            return objNewInstance;
        }
        try {
            Method declaredMethod = null;
            Class<?> cls = cloneable.getClass();
            do {
                try {
                    declaredMethod = cls.getDeclaredMethod("clone", new Class[0]);
                    break;
                } catch (NoSuchMethodException e) {
                    superclass = cls.getSuperclass();
                    cls = superclass;
                }
            } while (superclass != null);
            if (declaredMethod == null) {
                throw new CloneNotSupportedException(cloneable.getClass().getName());
            }
            if (!declaredMethod.isAccessible()) {
                declaredMethod.setAccessible(true);
            }
            return declaredMethod.invoke(cloneable, new Object[0]);
        } catch (IllegalAccessException e2) {
            throw new CloneNotSupportedException(cloneable.getClass().getName());
        } catch (SecurityException e3) {
            CloneNotSupportedException cloneNotSupportedException = new CloneNotSupportedException(cloneable.getClass().getName());
            cloneNotSupportedException.initCause(e3);
            throw cloneNotSupportedException;
        } catch (InvocationTargetException e4) {
            if (e4.getTargetException() instanceof CloneNotSupportedException) {
                throw ((CloneNotSupportedException) e4.getTargetException());
            }
            if (e4.getTargetException() instanceof RuntimeException) {
                throw ((RuntimeException) e4.getTargetException());
            }
            if (e4.getTargetException() instanceof Error) {
                throw ((Error) e4.getTargetException());
            }
            throw new CloneNotSupportedException(cloneable.getClass().getName());
        }
    }

    /* JADX WARN: Multi-variable type inference failed */
    /* JADX WARN: Type inference failed for: r0v10, types: [int[][], java.lang.Cloneable] */
    /* JADX WARN: Type inference failed for: r0v3, types: [int[], java.lang.Cloneable] */
    public static void main(String[] strArr) throws CloneNotSupportedException {
        System.out.println("clone: " + ((String[]) strArr.clone()).length + " (" + strArr.length + ")");
        System.out.println("copy: " + ((String[]) clone(strArr)).length + " (" + strArr.length + ")");
        ?? r0 = {1, 2, 3};
        System.out.println("Copies: " + ((int[]) clone(r0)).length + " (" + r0.length + ")");
        ?? r1 = {new int[]{1}, new int[]{2, 3}, new int[]{4, 5, 6}};
        int[][] iArr = (int[][]) clone(r1);
        System.out.println("Copies: " + iArr.length + " (" + r1.length + ")");
        System.out.println("Copies0: " + iArr[0].length + " (" + r1[0].length + ")");
        System.out.println("Copies1: " + iArr[1].length + " (" + r1[1].length + ")");
        System.out.println("Copies2: " + iArr[2].length + " (" + r1[2].length + ")");
        HashMap map = new HashMap();
        for (Object[] objArr : strArr) {
            map.put(objArr, objArr);
        }
        Map map2 = (Map) clone(map);
        System.out.println("Map : " + map);
        System.out.println("Copy: " + map2);
        Cloneable cloneable = new Cloneable() { // from class: com.twelvemonkeys.lang.SystemUtil.1
        };
        Cloneable cloneable2 = (Cloneable) clone(cloneable);
        System.out.println("cloneable: " + cloneable);
        System.out.println("clone: " + cloneable2);
        AccessController.doPrivileged(new PrivilegedAction<Void>() { // from class: com.twelvemonkeys.lang.SystemUtil.2
            /* JADX WARN: Can't rename method to resolve collision */
            @Override // java.security.PrivilegedAction
            public Void run() {
                return null;
            }
        }, AccessController.getContext());
    }

    public static boolean isClassAvailable(String str) {
        return isClassAvailable(str, (ClassLoader) null);
    }

    public static boolean isClassAvailable(String str, Class cls) {
        return isClassAvailable(str, cls != null ? cls.getClassLoader() : null);
    }

    private static boolean isClassAvailable(String str, ClassLoader classLoader) {
        try {
            getClass(str, true, classLoader);
            return true;
        } catch (ClassNotFoundException | LinkageError | SecurityException e) {
            return false;
        }
    }

    public static boolean isFieldAvailable(String str, String str2) {
        return isFieldAvailable(str, str2, (ClassLoader) null);
    }

    public static boolean isFieldAvailable(String str, String str2, Class cls) {
        return isFieldAvailable(str, str2, cls != null ? cls.getClassLoader() : null);
    }

    private static boolean isFieldAvailable(String str, String str2, ClassLoader classLoader) {
        try {
            if (getClass(str, false, classLoader).getField(str2) != null) {
                return true;
            }
            return false;
        } catch (ClassNotFoundException e) {
            return false;
        } catch (LinkageError e2) {
            return false;
        } catch (NoSuchFieldException e3) {
            return false;
        }
    }

    public static boolean isMethodAvailable(String str, String str2) {
        return isMethodAvailable(str, str2, (Class[]) null, (ClassLoader) null);
    }

    public static boolean isMethodAvailable(String str, String str2, Class[] clsArr) {
        return isMethodAvailable(str, str2, clsArr, (ClassLoader) null);
    }

    public static boolean isMethodAvailable(String str, String str2, Class[] clsArr, Class cls) {
        return isMethodAvailable(str, str2, clsArr, cls != null ? cls.getClassLoader() : null);
    }

    private static boolean isMethodAvailable(String str, String str2, Class[] clsArr, ClassLoader classLoader) {
        try {
            if (getClass(str, false, classLoader).getMethod(str2, clsArr) != null) {
                return true;
            }
            return false;
        } catch (ClassNotFoundException e) {
            return false;
        } catch (LinkageError e2) {
            return false;
        } catch (NoSuchMethodException e3) {
            return false;
        }
    }

    private static Class getClass(String str, boolean z, ClassLoader classLoader) throws ClassNotFoundException {
        return Class.forName(str, z, classLoader != null ? classLoader : Thread.currentThread().getContextClassLoader());
    }
}
