package org.slf4j.impl;

import java.io.PrintStream;
import java.util.Date;
import org.slf4j.event.LoggingEvent;
import org.slf4j.helpers.FormattingTuple;
import org.slf4j.helpers.MarkerIgnoringBase;
import org.slf4j.helpers.MessageFormatter;

/* JADX INFO: loaded from: autofirma.jar:org/slf4j/impl/SimpleLogger.class */
public class SimpleLogger extends MarkerIgnoringBase {
    private static final long serialVersionUID = -632788891211436180L;
    protected static final int LOG_LEVEL_TRACE = 0;
    protected static final int LOG_LEVEL_DEBUG = 10;
    protected static final int LOG_LEVEL_INFO = 20;
    protected static final int LOG_LEVEL_WARN = 30;
    protected static final int LOG_LEVEL_ERROR = 40;
    private static final String TID_PREFIX = "tid=";
    protected static final int LOG_LEVEL_OFF = 50;
    protected int currentLogLevel;
    private transient String shortLogName = null;
    public static final String SYSTEM_PREFIX = "org.slf4j.simpleLogger.";
    public static final String LOG_KEY_PREFIX = "org.slf4j.simpleLogger.log.";
    public static final String CACHE_OUTPUT_STREAM_STRING_KEY = "org.slf4j.simpleLogger.cacheOutputStream";
    public static final String WARN_LEVEL_STRING_KEY = "org.slf4j.simpleLogger.warnLevelString";
    public static final String LEVEL_IN_BRACKETS_KEY = "org.slf4j.simpleLogger.levelInBrackets";
    public static final String LOG_FILE_KEY = "org.slf4j.simpleLogger.logFile";
    public static final String SHOW_SHORT_LOG_NAME_KEY = "org.slf4j.simpleLogger.showShortLogName";
    public static final String SHOW_LOG_NAME_KEY = "org.slf4j.simpleLogger.showLogName";
    public static final String SHOW_THREAD_NAME_KEY = "org.slf4j.simpleLogger.showThreadName";
    public static final String SHOW_THREAD_ID_KEY = "org.slf4j.simpleLogger.showThreadId";
    public static final String DATE_TIME_FORMAT_KEY = "org.slf4j.simpleLogger.dateTimeFormat";
    public static final String SHOW_DATE_TIME_KEY = "org.slf4j.simpleLogger.showDateTime";
    public static final String DEFAULT_LOG_LEVEL_KEY = "org.slf4j.simpleLogger.defaultLogLevel";
    private static long START_TIME = System.currentTimeMillis();
    private static boolean INITIALIZED = false;
    private static final SimpleLoggerConfiguration CONFIG_PARAMS = new SimpleLoggerConfiguration();

    static void lazyInit() {
        if (INITIALIZED) {
            return;
        }
        INITIALIZED = true;
        init();
    }

    static void init() {
        CONFIG_PARAMS.init();
    }

    SimpleLogger(String name) {
        this.currentLogLevel = 20;
        this.name = name;
        String levelString = recursivelyComputeLevelString();
        if (levelString != null) {
            this.currentLogLevel = SimpleLoggerConfiguration.stringToLevel(levelString);
        } else {
            this.currentLogLevel = CONFIG_PARAMS.defaultLogLevel;
        }
    }

    String recursivelyComputeLevelString() {
        String tempName = this.name;
        String levelString = null;
        int length = tempName.length();
        while (true) {
            int indexOfLastDot = length;
            if (levelString != null || indexOfLastDot <= -1) {
                break;
            }
            tempName = tempName.substring(0, indexOfLastDot);
            levelString = CONFIG_PARAMS.getStringProperty(LOG_KEY_PREFIX + tempName, null);
            length = String.valueOf(tempName).lastIndexOf(".");
        }
        return levelString;
    }

    private void log(int level, String message, Throwable t) {
        if (!isLevelEnabled(level)) {
            return;
        }
        StringBuilder buf = new StringBuilder(32);
        if (CONFIG_PARAMS.showDateTime) {
            if (CONFIG_PARAMS.dateFormatter != null) {
                buf.append(getFormattedDate());
                buf.append(' ');
            } else {
                buf.append(System.currentTimeMillis() - START_TIME);
                buf.append(' ');
            }
        }
        if (CONFIG_PARAMS.showThreadName) {
            buf.append('[');
            buf.append(Thread.currentThread().getName());
            buf.append("] ");
        }
        if (CONFIG_PARAMS.showThreadId) {
            buf.append(TID_PREFIX);
            buf.append(Thread.currentThread().getId());
            buf.append(' ');
        }
        if (CONFIG_PARAMS.levelInBrackets) {
            buf.append('[');
        }
        String levelStr = renderLevel(level);
        buf.append(levelStr);
        if (CONFIG_PARAMS.levelInBrackets) {
            buf.append(']');
        }
        buf.append(' ');
        if (CONFIG_PARAMS.showShortLogName) {
            if (this.shortLogName == null) {
                this.shortLogName = computeShortName();
            }
            buf.append(String.valueOf(this.shortLogName)).append(" - ");
        } else if (CONFIG_PARAMS.showLogName) {
            buf.append(String.valueOf(this.name)).append(" - ");
        }
        buf.append(message);
        write(buf, t);
    }

    protected String renderLevel(int level) {
        switch (level) {
            case 0:
                return "TRACE";
            case 10:
                return "DEBUG";
            case 20:
                return "INFO";
            case 30:
                return CONFIG_PARAMS.warnLevelString;
            case 40:
                return "ERROR";
            default:
                throw new IllegalStateException("Unrecognized level [" + level + "]");
        }
    }

    void write(StringBuilder buf, Throwable t) {
        PrintStream targetStream = CONFIG_PARAMS.outputChoice.getTargetPrintStream();
        synchronized (CONFIG_PARAMS) {
            targetStream.println(buf.toString());
            writeThrowable(t, targetStream);
            targetStream.flush();
        }
    }

    protected void writeThrowable(Throwable t, PrintStream targetStream) {
        if (t != null) {
            t.printStackTrace(targetStream);
        }
    }

    private String getFormattedDate() {
        String dateText;
        Date now = new Date();
        synchronized (CONFIG_PARAMS.dateFormatter) {
            dateText = CONFIG_PARAMS.dateFormatter.format(now);
        }
        return dateText;
    }

    private String computeShortName() {
        return this.name.substring(this.name.lastIndexOf(".") + 1);
    }

    private void formatAndLog(int level, String format, Object arg1, Object arg2) {
        if (!isLevelEnabled(level)) {
            return;
        }
        FormattingTuple tp = MessageFormatter.format(format, arg1, arg2);
        log(level, tp.getMessage(), tp.getThrowable());
    }

    private void formatAndLog(int level, String format, Object... arguments) {
        if (!isLevelEnabled(level)) {
            return;
        }
        FormattingTuple tp = MessageFormatter.arrayFormat(format, arguments);
        log(level, tp.getMessage(), tp.getThrowable());
    }

    protected boolean isLevelEnabled(int logLevel) {
        return logLevel >= this.currentLogLevel;
    }

    @Override // org.slf4j.Logger
    public boolean isTraceEnabled() {
        return isLevelEnabled(0);
    }

    @Override // org.slf4j.Logger
    public void trace(String msg) {
        log(0, msg, null);
    }

    @Override // org.slf4j.Logger
    public void trace(String format, Object param1) {
        formatAndLog(0, format, param1, null);
    }

    @Override // org.slf4j.Logger
    public void trace(String format, Object param1, Object param2) {
        formatAndLog(0, format, param1, param2);
    }

    @Override // org.slf4j.Logger
    public void trace(String format, Object... argArray) {
        formatAndLog(0, format, argArray);
    }

    @Override // org.slf4j.Logger
    public void trace(String msg, Throwable t) {
        log(0, msg, t);
    }

    @Override // org.slf4j.Logger
    public boolean isDebugEnabled() {
        return isLevelEnabled(10);
    }

    @Override // org.slf4j.Logger
    public void debug(String msg) {
        log(10, msg, null);
    }

    @Override // org.slf4j.Logger
    public void debug(String format, Object param1) {
        formatAndLog(10, format, param1, null);
    }

    @Override // org.slf4j.Logger
    public void debug(String format, Object param1, Object param2) {
        formatAndLog(10, format, param1, param2);
    }

    @Override // org.slf4j.Logger
    public void debug(String format, Object... argArray) {
        formatAndLog(10, format, argArray);
    }

    @Override // org.slf4j.Logger
    public void debug(String msg, Throwable t) {
        log(10, msg, t);
    }

    @Override // org.slf4j.Logger
    public boolean isInfoEnabled() {
        return isLevelEnabled(20);
    }

    @Override // org.slf4j.Logger
    public void info(String msg) {
        log(20, msg, null);
    }

    @Override // org.slf4j.Logger
    public void info(String format, Object arg) {
        formatAndLog(20, format, arg, null);
    }

    @Override // org.slf4j.Logger
    public void info(String format, Object arg1, Object arg2) {
        formatAndLog(20, format, arg1, arg2);
    }

    @Override // org.slf4j.Logger
    public void info(String format, Object... argArray) {
        formatAndLog(20, format, argArray);
    }

    @Override // org.slf4j.Logger
    public void info(String msg, Throwable t) {
        log(20, msg, t);
    }

    @Override // org.slf4j.Logger
    public boolean isWarnEnabled() {
        return isLevelEnabled(30);
    }

    @Override // org.slf4j.Logger
    public void warn(String msg) {
        log(30, msg, null);
    }

    @Override // org.slf4j.Logger
    public void warn(String format, Object arg) {
        formatAndLog(30, format, arg, null);
    }

    @Override // org.slf4j.Logger
    public void warn(String format, Object arg1, Object arg2) {
        formatAndLog(30, format, arg1, arg2);
    }

    @Override // org.slf4j.Logger
    public void warn(String format, Object... argArray) {
        formatAndLog(30, format, argArray);
    }

    @Override // org.slf4j.Logger
    public void warn(String msg, Throwable t) {
        log(30, msg, t);
    }

    @Override // org.slf4j.Logger
    public boolean isErrorEnabled() {
        return isLevelEnabled(40);
    }

    @Override // org.slf4j.Logger
    public void error(String msg) {
        log(40, msg, null);
    }

    @Override // org.slf4j.Logger
    public void error(String format, Object arg) {
        formatAndLog(40, format, arg, null);
    }

    @Override // org.slf4j.Logger
    public void error(String format, Object arg1, Object arg2) {
        formatAndLog(40, format, arg1, arg2);
    }

    @Override // org.slf4j.Logger
    public void error(String format, Object... argArray) {
        formatAndLog(40, format, argArray);
    }

    @Override // org.slf4j.Logger
    public void error(String msg, Throwable t) {
        log(40, msg, t);
    }

    public void log(LoggingEvent event) {
        int levelInt = event.getLevel().toInt();
        if (!isLevelEnabled(levelInt)) {
            return;
        }
        FormattingTuple tp = MessageFormatter.arrayFormat(event.getMessage(), event.getArgumentArray(), event.getThrowable());
        log(levelInt, tp.getMessage(), event.getThrowable());
    }
}
