package com.github.markusbernhardt.proxy.search.desktop.kde;

import com.github.markusbernhardt.proxy.util.Logger;
import com.github.markusbernhardt.proxy.util.PlatformUtil;
import com.sun.jna.platform.win32.COM.tlb.imp.TlbConst;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Properties;

/* JADX INFO: loaded from: autofirma.jar:com/github/markusbernhardt/proxy/search/desktop/kde/KdeSettingsParser.class */
public class KdeSettingsParser {
    private File settingsFile;

    public KdeSettingsParser() {
        this(null);
    }

    public KdeSettingsParser(File settingsFile) {
        this.settingsFile = settingsFile;
    }

    public Properties parseSettings() throws IOException {
        if (this.settingsFile == null) {
            this.settingsFile = findSettingsFile();
        }
        if (this.settingsFile == null) {
            return null;
        }
        BufferedReader fin = new BufferedReader(new InputStreamReader(new FileInputStream(this.settingsFile)));
        Properties result = new Properties();
        try {
            String line = fin.readLine();
            while (line != null && !"[Proxy Settings]".equals(line.trim())) {
                line = fin.readLine();
            }
            if (line != null) {
                for (String line2 = ""; line2 != null && !line2.trim().startsWith("["); line2 = fin.readLine()) {
                    String line3 = line2.trim();
                    int index = line3.indexOf(61);
                    if (index > 0) {
                        String key = line3.substring(0, index).trim();
                        String value = line3.substring(index + 1).trim();
                        result.setProperty(key, value);
                    }
                }
                fin.close();
                return result;
            }
            fin.close();
            return result;
        } catch (Throwable th) {
            fin.close();
            throw th;
        }
    }

    private File findSettingsFile() {
        File userDir = new File(PlatformUtil.getUserHomeDir());
        if (TlbConst.TYPELIB_MINOR_VERSION_WORD.equals(System.getenv("KDE_SESSION_VERSION"))) {
            this.settingsFile = findSettingsFile(new File(userDir, ".kde4" + File.separator + "share" + File.separator + "config" + File.separator + "kioslaverc"));
        }
        if (this.settingsFile == null) {
            return findSettingsFile(new File(userDir, ".kde" + File.separator + "share" + File.separator + "config" + File.separator + "kioslaverc"));
        }
        return this.settingsFile;
    }

    private File findSettingsFile(File settingsFile) {
        Logger.log(getClass(), Logger.LogLevel.TRACE, "Searching Kde settings in {}", settingsFile);
        if (!settingsFile.exists()) {
            Logger.log(getClass(), Logger.LogLevel.DEBUG, "Settings not found", new Object[0]);
            return null;
        }
        Logger.log(getClass(), Logger.LogLevel.TRACE, "Settings found", new Object[0]);
        return settingsFile;
    }
}
