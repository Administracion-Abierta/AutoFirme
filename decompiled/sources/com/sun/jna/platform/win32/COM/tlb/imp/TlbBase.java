package com.sun.jna.platform.win32.COM.tlb.imp;

import com.sun.jna.platform.win32.COM.TypeInfoUtil;
import com.sun.jna.platform.win32.COM.TypeLibUtil;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/* JADX INFO: loaded from: autofirma.jar:com/sun/jna/platform/win32/COM/tlb/imp/TlbBase.class */
public abstract class TlbBase {
    public static final String CR = "\n";
    public static final String CRCR = "\n\n";
    public static final String TAB = "\t";
    public static final String TABTAB = "\t\t";
    protected TypeLibUtil typeLibUtil;
    protected TypeInfoUtil typeInfoUtil;
    protected int index;
    protected StringBuffer templateBuffer;
    protected StringBuffer classBuffer;
    protected String content;
    protected String filename;
    protected String name;
    public static String[] IUNKNOWN_METHODS = {"QueryInterface", "AddRef", "Release"};
    public static String[] IDISPATCH_METHODS = {"GetTypeInfoCount", "GetTypeInfo", "GetIDsOfNames", "Invoke"};
    protected String bindingMode;

    protected abstract String getClassTemplate();

    public TlbBase(int index, TypeLibUtil typeLibUtil, TypeInfoUtil typeInfoUtil) {
        this(index, typeLibUtil, typeInfoUtil, TlbConst.BINDING_MODE_DISPID);
    }

    public TlbBase(int index, TypeLibUtil typeLibUtil, TypeInfoUtil typeInfoUtil, String bindingMode) {
        this.content = "";
        this.filename = "DefaultFilename";
        this.name = "DefaultName";
        this.bindingMode = TlbConst.BINDING_MODE_DISPID;
        this.index = index;
        this.typeLibUtil = typeLibUtil;
        this.typeInfoUtil = typeInfoUtil;
        this.bindingMode = bindingMode;
        String filename = getClassTemplate();
        try {
            readTemplateFile(filename);
            this.classBuffer = this.templateBuffer;
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void logError(String msg) {
        log("ERROR", msg);
    }

    public void logInfo(String msg) {
        log("INFO", msg);
    }

    public StringBuffer getClassBuffer() {
        return this.classBuffer;
    }

    public void createContent(String content) {
        replaceVariable("content", content);
    }

    public void setFilename(String filename) {
        if (!filename.endsWith("java")) {
            filename = filename + ".java";
        }
        this.filename = filename;
    }

    public String getFilename() {
        return this.filename;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    protected void log(String level, String msg) {
        String _msg = level + " " + getTime() + " : " + msg;
        System.out.println(_msg);
    }

    private String getTime() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
        return sdf.format(new Date());
    }

    protected void readTemplateFile(String filename) throws IOException {
        this.templateBuffer = new StringBuffer();
        BufferedReader reader = null;
        try {
            InputStream is = getClass().getClassLoader().getResourceAsStream(filename);
            reader = new BufferedReader(new InputStreamReader(is));
            while (true) {
                String line = reader.readLine();
                if (line == null) {
                    break;
                } else {
                    this.templateBuffer.append(line + "\n");
                }
            }
            if (reader != null) {
                reader.close();
            }
        } catch (Throwable th) {
            if (reader != null) {
                reader.close();
            }
            throw th;
        }
    }

    protected void replaceVariable(String name, String value) {
        String result;
        if (value == null) {
            value = "";
        }
        Pattern pattern = Pattern.compile("\\$\\{" + name + "\\}");
        Matcher matcher = pattern.matcher(this.classBuffer);
        String replacement = value;
        String strReplaceAll = "";
        while (true) {
            result = strReplaceAll;
            if (!matcher.find()) {
                break;
            } else {
                strReplaceAll = matcher.replaceAll(replacement);
            }
        }
        if (result.length() > 0) {
            this.classBuffer = new StringBuffer(result);
        }
    }

    protected void createPackageName(String packagename) {
        replaceVariable("packagename", packagename);
    }

    protected void createClassName(String name) {
        replaceVariable("classname", name);
    }

    protected boolean isReservedMethod(String method) {
        for (int i = 0; i < IUNKNOWN_METHODS.length; i++) {
            if (IUNKNOWN_METHODS[i].equalsIgnoreCase(method)) {
                return true;
            }
        }
        for (int i2 = 0; i2 < IDISPATCH_METHODS.length; i2++) {
            if (IDISPATCH_METHODS[i2].equalsIgnoreCase(method)) {
                return true;
            }
        }
        return false;
    }

    protected boolean isVTableMode() {
        if (this.bindingMode.equalsIgnoreCase(TlbConst.BINDING_MODE_VTABLE)) {
            return true;
        }
        return false;
    }

    protected boolean isDispIdMode() {
        if (this.bindingMode.equalsIgnoreCase(TlbConst.BINDING_MODE_DISPID)) {
            return true;
        }
        return false;
    }
}
