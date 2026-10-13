package com.github.markusbernhardt.proxy.search.browser.firefox;

import com.github.markusbernhardt.proxy.util.PlatformUtil;
import java.io.File;

/* JADX INFO: loaded from: autofirma.jar:com/github/markusbernhardt/proxy/search/browser/firefox/OsxFirefoxProfileSource.class */
class OsxFirefoxProfileSource implements FirefoxProfileSource {
    OsxFirefoxProfileSource() {
    }

    @Override // com.github.markusbernhardt.proxy.search.browser.firefox.FirefoxProfileSource
    public File getProfilesIni() {
        File userDir = new File(PlatformUtil.getUserHomeDir());
        return new File(userDir, "Library" + File.separator + "Application Support" + File.separator + "Firefox" + File.separator + "profiles.ini");
    }
}
