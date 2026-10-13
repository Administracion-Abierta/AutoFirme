package com.github.markusbernhardt.proxy.search.desktop;

import com.github.markusbernhardt.proxy.ProxySearchStrategy;
import com.github.markusbernhardt.proxy.search.desktop.gnome.GnomeProxySearchStrategy;
import com.github.markusbernhardt.proxy.search.desktop.kde.KdeProxySearchStrategy;
import com.github.markusbernhardt.proxy.search.desktop.osx.OsxProxySearchStrategy;
import com.github.markusbernhardt.proxy.search.desktop.win.WinProxySearchStrategy;
import com.github.markusbernhardt.proxy.util.Logger;
import com.github.markusbernhardt.proxy.util.PlatformUtil;
import com.github.markusbernhardt.proxy.util.ProxyException;
import java.net.ProxySelector;

/* JADX INFO: loaded from: autofirma.jar:com/github/markusbernhardt/proxy/search/desktop/DesktopProxySearchStrategy.class */
public class DesktopProxySearchStrategy implements ProxySearchStrategy {
    @Override // com.github.markusbernhardt.proxy.ProxySearchStrategy
    public ProxySelector getProxySelector() throws ProxyException {
        ProxySearchStrategy strategy = findDesktopSpecificStrategy();
        if (strategy == null) {
            return null;
        }
        return strategy.getProxySelector();
    }

    @Override // com.github.markusbernhardt.proxy.ProxySearchStrategy
    public String getName() {
        return "desktop";
    }

    private ProxySearchStrategy findDesktopSpecificStrategy() {
        PlatformUtil.Platform pf = PlatformUtil.getCurrentPlattform();
        PlatformUtil.Desktop dt = PlatformUtil.getCurrentDesktop();
        Logger.log(getClass(), Logger.LogLevel.TRACE, "Detecting system settings.", new Object[0]);
        ProxySearchStrategy strategy = null;
        if (pf == PlatformUtil.Platform.WIN) {
            Logger.log(getClass(), Logger.LogLevel.TRACE, "We are running on Windows.", new Object[0]);
            strategy = new WinProxySearchStrategy();
        } else if (dt == PlatformUtil.Desktop.KDE) {
            Logger.log(getClass(), Logger.LogLevel.TRACE, "We are running on KDE.", new Object[0]);
            strategy = new KdeProxySearchStrategy();
        } else if (dt == PlatformUtil.Desktop.GNOME) {
            Logger.log(getClass(), Logger.LogLevel.TRACE, "We are running on Gnome.", new Object[0]);
            strategy = new GnomeProxySearchStrategy();
        } else if (dt == PlatformUtil.Desktop.MAC_OS) {
            Logger.log(getClass(), Logger.LogLevel.TRACE, "We are running on Mac OSX.", new Object[0]);
            strategy = new OsxProxySearchStrategy();
        }
        return strategy;
    }
}
