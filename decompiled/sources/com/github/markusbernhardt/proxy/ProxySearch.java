package com.github.markusbernhardt.proxy;

import com.github.markusbernhardt.proxy.search.browser.firefox.FirefoxProxySearchStrategy;
import com.github.markusbernhardt.proxy.search.browser.ie.IEProxySearchStrategy;
import com.github.markusbernhardt.proxy.search.desktop.DesktopProxySearchStrategy;
import com.github.markusbernhardt.proxy.search.desktop.gnome.GnomeDConfProxySearchStrategy;
import com.github.markusbernhardt.proxy.search.desktop.gnome.GnomeProxySearchStrategy;
import com.github.markusbernhardt.proxy.search.desktop.kde.KdeProxySearchStrategy;
import com.github.markusbernhardt.proxy.search.desktop.win.WinProxySearchStrategy;
import com.github.markusbernhardt.proxy.search.env.EnvProxySearchStrategy;
import com.github.markusbernhardt.proxy.search.java.JavaProxySearchStrategy;
import com.github.markusbernhardt.proxy.search.wpad.WpadProxySearchStrategy;
import com.github.markusbernhardt.proxy.selector.misc.BufferedProxySelector;
import com.github.markusbernhardt.proxy.selector.misc.ProxyListFallbackSelector;
import com.github.markusbernhardt.proxy.selector.pac.PacProxySelector;
import com.github.markusbernhardt.proxy.util.Logger;
import com.github.markusbernhardt.proxy.util.PlatformUtil;
import com.github.markusbernhardt.proxy.util.ProxyException;
import es.gob.afirma.standalone.configurator.common.PreferencesManager;
import java.awt.GraphicsEnvironment;
import java.net.ProxySelector;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;
import org.slf4j.helpers.MessageFormatter;

/* JADX INFO: loaded from: autofirma.jar:com/github/markusbernhardt/proxy/ProxySearch.class */
public class ProxySearch implements ProxySearchStrategy {
    private static final int DEFAULT_PAC_CACHE_SIZE = 20;
    private static final long DEFAULT_PAC_CACHE_TTL = 600000;
    private static final BufferedProxySelector.CacheScope DEFAULT_PAC_CACHE_SCOPE = BufferedProxySelector.CacheScope.CACHE_SCOPE_HOST;
    private List<ProxySearchStrategy> strategies = new ArrayList();
    private int pacCacheSize = 20;
    private long pacCacheTTL = DEFAULT_PAC_CACHE_TTL;
    private BufferedProxySelector.CacheScope pacCacheScope = DEFAULT_PAC_CACHE_SCOPE;

    /* JADX INFO: loaded from: autofirma.jar:com/github/markusbernhardt/proxy/ProxySearch$Strategy.class */
    public enum Strategy {
        OS_DEFAULT,
        WPAD,
        BROWSER,
        FIREFOX,
        IE,
        ENV_VAR,
        WIN,
        KDE,
        GNOME,
        JAVA
    }

    public static ProxySearch getDefaultProxySearch() {
        ProxySearch s = new ProxySearch();
        boolean headless = GraphicsEnvironment.isHeadless();
        if (headless) {
            s.addStrategy(Strategy.JAVA);
            s.addStrategy(Strategy.OS_DEFAULT);
            s.addStrategy(Strategy.ENV_VAR);
        } else {
            s.addStrategy(Strategy.JAVA);
            s.addStrategy(Strategy.BROWSER);
            s.addStrategy(Strategy.OS_DEFAULT);
            s.addStrategy(Strategy.ENV_VAR);
        }
        Logger.log(ProxySearch.class, Logger.LogLevel.TRACE, "Using default search priority: {}", s);
        return s;
    }

    public void addStrategy(Strategy strategy) {
        switch (strategy) {
            case OS_DEFAULT:
                this.strategies.add(new DesktopProxySearchStrategy());
                return;
            case WPAD:
                this.strategies.add(new WpadProxySearchStrategy());
                return;
            case BROWSER:
                this.strategies.add(getDefaultBrowserStrategy());
                return;
            case FIREFOX:
                this.strategies.add(new FirefoxProxySearchStrategy());
                return;
            case IE:
                this.strategies.add(new IEProxySearchStrategy());
                return;
            case ENV_VAR:
                this.strategies.add(new EnvProxySearchStrategy());
                return;
            case WIN:
                this.strategies.add(new WinProxySearchStrategy());
                return;
            case KDE:
                this.strategies.add(new KdeProxySearchStrategy());
                return;
            case GNOME:
                this.strategies.add(new GnomeDConfProxySearchStrategy());
                this.strategies.add(new GnomeProxySearchStrategy());
                return;
            case JAVA:
                this.strategies.add(new JavaProxySearchStrategy());
                return;
            default:
                throw new IllegalArgumentException("Unknown strategy code!");
        }
    }

    public void addStrategy(ProxySearchStrategy customStrategy, boolean addAsFirst) {
        if (customStrategy == null) {
            throw new IllegalArgumentException("No custom search strategy provided!");
        }
        if (addAsFirst) {
            this.strategies.add(0, customStrategy);
        } else {
            this.strategies.add(customStrategy);
        }
    }

    public void removeStrategy(Strategy strategy) {
        switch (strategy) {
            case OS_DEFAULT:
                removeStrategy(pss -> {
                    return pss instanceof DesktopProxySearchStrategy;
                });
                break;
            case WPAD:
                removeStrategy(pss2 -> {
                    return pss2 instanceof WpadProxySearchStrategy;
                });
                break;
            case BROWSER:
                switch (PlatformUtil.getDefaultBrowser()) {
                    case IE:
                        removeStrategy(pss3 -> {
                            return pss3 instanceof IEProxySearchStrategy;
                        });
                        break;
                    case FIREFOX:
                        removeStrategy(pss4 -> {
                            return pss4 instanceof FirefoxProxySearchStrategy;
                        });
                        break;
                }
                break;
            case FIREFOX:
                removeStrategy(pss5 -> {
                    return pss5 instanceof FirefoxProxySearchStrategy;
                });
                break;
            case IE:
                removeStrategy(pss6 -> {
                    return pss6 instanceof IEProxySearchStrategy;
                });
                break;
            case ENV_VAR:
                removeStrategy(pss7 -> {
                    return pss7 instanceof EnvProxySearchStrategy;
                });
                break;
            case WIN:
                removeStrategy(pss8 -> {
                    return pss8 instanceof WinProxySearchStrategy;
                });
                break;
            case KDE:
                removeStrategy(pss9 -> {
                    return pss9 instanceof KdeProxySearchStrategy;
                });
                break;
            case GNOME:
                removeStrategy(pss10 -> {
                    return pss10 instanceof GnomeDConfProxySearchStrategy;
                });
                removeStrategy(pss11 -> {
                    return pss11 instanceof GnomeProxySearchStrategy;
                });
                break;
            case JAVA:
                removeStrategy(pss12 -> {
                    return pss12 instanceof JavaProxySearchStrategy;
                });
                break;
        }
    }

    private void removeStrategy(Predicate<ProxySearchStrategy> filter) {
        ProxySearchStrategy proxySeachStrategy = this.strategies.stream().filter(filter).findFirst().orElse(null);
        if (proxySeachStrategy != null) {
            this.strategies.remove(proxySeachStrategy);
        }
    }

    public void setPacCacheSettings(int size, long ttl, BufferedProxySelector.CacheScope cacheScope) {
        this.pacCacheSize = size;
        this.pacCacheTTL = ttl;
        this.pacCacheScope = cacheScope;
    }

    private ProxySearchStrategy getDefaultBrowserStrategy() {
        switch (PlatformUtil.getDefaultBrowser()) {
            case IE:
                return new IEProxySearchStrategy();
            case FIREFOX:
                return new FirefoxProxySearchStrategy();
            default:
                return null;
        }
    }

    @Override // com.github.markusbernhardt.proxy.ProxySearchStrategy
    public ProxySelector getProxySelector() {
        Logger.log(getClass(), Logger.LogLevel.TRACE, "Executing search strategies to find proxy selector", new Object[0]);
        for (ProxySearchStrategy strat : this.strategies) {
            try {
                ProxySelector selector = strat.getProxySelector();
                if (selector != null) {
                    ProxySelector selector2 = installBufferingAndFallbackBehaviour(selector);
                    Logger.log(getClass(), Logger.LogLevel.INFO, "Proxy found for " + strat.getName(), new Object[0]);
                    return selector2;
                }
                Logger.log(getClass(), Logger.LogLevel.INFO, "No proxy found for " + strat.getName() + ". Trying next one.", new Object[0]);
            } catch (ProxyException e) {
                Logger.log(getClass(), Logger.LogLevel.DEBUG, "Strategy {} failed trying next one.", e);
            }
        }
        return null;
    }

    @Override // com.github.markusbernhardt.proxy.ProxySearchStrategy
    public String getName() {
        return PreferencesManager.VALUE_KEYSTORE_DEFAULT;
    }

    private ProxySelector installBufferingAndFallbackBehaviour(ProxySelector selector) {
        if (selector instanceof PacProxySelector) {
            if (this.pacCacheSize > 0) {
                selector = new BufferedProxySelector(this.pacCacheSize, this.pacCacheTTL, selector, this.pacCacheScope);
            }
            selector = new ProxyListFallbackSelector(selector);
        }
        return selector;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder("Proxy search: ");
        for (ProxySearchStrategy strat : this.strategies) {
            sb.append(strat);
            sb.append(" ");
        }
        return sb.toString();
    }

    public static void main(String[] args) {
        ProxySearch ps = getDefaultProxySearch();
        Logger.setBackend(new Logger.LogBackEnd() { // from class: com.github.markusbernhardt.proxy.ProxySearch.1
            @Override // com.github.markusbernhardt.proxy.util.Logger.LogBackEnd
            public void log(Class<?> clazz, Logger.LogLevel loglevel, String msg, Object... params) {
                System.out.println(MessageFormatter.format(msg, params).getMessage());
            }
        });
        ps.getProxySelector();
    }
}
