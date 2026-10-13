package org.slf4j.impl;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Properties;
import org.apache.pdfbox.pdmodel.documentinterchange.taggedpdf.PDPrintFieldAttributeObject;
import org.slf4j.helpers.Util;

/* JADX INFO: loaded from: autofirma.jar:org/slf4j/impl/SimpleLoggerConfiguration.class */
public class SimpleLoggerConfiguration {
    private static final String CONFIGURATION_FILE = "simplelogger.properties";
    private static final boolean SHOW_DATE_TIME_DEFAULT = false;
    private static final boolean SHOW_THREAD_NAME_DEFAULT = true;
    private static final boolean SHOW_THREAD_ID_DEFAULT = false;
    static final boolean SHOW_LOG_NAME_DEFAULT = true;
    private static final boolean SHOW_SHORT_LOG_NAME_DEFAULT = false;
    private static final boolean LEVEL_IN_BRACKETS_DEFAULT = false;
    private static final boolean CACHE_OUTPUT_STREAM_DEFAULT = false;
    private static final String WARN_LEVELS_STRING_DEFAULT = "WARN";
    static int DEFAULT_LOG_LEVEL_DEFAULT = 20;
    private static final String DATE_TIME_FORMAT_STR_DEFAULT = null;
    private static String dateTimeFormatStr = DATE_TIME_FORMAT_STR_DEFAULT;
    private static String LOG_FILE_DEFAULT = "System.err";
    int defaultLogLevel = DEFAULT_LOG_LEVEL_DEFAULT;
    boolean showDateTime = false;
    DateFormat dateFormatter = null;
    boolean showThreadName = true;
    boolean showThreadId = false;
    boolean showLogName = true;
    boolean showShortLogName = false;
    boolean levelInBrackets = false;
    private String logFile = LOG_FILE_DEFAULT;
    OutputChoice outputChoice = null;
    private boolean cacheOutputStream = false;
    String warnLevelString = WARN_LEVELS_STRING_DEFAULT;
    private final Properties properties = new Properties();

    void init() {
        loadProperties();
        String defaultLogLevelString = getStringProperty(SimpleLogger.DEFAULT_LOG_LEVEL_KEY, null);
        if (defaultLogLevelString != null) {
            this.defaultLogLevel = stringToLevel(defaultLogLevelString);
        }
        this.showLogName = getBooleanProperty(SimpleLogger.SHOW_LOG_NAME_KEY, true);
        this.showShortLogName = getBooleanProperty(SimpleLogger.SHOW_SHORT_LOG_NAME_KEY, false);
        this.showDateTime = getBooleanProperty(SimpleLogger.SHOW_DATE_TIME_KEY, false);
        this.showThreadName = getBooleanProperty(SimpleLogger.SHOW_THREAD_NAME_KEY, true);
        this.showThreadId = getBooleanProperty(SimpleLogger.SHOW_THREAD_ID_KEY, false);
        dateTimeFormatStr = getStringProperty(SimpleLogger.DATE_TIME_FORMAT_KEY, DATE_TIME_FORMAT_STR_DEFAULT);
        this.levelInBrackets = getBooleanProperty(SimpleLogger.LEVEL_IN_BRACKETS_KEY, false);
        this.warnLevelString = getStringProperty(SimpleLogger.WARN_LEVEL_STRING_KEY, WARN_LEVELS_STRING_DEFAULT);
        this.logFile = getStringProperty(SimpleLogger.LOG_FILE_KEY, this.logFile);
        this.cacheOutputStream = getBooleanProperty(SimpleLogger.CACHE_OUTPUT_STREAM_STRING_KEY, false);
        this.outputChoice = computeOutputChoice(this.logFile, this.cacheOutputStream);
        if (dateTimeFormatStr != null) {
            try {
                this.dateFormatter = new SimpleDateFormat(dateTimeFormatStr);
            } catch (IllegalArgumentException e) {
                Util.report("Bad date format in simplelogger.properties; will output relative time", e);
            }
        }
    }

    private void loadProperties() {
        InputStream in = (InputStream) AccessController.doPrivileged(new PrivilegedAction<InputStream>() { // from class: org.slf4j.impl.SimpleLoggerConfiguration.1
            /* JADX WARN: Can't rename method to resolve collision */
            @Override // java.security.PrivilegedAction
            public InputStream run() {
                ClassLoader threadCL = Thread.currentThread().getContextClassLoader();
                if (threadCL != null) {
                    return threadCL.getResourceAsStream(SimpleLoggerConfiguration.CONFIGURATION_FILE);
                }
                return ClassLoader.getSystemResourceAsStream(SimpleLoggerConfiguration.CONFIGURATION_FILE);
            }
        });
        if (null != in) {
            try {
                this.properties.load(in);
            } catch (IOException e) {
            } finally {
                try {
                    in.close();
                } catch (IOException e2) {
                }
            }
        }
    }

    String getStringProperty(String name, String defaultValue) {
        String prop = getStringProperty(name);
        return prop == null ? defaultValue : prop;
    }

    boolean getBooleanProperty(String name, boolean defaultValue) {
        String prop = getStringProperty(name);
        return prop == null ? defaultValue : "true".equalsIgnoreCase(prop);
    }

    String getStringProperty(String name) {
        String prop = null;
        try {
            prop = System.getProperty(name);
        } catch (SecurityException e) {
        }
        return prop == null ? this.properties.getProperty(name) : prop;
    }

    static int stringToLevel(String levelStr) {
        if ("trace".equalsIgnoreCase(levelStr)) {
            return 0;
        }
        if ("debug".equalsIgnoreCase(levelStr)) {
            return 10;
        }
        if ("info".equalsIgnoreCase(levelStr)) {
            return 20;
        }
        if ("warn".equalsIgnoreCase(levelStr)) {
            return 30;
        }
        if ("error".equalsIgnoreCase(levelStr)) {
            return 40;
        }
        if (PDPrintFieldAttributeObject.CHECKED_STATE_OFF.equalsIgnoreCase(levelStr)) {
            return 50;
        }
        return 20;
    }

    private static OutputChoice computeOutputChoice(String logFile, boolean cacheOutputStream) {
        if ("System.err".equalsIgnoreCase(logFile)) {
            if (cacheOutputStream) {
                return new OutputChoice(OutputChoice.OutputChoiceType.CACHED_SYS_ERR);
            }
            return new OutputChoice(OutputChoice.OutputChoiceType.SYS_ERR);
        }
        if ("System.out".equalsIgnoreCase(logFile)) {
            if (cacheOutputStream) {
                return new OutputChoice(OutputChoice.OutputChoiceType.CACHED_SYS_OUT);
            }
            return new OutputChoice(OutputChoice.OutputChoiceType.SYS_OUT);
        }
        try {
            FileOutputStream fos = new FileOutputStream(logFile);
            PrintStream printStream = new PrintStream(fos);
            return new OutputChoice(printStream);
        } catch (FileNotFoundException e) {
            Util.report("Could not open [" + logFile + "]. Defaulting to System.err", e);
            return new OutputChoice(OutputChoice.OutputChoiceType.SYS_ERR);
        }
    }
}
