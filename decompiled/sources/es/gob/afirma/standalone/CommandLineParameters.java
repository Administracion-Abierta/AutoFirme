package es.gob.afirma.standalone;

import es.gob.afirma.core.misc.Platform;
import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;

/* JADX INFO: loaded from: autofirma.jar:es/gob/afirma/standalone/CommandLineParameters.class */
final class CommandLineParameters {
    private static final String PARAM_INPUT = "-i";
    private static final String PARAM_OUTPUT = "-o";
    private static final String PARAM_ALIAS = "-alias";
    private static final String PARAM_FILTER = "-filter";
    private static final String PARAM_STORE = "-store";
    private static final String PARAM_FORMAT = "-format";
    private static final String PARAM_PASSWD = "-password";
    private static final String PARAM_ALGO = "-algorithm";
    private static final String PARAM_CONFIG = "-config";
    private static final String PARAM_OP = "-operation";
    private static final String PARAM_GUI = "-gui";
    private static final String PARAM_CERT_GUI = "-certgui";
    private static final String PARAM_PREURL = "-preurl";
    private static final String PARAM_POSTURL = "-posturl";
    private static final String PARAM_HASH_FORMAT = "-hformat";
    private static final String PARAM_HASH_ALGO = "-halgorithm";
    private static final String PARAM_RECURSIVE = "-r";
    public static final String PARAM_XML = "-xml";
    public static final String FORMAT_AUTO = "auto";
    public static final String FORMAT_XADES = "xades";
    public static final String FORMAT_PADES = "pades";
    public static final String FORMAT_CADES = "cades";
    public static final String FORMAT_FACTURAE = "facturae";
    public static final String FORMAT_OOXML = "ooxml";
    public static final String FORMAT_ODF = "odf";
    public static final String DEFAULT_FORMAT = "auto";
    public static final String FORMAT_HASH_FILE_HEX = "hex";
    public static final String FORMAT_HASH_FILE_BASE64 = "b64";
    public static final String FORMAT_HASH_FILE_BIN = "bin";
    public static final String FORMAT_HASH_DIR_PLAIN = "txt";
    public static final String FORMAT_HASH_DIR_XML = "xml";
    public static final String DEFAULT_FORMAT_FILE_HASH = "hex";
    public static final String DEFAULT_FORMAT_DIR_HASH = "xml";
    public static final String MASSIVE_OP_SIGN = "sign";
    public static final String MASSIVE_OP_COSIGN = "cosign";
    public static final String MASSIVE_OP_COUNTERSIGN = "countersign";
    private static final String DEFAULT_MASSIVE_OP = "sign";
    public static final String ALGO_SHA1 = "sha1";
    public static final String ALGO_SHA256 = "sha256";
    public static final String ALGO_SHA384 = "sha384";
    public static final String ALGO_SHA512 = "sha512";
    private static final String DEFAULT_ALGO = "sha512";
    private String store;
    private String alias;
    private String filter;
    private File mainFile;
    private String password;
    private String algorithm;
    private String hashAlgorithm;
    private String extraParams;
    private String massiveOp;
    private boolean xml;
    private boolean gui;
    private boolean certgui;
    private boolean recursive;
    private String formatParam;
    private String hashFormatParam;
    private String preUrlParam;
    private String postUrlParam;
    private String inputFileParam;
    private String outputFileParam;
    private File inputFile = null;
    private File outputFile = null;
    private String format = null;
    private String hashFormat = null;
    private final boolean help = false;
    private URL postUrl = null;
    private URL preUrl = null;
    private final Platform.OS system = Platform.getOS();

    public CommandLineParameters(CommandLineCommand commandLineCommand, String[] strArr) throws CommandLineException {
        this.store = null;
        this.alias = null;
        this.filter = null;
        this.mainFile = null;
        this.password = null;
        this.algorithm = null;
        this.hashAlgorithm = null;
        this.extraParams = null;
        this.massiveOp = null;
        this.xml = false;
        this.gui = false;
        this.certgui = false;
        this.recursive = false;
        this.formatParam = null;
        this.hashFormatParam = null;
        this.preUrlParam = null;
        this.postUrlParam = null;
        this.inputFileParam = null;
        this.outputFileParam = null;
        int i = 1;
        if (commandLineCommand.isMainFileNeeded()) {
            if (strArr.length < 2) {
                throw new CommandLineException(CommandLineMessages.getString("CommandLineLauncher.120"));
            }
            this.mainFile = new File(strArr[1]);
            if (!this.mainFile.exists()) {
                throw new CommandLineException(CommandLineMessages.getString("CommandLineLauncher.121", strArr[1]));
            }
            if (!this.mainFile.canRead()) {
                throw new CommandLineException(CommandLineMessages.getString("CommandLineLauncher.122", strArr[1]));
            }
            i = 1 + 1;
        }
        while (i < strArr.length) {
            if (PARAM_XML.equals(strArr[i])) {
                this.xml = true;
            } else if (PARAM_PREURL.equals(strArr[i])) {
                if (this.preUrlParam != null) {
                    throw new CommandLineException(CommandLineMessages.getString("CommandLineLauncher.26", strArr[i]), this.gui);
                }
                if (i >= strArr.length - 1) {
                    throw new CommandLineException(CommandLineMessages.getString("CommandLineLauncher.128"), this.gui);
                }
                this.preUrlParam = strArr[i + 1];
                i++;
            } else if (PARAM_POSTURL.equals(strArr[i])) {
                if (this.postUrlParam != null) {
                    throw new CommandLineException(CommandLineMessages.getString("CommandLineLauncher.26", strArr[i]), this.gui);
                }
                if (i >= strArr.length - 1) {
                    throw new CommandLineException(CommandLineMessages.getString("CommandLineLauncher.129"), this.gui);
                }
                this.postUrlParam = strArr[i + 1];
                i++;
            } else if (PARAM_GUI.equals(strArr[i])) {
                this.gui = true;
            } else if (PARAM_CERT_GUI.equals(strArr[i])) {
                this.certgui = true;
            } else if (PARAM_RECURSIVE.equals(strArr[i])) {
                this.recursive = true;
            } else if (PARAM_STORE.equals(strArr[i])) {
                if (this.store != null) {
                    throw new CommandLineException(CommandLineMessages.getString("CommandLineLauncher.26", strArr[i]), this.gui);
                }
                if (i >= strArr.length - 1) {
                    throw new CommandLineException(CommandLineMessages.getString("CommandLineLauncher.130"), this.gui);
                }
                this.store = strArr[i + 1];
                i++;
            } else if (PARAM_OP.equals(strArr[i])) {
                if (this.massiveOp != null) {
                    throw new CommandLineException(CommandLineMessages.getString("CommandLineLauncher.26", strArr[i]), this.gui);
                }
                if (i >= strArr.length - 1) {
                    throw new CommandLineException(CommandLineMessages.getString("CommandLineLauncher.131"), this.gui);
                }
                this.massiveOp = strArr[i + 1];
                i++;
            } else if (PARAM_ALGO.equals(strArr[i])) {
                if (this.algorithm != null) {
                    throw new CommandLineException(CommandLineMessages.getString("CommandLineLauncher.26", strArr[i]), this.gui);
                }
                if (i >= strArr.length - 1) {
                    throw new CommandLineException(CommandLineMessages.getString("CommandLineLauncher.132"), this.gui);
                }
                this.algorithm = strArr[i + 1];
                i++;
            } else if (PARAM_HASH_ALGO.equals(strArr[i])) {
                if (this.hashAlgorithm != null) {
                    throw new CommandLineException(CommandLineMessages.getString("CommandLineLauncher.26", strArr[i]), this.gui);
                }
                if (i >= strArr.length - 1) {
                    throw new CommandLineException(CommandLineMessages.getString("CommandLineLauncher.133"), this.gui);
                }
                this.hashAlgorithm = strArr[i + 1];
                i++;
            } else if (PARAM_CONFIG.equals(strArr[i])) {
                if (this.extraParams != null) {
                    throw new CommandLineException(CommandLineMessages.getString("CommandLineLauncher.26", strArr[i]), this.gui);
                }
                if (i >= strArr.length - 1) {
                    throw new CommandLineException(CommandLineMessages.getString("CommandLineLauncher.134"), this.gui);
                }
                this.extraParams = strArr[i + 1];
                i++;
            } else if (PARAM_PASSWD.equals(strArr[i])) {
                if (this.password != null) {
                    throw new CommandLineException(CommandLineMessages.getString("CommandLineLauncher.26", strArr[i]), this.gui);
                }
                if (i >= strArr.length - 1) {
                    throw new CommandLineException(CommandLineMessages.getString("CommandLineLauncher.135"), this.gui);
                }
                this.password = strArr[i + 1];
                i++;
            } else if (PARAM_ALIAS.equals(strArr[i])) {
                if (this.alias != null) {
                    throw new CommandLineException(CommandLineMessages.getString("CommandLineLauncher.26", strArr[i]), this.gui);
                }
                if (this.filter != null) {
                    throw new CommandLineException(CommandLineMessages.getString("CommandLineLauncher.28"), this.gui);
                }
                if (i >= strArr.length - 1) {
                    throw new CommandLineException(CommandLineMessages.getString("CommandLineLauncher.136"), this.gui);
                }
                this.alias = strArr[i + 1];
                i++;
            } else if (PARAM_FILTER.equals(strArr[i])) {
                if (this.filter != null) {
                    throw new CommandLineException(CommandLineMessages.getString("CommandLineLauncher.26", strArr[i]), this.gui);
                }
                if (this.alias != null) {
                    throw new CommandLineException(CommandLineMessages.getString("CommandLineLauncher.28"), this.gui);
                }
                if (i >= strArr.length - 1) {
                    throw new CommandLineException(CommandLineMessages.getString("CommandLineLauncher.137"), this.gui);
                }
                this.filter = strArr[i + 1];
                i++;
            } else if (PARAM_INPUT.equals(strArr[i])) {
                if (this.inputFileParam != null) {
                    throw new CommandLineException(CommandLineMessages.getString("CommandLineLauncher.26", strArr[i]), this.gui);
                }
                if (i >= strArr.length - 1) {
                    throw new CommandLineException(CommandLineMessages.getString("CommandLineLauncher.74"), this.gui);
                }
                this.inputFileParam = strArr[i + 1];
                i++;
            } else if (PARAM_FORMAT.equals(strArr[i])) {
                if (this.formatParam != null) {
                    throw new CommandLineException(CommandLineMessages.getString("CommandLineLauncher.26", strArr[i]), this.gui);
                }
                if (i >= strArr.length - 1) {
                    throw new CommandLineException(CommandLineMessages.getString("CommandLineLauncher.138"), this.gui);
                }
                this.formatParam = strArr[i + 1];
                i++;
            } else if (PARAM_HASH_FORMAT.equals(strArr[i])) {
                if (this.hashFormatParam != null) {
                    throw new CommandLineException(CommandLineMessages.getString("CommandLineLauncher.26", strArr[i]), this.gui);
                }
                if (i >= strArr.length - 1) {
                    throw new CommandLineException(CommandLineMessages.getString("CommandLineLauncher.139"), this.gui);
                }
                this.hashFormatParam = strArr[i + 1];
                i++;
            } else if (PARAM_OUTPUT.equals(strArr[i])) {
                if (this.outputFileParam != null) {
                    throw new CommandLineException(CommandLineMessages.getString("CommandLineLauncher.26", strArr[i]), this.gui);
                }
                if (i >= strArr.length - 1) {
                    throw new CommandLineException(CommandLineMessages.getString("CommandLineLauncher.75"), this.gui);
                }
                this.outputFileParam = strArr[i + 1];
                i++;
            } else {
                throw new CommandLineException(CommandLineMessages.getString("CommandLineLauncher.25", strArr[i]), this.gui);
            }
            i++;
        }
        checkParams();
    }

    private void checkParams() throws CommandLineException {
        if (this.formatParam != null) {
            this.format = this.formatParam.toLowerCase();
            if (!this.format.equals(FORMAT_XADES) && !this.format.equals(FORMAT_CADES) && !this.format.equals(FORMAT_PADES) && !this.format.equals(FORMAT_FACTURAE) && !this.format.equals(FORMAT_OOXML) && !this.format.equals(FORMAT_ODF) && !this.format.equals("auto")) {
                throw new CommandLineParameterException(CommandLineMessages.getString("CommandLineLauncher.4", this.formatParam), this.gui);
            }
        }
        if (this.hashFormatParam != null) {
            this.hashFormat = this.hashFormatParam.toLowerCase();
            if (!this.hashFormat.equals("hex") && !this.hashFormat.equals(FORMAT_HASH_FILE_BIN) && !this.hashFormat.equals(FORMAT_HASH_FILE_BASE64) && !this.hashFormat.equals("xml") && !this.hashFormat.equals(FORMAT_HASH_DIR_PLAIN)) {
                throw new CommandLineParameterException(CommandLineMessages.getString("CommandLineLauncher.89", this.hashFormatParam), this.gui);
            }
        }
        if (this.preUrlParam != null) {
            try {
                this.preUrl = new URL(this.preUrlParam);
            } catch (MalformedURLException e) {
                throw new CommandLineParameterException(CommandLineMessages.getString("CommandLineLauncher.59", this.preUrlParam, e.toString()), this.gui);
            }
        }
        if (this.postUrlParam != null) {
            try {
                this.postUrl = new URL(this.postUrlParam);
            } catch (MalformedURLException e2) {
                throw new CommandLineParameterException(CommandLineMessages.getString("CommandLineLauncher.59", this.postUrlParam, e2.toString()), this.gui);
            }
        }
        if (this.inputFileParam != null) {
            this.inputFile = getFile(this.inputFileParam);
            if (!this.inputFile.exists()) {
                throw new CommandLineException(CommandLineMessages.getString("CommandLineLauncher.0", this.inputFileParam), this.gui);
            }
            if (!this.inputFile.canRead()) {
                throw new CommandLineException(CommandLineMessages.getString("CommandLineLauncher.1", this.inputFileParam), this.gui);
            }
        }
        if (this.outputFileParam != null) {
            this.outputFile = getFile(this.outputFileParam);
            File parentFile = this.outputFile.getParentFile();
            if (parentFile != null && !parentFile.canWrite()) {
                throw new CommandLineException(CommandLineMessages.getString("CommandLineLauncher.3", this.outputFileParam), this.gui);
            }
        }
    }

    public URL getPostSignUrl() {
        return this.postUrl;
    }

    public URL getPreSignUrl() {
        return this.preUrl;
    }

    public String getStore() {
        return this.store;
    }

    public String getAlias() {
        return this.alias;
    }

    public String getFilter() {
        return this.filter;
    }

    public File getMainFile() {
        return this.mainFile;
    }

    public File getInputFile() {
        return this.inputFile;
    }

    public File getOutputFile() {
        return this.outputFile;
    }

    public String getFormat() {
        return this.format != null ? this.format : "auto";
    }

    public String getHashFormat() {
        return this.hashFormat;
    }

    String getHashDirectoryFormat() throws CommandLineException {
        String str;
        if (this.hashFormat == null) {
            str = "xml";
        } else if (this.hashFormat.equals("xml") || this.hashFormat.equals(FORMAT_HASH_DIR_PLAIN)) {
            str = this.hashFormat;
        } else {
            throw new CommandLineException(CommandLineMessages.getString("CommandLineLauncher.90", this.hashFormat));
        }
        return str;
    }

    String getHashFileFormat() throws CommandLineException {
        String str;
        if (this.hashFormat == null) {
            str = "hex";
        } else if (this.hashFormat.equals("hex") || this.hashFormat.equals(FORMAT_HASH_FILE_BASE64) || this.hashFormat.equals(FORMAT_HASH_FILE_BIN)) {
            str = this.hashFormat;
        } else {
            throw new CommandLineException(CommandLineMessages.getString("CommandLineLauncher.91", this.hashFormat));
        }
        return str;
    }

    private File getFile(String str) {
        File file;
        if (str.startsWith("~") && Platform.isUnixSystem(this.system)) {
            file = new File(Platform.getUserHome(), str.substring(1));
        } else {
            file = new File(str);
        }
        return file;
    }

    public String getPassword() {
        return this.password;
    }

    public String getMassiveOperation() {
        return this.massiveOp != null ? this.massiveOp : "sign";
    }

    public String getAlgorithm() {
        return this.algorithm != null ? this.algorithm : "sha512";
    }

    public String getExtraParams() {
        return this.extraParams;
    }

    public boolean isXml() {
        return this.xml;
    }

    public boolean isGui() {
        return this.gui;
    }

    public boolean isCertGui() {
        return this.certgui;
    }

    public boolean isRecursive() {
        return this.recursive;
    }

    public boolean isHelp() {
        getClass();
        return false;
    }

    public static String buildSyntaxError(CommandLineCommand commandLineCommand, String str) {
        switch (commandLineCommand) {
            case SIGN:
            case COSIGN:
            case COUNTERSIGN:
                return buildOperationSignSyntaxError(commandLineCommand.getOp(), str);
            case LIST:
                return buildOperationListSyntaxError(commandLineCommand.getOp(), str);
            case VERIFY:
                return buildOperationVerifySyntaxError(commandLineCommand.getOp(), str);
            case BATCHSIGN:
                return buildOperationBatchSignSyntaxError(commandLineCommand.getOp(), str);
            default:
                return str;
        }
    }

    private static String buildOperationSignSyntaxError(String str, String str2) {
        String applicationFilename = DesktopUtil.getApplicationFilename();
        StringBuilder sb = new StringBuilder();
        if (str2 != null) {
            sb.append(str2).append("\n");
        }
        sb.append(CommandLineMessages.getString("CommandLineLauncher.7")).append(": ").append(applicationFilename).append(" ").append(str).append(" [").append(CommandLineMessages.getString("CommandLineLauncher.140")).append("...]\n\n").append(CommandLineMessages.getString("CommandLineLauncher.115")).append(":\n").append("  ").append(PARAM_GUI).append("\t\t\t (").append(CommandLineMessages.getString("CommandLineLauncher.23")).append(" ").append(CommandLineMessages.getString("CommandLineLauncher.146", PARAM_CERT_GUI)).append(")\n").append("  ").append(PARAM_CERT_GUI).append("\t\t (").append(CommandLineMessages.getString("CommandLineLauncher.76")).append(")\n").append("  ").append(PARAM_INPUT).append(" inputfile\t\t (").append(CommandLineMessages.getString("CommandLineLauncher.13")).append(")\n").append("  ").append(PARAM_OUTPUT).append(" outputfile\t\t (").append(CommandLineMessages.getString("CommandLineLauncher.14")).append(")\n").append("  ").append(PARAM_ALGO).append(" algo\t (").append(CommandLineMessages.getString("CommandLineLauncher.20")).append(")\n").append("  \t ").append(ALGO_SHA1).append("\t\t (").append(CommandLineMessages.getString("CommandLineLauncher.142")).append(")\n").append("  \t ").append(ALGO_SHA256).append("\t\t (").append(CommandLineMessages.getString("CommandLineLauncher.143")).append(")\n").append("  \t ").append(ALGO_SHA384).append("\t\t (").append(CommandLineMessages.getString("CommandLineLauncher.144")).append(")\n").append("  \t ").append("sha512").append("\t\t (").append(CommandLineMessages.getString("CommandLineLauncher.145")).append(")\n").append("  ").append(PARAM_FORMAT).append("\t\t (").append(CommandLineMessages.getString("CommandLineLauncher.32")).append(")\n").append("  \t ").append("auto").append("\t\t (").append(CommandLineMessages.getString("CommandLineLauncher.42")).append(")\n").append("  \t ").append(FORMAT_CADES).append("\t\t (").append(CommandLineMessages.getString("CommandLineLauncher.43")).append(")\n").append("  \t ").append(FORMAT_PADES).append("\t\t (").append(CommandLineMessages.getString("CommandLineLauncher.44")).append(")\n").append("  \t ").append(FORMAT_XADES).append("\t\t (").append(CommandLineMessages.getString("CommandLineLauncher.45")).append(")\n").append("  \t ").append(FORMAT_FACTURAE).append("\t (").append(CommandLineMessages.getString("CommandLineLauncher.46")).append(")\n").append("  \t ").append(FORMAT_OOXML).append("\t\t (").append(CommandLineMessages.getString("CommandLineLauncher.72")).append(")\n").append("  \t ").append(FORMAT_ODF).append("\t\t (").append(CommandLineMessages.getString("CommandLineLauncher.73")).append(")\n").append("  ").append(PARAM_CONFIG).append(" extraParams\t (").append(CommandLineMessages.getString("CommandLineLauncher.27")).append(")\n").append("  ").append(PARAM_STORE).append("\t\t (").append(CommandLineMessages.getString("CommandLineLauncher.31")).append(")\n").append("  \t auto\t\t (").append(CommandLineMessages.getString("CommandLineLauncher.36")).append(")\n").append("  \t windows\t (").append(CommandLineMessages.getString("CommandLineLauncher.37")).append(")\n").append("  \t mac\t\t (").append(CommandLineMessages.getString("CommandLineLauncher.38")).append(")\n").append("  \t mozilla\t (").append(CommandLineMessages.getString("CommandLineLauncher.39")).append(")\n").append("  \t dni\t\t (").append(CommandLineMessages.getString("CommandLineLauncher.40")).append(")\n").append("  \t pkcs12:p12file\t (").append(CommandLineMessages.getString("CommandLineLauncher.41")).append(")\n").append("  \t pkcs11:p11file\t (").append(CommandLineMessages.getString("CommandLineLauncher.47")).append(")\n").append("  ").append(PARAM_PASSWD).append(" password\t (").append(CommandLineMessages.getString("CommandLineLauncher.12")).append(")\n").append("  ").append(PARAM_ALIAS).append(" alias\t\t (").append(CommandLineMessages.getString("CommandLineLauncher.16")).append(")\n").append("  ").append(PARAM_FILTER).append(" filter\t (").append(CommandLineMessages.getString("CommandLineLauncher.66")).append(")\n").append("  ").append(PARAM_XML).append("\t\t\t (").append(CommandLineMessages.getString("CommandLineLauncher.18")).append(")");
        return sb.toString();
    }

    private static String buildOperationBatchSignSyntaxError(String str, String str2) {
        String applicationFilename = DesktopUtil.getApplicationFilename();
        StringBuilder sb = new StringBuilder();
        if (str2 != null) {
            sb.append(str2).append("\n");
        }
        sb.append(CommandLineMessages.getString("CommandLineLauncher.7")).append(": ").append(applicationFilename).append(" ").append(str).append(" [").append(CommandLineMessages.getString("CommandLineLauncher.140")).append("...]\n\n").append(CommandLineMessages.getString("CommandLineLauncher.115")).append(":\n").append("  ").append(PARAM_INPUT).append(" inputfile\t\t (").append(CommandLineMessages.getString("CommandLineLauncher.62")).append(")\n").append("  ").append(PARAM_OUTPUT).append(" outputfile\t\t (").append(CommandLineMessages.getString("CommandLineLauncher.63")).append(")\n").append("  ").append(PARAM_STORE).append("\t\t (").append(CommandLineMessages.getString("CommandLineLauncher.31")).append(")\n").append("  \t auto\t\t (").append(CommandLineMessages.getString("CommandLineLauncher.36")).append(")\n").append("  \t windows\t (").append(CommandLineMessages.getString("CommandLineLauncher.37")).append(")\n").append("  \t mac\t\t (").append(CommandLineMessages.getString("CommandLineLauncher.38")).append(")\n").append("  \t mozilla\t (").append(CommandLineMessages.getString("CommandLineLauncher.39")).append(")\n").append("  \t dni\t\t (").append(CommandLineMessages.getString("CommandLineLauncher.40")).append(")\n").append("  \t pkcs12:p12file\t (").append(CommandLineMessages.getString("CommandLineLauncher.41")).append(")\n").append("  \t pkcs11:p11file\t (").append(CommandLineMessages.getString("CommandLineLauncher.47")).append(")\n").append("  ").append(PARAM_PASSWD).append(" password\t (").append(CommandLineMessages.getString("CommandLineLauncher.12")).append(")\n").append("  ").append(PARAM_ALIAS).append(" alias\t\t (").append(CommandLineMessages.getString("CommandLineLauncher.16")).append(")\n").append("  ").append(PARAM_FILTER).append(" filter\t (").append(CommandLineMessages.getString("CommandLineLauncher.66")).append(")\n").append("  ").append(PARAM_PREURL).append(" url\t\t (").append(CommandLineMessages.getString("CommandLineLauncher.64")).append(")\n").append("  ").append(PARAM_POSTURL).append(" url\t\t (").append(CommandLineMessages.getString("CommandLineLauncher.65")).append(")\n").append("  ").append(PARAM_XML).append("\t\t\t (").append(CommandLineMessages.getString("CommandLineLauncher.18")).append(")");
        return sb.toString();
    }

    private static String buildOperationVerifySyntaxError(String str, String str2) {
        String applicationFilename = DesktopUtil.getApplicationFilename();
        StringBuilder sb = new StringBuilder();
        if (str2 != null) {
            sb.append(str2).append("\n");
        }
        sb.append(CommandLineMessages.getString("CommandLineLauncher.7")).append(": ").append(applicationFilename).append(" ").append(str).append(" [").append(CommandLineMessages.getString("CommandLineLauncher.140")).append("...]\n\n").append(CommandLineMessages.getString("CommandLineLauncher.115")).append(":\n").append("  ").append(PARAM_GUI).append(" \t\t (").append(CommandLineMessages.getString("CommandLineLauncher.23")).append(")\n").append("  ").append(PARAM_INPUT).append(" inputfile\t (").append(CommandLineMessages.getString("CommandLineLauncher.13")).append(")");
        return sb.toString();
    }

    private static String buildOperationListSyntaxError(String str, String str2) {
        String applicationFilename = DesktopUtil.getApplicationFilename();
        StringBuilder sb = new StringBuilder();
        if (str2 != null) {
            sb.append(str2).append("\n");
        }
        sb.append(CommandLineMessages.getString("CommandLineLauncher.7")).append(": ").append(applicationFilename).append(" ").append(str).append(" [").append(CommandLineMessages.getString("CommandLineLauncher.140")).append("...]\n\n").append(CommandLineMessages.getString("CommandLineLauncher.115")).append(":\n").append("  ").append(PARAM_STORE).append("\t\t (").append(CommandLineMessages.getString("CommandLineLauncher.31")).append(")\n").append("  \t auto\t\t (").append(CommandLineMessages.getString("CommandLineLauncher.36")).append(")\n").append("  \t windows\t (").append(CommandLineMessages.getString("CommandLineLauncher.37")).append(")\n").append("  \t mac\t\t (").append(CommandLineMessages.getString("CommandLineLauncher.38")).append(")\n").append("  \t mozilla\t (").append(CommandLineMessages.getString("CommandLineLauncher.39")).append(")\n").append("  \t dni\t\t (").append(CommandLineMessages.getString("CommandLineLauncher.40")).append(")\n").append("  \t pkcs12:p12file\t (").append(CommandLineMessages.getString("CommandLineLauncher.41")).append(")\n").append("  ").append(PARAM_PASSWD).append(" password\t (").append(CommandLineMessages.getString("CommandLineLauncher.12")).append(")\n").append("  ").append(PARAM_XML).append("\t\t\t (").append(CommandLineMessages.getString("CommandLineLauncher.18")).append(")");
        return sb.toString();
    }
}
