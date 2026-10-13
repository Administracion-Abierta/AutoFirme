package com.github.markusbernhardt.proxy.search.browser.firefox;

import com.github.markusbernhardt.proxy.util.Logger;
import es.gob.afirma.standalone.configurator.common.PreferencesManager;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.stream.Collectors;
import org.apache.pdfbox.contentstream.operator.OperatorName;
import org.ini4j.Ini;
import org.ini4j.Profile;

/* JADX INFO: loaded from: autofirma.jar:com/github/markusbernhardt/proxy/search/browser/firefox/FirefoxSettingParser.class */
class FirefoxSettingParser {
    public Properties parseSettings(FirefoxProfileSource source) throws IOException {
        File settingsFile = getSettingsFile(source);
        Properties result = new Properties();
        if (settingsFile == null) {
            return result;
        }
        BufferedReader fin = new BufferedReader(new InputStreamReader(new FileInputStream(settingsFile)));
        Throwable th = null;
        while (true) {
            try {
                try {
                    String line = fin.readLine();
                    if (line == null) {
                        break;
                    }
                    String line2 = line.trim();
                    if (line2.startsWith("user_pref(\"network.proxy")) {
                        String line3 = line2.substring(10, line2.length() - 2);
                        int index = line3.indexOf(",");
                        String key = removeDoubleQuotes(line3.substring(0, index).trim());
                        String value = removeDoubleQuotes(line3.substring(index + 1).trim());
                        result.put(key, value);
                    }
                } catch (Throwable th2) {
                    if (fin != null) {
                        if (th != null) {
                            try {
                                fin.close();
                            } catch (Throwable th3) {
                                th.addSuppressed(th3);
                            }
                        } else {
                            fin.close();
                        }
                    }
                    throw th2;
                }
            } catch (Throwable th4) {
                th = th4;
                throw th4;
            }
        }
        if (fin != null) {
            if (0 != 0) {
                try {
                    fin.close();
                } catch (Throwable th5) {
                    th.addSuppressed(th5);
                }
            } else {
                fin.close();
            }
        }
        return result;
    }

    private String removeDoubleQuotes(String string) {
        if (string.startsWith(OperatorName.SHOW_TEXT_LINE_AND_SPACE)) {
            string = string.substring(1);
        }
        if (string.endsWith(OperatorName.SHOW_TEXT_LINE_AND_SPACE)) {
            string = string.substring(0, string.length() - 1);
        }
        return string;
    }

    protected File getSettingsFile(FirefoxProfileSource source) throws IOException {
        File profilesIniFile = source.getProfilesIni();
        if (profilesIniFile.exists()) {
            Ini profilesIni = new Ini(profilesIniFile);
            List<String> keysFF67 = (List) profilesIni.keySet().stream().filter(s -> {
                return s.startsWith("Install");
            }).collect(Collectors.toList());
            if (!keysFF67.isEmpty()) {
                Logger.log(getClass(), Logger.LogLevel.DEBUG, "Firefox settings for FF67+ detected.", new Object[0]);
                for (String keyFF67 : keysFF67) {
                    Logger.log(getClass(), Logger.LogLevel.DEBUG, "Current FF67+ section key is: {}", keysFF67);
                    Profile.Section section = profilesIni.get(keyFF67);
                    if ("1".equals(section.get("Locked"))) {
                        File profileFolder = new File(profilesIniFile.getParentFile().getAbsolutePath(), (String) section.get("Default"));
                        Logger.log(getClass(), Logger.LogLevel.DEBUG, "Firefox settings folder is {}", profileFolder);
                        File settingsFile = new File(profileFolder, "prefs.js");
                        return settingsFile;
                    }
                }
            } else {
                for (Map.Entry<String, Profile.Section> entry : profilesIni.entrySet()) {
                    Logger.log(getClass(), Logger.LogLevel.TRACE, "Current entry, key: {}, value: {}", entry.getKey(), entry.getValue());
                    if (PreferencesManager.VALUE_KEYSTORE_DEFAULT.equals(entry.getValue().get("Name")) && "1".equals(entry.getValue().get("IsRelative"))) {
                        File profileFolder2 = new File(profilesIniFile.getParentFile().getAbsolutePath(), (String) entry.getValue().get("Path"));
                        Logger.log(getClass(), Logger.LogLevel.DEBUG, "Firefox settings folder is {}", profileFolder2);
                        File settingsFile2 = new File(profileFolder2, "prefs.js");
                        return settingsFile2;
                    }
                }
            }
        }
        Logger.log(getClass(), Logger.LogLevel.DEBUG, "Firefox settings folder not found!", new Object[0]);
        return null;
    }
}
