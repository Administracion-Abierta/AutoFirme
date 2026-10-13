package com.github.markusbernhardt.proxy.search.browser.firefox;

import com.sun.jna.platform.win32.Shell32Util;
import java.io.File;
import java.io.IOException;

/* JADX INFO: loaded from: autofirma.jar:com/github/markusbernhardt/proxy/search/browser/firefox/WinFirefoxProfileSource.class */
class WinFirefoxProfileSource implements FirefoxProfileSource {
    private String getAppFolder() {
        return Shell32Util.getFolderPath(26);
    }

    @Override // com.github.markusbernhardt.proxy.search.browser.firefox.FirefoxProfileSource
    public File getProfilesIni() throws IOException {
        File appDataDir = new File(getAppFolder());
        return new File(appDataDir, "Mozilla" + File.separator + "Firefox" + File.separator + "profiles.ini");
    }
}
