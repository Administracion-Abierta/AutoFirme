package es.gob.afirma.standalone.ui;

import es.gob.afirma.core.AOCancelledOperationException;
import es.gob.afirma.core.ui.AOUIFactory;
import es.gob.afirma.core.ui.GenericFileFilter;
import es.gob.afirma.standalone.SimpleAfirmaMessages;
import java.awt.Component;
import java.awt.Desktop;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Collections;
import java.util.logging.Logger;

/* JADX INFO: loaded from: autofirma.jar:es/gob/afirma/standalone/ui/ShowFileLinkAction.class */
public final class ShowFileLinkAction {
    private static final Logger LOGGER = Logger.getLogger("es.gob.afirma");
    private final String text;
    private final byte[] data;
    private final Component parent;

    public ShowFileLinkAction(String str, byte[] bArr, Component component) {
        this.text = str;
        this.data = bArr;
        this.parent = component;
    }

    void action() {
        if (this.data == null) {
            return;
        }
        DataFileInfo dataFileInfoAnalize = DataFileAnalizer.analize(this.data);
        DataFileInfoDialog.Options optionsShow = new DataFileInfoDialog(dataFileInfoAnalize, this.parent).show();
        if (optionsShow == DataFileInfoDialog.Options.SAVE) {
            saveDataFile(dataFileInfoAnalize, this.parent);
        } else if (optionsShow == DataFileInfoDialog.Options.OPEN) {
            openDataFile(dataFileInfoAnalize, this.parent);
        }
    }

    private static void openDataFile(DataFileInfo dataFileInfo, Component component) {
        try {
            File fileCreateTempFile = File.createTempFile("afirma", "." + dataFileInfo.getExtension());
            try {
                BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(new FileOutputStream(fileCreateTempFile));
                Throwable th = null;
                try {
                    bufferedOutputStream.write(dataFileInfo.getData());
                    Desktop.getDesktop().open(fileCreateTempFile);
                    fileCreateTempFile.deleteOnExit();
                    if (bufferedOutputStream != null) {
                        if (0 != 0) {
                            try {
                                bufferedOutputStream.close();
                            } catch (Throwable th2) {
                                th.addSuppressed(th2);
                            }
                        } else {
                            bufferedOutputStream.close();
                        }
                    }
                } catch (Throwable th3) {
                    if (bufferedOutputStream != null) {
                        if (0 != 0) {
                            try {
                                bufferedOutputStream.close();
                            } catch (Throwable th4) {
                                th.addSuppressed(th4);
                            }
                        } else {
                            bufferedOutputStream.close();
                        }
                    }
                    throw th3;
                }
            } catch (Exception e) {
                LOGGER.warning("Error intentado abrir el fichero: " + e);
                AOUIFactory.showErrorMessage(SimpleAfirmaMessages.getString("ShowFileLinkAction.2", dataFileInfo.getExtension()), SimpleAfirmaMessages.getString("SimpleAfirma.7"), 0, e);
            }
        } catch (IOException e2) {
            LOGGER.info("No se pudo crear el temporal para abrir el fichero: " + e2);
            saveDataFile(dataFileInfo, component);
        }
    }

    private static void saveDataFile(DataFileInfo dataFileInfo, Component component) {
        try {
            AOUIFactory.getSaveDataToFile(dataFileInfo.getData(), SimpleAfirmaMessages.getString("ShowFileLinkAction.1"), null, dataFileInfo.getExtension() != null ? SimpleAfirmaMessages.getString("ShowFileLinkAction.5") + "." + dataFileInfo.getExtension() : null, Collections.singletonList(new GenericFileFilter(dataFileInfo.getExtension() != null ? new String[]{dataFileInfo.getExtension()} : null, dataFileInfo.getDescription())), component);
        } catch (AOCancelledOperationException e) {
            LOGGER.info("Operacion cancelada por el usuario: " + e);
        } catch (IOException e2) {
            LOGGER.severe("No se ha podido guardar el fichero: " + e2);
            AOUIFactory.showErrorMessage(SimpleAfirmaMessages.getString("ShowFileLinkAction.3"), SimpleAfirmaMessages.getString("ShowFileLinkAction.4"), 0, e2);
        }
    }

    public String toString() {
        return this.text;
    }
}
