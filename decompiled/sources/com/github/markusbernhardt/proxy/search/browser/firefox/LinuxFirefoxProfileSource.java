package com.github.markusbernhardt.proxy.search.browser.firefox;

import com.github.markusbernhardt.proxy.util.PlatformUtil;
import java.io.File;

/* JADX INFO: loaded from: autofirma.jar:com/github/markusbernhardt/proxy/search/browser/firefox/LinuxFirefoxProfileSource.class */
class LinuxFirefoxProfileSource implements FirefoxProfileSource {
    LinuxFirefoxProfileSource() {
    }

    @Override // com.github.markusbernhardt.proxy.search.browser.firefox.FirefoxProfileSource
    public File getProfilesIni() {
        File userDir = new File(PlatformUtil.getUserHomeDir());
        return new File(userDir, ".mozilla" + File.separator + "firefox" + File.separator + "profiles.ini");
    }
}
