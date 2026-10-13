package com.sun.java.help.search;

import com.sun.java.help.impl.HeaderParser;
import es.gob.afirma.keystores.filters.PolicyIdFilter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.util.Hashtable;
import java.util.Locale;
import java.util.Vector;
import javax.help.search.ConfigFile;
import javax.help.search.IndexerKit;
import org.apache.pdfbox.contentstream.operator.OperatorName;

/* JADX INFO: loaded from: autofirma.jar:com/sun/java/help/search/Indexer.class */
public class Indexer {
    String dbName;
    short docNumber;
    private ConfigFile config;
    private String title;
    private String header;
    String lastPCdata;
    int currentPos;
    String sourcepath;
    private IndexerKit kit;
    private Hashtable typeHandlers;
    private String charSetName;
    private String lang;
    private static final Hashtable kitRegistry = new Hashtable();
    private static final Hashtable kitTypeRegistry = new Hashtable();
    private static final Hashtable kitLoaderRegistry = new Hashtable();
    private static boolean debugFlag;
    PrintStream verbose = null;
    PrintStream logStream = null;
    private DefaultIndexBuilder indexBuilder = null;
    private boolean firstHeader = false;
    boolean openTagSeen = false;
    boolean inPre = false;
    boolean inTitle = false;
    boolean inFirstHeader = false;
    boolean inOption = false;
    int inBlock = 0;
    private String defaultCharSetName = "ISO8859_1";
    private String defaultLang = Locale.getDefault().toString();

    public static void main(String[] strArr) {
        try {
            new Indexer().compile(strArr);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public Indexer() {
        this.dbName = "JavaHelpSearch";
        if (this.dbName.lastIndexOf("/") != this.dbName.length() - 1) {
            this.dbName = this.dbName.concat("/");
        }
        this.docNumber = (short) 0;
    }

    public void compile(String[] strArr) throws Exception {
        long jCurrentTimeMillis = System.currentTimeMillis();
        String str = null;
        Vector vector = new Vector();
        Vector vector2 = new Vector();
        this.sourcepath = "";
        boolean z = false;
        int i = 0;
        while (i < strArr.length) {
            if (strArr[i].equals("-db")) {
                if (i + 1 < strArr.length) {
                    i++;
                    this.dbName = strArr[i];
                    if (this.dbName.lastIndexOf("/") != this.dbName.length() - 1) {
                        this.dbName = this.dbName.concat("/");
                    }
                } else {
                    System.out.println(new StringBuffer().append(strArr[i]).append("-db requires argument").toString());
                }
            } else if (strArr[i].equals("-sourcepath")) {
                if (i + 1 < strArr.length) {
                    i++;
                    this.sourcepath = strArr[i];
                } else {
                    System.out.println(new StringBuffer().append(strArr[i]).append("-sourcepath requires argument").toString());
                }
            } else if (strArr[i].equals("-locale")) {
                if (i + 1 < strArr.length) {
                    i++;
                    this.defaultLang = strArr[i];
                } else {
                    System.out.println(new StringBuffer().append(strArr[i]).append("-locale requires argument").toString());
                }
            } else if (strArr[i].equals("-logfile")) {
                if (i + 1 < strArr.length) {
                    i++;
                    String str2 = strArr[i];
                    try {
                        this.logStream = new PrintStream((OutputStream) new FileOutputStream(str2), true);
                        System.setErr(this.logStream);
                        System.setOut(this.logStream);
                        this.verbose = this.logStream;
                    } catch (FileNotFoundException e) {
                        System.out.println(new StringBuffer().append("Couldn't create logFile ").append(str2).toString());
                    }
                } else {
                    System.out.println(new StringBuffer().append(strArr[i]).append("-logfile requires argument").toString());
                }
            } else if (strArr[i].equals("-verbose")) {
                this.verbose = System.out;
            } else if (strArr[i].equals("-nostopwords")) {
                z = true;
            } else if (strArr[i].equals("-c")) {
                if (i + 1 < strArr.length) {
                    i++;
                    str = strArr[i];
                } else {
                    System.out.println(new StringBuffer().append(strArr[i]).append("-c requires argument").toString());
                }
            } else {
                if (strArr[i].startsWith("-")) {
                    System.out.println(new StringBuffer().append("Unknown argument '").append(strArr[i]).append(OperatorName.SHOW_TEXT_LINE).toString());
                    showUsage();
                    return;
                }
                vector.addElement(strArr[i]);
            }
            i++;
        }
        this.config = new ConfigFile(str, vector2, z);
        Vector files = this.config.getFiles();
        int size = vector.size();
        for (int i2 = 0; i2 < size; i2++) {
            files = loadFiles((String) vector.elementAt(i2), files);
        }
        int size2 = files.size();
        if (size2 == 0) {
            System.out.println("No files specified to index");
            showUsage();
            return;
        }
        this.indexBuilder = new DefaultIndexBuilder(this.dbName);
        this.indexBuilder.storeStopWords(this.config.getStopWords());
        for (int i3 = 0; i3 < size2; i3++) {
            String str3 = (String) files.elementAt(i3);
            URL url = new URL("file", "", new StringBuffer().append(this.sourcepath).append(str3).toString());
            InputStream inputStreamOpenStream = url.openStream();
            setContentType(url.openConnection().getContentType());
            if (this.kit != null) {
                try {
                    if (this.verbose != null) {
                        this.verbose.println(new StringBuffer().append("   File: '").append(str3).append(OperatorName.SHOW_TEXT_LINE).toString());
                        this.verbose.println(new StringBuffer().append("    URL: '").append(this.config.getURLString(str3)).append(OperatorName.SHOW_TEXT_LINE).toString());
                    }
                    parseFile(inputStreamOpenStream, str3, false);
                    inputStreamOpenStream.close();
                } catch (UnsupportedEncodingException e2) {
                    System.out.println(new StringBuffer().append("File: '").append(str3).append("' encoding ").append(this.charSetName).append(" not supported").toString());
                    inputStreamOpenStream.close();
                } catch (IOException e3) {
                    if (debugFlag) {
                        e3.printStackTrace();
                    }
                    System.out.println(new StringBuffer().append("I/O exception occurred in file '").append(this.sourcepath).append(str3).append(OperatorName.SHOW_TEXT_LINE).toString());
                    inputStreamOpenStream.close();
                }
            }
        }
        long jCurrentTimeMillis2 = System.currentTimeMillis() - jCurrentTimeMillis;
        long jCurrentTimeMillis3 = (System.currentTimeMillis() - jCurrentTimeMillis) - jCurrentTimeMillis2;
        if (this.verbose != null) {
            this.verbose.println(new StringBuffer().append("        Parse time: ").append(jCurrentTimeMillis2 / 1000.0d).append(" s").toString());
        }
        this.indexBuilder.close();
    }

    void parseFile(InputStream inputStream, String str, boolean z) throws IOException {
        try {
            this.kit.parse(new InputStreamReader(inputStream, this.charSetName), str, z, this.indexBuilder, this.config);
        } catch (ChangedCharSetException e) {
            String charSetSpec = e.getCharSetSpec();
            if (e.keyEqualsCharSet()) {
                this.charSetName = charSetSpec;
            } else {
                setCharsetFromContentTypeParameters(charSetSpec);
            }
            inputStream.close();
            parseFile(new URL("file", "", new StringBuffer().append(this.sourcepath).append(str).toString()).openStream(), str, true);
        }
    }

    public Vector loadFiles(String str, Vector vector) {
        File file = new File(str);
        if (file.isDirectory()) {
            for (String str2 : file.list()) {
                vector = loadFiles(new StringBuffer().append(file.getPath()).append(File.separator).append(str2).toString(), vector);
            }
        } else {
            vector.addElement(str);
        }
        return vector;
    }

    public void showUsage() {
        System.out.println(" Usage:   java JavaHelp.Index options file ...");
        System.out.println(" Options: -c file   config file");
        System.out.println("          -db file  generated database file name");
        System.out.println("          -verbose  verbose documentation");
        System.out.println("          -nostopwords ignore stop words");
        System.out.println("          -locale language_country_variant");
        System.out.println("          -logfile log file name");
        System.out.println("Note: config file composition:");
        System.out.println("          IndexRemove /public_html/JavaHelp/demo");
        System.out.println("          IndexPrepend ..");
        System.out.println("          StopWords word1 ... wordN");
        System.out.println("          StopWordsFile stopWordFileName");
        System.out.println("          File /public_html/JavaHelp/demo/first.html");
        System.out.println("          File=/public_html/JavaHelp/demo/second.html");
        System.out.println("          ...");
    }

    static {
        registerIndexerKitForContentType("text/plain", "com.sun.java.help.search.PlainTextIndexerKit", null);
        registerIndexerKitForContentType("text/html", "com.sun.java.help.search.HTMLIndexerKit", null);
        debugFlag = false;
    }

    public final void setContentType(String str) {
        IndexerKit indexerKitForContentType;
        this.charSetName = this.defaultCharSetName;
        this.lang = this.defaultLang;
        debug(new StringBuffer().append("type=").append(str).toString());
        int iIndexOf = str.indexOf(PolicyIdFilter.OID_SEPARATOR);
        if (iIndexOf > -1) {
            String strSubstring = str.substring(iIndexOf);
            str = str.substring(0, iIndexOf).trim();
            setCharsetFromContentTypeParameters(strSubstring);
        }
        if ((this.kit == null || !str.equals(this.kit.getContentType())) && (indexerKitForContentType = getIndexerKitForContentType(str)) != null) {
            this.kit = indexerKitForContentType;
        }
        this.kit.setLocale(this.lang);
    }

    private void setCharsetFromContentTypeParameters(String str) {
        String strFindValue;
        try {
            int iIndexOf = str.indexOf(59);
            if (iIndexOf > -1 && iIndexOf < str.length() - 1) {
                str = str.substring(iIndexOf + 1);
            }
            if (str.length() > 0 && (strFindValue = new HeaderParser(str).findValue("charset")) != null) {
                this.charSetName = strFindValue;
            }
        } catch (IndexOutOfBoundsException e) {
        } catch (NullPointerException e2) {
        } catch (Exception e3) {
            System.err.println(new StringBuffer().append("Indexer.getCharsetFromContentTypeParameters failed on: ").append(str).toString());
            e3.printStackTrace();
        }
    }

    public IndexerKit getIndexerKitForContentType(String str) {
        if (this.typeHandlers == null) {
            this.typeHandlers = new Hashtable(3);
        }
        IndexerKit defaultIndexerKit = (IndexerKit) this.typeHandlers.get(str);
        if (defaultIndexerKit == null) {
            defaultIndexerKit = createIndexerKitForContentType(str);
            if (defaultIndexerKit != null) {
                setIndexerKitForContentType(str, defaultIndexerKit);
            }
        }
        if (defaultIndexerKit == null) {
            defaultIndexerKit = new DefaultIndexerKit();
            setIndexerKitForContentType(str, defaultIndexerKit);
        }
        return defaultIndexerKit;
    }

    public void setIndexerKitForContentType(String str, IndexerKit indexerKit) {
        if (this.typeHandlers == null) {
            this.typeHandlers = new Hashtable(3);
        }
        this.typeHandlers.put(str, indexerKit);
    }

    public static IndexerKit createIndexerKitForContentType(String str) {
        Class<?> cls;
        debug(new StringBuffer().append("Getting IndexerKit for ").append(str).toString());
        IndexerKit indexerKit = (IndexerKit) kitRegistry.get(str);
        if (indexerKit == null) {
            String str2 = (String) kitTypeRegistry.get(str);
            ClassLoader classLoader = (ClassLoader) kitLoaderRegistry.get(str);
            try {
                if (classLoader != null) {
                    cls = classLoader.loadClass(str2);
                } else {
                    cls = Class.forName(str2);
                }
                indexerKit = (IndexerKit) cls.newInstance();
                kitRegistry.put(str, indexerKit);
            } catch (Throwable th) {
                if (debugFlag) {
                    th.printStackTrace();
                }
                indexerKit = null;
            }
        }
        if (indexerKit != null) {
            return (IndexerKit) indexerKit.clone();
        }
        return null;
    }

    public static void registerIndexerKitForContentType(String str, String str2, ClassLoader classLoader) {
        kitTypeRegistry.put(str, str2);
        if (classLoader != null) {
            kitLoaderRegistry.put(str, classLoader);
        }
    }

    private static void debug(String str) {
        if (debugFlag) {
            System.out.println(new StringBuffer().append("Indexer: ").append(str).toString());
        }
    }
}
