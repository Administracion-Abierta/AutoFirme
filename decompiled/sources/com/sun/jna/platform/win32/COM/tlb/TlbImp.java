package com.sun.jna.platform.win32.COM.tlb;

import com.sun.jna.platform.win32.COM.TypeLibUtil;
import com.sun.jna.platform.win32.COM.tlb.imp.TlbBase;
import com.sun.jna.platform.win32.COM.tlb.imp.TlbCmdlineArgs;
import com.sun.jna.platform.win32.COM.tlb.imp.TlbCoClass;
import com.sun.jna.platform.win32.COM.tlb.imp.TlbConst;
import com.sun.jna.platform.win32.COM.tlb.imp.TlbDispInterface;
import com.sun.jna.platform.win32.COM.tlb.imp.TlbEnum;
import com.sun.jna.platform.win32.COM.tlb.imp.TlbInterface;
import com.sun.jna.platform.win32.OaIdl;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

/* JADX INFO: loaded from: autofirma.jar:com/sun/jna/platform/win32/COM/tlb/TlbImp.class */
public class TlbImp implements TlbConst {
    private TypeLibUtil typeLibUtil;
    private File comRootDir;
    private File outputDir;
    private TlbCmdlineArgs cmdlineArgs;

    public static void main(String[] args) {
        new TlbImp(args);
    }

    public TlbImp(String[] args) {
        this.cmdlineArgs = new TlbCmdlineArgs(args);
        if (this.cmdlineArgs.isTlbId()) {
            String clsid = this.cmdlineArgs.getRequiredParam(TlbConst.CMD_ARG_TYPELIB_ID);
            int majorVersion = this.cmdlineArgs.getIntParam(TlbConst.CMD_ARG_TYPELIB_MAJOR_VERSION);
            int minorVersion = this.cmdlineArgs.getIntParam(TlbConst.CMD_ARG_TYPELIB_MINOR_VERSION);
            this.typeLibUtil = new TypeLibUtil(clsid, majorVersion, minorVersion);
            startCOM2Java();
            return;
        }
        if (this.cmdlineArgs.isTlbFile()) {
            String file = this.cmdlineArgs.getRequiredParam(TlbConst.CMD_ARG_TYPELIB_FILE);
            this.typeLibUtil = new TypeLibUtil(file);
            startCOM2Java();
            return;
        }
        this.cmdlineArgs.showCmdHelp();
    }

    public void startCOM2Java() {
        try {
            createDir();
            String bindingMode = this.cmdlineArgs.getBindingMode();
            int typeInfoCount = this.typeLibUtil.getTypeInfoCount();
            for (int i = 0; i < typeInfoCount; i++) {
                OaIdl.TYPEKIND typekind = this.typeLibUtil.getTypeInfoType(i);
                if (typekind.value == 0) {
                    createCOMEnum(i, getPackageName(), this.typeLibUtil);
                } else if (typekind.value == 1) {
                    logInfo("'TKIND_RECORD' objects are currently not supported!");
                } else if (typekind.value == 2) {
                    logInfo("'TKIND_MODULE' objects are currently not supported!");
                } else if (typekind.value == 3) {
                    createCOMInterface(i, getPackageName(), this.typeLibUtil);
                } else if (typekind.value == 4) {
                    createCOMDispInterface(i, getPackageName(), this.typeLibUtil);
                } else if (typekind.value == 5) {
                    createCOMCoClass(i, getPackageName(), this.typeLibUtil, bindingMode);
                } else if (typekind.value == 6) {
                    logInfo("'TKIND_ALIAS' objects are currently not supported!");
                } else if (typekind.value == 7) {
                    logInfo("'TKIND_UNION' objects are currently not supported!");
                }
            }
            logInfo(typeInfoCount + " files sucessfully written to: " + this.comRootDir.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void createDir() throws FileNotFoundException {
        String _outputDir = this.cmdlineArgs.getParam(TlbConst.CMD_ARG_OUTPUT_DIR);
        String path = "_jnaCOM_" + System.currentTimeMillis() + "\\myPackage\\" + this.typeLibUtil.getName().toLowerCase() + "\\";
        if (_outputDir != null) {
            this.comRootDir = new File(_outputDir + "\\" + path);
        } else {
            String tmp = System.getProperty("java.io.tmpdir");
            this.comRootDir = new File(tmp + "\\" + path);
        }
        if (this.comRootDir.exists()) {
            this.comRootDir.delete();
        }
        if (this.comRootDir.mkdirs()) {
            logInfo("Output directory sucessfully created.");
            return;
        }
        throw new FileNotFoundException("Output directory NOT sucessfully created to: " + this.comRootDir.toString());
    }

    private String getPackageName() {
        return "myPackage." + this.typeLibUtil.getName().toLowerCase();
    }

    private void writeTextFile(String filename, String str) throws IOException {
        String file = this.comRootDir + File.separator + filename;
        BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(file));
        bos.write(str.getBytes());
        bos.close();
    }

    private void writeTlbClass(TlbBase tlbBase) throws IOException {
        StringBuffer classBuffer = tlbBase.getClassBuffer();
        writeTextFile(tlbBase.getFilename(), classBuffer.toString());
    }

    private void createCOMEnum(int index, String packagename, TypeLibUtil typeLibUtil) throws IOException {
        TlbEnum tlbEnum = new TlbEnum(index, packagename, typeLibUtil);
        writeTlbClass(tlbEnum);
    }

    private void createCOMInterface(int index, String packagename, TypeLibUtil typeLibUtil) throws IOException {
        TlbInterface tlbInterface = new TlbInterface(index, packagename, typeLibUtil);
        writeTlbClass(tlbInterface);
    }

    private void createCOMDispInterface(int index, String packagename, TypeLibUtil typeLibUtil) throws IOException {
        TlbDispInterface tlbDispatch = new TlbDispInterface(index, packagename, typeLibUtil);
        writeTlbClass(tlbDispatch);
    }

    private void createCOMCoClass(int index, String packagename, TypeLibUtil typeLibUtil, String bindingMode) throws IOException {
        TlbCoClass tlbCoClass = new TlbCoClass(index, getPackageName(), typeLibUtil, bindingMode);
        writeTlbClass(tlbCoClass);
    }

    public static void logInfo(String msg) {
        System.out.println(msg);
    }
}
