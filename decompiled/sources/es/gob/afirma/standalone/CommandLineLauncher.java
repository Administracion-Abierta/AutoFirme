package es.gob.afirma.standalone;

import es.gob.afirma.core.AOException;
import es.gob.afirma.core.keystores.CertificateContext;
import es.gob.afirma.core.keystores.KeyStoreManager;
import es.gob.afirma.core.misc.AOUtil;
import es.gob.afirma.core.misc.Base64;
import es.gob.afirma.core.misc.MimeHelper;
import es.gob.afirma.core.misc.Platform;
import es.gob.afirma.core.signers.AOConfigurableContext;
import es.gob.afirma.core.signers.AOSignConstants;
import es.gob.afirma.core.signers.AOSigner;
import es.gob.afirma.core.signers.CounterSignTarget;
import es.gob.afirma.core.ui.AOUIFactory;
import es.gob.afirma.keystores.AOKeyStore;
import es.gob.afirma.keystores.AOKeyStoreDialog;
import es.gob.afirma.keystores.AOKeyStoreManager;
import es.gob.afirma.keystores.AOKeyStoreManagerFactory;
import es.gob.afirma.keystores.AOKeystoreAlternativeException;
import es.gob.afirma.keystores.callbacks.CachePasswordCallback;
import es.gob.afirma.keystores.filters.CertFilterManager;
import es.gob.afirma.signers.batch.client.BatchSigner;
import es.gob.afirma.signers.cades.AOCAdESSigner;
import es.gob.afirma.signers.odf.AOODFSigner;
import es.gob.afirma.signers.ooxml.AOOOXMLSigner;
import es.gob.afirma.signers.pades.AOPDFSigner;
import es.gob.afirma.signers.pades.IncorrectPageException;
import es.gob.afirma.signers.pades.InvalidSignaturePositionException;
import es.gob.afirma.signers.pades.common.PdfExtraParams;
import es.gob.afirma.signers.xades.AOFacturaESigner;
import es.gob.afirma.signers.xades.AOXAdESSigner;
import es.gob.afirma.signvalidation.SignValider;
import es.gob.afirma.signvalidation.SignValiderFactory;
import es.gob.afirma.signvalidation.SignValidity;
import es.gob.afirma.standalone.plugins.AfirmaPlugin;
import es.gob.afirma.standalone.plugins.Permission;
import es.gob.afirma.standalone.plugins.PluginCommand;
import es.gob.afirma.standalone.plugins.PluginControlledException;
import es.gob.afirma.standalone.plugins.PluginInfo;
import es.gob.afirma.standalone.plugins.manager.PermissionChecker;
import es.gob.afirma.standalone.plugins.manager.PluginException;
import es.gob.afirma.standalone.plugins.manager.PluginLoader;
import es.gob.afirma.standalone.plugins.manager.PluginsManager;
import es.gob.afirma.standalone.ui.SignOperationConfig;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.Console;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.net.URL;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableEntryException;
import java.security.cert.CertificateEncodingException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.pdfbox.contentstream.operator.OperatorName;
import org.apache.pdfbox.pdmodel.documentinterchange.taggedpdf.PDPrintFieldAttributeObject;
import org.slf4j.Marker;
import org.slf4j.impl.SimpleLogger;

/* JADX INFO: loaded from: autofirma.jar:es/gob/afirma/standalone/CommandLineLauncher.class */
final class CommandLineLauncher {
    private static final String PARAM_HELP = "-help";
    private static final String EXTRA_PARAM_TARGET = "target";
    private static final String KEY_FILTERS = "filters";
    private static final int STATUS_ERROR = -1;
    private static final int STATUS_SUCCESS = 0;
    private static final String STORE_AUTO = "auto";
    private static final String STORE_MAC = "mac";
    private static final String STORE_WIN = "windows";
    private static final String STORE_P12 = "pkcs12";
    private static final String STORE_NSS = "mozilla";
    private static final String STORE_DNI = "dni";
    private static final String STORE_P11 = "pkcs11";
    private static PluginsManager pluginsManager = null;

    private CommandLineLauncher() {
    }

    static void processCommandLine(String[] strArr) {
        String message;
        String strProcessPluginCommand;
        Console console = System.console();
        PrintWriter printWriterWriter = console != null ? console.writer() : new PrintWriter(System.out);
        Throwable th = null;
        try {
            if (strArr != null) {
                if (strArr.length >= 1 && !PARAM_HELP.equalsIgnoreCase(strArr[0])) {
                    String lowerCase = strArr[0].toLowerCase();
                    boolean zCheckXmlResponseNeeded = checkXmlResponseNeeded(strArr);
                    CommandLineCommand commandLineCommand = CommandLineCommand.parse(lowerCase);
                    try {
                        if (commandLineCommand != null) {
                            strProcessPluginCommand = processCommand(commandLineCommand, strArr);
                        } else {
                            PluginCommand pluginCommand = getPluginCommand(strArr[0].toLowerCase());
                            if (pluginCommand == null) {
                                throw new UnsupportedOperationException(buildGeneralSyntax(CommandLineMessages.getString("CommandLineLauncher.15", strArr[0])));
                            }
                            strProcessPluginCommand = processPluginCommand(pluginCommand, strArr);
                        }
                        printMessage(printWriterWriter, strProcessPluginCommand);
                        if (strProcessPluginCommand != null) {
                            System.exit(0);
                        }
                        if (printWriterWriter != null) {
                            if (0 == 0) {
                                printWriterWriter.close();
                                return;
                            }
                            try {
                                printWriterWriter.close();
                                return;
                            } catch (Throwable th2) {
                                th.addSuppressed(th2);
                                return;
                            }
                        }
                        return;
                    } catch (CommandLineParameterException e) {
                        if (e.isUsingGui()) {
                            showErrorDialog(e.getMessage(), e);
                        }
                        closeApp(-1, printWriterWriter, CommandLineParameters.buildSyntaxError(commandLineCommand, e.getMessage()));
                        if (printWriterWriter != null) {
                            if (0 == 0) {
                                printWriterWriter.close();
                                return;
                            }
                            try {
                                printWriterWriter.close();
                                return;
                            } catch (Throwable th3) {
                                th.addSuppressed(th3);
                                return;
                            }
                        }
                        return;
                    } catch (CommandLineException e2) {
                        if (e2.isUsingGui()) {
                            showErrorDialog(e2.getMessage(), e2);
                        }
                        Throwable cause = e2.getCause();
                        if (cause == null || !(cause instanceof AOKeystoreAlternativeException)) {
                            message = cause != null ? cause.getMessage() : e2.getMessage();
                        } else {
                            message = CommandLineMessages.getString("CommandLineLauncher.49", cause.getMessage());
                        }
                        if (zCheckXmlResponseNeeded) {
                            message = buildXmlResponse(false, message, null);
                        }
                        closeApp(-1, printWriterWriter, message);
                        if (printWriterWriter != null) {
                            if (0 == 0) {
                                printWriterWriter.close();
                                return;
                            }
                            try {
                                printWriterWriter.close();
                                return;
                            } catch (Throwable th4) {
                                th.addSuppressed(th4);
                                return;
                            }
                        }
                        return;
                    } catch (PluginControlledException | UnsupportedOperationException e3) {
                        String message2 = e3.getMessage();
                        if (zCheckXmlResponseNeeded) {
                            message2 = buildXmlResponse(false, message2, null);
                        }
                        closeApp(-1, printWriterWriter, message2);
                        if (printWriterWriter != null) {
                            if (0 == 0) {
                                printWriterWriter.close();
                                return;
                            }
                            try {
                                printWriterWriter.close();
                                return;
                            } catch (Throwable th5) {
                                th.addSuppressed(th5);
                                return;
                            }
                        }
                        return;
                    } catch (Exception e4) {
                        String string = CommandLineMessages.getString("CommandLineLauncher.50", e4.getMessage());
                        if (zCheckXmlResponseNeeded) {
                            string = buildXmlResponse(false, string, null);
                        }
                        closeApp(-1, printWriterWriter, string);
                        if (printWriterWriter != null) {
                            if (0 == 0) {
                                printWriterWriter.close();
                                return;
                            }
                            try {
                                printWriterWriter.close();
                                return;
                            } catch (Throwable th6) {
                                th.addSuppressed(th6);
                                return;
                            }
                        }
                        return;
                    }
                }
            }
            closeApp(0, printWriterWriter, buildGeneralSyntax(null));
            if (printWriterWriter != null) {
                if (0 == 0) {
                    printWriterWriter.close();
                    return;
                }
                try {
                    printWriterWriter.close();
                } catch (Throwable th7) {
                    th.addSuppressed(th7);
                }
            }
        } catch (Throwable th8) {
            if (printWriterWriter != null) {
                if (0 != 0) {
                    try {
                        printWriterWriter.close();
                    } catch (Throwable th9) {
                        th.addSuppressed(th9);
                    }
                } else {
                    printWriterWriter.close();
                }
            }
            throw th8;
        }
    }

    private static boolean checkXmlResponseNeeded(String[] strArr) {
        boolean z = false;
        for (int i = 1; i < strArr.length && !z; i++) {
            if (CommandLineParameters.PARAM_XML.equalsIgnoreCase(strArr[i])) {
                z = true;
            }
        }
        return z;
    }

    private static String processCommand(CommandLineCommand commandLineCommand, String[] strArr) throws UnsupportedOperationException, CommandLineException {
        if (strArr.length > 1 && PARAM_HELP.equalsIgnoreCase(strArr[1])) {
            return CommandLineParameters.buildSyntaxError(commandLineCommand, null);
        }
        CommandLineParameters commandLineParameters = new CommandLineParameters(commandLineCommand, strArr);
        String strSignByCommandLine = null;
        switch (commandLineCommand) {
            case LIST:
                strSignByCommandLine = listAliasesByCommandLine(commandLineParameters);
                break;
            case VERIFY:
                if (commandLineParameters.isGui()) {
                    verifyByGui(commandLineParameters);
                } else {
                    strSignByCommandLine = verifyByCommandLine(commandLineParameters);
                }
                break;
            case SIGN:
                if (commandLineParameters.isGui()) {
                    signByGui(commandLineCommand, commandLineParameters);
                } else {
                    strSignByCommandLine = signByCommandLine(commandLineCommand, commandLineParameters);
                }
                break;
            case COSIGN:
            case COUNTERSIGN:
                strSignByCommandLine = signByCommandLine(commandLineCommand, commandLineParameters);
                break;
            case BATCHSIGN:
                batchByCommandLine(commandLineParameters);
                break;
            default:
                throw new UnsupportedOperationException(CommandLineMessages.getString("CommandLineLauncher.54", commandLineCommand.getOp()));
        }
        return strSignByCommandLine;
    }

    private static PluginCommand[] getPluginCommands() throws PluginException {
        ArrayList arrayList = new ArrayList();
        Iterator<AfirmaPlugin> it = pluginsManager.getPluginsLoadedList().iterator();
        while (it.hasNext()) {
            PluginCommand[] commands = it.next().getInfo().getCommands();
            if (commands != null) {
                for (PluginCommand pluginCommand : commands) {
                    arrayList.add(pluginCommand);
                }
            }
        }
        return (PluginCommand[]) arrayList.toArray(new PluginCommand[0]);
    }

    private static PluginCommand getPluginCommand(String str) throws PluginException {
        PluginCommand[] commands;
        Iterator<AfirmaPlugin> it = pluginsManager.getPluginsLoadedList().iterator();
        while (it.hasNext()) {
            PluginInfo info = it.next().getInfo();
            if (PermissionChecker.check(info, Permission.COMMANDS) && (commands = info.getCommands()) != null) {
                for (PluginCommand pluginCommand : commands) {
                    if (pluginCommand.getCommand().equalsIgnoreCase(str)) {
                        return pluginCommand;
                    }
                }
            }
        }
        return null;
    }

    private static String processPluginCommand(PluginCommand pluginCommand, String[] strArr) throws PluginControlledException, IllegalArgumentException {
        try {
            return PluginLoader.getPluginCommandAction(pluginCommand.getCommandActionClass()).start(strArr);
        } catch (PluginException e) {
            throw new PluginControlledException("No se pudo cargar la accion del plugin", e);
        }
    }

    private static void deactivateConsoleLog(String str) {
        Logger.getLogger(str).setLevel(Level.OFF);
    }

    private static void deactivateSlf4JLogs() {
        System.setProperty(SimpleLogger.DEFAULT_LOG_LEVEL_KEY, PDPrintFieldAttributeObject.CHECKED_STATE_OFF);
    }

    private static void verifyByGui(CommandLineParameters commandLineParameters) throws CommandLineException {
        File inputFile = commandLineParameters.getInputFile();
        if (inputFile == null) {
            throw new CommandLineException(CommandLineMessages.getString("CommandLineLauncher.5"), true);
        }
        new VisorFirma(true, null).initialize(false, inputFile);
    }

    private static void signByGui(CommandLineCommand commandLineCommand, CommandLineParameters commandLineParameters) throws CommandLineException {
        File inputFile = commandLineParameters.getInputFile();
        if (inputFile == null) {
            throw new CommandLineException(CommandLineMessages.getString("CommandLineLauncher.5"), true);
        }
        try {
            new SimpleAfirma().initGUI(inputFile, loadSignConfig(commandLineCommand, commandLineParameters));
        } catch (IOException e) {
            throw new CommandLineException(e.getMessage(), e);
        }
    }

    private static SignOperationConfig loadSignConfig(CommandLineCommand commandLineCommand, CommandLineParameters commandLineParameters) throws IOException, CommandLineParameterException {
        SignOperationConfig signOperationConfig = new SignOperationConfig();
        switch (commandLineCommand) {
            case SIGN:
                signOperationConfig.setCryptoOperation(SignOperationConfig.CryptoOperation.SIGN);
                break;
            case COSIGN:
                signOperationConfig.setCryptoOperation(SignOperationConfig.CryptoOperation.COSIGN);
                break;
            case COUNTERSIGN:
                signOperationConfig.setCryptoOperation(SignOperationConfig.CryptoOperation.COUNTERSIGN_LEAFS);
                break;
            default:
                signOperationConfig.setCryptoOperation(SignOperationConfig.CryptoOperation.SIGN);
                break;
        }
        if (!"auto".equals(commandLineParameters.getFormat())) {
            try {
                String strSelectFormatByData = selectFormatByData(loadFile(commandLineParameters.getInputFile()));
                try {
                    signOperationConfig.setSigner(getSigner(strSelectFormatByData));
                } catch (Exception e) {
                    throw new CommandLineParameterException(CommandLineMessages.getString("CommandLineLauncher.4", strSelectFormatByData), e);
                }
            } catch (Exception e2) {
                throw new IOException("No se ha podido leer el fichero de entrada: " + commandLineParameters.getInputFile().getAbsolutePath(), e2);
            }
        }
        if (commandLineParameters.getExtraParams() != null) {
            Properties properties = new Properties();
            for (String str : commandLineParameters.getExtraParams().split("\n")) {
                String strTrim = str.trim();
                int iIndexOf = strTrim.indexOf("=");
                if (iIndexOf > 0 && !strTrim.startsWith("#")) {
                    properties.setProperty(strTrim.substring(0, iIndexOf).trim(), strTrim.substring(iIndexOf + 1).trim());
                }
            }
            signOperationConfig.setExtraParams(properties);
        }
        if (commandLineParameters.getAlgorithm() != null) {
            signOperationConfig.setDigestAlgorithm(AOSignConstants.getDigestAlgorithmName(commandLineParameters.getAlgorithm()));
        }
        return signOperationConfig;
    }

    /* JADX WARN: Not initialized variable reg: 17, insn: 0x015d: MOVE (r0 I:??[int, float, boolean, short, byte, char, OBJECT, ARRAY]) = (r17 I:??[int, float, boolean, short, byte, char, OBJECT, ARRAY]) A[TRY_LEAVE], block:B:67:0x015d */
    /* JADX WARN: Not initialized variable reg: 18, insn: 0x0162: MOVE (r0 I:??[int, float, boolean, short, byte, char, OBJECT, ARRAY]) = (r18 I:??[int, float, boolean, short, byte, char, OBJECT, ARRAY]), block:B:69:0x0162 */
    private static String batchByCommandLine(CommandLineParameters commandLineParameters) throws CommandLineException {
        InputStream inputStream;
        Throwable th;
        File inputFile = commandLineParameters.getInputFile();
        if (inputFile == null) {
            throw new CommandLineParameterException(CommandLineMessages.getString("CommandLineLauncher.5"));
        }
        String alias = commandLineParameters.getAlias();
        if (alias == null && commandLineParameters.getFilter() == null) {
            throw new CommandLineParameterException(CommandLineMessages.getString("CommandLineLauncher.17"));
        }
        File outputFile = commandLineParameters.getOutputFile();
        if (outputFile == null && !commandLineParameters.isXml()) {
            throw new CommandLineParameterException(CommandLineMessages.getString("CommandLineLauncher.19"));
        }
        URL preSignUrl = commandLineParameters.getPreSignUrl();
        URL postSignUrl = commandLineParameters.getPostSignUrl();
        if (preSignUrl == null || postSignUrl == null) {
            throw new CommandLineParameterException(CommandLineMessages.getString("CommandLineLauncher.60"));
        }
        try {
            AOKeyStoreManager ksm = getKsm(commandLineParameters.getStore(), commandLineParameters.getPassword());
            if (commandLineParameters.getFilter() != null) {
                alias = filterCertificates(ksm, commandLineParameters.getFilter());
            }
            KeyStore.PrivateKeyEntry keyEntry = ksm.getKeyEntry(alias);
            if (keyEntry == null) {
                throw new CommandLineException(CommandLineMessages.getString("CommandLineLauncher.61", alias));
            }
            try {
                FileInputStream fileInputStream = new FileInputStream(inputFile);
                Throwable th2 = null;
                BufferedInputStream bufferedInputStream = new BufferedInputStream(fileInputStream);
                Throwable th3 = null;
                try {
                    try {
                        byte[] dataFromInputStream = AOUtil.getDataFromInputStream(bufferedInputStream);
                        if (bufferedInputStream != null) {
                            if (0 != 0) {
                                try {
                                    bufferedInputStream.close();
                                } catch (Throwable th4) {
                                    th3.addSuppressed(th4);
                                }
                            } else {
                                bufferedInputStream.close();
                            }
                        }
                        if (fileInputStream != null) {
                            if (0 != 0) {
                                try {
                                    fileInputStream.close();
                                } catch (Throwable th5) {
                                    th2.addSuppressed(th5);
                                }
                            } else {
                                fileInputStream.close();
                            }
                        }
                        String strSignXML = BatchSigner.signXML(!Base64.isBase64(dataFromInputStream) ? Base64.encode(dataFromInputStream, true) : new String(dataFromInputStream).replace(Marker.ANY_NON_NULL_MARKER, "-").replace("/", "_"), preSignUrl.toString(), postSignUrl.toString(), keyEntry.getCertificateChain(), keyEntry.getPrivateKey(), buildProperties(commandLineParameters.getExtraParams()));
                        FileOutputStream fileOutputStream = new FileOutputStream(outputFile);
                        Throwable th6 = null;
                        try {
                            BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(fileOutputStream);
                            Throwable th7 = null;
                            try {
                                try {
                                    fileOutputStream.write(strSignXML.getBytes());
                                    fileOutputStream.flush();
                                    if (bufferedOutputStream != null) {
                                        if (0 != 0) {
                                            try {
                                                bufferedOutputStream.close();
                                            } catch (Throwable th8) {
                                                th7.addSuppressed(th8);
                                            }
                                        } else {
                                            bufferedOutputStream.close();
                                        }
                                    }
                                    if (fileOutputStream != null) {
                                        if (0 != 0) {
                                            try {
                                                fileOutputStream.close();
                                            } catch (Throwable th9) {
                                                th6.addSuppressed(th9);
                                            }
                                        } else {
                                            fileOutputStream.close();
                                        }
                                    }
                                    String string = CommandLineMessages.getString("CommandLineLauncher.22");
                                    if (commandLineParameters.isXml()) {
                                        return commandLineParameters.getOutputFile() != null ? buildXmlResponse(true, string, null) : buildXmlResponse(true, string, strSignXML.getBytes());
                                    }
                                    return string;
                                } catch (Throwable th10) {
                                    th7 = th10;
                                    throw th10;
                                }
                            } catch (Throwable th11) {
                                if (bufferedOutputStream != null) {
                                    if (th7 != null) {
                                        try {
                                            bufferedOutputStream.close();
                                        } catch (Throwable th12) {
                                            th7.addSuppressed(th12);
                                        }
                                    } else {
                                        bufferedOutputStream.close();
                                    }
                                }
                                throw th11;
                            }
                        } catch (Throwable th13) {
                            if (fileOutputStream != null) {
                                if (0 != 0) {
                                    try {
                                        fileOutputStream.close();
                                    } catch (Throwable th14) {
                                        th6.addSuppressed(th14);
                                    }
                                } else {
                                    fileOutputStream.close();
                                }
                            }
                            throw th13;
                        }
                    } catch (Throwable th15) {
                        th3 = th15;
                        throw th15;
                    }
                } catch (Throwable th16) {
                    if (bufferedInputStream != null) {
                        if (th3 != null) {
                            try {
                                bufferedInputStream.close();
                            } catch (Throwable th17) {
                                th3.addSuppressed(th17);
                            }
                        } else {
                            bufferedInputStream.close();
                        }
                    }
                    throw th16;
                }
            } catch (Throwable th18) {
                if (inputStream != null) {
                    if (th != null) {
                        try {
                            inputStream.close();
                        } catch (Throwable th19) {
                            th.addSuppressed(th19);
                        }
                    } else {
                        inputStream.close();
                    }
                }
                throw th18;
            }
        } catch (AOException | AOKeystoreAlternativeException | IOException | KeyStoreException | NoSuchAlgorithmException | UnrecoverableEntryException | CertificateEncodingException e) {
            return commandLineParameters.isXml() ? buildXmlResponse(false, e.getMessage(), null) : e.getMessage();
        }
    }

    private static String verifyByCommandLine(CommandLineParameters commandLineParameters) throws CommandLineException {
        if (commandLineParameters.getInputFile() == null) {
            throw new CommandLineParameterException(CommandLineMessages.getString("CommandLineLauncher.5"));
        }
        try {
            byte[] bArrLoadFile = loadFile(commandLineParameters.getInputFile());
            List<SignValidity> arrayList = new ArrayList();
            try {
                SignValider signValider = SignValiderFactory.getSignValider(bArrLoadFile);
                if (signValider != null) {
                    arrayList = signValider.validate(bArrLoadFile);
                } else if (DataAnalizerUtil.isSignedODF(bArrLoadFile)) {
                    arrayList.add(new SignValidity(SignValidity.SIGN_DETAIL_TYPE.UNKNOWN, SignValidity.VALIDITY_ERROR.ODF_UNKOWN_VALIDITY));
                } else if (DataAnalizerUtil.isSignedOOXML(bArrLoadFile)) {
                    arrayList.add(new SignValidity(SignValidity.SIGN_DETAIL_TYPE.UNKNOWN, SignValidity.VALIDITY_ERROR.OOXML_UNKOWN_VALIDITY));
                }
                if (arrayList.size() == 0) {
                    arrayList.add(new SignValidity(SignValidity.SIGN_DETAIL_TYPE.KO, SignValidity.VALIDITY_ERROR.UNKOWN_SIGNATURE_FORMAT));
                }
                StringBuilder sb = new StringBuilder();
                Iterator<SignValidity> it = arrayList.iterator();
                while (it.hasNext()) {
                    sb.append(it.next().toString()).append("\n");
                }
                return sb.toString();
            } catch (Exception e) {
                throw new CommandLineException("Error en la validacion de la firma", e);
            }
        } catch (Exception e2) {
            throw new CommandLineException("No se ha podido leer el fichero de entrada: " + commandLineParameters.getInputFile().getAbsolutePath(), e2);
        }
    }

    private static String signByCommandLine(CommandLineCommand commandLineCommand, CommandLineParameters commandLineParameters) throws CommandLineException {
        String alias;
        if (commandLineParameters.getInputFile() == null) {
            throw new CommandLineParameterException(CommandLineMessages.getString("CommandLineLauncher.5"));
        }
        if (commandLineParameters.getAlias() == null && commandLineParameters.getFilter() == null && !commandLineParameters.isCertGui()) {
            throw new CommandLineParameterException(CommandLineMessages.getString("CommandLineLauncher.17"));
        }
        if (commandLineParameters.getOutputFile() == null && !commandLineParameters.isXml()) {
            throw new CommandLineParameterException(CommandLineMessages.getString("CommandLineLauncher.19"));
        }
        try {
            AOKeyStoreManager ksm = getKsm(commandLineParameters.getStore(), commandLineParameters.getPassword());
            if (commandLineParameters.isCertGui()) {
                Properties propertiesBuildProperties = buildProperties(commandLineParameters.getExtraParams());
                if (commandLineParameters.getFilter() != null && !commandLineParameters.getFilter().isEmpty()) {
                    propertiesBuildProperties.put(KEY_FILTERS, commandLineParameters.getFilter());
                }
                CertFilterManager certFilterManager = new CertFilterManager(propertiesBuildProperties);
                AOKeyStoreDialog aOKeyStoreDialog = new AOKeyStoreDialog(ksm, null, true, true, false, certFilterManager.getFilters(), false);
                aOKeyStoreDialog.allowOpenExternalStores(certFilterManager.isExternalStoresOpeningAllowed());
                aOKeyStoreDialog.show();
                CertificateContext selectedCertificateContext = aOKeyStoreDialog.getSelectedCertificateContext();
                KeyStoreManager keyStoreManager = selectedCertificateContext.getKeyStoreManager();
                if (keyStoreManager instanceof AOKeyStoreManager) {
                    ksm = (AOKeyStoreManager) keyStoreManager;
                }
                alias = selectedCertificateContext.getAlias();
            } else {
                alias = commandLineParameters.getAlias();
                if (commandLineParameters.getFilter() != null) {
                    alias = filterCertificates(ksm, commandLineParameters.getFilter());
                }
            }
            byte[] bArrSign = sign(commandLineCommand, commandLineParameters.getFormat(), commandLineParameters.getAlgorithm(), commandLineParameters.getExtraParams(), commandLineParameters.getInputFile(), alias, ksm, commandLineParameters.getPassword());
            if (commandLineParameters.getOutputFile() != null) {
                try {
                    FileOutputStream fileOutputStream = new FileOutputStream(commandLineParameters.getOutputFile());
                    Throwable th = null;
                    try {
                        try {
                            fileOutputStream.write(bArrSign);
                            if (fileOutputStream != null) {
                                if (0 != 0) {
                                    try {
                                        fileOutputStream.close();
                                    } catch (Throwable th2) {
                                        th.addSuppressed(th2);
                                    }
                                } else {
                                    fileOutputStream.close();
                                }
                            }
                        } catch (Throwable th3) {
                            th = th3;
                            throw th3;
                        }
                    } catch (Throwable th4) {
                        if (fileOutputStream != null) {
                            if (th != null) {
                                try {
                                    fileOutputStream.close();
                                } catch (Throwable th5) {
                                    th.addSuppressed(th5);
                                }
                            } else {
                                fileOutputStream.close();
                            }
                        }
                        throw th4;
                    }
                } catch (Exception e) {
                    throw new CommandLineException(CommandLineMessages.getString("CommandLineLauncher.21", commandLineParameters.getOutputFile().getAbsolutePath()), e);
                }
            }
            String string = CommandLineMessages.getString("CommandLineLauncher.22");
            if (commandLineParameters.isXml()) {
                if (commandLineParameters.getOutputFile() != null) {
                    return buildXmlResponse(true, string, null);
                }
                return buildXmlResponse(true, string, bArrSign);
            }
            return string;
        } catch (AOException | AOKeystoreAlternativeException | IOException e2) {
            throw new CommandLineException(e2.getMessage(), e2);
        }
    }

    private static String filterCertificates(AOKeyStoreManager aOKeyStoreManager, String str) throws CommandLineException {
        Properties properties = new Properties();
        properties.setProperty(KEY_FILTERS, str);
        String[] strArrMatches = new CertFilterManager(properties).getFilters().get(0).matches(aOKeyStoreManager.getAliases(), aOKeyStoreManager);
        if (strArrMatches == null || strArrMatches.length == 0) {
            throw new CommandLineException(CommandLineMessages.getString("CommandLineLauncher.52"));
        }
        if (strArrMatches.length > 1) {
            throw new CommandLineException(CommandLineMessages.getString("CommandLineLauncher.53"));
        }
        return strArrMatches[0];
    }

    private static byte[] sign(CommandLineCommand commandLineCommand, String str, String str2, String str3, File file, String str4, AOKeyStoreManager aOKeyStoreManager, String str5) throws CommandLineException, IOException, AOException {
        byte[] bArrSign;
        aOKeyStoreManager.setEntryPasswordCallBack(new CachePasswordCallback(str5 != null ? str5.toCharArray() : "dummy".toCharArray()));
        try {
            KeyStore.PrivateKeyEntry keyEntry = aOKeyStoreManager.getKeyEntry(str4);
            if (keyEntry == null) {
                throw new AOException("No se hay ninguna entrada en el almacen con el alias indicado: " + str4);
            }
            try {
                byte[] bArrLoadFile = loadFile(file);
                String strSelectFormatByData = str;
                if ("auto".equals(str)) {
                    strSelectFormatByData = selectFormatByData(bArrLoadFile);
                }
                Properties propertiesBuildProperties = buildProperties(str3);
                try {
                    AOSigner signer = getSigner(strSelectFormatByData);
                    if (signer instanceof AOConfigurableContext) {
                        ((AOConfigurableContext) signer).setSecureMode(false);
                    }
                    String algorithm = keyEntry.getPrivateKey().getAlgorithm();
                    try {
                        String strComposeSignatureAlgorithmName = AOSignConstants.composeSignatureAlgorithmName(str2, algorithm);
                        try {
                            if (commandLineCommand == CommandLineCommand.SIGN) {
                                bArrSign = signer.sign(bArrLoadFile, strComposeSignatureAlgorithmName, keyEntry.getPrivateKey(), keyEntry.getCertificateChain(), propertiesBuildProperties);
                            } else if (commandLineCommand == CommandLineCommand.COSIGN) {
                                bArrSign = signer.cosign(bArrLoadFile, strComposeSignatureAlgorithmName, keyEntry.getPrivateKey(), keyEntry.getCertificateChain(), propertiesBuildProperties);
                            } else if (commandLineCommand == CommandLineCommand.COUNTERSIGN) {
                                CounterSignTarget counterSignTarget = CounterSignTarget.LEAFS;
                                if (propertiesBuildProperties != null && propertiesBuildProperties.containsKey(EXTRA_PARAM_TARGET) && CounterSignTarget.TREE.name().equalsIgnoreCase(propertiesBuildProperties.getProperty(EXTRA_PARAM_TARGET))) {
                                    counterSignTarget = CounterSignTarget.TREE;
                                }
                                bArrSign = signer.countersign(bArrLoadFile, strComposeSignatureAlgorithmName, counterSignTarget, null, keyEntry.getPrivateKey(), keyEntry.getCertificateChain(), propertiesBuildProperties);
                            } else {
                                throw new CommandLineParameterException("Operacion no soportada: " + commandLineCommand.getOp());
                            }
                        } catch (IncorrectPageException | InvalidSignaturePositionException e) {
                            bArrSign = sign(commandLineCommand, str, strComposeSignatureAlgorithmName, removeSignaturePageProperties(str3), file, str4, aOKeyStoreManager, str5);
                        } catch (Exception e2) {
                            throw new AOException("Error en la operacion de firma: " + e2.getMessage(), e2);
                        }
                        return bArrSign;
                    } catch (Exception e3) {
                        throw new CommandLineParameterException(CommandLineMessages.getString("CommandLineLauncher.141", algorithm), e3);
                    }
                } catch (Exception e4) {
                    throw new CommandLineParameterException(CommandLineMessages.getString("CommandLineLauncher.4", strSelectFormatByData), e4);
                }
            } catch (Exception e5) {
                throw new IOException("No se ha podido leer el fichero de entrada: " + file.getAbsolutePath(), e5);
            }
        } catch (Exception e6) {
            throw new AOException("No se ha podido obtener la referencia a la clave privada: " + e6, e6);
        }
    }

    private static byte[] loadFile(File file) throws IOException {
        FileInputStream fileInputStream = new FileInputStream(file);
        Throwable th = null;
        try {
            try {
                byte[] dataFromInputStream = AOUtil.getDataFromInputStream(fileInputStream);
                if (fileInputStream != null) {
                    if (0 != 0) {
                        try {
                            fileInputStream.close();
                        } catch (Throwable th2) {
                            th.addSuppressed(th2);
                        }
                    } else {
                        fileInputStream.close();
                    }
                }
                return dataFromInputStream;
            } catch (Throwable th3) {
                th = th3;
                throw th3;
            }
        } catch (Throwable th4) {
            if (fileInputStream != null) {
                if (th != null) {
                    try {
                        fileInputStream.close();
                    } catch (Throwable th5) {
                        th.addSuppressed(th5);
                    }
                } else {
                    fileInputStream.close();
                }
            }
            throw th4;
        }
    }

    private static String selectFormatByData(byte[] bArr) throws IOException {
        String str;
        String extension = new MimeHelper(bArr).getExtension();
        if ("pdf".equals(extension)) {
            str = CommandLineParameters.FORMAT_PADES;
        } else if ("xml".equals(extension)) {
            str = CommandLineParameters.FORMAT_XADES;
        } else {
            str = CommandLineParameters.FORMAT_CADES;
        }
        return str;
    }

    private static AOSigner getSigner(String str) {
        AOSigner aOODFSigner;
        if (CommandLineParameters.FORMAT_CADES.equals(str)) {
            aOODFSigner = new AOCAdESSigner();
        } else if (CommandLineParameters.FORMAT_XADES.equals(str)) {
            aOODFSigner = new AOXAdESSigner();
        } else if (CommandLineParameters.FORMAT_PADES.equals(str)) {
            aOODFSigner = new AOPDFSigner();
        } else if (CommandLineParameters.FORMAT_FACTURAE.equals(str)) {
            aOODFSigner = new AOFacturaESigner();
        } else if (CommandLineParameters.FORMAT_OOXML.equals(str)) {
            aOODFSigner = new AOOOXMLSigner();
        } else if (CommandLineParameters.FORMAT_ODF.equals(str)) {
            aOODFSigner = new AOODFSigner();
        } else {
            throw new IllegalArgumentException("Formato de firma no soportado: " + str);
        }
        return aOODFSigner;
    }

    private static String listAliasesByCommandLine(CommandLineParameters commandLineParameters) throws CommandLineException {
        try {
            String[] aliases = getKsm(commandLineParameters.getStore(), commandLineParameters.getPassword()).getAliases();
            StringBuilder sb = new StringBuilder();
            if (commandLineParameters.isXml()) {
                sb.append("<afirma><result>ok</result><response>");
                for (String str : aliases) {
                    sb.append("<alias>").append(str).append("</alias>");
                }
                sb.append("</response></afirma>");
            } else {
                for (String str2 : aliases) {
                    sb.append(str2).append('\n');
                }
            }
            return sb.toString();
        } catch (AOKeystoreAlternativeException | IOException e) {
            throw new CommandLineException(e.getMessage(), e);
        }
    }

    private static AOKeyStoreManager getKsm(String str, String str2) throws AOKeystoreAlternativeException, CommandLineException, IOException {
        AOKeyStore aOKeyStore;
        String absolutePath = null;
        if ("auto".equals(str) || str == null) {
            if (Platform.OS.MACOSX.equals(Platform.getOS())) {
                aOKeyStore = AOKeyStore.APPLE;
            } else if (Platform.OS.WINDOWS.equals(Platform.getOS())) {
                aOKeyStore = AOKeyStore.WINDOWS;
            } else {
                aOKeyStore = AOKeyStore.SHARED_NSS;
            }
        } else if (STORE_MAC.equals(str)) {
            aOKeyStore = AOKeyStore.APPLE;
        } else if (STORE_WIN.equals(str)) {
            aOKeyStore = AOKeyStore.WINDOWS;
        } else if (STORE_NSS.equals(str)) {
            aOKeyStore = AOKeyStore.MOZ_UNI;
        } else if (STORE_DNI.equals(str) || "dnie".equals(str)) {
            aOKeyStore = AOKeyStore.DNIEJAVA;
        } else if (str.startsWith("pkcs12:")) {
            aOKeyStore = AOKeyStore.PKCS12;
            String strReplace = str.replace("pkcs12:", "");
            File fileCleanupPathFile = cleanupPathFile(strReplace);
            if (!fileCleanupPathFile.exists()) {
                throw new IOException("El fichero PKCS#12 indicado no existe: " + strReplace);
            }
            if (!fileCleanupPathFile.canRead()) {
                throw new IOException("No se tienen permisos de lectura para el fichero PKCS#12 indicado: " + strReplace);
            }
            absolutePath = strReplace;
        } else if (str.startsWith("pkcs11:")) {
            aOKeyStore = AOKeyStore.PKCS11;
            String strReplace2 = str.replace("pkcs11:", "");
            File fileCleanupPathFile2 = cleanupPathFile(strReplace2);
            if (!fileCleanupPathFile2.exists()) {
                throw new IOException("La biblioteca PKCS#11 indicada no existe: " + strReplace2);
            }
            if (!fileCleanupPathFile2.canRead()) {
                throw new IOException("No se tienen permisos de lectura para la biblioteca PKCS#11 indicada: " + strReplace2);
            }
            absolutePath = fileCleanupPathFile2.getAbsolutePath();
        } else {
            throw new CommandLineParameterException(CommandLineMessages.getString("CommandLineLauncher.48", str));
        }
        return AOKeyStoreManagerFactory.getAOKeyStoreManager(aOKeyStore, absolutePath, "CommandLine", str2 != null ? new CachePasswordCallback(str2.toCharArray()) : null, null);
    }

    private static File cleanupPathFile(String str) {
        File canonicalFile;
        try {
            canonicalFile = new File(str.trim().replace(OperatorName.SHOW_TEXT_LINE_AND_SPACE, "").replace(OperatorName.SHOW_TEXT_LINE, "")).getCanonicalFile();
        } catch (Exception e) {
            canonicalFile = null;
        }
        return canonicalFile;
    }

    private static String buildGeneralSyntax(String str) {
        PluginCommand[] pluginCommands;
        StringBuilder sb = new StringBuilder();
        if (str != null) {
            sb.append(str).append("\n");
        } else {
            sb.append(CommandLineMessages.getString("CommandLineLauncher.34")).append("\n\n");
        }
        String applicationFilename = DesktopUtil.getApplicationFilename();
        sb.append(CommandLineMessages.getString("CommandLineLauncher.7")).append(": ").append(applicationFilename).append(" cmd [").append(CommandLineMessages.getString("CommandLineLauncher.140")).append("...]\n\n").append(CommandLineMessages.getString("CommandLineLauncher.33")).append(" cmd:\n\n").append("  ").append(CommandLineCommand.SIGN.getOp()).append("\t\t (").append(CommandLineMessages.getString("CommandLineLauncher.8")).append(")\n").append("  ").append(CommandLineCommand.COSIGN.getOp()).append("\t (").append(CommandLineMessages.getString("CommandLineLauncher.9")).append(")\n").append("  ").append(CommandLineCommand.COUNTERSIGN.getOp()).append("\t (").append(CommandLineMessages.getString("CommandLineLauncher.10")).append(")\n").append("  ").append(CommandLineCommand.LIST.getOp()).append("\t (").append(CommandLineMessages.getString("CommandLineLauncher.11")).append(")\n").append("  ").append(CommandLineCommand.VERIFY.getOp()).append("\t (").append(CommandLineMessages.getString("CommandLineLauncher.29")).append(")\n").append("  ").append(CommandLineCommand.BATCHSIGN.getOp()).append("\t (").append(CommandLineMessages.getString("CommandLineLauncher.69")).append(")\n\n");
        try {
            pluginCommands = getPluginCommands();
        } catch (Exception e) {
            sb.append(CommandLineMessages.getString("CommandLineLauncher.125"));
            pluginCommands = null;
        }
        if (pluginCommands != null && pluginCommands.length > 0) {
            sb.append(CommandLineMessages.getString("CommandLineLauncher.126")).append(":\n\n");
            for (PluginCommand pluginCommand : pluginCommands) {
                sb.append("  ").append(pluginCommand.getCommand()).append("\t (").append(pluginCommand.getDescription()).append(")\n");
            }
            sb.append("\n");
        }
        sb.append(CommandLineMessages.getString("CommandLineLauncher.30", applicationFilename));
        return sb.toString();
    }

    private static String buildXmlResponse(boolean z, String str, byte[] bArr) {
        StringBuilder sb = new StringBuilder();
        sb.append("<afirma><result>").append(z).append("</result>");
        if (str != null || bArr != null) {
            sb.append("<response>");
            if (str != null) {
                sb.append("<msg>").append(str).append("</msg>");
            }
            if (bArr != null) {
                sb.append("<sign>").append(Base64.encode(bArr)).append("</sign>");
            }
            sb.append("</response>");
        }
        sb.append("</afirma>");
        return sb.toString();
    }

    private static void closeApp(int i, PrintWriter printWriter, String str) {
        printMessage(printWriter, str);
        System.exit(i);
    }

    private static void printMessage(PrintWriter printWriter, String str) {
        if (printWriter != null && str != null) {
            printWriter.write(str);
            printWriter.write("\n");
            printWriter.flush();
        }
    }

    public static void main(String[] strArr) {
        deactivateConsoleLog("java.util.prefs");
        deactivateConsoleLog("es.gob.afirma");
        deactivateConsoleLog("es.gob.jmulticard");
        deactivateSlf4JLogs();
        LookAndFeelManager.applyLookAndFeel();
        if (pluginsManager == null) {
            pluginsManager = SimpleAfirma.getPluginsManager();
        }
        processCommandLine(strArr);
    }

    private static Properties buildProperties(String str) {
        int i;
        Properties properties = new Properties();
        if (str != null) {
            String strTrim = str.trim();
            int length = 0;
            while (true) {
                i = length;
                int iIndexOf = strTrim.indexOf("\\n", i);
                if (iIndexOf == -1) {
                    break;
                }
                String strTrim2 = strTrim.substring(i, iIndexOf).trim();
                if (strTrim2.length() > 0 && strTrim2.charAt(0) != '#') {
                    properties.setProperty(strTrim2.substring(0, strTrim2.indexOf(61)), strTrim2.substring(strTrim2.indexOf(61) + 1));
                }
                length = iIndexOf + "\\n".length();
            }
            properties.setProperty(strTrim.substring(i, strTrim.indexOf(61, i)).trim(), strTrim.substring(strTrim.indexOf(61, i) + 1).trim());
        }
        return properties;
    }

    private static String removeSignaturePageProperties(String str) {
        String str2 = null;
        if (str != null) {
            for (String str3 : str.replace("\\n", "\n").split("\\n")) {
                if (str3.indexOf(PdfExtraParams.SIGNATURE_PAGE) == -1 && str3.indexOf(PdfExtraParams.SIGNATURE_PAGES) == -1 && str3.indexOf(PdfExtraParams.SIGNATURE_POSITION_ON_PAGE_LOWER_LEFTX) == -1 && str3.indexOf(PdfExtraParams.SIGNATURE_POSITION_ON_PAGE_LOWER_LEFTY) == -1 && str3.indexOf(PdfExtraParams.SIGNATURE_POSITION_ON_PAGE_UPPER_RIGHTX) == -1 && str3.indexOf(PdfExtraParams.SIGNATURE_POSITION_ON_PAGE_UPPER_RIGHTY) == -1) {
                    str2 = str2 + str3 + "\\n";
                }
            }
        }
        return str2;
    }

    private static void showErrorDialog(String str, Exception exc) {
        AOUIFactory.showErrorMessage(str, CommandLineMessages.getString("CommandLineLauncher.127"), 0, exc);
    }
}
